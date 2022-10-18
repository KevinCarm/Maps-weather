package com.example.mapsweather

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMapClickListener {
    private lateinit var map: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()
    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

    }

    override fun onMapClick(p0: LatLng) {
        val marker = MarkerOptions()
            .position(lat)
        map.addMarker(marker)

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> =
            geocoder.getFromLocation(marker.position.latitude, marker.position.longitude, 1)


        val address: List<String> = addresses[0].getAddressLine(0).split(",")
        /*println("----->")
        println(address)
        println(addresses)
        println(addresses[0].locality)
        println(addresses[0].adminArea)
        println(addresses[0].countryName)*/
    }

}