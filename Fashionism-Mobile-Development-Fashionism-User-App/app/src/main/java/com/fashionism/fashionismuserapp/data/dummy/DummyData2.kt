package com.fashionism.fashionismuserapp.data.dummy

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DummyData2(
    val id: Int,
    val imageOne: Int,
    val name: String,
    val price: String,
    val storeName: String,
    val desc: String,
    val stock: Int,
) : Parcelable

