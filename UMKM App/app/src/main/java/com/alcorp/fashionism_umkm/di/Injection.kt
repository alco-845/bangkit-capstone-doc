package com.alcorp.fashionism_umkm.di

import android.content.Context
import com.alcorp.fashionism_umkm.data.AppRepository
import com.alcorp.fashionism_umkm.data.remote.retrofit.ApiConfig


object Injection {
    fun provideRepository(context: Context): AppRepository {
        val apiService = ApiConfig.getApiService()
        return AppRepository.getInstance(apiService)
    }
}