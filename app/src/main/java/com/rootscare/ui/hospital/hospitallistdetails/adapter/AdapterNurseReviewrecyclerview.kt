package com.rootscare.ui.hospital.hospitallistdetails.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.databinding.ItemDoctorldetailsReviewlistRecyclewviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_doctorldetails_reviewlist_recyclewview.view.*

class AdapterNurseReviewrecyclerview(
    val reviewRatingList: ArrayList<com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.ReviewRatingItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterNurseReviewrecyclerview.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

//    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
//    internal lateinit var recyclerViewItemClickWithView: RecyclerViewItemClickWithView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemDoctorldetailsReviewlistRecyclewviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_doctorldetails_reviewlist_recyclewview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return reviewRatingList!!.size
//        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemDoctorldetailsReviewlistRecyclewviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            itemView.rootView?.txt_review_provider_name?.text =
                "Review By:" + " " + reviewRatingList?.get(
                    pos
                )?.reviewBy
            itemView.rootView?.txt_review?.text = reviewRatingList?.get(pos)?.review
            itemView.rootView?.ratingBardoctordetailseview?.rating =
                reviewRatingList?.get(pos)?.rating?.toFloat()!!
//            itemView?.rootView?.txt_home_babysitter_qualification?.setText(babysitterList?.get(pos)?.qualification)
//            Glide.with(context)
//                .load(BaseImageUrls.USERIMAGE.url + (babysitterList?.get(pos)?.image))
//                .into(itemView?.rootView?.img_babysitter_profile!!)


        }
    }

}