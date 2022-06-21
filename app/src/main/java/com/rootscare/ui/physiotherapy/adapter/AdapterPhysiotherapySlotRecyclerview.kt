package com.rootscare.ui.physiotherapy.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnDoctorPrivateSlotClickListner
import com.rootscare.R
import com.rootscare.databinding.ItemPhysiotherapySlotRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw

class AdapterPhysiotherapySlotRecyclerview (internal var context: Context) : RecyclerView.Adapter<AdapterPhysiotherapySlotRecyclerview.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
//    internal lateinit var recyclerViewItemClickWithView: OnItemClikWithIdListener
    internal lateinit var recyclerViewItemClick: OnDoctorPrivateSlotClickListner

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding = DataBindingUtil.inflate<ItemPhysiotherapySlotRecyclerviewBinding>(
            LayoutInflater.from(context),
            R.layout.item_physiotherapy_slot_recyclerview, parent, false)
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
//        return doctorprivateList!!.size
        return 6
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemPhysiotherapySlotRecyclerviewBinding) : RecyclerView.ViewHolder(itemView.root) {

        private var local_position:Int = 0
        init {
//            itemView?.root?.crdview_doctor_private_slot?.setOnClickListener(View.OnClickListener {
//                selectedPosition = local_position
//                notifyDataSetChanged()
//                recyclerViewItemClick?.onItemClick(doctorprivateList?.get(local_position))
//            })
//            itemView?.root?.btn_view_trainner_profile?.setOnClickListener(View.OnClickListener {
//                recyclerViewItemClickWithView?.onItemClick(trainerList?.get(local_position)?.id?.toInt()!!)
//            })

//            itemView.root?.img_bid?.setOnClickListener {
//                run {
//                    recyclerViewItemClick?.onClick(itemView.root?.img_bid,local_position)
//                    //serviceListItem?.get(local_position)?.requestid?.let { it1 -> recyclerViewItemClick.onClick(itemView.root?.img_bid,it1) }
//                }
//            }
//
//            itemView.root?.img_details?.setOnClickListener {
//                run {
//                    recyclerViewItemClick?.onClick(itemView.root?.img_details,local_position)
//                    // serviceListItem?.get(local_position)?.requestid?.let { it1 -> recyclerViewItemClick.onClick(itemView.root?.img_details,it1) }
//                }
//            }


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos

//            if(selectedPosition==pos)
//                itemView?.rootView?.setBackgroundColor(Color.parseColor("#D2F2F5"))
//            else
//                itemView?.rootView?.setBackgroundColor(Color.parseColor("#ffffff"))

//            itemView?.rootView?.txt_doctorslots_clinic_name?.setText("Clinic Name:"+" "+doctorprivateList?.get(pos)?.clinicName)
//            itemView?.rootView?.txt_doctorslots_clinic_address?.setText(doctorprivateList?.get(pos)?.clinicAddress)
//            itemView?.rootView?.txt_doctorslots_clinic_day?.setText(doctorprivateList?.get(pos)?.day)
//            itemView?.rootView?.txt_doctor_clinic_time?.setText(doctorprivateList?.get(pos)?.timeFrom+"-"+doctorprivateList?.get(pos)?.timeTo)



        }
    }

}