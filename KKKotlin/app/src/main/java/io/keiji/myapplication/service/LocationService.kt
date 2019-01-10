package io.keiji.myapplication.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.LocationManager
import android.os.IBinder
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
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

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Timber.i("hoge:LocationService_onDestroy")
        locationManager.removeUpdates(locationListener)
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}