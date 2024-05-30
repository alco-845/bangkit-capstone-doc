package com.alcorp.fashionism_umkm.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alcorp.fashionism_umkm.R
import com.alcorp.fashionism_umkm.ViewModelFactory
import com.alcorp.fashionism_umkm.databinding.FragmentHomeBinding
import com.alcorp.fashionism_umkm.ui.auth.login.LoginActivity
import com.alcorp.fashionism_umkm.utils.Helper.showToast
import com.alcorp.fashionism_umkm.utils.LoadingDialog
import com.alcorp.fashionism_umkm.utils.PrefData
import com.alcorp.fashionism_umkm.utils.Status
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var pref: SharedPreferences
    private lateinit var chartArrayList: ArrayList<Entry>
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setupView()
        checkLogin()
        loadData()
        setDate()
        setChart()
    }

    private fun checkLogin() {
        val token = pref.getString("access_token", "").toString()
        PrefData.token = "Bearer $token"
        PrefData.idUser = pref.getString("id", "").toString()
        if (token == null && token == "") {
            val i = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }
    }

    private fun setupView() {
        pref = requireActivity().getSharedPreferences("fashionism_umkm", AppCompatActivity.MODE_PRIVATE)
        loadingDialog = LoadingDialog(requireContext())
    }

    private fun loadData() {
        homeViewModel.getTotalProduct(PrefData.token, PrefData.idUser)
        homeViewModel.totalProductState.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    it.data?.data.let { data -> binding.tvTotalProduct.text = data?.total_products }
                }
                else -> {
                    loadingDialog.showLoading(false)
                    showToast(requireContext(), it.message.toString())
                }
            }
        }

        homeViewModel.getRecentProduct(PrefData.token, PrefData.idUser)
        homeViewModel.recentProductState.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    it.data?.data.let { data -> binding.tvRecentProduct.text = data?.recent_products }
                }
                else -> {
                    loadingDialog.showLoading(false)
                    showToast(requireContext(), it.message.toString())
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDate() {
        val calendar = Calendar.getInstance()

        val getMonth = SimpleDateFormat("MMMM")
        val getDay = SimpleDateFormat("EEEE")
        val d = Date()

        val dayName = getDay.format(d)
        val month = getMonth.format(calendar.time)

        val year = calendar[Calendar.YEAR]
        val day = calendar[Calendar.DAY_OF_WEEK]

        binding.tvDate.text = "$dayName, $day $month $year"
    }

    private fun setChart() {
        setChartData()
        val dataSet = LineDataSet(chartArrayList, resources.getString(R.string.title_chart))
        val barData = LineData(dataSet)

        dataSet.setColors(Color.CYAN)
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 16f

        binding.lineTransaction.data = barData
    }

    private fun setChartData() {
        chartArrayList = ArrayList()
        for (i in 1..10) {
            val fl = i*10f
            chartArrayList.add(Entry(i.toFloat(), fl))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}