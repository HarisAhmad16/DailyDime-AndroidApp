package com.cmpt362team21.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.cmpt362team21.MainActivity
import com.cmpt362team21.databinding.ActivitySignInBinding
import com.cmpt362team21.ui.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val uid = user?.uid

                        val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid.orEmpty())
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val firstName = snapshot.child("firstName").value?.toString() ?: ""
                                val lastName = snapshot.child("lastName").value?.toString() ?: ""

                                val homeViewModel = HomeViewModel.getInstance()
                                homeViewModel.setUserNames(firstName, lastName, email)

                                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                                startActivity(intent)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // TODO: implement a message indicating log in user data didn't change (populate the home)
                            }
                        })
                    } else {
                        Toast.makeText(this, "Incorrect Email or Password.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
