package io.keiji.myapplication.activity

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
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
    private var mLocationPermissionGranted: Boolean = false
    private lateinit var mLastKnownLocation: Location
    private lateinit var mGeoDataClient: GeoDataClient
    private lateinit var mPlaceDetectionClient: PlaceDetectionClient
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var placeInfoList: MutableList<PlaceInfo> = mutableListOf()

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
        mLocationPermissionGranted = ContextCompat.checkSelfPermission(this.applicationContext,
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if(mLocationPermissionGranted){
            initMapFragment()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /**
     * MapFragmentを初期化する
     * 複数回呼ばないこと
     */
    private fun initMapFragment(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
     * GoogleMap onMarkerClick
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        var message = StringBuilder()
                .appendln(marker.title)
                .append("緯度：").appendln(marker.position.latitude)
                .append("経度：").appendln(marker.position.longitude)
                .toString()
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        return false
    }

    /**
     * GoogleMap onMapClick
     */
    override fun onMapClick(p0: LatLng?) {
        mMap.addMarker(MarkerOptions().position(p0!!).title("New Marker"))
    }

    /**
     * LocationPermission RequestResult
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                    initMapFragment()
                } else {
                    Toast.makeText(this.applicationContext, "実行には位置情報権限が必要です.", Toast.LENGTH_LONG)
                }
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent){
        /**
         * PlacePickerで取得した情報をPlaceInfoとして保持する
         */
        if(requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK){
            val place = PlacePicker.getPlace(this, intent)
            val placeInfo = PlaceInfo(place.name.toString(), place.address.toString(), )
            Toast.makeText(this, "Place: ${place.name}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 現在地の近くから5件プレイスを取得する
     */
    private fun getPlaceInfo(maxEntries: Int) {
        // リストを初期化
        placeInfoList.clear()

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

                openPlacesDialog()
            }
        })
    }

    /**
     * 取得されたプレイスをダイアログで表示
     * 選択されたプレイスにマーカーを打ち、カメラ移動
     */
    private fun openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        val listener = DialogInterface.OnClickListener { _, which ->
            // The "which" argument contains the position of the selected item.
            val markerSnippet = if (placeInfoList[which].attribution.isNullOrEmpty()) {
                placeInfoList[which].address
            } else {
                placeInfoList[which].address + "\n" + placeInfoList[which].attribution
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            mMap.addMarker(MarkerOptions()
                    .title(placeInfoList[which].name)
                    .position(placeInfoList[which].latLng)
                    .snippet(markerSnippet))

            // Position the map's camera at the location of the marker.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeInfoList[which].latLng, DEFAULT_ZOOM))
        }

        val places = placeInfoList.map({v -> v.name}).toTypedArray()

        // Display the dialog.
        AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(places, listener)
                .show()
    }
}
