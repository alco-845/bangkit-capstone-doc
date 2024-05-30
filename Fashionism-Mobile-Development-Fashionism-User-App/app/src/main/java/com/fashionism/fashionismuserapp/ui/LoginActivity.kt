package com.fashionism.fashionismuserapp.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.fashionism.fashionismuserapp.MainActivity
import com.fashionism.fashionismuserapp.R
import com.fashionism.fashionismuserapp.data.db.LoginDataAccount
import com.fashionism.fashionismuserapp.data.session.UserSession
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModel
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModelFactory
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModel
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModelFactory
import com.fashionism.fashionismuserapp.databinding.ActivityLoginBinding
import com.fashionism.fashionismuserapp.tools.Helper.showLoading

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val loginViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupClickListeners()
        window.statusBarColor = ContextCompat.getColor(this, R.color.login_statusBar)

        val userSession = UserSession.getInstance(dataStore)
        val userSessionViewModel =
            ViewModelProvider(
                this,
                UserSessionViewModelFactory(userSession)
            )[UserSessionViewModel::class.java]

        userSessionViewModel.getLoginSession().observe(this) { sessionTrue ->
            if (sessionTrue) {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.slidefromright_in, R.anim.slidefromright_out)
            }
        }

        loginViewModel.message.observe(this) { message ->
            responseLogin(
                message,
                userSessionViewModel
            )
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it, binding.progressBarLogin)
        }
    }

    private fun isDataValid(): Boolean {
        val email = binding.inputEmailLoginField.text.toString().trim()
        val password = binding.inputPasswordLoginField.text.toString().trim()

        val emailErrorRes = if (email.isEmpty()) R.string.emailRequired
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) R.string.emailInvalid
        else null

        val passwordErrorRes = if (password.isEmpty()) R.string.passwordRequired
        else if (password.length < 6) R.string.passwordInvalid
        else null

        binding.inputEmailLogin.error = emailErrorRes?.let { getString(it) }
        binding.inputPasswordLogin.error = passwordErrorRes?.let { getString(it) }

        return emailErrorRes == null && passwordErrorRes == null
    }

    private fun setupClickListeners() {
        with(binding) {
            loginBtn.setOnClickListener {
                inputEmailLogin.clearFocus()
                inputPasswordLogin.clearFocus()

                if (isDataValid()) {
                    val requestLogin = LoginDataAccount(
                        inputEmailLoginField.text.toString().trim(),
                        inputPasswordLoginField.text.toString().trim()
                    )
                    loginViewModel.login(requestLogin)
                }
            }

            signUpNavigate.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit)
            }

            backButtonLogin.setOnClickListener {
                finish()
                overridePendingTransition(R.anim.slidefromleft_in, R.anim.slidefromleft_out)
            }
        }
    }

    private fun responseLogin(
        message: String,
        userSessionViewModel: UserSessionViewModel
    ) {
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
}