package com.rootscare.ui.nurses.nursescategorylisting.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.OnClickWithTwoButton
import com.rootscare.R
import com.rootscare.data.model.api.response.nurses.nurselist.ResultItem
import com.rootscare.databinding.ItemNursesCategorylistingRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_nurses_categorylisting_recyclerview.view.*

class AdapterNursesCtegoryListingRecyclerview(
    val nurseList: ArrayList<ResultItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterNursesCtegoryListingRecyclerview.ViewHolder>() {
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
            DataBindingUtil.inflate<ItemNursesCategorylistingRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_nurses_categorylisting_recyclerview, parent, false
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


    inner class ViewHolder(itemView: ItemNursesCategorylistingRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.btn_rootscare_booking_nurses?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(
                    localPosition,
                    nurseList?.get(localPosition)?.userId?.toInt()!!
                )
            }
//
            itemView.root.crdview_nurses_category_list?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(nurseList?.get(localPosition)?.userId?.toInt()!!)
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
            nurseFirstName = if (nurseList?.get(pos)?.firstName != null && !nurseList[pos]?.firstName.equals("")) {
                nurseList[pos]?.firstName!!
            } else {
                ""
            }
            nurseLastName = if (nurseList?.get(pos)?.lastName != null && !nurseList[pos]?.lastName.equals("")) {
                nurseList.get(pos)?.lastName!!
            } else {
                ""
            }

            itemView.rootView?.txt_nurse_name?.text = "$nurseFirstName $nurseLastName"
            if (nurseList?.get(pos)?.qualification != null && !nurseList[pos]?.qualification.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_nurse_qualification?.text = nurseList[pos]?.qualification
            } else {
                itemView.rootView?.txt_nurse_qualification?.text = ""
            }
            if (nurseList?.get(pos)?.description != null && !nurseList[pos]?.description.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_nurse_description?.text = nurseList[pos]?.description
            } else {
                itemView.rootView?.txt_nurse_description?.text = ""
            }

            if (nurseList?.get(pos)?.avgRating != null && !nurseList[pos]?.avgRating.equals("")) {
                itemView.rootView?.ratingBardoctordetailseview?.rating =
                    nurseList[pos]?.avgRating?.toFloat()!!
            }
            if (nurseList?.get(pos)?.totalReviewRating != null && !nurseList[pos]?.totalReviewRating.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_nurselist_noofreview?.text =
                    nurseList.get(pos)?.totalReviewRating + " " + "reviews"
            } else {
                itemView.rootView?.txt_nurselist_noofreview?.text = ""
            }
            if (nurseList?.get(pos)?.image != null && !nurseList[pos]?.image.equals("")) {
                Glide.with(context)
                    .load(BaseMediaUrls.USERIMAGE.url + (nurseList[pos]?.image))
                    .into(itemView.rootView?.img_nurselisting_profilephoto!!)
            } else {
                Glide.with(context)
                    .load(R.drawable.no_image)
                    .into(itemView.rootView?.img_nurselisting_profilephoto!!)
            }

        }
    }

}