package com.fashionism.fashionismuserapp.tools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.fashionism.fashionismuserapp.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CameraUtils {
    private lateinit var currentPhotoPath: String

    fun startTakePhoto(activity: Activity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(activity.packageManager)
        createCustomTempFile(activity.application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                activity,
                activity.getString(R.string.package_name),
                it
            )

            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            activity.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun startTakePhoto2(context: Context) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(context.packageManager)
        createCustomTempFile(context).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                context,
                context.getString(R.string.package_name),
                it
            )

            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            // activity.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            (context as Activity).startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int): File? {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            return File(currentPhotoPath)
        }
        return null
    }

    fun handleActivityResult2(requestCode: Int, resultCode: Int): File? {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            return File(currentPhotoPath)
        }
        return null
    }

    private fun createCustomTempFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat(
            FILENAME_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())

        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    const val REQUEST_IMAGE_CAPTURE = 1
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
}