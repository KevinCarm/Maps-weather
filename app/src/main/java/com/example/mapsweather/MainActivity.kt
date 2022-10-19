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
import java.util.*


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
        val stateText: TextView = inflateView.findViewById(R.id.state)
        val countryText: TextView = inflateView.findViewById(R.id.country)
        val temperature: TextView = inflateView.findViewById(R.id.temperature)
        val imageWeather: ImageView = inflateView.findViewById(R.id.imageWeather)

        cityText.text = city
        stateText.text = state
        countryText.text = country
        temperature.text = formatTwoDecimals((weather.main.temp - kelvin)).toString().plus("°C")


        try {
            val url = "https://openweathermap.org/img/wn/${weather.weather[0].icon}@4x.png"
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap =
                    BitmapFactory.decodeStream(URL(url).content as InputStream)
                withContext(Dispatchers.Main) {
                    imageWeather.setImageBitmap(bitmap)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        MaterialAlertDialogBuilder(this)
            .setView(inflateView)
            .setPositiveButton("Done") { _, _ -> }
            .show()
    }

    override fun onMapClick(lat: LatLng) {
        map.clear()

        marker = MarkerOptions().position(lat)
        map.addMarker(marker!!)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(lat, 10f),
            4000,
            null
        )

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> =
            geocoder.getFromLocation(marker?.position!!.latitude, marker?.position!!.longitude, 1)

        model.getWeatherByLocation(
            marker?.position!!.latitude,
            marker?.position!!.longitude,
            "29506a12ba108d822f46afefe72ccccc"
        )

        city = addresses[0].locality
        state = addresses[0].adminArea
        country = addresses[0].countryName
    }

}