package com.cmpt362team21.ui.expenseTracker

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.cmpt362team21.R

class ExpenseAdapter(context: Context, private val expenseList: ArrayList<ExpenseItem>) : BaseAdapter(){

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
            parent?.context?.let {
                Toast.makeText(it, "Selected ${expenseList[position].type} for editing", Toast.LENGTH_SHORT).show()
            }
        }
        deleteButton.setOnClickListener {
            parent?.context?.let {
                Toast.makeText(it, "Selected ${expenseList[position].type} for deleting", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}