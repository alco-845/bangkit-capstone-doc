package com.alcorp.fashionism_umkm.data.remote.response

import com.google.gson.annotations.SerializedName

data class RecentProductResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("data")
    val data: RecentProductData? = null
)

data class RecentProductData(
    @field:SerializedName("recent_products")
    val recent_products: String? = null,
)