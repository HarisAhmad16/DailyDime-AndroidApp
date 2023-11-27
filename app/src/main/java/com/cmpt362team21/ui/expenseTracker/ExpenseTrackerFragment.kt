package com.cmpt362team21.ui.expenseTracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentExpenseTrackerBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


data class ExpenseItem(val type: String, val amount: String, val date: String)
class ExpenseTrackerFragment : Fragment() {

    private var _binding: FragmentExpenseTrackerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var expenseList : ArrayList<ExpenseItem>
    //private lateinit var db:DatabaseReference
    private lateinit var expensesListView:ListView




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

        //val expense1 = expenseItem("Insurance","25","2023 Nov 21")
        //val expense2 = expenseItem("School","250","2023 Nov 23")



        //expenseList.add(expense1)
        //expenseList.add(expense2)


        //Log.d("db content", expenseList.toString())
        val calenderView = root.findViewById<CalendarView>(R.id.calendarView)
        calenderView.setOnDateChangeListener{ view, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"

            val builder = AlertDialog.Builder(requireContext())

            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.expense_pop_up, null)


            builder.setTitle("Expenses for $selectedDate")
            builder.setView(dialogView)

            expensesListView = dialogView.findViewById(R.id.expensesListView)

            getExpensesFromDB()

            //val expensesText = "Expenses will be populated with database information"
            //expensesTextView.text = expensesText

            builder.setPositiveButton("Close") { dialog, which ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        return root
    }

    private fun getExpensesFromDB() {
        val db = FirebaseFirestore.getInstance()
        val expenseCollection = db.collection("expenses")

        expenseCollection.addSnapshotListener{snapshot, exception ->
            if (exception != null) {
                Log.e("Firestore", "Listen failed.", exception)
                return@addSnapshotListener
            }
            if(snapshot != null){
                expenseList = arrayListOf()

                for(expense in snapshot.documents){
                    val type = expense.getString("type") ?: ""
                    val amount = expense.getDouble("amount") ?: 0.0
                    val relatedDate = expense.getString("date") ?: ""
                    val userExpense = ExpenseItem(type,amount.toString(),relatedDate)
                    expenseList.add(userExpense)
                }
                val adapter = ExpenseAdapter(requireContext(), expenseList)
                expensesListView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}