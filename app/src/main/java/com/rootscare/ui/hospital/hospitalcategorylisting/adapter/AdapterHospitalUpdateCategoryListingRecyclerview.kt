package com.rootscare.ui.hospital.hospitalcategorylisting.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.OnClickWithTwoButton
import com.rootscare.R
import com.rootscare.data.model.api.response.hospitalallapiresponse.allhospitalistingresponse.HospitalResultItem
import com.rootscare.databinding.ItemHospitalCategorylistRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_hospital_categorylist_recyclerview.view.*

class AdapterHospitalUpdateCategoryListingRecyclerview(
    val nurseList: ArrayList<HospitalResultItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterHospitalUpdateCategoryListingRecyclerview.ViewHolder>() {
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
            DataBindingUtil.inflate<ItemHospitalCategorylistRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_hospital_categorylist_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
//        return trainerList!!.size
        return nurseList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemHospitalCategorylistRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.btn_rootscare_booking_hospital?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(
                    localPosition,
                    nurseList?.get(localPosition)?.user_id?.toInt()!!
                )
            }
//
            itemView.root.crdview_hospital_category_list?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(nurseList?.get(localPosition)?.user_id?.toInt()!!)
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
            nurseFirstName =
                if (nurseList?.get(pos)?.name != null && !nurseList[pos]?.name.equals("")) {
                    nurseList[pos]?.name!!
                } else {
                    ""
                }


            itemView.rootView?.name?.text = "$nurseFirstName $nurseLastName"
            if (nurseList?.get(pos)?.address != null && !nurseList[pos]?.address.equals("")) {
                itemView.rootView?.address?.text = nurseList[pos]?.address
            } else {
                itemView.rootView?.address?.text = ""
            }
            /* if(nurseList?.get(pos)?.description!=null && !nurseList?.get(pos)?.description.equals("")){
                 itemView?.rootView?.txt_nurse_description?.setText(nurseList?.get(pos)?.description)
             }else{
                 itemView?.rootView?.txt_nurse_description?.setText("")
             }

             if(nurseList?.get(pos)?.avgRating!=null && !nurseList?.get(pos)?.avgRating.equals("")) {
                 itemView?.rootView?.ratingBardoctordetailseview?.rating =
                     nurseList?.get(pos)?.avgRating?.toFloat()!!
             }
             if (nurseList?.get(pos)?.totalReviewRating!=null && !nurseList?.get(pos)?.totalReviewRating.equals("")){
                 itemView?.rootView?.txt_nurselist_noofreview?.setText(nurseList?.get(pos)?.totalReviewRating+" "+"reviews")
             }else{
                 itemView?.rootView?.txt_nurselist_noofreview?.setText("")
             }*/
            if (nurseList?.get(pos)?.image != null && !nurseList[pos]?.image.equals("")) {
                Glide.with(context)
                    .load(
                        BaseMediaUrls.USERIMAGE.url + (nurseList[pos]?.image)
                    )
                    .into(itemView.rootView?.img_doctordetails_profilephoto!!)
            } else {
                Glide.with(context)
                    .load(R.drawable.no_image)
                    .into(itemView.rootView?.img_doctordetails_profilephoto!!)
            }

        }
    }

}