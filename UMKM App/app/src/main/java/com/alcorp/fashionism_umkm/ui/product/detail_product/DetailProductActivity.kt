package com.alcorp.fashionism_umkm.ui.product.detail_product

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alcorp.fashionism_umkm.R
import com.alcorp.fashionism_umkm.ViewModelFactory
import com.alcorp.fashionism_umkm.data.remote.response.DetailProductData
import com.alcorp.fashionism_umkm.databinding.ActivityDetailProductBinding
import com.alcorp.fashionism_umkm.ui.product.add_or_edit_product.AddEditProductActivity
import com.alcorp.fashionism_umkm.ui.product.add_or_edit_product.AddEditProductActivity.Companion.EXTRA_EDIT_PRODUCT
import com.alcorp.fashionism_umkm.utils.Helper.showToast
import com.alcorp.fashionism_umkm.utils.LoadingDialog
import com.alcorp.fashionism_umkm.utils.PrefData
import com.alcorp.fashionism_umkm.utils.Status
import com.bumptech.glide.Glide

class DetailProductActivity : AppCompatActivity(), View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityDetailProductBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var id: String
    private lateinit var detailProductData: DetailProductData
    private val detailProductViewModel: DetailProductViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        setupToolbar()
        setupView()
        loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.tvToolbar.text = getString(R.string.title_detail_outfit)
        binding.toolbar.btnBack.setOnClickListener { finish() }
    }

    private fun setupView() {
        id = intent.getStringExtra(EXTRA_DETAIL_PRODUCT).toString()

        loadingDialog = LoadingDialog(this)
        binding.btnUpdate.setOnClickListener(this)
        binding.btnDelete.setOnClickListener(this)
        binding.refreshProductDetail.setOnRefreshListener(this)
    }

    private fun loadData() {
        detailProductViewModel.getProductDetail(PrefData.token, PrefData.idUser, id)
        detailProductViewModel.detailProductState.observe(this) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    it.data?.data.let { detail ->
                        detailProductData = DetailProductData(detail?.id, detail?.name, detail?.description, detail?.stock, detail?.price, detail?.type_id, detail?.category_id, detail?.product_image)
                        Glide.with(this@DetailProductActivity)
                            .load(detail?.product_image)
                            .placeholder(ContextCompat.getDrawable(this@DetailProductActivity, R.drawable.default_image))
                            .error(ContextCompat.getDrawable(this@DetailProductActivity, R.drawable.default_image))
                            .into(binding.ivProduct)

                        binding.tvProductDetailTitle.text = detail?.name
                        binding.tvDesc.text = detail?.description
                        binding.tvStock.text = detail?.stock
                        binding.tvPrice.text = detail?.price
                    }
                }

                else -> {
                    loadingDialog.showLoading(false)
                    showToast(this@DetailProductActivity, it.message.toString())
                }
            }
        }
    }

    private fun deleteDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.dialog_delete_product))
        builder.setPositiveButton(getString(R.string.dialog_yes)) { _, _ -> deleteData() }
        builder.setNegativeButton(getString(R.string.dialog_no)) { dialog, _ -> dialog.cancel() }
        builder.setCancelable(true)
        builder.create().show()
    }

    private fun deleteData() {
        detailProductViewModel.deleteProduct(PrefData.token, PrefData.idUser, id)
        detailProductViewModel.deleteProductState.observe(this) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    it.data?.let { data ->
                        showToast(this@DetailProductActivity, data.message.toString())
                        finish()
                    }
                }

                else -> {
                    loadingDialog.showLoading(false)
                    showToast(this@DetailProductActivity, it.message.toString())
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnUpdate -> {
                val intent = Intent(this, AddEditProductActivity::class.java)
                intent.putExtra(EXTRA_EDIT_PRODUCT, detailProductData)
                startActivityForResult(intent, PRODUCT_DETAIL_RESULT)
            }
            binding.btnDelete -> {
                deleteDialog()
            }
        }
    }

    override fun onRefresh() {
        loadData()
        binding.refreshProductDetail.isRefreshing = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == PRODUCT_DETAIL_RESULT) {
            loadData()
        }
    }

    companion object {
        const val EXTRA_DETAIL_PRODUCT = "extra_detail_product"
        const val PRODUCT_DETAIL_RESULT = 2
    }
}