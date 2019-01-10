package io.keiji.myapplication.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.widget.Toast
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.Vibrator
import com.google.android.gms.maps.model.LatLng
import io.keiji.myapplication.R
import io.keiji.myapplication.event.*
import io.keiji.myapplication.fragment.GMapFragment
import io.keiji.myapplication.fragment.OptionFragment
import io.keiji.myapplication.service.LocationService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber

/**
 * Map表示のActivity
 */
class MainActivity : AppCompatActivity(){

    /**
     * Toastを表示する用受け口
     */
    @Subscribe
    fun toastEvent(event: ToastEvent){
        Timber.i("hoge : " + event.text)
        Toast.makeText(this, event.text, Toast.LENGTH_LONG).show()
    }

    /**
     * 現在値を更新する用受け口
     */
    @Subscribe
    fun updateLocationEvent(event: NotifyLocationEvent){
        supportFragmentManager.findFragmentById(R.id.mapContainer)?.let{
            (it as GMapFragment).updateCurrentLatLng(LatLng(event.location.latitude, event.location.longitude))
        }
    }

    /**
     * アラート開始受け口
     */
    @Subscribe
    fun startAlertEvent(event: StartAlertEvent){
//        locationManager.removeUpdates(appListener)
        Timber.d("startAlert")
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

    /**
     * PlacePicker開始口
     */
    @Subscribe
    fun pickPlaceEvent(event: MapCallEvent){
        supportFragmentManager.findFragmentById(R.id.mapContainer)?.let{
            when(event.callEnum){
                MapCallEvent.MapCallEnum.GetAroundCurrent -> {
                    (it as GMapFragment).getAroundCurrentPlaces()
                }
                MapCallEvent.MapCallEnum.PickPlace -> {
                    (it as GMapFragment).pickPlaceInfo()
                }
            }
        }
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

        // Fragmentを設定
        if(savedInstanceState == null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.optionContainer, OptionFragment())
            transaction.add(R.id.mapContainer, GMapFragment())

            transaction.commit()
        }

        // LocationServiceを開始
        startService()
    }

    /**
     * Activity onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()

        // アプリケーション全体のイベント停止
        EventBus.getDefault().unregister(this)

        // LocationServiceを停止
        stopService()
    }

    private fun startService(){
        Timber.i("hoge:Start Service.")
        val intent = Intent(this, LocationService::class.java)
        startForegroundService(intent)
    }

    private fun stopService(){
        Timber.i("hoge:Stop Service")
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
    }

}
