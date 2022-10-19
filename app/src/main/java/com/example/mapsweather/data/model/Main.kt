package com.example.mapsweather.data.model

data class Main (
    var temp: Double,
    var feels_like: Double,
    var temp_min: Double,
    var temp_max: Double,
    var pressure: Double,
    var humidity: Int,
    var sea_level: Int,
    var grnd_level: Int
    )