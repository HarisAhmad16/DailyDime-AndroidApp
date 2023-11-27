package com.cmpt362team21.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _firstName = MutableLiveData<String>()
    val firstName: LiveData<String> = _firstName

    private val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String> = _lastName

    fun setUserNames(firstName: String, lastName: String) {
        _firstName.postValue(firstName)
        _lastName.postValue(lastName)
    }
}
