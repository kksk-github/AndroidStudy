package io.keiji.myapplication.fragment

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.keiji.myapplication.R
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat

import kotlinx.android.synthetic.main.fragment_sample.*
import android.media.RingtoneManager



class SampleFragment : Fragment() {
    private var text: String = ""

    companion object {
        private const val KEY_TEXT = "key_text"

        fun createInstance(text: String): SampleFragment {
            val fragment = SampleFragment()
            val args = Bundle()
            args.putString(KEY_TEXT, text)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            text = arguments.getString(KEY_TEXT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater?.inflate(R.layout.fragment_sample, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentBtn.text = text
    }

    override fun onResume() {
        super.onResume()

        fragmentBtn.setOnClickListener {
            val name = "通知のタイトル"
            val id = "channelId"
            val notifyDesctiption = "通知の詳細情報"

            val notificationManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.getNotificationChannel(id) == null) {
                val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
                mChannel.apply {
                    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    setSound(defaultSoundUri, null)
                    lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                    description = notifyDesctiption
                }
                notificationManager.createNotificationChannel(mChannel)
            }

            val notification = NotificationCompat
                    .Builder(activity, id)
                    .apply {
                        setSmallIcon(R.drawable.ic_launcher_background)
                        mContentTitle = "タイトルだよ"
                        mContentText = "内容だよ"
                    }.build()

            notificationManager.notify(1, notification)
        }
    }
}
