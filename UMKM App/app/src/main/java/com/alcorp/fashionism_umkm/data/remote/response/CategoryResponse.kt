package com.alcorp.fashionism_umkm.data.remote.response

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("data")
    val data: List<CategoryData>? = null
)

data class CategoryData(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("name")
    val name: String? = null
)