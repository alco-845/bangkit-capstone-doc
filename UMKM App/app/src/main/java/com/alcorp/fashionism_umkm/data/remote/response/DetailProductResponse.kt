package com.alcorp.fashionism_umkm.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class DetailProductResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("data")
    val data: DetailProductData? = null
)

@Parcelize
data class DetailProductData(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("stock")
    val stock: String? = null,

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("type_id")
    val type_id: String? = null,

    @field:SerializedName("category_id")
    val category_id: String? = null,

    @field:SerializedName("product_image")
    val product_image: String? = null
) : Parcelable