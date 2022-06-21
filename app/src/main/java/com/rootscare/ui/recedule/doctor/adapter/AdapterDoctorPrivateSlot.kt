package com.rootscare.ui.recedule.doctor.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnDoctorPrivateSlotClickListner
import com.rootscare.R
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.ResultItem
import com.rootscare.databinding.ItemDoctorSlotsRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_doctor_slots_recyclerview.view.*

class AdapterDoctorPrivateSlot(
    val doctorprivateList: ArrayList<ResultItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterDoctorPrivateSlot.ViewHolder>() {
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
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemDoctorSlotsRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_doctor_slots_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return doctorprivateList!!.size
//        return 6
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemDoctorSlotsRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.crdview_doctor_private_slot.setOnClickListener {
                selectedPosition = localPosition
                notifyDataSetChanged()
                recyclerViewItemClick.onItemClick(doctorprivateList?.get(localPosition))
            }
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
            localPosition = pos

            if (selectedPosition == pos)
                itemView.rootView?.setBackgroundColor(Color.parseColor("#D2F2F5"))
            else
                itemView.rootView?.setBackgroundColor(Color.parseColor("#ffffff"))

            println("hospitalId ${doctorprivateList?.get(pos)?.hospitalId}")
            if (doctorprivateList?.get(pos)?.hospitalId != null && doctorprivateList[pos]?.hospitalId != "") {
                itemView.rootView?.txt_doctorslots_clinic_name?.text =
                    "Hospital Name:" + " " + doctorprivateList?.get(
                        pos
                    )?.hospitalName
            } else {
                itemView.rootView?.txt_doctorslots_clinic_name?.text =
                    "Clinic Name:" + " " + doctorprivateList?.get(
                        pos
                    )?.clinicName
            }

            itemView.rootView?.txt_doctorslots_clinic_address?.text =
                doctorprivateList?.get(pos)?.clinicAddress
            itemView.rootView?.txt_doctorslots_clinic_day?.text = doctorprivateList?.get(pos)?.day
//            itemView?.rootView?.txt_doctor_clinic_time?.setText(doctorprivateList?.get(pos)?.timeFrom+"-"+doctorprivateList?.get(pos)?.timeTo)


        }
    }

}