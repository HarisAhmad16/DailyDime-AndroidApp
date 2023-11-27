package com.cmpt362team21.ui.addExpense

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentExpenseInputDialogBinding
import java.util.Calendar

class ExpenseInputActivity : AppCompatActivity() {

    private var selectedDate: Calendar? = null
    private var _binding: FragmentExpenseInputDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentExpenseInputDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up UI components and bindings
        val editTextExpenseType: LinearLayout = binding.layoutExpenseType
        val editTextExpenseAmount: LinearLayout = binding.layoutExpenseAmount

        // Set up click listener for date selection
        binding.layoutSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        val btnSave: Button = binding.btnSaveExpense
        btnSave.setOnClickListener {
            finish()
        }

        val btnCancel: Button = binding.btnCancelExpense
        btnCancel.setOnClickListener {
            Toast.makeText(
                this@ExpenseInputActivity, "Expense Entry Cancelled", Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        setupDialogListeners()
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
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        alertDialog.setView(input)
        alertDialog.setPositiveButton("OK") { _, _ ->
            val inputText = input.text.toString()
            val amount = if (inputText.isNotBlank()) inputText.toDouble() else 0.0
            // Handle the entered expense amount
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

        alertDialog.setView(input)

        alertDialog.setPositiveButton("OK") { _, _ ->
            val expenseType = input.text.toString()
            // Handle the entered expense type
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
            this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate = Calendar.getInstance()
                selectedDate?.set(year, month, dayOfMonth)
            }, year, month, day
        )

        datePickerDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

