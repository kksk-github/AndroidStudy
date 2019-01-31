package io.keiji.myapplication.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import java.net.Inet4Address

/**
 * Created by z00s600051 on 2019/01/22.
 */
class ParcelableMarkerInfo: Parcelable{
    var name:String? = null
    var address:String? = null
    var attribution: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var latLng: LatLng? = null
    var flgTarget: Boolean = false


    constructor(name:String,
                address:String,
                attribution: String,
                latLng: LatLng){
        this.name = name
        this.address = address
        this.attribution = attribution
        this.latitude = latLng.latitude
        this.longitude = latLng.longitude
        this.latLng = LatLng(latLng.latitude, latLng.longitude)
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
        this.latitude = place.latLng.latitude
        this.longitude = place.latLng.longitude
    }

    constructor(parcel:Parcel){
        name = parcel.readString()
        address = parcel.readString()
        attribution = parcel.readString()
        latitude = parcel.readDouble()
        longitude = parcel.readDouble()
        latLng = LatLng(latitude!!, longitude!!)
        val flg = parcel.readInt()
        flgTarget = flg == 1
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeString(attribution)
        parcel.writeDouble(latitude!!)
        parcel.writeDouble(longitude!!)
        var flg = 0
        if(flgTarget) flg = 1
        parcel.writeInt(flg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableMarkerInfo> {
        override fun createFromParcel(parcel: Parcel): ParcelableMarkerInfo {
            return ParcelableMarkerInfo(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableMarkerInfo?> {
            return arrayOfNulls(size)
        }
    }

}