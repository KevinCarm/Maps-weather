package com.example.mapsweather.data.api

import com.example.mapsweather.data.model.WeatherMain
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
     suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") key: String
    ): Response<WeatherMain>
}

