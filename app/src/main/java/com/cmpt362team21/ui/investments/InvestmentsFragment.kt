package com.cmpt362team21.ui.investments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentInvestmentsBinding

data class Stock(val symbol: String, val quantity: Int, val price: Double)

class InvestmentsFragment : Fragment() {
    private var _binding: FragmentInvestmentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: InvestmentsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvestmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(InvestmentsViewModel::class.java)

        binding.addStockButton.setOnClickListener {
            showAddStockDialog()
        }

        displayStocks(binding.stockListLayout)
    }

    private fun showAddStockDialog() {
        val dialog = AddStockDialogFragment(object : AddStockDialogFragment.OnStockAddedListener {
            override fun onStockAdded(stock: Stock) {
                if (isStockValid(stock)) {
                    viewModel.addStock(stock)
                    displayStocks(binding.stockListLayout)
                } else {
                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                }
            }
        })
        dialog.show(childFragmentManager, "AddStockDialog")
    }

    private fun displayStocks(layout: LinearLayout) {
        layout.removeAllViews()

        for (stock in viewModel.stocks) {
            val stockView = View.inflate(requireContext(), R.layout.item_stock, null)
            stockView.findViewById<TextView>(R.id.stockInfoTextView).text =
                "${stock.symbol}: ${stock.quantity} shares at $${stock.price}"

            val deleteButton: Button = stockView.findViewById(R.id.deleteButton)
            deleteButton.setOnClickListener {
                viewModel.deleteStock(stock)
                displayStocks(binding.stockListLayout)
            }

            layout.addView(stockView)
        }
    }

    private fun isStockValid(stock: Stock): Boolean {
        return stock.symbol.isNotBlank() && stock.quantity > 0 && stock.price > 0.0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}