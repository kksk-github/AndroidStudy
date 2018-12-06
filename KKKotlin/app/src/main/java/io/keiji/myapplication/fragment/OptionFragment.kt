package io.keiji.myapplication.fragment


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.view.*
import io.keiji.myapplication.R
import kotlinx.android.synthetic.main.fragment_sample.*

/**
 * Created by z00s600051 on 2018/12/03.
 */
class OptionFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        return inflater?.inflate(R.layout.fragment_option, container, false)
    }

    /**
     * オプションメニュー追加
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        inflater.inflate(R.menu.current_place_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * オプションメニューイベント
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.option_get_place) {
            // 現在値付近のPlaceを取得
//            getAroundCurrentPlaces(10)
        } else if (item.itemId == R.id.option_pick_place) {
            // 検索してPlaceを取得
//            pickPlaceInfo()
        }
        return true
    }
}