package com.fashionism.fashionismuserapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fashionism.fashionismuserapp.R
import com.fashionism.fashionismuserapp.adapter.FashionItemAdapter
import com.fashionism.fashionismuserapp.data.db.Product
import com.fashionism.fashionismuserapp.data.session.UserSession
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModel
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModelFactory
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModel
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModelFactory
import com.fashionism.fashionismuserapp.databinding.ActivityFavoriteBinding
import com.fashionism.fashionismuserapp.tools.GridSpacingItemDecoration
import com.fashionism.fashionismuserapp.tools.Helper.showLoading
import com.fashionism.fashionismuserapp.ui.DetailFashionActivity.Companion.EXTRA_FASHION_ITEM

class FavoriteActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityFavoriteBinding.inflate(layoutInflater)
    }

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private val userSessionViewModel by lazy {
        ViewModelProvider(
            this,
            UserSessionViewModelFactory(UserSession.getInstance(dataStore))
        )[UserSessionViewModel::class.java]
    }

    private var rvFavorite: RecyclerView? = null
    private var idUser = 0
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBackFavorite.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slidefromleft_in, R.anim.slidefromleft_out)
        }

        rvFavorite = binding.rvFavoriteItem
        rvFavorite?.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(this, 2)
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        val includeBottom = true
        rvFavorite?.layoutManager = layoutManager
        rvFavorite?.addItemDecoration(
            GridSpacingItemDecoration(
                spacing,
                includeBottom
            )
        )

        userSessionViewModel.getAllUserData().observe(this) { dataUser ->
            idUser = dataUser.idUser
            token = dataUser.token
            mainViewModel.getFavorite(idUser, token)
        }

        mainViewModel.productFavorite.observe(this) { products ->
            val favProductAdapter = FashionItemAdapter(products, true)
            rvFavorite?.adapter = favProductAdapter

            favProductAdapter.setOnItemClickCallback(object :
                FashionItemAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Product) {
                    showSelectedFavorite(data)
                }
            })
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading, binding.progressBarFavorite)
        }

        mainViewModel.message.observe(this) { message ->
            if (message == "Anda belum memiliki produk favorit") {
                binding.llNodataFavorite.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSelectedFavorite(fashionItem: Product) {
        val intent = Intent(this, DetailFashionActivity::class.java)
        intent.putExtra(EXTRA_FASHION_ITEM, fashionItem)
        startActivity(intent)
    }
}