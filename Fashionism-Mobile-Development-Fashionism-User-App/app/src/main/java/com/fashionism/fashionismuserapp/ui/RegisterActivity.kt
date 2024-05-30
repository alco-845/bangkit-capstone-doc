package com.fashionism.fashionismuserapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fashionism.fashionismuserapp.MainActivity
import com.fashionism.fashionismuserapp.R
import com.fashionism.fashionismuserapp.data.db.LoginDataAccount
import com.fashionism.fashionismuserapp.data.db.RegisterDataAccount
import com.fashionism.fashionismuserapp.data.session.UserSession
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModel
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModelFactory
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModel
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModelFactory
import com.fashionism.fashionismuserapp.databinding.ActivityRegisterBinding
import com.fashionism.fashionismuserapp.tools.Helper.showLoading

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private val loginViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private val userSessionViewModel by lazy {
        ViewModelProvider(
            this,
            UserSessionViewModelFactory(UserSession.getInstance(dataStore))
        )[UserSessionViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()

        userSessionViewModel.getLoginSession().observe(this) { sessionTrue ->
            if (sessionTrue) {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.slidefromright_in, R.anim.slidefromright_out)
            }
        }

        registerViewModel.message.observe(this) { messageRegist ->
            responseRegister(
                messageRegist
            )
        }

        registerViewModel.isLoading.observe(this) {
            showLoading(it, binding.progressBarRegister)
        }

        loginViewModel.message.observe(this) { messageLogin ->
            responseLogin(
                messageLogin,
                userSessionViewModel
            )
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it, binding.progressBarRegister)
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            registerBtn.setOnClickListener {
                inputNameRegisterField.clearFocus()
                inputEmailRegisterField.clearFocus()
                inputPasswordRegisterField.clearFocus()
                inputRetypePasswordRegisterField.clearFocus()

                if (isDataValid()) {
                    val dataAccount = RegisterDataAccount(
                        inputNameRegisterField.text.toString().trim(),
                        inputEmailRegisterField.text.toString().trim(),
                        inputPasswordRegisterField.text.toString().trim(),
                    )
                    registerViewModel.register(dataAccount)
                }
            }

            loginNavigate.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit)
            }

            btnBackRegister.setOnClickListener {
                finish()
                overridePendingTransition(R.anim.slidefromleft_in, R.anim.slidefromleft_out)
            }
        }
    }

    private fun isDataValid(): Boolean {
        val name = binding.inputNameRegisterField.text.toString().trim()
        val email = binding.inputEmailRegisterField.text.toString().trim()
        val phone = binding.inputPhoneRegisterField.text.toString().trim()
        val passwordEditText = binding.inputPasswordRegisterField
        val password = passwordEditText.text.toString().trim()
        val retypePasswordEditText = binding.inputRetypePasswordRegisterField
        val retypePassword = retypePasswordEditText.text.toString().trim()

        val nameErrorRes = if (name.isEmpty()) R.string.nameRequired else null
        val emailErrorRes = if (email.isEmpty()) R.string.emailRequired
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) R.string.emailInvalid
        else null

        val phoneErrorRes = if (phone.isEmpty()) R.string.phoneRequired
        else if (phone.length < 10 || phone.length > 13) R.string.phoneInvalid
        else null

        val passwordErrorRes = if (password.isEmpty()) R.string.passwordRequired
        else if (password.length < 6) R.string.passwordInvalid
        else null

        val retypePasswordErrorRes = if (retypePassword.isEmpty()) R.string.confirmPasswordRequired
        else if (retypePassword != password) R.string.confirmPasswordInvalid
        else null

        binding.inputNameRegister.error = nameErrorRes?.let { getString(it) }
        binding.inputEmailRegister.error = emailErrorRes?.let { getString(it) }
        binding.inputPhoneRegister.error = phoneErrorRes?.let { getString(it) }
        binding.inputPasswordRegister.error = passwordErrorRes?.let { getString(it) }
        binding.inputRetypePasswordRegister.error = retypePasswordErrorRes?.let { getString(it) }

        return nameErrorRes == null && emailErrorRes == null && phoneErrorRes == null && passwordErrorRes == null && retypePasswordErrorRes == null
    }

    private fun responseLogin(
        message: String,
        userSessionViewModel: UserSessionViewModel
    ) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        if (message.contains("Login")) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val user = loginViewModel.userLogin.value
            userSessionViewModel.saveLoginSession(true)
            userSessionViewModel.saveIdUser(user?.data!!.id)
            userSessionViewModel.saveToken(user.data.access_token)
            userSessionViewModel.saveName(user.data.name)
            userSessionViewModel.saveEmail(user.data.email)
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun responseRegister(
        message: String,
    ) {
        if (message.contains("login")) {
            Toast.makeText(
                this,
                resources.getString(R.string.registerSuccess),
                Toast.LENGTH_SHORT
            ).show()
            val userLogin = LoginDataAccount(
                binding.inputEmailRegisterField.text.toString(),
                binding.inputPasswordRegisterField.text.toString()
            )
            loginViewModel.login(userLogin)
        } else {
            if (message.contains("already")) {
                binding.inputEmailRegisterField.error =
                    resources.getString(R.string.emailAlreadyExist)

                Toast.makeText(this, message, Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}