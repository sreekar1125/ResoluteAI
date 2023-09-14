package com.example.resoluteai

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var nameTv:TextView
    private lateinit var callBtn: Button
    private var name: String = ""


    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 1

    private lateinit var mgoogleMap : GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        name = intent.getStringExtra("name").toString()
        nameTv = findViewById(R.id.nameTv)
        callBtn = findViewById(R.id.callBtn)
        nameTv.setText("Welcome " + name)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        callBtn.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java).putExtra("name", name)
            startActivity(intent)
        }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mgoogleMap = googleMap
        mgoogleMap.uiSettings.isZoomControlsEnabled = true

        setUpMap()

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }
        mgoogleMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {location ->

            if(location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLng: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLng)
        markerOptions.title("$currentLatLng")
        mgoogleMap.addMarker(markerOptions)
    }

}