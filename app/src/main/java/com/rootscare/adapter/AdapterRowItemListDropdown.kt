package com.rootscare.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnDropDownListItemClickListener
import com.interfaces.OnRowItemDropdownItemClick
import com.rootscare.R
import com.rootscare.databinding.ItemCommonDropdownListBinding
import com.model.RowItem
import kotlinx.android.synthetic.main.item_common_dropdown_list.view.*

class AdapterRowItemListDropdown(val dropdownList: ArrayList<RowItem?>?, internal var context: Context) : RecyclerView.Adapter<AdapterRowItemListDropdown.ViewHolder>() {
    //
    companion object {
        val TAG: String = AdapterCommonDropdown::class.java.simpleName
    }

    internal lateinit var recyclerViewItemClick: OnRowItemDropdownItemClick
//
//        internal lateinit var recyclerViewItemClickWithView: OnClickWithTwoButton

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding = DataBindingUtil.inflate<ItemCommonDropdownListBinding>(
            LayoutInflater.from(context),
            R.layout.item_common_dropdown_list, parent, false)
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return dropdownList!!.size
//            return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemCommonDropdownListBinding) : RecyclerView.ViewHolder(itemView.root) {

        private var local_position:Int = 0
        init {
            itemView?.root?.ll_dropdown_list?.setOnClickListener(View.OnClickListener {
                recyclerViewItemClick?.onConfirm(dropdownList?.get(local_position)?.title_item!!,
                    dropdownList?.get(local_position)?.item_id!!
                )
            })
////
//                itemView?.root?.crdview_hospital_category_list?.setOnClickListener(View.OnClickListener {
//                    recyclerViewItemClickWithView?.onSecondItemClick(1)
//                })
//            itemView?.root?.btn_view_trainner_profile?.setOnClickListener(View.OnClickListener {
//                recyclerViewItemClickWithView?.onItemClick(trainerList?.get(local_position)?.id?.toInt()!!)
//            })

//            itemView.root?.img_bid?.setOnClickListener {
//                run {
//                    recyclerViewItemClick?.onClick(itemView.root?.img_bid,local_position)
//                    //serviceListItem?.get(local_position)?.requestid?.let { it1 -> recyclerViewItemClick.onClick(itemView.root?.img_bid,it1) }
//                }
//            }
//
//            itemView.root?.img_details?.setOnClickListener {
//                run {
//                    recyclerViewItemClick?.onClick(itemView.root?.img_details,local_position)
//                    // serviceListItem?.get(local_position)?.requestid?.let { it1 -> recyclerViewItemClick.onClick(itemView.root?.img_details,it1) }
//                }
//            }


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos
            itemView?.rootView?.txtDropDownListLabel?.setText(dropdownList?.get(local_position)?.title_item)

//            itemView?.rootView?.txt_teacher_name?.text= trainerList?.get(pos)?.name
//            itemView?.rootView?.txt_teacher_qualification?.text= "Qualification : "+" "+trainerList?.get(pos)?.qualification
//            if(trainerList?.get(pos)?.avgRating!=null && !trainerList?.get(pos)?.avgRating.equals("")){
//                itemView?.rootView?.ratingBarteacher?.rating= trainerList?.get(pos)?.avgRating?.toFloat()!!
//            }





//            itemView?.rootView?.txt_rating_count?.text="("+contactListItem?.get(pos)?.contactRating+")"
//            (contactListItem?.get(pos)?.contactRating)?.toFloat()?.let { itemView?.rootView?.ratingBar?.setRating(it) }
////            itemView?.rootView?.ratingBar?.rating=1.5f


        }
    }

}