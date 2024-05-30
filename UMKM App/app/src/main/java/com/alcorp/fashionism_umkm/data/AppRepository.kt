package com.alcorp.fashionism_umkm.data

import com.alcorp.fashionism_umkm.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AppRepository(private val apiService: ApiService) {
    fun registerUser(name: String, email: String, password: String) =
        apiService.registerUser(name, email, password)

    fun loginUser(email: String, password: String) =
        apiService.loginUser(email, password)

    fun getTotalProduct(token: String, idUser: String) =
        apiService.getTotalProduct(token, idUser)

    fun getRecentProduct(token: String, idUser: String) =
        apiService.getRecentProduct(token, idUser)

    fun getProductList(token: String, idUser: String) =
        apiService.getProductList(token, idUser)

    fun filterProductByType(token: String, idUser: String, idType: String) =
        apiService.filterProductByType(token, idUser, idType)

    fun filterProductByCategory(token: String, idUser: String, idCategory: String) =
        apiService.filterProductByCategory(token, idUser, idCategory)

    fun getProductDetail(token: String, idUser: String, idProduct: String) =
        apiService.getProductDetail(token, idUser, idProduct)

    fun insertProduct(token: String, idUser: String, name: RequestBody, description: RequestBody, stock: RequestBody, price: RequestBody, type_id: RequestBody, category_id: RequestBody, file: MultipartBody.Part) =
        apiService.insertProduct(token, idUser, name, description, stock, price, type_id, category_id, file)

    fun deleteProduct(token: String, idUser: String, idProduct: String) =
        apiService.deleteProduct(token, idUser, idProduct)

    fun updateProduct(token: String, idUser: String, idProduct: String, name: RequestBody, description: RequestBody, stock: RequestBody, price: RequestBody, type_id: RequestBody, category_id: RequestBody, file: MultipartBody.Part) =
        apiService.updateProduct(token, idUser, idProduct, name, description, stock, price, type_id, category_id, file)

    fun getProfile(token: String, idUser: String) =
        apiService.getProfile(token, idUser)

    fun updateProfile(token: String, idUser: String, name: RequestBody, email: RequestBody, phone: RequestBody, address: RequestBody, file: MultipartBody.Part) =
        apiService.updateProfile(token, idUser, name, email, phone, address, file)

    fun changePassword(token: String, idUser: String, old_password: String, new_password: String, confirm_password: String) =
        apiService.changePassword(token, idUser, old_password, new_password, confirm_password)

    fun getTypeList(token: String) = apiService.getTypeList(token)

    fun getCategoryList(token: String) = apiService.getCategoryList(token)

    companion object {
        @Volatile
        private var instance: AppRepository? = null
        fun getInstance(
            apiService: ApiService
        ): AppRepository =
            instance ?: synchronized(this) {
                instance ?: AppRepository(apiService)
            }.also { instance = it }
    }
}