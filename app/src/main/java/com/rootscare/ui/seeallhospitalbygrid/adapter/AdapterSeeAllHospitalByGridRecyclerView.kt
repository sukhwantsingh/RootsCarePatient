package com.rootscare.ui.seeallhospitalbygrid.adapter

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
import com.rootscare.databinding.ItemSeeAllDoctorByGridRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_hospital_categorylist_recyclerview.view.*
import kotlinx.android.synthetic.main.item_see_all_doctor_by_grid_recyclerview.view.*
import kotlinx.android.synthetic.main.item_see_all_doctor_by_grid_recyclerview.view.crdview_seeall_doctorlisting

class AdapterSeeAllHospitalByGridRecyclerView(
    val allDoctorList: ArrayList<HospitalResultItem?>?,
    internal var context: Context,
    var isGrid: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 0) {
            val singleItemDashboardListingBinding =
                DataBindingUtil.inflate<ItemSeeAllDoctorByGridRecyclerviewBinding>(
                    LayoutInflater.from(context),
                    R.layout.item_see_all_doctor_by_grid_recyclerview, parent, false
                )
            ViewHolderGrid(singleItemDashboardListingBinding)
        } else {
            val itemHospitalCategorylistRecyclerviewBinding =
                DataBindingUtil.inflate<ItemHospitalCategorylistRecyclerviewBinding>(
                    LayoutInflater.from(context),
                    R.layout.item_hospital_categorylist_recyclerview, parent, false
                )
            ViewHolderList(itemHospitalCategorylistRecyclerviewBinding)
        }
    }

    override fun getItemCount(): Int {
        return allDoctorList!!.size
//        return 10
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderGrid -> holder.onBind(position)
            is ViewHolderList -> holder.onBind(position)
        }
    }

    fun setType(isGrid: Boolean) {
        this.isGrid = isGrid
        notifyDataSetChanged()
    }


    inner class ViewHolderGrid(itemView: ItemSeeAllDoctorByGridRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.crdview_seeall_doctorlisting?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(localPosition, 1)
            }
            itemView.root.crdview_seeall_doctorlisting?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(allDoctorList?.get(localPosition)?.user_id?.toInt()!!)
            }


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            itemView.rootView?.txt_all_doctorlist_name?.text = allDoctorList?.get(pos)?.name
            itemView.rootView?.txt_all_doctorlist_qualification?.text =
                allDoctorList?.get(pos)?.address
            // itemView?.rootView?.txt_all_doctorlist_description?.setText(allDoctorList?.get(pos)?.description)
            Glide.with(context)
                .load(BaseMediaUrls.USERIMAGE.url + (allDoctorList?.get(pos)?.image)  )
                .into(itemView.rootView?.img_all_doctorlist_image!!)
        }
    }

    inner class ViewHolderList(itemView: ItemHospitalCategorylistRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.crdview_hospital_category_list?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(localPosition, 1)
            }
            itemView.root.crdview_hospital_category_list?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(allDoctorList?.get(localPosition)?.user_id?.toInt()!!)
            }


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            itemView.rootView?.name?.text = allDoctorList?.get(pos)?.name
            itemView.rootView?.address?.text =
                allDoctorList?.get(pos)?.address

            itemView.rootView?.ratingBardoctordetailseview?.rating =
                allDoctorList?.get(pos)?.rating ?: 0f

            if (allDoctorList?.get(pos)?.reviews ?: 0 == 0) {
                itemView.rootView?.txt_doctordetails_noofreview?.text = "No Reviews"
            } else {
                itemView.rootView?.txt_doctordetails_noofreview?.text =
                    "${allDoctorList?.get(pos)?.reviews ?: 0} Reviews"
            }
            itemView.rootView?.tv_departments?.text =
                "DEPARTMENTS: ${allDoctorList?.get(pos)?.departments ?: ""}"
            Glide.with(context)
                .load(
                    BaseMediaUrls.USERIMAGE.url + (allDoctorList?.get(
                        pos
                    )?.image)
                )
                .into(itemView.rootView?.img_doctordetails_profilephoto!!)
        }
    }

}