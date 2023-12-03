package com.cmpt362team21.ui.income

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.cmpt362team21.R

class IncomeAdapter(context: Context, private val incomes: List<IncomeItem>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return incomes.size
    }

    override fun getItem(position: Int): Any {
        return incomes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(R.layout.item_income, parent, false)

        val item = incomes[position]

        val incomeTypeTextView: TextView = view.findViewById(R.id.incomeTypeTextView)
        val incomeAmountTextView: TextView = view.findViewById(R.id.incomeAmountTextView)

        incomeTypeTextView.text = item.type
        incomeAmountTextView.text = item.amount

        return view
    }
}
