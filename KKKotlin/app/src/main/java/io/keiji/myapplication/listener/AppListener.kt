package io.keiji.myapplication.listener

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.keiji.myapplication.event.NotifyLocationEvent
import io.keiji.myapplication.event.ToastEvent
import io.keiji.myapplication.event.StartAlertEvent
import io.keiji.myapplication.logic.DistanceLogic
import org.greenrobot.eventbus.EventBus

/**
 * Created by z00s600051 on 2018/11/26.
 */
class AppListener:
        LocationListener
        , GoogleMap.OnMarkerClickListener
        , GoogleMap.OnMapClickListener{

    private var distanceLogic: DistanceLogic

    init{
        distanceLogic = DistanceLogic()
    }

    override fun onProviderDisabled(p0: String?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    /**
     * 位置情報が変わった時の通知
     */
    override fun onLocationChanged(location: Location) {
        EventBus.getDefault().post(ToastEvent("${location.latitude}, ${location.longitude}"))
        EventBus.getDefault().post(NotifyLocationEvent(location))
        // 距離が一定以内に入ったらお知らせイベント
        if(this.distanceLogic.isNotify(location)){
            EventBus.getDefault().post(StartAlertEvent())
        }
    }

    /**
     * Map上のマーカをクリックした時の通知
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        // 目的地の更新
        this.distanceLogic.targetLocation = marker.position

        EventBus.getDefault().post(ToastEvent("Target set to ${marker.title}"))
        return false
    }

    /**
     * Map上をクリックした時の通知
     */
    override fun onMapClick(p0: LatLng?) {
//    mMap.addMarker(MarkerOptions().position(p0!!).title("New Marker"))
    }
}