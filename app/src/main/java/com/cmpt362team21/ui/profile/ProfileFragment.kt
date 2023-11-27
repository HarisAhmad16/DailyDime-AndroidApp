package com.cmpt362team21.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cmpt362team21.databinding.FragmentProfileBinding
import com.cmpt362team21.ui.home.HomeViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel = HomeViewModel.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.emailEtBottom.setText(homeViewModel.email.value)
        binding.firstNameEtBottom.setText(homeViewModel.firstName.value)
        binding.LastNameEtBottom.setText(homeViewModel.lastName.value)

        binding.buttonBottom.setOnClickListener {
            // TODO: Update the user db info
            Toast.makeText(requireContext(), "Updating User Incomplete As Of Now", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
