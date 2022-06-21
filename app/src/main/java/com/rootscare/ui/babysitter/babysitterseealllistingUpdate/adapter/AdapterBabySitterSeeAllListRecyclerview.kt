package com.rootscare.ui.babysitter.babysitterseealllistingUpdate.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.OnClickWithTwoButton
import com.rootscare.R
import com.rootscare.data.model.api.response.caregiverallresponse.allcaregiverlistingresponse.ResultCareItem
import com.rootscare.databinding.ItemBabysitterCategorylistingRecyclerviewBinding
import com.rootscare.databinding.ItemSeeAllCaregiverListRecyclerviewBinding
import com.rootscare.ui.babysitter.babysittercategorylistingUpdate.adapter.AdapterCategoryListingRecyclerviewBibySitter
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_babysitter_categorylisting_recyclerview.view.*
import kotlinx.android.synthetic.main.item_see_all_caregiver_list_recyclerview.view.*

class AdapterBabySitterSeeAllListRecyclerview(
    val allDoctorList: ArrayList<ResultCareItem?>?,
    internal var context: Context, var isGrid: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var nurseFirstName = ""
    var nurseLastName = ""

    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterBabySitterSeeAllListRecyclerview::class.java.simpleName
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
                DataBindingUtil.inflate<ItemBabysitterCategorylistingRecyclerviewBinding>(
                    LayoutInflater.from(context),
                    R.layout.item_babysitter_categorylisting_recyclerview, parent, false
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
            is AdapterBabySitterSeeAllListRecyclerview.ViewHolderGrid -> holder.onBind(position)
            is AdapterBabySitterSeeAllListRecyclerview.ViewHolderList -> holder.onBind(position)
        }
    }


    inner class ViewHolderGrid(itemView: ItemSeeAllCaregiverListRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.crdview_seeall_caregiverlisting?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(localPosition, 1)
            }
            itemView.root.crdview_seeall_caregiverlisting?.setOnClickListener(View.OnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(allDoctorList?.get(localPosition)?.userId?.toInt()!!)
            })


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
            itemView.rootView?.txt_caregiver_name?.text =
                allDoctorList?.get(pos)?.firstName + " " + allDoctorList?.get(pos)?.lastName

            itemView.rootView?.txt_qualification?.text = allDoctorList?.get(pos)?.qualification
            itemView.rootView?.txt_description?.text = allDoctorList?.get(pos)?.description
            Glide.with(context).load(
                BaseMediaUrls.USERIMAGE.url + (allDoctorList?.get(pos)?.image)
            ).into(itemView.rootView?.image_caragiver!!)
        }
    }

    inner class ViewHolderList(itemView: ItemBabysitterCategorylistingRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.btn_rootscare_booking_babysitter?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(
                    localPosition,
                    allDoctorList?.get(localPosition)?.userId?.toInt()!!
                )
            }
//
            itemView.root.crdview_babysitter_category_list?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(allDoctorList?.get(localPosition)?.userId?.toInt()!!)
            }

        }

        fun onBind(pos: Int) {
            Log.d(AdapterCategoryListingRecyclerviewBibySitter.TAG, " true")
            localPosition = pos
            nurseFirstName =
                if (allDoctorList?.get(pos)?.firstName != null && !allDoctorList[pos]?.firstName.equals(
                        ""
                    )
                ) {
                    allDoctorList.get(pos)?.firstName!!
                } else {
                    ""
                }
            nurseLastName =
                if (allDoctorList?.get(pos)?.lastName != null && !allDoctorList[pos]?.lastName.equals(
                        ""
                    )
                ) {
                    allDoctorList[pos]?.lastName!!
                } else {
                    ""
                }

            itemView.rootView?.txt_name?.text = "$nurseFirstName $nurseLastName"
            if (allDoctorList?.get(pos)?.qualification != null && !allDoctorList[pos]?.qualification.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_qulificationi?.text = allDoctorList[pos]?.qualification
            } else {
                itemView.rootView?.txt_qulificationi?.text = ""
            }
            if (allDoctorList?.get(pos)?.description != null && !allDoctorList[pos]?.description.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_desc?.text = allDoctorList[pos]?.description
            } else {
                itemView.rootView?.txt_desc?.text = ""
            }

            if (allDoctorList?.get(pos)?.image != null && !allDoctorList[pos]?.image.equals("")) {
                Glide.with(context)
                    .load(BaseMediaUrls.USERIMAGE.url + (allDoctorList[pos]?.image))
                    .into(itemView.rootView?.img_doctordetails_profilephoto!!)
            } else {
                Glide.with(context)
                    .load(R.drawable.no_image)
                    .into(itemView.rootView?.img_doctordetails_profilephoto!!)
            }

        }
    }

}