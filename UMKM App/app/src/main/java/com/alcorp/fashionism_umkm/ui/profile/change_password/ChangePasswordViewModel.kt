package com.alcorp.fashionism_umkm.ui.profile.change_password

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


class ChangePasswordViewModel(private val repository: AppRepository) : ViewModel() {
    private val _changePasswordState = MutableLiveData(
        UiState(
            Status.LOADING,
            ApiResponse(),
            ""
        )
    )

    val changePasswordState = _changePasswordState

    fun changePassword(token: String, idUser: String, old_password: String, new_password: String, confirm_password: String) {
        _changePasswordState.value = UiState.loading()
        val response = repository.changePassword(token, idUser, old_password, new_password, confirm_password)
        response.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    _changePasswordState.value = UiState.success(response.body())
                    _changePasswordState.value = UiState.message(response.body()!!.message.toString())
                } else {
                    val getErrorObject = JSONObject(response.errorBody()!!.string())
                    _changePasswordState.value = UiState.message(getErrorObject.getString("message"))
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _changePasswordState.value = UiState.message("Error : $t")
            }
        })
    }
}