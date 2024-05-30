package com.fashionism.fashionismuserapp.data.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class UserData(
    val loginSession: Boolean,
    val token: String,
    val name: String,
    val idUser: Int,
    val email: String
)

data class RegisterResponse(
    var error: String,
    var message: String,
)

data class RegisterDataAccount(
    var name: String,
    var email: String,
    var password: String
)

data class LoginResponse(
    var error: String,
    var message: String,
    var data: LoginResult
)

data class LoginDataAccount(
    var email: String,
    var password: String
)

data class LoginResult(
    var id: Int,
    var name: String,
    var email: String,
    var access_token: String
)

data class ResponseGetProfile(
    var error: Boolean,
    var data: ProfileDetail
)

data class ResponseUpdateProfile(
    var error: Boolean,
    var message: String
)

data class ResponseChangePassword(
    var error: Boolean,
    var message: String,
)

data class ChangePassword(
    var old_password: String,
    var new_password: String,
    var confirm_password: String
)

data class ResponseGetAllPreferences(
    var error: Boolean,
    var data: List<PreferenceDetail>
)

data class PreferenceDetail(
    var id: Int,
    var name: String,
)

data class ResponseGetUserPreferences(
    var error: Boolean,
    var data: List<UserPreferences>
)

data class UserPreferences(
    var id: Int,
    var name: String,
    var user_account_preferences: UserAccountPreferences
)

data class UserAccountPreferences(
    var user_account_id: Int,
    var preference_id: Int
)

data class ResponseSetPreferences(
    var error: Boolean,
    var message: String
)

data class ProfileDetail(
    var id: Int,
    var name: String,
    var email: String,
    var phone: String? = null,
    var address: String? = null,
    var avatar: String? = null,
)

data class ItemFavorite(
    var user_account_id: Int,
    var product_id: Int,
)

data class ResponseFavorite(
    var error: Boolean,
    var message: String,
)

data class ResponseGetFavorites(
    var error: Boolean,
    var data: List<Product>? = null,
)

data class FavoriteDetail(
    var id: Int,
    var name: String,
    var description: String,
    var stock: Int,
    var price: Int,
    var image: String,
    var msme_account_id: Int,
    var user_account_favorite: ItemFavorite,
)

@Parcelize
data class ProductDetail(
    var imageFashion: String,
    var storeName: String,
    var product: String,
    var price: String,
    var description: String,
    var id: String,
) : Parcelable

data class ResponseGetAllCategory(
    var error: Boolean,
    var data: List<Category>
)

data class Category(
    var id: Int,
    var name: String,
    var createdAt: String,
    var updatedAt: String,
)

data class ResponseProductByCategory(
    var error: Boolean,
    var data: List<Product>
)

data class ResponseGetSpecificProduct(
    var error: Boolean,
    var data: Product
)

@Parcelize
data class Product(
    var id: Int,
    var name: String,
    var description: String,
    var stock: Int,
    var price: String,
    var product_image: String,
) : Parcelable

data class ResponseFashionRecommendation(
    var data: FashionRecommendation,
    var error: Boolean,
)

data class FashionRecommendation(
    var price_output: List<String>,
    var target_link: List<String>,
)