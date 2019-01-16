package io.keiji.myapplication.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.keiji.myapplication.R
import io.keiji.myapplication.entity.PlaceInfo
import io.keiji.myapplication.event.StartAlertEvent
import io.keiji.myapplication.listener.MapListener
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 1
private const val DEFAULT_ZOOM: Float = 15f
private const val PLACE_PICKER_REQUEST: Int = 1

/**
 * Created by z00s600051 on 2018/12/05.
 */
class GMapFragment: Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var mGeoDataClient: GeoDataClient
    private lateinit var mPlaceDetectionClient: PlaceDetectionClient
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mapListener: MapListener

    private var currentLatLng: LatLng = LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        mapListener = MapListener()
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater?.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.requestPermission()
    }

    /**
     * 位置情報取得権限を確認し、あればマップを準備、なければ位置情報取得権限のリクエストを行う.
     * 位置情報取得権限がつかない限りループする.
     */
    private fun requestPermission(){
        // 位置情報取得権限の確認、ある場合はマップ準備、ない場合はリクエスト
        val isLocationPermissionGranted = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if(isLocationPermissionGranted) {
            initMap()
        } else {
            Toast.makeText(activity, "アプリ実行には位置情報権限が必要です.", Toast.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /**
     * LocationPermission RequestResult
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                this.requestPermission()
            }
        }
    }

    /**
     * Mapを準備する
     */
    @SuppressLint("MissingPermission")
    private fun initMap(){
//        (activity as MainActivity).startService()

        // GoogleMapAPIClientの初期化
        mGeoDataClient = Places.getGeoDataClient(activity, null)
        mPlaceDetectionClient = Places.getPlaceDetectionClient(activity, null)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)

        // GoogleMapの初期化
        val mapFragment = getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }


    /**
     * GoogleMap onMapReady
     */
    override fun onMapReady(googleMap: GoogleMap) {
        // Mapを初期化
        mMap = googleMap
        googleMap.setOnMarkerClickListener(mapListener)
        googleMap.setOnMapClickListener(mapListener)

        // 現在値表示ボタンを追加する
        setCurrentButton(true)

        // 現在値を取得し、マップをそこに持っていく
        initLocationToCurrent()
    }


    /**
     * 現在値ボタンを画面に設定する。
     */
    private fun setCurrentButton(isEnabled: Boolean) {
        try {
            mMap.isMyLocationEnabled = isEnabled
            mMap.uiSettings.isMyLocationButtonEnabled = isEnabled
        } catch (e: SecurityException) {
            Timber.e("Exception: %s", e.message)
        }
    }

    /**
     * 現在値を取得し、マップをそこに動かす
     */
    private fun initLocationToCurrent() {
        try {
            val locationResult = mFusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    val mLastKnownLocation = task.result
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            LatLng(mLastKnownLocation.latitude, mLastKnownLocation.longitude), DEFAULT_ZOOM))
                } else {
                    Timber.d("Current location is null. Using defaults.")
                    Timber.e("Exception: %s", task.exception)
                    mMap.uiSettings.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    /**
     * PlacePickerを表示する
     */
    fun pickPlaceInfo(){
        val builder = PlacePicker.IntentBuilder()
        builder.setLatLngBounds(LatLngBounds(LatLng(currentLatLng.latitude, currentLatLng.longitude)
                , LatLng(currentLatLng.latitude, currentLatLng.longitude + 1)))
        startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST)
    }

    /**
     * ActivityResult受け取り処理
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        /**
         * PlacePickerで取得した情報をPlaceInfoとして保持する
         */
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK){
            val place = PlacePicker.getPlace(activity, data)
            addMarker(PlaceInfo(place))
        }
    }

    /**
     * 現在地付近のプレイスを取得する
     */
    @SuppressLint("MissingPermission")
    fun getAroundCurrentPlaces() {
        val placeResult = mPlaceDetectionClient.getCurrentPlace(null)
        placeResult.addOnCompleteListener(object : OnCompleteListener<PlaceLikelihoodBufferResponse> {
            override fun onComplete(task: Task<PlaceLikelihoodBufferResponse>) {
                if (!task.isSuccessful || task.result == null) {
                    Timber.e("Exception: %s", task.exception)
                }

                val likelyPlaces = task.result

                val maxEntries = 10
                val count: Int = if (likelyPlaces.count < maxEntries) {
                    likelyPlaces.count
                } else {
                    maxEntries
                }

                val placeInfoList: MutableList<PlaceInfo> = mutableListOf()

                for ((index, placeLikelihood) in likelyPlaces.withIndex()) {
                    val name = placeLikelihood.place.name as String
                    val address = placeLikelihood.place.address as String
                    val attribution = if (placeLikelihood.place.attributions == null) {
                        ""
                    } else {
                        placeLikelihood.place.attributions as String
                    }
                    val latLng = placeLikelihood.place.latLng

                    placeInfoList.add(PlaceInfo(name, address, attribution, latLng))

                    if (index >= (count - 1)) {
                        break
                    }
                }

                // Release the place likelihood buffer, to avoid memory leaks.
                likelyPlaces.release()

                openPlacesDialog(placeInfoList)
            }
        })
    }

    /**
     * 取得されたプレイスをダイアログで表示
     */
    private fun openPlacesDialog(placeInfoList: List<PlaceInfo>) {
        // ダイアログクリック時イベント登録
        // クリックされたPlaceInfoでマーカーを打つ
        val listener = DialogInterface.OnClickListener { _, which ->
            addMarker(placeInfoList[which])
        }

        // ダイアログ表示用のPlaceNameリストを生成
        val places = placeInfoList.map({v -> v.name}).toTypedArray()

        // プレイス選択ダイアログを表示する
        AlertDialog.Builder(activity)
                .setTitle(R.string.pick_place)
                .setItems(places, listener)
                .show()
    }

    /**
     * 渡されたPlaceInfoを元にMapにMarkerを打つ
     */
    private fun addMarker(placeInfo: PlaceInfo){
        // Attributionの有無をチェックしてスニペットを作成
        val markerSnippet = if (placeInfo.attribution.isNullOrEmpty()) {
            placeInfo.address
        } else {
            placeInfo.address + "\n" + placeInfo.attribution
        }

        // マーカーを生成してカメラを移動
        mMap.addMarker(MarkerOptions()
                .title(placeInfo.name)
                .position(placeInfo.latLng)
                .snippet(markerSnippet))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeInfo.latLng, DEFAULT_ZOOM))
    }

    fun updateCurrentLatLng(currentLatLng: LatLng){
        // 距離が一定以内に入ったらお知らせイベント
        if(this.mapListener.distanceLogic.isNotify(currentLatLng)){
            EventBus.getDefault().post(StartAlertEvent())
        }

        this.currentLatLng = currentLatLng

        this.mapListener.setTargetLocation(currentLatLng)
    }
}
