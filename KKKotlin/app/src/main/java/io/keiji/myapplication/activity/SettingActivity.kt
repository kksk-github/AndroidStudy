package io.keiji.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.keiji.myapplication.R
import io.keiji.myapplication.entity.ParcelableMarkerInfo
import io.keiji.myapplication.event.StopAlertEvent
import io.keiji.myapplication.event.ToastEvent
import io.keiji.myapplication.fragment.SampleFragment
import kotlinx.android.synthetic.main.activity_sample.*
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.view_markerrow.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by z00s600051 on 2019/01/22.
 */
class SettingActivity : AppCompatActivity() {

    private lateinit var markers: ArrayList<ParcelableMarkerInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        markers = intent.getParcelableArrayListExtra<ParcelableMarkerInfo>("test")
        markerList.adapter = MarkerSettingAdapter(applicationContext, markers)

//        if(savedInstanceState == null){
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.add(R.id.container1, SampleFragment.createInstance("ほげほげぇぇぇぇ"))
//
//            transaction.commit()
//        }
//
//        stopBtn.setOnClickListener {
//            finish()
//        }
    }

    override fun finish(){
        stopAlert()



        val intent = Intent(this, SettingActivity::class.java)
        intent.putParcelableArrayListExtra("test", markers)
        setResult(RESULT_OK, intent)
        super.finish()
    }

    private fun stopAlert(){
        EventBus.getDefault().post(ToastEvent("hogehoge"))
//        EventBus.getDefault().post(StopAlertEvent())
    }

}