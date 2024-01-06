package com.foodfairy.deliveryboy.Location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*

class LocationUtil(val context : Context, val userLocation: UserLocation) {

    @SuppressLint("MissingPermission")
    fun getLastLocation(){

        lateinit var mFusedLocationClient: FusedLocationProviderClient

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        mFusedLocationClient.lastLocation.addOnCompleteListener{ task ->

            val location: Location? = task.result
            if (location == null) {

                requestNewLocationData()

            } else {

                userLocation.userLatLong(location.latitude.toString(),location.longitude.toString())
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        lateinit var mFusedLocationClient: FusedLocationProviderClient

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            val mLastLocation: Location = locationResult.lastLocation

            userLocation.userLatLong(mLastLocation.latitude.toString(),mLastLocation.longitude.toString())
        }
    }

    interface UserLocation{
        fun userLatLong(lat :String,long:String)
    }

}