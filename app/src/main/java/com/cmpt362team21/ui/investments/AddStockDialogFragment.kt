package com.cmpt362team21.ui.investments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.cmpt362team21.R

class AddStockDialogFragment(private val listener: OnStockAddedListener) : DialogFragment() {

    interface OnStockAddedListener {
        fun onStockAdded(stock: Stock)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_stock, null)

        val symbolEditText: EditText = view.findViewById(R.id.symbolEditText)
        val quantityEditText: EditText = view.findViewById(R.id.quantityEditText)
        val priceEditText: EditText = view.findViewById(R.id.priceEditText)

        builder.setView(view)
            .setTitle("Add Stock")
            .setPositiveButton("Add") { _, _ ->
                val symbol = symbolEditText.text.toString()
                val quantity = quantityEditText.text.toString().toInt()
                val price = priceEditText.text.toString().toDouble()

                val stock = Stock(symbol, quantity, price)
                listener.onStockAdded(stock)
            }
            .setNegativeButton("Cancel") { _, _ -> }

        return builder.create()
    }
}