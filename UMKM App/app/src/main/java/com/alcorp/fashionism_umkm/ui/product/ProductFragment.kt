package com.alcorp.fashionism_umkm.ui.product

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alcorp.fashionism_umkm.R
import com.alcorp.fashionism_umkm.ViewModelFactory
import com.alcorp.fashionism_umkm.data.remote.response.CategoryData
import com.alcorp.fashionism_umkm.databinding.FragmentProductBinding
import com.alcorp.fashionism_umkm.ui.product.add_or_edit_product.AddEditProductActivity
import com.alcorp.fashionism_umkm.ui.product.add_or_edit_product.AddEditProductViewModel
import com.alcorp.fashionism_umkm.utils.Helper.showToast
import com.alcorp.fashionism_umkm.utils.LoadingDialog
import com.alcorp.fashionism_umkm.utils.PrefData
import com.alcorp.fashionism_umkm.utils.Status

class ProductFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
    AdapterView.OnItemSelectedListener {

    private var idType: String = ""
    private var idCategory: String = ""
    private val listTypeId: ArrayList<String> = ArrayList()
    private val listCategoryId: ArrayList<String> = ArrayList()
    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialog: Dialog
    private lateinit var loadingDialog: LoadingDialog
    private val productViewModel: ProductViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private val addEditProductViewModel: AddEditProductViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setupToolbar()
        setupView()
        loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.btnBack.visibility = View.GONE
        binding.toolbar.tvToolbar.text = getString(R.string.title_all_product)
        binding.toolbar.btnFilter.visibility = View.VISIBLE
        binding.toolbar.btnFilter.setOnClickListener {
            filterDialog()
        }
        binding.toolbar.btnAdd.visibility = View.VISIBLE
        binding.toolbar.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddEditProductActivity::class.java)
            startActivityForResult(intent, PRODUCT_LIST_RESULT)
        }
    }

    private fun setupView() {
        val pref = requireActivity().getSharedPreferences("fashionism_umkm", AppCompatActivity.MODE_PRIVATE)
        PrefData.token = "Bearer ${pref.getString("access_token", "").toString()}"
        PrefData.idUser = pref.getString("id", "").toString()

        dialog = Dialog(requireContext())
        loadingDialog = LoadingDialog(requireContext())
        binding.refreshProductList.setOnRefreshListener(this)
    }

    private fun filterDialog() {
        dialog.setContentView(R.layout.dialog_filter)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setSpinner()

        val clType = dialog.findViewById<ConstraintLayout>(R.id.clType)
        val clCategory = dialog.findViewById<ConstraintLayout>(R.id.clCategory)
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)
        val params = btnSubmit.layoutParams as ConstraintLayout.LayoutParams

        val rbAll = dialog.findViewById<RadioButton>(R.id.rbAll)
        rbAll.setOnClickListener {
            clType.visibility = View.GONE
            clCategory.visibility = View.GONE
            params.topToBottom = radioGroup.id
            btnSubmit.requestLayout()
        }

        val rbType = dialog.findViewById<RadioButton>(R.id.rbType)
        rbType.setOnClickListener {
            clType.visibility = View.VISIBLE
            clCategory.visibility = View.GONE
            params.topToBottom = clType.id
            btnSubmit.requestLayout()
        }

        val rbCategory = dialog.findViewById<RadioButton>(R.id.rbCategory)
        rbCategory.setOnClickListener {
            clType.visibility = View.GONE
            clCategory.visibility = View.VISIBLE
            params.topToBottom = clCategory.id
            btnSubmit.requestLayout()
        }

        btnSubmit.setOnClickListener {
            if (rbType.isChecked) {
                filterByType()
            } else if (rbCategory.isChecked) {
                filterByCategory()
            } else {
                loadData()
            }
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.create()
        dialog.show()
    }

    private fun setSpinner() {
        addEditProductViewModel.getTypeList(PrefData.token)
        addEditProductViewModel.typeState.observe(requireActivity()) {
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
                    val spType = dialog.findViewById<Spinner>(R.id.spType)
                    spType.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, listName)
                    spType.onItemSelectedListener = this
                }
                else -> {
                    loadingDialog.showLoading(false)
                    showToast(requireActivity(), it.message.toString())
                }
            }
        }

        addEditProductViewModel.getCategoryList(PrefData.token)
        addEditProductViewModel.categoryState.observe(requireActivity()) {
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
                    val spCategory = dialog.findViewById<Spinner>(R.id.spCategory)
                    spCategory.adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, listName)
                    spCategory.onItemSelectedListener = this
                }
                else -> {
                    loadingDialog.showLoading(false)
                    showToast(requireActivity(), it.message.toString())
                }
            }
        }
    }

    private fun loadData() {
        productViewModel.getProductList(PrefData.token, PrefData.idUser)
        productViewModel.productState.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    it.data?.let { data ->
                        checkIfFragmentAttached {
                            val productAdapter = data.data?.let { listProduct -> ProductAdapter(listProduct) }
                            binding.rvProduct.setHasFixedSize(true)
                            binding.rvProduct.layoutManager = GridLayoutManager(requireContext(), 2)
                            binding.rvProduct.adapter = productAdapter
                        }
                    }
                }

                else -> {
                    loadingDialog.showLoading(false)
                    showToast(requireContext(), it.message.toString())
                }
            }
        }
    }

    private fun filterByType() {
        productViewModel.filterProductByType(PrefData.token, PrefData.idUser, idType)
        productViewModel.productState.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    it.data?.let { data ->
                        checkIfFragmentAttached {
                            val productAdapter = data.data?.let { listProduct -> ProductAdapter(listProduct) }
                            binding.rvProduct.setHasFixedSize(true)
                            binding.rvProduct.layoutManager = GridLayoutManager(requireContext(), 2)
                            binding.rvProduct.adapter = productAdapter
                        }
                    }
                }

                else -> {
                    loadingDialog.showLoading(false)
                    showToast(requireContext(), it.message.toString())
                }
            }
        }
    }

    private fun filterByCategory() {
        productViewModel.filterProductByCategory(PrefData.token, PrefData.idUser, idCategory)
        productViewModel.categoryState.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    it.data?.let { data ->
                        checkIfFragmentAttached {
                            val productAdapter = data.data?.let { listProduct -> ProductAdapter(listProduct) }
                            binding.rvProduct.setHasFixedSize(true)
                            binding.rvProduct.layoutManager = GridLayoutManager(requireContext(), 2)
                            binding.rvProduct.adapter = productAdapter
                        }
                    }
                }

                else -> {
                    loadingDialog.showLoading(false)
                    showToast(requireContext(), it.message.toString())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onRefresh() {
        loadData()
        binding.refreshProductList.isRefreshing = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == PRODUCT_LIST_RESULT) {
            loadData()
        }
    }

    private fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.spType -> idType = listTypeId[position]
            R.id.spCategory -> idCategory = listCategoryId[position]
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PRODUCT_LIST_RESULT = 1
    }
}