package com.rootscare.ui.viewPathologyReport.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.R
import com.rootscare.data.model.api.response.patientReport.ResultItem
import com.rootscare.databinding.ItemViewPresprictionRecyclerviewBinding
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.ui.showimagelarger.TransaprentPopUpActivityForImageShow
import com.rootscare.utils.AnimUtils
import com.rootscare.utils.AppConstants
import kotlinx.android.synthetic.main.item_view_prespriction_recyclerview.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterViewReportRecyclerView(
    val prescriptionList: ArrayList<ResultItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterViewReportRecyclerView.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnItemClikWithIdListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemViewPresprictionRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_view_prespriction_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
//        return trainerList!!.size
        return prescriptionList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemViewPresprictionRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
//            itemView?.root?.crdview_appoitment_list?.setOnClickListener(View.OnClickListener {
//                recyclerViewItemClickWithView?.onItemClick(1)
//            })
//

            if (prescriptionList?.get(localPosition)?.eReport?.toLowerCase(Locale.ROOT)!!
                    .contains("pdf")
            ) {

                itemView.root.crdview_view_prescription?.setOnClickListener {
                    AnimUtils.animateIntent(
                        context as HomeActivity,
                        itemView.root.crdview_view_prescription, null,
                        TransaprentPopUpActivityForImageShow.newIntent(
                            context as HomeActivity,
                            prescriptionList[localPosition]?.eReport!!, "pdf"
                        ),
                        AppConstants.REQUEST_RESULT_CODE_FOR_TRANSITION_ANIM_LABLISTING_TO_POPUPIMAGESHOW
                    )
                }

            } else {
                itemView.root.crdview_view_prescription?.setOnClickListener {
                    AnimUtils.animateIntent(
                        context as HomeActivity,
                        itemView.root.crdview_view_prescription, null,
                        TransaprentPopUpActivityForImageShow.newIntent(
                            context as HomeActivity,
                            prescriptionList[localPosition]?.eReport!!, "image"
                        ),
                        AppConstants.REQUEST_RESULT_CODE_FOR_TRANSITION_ANIM_LABLISTING_TO_POPUPIMAGESHOW
                    )
                }
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            if (prescriptionList?.get(pos)?.reportNumber != null && !prescriptionList[pos]?.reportNumber.equals(
                    ""
                )
            ) {
                itemView.txt_description?.text = prescriptionList[pos]?.reportNumber
            } else {
                itemView.txt_description?.text = ""
            }
            if (prescriptionList?.get(pos)?.createdDate != null && !prescriptionList[pos]?.createdDate.equals(
                    ""
                )
            ) {
                itemView.txt_prescription_date?.text = formatDateFromString(
                    "yyyy-MM-dd", "dd MMM yyyy",
                    prescriptionList[pos]?.createdDate
                )
            } else {
                itemView.txt_prescription_date?.text = ""
            }
            if (!prescriptionList?.get(localPosition)?.eReport?.toLowerCase(Locale.ROOT)!!
                    .contains("pdf")
            ) {
                Glide.with(context)
                    .load(prescriptionList[localPosition]?.eReport)
                    .into(itemView.rootView?.img_contentimage!!)
            } else {
                Glide.with(context)
                    .load(R.drawable.pdf_file_logo)
                    .into(itemView.rootView?.img_contentimage!!)
            }


        }

        private fun formatDateFromString(
            inputFormat: String?,
            outputFormat: String?,
            inputDate: String?
        ): String {
            val parsed: Date?
            var outputDate = ""
            val df_input =
                SimpleDateFormat(inputFormat, Locale.ENGLISH)
            val df_output =
                SimpleDateFormat(outputFormat, Locale.ENGLISH)
            try {
                parsed = df_input.parse(inputDate)
                outputDate = df_output.format(parsed)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return outputDate
        }

    }
}