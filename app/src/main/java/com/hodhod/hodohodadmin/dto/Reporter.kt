package com.hodhod.hodohodadmin.dto

import com.google.android.gms.maps.model.LatLng


data class Reporter(var name: String = "Reporter", var lat: Double = 0.0, var lng: Double = 0.0, val speciality: String = "") {

    fun getLocation(): LatLng = LatLng(lat, lng)

}