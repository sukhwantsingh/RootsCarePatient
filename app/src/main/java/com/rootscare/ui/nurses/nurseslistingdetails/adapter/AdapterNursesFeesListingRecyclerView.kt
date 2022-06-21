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
import com.rootscare.databinding.ItemNursesFeesListingRecyclerviewBinding
import com.rootscare.databinding.ItemSeeAllNursesByGridRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.ui.nurses.adapter.AdapterSeeAllNursesByGridRecyclerView
import kotlinx.android.synthetic.main.item_nurses_fees_listing_recyclerview.view.*

class AdapterNursesFeesListingRecyclerView(val hourlyRatesList: ArrayList<HourlyRatesItem?>?,internal var context: Context) : RecyclerView.Adapter<AdapterNursesFeesListingRecyclerView.ViewHolder>() {
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }
    internal lateinit var recyclerViewItemClickWithView: OnClickWithTwoButton

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding = DataBindingUtil.inflate<ItemNursesFeesListingRecyclerviewBinding>(
            LayoutInflater.from(context),
            R.layout.item_nurses_fees_listing_recyclerview, parent, false)
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return hourlyRatesList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemNursesFeesListingRecyclerviewBinding) : RecyclerView.ViewHolder(itemView.root) {

        private var local_position:Int = 0
        init {
//
        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos

            if(hourlyRatesList?.get(pos)?.duration!=null && !hourlyRatesList?.get(pos)?.duration.equals("")){
                itemView?.rootView?.txt_nurse_hourtext?.setText("For"+" "+hourlyRatesList?.get(pos)?.duration)
            }else{
                itemView?.rootView?.txt_nurse_hourtext?.setText("")
            }

            if (hourlyRatesList?.get(pos)?.price!=null && !hourlyRatesList?.get(pos)?.price.equals("")){
                itemView?.rootView?.txt_nurse_hourvalue?.setText("SR"+hourlyRatesList?.get(pos)?.price)
            }else{
                itemView?.rootView?.txt_nurse_hourvalue?.setText("")
            }
        }
    }

}