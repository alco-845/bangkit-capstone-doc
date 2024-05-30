package com.alcorp.fashionism_umkm.ui.product.add_or_edit_product

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alcorp.fashionism_umkm.R
import com.alcorp.fashionism_umkm.ViewModelFactory
import com.alcorp.fashionism_umkm.data.remote.response.CategoryData
import com.alcorp.fashionism_umkm.data.remote.response.DetailProductData
import com.alcorp.fashionism_umkm.databinding.ActivityAddEditProductBinding
import com.alcorp.fashionism_umkm.ui.product.ProductFragment.Companion.PRODUCT_LIST_RESULT
import com.alcorp.fashionism_umkm.ui.product.add_or_edit_product.camera.CameraActivity
import com.alcorp.fashionism_umkm.ui.product.detail_product.DetailProductActivity.Companion.PRODUCT_DETAIL_RESULT
import com.alcorp.fashionism_umkm.utils.Helper.showToast
import com.alcorp.fashionism_umkm.utils.LoadingDialog
import com.alcorp.fashionism_umkm.utils.PrefData
import com.alcorp.fashionism_umkm.utils.Status
import com.alcorp.fashionism_umkm.utils.UploadImage.reduceFileImage
import com.alcorp.fashionism_umkm.utils.UploadImage.uriToFile
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddEditProductActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private var getFile: File? = null
    private var idOutfit: Int = 0
    private var idType: String = ""
    private var idCategory: String = ""
    private val listTypeId: ArrayList<String> = ArrayList()
    private val listCategoryId: ArrayList<String> = ArrayList()
    private lateinit var binding: ActivityAddEditProductBinding
    private lateinit var loadingDialog: LoadingDialog
    private val addEditProductViewModel: AddEditProductViewModel by viewModels {
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
                Glide.with(this@AddEditProductActivity)
                    .load(BitmapFactory.decodeFile(file.path))
                    .into(binding.ivPreviewImage)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddEditProductActivity)

            getFile = myFile
            Glide.with(this@AddEditProductActivity)
                .load(selectedImg)
                .into(binding.ivPreviewImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditProductBinding.inflate(layoutInflater)
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
        if (intent.hasExtra(EXTRA_EDIT_PRODUCT)) {
            binding.toolbar.tvToolbar.text = getString(R.string.title_update)
        } else {
            binding.toolbar.tvToolbar.text = getString(R.string.title_add_product)
        }

        binding.toolbar.btnBack.setOnClickListener { finish() }
    }

    private fun setupView() {
        loadingDialog = LoadingDialog(this)

        binding.btnUpload.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.spType.onItemSelectedListener = this
        binding.spCategory.onItemSelectedListener = this
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
            intent.getParcelableExtra(EXTRA_EDIT_PRODUCT, DetailProductData::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_EDIT_PRODUCT)
        }

        if (intent.hasExtra(EXTRA_EDIT_PRODUCT)) {
            idOutfit = data?.id!!
            Glide.with(this@AddEditProductActivity)
                .load(data.product_image)
                .placeholder(ContextCompat.getDrawable(this@AddEditProductActivity, R.drawable.default_image))
                .error(ContextCompat.getDrawable(this@AddEditProductActivity, R.drawable.default_image))
                .into(binding.ivPreviewImage)

            binding.edtProductName.setText(data.name)
            binding.edtDescription.setText(data.description)
            binding.edtStock.setText(data.stock)
            binding.edtPrice.setText(data.price?.replace("IDR ", "")?.replace(",", ""))

            setSpinnerPosition(data.type_id.toString(), data.category_id.toString())
        } else {
            setSpinner()
        }
    }

    private fun setSpinnerPosition(type_id: String, category_id: String) {
        addEditProductViewModel.getTypeList(PrefData.token)
        addEditProductViewModel.typeState.observe(this) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    val listTypeData: List<CategoryData>? = it.data?.data
                    val listName: ArrayList<String> = ArrayList()
                    for (i in 1..listTypeData?.size!!) {
                        listTypeId.add(listTypeData[i-1].id.toString())
                        listName.add(listTypeData[i-1].name.toString())
                    }
                    binding.spType.adapter = ArrayAdapter(this@AddEditProductActivity, android.R.layout.simple_list_item_1, listName)

                    val selection = listTypeData.indexOfFirst { index -> index.id == type_id }
                    binding.spType.setSelection(selection)
                }
                else -> {
                    loadingDialog.showLoading(false)
                    showToast(this, it.message.toString())
                }
            }
        }

        addEditProductViewModel.getCategoryList(PrefData.token)
        addEditProductViewModel.categoryState.observe(this) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    val listCategoryData: List<CategoryData>? = it.data?.data
                    val listName: ArrayList<String> = ArrayList()
                    for (i in 1..listCategoryData?.size!!) {
                        listCategoryId.add(listCategoryData[i-1].id.toString())
                        listName.add(listCategoryData[i-1].name.toString())
                    }
                    binding.spCategory.adapter = ArrayAdapter(this@AddEditProductActivity, android.R.layout.simple_list_item_1, listName)

                    val selection = listCategoryData.indexOfFirst { index -> index.id == category_id }
                    binding.spCategory.setSelection(selection)
                }
                else -> {
                    loadingDialog.showLoading(false)
                    showToast(this, it.message.toString())
                }
            }
        }
    }

    private fun setSpinner() {
        addEditProductViewModel.getTypeList(PrefData.token)
        addEditProductViewModel.typeState.observe(this) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    val listTypeData: List<CategoryData>? = it.data?.data
                    val listName: ArrayList<String> = ArrayList()
                    for (i in 1..listTypeData?.size!!) {
                        listTypeId.add(listTypeData[i-1].id.toString())
                        listName.add(listTypeData[i-1].name.toString())
                    }
                    binding.spType.adapter = ArrayAdapter(this@AddEditProductActivity, android.R.layout.simple_list_item_1, listName)
                }
                else -> {
                    loadingDialog.showLoading(false)
                    showToast(this, it.message.toString())
                }
            }
        }

        addEditProductViewModel.getCategoryList(PrefData.token)
        addEditProductViewModel.categoryState.observe(this) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    val listCategoryData: List<CategoryData>? = it.data?.data
                    val listName: ArrayList<String> = ArrayList()
                    for (i in 1..listCategoryData?.size!!) {
                        listCategoryId.add(listCategoryData[i-1].id.toString())
                        listName.add(listCategoryData[i-1].name.toString())
                    }
                    binding.spCategory.adapter = ArrayAdapter(this@AddEditProductActivity, android.R.layout.simple_list_item_1, listName)
                }
                else -> {
                    loadingDialog.showLoading(false)
                    showToast(this, it.message.toString())
                }
            }
        }
    }

    private fun submitData() {
        if (getFile != null || intent.hasExtra(EXTRA_EDIT_PRODUCT)) {
            val txtOutfitName = binding.edtProductName.text.toString()
            val txtDesc = binding.edtDescription.text.toString()
            val txtStock = binding.edtStock.text.toString()
            val txtPrice = binding.edtPrice.text.toString()

            if (txtOutfitName != "" || txtDesc != ""|| txtStock != "" || txtPrice != "") {

                val outfitName = txtOutfitName . toRequestBody("text/plain" . toMediaType())
                val desc = txtDesc . toRequestBody("text/plain" . toMediaType())
                val stock = txtStock . toRequestBody("text/plain" . toMediaType())
                val price = txtPrice . toRequestBody("text/plain" . toMediaType())
                val type_id = idType . toRequestBody("text/plain" . toMediaType())
                val category_id = idCategory . toRequestBody("text/plain" . toMediaType())

                if (intent.hasExtra(EXTRA_EDIT_PRODUCT)) {
                    val imageMultiPart = if (getFile == null) {
                        MultipartBody.Part.createFormData(
                            "product_image",
                            ""
                        )
                    } else {
                        val file = reduceFileImage(getFile as File)
                        val requestImageFile = file.asRequestBody("image/jpeg" . toMediaTypeOrNull())
                        MultipartBody.Part.createFormData(
                            "product_image",
                            file.name,
                            requestImageFile
                        )
                    }
                    addEditProductViewModel.updateProduct(
                        PrefData.token,
                        PrefData.idUser,
                        idOutfit.toString(),
                        outfitName,
                        desc,
                        stock,
                        price,
                        type_id,
                        category_id,
                        imageMultiPart
                    )
                    addEditProductViewModel.addEditState.observe(this) {
                        when (it.status) {
                            Status.LOADING -> loadingDialog.showLoading(true)

                            Status.SUCCESS -> {
                                loadingDialog.showLoading(false)
                                it.data?.let { data ->
                                    showToast(
                                        this@AddEditProductActivity,
                                        data.message.toString()
                                    )
                                    setResult(PRODUCT_DETAIL_RESULT)
                                    finish()
                                }
                            }

                            else -> {
                                loadingDialog.showLoading(false)
                                showToast(this@AddEditProductActivity, it.message.toString())
                            }
                        }
                    }
                } else {
                    val file = reduceFileImage(getFile as File)
                    val requestImageFile = file.asRequestBody("image/jpeg" . toMediaTypeOrNull())
                    val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "product_image",
                        file.name,
                        requestImageFile
                    )
                    addEditProductViewModel.insertProduct(
                        PrefData.token,
                        PrefData.idUser,
                        outfitName,
                        desc,
                        stock,
                        price,
                        type_id,
                        category_id,
                        imageMultiPart
                    )
                    addEditProductViewModel.addEditState.observe(this) {
                        when (it.status) {
                            Status.LOADING -> loadingDialog.showLoading(true)

                            Status.SUCCESS -> {
                                loadingDialog.showLoading(false)
                                it.data?.let { data ->
                                    showToast(
                                        this@AddEditProductActivity,
                                        data.message.toString()
                                    )
                                    setResult(PRODUCT_LIST_RESULT)
                                    finish()
                                }
                            }

                            else -> {
                                loadingDialog.showLoading(false)
                                showToast(this@AddEditProductActivity, it.message.toString())
                            }
                        }
                    }
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

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.spType -> idType = listTypeId[position]
            R.id.spCategory -> idCategory = listCategoryId[position]
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}

    companion object {
        const val CAMERA_X_RESULT = 200
        const val EXTRA_EDIT_PRODUCT = "extra_edit_product"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}