package com.alcorp.fashionism_umkm

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.alcorp.fashionism_umkm.databinding.ActivityMainBinding
import com.alcorp.fashionism_umkm.ui.auth.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        pref = getSharedPreferences("fashionism_umkm", MODE_PRIVATE)

        setupView()
        checkLogin()
    }

    private fun setupView() {
        val popupMenu = PopupMenu(this, null)
        val menuInflater = popupMenu.menuInflater
        menuInflater.inflate(R.menu.bottom_nav_menu, popupMenu.menu)
        val navView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(popupMenu.menu, navController)
    }

    private fun checkLogin() {
        val token = pref.getString("token", "").toString()
        if (token == null && token == "") {
            val i = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}