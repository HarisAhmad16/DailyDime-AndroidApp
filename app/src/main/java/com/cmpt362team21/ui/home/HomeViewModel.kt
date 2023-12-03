package com.cmpt362team21.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _firstName = MutableLiveData<String>()
    val firstName: LiveData<String> = _firstName

    private val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String> = _lastName

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    companion object {
        private val instance = HomeViewModel()

        fun getInstance(): HomeViewModel {
            return instance
        }
    }

    fun setUserNames(firstName: String, lastName: String, email: String) {
        _firstName.value = firstName
        _lastName.value = lastName
        _email.value = email
        Log.d("FirstName:", _firstName.value.toString())
    }

    fun updateFirstName(newFirstName: String) {
        _firstName.value = newFirstName
    }

    fun updateLastName(newLastName: String) {
        _lastName.value = newLastName
    }
}

