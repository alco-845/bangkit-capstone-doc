package com.alcorp.fashionism_umkm.ui.auth.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcorp.fashionism_umkm.data.AppRepository
import com.alcorp.fashionism_umkm.data.remote.response.ApiResponse
import com.alcorp.fashionism_umkm.utils.Status
import com.alcorp.fashionism_umkm.utils.UiState
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val repository: AppRepository) : ViewModel() {
    private val _registerState = MutableLiveData(
        UiState(
            Status.LOADING,
            ApiResponse(),
            ""
        )
    )

    val registerState = _registerState

    fun registerUser(name: String, email: String, password: String) {
        _registerState.value = UiState.loading()
        val response = repository.registerUser(name, email, password)
        response.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    _registerState.value = UiState.success(response.body())
                    _registerState.value = UiState.message(response.body()!!.message.toString())
                } else {
                    val getErrorObject = JSONObject(response.errorBody()!!.string())
                    _registerState.value = UiState.message(getErrorObject.getString("message"))
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _registerState.value = UiState.message("Error : $t")
            }
        })
    }
}