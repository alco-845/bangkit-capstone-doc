package com.alcorp.fashionism_umkm.ui.product.detail_product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcorp.fashionism_umkm.data.AppRepository
import com.alcorp.fashionism_umkm.data.remote.response.ApiResponse
import com.alcorp.fashionism_umkm.data.remote.response.DetailProductResponse
import com.alcorp.fashionism_umkm.utils.Status
import com.alcorp.fashionism_umkm.utils.UiState
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailProductViewModel(private val repository: AppRepository) : ViewModel() {
    private val _detailProductState = MutableLiveData(
        UiState(
            Status.LOADING,
            DetailProductResponse(),
            ""
        )
    )

    val detailProductState = _detailProductState

    private val _deleteProductState = MutableLiveData(
        UiState(
            Status.LOADING,
            ApiResponse(),
            ""
        )
    )

    val deleteProductState = _deleteProductState

    fun getProductDetail(token: String, idUser: String, idProduct: String) {
        _detailProductState.value = UiState.loading()
        val response = repository.getProductDetail(token, idUser, idProduct)
        response.enqueue(object : Callback<DetailProductResponse> {
            override fun onResponse(call: Call<DetailProductResponse>, response: Response<DetailProductResponse>) {
                if (response.isSuccessful) {
                    _detailProductState.value = UiState.success(response.body())
                } else {
                    _detailProductState.value = UiState.message(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<DetailProductResponse>, t: Throwable) {
                _detailProductState.value = UiState.message("Error : $t")
            }
        })
    }

    fun deleteProduct(token: String, idUser: String, idProduct: String) {
        _deleteProductState.value = UiState.loading()
        val response = repository.deleteProduct(token, idUser, idProduct)
        response.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    _deleteProductState.value = UiState.success(response.body())
                    _deleteProductState.value = UiState.message(response.body()!!.message.toString())
                } else {
                    val getErrorObject = JSONObject(response.errorBody()!!.string())
                    _deleteProductState.value = UiState.message(getErrorObject.getString("message"))
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _deleteProductState.value = UiState.message("Error : $t")
            }
        })
    }
}