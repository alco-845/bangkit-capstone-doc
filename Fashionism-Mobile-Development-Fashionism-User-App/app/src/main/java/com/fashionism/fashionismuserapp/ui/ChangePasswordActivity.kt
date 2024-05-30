package com.fashionism.fashionismuserapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fashionism.fashionismuserapp.R
import com.fashionism.fashionismuserapp.data.db.ChangePassword
import com.fashionism.fashionismuserapp.data.session.UserSession
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModel
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModelFactory
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModel
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModelFactory
import com.fashionism.fashionismuserapp.databinding.ActivityChangePasswordBinding
import com.fashionism.fashionismuserapp.tools.Helper.showLoading
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private val mainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private val userSessionViewModel by lazy {
        ViewModelProvider(
            this,
            UserSessionViewModelFactory(UserSession.getInstance(dataStore))
        )[UserSessionViewModel::class.java]
    }

    private var idUser = 0
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userSessionViewModel.getAllUserData().observe(this) { dataUser ->
            idUser = dataUser.idUser
            token = dataUser.token
            mainViewModel.getProfile(idUser, token)
        }

        mainViewModel.userProfile.observe(this) { userProfile ->
            Glide.with(this)
                .load(userProfile.data.avatar)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.userImage)
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it, binding.progressBarChangePassword)
        }

        mainViewModel.message.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        binding.btnBackChangePassword.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slidefromleft_in, R.anim.slidefromleft_out)
        }

        binding.saveChangesPassword.setOnClickListener {
            if (isInputValid()) {
                showBottomSheetDialog()
            }
        }
    }

    private fun isInputValid(): Boolean {
        var isValid = true
        if (binding.passwordChangesProfileField.text.toString().isEmpty()) {
            binding.passwordChangesProfile.error = resources.getString(R.string.oldPasswordRequired)
            isValid = false
        }
        if (binding.newPasswordChangesField.text.toString().isEmpty()) {
            binding.newPasswordChanges.error = resources.getString(R.string.passwordRequired)
            isValid = false
        }
        if (binding.confirmNewPasswordChangesProfileField.text.toString().isEmpty()) {
            binding.confirmNewPasswordChangesProfile.error =
                resources.getString(R.string.confirmPasswordRequired)
            isValid = false
        }
        if (binding.newPasswordChangesField.text.toString() != binding.confirmNewPasswordChangesProfileField.text.toString()) {
            binding.confirmNewPasswordChangesProfile.error =
                resources.getString(R.string.confirmPasswordInvalid)
            isValid = false
        }
        return isValid
    }

    private fun changePassword() {
        val newPassword = ChangePassword(
            binding.passwordChangesProfileField.text.toString(),
            binding.newPasswordChangesField.text.toString(),
            binding.confirmNewPasswordChangesProfileField.text.toString()
        )
        userSessionViewModel.getAllUserData().observe(this) { dataUser ->
            idUser = dataUser.idUser
            token = dataUser.token
            mainViewModel.updatePassword(idUser, newPassword, token)
        }
    }

    private fun showBottomSheetDialog() {

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.popup_savedata)
        val save = bottomSheetDialog.findViewById<MaterialButton>(R.id.saveDataBtn)
        val close = bottomSheetDialog.findViewById<MaterialButton>(R.id.cancelDataChanges)

        bottomSheetDialog.show()

        save?.setOnClickListener {
            changePassword()
            bottomSheetDialog.dismiss()
        }

        close?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }
}