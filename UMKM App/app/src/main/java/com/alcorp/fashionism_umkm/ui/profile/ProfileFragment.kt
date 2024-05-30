package com.alcorp.fashionism_umkm.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alcorp.fashionism_umkm.R
import com.alcorp.fashionism_umkm.ViewModelFactory
import com.alcorp.fashionism_umkm.data.remote.response.ProfileData
import com.alcorp.fashionism_umkm.databinding.FragmentProfileBinding
import com.alcorp.fashionism_umkm.ui.auth.login.LoginActivity
import com.alcorp.fashionism_umkm.ui.profile.change_password.ChangePasswordActivity
import com.alcorp.fashionism_umkm.ui.profile.edit_profile.EditProfileActivity
import com.alcorp.fashionism_umkm.ui.profile.edit_profile.EditProfileActivity.Companion.EXTRA_EDIT_PROFILE
import com.alcorp.fashionism_umkm.utils.Helper.showToast
import com.alcorp.fashionism_umkm.utils.LoadingDialog
import com.alcorp.fashionism_umkm.utils.PrefData
import com.alcorp.fashionism_umkm.utils.Status
import com.bumptech.glide.Glide

class ProfileFragment : Fragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var pref: SharedPreferences
    private lateinit var prefEdit: SharedPreferences.Editor
    private lateinit var profileData: ProfileData
    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setupView()
        loadData()
    }

    private fun setupView() {
        pref = requireActivity().getSharedPreferences("fashionism_umkm", AppCompatActivity.MODE_PRIVATE)
        PrefData.token = "Bearer ${pref.getString("access_token", "").toString()}"
        PrefData.idUser = pref.getString("id", "").toString()

        loadingDialog = LoadingDialog(requireContext())

        binding.btnUpdateProfile.setOnClickListener(this)
        binding.btnChangePassword.setOnClickListener(this)
        binding.btnHelpCenter.setOnClickListener(this)
        binding.btnLogout.setOnClickListener(this)
        binding.refreshProfile.setOnRefreshListener(this)
    }

    private fun loadData() {
        profileViewModel.getProfile(PrefData.token, PrefData.idUser)
        profileViewModel.profileState.observe(requireActivity()) {
            when (it.status) {
                Status.LOADING -> loadingDialog.showLoading(true)

                Status.SUCCESS -> {
                    loadingDialog.showLoading(false)
                    it.data?.let { profile ->
                        checkIfFragmentAttached {
                            profileData = ProfileData(profile.data?.id, profile.data?.name, profile.data?.email, profile.data?.phone, profile.data?.address, profile.data?.avatar)
                            Glide.with(requireContext())
                                .load(profile.data?.avatar)
                                .error(ContextCompat.getDrawable(requireContext(), R.drawable.default_image))
                                .placeholder(ContextCompat.getDrawable(requireContext(), R.drawable.default_image))
                                .into(binding.ivProfile)

                            binding.tvShopName.text = profile.data?.name
                            binding.tvEmail.text = profile.data?.email
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

    override fun onClick(view: View) {
        when (view) {
            binding.btnUpdateProfile -> {
                val intent = Intent(requireContext(), EditProfileActivity::class.java)
                intent.putExtra(EXTRA_EDIT_PROFILE, profileData)
                startActivityForResult(intent, EDIT_PROFILE_RESULT)
            }
            binding.btnChangePassword -> {
                val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
                startActivity(intent)
            }
            binding.btnHelpCenter -> {
                showToast(requireContext(), resources.getString(R.string.toast_unreleased_feature))
            }
            binding.btnLogout -> {
                prefEdit = pref.edit()
                prefEdit.clear().apply()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    private fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

    override fun onRefresh() {
        loadData()
        binding.refreshProfile.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == EDIT_PROFILE_RESULT) {
            loadData()
        }
    }

    companion object {
        const val EDIT_PROFILE_RESULT = 3
    }
}