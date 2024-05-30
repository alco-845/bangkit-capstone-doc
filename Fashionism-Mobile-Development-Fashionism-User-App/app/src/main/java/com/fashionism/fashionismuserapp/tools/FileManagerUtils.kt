package com.fashionism.fashionismuserapp.tools

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.fashionism.fashionismuserapp.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object FileManagerUtils {
    private lateinit var currentPhotoPath: String

    const val REQUEST_PICK_IMAGE = 2
    private const val REQUEST_IMAGE_CAPTURE = 1
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

    fun startGallery(activity: Activity) {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        activity.startActivityForResult(
            Intent.createChooser(
                intent,
                activity.getString(R.string.choose_picture)
            ), REQUEST_PICK_IMAGE
        )
    }

    fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        context: Context
    ): File? {
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImg: Uri? = data.data
            if (selectedImg != null) {
                return uriToFile(selectedImg, context)
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            return File(currentPhotoPath)
        }
        return null
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun createCustomTempFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat(
            FILENAME_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())

        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }
}
