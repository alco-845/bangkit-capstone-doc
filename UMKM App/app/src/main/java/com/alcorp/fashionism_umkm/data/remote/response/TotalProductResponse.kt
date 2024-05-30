package com.alcorp.fashionism_umkm.data.remote.response

import com.google.gson.annotations.SerializedName

data class TotalProductResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("data")
    val data: TotalProductData? = null
)

data class TotalProductData(
    @field:SerializedName("total_products")
    val total_products: String? = null,
)