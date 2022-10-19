package com.example.mapsweather

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mapsweather.viewmodel.WeatherViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import java.util.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMapClickListener {
    private lateinit var map: GoogleMap
    private lateinit var model: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()

        model = ViewModelProvider(this)[WeatherViewModel::class.java]

        model.getWeather().observe(this) {response ->
            println(Gson().toJson(response))
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
        val marker = MarkerOptions()
            .position(coordinate)
            .title("My location")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinate, 10f),
            4000,
            null
        )
    }

    override fun onMapClick(lat: LatLng) {
        val marker = MarkerOptions()
            .position(lat)
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(lat, 10f),
            4000,
            null
        )

        //val geocoder = Geocoder(this, Locale.getDefault())
        /*val addresses: List<Address> =
            geocoder.getFromLocation(marker.position.latitude, marker.position.longitude, 1)*/

        model.getWeatherByLocation(marker.position.latitude, marker.position.longitude, "29506a12ba108d822f46afefe72ccccc")

        //val address: List<String> = addresses[0].getAddressLine(0).split(",")
        /*println("----->")
        println(address)
        println(addresses)
        println(addresses[0].locality)
        println(addresses[0].adminArea)
        println(addresses[0].countryName)*/
    }

}