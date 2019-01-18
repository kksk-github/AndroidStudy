package io.keiji.myapplication.util

import android.content.SharedPreferences
import com.google.gson.Gson
import io.keiji.myapplication.entity.MarkerInfo
import com.google.android.gms.drive.metadata.CustomPropertyKey.fromJson
import com.google.gson.reflect.TypeToken



private const val PREF_KEY_MARKERS = "pref_key_markers"

/**
 * Created by z00s600051 on 2019/01/17.
 */
class PreferenceUtil{
    companion object {
        var preferenceUtil = PreferenceUtil()
    }

    private val gson = Gson()

    fun setMarkers(pref: SharedPreferences, markers: List<MarkerInfo>){
        this.setPreference(pref, PREF_KEY_MARKERS, gson.toJson(markers))
    }

    fun getMarkers(pref: SharedPreferences): MutableList<MarkerInfo>{
        val json = this.getPreference(pref, PREF_KEY_MARKERS)
        if(json == null) {
            return ArrayList()
        }
        val listType = object : TypeToken<MutableList<MarkerInfo>>() { }.type
        return gson.fromJson<MutableList<MarkerInfo>>(json, listType)
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