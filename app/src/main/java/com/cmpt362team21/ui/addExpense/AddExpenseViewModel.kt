package com.cmpt362team21.ui.addExpense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddExpenseViewModel : ViewModel() {

    // LiveData for the displayed text in the fragment
    private val _text = MutableLiveData<String>().apply {
        value = "Expense Tracker Page"
    }
    val text: LiveData<String> = _text

    // Additional LiveData or methods for expense-related data
    private val _totalExpense = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val totalExpense: LiveData<Double> = _totalExpense

    // Method to update the total expense
    fun updateTotalExpense(newExpense: Double) {
        _totalExpense.value = _totalExpense.value?.plus(newExpense) ?: newExpense
    }

    // Any other methods or properties related to expense tracking can be added here
}
