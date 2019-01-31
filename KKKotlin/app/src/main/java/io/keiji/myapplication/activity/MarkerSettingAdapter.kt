package io.keiji.myapplication.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.keiji.myapplication.R
import io.keiji.myapplication.entity.MarkerInfo
import io.keiji.myapplication.entity.ParcelableMarkerInfo
import timber.log.Timber

/**
 * Created by z00s600051 on 2019/01/22.
 */
class MarkerSettingAdapter(context: Context, list: java.util.ArrayList<ParcelableMarkerInfo>) : ArrayAdapter<ParcelableMarkerInfo>(context, 0, list) {
    private val mInflater: LayoutInflater
    private var mCheckbox: CheckBox? = null

    init {
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_markerrow, null)
        }

        val item = this.getItem(position)
        if (item != null) {
            mCheckbox = convertView!!.findViewById(R.id.checkbox_marker)
            mCheckbox!!.text = item.name
            mCheckbox!!.isChecked = item.flgTarget
        }

        mCheckbox!!.setOnCheckedChangeListener{buttonView, isChecked ->
            item.flgTarget = isChecked
        }
        
        return convertView!!
    }
}