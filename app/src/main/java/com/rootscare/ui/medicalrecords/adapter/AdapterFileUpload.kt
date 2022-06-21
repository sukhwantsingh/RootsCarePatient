package com.rootscare.ui.medicalrecords.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.interfaces.OnMedicalRecordFileDeleteListner
import com.rootscare.R
import com.rootscare.data.model.api.response.medicalrecordresponse.UploadDocumentItem
import com.rootscare.databinding.ItemMedicalRecordsFileUploadBinding
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.ui.showimagelarger.TransaprentPopUpActivityForImageShow
import com.rootscare.utilitycommon.BaseMediaUrls
import com.rootscare.utils.AnimUtils
import com.rootscare.utils.AppConstants
import kotlinx.android.synthetic.main.item_medical_records_file_upload.view.*
import java.util.*
import kotlin.collections.ArrayList

class AdapterFileUpload(
    val uploadDocumentlist: ArrayList<UploadDocumentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterFileUpload.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnMedicalRecordFileDeleteListner

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemMedicalRecordsFileUploadBinding>(
                LayoutInflater.from(context),
                R.layout.item_medical_records_file_upload, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
//        return trainerList!!.size
        return uploadDocumentlist?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemMedicalRecordsFileUploadBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.img_file_delete?.setOnClickListener {
                recyclerViewItemClickWithView.onDelectClick(uploadDocumentlist?.get(localPosition)?.id!!)
            }


            if (uploadDocumentlist?.get(localPosition)?.file?.toLowerCase(Locale.ROOT)!!
                    .contains("pdf")
            ) {
                itemView.root.imageview_files_type?.setOnClickListener {
                    AnimUtils.animateIntent(
                        context as HomeActivity, itemView.root.imageview_files_type, null,
                        TransaprentPopUpActivityForImageShow.newIntent(
                            context as HomeActivity,BaseMediaUrls.USERIMAGE.url + uploadDocumentlist.get(localPosition)?.file!!, "pdf"
                        ),
                        AppConstants.REQUEST_RESULT_CODE_FOR_TRANSITION_ANIM_LABLISTING_TO_POPUPIMAGESHOW
                    )
                }
            } else {

                val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                Glide.with(context).load(
                    BaseMediaUrls.USERIMAGE.url + uploadDocumentlist.get(
                        localPosition
                    )?.file!!
                ).thumbnail(
                    Glide.with(context).load(
                        BaseMediaUrls.USERIMAGE.url + uploadDocumentlist.get(
                            localPosition
                        )?.file!!
                    )
                ).apply(
                    RequestOptions().placeholder(R.drawable.prescription)
                        .error(R.drawable.prescription)
                ).apply(requestOptions).into(itemView.root.imageview_files_type)
                itemView.root.imageview_files_type.setOnClickListener {
                    AnimUtils.animateIntent(
                        context as HomeActivity, itemView.root.imageview_files_type, null,
                        TransaprentPopUpActivityForImageShow.newIntent(
                            context as HomeActivity,
                            BaseMediaUrls.USERIMAGE.url + uploadDocumentlist.get(
                                localPosition
                            )?.file!!,
                            "image"
                        ),
                        AppConstants.REQUEST_RESULT_CODE_FOR_TRANSITION_ANIM_LABLISTING_TO_POPUPIMAGESHOW
                    )
                }
//                itemView?.root?.imageview_files_type?.setOnClickListener(View.OnClickListener {
//                    AnimUtils.animateIntent(context as HomeActivity, itemView?.root?.imageview_files_type, null,
//                        TransaprentPopUpActivityForImageShow.newIntent(context as HomeActivity,
//                            "http://166.62.54.122/rootscare/uploads/images/"+uploadDocumentlist?.get(local_position)?.file!!, "image"),
//                        AppConstants.REQUEST_RESULT_CODE_FOR_TRANSITION_ANIM_LABLISTING_TO_POPUPIMAGESHOW)
//                })
            }


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
            if (uploadDocumentlist?.get(pos)?.title != null && !uploadDocumentlist.get(pos)?.title.equals(
                    ""
                )
            ) {
                itemView.rootView?.tv_file_name?.text = uploadDocumentlist.get(pos)?.title
            } else {
                itemView.rootView?.tv_file_name?.text = ""
            }

            if (uploadDocumentlist?.get(localPosition)?.file?.toLowerCase(Locale.ROOT)!!
                    .contains("pdf")
            ) {

                Glide.with(context)
                    .load(R.drawable.pdf_file_logo)
                    .into(itemView.rootView?.imageview_files_type!!)
            } else {
                Glide.with(context)
                    .load(
                        BaseMediaUrls.USERIMAGE.url + uploadDocumentlist.get(
                            localPosition
                        )?.file!!
                    )
                    .into(itemView.rootView?.imageview_files_type!!)
            }


        }


    }

}