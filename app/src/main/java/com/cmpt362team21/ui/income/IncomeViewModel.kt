package com.cmpt362team21.ui.income

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IncomeViewModel : ViewModel() {

    // LiveData for the displayed text in the fragment
    private val _text = MutableLiveData<String>().apply {
        value = "Income Tracker Page"
    }
    val text: LiveData<String> = _text

    // Additional LiveData or methods for income-related data
    private val _totalIncome = MutableLiveData<Double>().apply {
        value = 0.0
    }
    val totalIncome: LiveData<Double> = _totalIncome

    // Method to update the total income
    fun updateTotalIncome(newIncome: Double) {
        _totalIncome.value = _totalIncome.value?.plus(newIncome) ?: newIncome
    }

    // Any other methods or properties related to income tracking can be added here
}
