package io.keiji.myapplication

import android.app.Application
import io.keiji.myapplication.timber.MyDebugTree
import timber.log.Timber

/**
 * Created by z00s600051 on 2018/10/23.
 */
class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(MyDebugTree())
        Timber.tag("TIMBER")
    }
}