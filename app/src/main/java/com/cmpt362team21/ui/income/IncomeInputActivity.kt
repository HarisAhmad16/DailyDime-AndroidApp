package com.cmpt362team21.ui.income

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cmpt362team21.R
import com.cmpt362team21.databinding.FragmentIncomeInputDialogBinding
import java.util.Calendar

class IncomeInputActivity : AppCompatActivity() {

    private var selectedDate: Calendar? = null
    private var _binding: FragmentIncomeInputDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentIncomeInputDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up UI components and bindings
        val editTextIncomeType: LinearLayout = binding.layoutIncomeType
        val editTextIncomeAmount: LinearLayout = binding.layoutIncomeAmount

        // Set up click listener for date selection
        binding.layoutSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        val btnSave: Button = binding.btnSaveIncome
        btnSave.setOnClickListener {
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
        input.inputType = InputType.TYPE_CLASS_NUMBER
        alertDialog.setView(input)
        alertDialog.setPositiveButton("OK") { _, _ ->
            val inputText = input.text.toString()
            val amount = if (inputText.isNotBlank()) inputText.toDouble() else 0.0
            // Handle the entered income amount
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

        alertDialog.setView(input)

        alertDialog.setPositiveButton("OK") { _, _ ->
            val incomeType = input.text.toString()
            // Handle the entered income type
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
