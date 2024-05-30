package com.alcorp.fashionism_umkm.ui.profile.edit_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcorp.fashionism_umkm.data.AppRepository
import com.alcorp.fashionism_umkm.data.remote.response.ApiResponse
import com.alcorp.fashionism_umkm.utils.Status
import com.alcorp.fashionism_umkm.utils.UiState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileViewModel(private val repository: AppRepository) : ViewModel() {
    private val _editProfileState = MutableLiveData(
        UiState(
            Status.LOADING,
            ApiResponse(),
            ""
        )
    )

    val editProfileState = _editProfileState

    fun updateProfile(token: String, idUser: String, name: RequestBody, email: RequestBody, phone: RequestBody, address: RequestBody, file: MultipartBody.Part) {
        _editProfileState.value = UiState.loading()
        val response = repository.updateProfile(token, idUser, name, email, phone, address, file)
        response.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    _editProfileState.value = UiState.success(response.body())
                    _editProfileState.value = UiState.message(response.body()!!.message.toString())
                } else {
                    val getErrorObject = JSONObject(response.errorBody()!!.string())
                    _editProfileState.value = UiState.message(getErrorObject.getString("message"))
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _editProfileState.value = UiState.message("Error : $t")
            }
        })
    }
}