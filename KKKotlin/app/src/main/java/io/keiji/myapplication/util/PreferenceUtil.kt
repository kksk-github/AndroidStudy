package io.keiji.myapplication.util

import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import io.keiji.myapplication.entity.MarkerInfo
import com.google.gson.reflect.TypeToken
import io.keiji.myapplication.entity.ParcelableMarkerInfo


private const val PREF_KEY_MARKERS = "pref_key_markers"

/**
 * Created by z00s600051 on 2019/01/17.
 */
class PreferenceUtil{
    companion object {
        var preferenceUtil = PreferenceUtil()
    }

    private val gson = Gson()

    fun setMarkers(pref: SharedPreferences, markers: HashMap<LatLng, ParcelableMarkerInfo>){
        this.setPreference(pref, PREF_KEY_MARKERS, gson.toJson(markers.values))
    }

    fun getMarkers(pref: SharedPreferences): HashMap<LatLng, ParcelableMarkerInfo>{
        var map = HashMap<LatLng, ParcelableMarkerInfo>()

        this.getPreference(pref, PREF_KEY_MARKERS)?.let{
            val listType = object : TypeToken<ArrayList<ParcelableMarkerInfo>>() { }.type
            val json = gson.fromJson<ArrayList<ParcelableMarkerInfo>>(it!!, listType)
            for(marker in json){
                map.put(marker.latLng!!, marker)
            }
        }

        return map
    }

    private fun setPreference(pref: SharedPreferences, key: String, json: String){
        val edit = pref.edit()
        edit.putString(key, json)
        edit.commit()
    }

    private fun getPreference(pref: SharedPreferences, key:String): String?{
        return pref.getString(key, null)
    }
}