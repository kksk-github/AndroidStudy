package io.keiji.myapplication.activity

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.widget.Toast
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import io.keiji.myapplication.R
import io.keiji.myapplication.event.*
import io.keiji.myapplication.fragment.GMapFragment
import io.keiji.myapplication.fragment.OptionFragment
import io.keiji.myapplication.receiver.NotificationReceiver
import io.keiji.myapplication.receiver.NotificationReceiver.Companion.DELETE_NOTIFICATION
import io.keiji.myapplication.service.LocationService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber

/**
 * Map表示のActivity
 */
class MainActivity : AppCompatActivity(){
    private lateinit var ringtone: Ringtone
    private lateinit var vibrator: Vibrator
    private lateinit var pattern: LongArray
    private lateinit var notification: NotificationCompat.Builder

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
        Timber.d("startAlert")

        // アラート開始
        ringtone.play()
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0))

        // 通知削除時Intent定義
        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        intent.action = DELETE_NOTIFICATION
        intent.putExtra("latLng", event.latLng)
        notification.setDeleteIntent(PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))

        // 通知を送信
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(123, notification.build())

    }

    /**
     * アラート停止受け口
     */
    @SuppressLint("MissingPermission")
    @Subscribe
    fun stopAlertEvent(event: StopAlertEvent){
        ringtone.stop()
        vibrator.cancel()
    }

    /**
     * PlacePicker or getAroundCurrentPlace開始口
     */
    @Subscribe
    fun mapCallEvent(event: MapCallEvent){
        supportFragmentManager.findFragmentById(R.id.mapContainer)?.let{
            when(event.callEnum){
                MapCallEvent.MapCallEnum.GetAroundCurrent -> {
                    (it as GMapFragment).getAroundCurrentPlaces()
                }
                MapCallEvent.MapCallEnum.PickPlace -> {
                    (it as GMapFragment).pickPlaceInfo()
                }
                MapCallEvent.MapCallEnum.DeleteMarker -> {
                    (it as GMapFragment).deleteMarker(event.latLng!!)
                }
                MapCallEvent.MapCallEnum.StartMarkerSetting -> {
                    (it as GMapFragment).startMarkerSetting()
                }
                MapCallEvent.MapCallEnum.SetTarget -> {
                    (it as GMapFragment).setTarget(event.latLng!!)
                }
                MapCallEvent.MapCallEnum.StopAlert -> {
                    (it as GMapFragment).stopAlert(event.latLng!!)
                    ringtone.stop()
                    vibrator.cancel()
                }
            }
        }
    }

    @Subscribe
    fun startSettingEvent(event: StartSettingEvent){

    }
    
    

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

        // Alert用Notification初期化
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = "寝過ごし防止アラーム"
        val id = "alert_notification"
        val notifyDescription = "通知を削除して停止してください。"

        // NotificationChannel設定
        if (manager.getNotificationChannel(id) == null) {
            val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.apply {
                description = notifyDescription
            }
            manager.createNotificationChannel(mChannel)
        }

        // 通知に値を設定
        notification = NotificationCompat.Builder(this, id).apply {
            mContentTitle = name
            mContentText = notifyDescription
            setSmallIcon(R.mipmap.ic_launcher)
        }

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

        // アラートを念の為停止
        ringtone.stop()
        vibrator.cancel()
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
