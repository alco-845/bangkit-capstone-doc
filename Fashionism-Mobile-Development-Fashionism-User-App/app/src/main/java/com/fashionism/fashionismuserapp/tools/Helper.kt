package com.fashionism.fashionismuserapp.tools

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object Helper {
    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    fun showLoading(isLoading: Boolean, view: View) {
        view.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}