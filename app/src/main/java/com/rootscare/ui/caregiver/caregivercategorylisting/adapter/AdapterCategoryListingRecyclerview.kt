package com.rootscare.ui.caregiver.caregivercategorylisting.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.OnClickWithTwoButton
import com.rootscare.R
import com.rootscare.data.model.api.response.caregiverallresponse.caregiverlist.CaregiverResultItem
import com.rootscare.databinding.ItemCaregiverCategoryListingRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_caregiver_category_listing_recyclerview.view.*

class AdapterCategoryListingRecyclerview(
    val allDoctorList: ArrayList<CaregiverResultItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterCategoryListingRecyclerview.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnClickWithTwoButton
    var nurseFirstName = ""
    var nurseLastName = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemCaregiverCategoryListingRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_caregiver_category_listing_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
//        return trainerList!!.size
        return allDoctorList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemCaregiverCategoryListingRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.btn_rootscare_booking_caregiver?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(
                    localPosition,
                    allDoctorList?.get(localPosition)?.userId?.toInt()!!
                )
            }
//
            itemView.root.crdview_caregiver_category_list?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(allDoctorList?.get(localPosition)?.userId?.toInt()!!)
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
            if (allDoctorList?.get(pos)?.firstName != null && !allDoctorList[pos]?.firstName.equals(
                    ""
                )
            ) {
                nurseFirstName = allDoctorList[pos]?.firstName!!
            } else {
                nurseFirstName = ""
            }
            if (allDoctorList?.get(pos)?.lastName != null && !allDoctorList[pos]?.lastName.equals(
                    ""
                )
            ) {
                nurseLastName = allDoctorList.get(pos)?.lastName!!
            } else {
                nurseLastName = ""
            }

            itemView.rootView?.txt_caregiver_list_name?.text = "$nurseFirstName $nurseLastName"
            if (allDoctorList?.get(pos)?.qualification != null && !allDoctorList[pos]?.qualification.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_caregive_qualification?.text =
                    allDoctorList[pos]?.qualification
            } else {
                itemView.rootView?.txt_caregive_qualification?.text = ""
            }
            if (allDoctorList?.get(pos)?.description != null && !allDoctorList[pos]?.description.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_caregiver_desc?.text = allDoctorList[pos]?.description
            } else {
                itemView.rootView?.txt_caregiver_desc?.text = ""
            }

            if (allDoctorList?.get(pos)?.avgRating != null && !allDoctorList[pos]?.avgRating.equals(
                    ""
                )
            ) {
                itemView.rootView?.ratingcaregiverdetailseview?.rating =
                    allDoctorList[pos]?.avgRating?.toFloat()!!
            }
            if (allDoctorList?.get(pos)?.totalReviewRating != null && !allDoctorList[pos]?.totalReviewRating.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_caregiverdetails_noofreview?.text =
                    allDoctorList[pos]?.totalReviewRating + " " + "reviews"
            } else {
                itemView.rootView?.txt_caregiverdetails_noofreview?.text = ""
            }
            if (allDoctorList?.get(pos)?.image != null && !allDoctorList[pos]?.image.equals("")) {
                Glide.with(context)
                    .load(BaseMediaUrls.USERIMAGE.url + (allDoctorList[pos]?.image))
                    .into(itemView.rootView?.img_caregiver_profilephoto!!)
            } else {
                Glide.with(context)
                    .load(R.drawable.no_image)
                    .into(itemView.rootView?.img_caregiver_profilephoto!!)
            }

        }
    }


}