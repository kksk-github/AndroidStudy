package io.keiji.myapplication.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.IBinder
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import io.keiji.myapplication.R
import io.keiji.myapplication.activity.MainActivity
import io.keiji.myapplication.listener.LocationListener
import timber.log.Timber

private const val MIN_TIME: Long = 1000
private const val MIN_DISTANCE: Float = 10.0f

/**
 * Created by z00s600051 on 2019/01/07.
 */
class LocationService: Service() {
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager

    override fun onCreate(){
        Timber.i("hoge:LocationService_onCreate")
        // アプリケーション全体のリスナ初期化
        locationListener = LocationListener()

        // 現在値取得用Manager初期化
        locationManager = this.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        // GPS設定の確認、無効の場合は有効化を促してGPSから現在値を取得させる
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("hoge:LocationService_onStartCommand")

        // 現在地取得開始
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = "寝過ごし防止アラーム"
        val id = "location_foreground"
        val notifyDescription = "位置情報を取得しています。"

        if (manager.getNotificationChannel(id) == null) {
            val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            mChannel.apply {
                description = notifyDescription
            }
            manager.createNotificationChannel(mChannel)
        }

        val notification = NotificationCompat.Builder(this, id).apply {
            mContentTitle = name
            mContentText = notifyDescription
            setSmallIcon(R.mipmap.ic_launcher)
        }.build()

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        Timber.i("hoge:LocationService_onDestroy")
        locationManager.removeUpdates(locationListener)
        stopForeground(true)
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}