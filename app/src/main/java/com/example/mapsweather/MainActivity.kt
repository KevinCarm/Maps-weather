package com.example.mapsweather

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
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

    private fun createDialog(weather: WeatherMain) {
        val inflate = layoutInflater
        val inflate_view = inflate.inflate(R.layout.show_weather, null)
        val cityText: TextView = inflate_view.findViewById(R.id.city)
        val stateText: TextView = inflate_view.findViewById(R.id.state)
        val countryText: TextView = inflate_view.findViewById(R.id.country)
        val temperature: TextView = inflate_view.findViewById(R.id.temperature)

        cityText.text = city
        stateText.text = state
        countryText.text = country
        temperature.text = "%.2f".format((weather.main.temp - kelvin)).toDouble().toString().plus("°C")


        MaterialAlertDialogBuilder(this)
            .setView(inflate_view)
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