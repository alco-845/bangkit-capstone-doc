package com.fashionism.fashionismuserapp.di

import android.content.Context
import com.fashionism.fashionismuserapp.data.api.APIConfig
import com.fashionism.fashionismuserapp.data.viewmodel.MainRepository

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val apiService = APIConfig.getApiServiceV1()
        return MainRepository(apiService)
    }
}