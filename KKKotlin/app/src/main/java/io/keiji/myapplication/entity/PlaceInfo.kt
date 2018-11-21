package io.keiji.myapplication.entity

import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng

/**
 * Created by z00s600051 on 2018/11/13.
 */
class PlaceInfo{
    var name:String
    var address:String
    var attribution: String
    var latLng: LatLng

    constructor(name:String,
                address:String,
                attribution: String,
                latLng: LatLng){
        this.name = name
        this.address = address
        this.attribution = attribution
        this.latLng = latLng
    }

    constructor(place: Place){
        this.name = place.name as String
        this.address = place.address as String
        this.attribution = if (place.attributions == null) {
            ""
        } else {
            place.attributions as String
        }
        this.latLng = place.latLng
    }
}