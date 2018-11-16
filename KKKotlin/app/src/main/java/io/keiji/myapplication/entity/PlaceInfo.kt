package io.keiji.myapplication.entity

import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng

/**
 * Created by z00s600051 on 2018/11/13.
 */
class PlaceInfo(
        name:String,
        address:String,
        attribution: String,
        latLng: LatLng){
    val name:String = name
    val address:String = address
    val attribution:String = attribution
    val latLng:LatLng = latLng
    constructor(place: Place):PlaceInfo(name, address, attribution, latLng){

    }
}