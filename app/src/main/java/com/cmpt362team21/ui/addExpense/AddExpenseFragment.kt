package com.cmpt362team21.ui.addExpense


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentAddExpenseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

data class ExpenseItem(val type: String, val amount: String, val date: String)

class AddExpenseFragment : Fragment() {
    private var expenseItems: MutableList<ExpenseItem> = mutableListOf()
    private lateinit var adapterExpense: ExpenseAdapter

    private lateinit var expenseViewModel: AddExpenseViewModel
    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Fragment binding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize ViewModel
        expenseViewModel = ViewModelProvider(this)[AddExpenseViewModel::class.java]

        // Set up UI components and bindings
        val textView: TextView = binding.textExpense
        expenseViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val spinner: Spinner = binding.monthSpinner
        val months = arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December",
            "All Months"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                // Handle item selection, and update the database listener accordingly
                setupExpenseDatabaseListener(months[position])
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                setupExpenseDatabaseListener("January")
            }
        }

        setupExpenseDatabaseListener("January")
        adapterExpense = ExpenseAdapter(requireContext(), expenseItems)
        // Set up the ListView with the custom adapter
        val listViewExpenses: ListView = binding.listViewExpense
        listViewExpenses.adapter = adapterExpense

        listViewExpenses.emptyView = binding.emptyView
        listViewExpenses.divider = ColorDrawable(Color.BLACK)
        listViewExpenses.dividerHeight = resources.getDimensionPixelSize(R.dimen.divider_height)

        val btnExpense: Button = binding.btnExpense
        btnExpense.setOnClickListener {
            // Handle button click, show the ExpenseInputDialogFragment
            val intent = Intent(requireContext(), ExpenseInputActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    private fun setupExpenseDatabaseListener(selectedMonth: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val expenseCollection = db.collection("expenses")

        if (!isAdded) {
            return
        }

        if (currentUser != null) {
            expenseCollection.whereEqualTo("userId", currentUser.uid)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Log.e("Firestore", "Listen failed.", exception)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val newExpenseItems = mutableListOf<ExpenseItem>()
                        var totalExpense = 0.0
                        var currentBalance = 0.0

                        for (document in snapshot.documents) {
                            val type = document.getString("type") ?: ""
                            val amount = document.getDouble("amount") ?: 0.0
                            currentBalance -= amount
                            val date = document.getString("date") ?: ""
                            val documentMonth = date.split(" ")[1]
                            val documentMonthFull = convertToFullMonth(documentMonth)
                            Log.d("Firestore", "Document Month: $documentMonthFull, Selected Month: $selectedMonth")

                            // Check if the document's month matches the selected month
                            if (selectedMonth == "All Months" || selectedMonth == documentMonthFull) {
                                val expenseItem = ExpenseItem(type, "-$amount", date)
                                newExpenseItems.add(expenseItem)

                                // Update totalExpense with the current amount
                                totalExpense += amount
                            }
                        }

                        // Update the expenseItems list with the new data
                        expenseItems.clear()
                        expenseItems.addAll(newExpenseItems)

                        // Notify the adapter that the data has changed
                        adapterExpense.notifyDataSetChanged()

                        // Update the totalExpense TextView
                        binding.totalExpense.text = String.format("Total Expense: $%.2f", totalExpense)
                        binding.currentBalance.text = String.format("$%.2f", currentBalance)
                    }
                }
        }
    }



    private fun convertToFullMonth(abbreviation: String): String {
        val format = SimpleDateFormat("MMM", Locale.US)
        val date = format.parse(abbreviation)
        return SimpleDateFormat("MMMM", Locale.US).format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
