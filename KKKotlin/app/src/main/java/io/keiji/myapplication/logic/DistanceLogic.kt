package io.keiji.myapplication.logic

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import io.keiji.myapplication.entity.ParcelableMarkerInfo

private const val NOTIFY_DISTANCE: Double = 500.0 // メートル

/**
 * Created by z00s600051 on 2018/11/28.
 */
class DistanceLogic{
    fun isNotify(current: LatLng, marker: ParcelableMarkerInfo): Boolean{
        if(!marker.flgTarget) { return false }

        val array = FloatArray(3)
        Location.distanceBetween(marker.latitude!!, marker.longitude!!, current.latitude, current.longitude,  array)
        val currentDistance = array[0]

        return currentDistance < NOTIFY_DISTANCE
    }
}