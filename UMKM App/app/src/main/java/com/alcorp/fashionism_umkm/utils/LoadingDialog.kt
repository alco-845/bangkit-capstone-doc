package com.alcorp.fashionism_umkm.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.alcorp.fashionism_umkm.R

class LoadingDialog(context: Context) {
    private var dialog: Dialog = Dialog(context)

    private fun showDialog() {
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.create()
        dialog.show()
    }

    private fun hideDialog() {
        dialog.dismiss()
    }

    fun showLoading(isLoading: Boolean) {
        if (isLoading) showDialog() else hideDialog()
    }
}

