package com.cmpt362team21.ui.expenseTracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentExpenseTrackerBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale


data class ExpenseItem(val type: String, val amount: String, val date: String)
class ExpenseTrackerFragment : Fragment() {

    private var _binding: FragmentExpenseTrackerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var expenseList : ArrayList<ExpenseItem>
    private lateinit var expensesListView:ListView
    private lateinit var noExpensesTextView:TextView

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
            val displayDate = "${month + 1} $dayOfMonth, $year"
            val inputFormat = SimpleDateFormat("MM dd, yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

            val date = inputFormat.parse(displayDate)
            val formattedDate = outputFormat.format(date!!)

            val builder = AlertDialog.Builder(requireContext())

            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.expense_pop_up, null)


            builder.setTitle("Expenses for $formattedDate")
            builder.setView(dialogView)

            expensesListView = dialogView.findViewById(R.id.expensesListView)
            noExpensesTextView = dialogView.findViewById(R.id.noExpensesTextView)

            getExpensesFromDB(formatDate(selectedDate))

            builder.setPositiveButton("Close") { dialog, which ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        return root
    }

    private fun getExpensesFromDB(selectedDate: String) {
        val db = FirebaseFirestore.getInstance()
        val expenseCollection = db.collection("expenses")

        expenseCollection.whereEqualTo("date", selectedDate)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("FireStore", "Listen failed.", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    expenseList = arrayListOf()

                    for (expense in snapshot.documents) {
                        val type = expense.getString("type") ?: ""
                        val amount = expense.getDouble("amount") ?: 0.0
                        val relatedDate = expense.getString("date") ?: ""
                        val userExpense = ExpenseItem(type, amount.toString(), relatedDate)
                        expenseList.add(userExpense)
                    }


                    noExpensesTextView.visibility = if (expenseList.isEmpty()) View.VISIBLE else View.GONE

                    val adapter = ExpenseAdapter(requireContext(), expenseList)
                    expensesListView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy MMM dd", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}