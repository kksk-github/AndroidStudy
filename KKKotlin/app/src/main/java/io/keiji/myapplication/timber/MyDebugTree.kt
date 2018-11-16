package io.keiji.myapplication.timber

import timber.log.Timber
import android.util.Log


/**
 * Created by z00s600051 on 2018/10/23.
 */
class MyDebugTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)

        val thread = Throwable().stackTrace

        if (thread != null && thread.size >= 5) {
            val stack = thread[5]
            val className = stack.className
            val packageName = className.substring(0, className.lastIndexOf("."))
            val fileName = stack.fileName

            val msg = message + " at " + packageName + "(" + fileName + ":" + stack.lineNumber + ")"

            Log.println(priority, tag, msg)
        } else {
            Log.println(priority, tag, message)
        }
    }
}