package com.alcorp.fashionism_umkm.ui.profile.change_password

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alcorp.fashionism_umkm.R
import com.alcorp.fashionism_umkm.ViewModelFactory
import com.alcorp.fashionism_umkm.databinding.ActivityChangePasswordBinding
import com.alcorp.fashionism_umkm.utils.Helper.showToast
import com.alcorp.fashionism_umkm.utils.LoadingDialog
import com.alcorp.fashionism_umkm.utils.PrefData
import com.alcorp.fashionism_umkm.utils.Status

class ChangePasswordActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var loadingDialog: LoadingDialog
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        init()
    }

    private fun init() {
        setupToolbar()
        setupView()
    }

    private fun setupToolbar() {
        binding.toolbar.tvToolbar.text = getString(R.string.title_change_password)
        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupView() {
        loadingDialog = LoadingDialog(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    private fun submitData() {
        val passwordOld = binding.edtPasswordOld.text.toString().trim()
        val passwordNew = binding.edtPasswordNew.text.toString().trim()
        val passwordConf = binding.edtConfPassword.text.toString().trim()

        if (passwordOld.length >= 6 || passwordNew.length >= 6 || passwordConf.length >= 6) {
            if (passwordNew == passwordConf) {
//                lifecycleScope.launch {
//                    changePasswordViewModel.changePassword(PrefData.token, PrefData.idUser, passwordOld, passwordNew, passwordConf)
//                    changePasswordViewModel.changePasswordState.collect {
//                        when (it.status) {
//                            Status.LOADING -> loadingDialog.showLoading(true)
//
//                            Status.SUCCESS -> {
//                                loadingDialog.showLoading(false)
//                                it.data.let { data ->
//                                    showToast(this@ChangePasswordActivity, data?.message.toString())
//                                    finish()
//                                }
//                            }
//
//                            else -> {
//                                loadingDialog.showLoading(false)
//                                showToast(this@ChangePasswordActivity, it.data?.error.toString())
//                            }
//                        }
//                    }
//                }
                changePasswordViewModel.changePassword(PrefData.token, PrefData.idUser, passwordOld, passwordNew, passwordConf)
                changePasswordViewModel.changePasswordState.observe(this) {
                    when (it.status) {
                        Status.LOADING -> loadingDialog.showLoading(true)

                        Status.SUCCESS -> {
                            loadingDialog.showLoading(false)
                            it.data.let { data ->
                                showToast(this@ChangePasswordActivity, data?.message.toString())
                                finish()
                            }
                        }

                        else -> {
                            loadingDialog.showLoading(false)
                            showToast(this@ChangePasswordActivity, it.message.toString())
                        }
                    }
                }
            } else {
                showToast(this, getString(R.string.toast_password_match))
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_password_min_length), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnSubmit -> submitData()
        }
    }
}