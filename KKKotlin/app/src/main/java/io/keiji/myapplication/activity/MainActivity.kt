package io.keiji.myapplication.activity

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import android.widget.Toast
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import timber.log.Timber
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.keiji.myapplication.R
import io.keiji.myapplication.entity.PlaceInfo
import io.keiji.myapplication.event.NotifyLocationEvent
import io.keiji.myapplication.event.ToastEvent
import io.keiji.myapplication.event.StartAlertEvent
import io.keiji.myapplication.event.StopAlertEvent
import io.keiji.myapplication.fragment.GMapFragment
import io.keiji.myapplication.fragment.OptionFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Map表示のActivity
 */
class MainActivity : AppCompatActivity(){

    /**
     * Toastを表示する用受け口
     */
    @Subscribe
    fun toastEvent(event: ToastEvent){
        Toast.makeText(this, event.text, Toast.LENGTH_LONG).show()
    }

    /**
     * 現在値を更新する用受け口
     */
    @Subscribe
    fun updateLocationEvent(event: NotifyLocationEvent){
//        this.currentLatLng = LatLng(event.location.latitude, event.location.longitude)
    }

    /**
     * アラート開始受け口
     */
    @Subscribe
    fun startAlertEvent(event: StartAlertEvent){
//        locationManager.removeUpdates(appListener)
        ringtone.play()
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))
        startActivity(Intent(this, SampleActivity::class.java))
    }

    /**
     * アラート停止受け口
     */
    @SuppressLint("MissingPermission")
    @Subscribe
    fun stopAlertEvent(event: StopAlertEvent){
        ringtone.stop()
        vibrator.cancel()
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME, MIN_DISTANCE, appListener)
    }

    private lateinit var ringtone: Ringtone
    private lateinit var vibrator: Vibrator
    private lateinit var pattern: LongArray

    /**
     * Activity onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // アプリケーション全体のイベント管理
        EventBus.getDefault().register(this)

        // 着信音初期化
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(this, uri)

        // バイブレーション初期化
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        pattern = longArrayOf(1000, 500, 1000)

        if(savedInstanceState == null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.optionContainer, OptionFragment())
            transaction.add(R.id.mapContainer, GMapFragment())

            transaction.commit()
        }
    }

    /**
     * Activity onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()

        // アプリケーション全体のイベント停止
        EventBus.getDefault().unregister(this)
    }
}
