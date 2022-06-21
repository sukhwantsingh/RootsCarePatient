package com.rootscare.ui.medicalrecords.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnMedicalRecordFileDeleteListner
import com.rootscare.R
import com.rootscare.data.model.api.response.medicalrecordresponse.ResultItem
import com.rootscare.data.model.api.response.medicalrecordresponse.UploadDocumentItem
import com.rootscare.databinding.ItemMedicalRecordsRecyclerviewListBinding
import com.rootscare.databinding.ItemViewPresprictionRecyclerviewBinding
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.ui.viewprescription.adapter.AdapterViewPrescriptionRecyclerview
import kotlinx.android.synthetic.main.item_medical_records_recyclerview_list.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterMedicalRecordsRecyclerview(
    val medicalrecordlist: ArrayList<ResultItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterMedicalRecordsRecyclerview.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnMedicalRecordFileDeleteListner
    var fileId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemMedicalRecordsRecyclerviewListBinding>(
                LayoutInflater.from(context),
                R.layout.item_medical_records_recyclerview_list, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
//        return trainerList!!.size
        return medicalrecordlist?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemMedicalRecordsRecyclerviewListBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var local_position: Int = 0

        init {
//            itemView?.root?.crdview_appoitment_list?.setOnClickListener(View.OnClickListener {
//                recyclerViewItemClickWithView?.onItemClick(1)
//            })


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos
            if (medicalrecordlist?.get(pos)?.date != null && !medicalrecordlist[pos]?.date.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_medicalreport_upload_date?.text = formateDateFromstring(
                    "yyyy-MM-dd",
                    "dd MMM yyyy",
                    medicalrecordlist[pos]?.date
                )
            } else {
                itemView.rootView?.txt_medicalreport_upload_date?.text = ""
            }
            if (medicalrecordlist?.get(pos)?.uploadDocument != null && medicalrecordlist.get(pos)?.uploadDocument?.size!! > 0) {
                setUpViewForUploadedDocument(medicalrecordlist[pos]?.uploadDocument)
            }


//


        }

        private fun setUpViewForUploadedDocument(uploadDocumentlist: ArrayList<UploadDocumentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
            assert(itemView.rootView?.recyclerview_get_upload_file_list != null)
            val recyclerView = itemView.rootView?.recyclerview_get_upload_file_list
            val gridLayoutManager =
                GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
            recyclerView?.layoutManager = gridLayoutManager
            recyclerView?.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
            val contactListAdapter = AdapterFileUpload(uploadDocumentlist, context)
            recyclerView?.adapter = contactListAdapter
            contactListAdapter.recyclerViewItemClickWithView =
                object : OnMedicalRecordFileDeleteListner {
                    override fun onDelectClick(id: String) {
                        fileId = id
                        recyclerViewItemClickWithView.onDelectClick(fileId)
                    }

                }


        }

        fun formateDateFromstring(
            inputFormat: String?,
            outputFormat: String?,
            inputDate: String?
        ): String? {
            var parsed: Date? = null
            var outputDate = ""
            val df_input =
                SimpleDateFormat(inputFormat, Locale.ENGLISH)
            val df_output =
                SimpleDateFormat(outputFormat, Locale.ENGLISH)
            try {
                parsed = df_input.parse(inputDate)
                outputDate = df_output.format(parsed)
            } catch (e: ParseException) {
            }
            return outputDate
        }
    }

}