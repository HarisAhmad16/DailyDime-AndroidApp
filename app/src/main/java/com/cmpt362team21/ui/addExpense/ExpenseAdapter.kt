package com.cmpt362team21.ui.addExpense

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cmpt362team21.R

class ExpenseAdapter(context: Context, private val expenses: List<ExpenseItem>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return expenses.size
    }

    override fun getItem(position: Int): Any {
        return expenses[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(R.layout.item_add_expense, parent, false)

        val item = expenses[position]

        val expenseTypeTextView: TextView = view.findViewById(R.id.expenseTypeTextView)
        val expenseAmountTextView: TextView = view.findViewById(R.id.expenseAmountTextView)

        // Use the correct property names from your ExpenseItem class
        expenseTypeTextView.text = item.type
        expenseAmountTextView.text = item.amount

        return view
    }
}
