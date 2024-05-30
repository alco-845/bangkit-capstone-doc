package com.fashionism.fashionismuserapp.ui

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fashionism.fashionismuserapp.R
import com.fashionism.fashionismuserapp.data.session.UserSession
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModel
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModelFactory
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModel
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModelFactory
import com.fashionism.fashionismuserapp.databinding.ActivityChangeProfileBinding
import com.fashionism.fashionismuserapp.tools.CameraUtils
import com.fashionism.fashionismuserapp.tools.CameraUtils.startTakePhoto
import com.fashionism.fashionismuserapp.tools.FileManagerUtils
import com.fashionism.fashionismuserapp.tools.FileManagerUtils.REQUEST_PICK_IMAGE
import com.fashionism.fashionismuserapp.tools.Helper.reduceFileImage
import com.fashionism.fashionismuserapp.tools.Helper.showLoading
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class ChangeProfileActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityChangeProfileBinding.inflate(layoutInflater)
    }

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
    private var getFile: File? = null
    private lateinit var token: String
    private var isEditEnable = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userSessionViewModel.getAllUserData().observe(this) { dataUser ->
            idUser = dataUser.idUser
            token = dataUser.token
            mainViewModel.getProfile(idUser, token)
        }

        mainViewModel.userProfile.observe(this) { userProfile ->
            binding.nameSeeProfileValue.text = userProfile.data.name
            binding.emailSeeProfileValue.text = userProfile.data.email
            binding.phoneSeeProfileValue.text = userProfile.data.phone
            binding.addressSeeProfileValue.text = userProfile.data.address
            Glide.with(this)
                .load(userProfile.data.avatar)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.profileImage)
        }

        mainViewModel.message.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading, binding.progressBarChangeProfile)
        }

        binding.editProfileBtn.setOnClickListener {
            isEditEnable = true
            binding.titleSeeProfile.text = resources.getString(R.string.editProfileBtnChangeProfile)
            binding.editProfileBtn.text = resources.getString(R.string.saveBtn)
            hiddenTextView()
            showEditText()
            binding.profileImage.strokeColor = ColorStateList.valueOf(Color.RED)
            binding.profileImage.strokeWidth = 3F
            binding.editImageIcon.visibility = View.VISIBLE
            mainViewModel.userProfile.observe(this) { userProfile ->
                binding.inputNameProfileField.setText(userProfile.data.name)
                binding.inputEmailProfileField.setText(userProfile.data.email)
                binding.inputPhoneProfileField.setText(userProfile.data.phone)
                binding.inputAddressProfileField.setText(userProfile.data.address)
            }
        }

        binding.editImageIcon.setOnClickListener {
            if (isEditEnable) {
                showPopup()
            }
        }

        binding.profileImage.setOnClickListener {
            if (isEditEnable) {
                showPopup()
            }
        }

        binding.btnBackRegister.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slidefrombottom_in, R.anim.slidefrombottom_out)
        }

        binding.saveProfileBtn.setOnClickListener {
            if (isInputValid()) {
                window.setDimAmount(0.5f)
                showBottomSheetDialog()
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CameraUtils.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            getFile = CameraUtils.handleActivityResult(requestCode, resultCode)
            // Lakukan sesuatu dengan file foto yang diambil
            if (getFile != null) {
                // Contoh: Tampilkan gambar di ImageView
                val bitmap = BitmapFactory.decodeFile(getFile!!.absolutePath)
                binding.profileImage.setImageBitmap(bitmap)
            }
        }

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImg: Uri? = data.data
            if (selectedImg != null) {
                val myFile = FileManagerUtils.handleActivityResult(
                    requestCode = REQUEST_PICK_IMAGE,
                    resultCode = Activity.RESULT_OK,
                    data = data,
                    this
                )
                getFile = myFile
                binding.profileImage.setImageURI(selectedImg)
            }
        }
    }

    private fun showPopup() {
        val options = arrayOf(
            resources.getString(R.string.cameraOption),
            resources.getString(R.string.fileManagerOption),
            resources.getString(R.string.cancelBtn)
        )
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.chooseOneOption))
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Camera option selected
                        startTakePhoto(this)
                        dialog.dismiss()
                    }
                    1 -> {
                        // File Manager option selected
                        FileManagerUtils.startGallery(this)
                        dialog.dismiss()
                    }
                    2 -> {
                        // Cancel option selected
                        dialog.dismiss()
                    }
                }
            }
            .create()
            .show()
    }

    private fun isInputValid(): Boolean {
        var isValid = true
        if (binding.inputNameProfileField.text.toString().isEmpty()) {
            binding.inputNameProfileField.error = resources.getString(R.string.nameRequired)
            isValid = false
        }
        if (binding.inputEmailProfileField.text.toString().isEmpty()) {
            binding.inputEmailProfileField.error = resources.getString(R.string.emailRequired)
            isValid = false
        }
        return isValid
    }

    private fun hiddenTextView() {
        binding.nameSeeProfileValue.visibility = View.GONE
        binding.emailSeeProfileValue.visibility = View.GONE
        binding.phoneSeeProfileValue.visibility = View.GONE
        binding.addressSeeProfileValue.visibility = View.GONE

        binding.nameSeeProfile.visibility = View.GONE
        binding.emailSeeProfile.visibility = View.GONE
        binding.phoneSeeProfile.visibility = View.GONE
        binding.addressSeeProfile.visibility = View.GONE
        binding.editProfileBtn.visibility = View.GONE
    }

    private fun showEditText() {
        binding.nameSeeEditProfile.visibility = View.VISIBLE
        binding.emailSeeEditProfile.visibility = View.VISIBLE
        binding.phoneSeeEditProfile.visibility = View.VISIBLE
        binding.addressSeeEditProfile.visibility = View.VISIBLE

        binding.inputNameProfile.visibility = View.VISIBLE
        binding.inputEmailProfile.visibility = View.VISIBLE
        binding.inputPhoneProfile.visibility = View.VISIBLE
        binding.inputAddressProfile.visibility = View.VISIBLE
        binding.saveProfileBtn.visibility = View.VISIBLE
    }

    private fun changeDataProfile() {
        val imageMultiPart = if (getFile == null) {
            MultipartBody.Part.createFormData(
                "avatar",
                ""
            )
        } else {
            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(
                "avatar",
                file.name,
                requestImageFile
            )
        }

        val name = binding.inputNameProfileField.text.toString().trim()
        val email = binding.inputEmailProfileField.text.toString().trim()
        val phone = binding.inputPhoneProfileField.text.toString().trim()
        val address = binding.inputAddressProfileField.text.toString().trim()

        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val addressPart = address.toRequestBody("text/plain".toMediaTypeOrNull())

        mainViewModel.updateProfile(
            idUser,
            namePart,
            emailPart,
            phonePart,
            addressPart,
            imageMultiPart,
            token
        )
    }

    private fun showBottomSheetDialog() {

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.popup_savedata)
        val save = bottomSheetDialog.findViewById<MaterialButton>(R.id.saveDataBtn)
        val close = bottomSheetDialog.findViewById<MaterialButton>(R.id.cancelDataChanges)

        bottomSheetDialog.show()

        save?.setOnClickListener {
            changeDataProfile()
            bottomSheetDialog.dismiss()
        }

        close?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

}