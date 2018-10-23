package io.keiji.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.tag("TIMBER")
        Timber.plant(Timber.DebugTree())
        btn1.setOnClickListener{Timber.d("log1")}
        btn2.setOnClickListener{Timber.d("log2")}
        btn3.setOnClickListener{Timber.d("log3")}
        btn4.setOnClickListener{Timber.d("log4")}
    }
}
