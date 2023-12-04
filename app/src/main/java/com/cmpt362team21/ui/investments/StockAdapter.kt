package com.cmpt362team21.ui.investments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.cmpt362team21.R

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
        viewHolder.deleteButton.setOnClickListener {
            onDeleteClickListener.invoke(stock)
        }
        return view
    }

    private class ViewHolder(view: View) {
        val symbolTextView: TextView = view.findViewById(R.id.stockSymbolTextView)
        val quantityTextView: TextView = view.findViewById(R.id.stockQuantityTextView)
        val priceTextView: TextView = view.findViewById(R.id.stockPriceTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }
}
