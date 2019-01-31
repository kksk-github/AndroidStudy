package io.keiji.myapplication.listener

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.keiji.myapplication.event.MapCallEvent
import io.keiji.myapplication.event.ToastEvent
import io.keiji.myapplication.logic.DistanceLogic
import org.greenrobot.eventbus.EventBus

/**
 * Created by z00s600051 on 2018/11/26.
 */
class MapListener:
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener{
    /**
     * Map上のマーカをクリックした時の通知
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        // 目的地設定
        EventBus.getDefault().post(MapCallEvent(MapCallEvent.MapCallEnum.SetTarget, marker.position!!))

        EventBus.getDefault().post(ToastEvent("Target set to ${marker.title}"))
        return false
    }

    override fun onMapLongClick(latLng: LatLng?) {
        EventBus.getDefault().post(MapCallEvent(MapCallEvent.MapCallEnum.DeleteMarker, latLng))
    }

    /**
     * Map上をクリックした時の通知
     */
    override fun onMapClick(p0: LatLng?) {
//        EventBus.getDefault().post(MapCallEvent(MapCallEvent.MapCallEnum.DeleteMarker, p0!!))
    }
}