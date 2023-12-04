package com.cmpt362team21.ui.bankLocator

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cmpt362team21.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.lang.NullPointerException

class BankAtmLocator : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private val PERMISSION_REQUEST_CODE = 0
    private lateinit var locationManager: LocationManager
    private lateinit var markerOptions: MarkerOptions
    private var mapCentered = false
    private lateinit var centerToUserLocationBtn:ImageView
    private lateinit var placePickerBtn:ImageView
    private lateinit var placesClient: PlacesClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_atm_locator)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

        if(!Places.isInitialized()){
            Places.initialize(this,"AIzaSyBGIwb4c3NXUNz87rrrVPPaf3IJXDnYXBk")
        }

        //https://developers.google.com/maps/documentation/places/android-sdk/autocomplete
        val autoCompleteSupportFragment = (supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment)
            .setPlaceFields(listOf(Place.Field.LAT_LNG,Place.Field.NAME,Place.Field.ADDRESS
            ,Place.Field.PHONE_NUMBER,Place.Field.RATING,Place.Field.WEBSITE_URI,
                Place.Field.ID))

        autoCompleteSupportFragment.setCountries("CA", "US")

        autoCompleteSupportFragment.setOnPlaceSelectedListener(object:PlaceSelectionListener {
            override fun onError(status: Status) {
                Log.e("Error",status.statusMessage.toString())
            }

            override fun onPlaceSelected(place: Place) {
                hideKeyboard()
                val cameraUpdate = place.latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 17f) }
                if (cameraUpdate != null) {
                    mMap.animateCamera(cameraUpdate)
                }
                storePopUpInfo(place)
            }
        })

        centerToUserLocationBtn = findViewById(R.id.centerBtn)
        centerToUserLocationBtn.setOnClickListener{
            Toast.makeText(this,"Centering to User's Current Location",Toast.LENGTH_SHORT).show()
            try {
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null){
                    val latLng = LatLng(location.latitude,location.longitude)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                    mMap.animateCamera(cameraUpdate)
                }
            } catch (e:SecurityException){
                e.printStackTrace()
            }
        }
        placesClient = Places.createClient(this)
        placePickerBtn = findViewById(R.id.mapPicker)
        placePickerBtn.setOnClickListener{
            Toast.makeText(this,"Finding Nearby Places at User's Location",Toast.LENGTH_SHORT).show()
            requestNearbyPlaces()
        }

    }

    //https://developers.google.com/maps/documentation/places/android-sdk/current-place
    private fun requestNearbyPlaces() {
        try {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {

                val placeFields = listOf(
                    Place.Field.LAT_LNG,
                    Place.Field.NAME,
                    Place.Field.ID,
                    Place.Field.ADDRESS,
                    Place.Field.RATING,
                )

                val request = com.google.android.libraries.places.api.net.FindCurrentPlaceRequest.builder(placeFields).build()

                placesClient.findCurrentPlace(request)
                    .addOnSuccessListener { response ->
                        val places = response.placeLikelihoods
                        for (likelihood in places) {
                            val place = likelihood.place
                            val latLng = place.latLng
                            if (latLng != null) {
                                markerOptions.position(latLng).title(place.name)
                                storePopUpInfo(place)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Places API", "Error getting nearby places: ${exception.message}")
                    }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun storePopUpInfo(place:Place){
        mMap.setInfoWindowAdapter(InfoPopUpAdapter(this))
        if(place != null){
            try{
                val popUpBody = "Address: " + place.address + "\n" +
                        "Rating: " + place.rating + "\n" +
                        "Phone Number: " + place.phoneNumber + "\n" +
                        "Website: " + place.websiteUri + "\n"

                place.latLng?.let { markerOptions.title(place.name).snippet(popUpBody).position(it) }
                mMap.addMarker(markerOptions)
            }catch (e:NullPointerException){
                e.printStackTrace()
            }
        }
    }
    private fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null){
                onLocationChanged(location)
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        } catch (e:SecurityException){
            e.printStackTrace()
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        markerOptions = MarkerOptions()
        checkPermission()

    }
    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)

        if (!mapCentered) {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            mMap.animateCamera(cameraUpdate)
            mapCentered = true
        }
    }
    private fun hideKeyboard(){
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    //taken from professor xd map demo code
    private fun checkPermission(){
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        else
            initLocationManager()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) initLocationManager()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null)
            locationManager.removeUpdates(this)
    }
}