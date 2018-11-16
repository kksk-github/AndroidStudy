package io.keiji.myapplication.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.keiji.myapplication.R
import io.keiji.myapplication.fragment.SampleFragment

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        if(savedInstanceState == null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container1, SampleFragment.createInstance("ほげほげぇぇぇぇ"))

            transaction.commit()
        }
    }

}
