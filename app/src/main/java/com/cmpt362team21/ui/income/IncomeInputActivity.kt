package com.cmpt362team21.ui.income

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentIncomeInputDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IncomeInputActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private var enteredIncomeAmount: Double = 0.0
    private var enteredIncomeType: String = ""
    private var selectedDate: Calendar? = null
    private var _binding: FragmentIncomeInputDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentIncomeInputDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firestore = FirebaseFirestore.getInstance()


        // Set up click listener for date selection
        binding.layoutSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        val btnSave: Button = binding.btnSaveIncome
        btnSave.setOnClickListener {
            saveIncomeDataToFirestore()
            finish()
        }

        val btnCancel: Button = binding.btnCancelIncome
        btnCancel.setOnClickListener {
            Toast.makeText(
                this@IncomeInputActivity,
                "Income Entry Cancelled",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        setupDialogListeners()
    }

    private fun saveIncomeDataToFirestore() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null && selectedDate != null) {
            // Format the date as needed (e.g., converting to a string)
            val dateFormat = SimpleDateFormat("yyyy MMM dd", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate!!.time)

            // Create a new income object with user UID
            val income = hashMapOf(
                "type" to enteredIncomeType,
                "amount" to enteredIncomeAmount,
                "date" to formattedDate,
                "userId" to currentUser.uid
            )

            // Add a new document with a generated ID
            firestore.collection("incomes")
                .add(income)
                .addOnSuccessListener {
                    Log.d("Firestore", "Income data saved successfully")
                    Toast.makeText(this, "Income data saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Failed to save income data", e)
                    Toast.makeText(this, "Failed to save income data", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle the case when selectedDate is null
            Log.d("Firestore", "Please select a date")
            Toast.makeText(this, "Please select a date or log in", Toast.LENGTH_SHORT).show()
        }
    }



    private fun setupDialogListeners() {
        setLayoutClickListener(R.id.layoutSelectDate, "Date")
        setLayoutClickListener(R.id.layoutIncomeType, "Income Type")
        setLayoutClickListener(R.id.layoutIncomeAmount, "Income Amount")
    }

    private fun setLayoutClickListener(layoutId: Int, title: String) {
        val layout = findViewById<LinearLayout>(layoutId)
        layout.setOnClickListener {
            when (title) {
                "Date" -> {
                    showDatePickerDialog()
                }
                "Income Type" -> {
                    showIncomeTypeDialog()
                }
                "Income Amount" -> {
                    showIncomeAmountDialog()
                }
            }
        }
    }

    private fun showIncomeAmountDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Income Amount")
        val input = EditText(this)
        input.hint = "Enter Income Amount"

        // Allow both integers and decimals
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.setText(enteredIncomeAmount.toString())
        
        alertDialog.setView(input)
        alertDialog.setPositiveButton("OK") { _, _ ->
            val inputText = input.text.toString()
            enteredIncomeAmount = if (inputText.isNotBlank()) inputText.toDouble() else 0.0
        }
        alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        alertDialog.show()
    }


    private fun showIncomeTypeDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Income Type")

        val input = EditText(this)
        input.hint = "Enter Income Type"
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        input.setText(enteredIncomeType)
        
        alertDialog.setView(input)
        alertDialog.setPositiveButton("OK") { _, _ ->
            // Capture the entered income type
            enteredIncomeType = input.text.toString()
            // Handle the entered income type (if needed)
        }

        alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        alertDialog.show()
    }

    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate = Calendar.getInstance()
                selectedDate?.set(year, month, dayOfMonth)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
