package com.alcorp.fashionism_umkm.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("data")
    val data: List<ProductData>? = null
)

data class ProductData(
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

    @field:SerializedName("product_image")
    val product_image: String? = null
)