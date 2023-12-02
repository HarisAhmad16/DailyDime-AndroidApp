package com.cmpt362team21.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.exp

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel = HomeViewModel.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbListener()

        homeViewModel.firstName.observe(viewLifecycleOwner) { firstName ->
            binding.firstUserName.text = firstName
        }

        homeViewModel.lastName.observe(viewLifecycleOwner) { lastName ->
            binding.lastUserName.text = lastName
        }

        binding.profileSection.setOnClickListener {
            findNavController().navigate(R.id.nav_profile)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getFragmentBinding(): FragmentHomeBinding {
        return _binding
            ?: throw IllegalStateException("Binding is null. Fragment is not attached or already destroyed.")
    }

    private fun dbListener() {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val incomeCollection = db.collection("incomes")
        val expenseCollection = db.collection("expenses")

        var incomeBalance = 0.0
        var expenseBalance = 0.0
        var totalIncome = 0.0
        var totalExpense = 0.0

        if (currentUser != null) {
            incomeCollection.whereEqualTo("userId", currentUser.uid)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        for (document in snapshot.documents) {
                            val amount = document.getDouble("amount") ?: 0.0
                            incomeBalance += amount
                        }
                        totalIncome = incomeBalance

                        if (isAdded) {
                            requireActivity().runOnUiThread {
                                getFragmentBinding().incomeAmount.text = String.format("$%.2f", incomeBalance)
                                updatePieChart(totalIncome, totalExpense)
                            }
                        }
                    }
                }

            expenseCollection.whereEqualTo("userId", currentUser.uid)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        for (document in snapshot.documents) {
                            val amount = document.getDouble("amount") ?: 0.0
                            expenseBalance += amount
                        }
                        totalExpense = expenseBalance

                        if (isAdded) {
                            requireActivity().runOnUiThread {
                                getFragmentBinding().expensesAmount.text =
                                    String.format("$%.2f", expenseBalance)
                                updatePieChart(totalIncome, totalExpense)
                            }
                        }
                    }
                }
        }
    }


    private fun updatePieChart(totalIncome: Double, totalExpense: Double) {
        val pie = AnyChart.pie()
        val data: MutableList<DataEntry> = ArrayList()
        data.add(ValueDataEntry("Income", totalIncome))
        data.add(ValueDataEntry("Expenses", totalExpense))

        pie.data(data)
        pie.title("Income vs Expenses")

        // Set up AnyChartView
        binding.placeholder.setChart(pie)
    }
}

