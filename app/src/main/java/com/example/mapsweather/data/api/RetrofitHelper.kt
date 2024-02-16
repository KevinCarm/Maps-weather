package com.example.mapsweather.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val baseUrl = "https://api.openweathermap.org/"
    private var retrofit: Retrofit? = null
    fun getInstance(): Retrofit {
        return if(retrofit != null)
            retrofit!!
        else {
            val obj = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit = obj
            retrofit!!
        }
    }
}