package com.rootscare.ui.nurses.nursesbookingappointment.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.OnItemClikWithIdListener
import com.interfaces.OnPatientFamilyMemberListener
import com.rootscare.R
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.ResultItem
import com.rootscare.databinding.ItemAddPatientBinding
import com.rootscare.ui.bookingappointment.adapter.AdapterAddPatientListRecyclerview
import com.rootscare.utilitycommon.setRemoteProfileImage
import kotlinx.android.synthetic.main.item_add_patient.view.*
import java.util.*

class AdapterForNursesAddPatientListRecyclerview(val patientfamilymemberList: ArrayList<ResultItem?>?, internal var context: Context
) : RecyclerView.Adapter<AdapterForNursesAddPatientListRecyclerview.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnItemClikWithIdListener
    internal lateinit var recyclerViewOnAddPatientListClick: OnPatientFamilyMemberListener

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding = DataBindingUtil.inflate<ItemAddPatientBinding>(
            LayoutInflater.from(context),
            R.layout.item_add_patient, parent, false
        )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return patientfamilymemberList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemAddPatientBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {

            itemView.root.ll_add_patient?.setOnClickListener {
                selectedPosition = localPosition
                notifyDataSetChanged()
                recyclerViewOnAddPatientListClick.onItemClick(patientfamilymemberList?.get(localPosition)!!)
            }
            itemView.root.img_edit?.setOnClickListener {
                recyclerViewOnAddPatientListClick.onEditButtonClick(
                    patientfamilymemberList?.get(
                        localPosition
                    )!!
                )
            }

            itemView.root.img_remove?.setOnClickListener {
                recyclerViewOnAddPatientListClick.onDeleteButtonClick(
                    patientfamilymemberList?.get(
                        localPosition
                    )?.id!!
                )
            }


        }

        fun onBind(pos: Int) {
            localPosition = pos
            if (selectedPosition == pos) itemView.rootView?.setBackgroundResource(R.drawable.sq_back_patient_selected)
            else itemView.rootView?.setBackgroundResource(0)
//
//            if (selectedPosition == pos)
//                itemView.rootView?.setBackgroundColor(Color.parseColor("#D2F2F5"))
//            else
//                itemView.rootView?.setBackgroundColor(Color.parseColor("#ffffff"))

            itemView.rootView?.txt_patient_familymember_name?.text = patientfamilymemberList?.getOrNull(pos)?.firstName + " " + patientfamilymemberList?.get(pos)?.lastName

            itemView.rootView?.img_patient_familymember_photo?.
            setRemoteProfileImage(patientfamilymemberList?.getOrNull(pos)?.image)

        }
    }

}