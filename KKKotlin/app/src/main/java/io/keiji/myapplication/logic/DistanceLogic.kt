package io.keiji.myapplication.logic

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import io.keiji.myapplication.event.ToastEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by z00s600051 on 2018/11/28.
 */
class DistanceLogic{
    private var notifyDistance: Double
    private var targetLocation: LatLng

    init{
        this.notifyDistance = 500.0
        this.targetLocation = LatLng(0.0, 0.0)
    }

    fun setTargetLocation(location: LatLng){
        this.targetLocation = location
    }

    fun isNotify(latLng: LatLng): Boolean{
        val array = FloatArray(3)
        Location.distanceBetween(targetLocation.latitude, targetLocation.longitude, latLng.latitude, latLng.longitude,  array)
        val currentDistance = array[0]
//        EventBus.getDefault().post(ToastEvent("目標までの距離：" + currentDistance.toString() + "m"))

        return currentDistance < notifyDistance
    }
}