package com.nikhilhere.test.consumer.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLocation(
    val primaryName: String = "",
    val addressLines: String = "",
    val state: String = "",
    val district: String = "",
    val locality: String = "",
    val postalCode: String = "",
    val countryCode: String = "",
    val countryName: String = "",
    val latitude: Double = 0.00,
    val longitude: Double = 0.00,
    var houseFlatBlockNo: String = "",
    var landMark: String = ""
) : Parcelable{

}
