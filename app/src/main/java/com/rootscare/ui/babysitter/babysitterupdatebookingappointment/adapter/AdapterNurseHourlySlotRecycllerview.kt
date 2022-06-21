package com.rootscare.ui.babysitter.babysitterupdatebookingappointment.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnHourlyItemClick
import com.rootscare.R
import com.rootscare.data.model.api.response.nurses.nursehourlyslot.ResultItem
import com.rootscare.databinding.ItemNurseHourlySlotBinding
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_nurse_hourly_slot.view.*


class AdapterNurseHourlySlotRecycllerview (val hourlyList: ArrayList<ResultItem?>?,internal var context: Context) : RecyclerView.Adapter<AdapterNurseHourlySlotRecycllerview.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    private var lastChecked: CheckBox? = null
    private var lastCheckedPos = 0
    private var selectedPosition = 0
    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnHourlyItemClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding = DataBindingUtil.inflate<ItemNurseHourlySlotBinding>(
            LayoutInflater.from(context),
            R.layout.item_nurse_hourly_slot, parent, false)
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return hourlyList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemNurseHourlySlotBinding) : RecyclerView.ViewHolder(itemView.root) {

        private var local_position:Int = 0
        init {
//            itemView?.root?.crdview_appoitment_list?.setOnClickListener(View.OnClickListener {
//                recyclerViewItemClickWithView?.onItemClick(1)
//            })

            itemView?.root?.crdview_nurse_hourly_slot?.setOnClickListener(View.OnClickListener {
                selectedPosition = local_position
                notifyDataSetChanged()
                recyclerViewItemClickWithView?.onConfirm(hourlyList?.get(local_position)!!)
            })
        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos
            if(selectedPosition==pos)
                itemView?.rootView?.setBackgroundColor(Color.parseColor("#D2F2F5"))
            else
                itemView?.rootView?.setBackgroundColor(Color.parseColor("#ffffff"))

            if(hourlyList?.get(pos)?.duration!=null && !hourlyList?.get(pos)?.duration.equals("")){
               itemView?.rootView?.txt_hour?.setText(hourlyList?.get(pos)?.duration)
            }else{
                itemView?.rootView?.txt_hour?.setText("")
            }
            if(hourlyList?.get(pos)?.price!=null && !hourlyList?.get(pos)?.price.equals("")){
                itemView?.rootView?.txt_price?.setText("SR"+" "+hourlyList?.get(pos)?.price)
            }else{
                itemView?.rootView?.txt_price?.setText("")
            }
        }
    }

}