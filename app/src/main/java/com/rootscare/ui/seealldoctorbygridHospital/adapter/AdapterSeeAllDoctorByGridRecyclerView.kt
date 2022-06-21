package com.rootscare.ui.seealldoctorbygridHospital.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.OnClickWithTwoButton
import com.rootscare.R
import com.rootscare.data.model.api.response.doctorallapiresponse.alldoctorlistingresponse.ResultItem
import com.rootscare.databinding.ItemSeeAllDoctorByGridRecyclerviewBinding
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_see_all_doctor_by_grid_recyclerview.view.*

class AdapterSeeAllDoctorHospitalByGridRecyclerView(
    val allDoctorList: ArrayList<ResultItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterSeeAllDoctorHospitalByGridRecyclerView.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String =
            com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnClickWithTwoButton

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemSeeAllDoctorByGridRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_see_all_doctor_by_grid_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return allDoctorList!!.size
//        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemSeeAllDoctorByGridRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.crdview_seeall_doctorlisting?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(localPosition, 1)
            }
            itemView.root.crdview_seeall_doctorlisting?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(allDoctorList?.get(localPosition)?.userId?.toInt()!!)
            }


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            itemView.rootView?.txt_all_doctorlist_name?.text =
                allDoctorList?.get(pos)?.firstName + " " + allDoctorList?.get(pos)?.lastName
            itemView.rootView?.txt_all_doctorlist_qualification?.text =
                allDoctorList?.get(pos)?.qualification
            itemView.rootView?.txt_all_doctorlist_description?.text =
                allDoctorList?.get(pos)?.description
            Glide.with(context)
                .load( BaseMediaUrls.USERIMAGE.url + (allDoctorList?.get(
                        pos)?.image))
                .into(itemView.rootView?.img_all_doctorlist_image!!)
        }
    }

}