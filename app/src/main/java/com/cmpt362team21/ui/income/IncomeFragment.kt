package com.cmpt362team21.ui.income

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentIncomeBinding

class IncomeFragment : Fragment() {

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

        // sample data
        val incomeItems = listOf(
            IncomeItem("Salary", "+$3,000"),
            IncomeItem("Freelance", "+$1,000"),
            IncomeItem("Rent", "+$4,500"),
            // Add more items as needed
        )

        // Set up the ListView with the custom adapter
        val listViewIncomes: ListView = binding.listViewIncomes
        val incomeAdapter = IncomeAdapter(requireContext(), incomeItems)
        listViewIncomes.adapter = incomeAdapter

        listViewIncomes.setEmptyView(binding.emptyView)
        listViewIncomes.divider = ColorDrawable(Color.BLACK)
        listViewIncomes.dividerHeight =resources.getDimensionPixelSize(R.dimen.divider_height)




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
}
