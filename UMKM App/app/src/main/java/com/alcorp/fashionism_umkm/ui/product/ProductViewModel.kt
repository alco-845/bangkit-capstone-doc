package com.alcorp.fashionism_umkm.ui.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcorp.fashionism_umkm.data.AppRepository
import com.alcorp.fashionism_umkm.data.remote.response.ProductResponse
import com.alcorp.fashionism_umkm.utils.Status
import com.alcorp.fashionism_umkm.utils.UiState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductViewModel(private val repository: AppRepository) : ViewModel() {
    private val _productState = MutableLiveData(
        UiState(
            Status.LOADING,
            ProductResponse(),
            ""
        )
    )

    val productState = _productState

    private val _categoryState = MutableLiveData(
        UiState(
            Status.LOADING,
            ProductResponse(),
            ""
        )
    )

    val categoryState = _categoryState

    fun getProductList(token: String, idUser: String) {
        _productState.value = UiState.loading()
        val response = repository.getProductList(token, idUser)
        response.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    _productState.value = UiState.success(response.body())
                } else {
                    _productState.value = UiState.message(response.errorBody()!!.toString())
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                _productState.value = UiState.message("Error : $t")
            }
        })
    }

    fun filterProductByType(token: String, idUser: String, idType: String) {
        _productState.value = UiState.loading()
        val response = repository.filterProductByType(token, idUser, idType)
        response.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    _productState.value = UiState.success(response.body())
                } else {
                    _productState.value = UiState.message(response.errorBody()!!.toString())
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                _productState.value = UiState.message("Error : $t")
            }
        })
    }

    fun filterProductByCategory(token: String, idUser: String, idCategory: String) {
        _categoryState.value = UiState.loading()
        val response = repository.filterProductByCategory(token, idUser, idCategory)
        response.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    _categoryState.value = UiState.success(response.body())
                } else {
                    _categoryState.value = UiState.message(response.errorBody()!!.toString())
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                _categoryState.value = UiState.message("Error : $t")
            }
        })
    }
}