package io.keiji.myapplication.logic

import android.location.Location
import com.google.android.gms.maps.model.LatLng

/**
 * Created by z00s600051 on 2018/11/28.
 */
class DistanceLogic{
    var notifyDistance: Double
    var targetLocation: LatLng

    init{
        this.notifyDistance = 100.0
        this.targetLocation = LatLng(0.0, 0.0)
    }

    fun isNotify(location: Location): Boolean{
        val array = FloatArray(3)
        Location.distanceBetween(targetLocation.latitude, targetLocation.longitude, location.latitude, location.longitude,  array)
        val currentDistance = array[0]

        return currentDistance < notifyDistance
    }
}