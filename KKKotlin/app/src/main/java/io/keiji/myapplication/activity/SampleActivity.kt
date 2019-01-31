package io.keiji.myapplication.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.keiji.myapplication.R
import io.keiji.myapplication.event.ToastEvent
import io.keiji.myapplication.event.StopAlertEvent
import io.keiji.myapplication.fragment.SampleFragment
import kotlinx.android.synthetic.main.activity_sample.*
import org.greenrobot.eventbus.EventBus



class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        if(savedInstanceState == null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container1, SampleFragment.createInstance("ほげほげぇぇぇぇ"))

            transaction.commit()
        }

        stopBtn.setOnClickListener {
            finish()
        }
    }

    override fun finish(){
        stopAlert()
        super.finish()
    }

    private fun stopAlert(){
        EventBus.getDefault().post(ToastEvent("hogehoge"))
//        EventBus.getDefault().post(StopAlertEvent())
    }

}
