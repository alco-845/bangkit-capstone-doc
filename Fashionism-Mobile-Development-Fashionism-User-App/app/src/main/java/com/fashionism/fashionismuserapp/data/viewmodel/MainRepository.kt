package com.fashionism.fashionismuserapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fashionism.fashionismuserapp.data.api.APIConfig
import com.fashionism.fashionismuserapp.data.api.APIService
import com.fashionism.fashionismuserapp.data.api.DummyAPIConfig
import com.fashionism.fashionismuserapp.data.db.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository(private val apiService: APIService) {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingRecommendation = MutableLiveData<Boolean>()
    val isLoadingRecommendation: LiveData<Boolean> = _isLoadingRecommendation

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _userLogin = MutableLiveData<LoginResponse>()
    val userLogin: LiveData<LoginResponse> = _userLogin

    private val _product = MutableLiveData<List<ProductDetail>>()
    val product: LiveData<List<ProductDetail>> = _product

    private val _productFavorite = MutableLiveData<List<Product>>()
    val productFavorite: LiveData<List<Product>> = _productFavorite

    private val _category = MutableLiveData<List<Category>>()
    val category: LiveData<List<Category>> = _category

    private val _productListByCategory = MutableLiveData<List<Product>>()
    val productListByCategory: LiveData<List<Product>> = _productListByCategory

    private val _productDetail = MutableLiveData<Product>()
    val productDetail: LiveData<Product> = _productDetail

    private val _userProfile = MutableLiveData<ResponseGetProfile>()
    val userProfile: LiveData<ResponseGetProfile> = _userProfile

    fun login(loginDataAccount: LoginDataAccount) {
        _isLoading.value = true
        val api = apiService.loginUser(loginDataAccount)
        api.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()

                if (response.isSuccessful) {
                    _userLogin.value = responseBody!!
                    _message.value = responseBody.message
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun register(registerDataAccount: RegisterDataAccount) {
        _isLoading.value = true
        val api = apiService.registerUser(registerDataAccount)
        api.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()

                if (response.isSuccessful) {
                    _message.value = responseBody?.message
                } else {
                    when (response.code()) {
                        400 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }
        })
    }

    fun getProfileUser(idUser: Int, token: String) {
        _isLoading.value = true
        val api = apiService.getUser(idUser, "Bearer $token")
        api.enqueue(object : Callback<ResponseGetProfile> {
            override fun onResponse(
                call: Call<ResponseGetProfile>,
                response: Response<ResponseGetProfile>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _userProfile.value = response.body()
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGetProfile>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun updateProfileUser(
        idUser: Int,
        name: RequestBody,
        email: RequestBody,
        phone: RequestBody?,
        address: RequestBody?,
        avatar: MultipartBody.Part,
        token: String
    ) {
        _isLoading.value = true
        val api =
            apiService.updateUser(idUser, name, email, phone, address, avatar, "  Bearer $token")
        api.enqueue(object : Callback<ResponseUpdateProfile> {
            override fun onResponse(
                call: Call<ResponseUpdateProfile>,
                response: Response<ResponseUpdateProfile>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _message.value = "Profile berhasil diubah"
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseUpdateProfile>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun changePasswordUser(idUser: Int, requestUpdate: ChangePassword, token: String) {
        _isLoading.value = true
        val api = apiService.changePassword(idUser, requestUpdate, "Bearer $token")
        api.enqueue(object : Callback<ResponseChangePassword> {
            override fun onResponse(
                call: Call<ResponseChangePassword>,
                response: Response<ResponseChangePassword>
            ) {
                _isLoading.value = false
                val responseBody = response.body()

                if (response.isSuccessful) {
                    _message.value = responseBody?.message
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseChangePassword>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun getProductLiked() {
        _isLoading.value = true
        val apiDummy = DummyAPIConfig.getApiService().getProductLiked()
        apiDummy.enqueue(object : Callback<List<ProductDetail>> {
            override fun onResponse(
                call: Call<List<ProductDetail>>,
                response: Response<List<ProductDetail>>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _product.value = response.body()
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<List<ProductDetail>>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun addProductUserFavorite(product: ItemFavorite, token: String) {
        _isLoading.value = true
        val api = apiService.addFavorite(product, "Bearer $token")
        api.enqueue(object : Callback<ResponseFavorite> {
            override fun onResponse(
                call: Call<ResponseFavorite>,
                response: Response<ResponseFavorite>
            ) {
                _isLoading.value = false
                val responseBody = response.body()

                if (response.isSuccessful) {
                    _message.value = responseBody?.message
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseFavorite>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun deleteProductUserFavorite(product: ItemFavorite, token: String) {
        _isLoading.value = true
        val api = apiService.removeFavorite(product, "Bearer $token")
        api.enqueue(object : Callback<ResponseFavorite> {
            override fun onResponse(
                call: Call<ResponseFavorite>,
                response: Response<ResponseFavorite>
            ) {
                _isLoading.value = false
                val responseBody = response.body()

                if (response.isSuccessful) {
                    _message.value = responseBody?.message
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseFavorite>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun getFavoriteProductUser(idUser: Int, token: String) {
        _isLoading.value = true
        val api = apiService.getFavoritesUser(idUser, "Bearer $token")
        api.enqueue(object : Callback<ResponseGetFavorites> {
            override fun onResponse(
                call: Call<ResponseGetFavorites>,
                response: Response<ResponseGetFavorites>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    if (response.body()?.data?.size == 0) {
                        _message.value = "Anda belum memiliki produk favorit"
                    } else {
                        _productFavorite.value = response.body()?.data!!
                    }
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGetFavorites>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun getAllCategory(token: String) {
        _isLoading.value = true
        val api = APIConfig.getApiServiceV2().getAllCategory("Bearer $token")
        api.enqueue(object : Callback<ResponseGetAllCategory> {
            override fun onResponse(
                call: Call<ResponseGetAllCategory>,
                response: Response<ResponseGetAllCategory>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _category.value = response.body()?.data
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGetAllCategory>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun getProductByCategory(idCategory: Int, token: String) {
        _isLoading.value = true
        val api = APIConfig.getApiServiceV2().getProductByCategory(idCategory, "Bearer $token")
        api.enqueue(object : Callback<ResponseProductByCategory> {
            override fun onResponse(
                call: Call<ResponseProductByCategory>,
                response: Response<ResponseProductByCategory>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _productListByCategory.value = response.body()?.data
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseProductByCategory>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun getSpecificProduct(idCategory: Int, token: String) {
        _isLoading.value = true
        val api = APIConfig.getApiServiceV2().getProductDetail(idCategory, "Bearer $token")
        api.enqueue(object : Callback<ResponseGetSpecificProduct> {
            override fun onResponse(
                call: Call<ResponseGetSpecificProduct>,
                response: Response<ResponseGetSpecificProduct>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    _productDetail.value = response.body()?.data
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseGetSpecificProduct>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }

    fun getFashionRecommendation(imageRecommendation: MultipartBody.Part) {
        _isLoadingRecommendation.value = true
        val api = apiService.getFashionRecommendation(imageRecommendation)
        api.enqueue(object : Callback<ResponseFashionRecommendation> {
            override fun onResponse(
                call: Call<ResponseFashionRecommendation>,
                response: Response<ResponseFashionRecommendation>
            ) {
                _isLoadingRecommendation.value = false

                if (response.isSuccessful) {
                    _message.value = "Berhasil mendapat rekomendasi fashion"
                } else {
                    when (response.code()) {
                        401 -> _message.value =
                            response.message()
                        408 -> _message.value =
                            "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseFashionRecommendation>, t: Throwable) {
                _isLoading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }

        })
    }
}