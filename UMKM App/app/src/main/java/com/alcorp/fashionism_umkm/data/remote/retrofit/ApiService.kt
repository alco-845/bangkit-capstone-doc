package com.alcorp.fashionism_umkm.data.remote.retrofit

import com.alcorp.fashionism_umkm.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("auth/msme/signup")
    fun registerUser (
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("auth/msme/signin")
    fun loginUser (
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("product/total/{idUser}")
    fun getTotalProduct(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
    ): Call<TotalProductResponse>

    @GET("product/recent/{idUser}")
    fun getRecentProduct(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
    ): Call<RecentProductResponse>

    @GET("product/{idUser}")
    fun getProductList(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
    ): Call<ProductResponse>

    @GET("product/{idUser}/type/{idType}")
    fun filterProductByType(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
        @Path("idType") idType: String
    ): Call<ProductResponse>

    @GET("product/{idUser}/category/{idCategory}")
    fun filterProductByCategory(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
        @Path("idCategory") idCategory: String
    ): Call<ProductResponse>

    @GET("product/{idUser}/{idProduct}")
    fun getProductDetail(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
        @Path("idProduct") id: String
    ): Call<DetailProductResponse>

    @Multipart
    @POST("product/{idUser}")
    fun insertProduct(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("price") price: RequestBody,
        @Part("type_id") type_id: RequestBody,
        @Part("category_id") category_id: RequestBody,
        @Part file: MultipartBody.Part,
    ): Call<ApiResponse>

    @DELETE("product/{idUser}/{idProduct}")
    fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
        @Path("idProduct") id: String
    ): Call<ApiResponse>

    @Multipart
    @PUT("product/{idUser}/{idProduct}")
    fun updateProduct(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
        @Path("idProduct") idProduct: String,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("price") price: RequestBody,
        @Part("type_id") type_id: RequestBody,
        @Part("category_id") category_id: RequestBody,
        @Part file: MultipartBody.Part,
    ): Call<ApiResponse>

    @GET("profile/msme/{idUser}")
    fun getProfile(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
    ): Call<ProfileResponse>

    @Multipart
    @PUT("profile/msme/{idUser}")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
        @Part("name") name: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part file: MultipartBody.Part,
    ): Call<ApiResponse>

    @FormUrlEncoded
    @PUT("profile/msme/{idUser}/change-password")
    fun changePassword(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: String,
        @Field("old_password") old_password: String,
        @Field("new_password") new_password: String,
        @Field("confirm_password") confirm_password: String
    ): Call<ApiResponse>

    @GET("type")
    fun getTypeList(
        @Header("Authorization") token: String
    ): Call<CategoryResponse>

    @GET("category")
    fun getCategoryList(
        @Header("Authorization") token: String
    ): Call<CategoryResponse>
}