package io.keiji.myapplication.event

import com.google.android.gms.maps.model.LatLng

/**
 * Created by z00s600051 on 2018/12/06.
 */
class MapCallEvent(val callEnum:MapCallEnum, val latLng: LatLng?){
    constructor(callEnum:MapCallEnum): this(callEnum, null)

    enum class MapCallEnum {
        PickPlace,
        GetAroundCurrent,
        DeleteMarker,
        SetTarget,
        StartMarkerSetting,
        StartAlert,
        StopAlert,
    }
}