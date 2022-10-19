package com.example.mapsweather.data.model

data class Sys(
    var type: Int,
    var id: Int,
    var country: String,
    var sunrise: Long,
    var sunset: Long
)
