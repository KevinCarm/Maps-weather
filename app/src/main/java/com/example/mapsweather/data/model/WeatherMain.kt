package com.example.mapsweather.data.model


data class WeatherMain(
    var coord: Coord,
    var weather: List<Weather>,
    var base: String,
    var main: Main,
    var visibility: Int,
    var wind: Wind,
    var rain: Rain,
    var clouds: Clouds,
    var sys: Sys,
    var timezone: Int,
    var name: String,
    var cod: Int,
    )
