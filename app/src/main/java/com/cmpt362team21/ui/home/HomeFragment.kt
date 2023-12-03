package com.cmpt362team21.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Pie
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel = HomeViewModel.getInstance()

    private val incomeList = mutableListOf<Pair<String, Double>>()
    private val expenseList = mutableListOf<Pair<String, Double>>()

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


        binding.profileProfileSection.setOnClickListener {
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

        val pie = AnyChart.pie()

        if (currentUser != null) {
            incomeCollection.whereEqualTo("userId", currentUser.uid)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        for (document in snapshot.documents) {
                            val type = document.getString("type") ?: ""
                            val amount = document.getDouble("amount") ?: 0.0
                            incomeBalance += amount
                            incomeList.add(Pair(type, amount))
                        }
                        totalIncome = incomeBalance

                        if (isAdded) {
                            requireActivity().runOnUiThread {
                                getFragmentBinding().incomeAmount.text = String.format("$%.2f", incomeBalance)
                            }
                            binding.placeholder.post {
                                updatePieChart(totalIncome, totalExpense, pie)
                            }
                            val (mostCommonType, percentage) = findMostCommonType(incomeList)
                            if (isAdded) {
                                binding.mostCommonIncomeType.post {
                                    binding.mostCommonIncomeType.text = mostCommonType
                                }

                                binding.mostCommonIncomeTotal.post {
                                    binding.mostCommonIncomeTotal.text = String.format("This income takes up %.1f%% of all incomes", percentage)
                                }
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
                            val type = document.getString("type") ?: ""
                            expenseBalance += amount
                            expenseList.add(Pair(type, amount))
                        }
                        totalExpense = expenseBalance

                        if (isAdded) {
                            requireActivity().runOnUiThread {
                                getFragmentBinding().expensesAmount.text =
                                    String.format("$%.2f", expenseBalance)
                            }
                            binding.placeholder.post {
                                updatePieChart(totalIncome, totalExpense, pie)
                            }
                            val (mostCommonType, percentage) = findMostCommonType(expenseList)
                            if (isAdded) {
                                binding.mostCommonExpenseType.post {
                                    binding.mostCommonExpenseType.text = mostCommonType
                                }
                                binding.mostCommonExpenseTotal.post {
                                    binding.mostCommonExpenseTotal.text = String.format(
                                        "This expense takes up %.1f%% of all expenses",
                                        percentage
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileCardView: CardView = requireView().findViewById(R.id.profile_pic)
        val profileImageView: ImageView = profileCardView.getChildAt(0) as ImageView
        val newImageResourceId = R.drawable.profile_photo
        profileImageView.setImageResource(newImageResourceId)
    }


    private fun updatePieChart(totalIncome: Double, totalExpense: Double, pie: Pie) {
        val data: MutableList<DataEntry> = ArrayList()

        data.add(ValueDataEntry("Income", totalIncome).apply {
            setValue("fill", "#69DA6D")
        })
        data.add(ValueDataEntry("Expenses", totalExpense).apply {
            setValue("fill", "#DA6969")
        })

        pie.data(data)
        pie.background("#000000")
        pie.title("Income vs Expenses")
        binding.placeholder.setChart(pie)
    }

    private fun findMostCommonType(list: List<Pair<String, Double>>): Pair<String, Double> {
        val typeFrequencyMap = mutableMapOf<String, Int>()
        for ((type, _) in list) {
            typeFrequencyMap[type] = typeFrequencyMap.getOrDefault(type, 0) + 1
        }

        var mostCommonType = ""
        var maxFrequency = 0

        for ((type, frequency) in typeFrequencyMap) {
            if (frequency > maxFrequency) {
                mostCommonType = type
                maxFrequency = frequency
            }
        }

        val percentage = (maxFrequency.toDouble() / list.size) * 100.0
        return Pair(mostCommonType, percentage)
    }
}

