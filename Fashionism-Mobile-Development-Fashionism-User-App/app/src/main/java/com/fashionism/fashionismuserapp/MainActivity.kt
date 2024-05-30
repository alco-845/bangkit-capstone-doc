package com.fashionism.fashionismuserapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import androidx.navigation.findNavController
import com.fashionism.fashionismuserapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSmoothBottomMenu()

    }

    private fun setupSmoothBottomMenu() {
        val popupMenu = PopupMenu(this, null)
        val menuInflater = popupMenu.menuInflater
        menuInflater.inflate(R.menu.bottom_nav_menu, popupMenu.menu)
        val navController = findNavController(R.id.nav_host_fragment_activity_nyoba)
        binding.bottomBar.setupWithNavController(popupMenu.menu, navController)
    }
}