package com.rootscare.ui.bookingappointment.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.OnPatientFamilyMemberListener
import com.rootscare.R
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.ResultItem
import com.rootscare.databinding.ItemAddPatientBinding
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_add_patient.view.*
import java.util.*


class AdapterAddPatientListRecyclerview(
    val patientfamilymemberList: ArrayList<ResultItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterAddPatientListRecyclerview.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterAddPatientListRecyclerview::class.java.simpleName
    }

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
//        return 4
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemAddPatientBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var local_position: Int = 0

        init {

            itemView.root.ll_add_patient?.setOnClickListener {
                selectedPosition = local_position
                notifyDataSetChanged()
                recyclerViewOnAddPatientListClick.onItemClick(
                    patientfamilymemberList?.get(
                        local_position
                    )!!
                )
            }
            itemView.root.img_edit?.setOnClickListener {
                recyclerViewOnAddPatientListClick.onEditButtonClick(
                    patientfamilymemberList?.get(
                        local_position
                    )!!
                )
            }

            itemView.root.img_remove?.setOnClickListener {
                recyclerViewOnAddPatientListClick.onDeleteButtonClick(
                    patientfamilymemberList?.get(
                        local_position
                    )?.id!!
                )
            }


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos

            if (selectedPosition == pos)
                itemView.rootView?.setBackgroundColor(Color.parseColor("#D2F2F5"))
            else
                itemView.rootView?.setBackgroundColor(Color.parseColor("#ffffff"))

            itemView.rootView?.txt_patient_familymember_name?.text = patientfamilymemberList?.get(
                pos
            )?.firstName + " " + patientfamilymemberList?.get(pos)?.lastName
            Glide.with(context)
                .load(
                    BaseMediaUrls.USERIMAGE.url + (patientfamilymemberList?.get(
                        pos
                    )?.image)
                )
                .into(itemView.rootView?.img_patient_familymember_photo!!)

            if (!patientfamilymemberList?.get(pos)?.image.equals("") || patientfamilymemberList?.get(
                    pos
                )?.image != null
            ) {
                Glide.with(context)
                    .load(
                        BaseMediaUrls.USERIMAGE.url + (patientfamilymemberList?.get(
                            pos
                        )?.image)
                    )
                    .into(itemView.rootView?.img_patient_familymember_photo!!)

            } else {

                Glide.with(context)
                    .load(context.resources.getDrawable(R.drawable.no_image))
                    .into(itemView.rootView?.img_patient_familymember_photo!!)

            }


//            itemView?.rootView?.txt_teacher_name?.text= trainerList?.get(pos)?.name
//            itemView?.rootView?.txt_teacher_qualification?.text= "Qualification : "+" "+trainerList?.get(pos)?.qualification
//            if(trainerList?.get(pos)?.avgRating!=null && !trainerList?.get(pos)?.avgRating.equals("")){
//                itemView?.rootView?.ratingBarteacher?.rating= trainerList?.get(pos)?.avgRating?.toFloat()!!
//            }


//            itemView?.rootView?.txt_rating_count?.text="("+contactListItem?.get(pos)?.contactRating+")"
//            (contactListItem?.get(pos)?.contactRating)?.toFloat()?.let { itemView?.rootView?.ratingBar?.setRating(it) }
////            itemView?.rootView?.ratingBar?.rating=1.5f


        }
    }

}