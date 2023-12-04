package com.cmpt362team21.ui.investments

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.cmpt362team21.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class StockAdapter(
    private val context: Context,
    private val stocks: List<Stock>,
    private val onDeleteClickListener: (Stock) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int {
        return stocks.size
    }

    override fun getItem(position: Int): Any {
        return stocks[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val stock = getItem(position) as Stock
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_stock, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.symbolTextView.text = stock.symbol
        viewHolder.quantityTextView.text = stock.quantity.toString()
        viewHolder.priceTextView.text = "$${stock.price}"

        FetchStockPriceTask(viewHolder, stock).execute(stock.symbol)

        viewHolder.deleteButton.setOnClickListener {
            onDeleteClickListener.invoke(stock)
        }
        return view
    }

    private class ViewHolder(view: View) {
        val symbolTextView: TextView = view.findViewById(R.id.stockSymbolTextView)
        val quantityTextView: TextView = view.findViewById(R.id.stockQuantityTextView)
        val priceTextView: TextView = view.findViewById(R.id.stockPriceTextView)
        val latestPriceTextView: TextView = view.findViewById(R.id.latestPriceTextView)
        val profitLossTextView: TextView = view.findViewById(R.id.profitLossTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }

    private inner class FetchStockPriceTask(private val viewHolder: ViewHolder, private val stock: Stock) : AsyncTask<String, Void, Double>() {
        override fun doInBackground(vararg params: String): Double {
            val symbol = params[0]
            val apiUrl = "https://www.alphavantage.co/query"
            val function = "GLOBAL_QUOTE"
            val apiKey = "MOS192R50R4R9OK1"

            val url = "$apiUrl?function=$function&symbol=$symbol&apikey=$apiKey"

            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val json = JSONObject(response)
                val globalQuote = json.getJSONObject("Global Quote")
                return globalQuote.getString("05. price").toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return 0.0
        }

        override fun onPostExecute(result: Double) {
            val formattedLatestPrice = context.getString(R.string.latest_price_format, result)
            viewHolder.latestPriceTextView.text = formattedLatestPrice

            val profitLoss = (result - stock.price) * stock.quantity
            val formattedProfitLoss = context.getString(R.string.profit_loss_format, profitLoss)
            viewHolder.profitLossTextView.text = formattedProfitLoss
        }
    }
}
