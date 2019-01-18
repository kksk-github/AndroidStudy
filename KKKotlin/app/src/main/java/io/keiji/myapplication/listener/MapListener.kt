package io.keiji.myapplication.listener

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.keiji.myapplication.event.ToastEvent
import io.keiji.myapplication.logic.DistanceLogic
import org.greenrobot.eventbus.EventBus

/**
 * Created by z00s600051 on 2018/11/26.
 */
class MapListener:GoogleMap.OnMarkerClickListener , GoogleMap.OnMapClickListener{

    private var distanceLogic: DistanceLogic

    init{
        distanceLogic = DistanceLogic()
    }

    /**
     * Map上のマーカをクリックした時の通知
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        // 目的地の更新
        this.distanceLogic.setTargetLocation(marker.position)

        EventBus.getDefault().post(ToastEvent("Target set to ${marker.title}"))
        return false
    }

    /**
     * Map上をクリックした時の通知
     */
    override fun onMapClick(p0: LatLng?) {
//    mMap.addMarker(MarkerOptions().position(p0!!).title("New Marker"))
    }

    /**
     * 現在地からの距離をチェック
     */
    fun isNotify(latLng: LatLng): Boolean{
        return this.distanceLogic.isNotify(latLng)
    }
}