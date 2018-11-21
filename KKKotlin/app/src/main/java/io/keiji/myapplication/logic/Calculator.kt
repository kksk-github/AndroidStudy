package io.keiji.myapplication.logic

import com.google.android.gms.maps.model.LatLng
import java.lang.Math.sqrt

/**
 * Created by z00s600051 on 2018/11/19.
 */
class Calculator {
    /**
     * 二点間の距離を計算して返却
     */
    fun calcDistance(pointA: LatLng, pointB: LatLng) : Double{
        val y = pointA.latitude - pointB.latitude
        val x = pointA.longitude - pointB.longitude
        return sqrt(x*x + y*y)
    }

    fun calcDistanceFromCurrent(point: LatLng): Double{
        return this.calcDistance(point, point)
    }
}