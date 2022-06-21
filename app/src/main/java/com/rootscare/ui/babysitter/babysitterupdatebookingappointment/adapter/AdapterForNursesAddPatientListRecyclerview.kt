package com.rootscare.ui.babysitter.babysitterupdatebookingappointment.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.OnPatientFamilyMemberListener
import com.rootscare.R
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.ResultItem
import com.rootscare.databinding.ItemAddPatientBinding
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.ui.bookingappointment.adapter.AdapterAddPatientListRecyclerview
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_add_patient.view.*
import java.util.ArrayList

class AdapterForNursesAddPatientListRecyclerview (val patientfamilymemberList: ArrayList<ResultItem?>?,internal var context: Context) : RecyclerView.Adapter<AdapterForNursesAddPatientListRecyclerview.ViewHolder>() {
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
            R.layout.item_add_patient, parent, false)
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return patientfamilymemberList!!.size
//        return 4
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemAddPatientBinding) : RecyclerView.ViewHolder(itemView.root) {

        private var local_position:Int = 0
        init {

            itemView?.root?.ll_add_patient?.setOnClickListener(View.OnClickListener {
                selectedPosition = local_position
                notifyDataSetChanged()
                recyclerViewOnAddPatientListClick?.onItemClick(patientfamilymemberList?.get(local_position)!!)
            })
            itemView?.root?.img_edit?.setOnClickListener(View.OnClickListener {
                recyclerViewOnAddPatientListClick?.onEditButtonClick(patientfamilymemberList?.get(local_position)!!)
            })

            itemView?.root?.img_remove?.setOnClickListener(View.OnClickListener {
                recyclerViewOnAddPatientListClick?.onDeleteButtonClick(patientfamilymemberList?.get(local_position)?.id!!)
            })



        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos

            if(selectedPosition==pos)
                itemView?.rootView?.setBackgroundColor(Color.parseColor("#D2F2F5"))
            else
                itemView?.rootView?.setBackgroundColor(Color.parseColor("#ffffff"))

            itemView?.rootView?.txt_patient_familymember_name?.setText(patientfamilymemberList?.get(pos)?.firstName+" "+patientfamilymemberList?.get(pos)?.lastName)
            Glide.with(context)
                .load(BaseMediaUrls.USERIMAGE.url + (patientfamilymemberList?.get(pos)?.image))
                .into(itemView?.rootView?.img_patient_familymember_photo!!)

            if(!patientfamilymemberList?.get(pos)?.image.equals("") || patientfamilymemberList?.get(pos)?.image!=null){
                Glide.with(context)
                    .load(BaseMediaUrls.USERIMAGE.url + (patientfamilymemberList?.get(pos)?.image))
                    .into(itemView?.rootView?.img_patient_familymember_photo!!)

            }else{

                Glide.with(context)
                    .load(context.getResources().getDrawable(R.drawable.no_image))
                    .into(itemView?.rootView?.img_patient_familymember_photo!!)

            }


        }
    }

}