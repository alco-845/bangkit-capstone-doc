package com.alcorp.fashionism_umkm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alcorp.fashionism_umkm.data.AppRepository
import com.alcorp.fashionism_umkm.di.Injection
import com.alcorp.fashionism_umkm.ui.auth.login.LoginViewModel
import com.alcorp.fashionism_umkm.ui.auth.register.RegisterViewModel
import com.alcorp.fashionism_umkm.ui.home.HomeViewModel
import com.alcorp.fashionism_umkm.ui.product.ProductViewModel
import com.alcorp.fashionism_umkm.ui.product.add_or_edit_product.AddEditProductViewModel
import com.alcorp.fashionism_umkm.ui.product.detail_product.DetailProductViewModel
import com.alcorp.fashionism_umkm.ui.profile.ProfileViewModel
import com.alcorp.fashionism_umkm.ui.profile.change_password.ChangePasswordViewModel
import com.alcorp.fashionism_umkm.ui.profile.edit_profile.EditProfileViewModel

class ViewModelFactory(private val repository: AppRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(AddEditProductViewModel::class.java)) {
            return AddEditProductViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(DetailProductViewModel::class.java)) {
            return DetailProductViewModel(repository) as T
        }
        if(modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(repository) as T
        }
        if(modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository) as T
        }
        if(modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(repository) as T
        }
        if(modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            return ChangePasswordViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.simpleName)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
        }
    }
}