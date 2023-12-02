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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Stock(
    val symbol: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)

class InvestmentsFragment : Fragment() {
    private var _binding: FragmentInvestmentsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Fragment binding is null")

    private lateinit var viewModel: InvestmentsViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: StockAdapter // Ensure that you have declared the adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvestmentsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        viewModel = ViewModelProvider(this).get(InvestmentsViewModel::class.java)

        binding.addStockButton.setOnClickListener {
            showAddStockDialog()
        }

        // Initialize the adapter
        adapter = StockAdapter(requireContext(), viewModel.stocks)
        binding.stockListLayout.adapter = adapter

        displayStocks()

        return root
    }

    private fun showAddStockDialog() {
        val dialog = AddStockDialogFragment(object : AddStockDialogFragment.OnStockAddedListener {
            override fun onStockAdded(stock: Stock) {
                if (isStockValid(stock)) {
                    addStockToFirebase(stock)
                } else {
                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                }
            }
        })
        dialog.show(childFragmentManager, "AddStockDialog")
    }

    private fun addStockToFirebase(stock: Stock) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val stocksCollection = firestore.collection("stocks")

            // Add stock to Firebase
            stocksCollection.add(mapOf(
                "userId" to userId,
                "symbol" to stock.symbol,
                "quantity" to stock.quantity,
                "price" to stock.price
            ))
            displayStocks()
        }
    }

    private fun displayStocks() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val stocksCollection = firestore.collection("stocks")

            // Query stocks for the current user
            stocksCollection.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val newStocks = mutableListOf<Stock>()

                    for (document in documents) {
                        val stock = document.toObject(Stock::class.java)
                        newStocks.add(stock)
                    }

                    // Update the ViewModel with the new data
                    viewModel.stocks.clear()
                    viewModel.stocks.addAll(newStocks)

                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error getting stocks: $exception", Toast.LENGTH_SHORT).show()
                }
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