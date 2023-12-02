package com.cmpt362team21.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentHomeBinding
import com.cmpt362team21.ui.income.IncomeItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

        setupDatabaseListener()

        homeViewModel.firstName.observe(viewLifecycleOwner) { firstName ->
            binding.firstUserName.text = firstName
        }

        homeViewModel.lastName.observe(viewLifecycleOwner) { lastName ->
            binding.lastUserName.text = lastName
        }

        binding.profileSection.setOnClickListener {
            findNavController().navigate(R.id.nav_profile)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getFragmentBinding(): FragmentHomeBinding {
        return _binding
            ?: throw IllegalStateException("Binding is null. Fragment is not attached or already destroyed.")
    }

    private fun setupDatabaseListener() {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val incomeCollection = db.collection("incomes")

        if (currentUser != null) {
            incomeCollection.whereEqualTo("userId", currentUser.uid)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Log.e("Firestore", "Listen failed.", exception)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        var currentBalance = 0.0
                        for (document in snapshot.documents) {
                            val amount = document.getDouble("amount") ?: 0.0
                            currentBalance += amount
                        }

                        // Update UI on the main thread if fragment is still attached
                        if (isAdded) {
                            requireActivity().runOnUiThread {
                                getFragmentBinding().incomeAmount.text = String.format("$%.2f", currentBalance)
                            }
                        }
                    }
                }
        }
    }
}
