package com.rootscare.ui.caregiver.caregiverbookingappointment.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnClickWithTwoButton
import com.interfaces.OnNurseSlotClick
import com.rootscare.R
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.ResultItem
import com.rootscare.databinding.ItemNursetimingRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.ui.nurses.nurseslistingdetails.adapter.AdapterNurseViewTimingRecyclerview
import kotlinx.android.synthetic.main.item_doctor_slot_divission.view.*
import kotlinx.android.synthetic.main.item_nurse_hourly_slot.view.*
import kotlinx.android.synthetic.main.item_nursetiming_recyclerview.view.*

class AdapterNurseSlotTiimeRecyclerview(val nurseTimingList: ArrayList<ResultItem?>?, internal var context: Context) : RecyclerView.Adapter<AdapterNurseSlotTiimeRecyclerview.ViewHolder>() {
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }
    private var selectedPosition = -1
    val sdk = Build.VERSION.SDK_INT
    internal lateinit var recyclerViewItemClickWithView: OnNurseSlotClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding = DataBindingUtil.inflate<ItemNursetimingRecyclerviewBinding>(
            LayoutInflater.from(context),
            R.layout.item_nursetiming_recyclerview, parent, false)
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
//        return hourlyRatesList?.size!!
        return nurseTimingList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemNursetimingRecyclerviewBinding) : RecyclerView.ViewHolder(itemView.root) {

        private var local_position:Int = 0
        init {
//

            itemView?.root?.ll_nurse_slot_time?.setOnClickListener(View.OnClickListener {
                selectedPosition = local_position
                notifyDataSetChanged()
                recyclerViewItemClickWithView?.onConfirm(nurseTimingList?.get(local_position)!!)
            })
        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos

            if(selectedPosition==pos){
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    itemView?.rootView?.txt_nurse_view_timings?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_green_btn) )
                    itemView?.rootView?.txt_nurse_view_timings?.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_green_btn))
                    itemView?.rootView?.txt_nurse_view_timings?.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    itemView?.rootView?.txt_nurse_view_timings?.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_green_btn))
                    itemView?.rootView?.txt_nurse_view_timings?.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_green_btn))
                    itemView?.rootView?.txt_nurse_view_timings?.setTextColor(Color.parseColor("#ffffff"));
                }
            }
               // itemView?.rootView?.setBackgroundColor(Color.parseColor("#D2F2F5"))
            else{
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    itemView?.rootView?.txt_nurse_view_timings?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_blue_broder) )
                    itemView?.rootView?.txt_nurse_view_timings?.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_blue_broder))
                    itemView?.rootView?.txt_nurse_view_timings?.setTextColor(Color.parseColor("#515C6F"));
                } else {
                    itemView?.rootView?.txt_nurse_view_timings?.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_blue_broder))
                    itemView?.rootView?.txt_nurse_view_timings?.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_blue_broder))
                    itemView?.rootView?.txt_nurse_view_timings?.setTextColor(Color.parseColor("#515C6F"));
                }
            }
                itemView?.rootView?.setBackgroundColor(Color.parseColor("#ffffff"))

            itemView?.rootView?.txt_nurse_view_timings?.setText(nurseTimingList?.get(pos)?.startTime)

            if(nurseTimingList?.get(pos)?.status.equals("Active")){
                itemView?.rootView?.txt_nurse_view_timings?.setAlpha(1.0F)
                itemView?.rootView?.ll_nurse_slot_time?.isClickable=true
            }else if(nurseTimingList?.get(pos)?.status.equals("Inactive")){
                itemView?.rootView?.txt_nurse_view_timings?.setAlpha(0.4F)
                itemView?.rootView?.ll_nurse_slot_time?.isClickable=false
            }
        }
    }

}