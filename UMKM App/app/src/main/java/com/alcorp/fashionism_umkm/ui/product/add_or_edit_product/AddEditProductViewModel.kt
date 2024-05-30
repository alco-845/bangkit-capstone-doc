package com.alcorp.fashionism_umkm.ui.product.add_or_edit_product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alcorp.fashionism_umkm.data.AppRepository
import com.alcorp.fashionism_umkm.data.remote.response.ApiResponse
import com.alcorp.fashionism_umkm.data.remote.response.CategoryResponse
import com.alcorp.fashionism_umkm.utils.Status
import com.alcorp.fashionism_umkm.utils.UiState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddEditProductViewModel(private val repository: AppRepository) : ViewModel() {
    private val _addEditState = MutableLiveData(
        UiState(
            Status.LOADING,
            ApiResponse(),
            ""
        )
    )

    val addEditState = _addEditState

    private val _typeState = MutableLiveData(
        UiState(
            Status.LOADING,
            CategoryResponse(),
            ""
        )
    )

    val typeState = _typeState

    private val _categoryState = MutableLiveData(
        UiState(
            Status.LOADING,
            CategoryResponse(),
            ""
        )
    )

    val categoryState = _categoryState

    fun insertProduct(token: String, idUser: String, name: RequestBody, description: RequestBody, stock: RequestBody, price: RequestBody, type_id: RequestBody, category_id: RequestBody, file: MultipartBody.Part) {
        _addEditState.value = UiState.loading()
        val response = repository.insertProduct(token, idUser, name, description, stock, price, type_id, category_id, file)
        response.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    _addEditState.value = UiState.success(response.body())
                    _addEditState.value = UiState.message(response.body()!!.message.toString())
                } else {
                    val getErrorObject = JSONObject(response.errorBody()!!.string())
                    _addEditState.value = UiState.message(getErrorObject.getString("message"))
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _addEditState.value = UiState.message("Error : $t")
            }
        })
    }

    fun updateProduct(token: String, idUser: String, idProduct: String, name: RequestBody, description: RequestBody, stock: RequestBody, price: RequestBody, type_id: RequestBody, category_id: RequestBody, file: MultipartBody.Part) {
        _addEditState.value = UiState.loading()
        val response = repository.updateProduct(token, idUser, idProduct, name, description, stock, price, type_id, category_id, file)
        response.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    _addEditState.value = UiState.success(response.body())
                    _addEditState.value = UiState.message(response.body()!!.message.toString())
                } else {
                    val getErrorObject = JSONObject(response.errorBody()!!.string())
                    _addEditState.value = UiState.message(getErrorObject.getString("message"))
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                _addEditState.value = UiState.message("Error : $t")
            }
        })
    }

    fun getTypeList(token: String) {
        _typeState.value = UiState.loading()
        val response = repository.getTypeList(token)
        response.enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (response.isSuccessful) {
                    _typeState.value = UiState.success(response.body())
                } else {
                    _typeState.value = UiState.message(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                _typeState.value = UiState.message("Error : $t")
            }
        })
    }

    fun getCategoryList(token: String) {
        _categoryState.value = UiState.loading()
        val response = repository.getCategoryList(token)
        response.enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (response.isSuccessful) {
                    _categoryState.value = UiState.success(response.body())
                } else {
                    _categoryState.value = UiState.message(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                _categoryState.value = UiState.message("Error : $t")
            }
        })
    }
}