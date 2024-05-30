package com.fashionism.fashionismuserapp.ui

import android.app.Activity
import android.content.pm.PackageManager
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fashionism.fashionismuserapp.R
import com.fashionism.fashionismuserapp.adapter.FashionItemAdapter
import com.fashionism.fashionismuserapp.adapter.FavProductHomeItemAdapter
import com.fashionism.fashionismuserapp.data.db.Product
import com.fashionism.fashionismuserapp.data.session.UserSession
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModel
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModelFactory
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModel
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModelFactory
import com.fashionism.fashionismuserapp.databinding.FragmentHomeBinding
import com.fashionism.fashionismuserapp.tools.*
import com.fashionism.fashionismuserapp.tools.CameraUtils.startTakePhoto2
import com.fashionism.fashionismuserapp.tools.Helper.reduceFileImage
import com.fashionism.fashionismuserapp.tools.Helper.showLoading
import com.fashionism.fashionismuserapp.ui.DetailFashionActivity.Companion.EXTRA_FASHION_ITEM
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var rvLikedProduct: RecyclerView
    private lateinit var rvSuggestionsProduct: RecyclerView
    private val binding get() = _binding!!
    private var getFile: File? = null
    private lateinit var token: String

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(requireContext()))[MainViewModel::class.java]
    }

    private val userSessionViewModel by lazy {
        ViewModelProvider(
            this,
            UserSessionViewModelFactory(UserSession.getInstance(requireActivity().dataStore))
        )[UserSessionViewModel::class.java]
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                binding.imageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.login_statusBarDark)

        binding.profileAccountNavigate.setOnClickListener {
            val intent = Intent(requireContext(), ChangeProfileActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slidefromtop_in, R.anim.slidefromtop_out)
        }

        rvLikedProduct = binding.rvFavorite
        rvLikedProduct.setHasFixedSize(true)
        rvLikedProduct.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        rvSuggestionsProduct = binding.rvFashionItem
        val layoutManager = GridLayoutManager(requireContext(), 2)
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        val includeBottom = true

        rvSuggestionsProduct.layoutManager = layoutManager
        rvSuggestionsProduct.addItemDecoration(
            GridSpacingItemDecoration(
                spacing,
                includeBottom
            )
        )
        rvSuggestionsProduct.setHasFixedSize(true)

        userSessionViewModel.getToken().observe(viewLifecycleOwner) {
            token = it
            mainViewModel.getProductByCategory(1, token)
        }

        mainViewModel.productListByCategory.observe(viewLifecycleOwner) { products ->
            val favProductAdapter = FavProductHomeItemAdapter(products)
            rvLikedProduct.adapter = favProductAdapter

            favProductAdapter.setOnItemClickCallback(object :
                FavProductHomeItemAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Product) {
                    showSelectedFashion(data)
                }
            })

            val suggestionsProductAdapter = FashionItemAdapter(products, false)
            rvSuggestionsProduct.adapter = suggestionsProductAdapter

            suggestionsProductAdapter.setOnItemClickCallback(object :
                FashionItemAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Product) {
                    showSelectedFashion(data)
                }
            })
        }

        val userSession = UserSession.getInstance(requireActivity().dataStore)
        val userSessionViewModel =
            ViewModelProvider(
                this,
                UserSessionViewModelFactory(userSession)
            )[UserSessionViewModel::class.java]

        userSessionViewModel.getAllUserData().observe(viewLifecycleOwner) { dataUser ->
            mainViewModel.getProfile(dataUser.idUser, dataUser.token)
        }

        mainViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            binding.greetingUserName.text = shortenText(userProfile.data.name, 30)
            Glide.with(requireContext())
                .load(userProfile.data.avatar)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.profileAccountNavigate)
        }

        binding.searchFashionItem.setOnClickListener {
            showPopup()
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading, binding.progressBarHome)
        }

        mainViewModel.isLoadingRecommendation.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }
        }

        mainViewModel.message.observe(viewLifecycleOwner) { message ->
//            if (message == "Berhasil mendapat rekomendasi fashion") {
//                navigateToResultSearchActivity()
//            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        return root
    }

    private val launcherIntentFileManager = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, requireContext())
                binding.imageView.setImageURI(uri)
            }
        }
    }

    private val launcherIntentGallery =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val imgUri = data?.data
                binding.imageView.setImageURI(imgUri)
            }
        }

//    @Suppress("DEPRECATION")
////    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
////        super.onActivityResult(requestCode, resultCode, data)
////        if (requestCode == CameraUtils.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
////            getFile = CameraUtils.handleActivityResult(requestCode, resultCode)
////            // Lakukan sesuatu dengan file foto yang diambil
////            if (getFile != null) {
////                // Contoh: Tampilkan gambar di ImageView
////                val bitmap = BitmapFactory.decodeFile(getFile!!.absolutePath)
////                binding.blabla2.setImageBitmap(bitmap)
////            }
////        }
////
////        if (requestCode == FileManagerUtils.REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
////            val selectedImg: Uri? = data.data
////            if (selectedImg != null) {
////                val myFile = FileManagerUtils.handleActivityResult(
////                    requestCode = FileManagerUtils.REQUEST_PICK_IMAGE,
////                    resultCode = Activity.RESULT_OK,
////                    data = data,
////                    requireContext()
////                )
////                getFile = myFile
////                binding.blabla2.setImageURI(selectedImg)
////            }
////        }
////    }

    private lateinit var currentPhotoPath: String

    val REQUEST_PICK_IMAGE = 2

    private val REQUEST_IMAGE_CAPTURE = 1
    private val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

    private fun startFileManager() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentFileManager.launch(chooser)
    }

    private fun startGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        launcherIntentGallery.launch(gallery)
    }

    private fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        context: Context
    ): File? {
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val selectedImg: Uri? = data.data
            if (selectedImg != null) {
                return uriToFile(selectedImg, context)
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            return File(currentPhotoPath)
        }
        return null
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun createCustomTempFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat(
            FILENAME_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())

        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }


    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            getFile = handleActivityResult(requestCode, resultCode, data, requireContext())
            // Lakukan sesuatu dengan file foto yang diambil
            if (getFile != null) {
                // Contoh: Tampilkan gambar di ImageView
                val bitmap = BitmapFactory.decodeFile(getFile!!.absolutePath)
                binding.blabla2.setImageBitmap(bitmap)
            }
        }

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val selectedImg: Uri? = data.data
            if (selectedImg != null) {
                val myFile = handleActivityResult(
                    requestCode = REQUEST_PICK_IMAGE,
                    resultCode = resultCode,
                    data = data,
                    requireContext()
                )
                getFile = myFile
                binding.blabla2.setImageURI(selectedImg)
            }
        }
    }

//    private var mCurrentPhotoPath: File? = null
//    private var REQUEST_IMAGE_CAPTURE = 1
//    private var REQUEST_TAKE_PHOTO = 1
//    private var imagesDir = File(Environment.getExternalStorageDirectory(), "MyImages")
//    private fun dispatchTakePictureIntent() {
//        val width = 960
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
//                // Create the File where the photo should go
//                var photoFile: File? = null
//                try {
//                    photoFile = createImageFile()
//                } catch (ex: IOException) {
//                    Log.e("error", "error creating file")
//                }
//                // Continue only if the File was successfully created
//                if (photoFile != null) {
//                    var photoURI: Uri? = null
//                    try {
//                        photoURI = FileProvider.getUriForFile(
//                            requireActivity(), BuildConfig.APPLICATION_ID + ".provider",
//                            createImageFile()!!
//                        )
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                    //                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
//                }
//            }
//        } else if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
//            var photoFile: File? = null
//            try {
//                photoFile = createImageFile()
//            } catch (ignored: IOException) {
//            }
//            if (photoFile != null) {
//                mCurrentPhotoPath = photoFile
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoPath))
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
//            }
//        }
//    }
//
//    @Throws(IOException::class)
//    private fun createImageFile(): File? {
//        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
//        val imageFileName = "smok$timeStamp"
//        val image = File.createTempFile(
//            imageFileName,  /* prefix */
//            ".jpg",  /* suffix */
//            imagesDir /* directory */
//        )
//        mCurrentPhotoPath = File(image.absolutePath)
//        return image
//    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)
        createCustomTempFile(requireActivity().application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.fashionism.fashionismuserapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun sendImageRecommendation() {
        val file = reduceFileImage(getFile as File)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val final = MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestImageFile
        )
        mainViewModel.getFashionRecommendation(final)
    }

    private fun showPopup() {
        val options = arrayOf(
            resources.getString(R.string.cameraOption),
            resources.getString(R.string.fileManagerOption),
            resources.getString(R.string.cancelBtn)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.chooseOneOption))
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Camera option selected
                        startTakePhoto()
                        // Log.d("aa", "showPopup: $getFile")
                        dialog.dismiss()
                    }
                    1 -> {
                        // File Manager option selected
                        startGallery()
                        dialog.dismiss()
                    }
                    2 -> {
                        // Cancel option selected
                        dialog.dismiss()
                    }
                }
            }
            .setOnDismissListener {
                if (getFile != null) {
                    Log.d("aa", "showPopup: $getFile")
                    sendImageRecommendation()
                }
            }
            .create()
            .show()
    }

    private var loadingDialog: Dialog? = null
    private fun showLoadingDialog() {
        loadingDialog = Dialog(requireContext())
        loadingDialog?.setContentView(R.layout.await_data_search)
        loadingDialog?.setCancelable(false)
        // Mengatur ukuran dialog menjadi full screen
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(loadingDialog?.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        loadingDialog?.window?.attributes = layoutParams

        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

//    private fun navigateDummy() {
//        val intent = Intent(requireContext(), ResultSearchActivity::class.java)
//        intent.putExtra(
//            "imageUrl",
//            "https://storage.googleapis.com/fashionism/products/os1phbydqz02l7j0gylrum.jpg"
//        )
//        startActivity(intent)
//    }

    private fun navigateToResultSearchActivity() {
        val intent = Intent(requireContext(), ResultSearchActivity::class.java)
        startActivity(intent)
    }

    private fun showSelectedFashion(fashionItem: Product) {
        val intent = Intent(requireContext(), DetailFashionActivity::class.java)
        intent.putExtra(EXTRA_FASHION_ITEM, fashionItem)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.slidefromright_in, R.anim.slidefromright_out)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startCameraX() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
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

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                binding.imageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}