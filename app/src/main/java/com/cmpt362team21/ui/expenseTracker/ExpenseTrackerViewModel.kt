package com.cmpt362team21.ui.expenseTracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExpenseTrackerViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "View your daily expenses below. Click on any day to get started."
    }
    val text: LiveData<String> = _text
}