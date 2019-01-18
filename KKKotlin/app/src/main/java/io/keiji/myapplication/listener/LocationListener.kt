package io.keiji.myapplication.listener

import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import io.keiji.myapplication.event.NotifyLocationEvent
import io.keiji.myapplication.event.ToastEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by z00s600051 on 2018/11/26.
 */
class LocationListener : LocationListener{
    override fun onProviderDisabled(p0: String?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    /**
     * 位置情報が変わった時の通知
     */
    override fun onLocationChanged(location: Location) {
        EventBus.getDefault().post(NotifyLocationEvent(location))
    }
}