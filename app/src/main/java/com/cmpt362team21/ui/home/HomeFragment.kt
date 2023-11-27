package com.cmpt362team21.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentHomeBinding

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
}
