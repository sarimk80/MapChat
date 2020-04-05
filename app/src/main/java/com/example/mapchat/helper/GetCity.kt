package com.example.mapchat.helper

import android.content.Context
import android.location.Geocoder
import java.util.*

fun getCity(latitude: Double?, longitude: Double?, context: Context): String {

    val geoCoder: Geocoder = Geocoder(context, Locale.getDefault())

    val city: String
    city = try {
        val address = geoCoder.getFromLocation(latitude!!, longitude!!, 1)
        address[0].locality + ", " + address[0].countryName
    } catch (e: Throwable) {
        "Earth"
    }


    return city
}