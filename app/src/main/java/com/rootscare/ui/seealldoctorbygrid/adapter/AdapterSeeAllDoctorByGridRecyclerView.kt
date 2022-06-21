package com.rootscare.ui.seealldoctorbygrid.adapter

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
import com.rootscare.databinding.ItemRootscareDoctorCategorilistingRecyclerviewBinding
import com.rootscare.databinding.ItemSeeAllDoctorByGridRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_rootscare_doctor_categorilisting_recyclerview.view.*
import kotlinx.android.synthetic.main.item_see_all_doctor_by_grid_recyclerview.view.*

class AdapterSeeAllDoctorByGridRecyclerView(
    val allDoctorList: ArrayList<ResultItem?>?,
    internal var context: Context, var isGrid: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
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
//            DataBindingUtil.inflate<ItemSeeAllDoctorByGridRecyclerviewBinding>(
//                LayoutInflater.from(context),
//                R.layout.item_see_all_doctor_by_grid_recyclerview, parent, false
//            )
//        return ViewHolder(singleItemDashboardListingBinding)

        return if (viewType == 0) {
            val singleItemDashboardListingBinding =
                DataBindingUtil.inflate<ItemSeeAllDoctorByGridRecyclerviewBinding>(
                    LayoutInflater.from(context),
                    R.layout.item_see_all_doctor_by_grid_recyclerview, parent, false
                )
            ViewHolderGrid(singleItemDashboardListingBinding)
        } else {
            val itemSeeAllDoctorByGridRecyclerviewBinding =
                DataBindingUtil.inflate<ItemRootscareDoctorCategorilistingRecyclerviewBinding>(
                    LayoutInflater.from(context),
                    R.layout.item_rootscare_doctor_categorilisting_recyclerview, parent, false
                )
            ViewHolderList(itemSeeAllDoctorByGridRecyclerviewBinding)
        }
    }


    override fun getItemCount(): Int {
        return allDoctorList!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AdapterSeeAllDoctorByGridRecyclerView.ViewHolderGrid -> holder.onBind(position)
            is AdapterSeeAllDoctorByGridRecyclerView.ViewHolderList -> holder.onBind(position)
        }
    }


    inner class ViewHolderGrid(itemView: ItemSeeAllDoctorByGridRecyclerviewBinding) :
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
            Glide.with(context).load( BaseMediaUrls.USERIMAGE.url + (allDoctorList?.get( pos)?.image)
            ).into(itemView.rootView?.img_all_doctorlist_image!!)
        }
    }

    inner class ViewHolderList(itemView: ItemRootscareDoctorCategorilistingRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
//            itemView.root.crdview_doctor_category_list?.setOnClickListener {
//                recyclerViewItemClickWithView.onFirstItemClick(localPosition, 1)
//            }
            itemView.root.crdview_doctor_category_list?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(allDoctorList?.get(localPosition)?.userId?.toInt()!!)
            }


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            itemView.rootView?.txt_all_doctor_name?.text =
                allDoctorList?.get(pos)?.firstName + " " + allDoctorList?.get(pos)?.lastName
            itemView.rootView?.txt_all_doctor_qualification?.text =
                allDoctorList?.get(pos)?.qualification
            itemView.rootView?.txt_all_doctor_description?.text =
                allDoctorList?.get(pos)?.description
            Glide.with(context).load(
                BaseMediaUrls.USERIMAGE.url + (allDoctorList?.get(
                    pos
                )?.image)
            ).into(itemView.rootView?.img_all_doctor_image_list!!)
        }
    }

}