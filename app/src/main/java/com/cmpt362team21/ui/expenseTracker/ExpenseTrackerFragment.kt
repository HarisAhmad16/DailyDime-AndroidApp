package com.cmpt362team21.ui.expenseTracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentExpenseTrackerBinding


class ExpenseTrackerFragment : Fragment() {

    private var _binding: FragmentExpenseTrackerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val expenseTrackerViewModel =
            ViewModelProvider(this).get(ExpenseTrackerViewModel::class.java)

        _binding = FragmentExpenseTrackerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Expense Tracker"

        val textView: TextView = binding.textExpenseTracker
        expenseTrackerViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val calenderView = root.findViewById<CalendarView>(R.id.calendarView)
        calenderView.setOnDateChangeListener{ view, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"

            val builder = AlertDialog.Builder(requireContext())

            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.expense_pop_up, null)

            builder.setTitle("Expenses for $selectedDate")
            builder.setView(dialogView)

            val expensesTextView = dialogView.findViewById<TextView>(R.id.expensesTextView)
            val expensesText = "Expenses will be populated with database information"
            expensesTextView.text = expensesText

            builder.setPositiveButton("Close") { dialog, which ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}