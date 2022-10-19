package com.example.mapsweather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsweather.data.api.RetrofitHelper
import com.example.mapsweather.data.api.WeatherApi
import com.example.mapsweather.data.model.WeatherMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel: ViewModel() {
    private val weatherApi = RetrofitHelper
        .getInstance()
        .create(WeatherApi::class.java)

    private val weather: MutableLiveData<WeatherMain> = MutableLiveData()

    fun getWeather(): LiveData<WeatherMain> = weather

    fun getWeatherByLocation(lat: Double, lon: Double, key: String) {
        viewModelScope.launch {
            val response = weatherApi.getWeather(lat, lon, key)
            if(response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    weather.postValue(response.body())
                }
            }
        }
    }
}