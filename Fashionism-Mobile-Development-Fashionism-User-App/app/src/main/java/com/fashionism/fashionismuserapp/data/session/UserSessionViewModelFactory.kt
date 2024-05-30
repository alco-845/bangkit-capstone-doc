package com.fashionism.fashionismuserapp.data.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserSessionViewModelFactory(private val pref: UserSession) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserSessionViewModel::class.java)) {
            return UserSessionViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}