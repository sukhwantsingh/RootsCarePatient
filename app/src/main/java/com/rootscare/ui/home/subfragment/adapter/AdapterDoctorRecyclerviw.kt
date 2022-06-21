package com.rootscare.ui.home.subfragment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rootscare.R
import com.rootscare.data.model.api.response.patienthome.DoctorItem
import com.rootscare.databinding.ItemHomeDoctorRecyclerviewBinding
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_home_doctor_recyclerview.view.*

class AdapterDoctorRecyclerviw(
    val doctorList: ArrayList<DoctorItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterDoctorRecyclerviw.ViewHolder>() {
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    internal lateinit var recyclerViewItemClickWithView: OnItemClikWithIdListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemHomeDoctorRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_home_doctor_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return doctorList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: ItemHomeDoctorRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        private var localPosition: Int = 0

        init {
            itemView.root.crdview_home_doctor?.setOnClickListener {
                recyclerViewItemClickWithView.onItemClick(doctorList?.get(localPosition)?.userId?.toInt()!!)
            }
        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
            itemView.rootView?.txt_home_doctor_name?.text =
                doctorList?.get(pos)?.firstName + " " + doctorList?.get(pos)?.lastName
            itemView.rootView?.txt_home_doctor_qualification?.text =
                doctorList?.get(pos)?.qualification
            Glide.with(context)
                .load(
                    BaseMediaUrls.USERIMAGE.url + (doctorList?.get(
                        pos
                    )?.image)
                )
                .into(itemView.rootView?.img_home_doctorprofile!!)
        }
    }

}