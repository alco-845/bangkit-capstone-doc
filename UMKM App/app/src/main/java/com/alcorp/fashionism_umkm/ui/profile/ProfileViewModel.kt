package com.alcorp.fashionism_umkm.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcorp.fashionism_umkm.data.AppRepository
import com.alcorp.fashionism_umkm.data.remote.response.ProfileResponse
import com.alcorp.fashionism_umkm.utils.Status
import com.alcorp.fashionism_umkm.utils.UiState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val repository: AppRepository) : ViewModel() {

    private val _profileState = MutableLiveData(
        UiState(
            Status.LOADING,
            ProfileResponse(),
            ""
        )
    )

    val profileState = _profileState

    fun getProfile(token: String, idUser: String) {
        _profileState.value = UiState.loading()
        val response = repository.getProfile(token, idUser)
        response.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    _profileState.value = UiState.success(response.body())
                } else {
                    _profileState.value = UiState.message(response.errorBody()!!.toString())
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                _profileState.value = UiState.message("Error : $t")
            }
        })
    }
}