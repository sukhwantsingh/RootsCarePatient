package com.rootscare.ui.nurses.nurseslistingdetails.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnClickWithTwoButton
import com.rootscare.R
import com.rootscare.data.model.api.response.nurses.nursedetails.HourlyRatesItem
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.ResultItem
import com.rootscare.databinding.ItemNursesFeesListingRecyclerviewBinding
import com.rootscare.databinding.ItemNursetimingRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_nurses_fees_listing_recyclerview.view.*
import kotlinx.android.synthetic.main.item_nursetiming_recyclerview.view.*

class AdapterNurseViewTimingRecyclerview (val nurseTimingList: ArrayList<ResultItem?>?,internal var context: Context) : RecyclerView.Adapter<AdapterNurseViewTimingRecyclerview.ViewHolder>() {
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }
    internal lateinit var recyclerViewItemClickWithView: OnClickWithTwoButton

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
        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos
            itemView?.rootView?.txt_nurse_view_timings?.setText(nurseTimingList?.get(pos)?.startTime)


            if(nurseTimingList?.get(pos)?.status.equals("Active")){
                itemView?.rootView?.txt_nurse_view_timings?.setAlpha(1.0F)
             //   itemView?.rootView?.ll_nurse_slot_time?.isClickable=true
            }else if(nurseTimingList?.get(pos)?.status.equals("Inactive")){
                itemView?.rootView?.txt_nurse_view_timings?.setAlpha(0.4F)
              //  itemView?.rootView?.ll_nurse_slot_time?.isClickable=false
            }

//            if(hourlyRatesList?.get(pos)?.duration!=null && !hourlyRatesList?.get(pos)?.duration.equals("")){
//                itemView?.rootView?.txt_nurse_hourtext?.setText("For"+" "+hourlyRatesList?.get(pos)?.duration)
//            }else{
//                itemView?.rootView?.txt_nurse_hourtext?.setText("")
//            }
//
//            if (hourlyRatesList?.get(pos)?.price!=null && !hourlyRatesList?.get(pos)?.price.equals("")){
//                itemView?.rootView?.txt_nurse_hourvalue?.setText("SR"+hourlyRatesList?.get(pos)?.price)
//            }else{
//                itemView?.rootView?.txt_nurse_hourvalue?.setText("")
//            }
        }
    }

}