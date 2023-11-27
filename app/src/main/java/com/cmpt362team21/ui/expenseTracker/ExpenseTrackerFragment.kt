package com.cmpt362team21.ui.expenseTracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentExpenseTrackerBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
data class ExpenseItem(val expenseId:String, val userId:String,val type: String, val amount: String, val date: String)
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
        calenderView.setOnDateChangeListener{ _, year, month, dayOfMonth ->
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

            builder.setPositiveButton("Close") { dialog, _ ->
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

                        val amount = when (val amountField = expense.get("amount")) {
                            is Number -> amountField.toDouble()
                            is String -> amountField.toDoubleOrNull() ?: 0.0
                            else -> 0.0
                        }
                        val relatedDate = expense.getString("date") ?: ""
                        val userId = expense.getString("userId") ?: ""
                        val expenseId = expense.id
                        val userExpense = ExpenseItem(expenseId,userId,type, amount.toString(), relatedDate)
                        expenseList.add(userExpense)
                    }

                    noExpensesTextView.visibility = if (expenseList.isEmpty()) View.VISIBLE else View.GONE

                    if (isAdded) {
                        val adapter = ExpenseAdapter(requireContext(), expenseList, { expenseId -> deleteExpenseFromDB(expenseId) }, { expenseItem -> showEditDialog(expenseItem) })
                        expensesListView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("FragmentError", "Fragment not attached to a context")
                    }
                }
            }
    }

    private fun deleteExpenseFromDB(expensesId: String) {
        val db = FirebaseFirestore.getInstance()
        val expenseCollection = db.collection("expenses")

        expenseCollection.document(expensesId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(),"Expense Successfully paid",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Delete Failure", "Error deleting expense", e)
            }
    }

    private fun showEditDialog(expenseItem: ExpenseItem) {
        val builder = android.app.AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.expense_edit_pop_up, null)

        val editedTypeEditText = dialogView.findViewById<EditText>(R.id.editedTypeEditText)
        val editedAmountEditText = dialogView.findViewById<EditText>(R.id.editedAmountEditText)

        editedTypeEditText.setText(expenseItem.type)
        editedAmountEditText.setText(expenseItem.amount)

        builder.setView(dialogView)
            .setTitle("Edit Expense")
            .setPositiveButton("Save") { dialog, _ ->
                val editedType = editedTypeEditText.text.toString()
                val editedAmount = editedAmountEditText.text.toString()

                updateFireStoreDocument(expenseItem.expenseId, editedType, editedAmount)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun updateFireStoreDocument(expenseId: String, editedType: String, editedAmount: String) {
        val db = FirebaseFirestore.getInstance()
        val expenseCollection = db.collection("expenses")

        val expenseDocument = expenseCollection.document(expenseId)

        val amountValue = editedAmount.toDoubleOrNull() ?: 0.0

        expenseDocument.update(mapOf(
            "type" to editedType,
            "amount" to amountValue
        ))
            .addOnSuccessListener {
                Toast.makeText(requireContext(),"Expense successfully updated",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Edit Failure", "Error updating expense", e)
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