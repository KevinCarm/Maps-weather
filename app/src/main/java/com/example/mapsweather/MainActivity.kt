package com.example.mapsweather

import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mapsweather.data.model.WeatherMain
import com.example.mapsweather.viewmodel.WeatherViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL
import java.util.Locale


class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMapClickListener {
    private lateinit var map: GoogleMap
    private lateinit var model: WeatherViewModel
    private var marker: MarkerOptions? = null
    private var city: String = ""
    private var state: String = ""
    private var country: String = ""
    private val kelvin: Double = 273.15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()

        model = ViewModelProvider(this)[WeatherViewModel::class.java]

        model.getWeather().observe(this) { response ->
            createDialog(response)
        }
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener(this)
        createMarker()
    }

    private fun createMarker() {
        val coordinate = LatLng(20.211009, -100.884494)
        marker = MarkerOptions()
            .position(coordinate)
            .title("My location")
        map.addMarker(marker!!)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinate, 10f),
            4000,
            null
        )
    }

    private fun formatTwoDecimals(value: Double): Double {
        val string = "%.2f"
        return string.format(value).toDouble()
    }

    private fun createDialog(weather: WeatherMain) {
        val inflate = layoutInflater
        val inflateView = inflate.inflate(R.layout.show_weather, null)
        val cityText: TextView = inflateView.findViewById(R.id.city)
        val temperature: TextView = inflateView.findViewById(R.id.temperature)
        val imageWeather: ImageView = inflateView.findViewById(R.id.imageWeather)
        val description: TextView = inflateView.findViewById(R.id.description)
        val humidity: TextView = inflateView.findViewById(R.id.humidity)
        val maximum: TextView = inflateView.findViewById(R.id.maximum)
        val minimum: TextView = inflateView.findViewById(R.id.minimum)

        cityText.text = city.plus(", ").plus(country)
        description.text = weather.weather[0].description
        temperature.text = formatTwoDecimals((weather.main.temp - kelvin)).toString().plus("°C")
        humidity.text = weather.main.humidity.toString().plus("%")
        maximum.text = formatTwoDecimals(weather.main.temp_max - kelvin).toString().plus("°C")
        minimum.text = formatTwoDecimals(weather.main.temp_min - kelvin).toString().plus("°C")


        try {
            val url = "https://openweathermap.org/img/wn/${weather.weather[0].icon}@4x.png"
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap =
                    BitmapFactory.decodeStream(URL(url).content as InputStream)
                withContext(Dispatchers.Main) {
                    imageWeather.setImageBitmap(bitmap)
                }
            }.invokeOnCompletion {
                CoroutineScope(Dispatchers.Main).launch {
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setView(inflateView)
                        .show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onMapClick(lat: LatLng) {
        map.clear()

        marker = MarkerOptions().position(lat)
        map.addMarker(marker!!)
       CoroutineScope(Dispatchers.Main).launch {
           map.animateCamera(
               CameraUpdateFactory.newLatLngZoom(lat, 10f),
               1000,
               null
           )
       }.invokeOnCompletion {
           val geocoder = Geocoder(this, Locale.getDefault())
           try {
               val addresses: List<Address> =
                   geocoder.getFromLocation(marker?.position!!.latitude, marker?.position!!.longitude, 1)
               model.getWeatherByLocation(
                   marker?.position!!.latitude,
                   marker?.position!!.longitude,
                   getString(R.string.api_key)
               )
               city = addresses[0].locality
               state = addresses[0].adminArea
               country = addresses[0].countryName
           } catch (e: Exception) {
               e.printStackTrace()
           }
       }
    }
}