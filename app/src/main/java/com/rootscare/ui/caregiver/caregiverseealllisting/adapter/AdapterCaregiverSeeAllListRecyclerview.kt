package com.rootscare.ui.caregiver.caregiverseealllisting.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.OnClickWithTwoButton
import com.rootscare.R
import com.rootscare.data.model.api.response.caregiverallresponse.allcaregiverlistingresponse.ResultCareItem
import com.rootscare.databinding.ItemCaregiverCategoryListingRecyclerviewBinding
import com.rootscare.databinding.ItemSeeAllCaregiverListRecyclerviewBinding
import com.rootscare.ui.caregiver.caregivercategorylisting.adapter.AdapterCategoryListingRecyclerview
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_caregiver_category_listing_recyclerview.view.*
import kotlinx.android.synthetic.main.item_see_all_caregiver_list_recyclerview.view.*

class AdapterCaregiverSeeAllListRecyclerview(
    val allDoctorList: ArrayList<ResultCareItem?>?,
    internal var context: Context, var isGrid: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterCaregiverSeeAllListRecyclerview::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnClickWithTwoButton

    override fun getItemViewType(position: Int): Int {
        return if (isGrid)
            0
        else
            1
    }

    fun setType(isGrid: Boolean) {
        this.isGrid = isGrid
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val singleItemDashboardListingBinding =
//            DataBindingUtil.inflate<ItemSeeAllCaregiverListRecyclerviewBinding>(
//                LayoutInflater.from(context),
//                R.layout.item_see_all_caregiver_list_recyclerview, parent, false
//            )
//        return ViewHolder(singleItemDashboardListingBinding)

        return if (viewType == 0) {
            val singleItemDashboardListingBinding =
                DataBindingUtil.inflate<ItemSeeAllCaregiverListRecyclerviewBinding>(
                    LayoutInflater.from(context),
                    R.layout.item_see_all_caregiver_list_recyclerview, parent, false
                )
            ViewHolderGrid(singleItemDashboardListingBinding)
        } else {
            val itemSeeAllNursesByGridRecyclerviewBinding =
                DataBindingUtil.inflate<ItemCaregiverCategoryListingRecyclerviewBinding>(
                    LayoutInflater.from(context),
                    R.layout.item_caregiver_category_listing_recyclerview, parent, false
                )
            ViewHolderList(itemSeeAllNursesByGridRecyclerviewBinding)
        }
    }

    override fun getItemCount(): Int {
        return allDoctorList!!.size
//        return 10
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AdapterCaregiverSeeAllListRecyclerview.ViewHolderGrid -> holder.onBind(position)
            is AdapterCaregiverSeeAllListRecyclerview.ViewHolderList -> holder.onBind(position)
        }
    }


    inner class ViewHolderGrid(itemView: ItemSeeAllCaregiverListRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.crdview_seeall_caregiverlisting?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(localPosition, 1)
            }
            itemView.root.crdview_seeall_caregiverlisting?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(allDoctorList?.get(localPosition)?.userId?.toInt()!!)
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
            itemView.rootView?.txt_caregiver_name?.text =
                allDoctorList?.get(pos)?.firstName + " " + allDoctorList?.get(pos)?.lastName

            itemView.rootView?.txt_qualification?.text = allDoctorList?.get(pos)?.qualification
            itemView.rootView?.txt_description?.text = allDoctorList?.get(pos)?.description
            Glide.with(context)
                .load(BaseMediaUrls.USERIMAGE.url + (allDoctorList?.get(pos)?.image))
                .into(itemView.rootView?.image_caragiver!!)
        }
    }

    inner class ViewHolderList(itemView: ItemCaregiverCategoryListingRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.btn_rootscare_booking_caregiver?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(
                    localPosition,
                    allDoctorList?.get(localPosition)?.userId?.toInt()!!
                )
            }
            itemView.root.crdview_caregiver_category_list?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(allDoctorList?.get(localPosition)?.userId?.toInt()!!)
            }

        }

        fun onBind(pos: Int) {
            Log.d(AdapterCategoryListingRecyclerview.TAG, " true")
            localPosition = pos

            itemView.rootView?.txt_caregiver_list_name?.text =
                allDoctorList?.get(pos)?.firstName + " " + allDoctorList?.get(pos)?.lastName
            itemView.rootView?.txt_qualification?.text = allDoctorList?.get(pos)?.qualification
            itemView.rootView?.txt_description?.text = allDoctorList?.get(pos)?.description
            if (allDoctorList?.get(pos)?.image != null && !allDoctorList[pos]?.image.equals("")) {
                Glide.with(context)
                    .load(
                        BaseMediaUrls.USERIMAGE.url + (allDoctorList[pos]?.image)
                    )
                    .into(itemView.rootView?.img_caregiver_profilephoto!!)
            } else {
                Glide.with(context)
                    .load(R.drawable.no_image)
                    .into(itemView.rootView?.img_caregiver_profilephoto!!)
            }

        }
    }

}