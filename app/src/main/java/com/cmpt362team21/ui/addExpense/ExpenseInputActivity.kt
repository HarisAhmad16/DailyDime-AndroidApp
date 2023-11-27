package com.cmpt362team21.ui.addExpense

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentExpenseInputDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpenseInputActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private var enteredExpenseAmount: Double = 0.0
    private var enteredExpenseType: String = ""
    private var selectedDate: Calendar? = null
    private var _binding: FragmentExpenseInputDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentExpenseInputDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Set up click listener for date selection
        binding.layoutSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        val btnSave: Button = binding.btnSaveExpense
        btnSave.setOnClickListener {
            saveExpenseDataToFirestore()
            finish()
        }

        val btnCancel: Button = binding.btnCancelExpense
        btnCancel.setOnClickListener {
            Toast.makeText(
                this@ExpenseInputActivity,
                "Expense Entry Cancelled",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        setupDialogListeners()
    }

    private fun saveExpenseDataToFirestore() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null && selectedDate != null) {
            // Format the date as needed (e.g., converting to a string)
            val dateFormat = SimpleDateFormat("yyyy MMM dd", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate!!.time)

            // Create a new expense object with user UID
            val expense = hashMapOf(
                "type" to enteredExpenseType,
                "amount" to enteredExpenseAmount,
                "date" to formattedDate,
                "userId" to currentUser.uid
            )

            // Add a new document with a generated ID
            firestore.collection("expenses")
                .add(expense)
                .addOnSuccessListener {
                    Log.d("Firestore", "Expense data saved successfully")
                    Toast.makeText(this, "Expense data saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Failed to save expense data", e)
                    Toast.makeText(this, "Failed to save expense data", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle the case when selectedDate is null or user is not logged in (optional)
            Log.d("Firestore", "Please select a date or log in")
            Toast.makeText(this, "Please select a date or log in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDialogListeners() {
        setLayoutClickListener(R.id.layoutSelectDate, "Date")
        setLayoutClickListener(R.id.layoutExpenseType, "Expense Type")
        setLayoutClickListener(R.id.layoutExpenseAmount, "Expense Amount")
    }

    private fun setLayoutClickListener(layoutId: Int, title: String) {
        val layout = findViewById<LinearLayout>(layoutId)
        layout.setOnClickListener {
            when (title) {
                "Date" -> {
                    showDatePickerDialog()
                }
                "Expense Type" -> {
                    showExpenseTypeDialog()
                }
                "Expense Amount" -> {
                    showExpenseAmountDialog()
                }
            }
        }
    }

    private fun showExpenseAmountDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Expense Amount")
        val input = EditText(this)
        input.hint = "Enter Expense Amount"

        // Allow both integers and decimals
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.setText(enteredExpenseAmount.toString())

        alertDialog.setView(input)
        alertDialog.setPositiveButton("OK") { _, _ ->
            val inputText = input.text.toString()
            enteredExpenseAmount = if (inputText.isNotBlank()) inputText.toDouble() else 0.0
        }
        alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        alertDialog.show()
    }

    private fun showExpenseTypeDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Expense Type")

        val input = EditText(this)
        input.hint = "Enter Expense Type"
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        input.setText(enteredExpenseType)

        alertDialog.setView(input)
        alertDialog.setPositiveButton("OK") { _, _ ->
            // Capture the entered expense type
            enteredExpenseType = input.text.toString()
            // Handle the entered expense type (if needed)
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

