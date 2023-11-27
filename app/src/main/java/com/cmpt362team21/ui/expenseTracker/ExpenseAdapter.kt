package com.cmpt362team21.ui.expenseTracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.cmpt362team21.R
class ExpenseAdapter(context: Context,
                     private val expenseList: ArrayList<ExpenseItem>,
                     private val onDeleteExpenseItem: (expenseId: String) -> Unit,
                     private val onEditExpenseItem: (expenseItem: ExpenseItem) -> Unit) : BaseAdapter(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun getCount(): Int {
        return expenseList.size
    }
    override fun getItem(position: Int): Any {
        return expenseList[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(R.layout.expenses_tracking_list_item, parent, false)

        val typeTextView = view.findViewById<TextView>(R.id.typeTextView)
        val amountTextView = view.findViewById<TextView>(R.id.amountTextView)
        val editButton = view.findViewById<ImageButton>(R.id.editButton)
        val deleteButton = view.findViewById<ImageButton>(R.id.deleteButton)

        typeTextView.text = expenseList[position].type
        amountTextView.text = expenseList[position].amount
        editButton.setOnClickListener {
            onEditExpenseItem(expenseList[position])
        }
        deleteButton.setOnClickListener {
            val expenseId = expenseList[position].expenseId
            onDeleteExpenseItem(expenseId)
        }

        return view
    }

}