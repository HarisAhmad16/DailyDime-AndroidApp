package com.cmpt362team21.ui.income

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
import com.cmpt362team21.databinding.FragmentIncomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

data class IncomeItem(val type: String, val amount: String, val date: String)
class IncomeFragment : Fragment() {
    private var incomeItems: MutableList<IncomeItem> = mutableListOf()
    private lateinit var adapterIncome: IncomeAdapter

    private lateinit var incomeViewModel: IncomeViewModel
    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize ViewModel
        incomeViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]

        // Set up UI components and bindings
        val textView: TextView = binding.textIncome
        incomeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val spinner: Spinner = binding.monthSpinner
        val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December","All Months")
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
                setupDatabaseListener(months[position])
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                setupDatabaseListener("January")
            }
        }



        setupDatabaseListener("January")
        adapterIncome = IncomeAdapter(requireContext(), incomeItems)
        // Set up the ListView with the custom adapter
        val listViewIncomes: ListView = binding.listViewIncomes
        listViewIncomes.adapter = adapterIncome

        listViewIncomes.setEmptyView(binding.emptyView)
        listViewIncomes.divider = ColorDrawable(Color.BLACK)
        listViewIncomes.dividerHeight = resources.getDimensionPixelSize(R.dimen.divider_height)


        val btnIncome: Button = binding.btnIncome
        btnIncome.setOnClickListener {
            // Handle button click, show the IncomeInputDialogFragment
            val intent = Intent(requireContext(), IncomeInputActivity::class.java)
            startActivity(intent)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupDatabaseListener(selectedMonth: String ) {
        val db = FirebaseFirestore.getInstance()
        val incomeCollection = db.collection("incomes")

        incomeCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("Firestore", "Listen failed.", exception)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val newIncomeItems = mutableListOf<IncomeItem>()
                var totalIncome = 0.0
                var currentBalance = 0.0

                for (document in snapshot.documents) {
                    val type = document.getString("type") ?: ""
                    val amount = document.getDouble("amount") ?: 0.0
                    currentBalance += amount
                    val date = document.getString("date") ?: ""
                    val documentMonth = date.split(" ")[1]
                    val documentMonthFull = convertToFullMonth(documentMonth)
                    Log.d("Firestore", "Document Month: $documentMonthFull, Selected Month: $selectedMonth")
                    // Check if the document's month matches the selected month
                    if (selectedMonth == "All Months" || selectedMonth == documentMonthFull) {
                        val incomeItem = IncomeItem(type, "+$amount", date)
                        newIncomeItems.add(incomeItem)

                        // Update totalIncome with the current amount
                        totalIncome += amount
                    }

                }

                // Update the incomeItems list with the new data
                incomeItems.clear()
                incomeItems.addAll(newIncomeItems)

                // Notify the adapter that the data has changed
                adapterIncome.notifyDataSetChanged()

                // Update the totalIncome TextView
                binding.totalIncome.text = String.format("Total Income: $%.2f", totalIncome)
                binding.currentBalance.text = String.format("$%.2f", currentBalance)

            }
        }
    }

    private fun convertToFullMonth(abbreviation: String): String {
        val format = SimpleDateFormat("MMM", Locale.US)
        val date = format.parse(abbreviation)
        return SimpleDateFormat("MMMM", Locale.US).format(date)
    }


}
