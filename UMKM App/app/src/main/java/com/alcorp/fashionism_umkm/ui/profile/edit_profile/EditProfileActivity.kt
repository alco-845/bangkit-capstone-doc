package com.alcorp.fashionism_umkm.ui.profile.edit_profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alcorp.fashionism_umkm.R
import com.alcorp.fashionism_umkm.ViewModelFactory
import com.alcorp.fashionism_umkm.data.remote.response.ProfileData
import com.alcorp.fashionism_umkm.databinding.ActivityEditProfileBinding
import com.alcorp.fashionism_umkm.ui.product.add_or_edit_product.camera.CameraActivity
import com.alcorp.fashionism_umkm.ui.profile.ProfileFragment.Companion.EDIT_PROFILE_RESULT
import com.alcorp.fashionism_umkm.utils.Helper.checkEmailFormat
import com.alcorp.fashionism_umkm.utils.Helper.showToast
import com.alcorp.fashionism_umkm.utils.LoadingDialog
import com.alcorp.fashionism_umkm.utils.PrefData
import com.alcorp.fashionism_umkm.utils.Status
import com.alcorp.fashionism_umkm.utils.UploadImage
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {

    private var getFile: File? = null
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var loadingDialog: LoadingDialog
    private val editProfileViewModel: EditProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            getFile = myFile

            myFile?.let { file ->
                Glide.with(this@EditProfileActivity)
                    .load(BitmapFactory.decodeFile(file.path))
                    .into(binding.ivPreviewImage)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = UploadImage.uriToFile(selectedImg, this@EditProfileActivity)

            getFile = myFile
            Glide.with(this@EditProfileActivity)
                .load(selectedImg)
                .into(binding.ivPreviewImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        init()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                showToast(this, getString(R.string.toast_permission))
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun init() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupToolbar()
        setupView()
        loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.tvToolbar.text = getString(R.string.title_update_profile)
        binding.toolbar.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupView() {
        loadingDialog = LoadingDialog(this)
        binding.btnUpload.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.txt_choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_upload_title))
        builder.setMessage(getString(R.string.dialog_upload_message))
        builder.setPositiveButton(getString(R.string.dialog_camera)) { _, _ -> startCameraX() }
        builder.setNegativeButton(getString(R.string.dialog_gallery)) { _, _ -> startGallery() }
        builder.setNeutralButton(getString(R.string.dialog_cancel)) { dialog, _ -> dialog.cancel() }
        builder.setCancelable(true)
        builder.create().show()
    }

    private fun loadData() {
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_EDIT_PROFILE, ProfileData::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_EDIT_PROFILE)
        }

        Glide.with(this@EditProfileActivity)
            .load(data?.avatar)
            .placeholder(ContextCompat.getDrawable(this@EditProfileActivity, R.drawable.default_image))
            .error(ContextCompat.getDrawable(this@EditProfileActivity, R.drawable.default_image))
            .into(binding.ivPreviewImage)

        binding.edtShopName.setText(data?.name)
        binding.edtEmail.setText(data?.email)
        binding.edtPhone.setText(data?.phone)
        binding.edtAddress.setText(data?.address)
    }

    private fun submitData() {
        if (getFile != null || intent.hasExtra(EXTRA_EDIT_PROFILE)) {
            val txtShopName = binding.edtShopName.text.toString()
            val txtEmail = binding.edtEmail.text.toString()
            val txtPhone = binding.edtPhone.text.toString()
            val txtAddress = binding.edtAddress.text.toString()

            if (txtShopName != "" || txtEmail != "" || txtPhone != "" || txtAddress != "") {
                if (checkEmailFormat(txtEmail)) {
                    val shopName = txtShopName.toRequestBody("text/plain".toMediaType())
                    val email = txtEmail.toRequestBody("text/plain".toMediaType())
                    val phone = txtPhone.toRequestBody("text/plain".toMediaType())
                    val address = txtAddress.toRequestBody("text/plain".toMediaType())

                    val imageMultiPart = if (getFile == null) {
                        MultipartBody.Part.createFormData(
                            "avatar",
                            ""
                        )
                    } else {
                        val file = UploadImage.reduceFileImage(getFile as File)
                        val requestImageFile = file.asRequestBody("image/jpeg" . toMediaTypeOrNull())
                        MultipartBody.Part.createFormData(
                            "avatar",
                            file.name,
                            requestImageFile
                        )
                    }

                    editProfileViewModel.updateProfile(
                        PrefData.token,
                        PrefData.idUser,
                        shopName,
                        email,
                        phone,
                        address,
                        imageMultiPart
                    )
                    editProfileViewModel.editProfileState.observe(this) {
                        when (it.status) {
                            Status.LOADING -> loadingDialog.showLoading(true)

                            Status.SUCCESS -> {
                                loadingDialog.showLoading(false)
                                it.data?.let { data ->
                                    showToast(
                                        this@EditProfileActivity,
                                        data.message.toString()
                                    )
                                    setResult(EDIT_PROFILE_RESULT)
                                    finish()
                                }
                            }

                            else -> {
                                loadingDialog.showLoading(false)
                                showToast(
                                    this@EditProfileActivity,
                                    it.message.toString()
                                )
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.toast_email_validate), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_insert_columns), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_insert_image), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnUpload -> uploadDialog()
            binding.btnSubmit -> submitData()
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        const val EXTRA_EDIT_PROFILE = "extra_edit_profile"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}