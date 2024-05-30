package com.alcorp.fashionism_umkm.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcorp.fashionism_umkm.data.AppRepository
import com.alcorp.fashionism_umkm.data.remote.response.RecentProductResponse
import com.alcorp.fashionism_umkm.data.remote.response.TotalProductResponse
import com.alcorp.fashionism_umkm.utils.Status
import com.alcorp.fashionism_umkm.utils.UiState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val repository: AppRepository) : ViewModel() {
    private val _totalProductState = MutableLiveData(
        UiState(
            Status.LOADING,
            TotalProductResponse(),
            ""
        )
    )

    val totalProductState = _totalProductState

    private val _recentProductState = MutableLiveData(
        UiState(
            Status.LOADING,
            RecentProductResponse(),
            ""
        )
    )

    val recentProductState = _recentProductState

    fun getTotalProduct(token: String, idUser: String) {
        _totalProductState.value = UiState.loading()
        val response = repository.getTotalProduct(token, idUser)
        response.enqueue(object : Callback<TotalProductResponse> {
            override fun onResponse(call: Call<TotalProductResponse>, response: Response<TotalProductResponse>) {
                if (response.isSuccessful) {
                    _totalProductState.value = UiState.success(response.body())
                } else {
                    _totalProductState.value = UiState.message(response.errorBody()!!.toString())
                }
            }

            override fun onFailure(call: Call<TotalProductResponse>, t: Throwable) {
                _totalProductState.value = UiState.message("Error : $t")
            }
        })
    }

    fun getRecentProduct(token: String, idUser: String) {
        _recentProductState.value = UiState.loading()
        val response = repository.getRecentProduct(token, idUser)
        response.enqueue(object : Callback<RecentProductResponse> {
            override fun onResponse(call: Call<RecentProductResponse>, response: Response<RecentProductResponse>) {
                if (response.isSuccessful) {
                    _recentProductState.value = UiState.success(response.body())
                } else {
                    _recentProductState.value = UiState.message(response.errorBody()!!.toString())
                }
            }

            override fun onFailure(call: Call<RecentProductResponse>, t: Throwable) {
                _recentProductState.value = UiState.message("Error : $t")
            }
        })
    }
}