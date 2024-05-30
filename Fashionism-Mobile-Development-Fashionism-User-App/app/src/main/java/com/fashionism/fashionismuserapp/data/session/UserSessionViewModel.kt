package com.fashionism.fashionismuserapp.data.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fashionism.fashionismuserapp.data.db.UserData
import kotlinx.coroutines.launch

class UserSessionViewModel(private val pref: UserSession) : ViewModel() {

    fun getLoginSession(): LiveData<Boolean> {
        return pref.getLoginSession().asLiveData()
    }

    fun saveLoginSession(loginSession: Boolean) {
        viewModelScope.launch {
            pref.saveLoginSession(loginSession)
        }
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun getName(): LiveData<String> {
        return pref.getName().asLiveData()
    }

    fun saveName(token: String) {
        viewModelScope.launch {
            pref.saveName(token)
        }
    }

    fun getIdUser(): LiveData<Int> {
        return pref.getIdUser().asLiveData()
    }

    fun saveIdUser(idUser: Int) {
        viewModelScope.launch {
            pref.saveIdUser(idUser)
        }
    }

    fun getEmail(): LiveData<String> {
        return pref.getEmail().asLiveData()
    }

    fun saveEmail(email: String) {
        viewModelScope.launch {
            pref.saveEmail(email)
        }
    }

    fun clearDataLogin() {
        viewModelScope.launch {
            pref.clearDataLogin()
        }
    }

    fun setAllUserData(token: String, idUser: Int, name: String, email: String) {
        viewModelScope.launch {
            pref.saveToken(token)
            pref.saveIdUser(idUser)
            pref.saveName(name)
            pref.saveEmail(email)
        }
    }

    fun getAllUserData(): LiveData<UserData> {
        return pref.getAllUserData().asLiveData()
    }
}