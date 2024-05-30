package com.fashionism.fashionismuserapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fashionism.fashionismuserapp.R
import com.fashionism.fashionismuserapp.data.session.UserSession
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModel
import com.fashionism.fashionismuserapp.data.session.UserSessionViewModelFactory
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModel
import com.fashionism.fashionismuserapp.data.viewmodel.MainViewModelFactory
import com.fashionism.fashionismuserapp.databinding.FragmentProfileBinding
import com.fashionism.fashionismuserapp.tools.Helper.showLoading

class ProfileFragment : Fragment() {


    private val mainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(requireContext()))[MainViewModel::class.java]
    }

    private val userSessionViewModel by lazy {
        ViewModelProvider(
            this,
            UserSessionViewModelFactory(UserSession.getInstance(requireActivity().dataStore))
        )[UserSessionViewModel::class.java]
    }

    private var _binding: FragmentProfileBinding? = null
    private lateinit var token : String
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)

        userSessionViewModel.getToken().observe(viewLifecycleOwner) {
            token = it
        }
        userSessionViewModel.getAllUserData().observe(viewLifecycleOwner) { dataUser ->
            mainViewModel.getProfile(dataUser.idUser, dataUser.token)
        }

        mainViewModel.userProfile.observe(viewLifecycleOwner) {
            binding.nameUserProfile.text = it.data.name
            binding.emailUserProfile.text = it.data.email
            Glide.with(requireContext())
                .load(it.data.avatar)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.profileImage)
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading, binding.progressBarProfile)
        }

        binding.informationAccountBtn.setOnClickListener {
            val intent = Intent(requireContext(), ChangeProfileActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slidefromright_in, R.anim.slidefromright_out)
        }

        binding.favoriteProfileBtn.setOnClickListener {
            val intent = Intent(requireContext(), FavoriteActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slidefromright_in, R.anim.slidefromright_out)
        }

        binding.changePasswordAccountBtn.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slidefromright_in, R.anim.slidefromright_out)
        }

        binding.logoutProfileBtn.setOnClickListener {
            logout()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logout() {
        userSessionViewModel.clearDataLogin()
        Toast.makeText(requireContext(), resources.getString(R.string.logoutSuccess), Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.slidefromright_in, R.anim.slidefromright_out)
    }
}