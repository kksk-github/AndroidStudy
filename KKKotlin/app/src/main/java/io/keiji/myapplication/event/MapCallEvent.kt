package io.keiji.myapplication.event

/**
 * Created by z00s600051 on 2018/12/06.
 */
class MapCallEvent(val callEnum:MapCallEnum){
    enum class MapCallEnum {
        PickPlace,
        GetAroundCurrent,
    }
}