package com.alcorp.fashionism_umkm.ui.auth.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alcorp.fashionism_umkm.MainActivity
import com.alcorp.fashionism_umkm.R
import com.alcorp.fashionism_umkm.ViewModelFactory
import com.alcorp.fashionism_umkm.databinding.ActivityLoginBinding
import com.alcorp.fashionism_umkm.ui.auth.register.RegisterActivity
import com.alcorp.fashionism_umkm.utils.Helper.checkEmailFormat
import com.alcorp.fashionism_umkm.utils.Helper.showToast
import com.alcorp.fashionism_umkm.utils.LoadingDialog
import com.alcorp.fashionism_umkm.utils.Status

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var pref: SharedPreferences
    private lateinit var prefEdit: SharedPreferences.Editor
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        init()
    }

    private fun init() {
        setupToolbar()
        setupView()
        checkLogin()
    }

    private fun setupToolbar() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupView() {
        pref = getSharedPreferences("fashionism_umkm", MODE_PRIVATE)

        loadingDialog = LoadingDialog(this)
        binding.tvSignUp.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
    }

    private fun checkLogin() {
        val token = pref.getString("access_token", "")
        if (token != null && token != "") {
            val i = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun loginUser() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (checkEmailFormat(email)) {
            if (password.length >= 6) {
                loginViewModel.loginUser(email, password)
                loginViewModel.loginState.observe(this) {
                    when (it.status) {
                        Status.LOADING -> loadingDialog.showLoading(true)

                        Status.SUCCESS -> {
                            loadingDialog.showLoading(false)
                            it.data?.let { login ->
                                prefEdit = pref.edit()
                                prefEdit.putString("id", login.data?.id!!)
                                prefEdit.putString("access_token", login.data.access_token)
                                prefEdit.apply()

                                showToast(this@LoginActivity, login.message.toString())

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                        else -> {
                            loadingDialog.showLoading(false)
                            showToast(this@LoginActivity, it.message.toString())
                        }
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_password_min_length), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_email_validate), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View) {
        when(view) {
            binding.tvSignUp -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            binding.btnLogin -> loginUser()
        }
    }
}