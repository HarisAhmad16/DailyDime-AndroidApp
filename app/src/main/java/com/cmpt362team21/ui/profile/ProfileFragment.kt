package com.cmpt362team21.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentProfileBinding
import com.cmpt362team21.ui.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel = HomeViewModel.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.firstNameEtBottom.setText(homeViewModel.firstName.value)
        binding.LastNameEtBottom.setText(homeViewModel.lastName.value)

        binding.buttonBottom.setOnClickListener {
            updateProfile()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profileCardView: CardView = requireView().findViewById(R.id.profile_pic)
        val profileImageView: ImageView = profileCardView.getChildAt(0) as ImageView
        val newImageResourceId = R.drawable.profile_photo
        profileImageView.setImageResource(newImageResourceId)
    }

    private fun updateProfile() {
        val user = firebaseAuth.currentUser
        val uid = user?.uid

        val newFirstName = binding.firstNameEtBottom.text.toString()
        val newLastName = binding.LastNameEtBottom.text.toString()
        val newPassword = binding.passETBottom.text.toString()
        val newRetypePassword = binding.confirmPassEtBottom.text.toString()

        if (uid != null) {
            if (newPassword == newRetypePassword) {
                val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
                userRef.child("firstName").setValue(newFirstName)
                userRef.child("lastName").setValue(newLastName)

                if (newPassword.isNotEmpty()) {
                    user.updatePassword(newPassword)
                }

                homeViewModel.updateFirstName(newFirstName)
                homeViewModel.updateLastName(newLastName)

                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                activity?.supportFragmentManager?.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
