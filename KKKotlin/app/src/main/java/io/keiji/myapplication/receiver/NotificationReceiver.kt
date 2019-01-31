package io.keiji.myapplication.receiver

import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import com.google.android.gms.maps.model.LatLng
import io.keiji.myapplication.event.MapCallEvent
import io.keiji.myapplication.event.StartAlertEvent
import io.keiji.myapplication.event.StopAlertEvent
import org.greenrobot.eventbus.EventBus


/**
 * Created by z00s600051 on 2019/01/16.
 */
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action
        val latLng = intent.extras.get("latLng") as LatLng

        when (action) {
            DELETE_NOTIFICATION -> {
                //通知削除時のイベントを書く
                Toast.makeText(context, "通知が削除されました", Toast.LENGTH_LONG).show()
                EventBus.getDefault().post(MapCallEvent(MapCallEvent.MapCallEnum.StopAlert, latLng))
            }
            else -> {
            }
        }
    }

    companion object {
        val DELETE_NOTIFICATION = "delete_notification"
    }
}