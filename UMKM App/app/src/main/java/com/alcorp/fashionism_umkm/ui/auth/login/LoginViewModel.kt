package com.alcorp.fashionism_umkm.ui.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcorp.fashionism_umkm.data.AppRepository
import com.alcorp.fashionism_umkm.data.remote.response.LoginResponse
import com.alcorp.fashionism_umkm.utils.Status
import com.alcorp.fashionism_umkm.utils.UiState
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: AppRepository) : ViewModel() {
    private val _loginState = MutableLiveData(
        UiState(
            Status.LOADING,
            LoginResponse(),
            ""
        )
    )

    val loginState = _loginState

    fun loginUser(email: String, password: String) {
        _loginState.value = UiState.loading()
        val response = repository.loginUser(email, password)
        response.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    _loginState.value = UiState.success(response.body())
                    _loginState.value = UiState.message(response.body()!!.message.toString())
                } else {
                    val getErrorObject = JSONObject(response.errorBody()!!.string())
                    _loginState.value = UiState.message(getErrorObject.getString("message"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginState.value = UiState.message("Error : $t")
            }
        })
    }
}