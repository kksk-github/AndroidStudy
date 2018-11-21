package io.keiji.myapplication.activity

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.GoogleMap
import android.widget.Toast
import com.google.android.gms.maps.model.Marker
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.location.Location
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.keiji.myapplication.R
import io.keiji.myapplication.entity.PlaceInfo


private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 1
private const val DEFAULT_ZOOM: Float = 15f
private const val PLACE_PICKER_REQUEST: Int = 1

/**
 * Map表示のActivity
 */
class MainActivity : AppCompatActivity()
        , OnMapReadyCallback
        , GoogleMap.OnMarkerClickListener
        , GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mLastKnownLocation: Location
    private lateinit var mGeoDataClient: GeoDataClient
    private lateinit var mPlaceDetectionClient: PlaceDetectionClient
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    /**
     * Activity onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_map)

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null)

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null)

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // 位置情報権限を確認し、あればマップを準備、なければ権限のリクエスト
        this.initMapWithConfirmPermission()
    }

    /**
     * 位置情報取得権限を確認し、あればマップを準備、なければ位置情報取得権限のリクエストを行う.
     * 位置情報取得権限がつかない限りループする.
     */
    private fun initMapWithConfirmPermission(){
        val isLocationPermissionGranted = ContextCompat.checkSelfPermission(this.applicationContext,
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if(isLocationPermissionGranted){
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        } else {
            Toast.makeText(this.applicationContext, "アプリ実行には位置情報権限が必要です.", Toast.LENGTH_LONG)
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /**
     * GoogleMap onMapReady
     */
    override fun onMapReady(googleMap: GoogleMap) {
        // Mapを初期化
        mMap = googleMap
        googleMap.setOnMarkerClickListener(this)
        googleMap.setOnMapClickListener(this)

        // 現在値表示ボタンを追加する
        setCurrentButton(true)

        // 現在値を取得し、マップをそこに持っていく
        initLocationToCurrent()

    }

    /**
     * LocationPermission RequestResult
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                this.initMapWithConfirmPermission()
            }
        }
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
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    mLastKnownLocation = task.result
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
     * オプションメニュー追加
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.current_place_menu, menu)
        return true
    }

    /**
     * オプションメニューイベント
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.option_get_place) {
            getPlaceInfo(10)
        } else if (item.itemId == R.id.option_pick_place) {
            pickPlaceInfo()
        }
        return true
    }

    /**
     * PlacePickerを表示する
     */
    private fun pickPlaceInfo(){
        val builder = PlacePicker.IntentBuilder()
        builder.setLatLngBounds(LatLngBounds(LatLng(0.0, 50.0), LatLng(0.0, 49.0)))
        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
    }

    /**
     * ActivityResult受け取り処理
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        /**
         * PlacePickerで取得した情報をPlaceInfoとして保持する
         */
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK){
            val place = PlacePicker.getPlace(this, data)
            addMarker(PlaceInfo(place))
        }
    }

    /**
     * 現在地の近くから5件プレイスを取得する
     */
    private fun getPlaceInfo(maxEntries: Int) {
        val placeResult = mPlaceDetectionClient.getCurrentPlace(null)
        placeResult.addOnCompleteListener(object : OnCompleteListener<PlaceLikelihoodBufferResponse> {
            override fun onComplete(task: Task<PlaceLikelihoodBufferResponse>) {
                if (!task.isSuccessful || task.result == null) {
                    Timber.e("Exception: %s", task.exception)
                }

                val likelyPlaces = task.result

                // Set the count, handling cases where less than 5 entries are returned.
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
        AlertDialog.Builder(this)
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




    /**
     * GoogleMap onMarkerClick
     */
    override fun onMarkerClick(marker: Marker): Boolean {
//        var message = StringBuilder()
//                .appendln(marker.title)
//                .append("緯度：").appendln(marker.position.latitude)
//                .append("経度：").appendln(marker.position.longitude)
//                .toString()
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        return false
    }

    /**
     * GoogleMap onMapClick
     */
    override fun onMapClick(p0: LatLng?) {
//    mMap.addMarker(MarkerOptions().position(p0!!).title("New Marker"))
    }
}
