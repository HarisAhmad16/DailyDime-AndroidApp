package com.cmpt362team21.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362team21.databinding.ActivitySignUpBinding
import com.cmpt362team21.ui.util.DatePickerFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var selectedDate: String
    private lateinit var firebaseAuth: FirebaseAuth

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectedDate = "$year-${month + 1}-$dayOfMonth"
        binding.dateEditText.setText(selectedDate)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dateEditText = binding.dateEditText

        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        firebaseAuth = FirebaseAuth.getInstance()


        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val firstName = binding.firstNameEt.text.toString()
            val lastName = binding.LastNameEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty() && selectedDate.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                val uid = user?.uid
                                saveUserDataToDatabase(uid, firstName, lastName, selectedDate)
                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "Unable to create an account.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun saveUserDataToDatabase(
        uid: String?,
        firstName: String,
        lastName: String,
        dob: String
    ) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid.orEmpty())
        userRef.child("firstName").setValue(firstName)
        userRef.child("lastName").setValue(lastName)
        userRef.child("dob").setValue(dob)
    }


}