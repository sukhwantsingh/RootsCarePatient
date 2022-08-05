package com.rootscare.ui.newaddition.appointments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.text.parseAsHtml
import androidx.lifecycle.ViewModelProviders
import com.dialog.CommonDialog
import com.google.gson.JsonObject
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.databinding.LayoutNewAppointmentDetailsBinding
import com.rootscare.ui.base.BaseActivity
import com.rootscare.ui.newaddition.appointments.adapter.AdapterPaymentSplitting
import com.rootscare.ui.newaddition.appointments.models.ModelRescheduleDetail
import com.rootscare.ui.newaddition.providerlisting.DialogBadGateway
import com.rootscare.ui.newaddition.providerlisting.models.ModelNetworkTimeSlots
import com.rootscare.ui.newaddition.providerlisting.models.ModelTImeSlotsForDoctor
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback
import com.rootscare.ui.utilitycommon.MyExoPlayer
import com.rootscare.utilitycommon.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.rootscare.data.model.api.request.videoPushRequest.VideoPushRequest
import com.rootscare.data.model.api.response.videoPushResponse.VideoPushResponse
import com.rootscare.twilio.VideoCallActivity



class AppointmentDetailScreen : BaseActivity<LayoutNewAppointmentDetailsBinding, ViewModelMyAppointments>(),
    AppointmentNavigator {
    private var binding: LayoutNewAppointmentDetailsBinding? = null
    private var mViewModel: ViewModelMyAppointments? = null

    companion object{
        var appointmentId: String? = null
        var serviceType: String? = null
        var providerId = ""
        var bookType = ""
    }

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.layout_new_appointment_details
    override val viewModel: ViewModelMyAppointments
        get() {
            mViewModel = ViewModelProviders.of(this).get(ViewModelMyAppointments::class.java)
            return mViewModel as ViewModelMyAppointments
        }
    private val mAdapterPayment: AdapterPaymentSplitting by lazy { AdapterPaymentSplitting()  }


    private var orderId = ""
    var mRecheduleBS : BSReschedule? = null
    private var myExoPlayer: MyExoPlayer? = null
    private var downloadManager: DownloadManager? = null
    private var downLoadId: Long? = null
    private var mHospId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this
        binding = viewDataBinding
        initViews()
    }

    private fun initViews() {
           binding?.apply {
            topToolbar.tvHeader.text = getString(R.string.appointment_details)
            topToolbar.btnBack.setOnClickListener { finish() }
            rvPayments.adapter = mAdapterPayment
      //      tvhCall.setOnClickListener { openDialer(patientContact) }
          //  tvhOpenGmap.setOnClickListener { openGoogleMap(mPatientLat,mPatientLng) }

           btnRateAppointment.setOnClickListener {
              val mDialog = DialogRateAppointment.newInstance(btsCallback)
              mDialog.show(supportFragmentManager, "RatingDilaogBox")

           }
           btnReschedule.setOnClickListener {
           apiReschedule(appointmentId?:"", serviceType ?: "")
           }

            fetchTasksDetailApi()
        }
    }
    private val btsCallback = object : OnBottomSheetCallback {
        override fun onSubmitReschedule(vararg data: String) {
            apiUpdateReschedule(data.getOrNull(0)?:"",data.getOrNull(1)?:"",
                data.getOrNull(2)?:"",data.getOrNull(3)?:"")
        }

        override fun onDateChanged(vararg data: String) {
            apiFetchTimeSlots(data.getOrNull(0).orEmpty(),
                data.getOrNull(1).orEmpty(),
                data.getOrNull(2).orEmpty(), data.getOrNull(3).orEmpty(),
                data.getOrNull(4).orEmpty(), data.getOrNull(5).orEmpty())
       }

        override fun onSubmitRating(vararg data: String) {
            if (isNetworkConnected) {
                val rt = data.getOrNull(0) ?:""
                val rev = data.getOrNull(1) ?:""

                val jsonObject = JsonObject().apply {
                    addProperty("hospital_id", mHospId)
                    addProperty("service_type", serviceType)
                    addProperty("lgoin_user_id", mViewModel?.appSharedPref?.userId)
                    addProperty("order_id", orderId)
                    addProperty("rating", rt )
                    addProperty("review", rev)
                    addProperty("provider_id", providerId ?:"")
                }
                val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                hideKeyboard(); showLoading()
                mViewModel?.apiInserRating(body)
            } else {
                showToast(getString(R.string.check_network_connection))
            }
        }

        override fun onGoBack() { finish() }
        override fun onRetry() { fetchTasksDetailApi()  }

    }

    private fun fetchTasksDetailApi() {
        if (isNetworkConnected) {
            showLoading()
            val jsonObject = JsonObject().apply {
                 addProperty("service_type", serviceType)
                 addProperty("id", appointmentId?:"")
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            mViewModel?.apiAppointmentDetails(body)
        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }

    private fun apiFetchTimeSlots(dtSel: String, proType:String, proId:String, orderId:String, taskTyp:String, tskId:String) {
        if (isNetworkConnected) {
            showLoading()
            val jsonObject = JsonObject().apply {
                addProperty("date", dtSel)
                addProperty("service_type", proType)
                addProperty("provider_id", proId)
            }

            if(proType.equals(ProviderTypes.DOCTOR.getType(),ignoreCase = true)) {
                val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                mViewModel?.apiBookingTimeSlotsForDoc(body)
            }  else if(proType.equals(ProviderTypes.LAB.getType(),ignoreCase = true)) {
                val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                mViewModel?.apiBookingTimeSlotsForLab(body)
            }
            else {
                jsonObject.apply {
                addProperty("appid", orderId)
                addProperty("task_type", taskTyp)
                addProperty("hour_task_id", tskId) }
                val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                mViewModel?.apiBookingTimeSlots(body)
            }
        } else {
            showToast(getString(R.string.check_network_connection))
        }

    }

    override fun onSuccessBookingTimeSlots(response: ModelTImeSlotsForDoctor?) {
        hideLoading()
        if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            if (response?.result != null) {
                mRecheduleBS?.mTimeSlotList?.clear()

                response.result.online_task_time?.let { tTime ->
                    if(tTime.isBlank()) {  mRecheduleBS?.mTimeSlotList?.clear() } else {
                        mRecheduleBS?.mTimeSlotList?.addAll(tTime.split(",").map { it1 -> it1.trim() })}
                }
                response.result.home_visit_time?.let { hTime ->
                    if(hTime.isBlank()) { mRecheduleBS?.mTimeSlotList?.clear() }
                    else { mRecheduleBS?.mTimeSlotList?.addAll(hTime.split(",").map { it1 -> it1.trim()})}
                }
                mRecheduleBS?.updateTimeSlots()
            } else showToast(response?.message?:getString(R.string.something_went_wrong))

        }
    }

    override fun onSuccessBookingTimeSlots(response: ModelNetworkTimeSlots?) {
        hideLoading()
        if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            if (response?.result != null) {
                mRecheduleBS?.mTimeSlotList?.clear()

                response.result.task_time?.let { tTime ->
                    if(tTime.isBlank()) {  mRecheduleBS?.mTimeSlotList?.clear() } else {
                        mRecheduleBS?.mTimeSlotList?.addAll(tTime.split(",").map { it1 -> it1.trim() })}
                }
                response.result.hourly_time?.let { hTime ->
                    if(hTime.isBlank()) { mRecheduleBS?.mTimeSlotList?.clear() }
                    else { mRecheduleBS?.mTimeSlotList?.addAll(hTime.split(",").map { it1 -> it1.trim()})}
                }
                mRecheduleBS?.updateTimeSlots()
            } else showToast(response?.message?:getString(R.string.something_went_wrong))

        }
    }

    override fun onSuccessRating(response: CommonResponse?) {
        hideLoading()
        if (response?.code.equals(SUCCESS_CODE)) {
            fetchTasksDetailApi()
        } else showToast(response?.message?:"")
    }

    private fun apiReschedule(id: String, serviceType: String) {
        if (isNetworkConnected) {

            val jsonObject = JsonObject().apply {
                addProperty("service_type", serviceType)
                addProperty("order_id", id)
                addProperty("login_user_id", mViewModel?.appSharedPref?.userId)
            }

            if(serviceType.equals(ProviderTypes.DOCTOR.getType(),ignoreCase = true)) {

                val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                showLoading()
                mViewModel?.apiRescheduleForDoc(body, -1)
            } else if(serviceType.equals(ProviderTypes.LAB.getType(),ignoreCase = true)) {

                jsonObject.addProperty("task_type", bookType)
                val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
             //   showLoading()
             //   mViewModel?.apiRescheduleForLab(body,-1)
            }
            else{
                val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                showLoading()
                mViewModel?.apiReschedule(body, -1)
            }

        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }

    override fun onUpdateReschedule(response: ModelRescheduleDetail?) {
        hideLoading()
        showToast(response?.message?:"")
        if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            FragNewAppointmentListing.isRescheduled = true
            FragNewAppointmentListing.reDate = response?.result?.display_app_date
            FragNewAppointmentListing.reTime = response?.result?.app_time
            // change the appointment date and time
            binding?.tvBankName?.text = response?.result?.display_app_date?.uppercase()
            binding?.tvAcName?.text = response?.result?.app_time?.uppercase()
        }
    }

    override fun onRescheduleDetails(response: ModelRescheduleDetail?, position: Int) {
        hideLoading()
        if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            if (response?.result != null) {
             mRecheduleBS = BSReschedule.newInstance(response.result, btsCallback)
             mRecheduleBS?.show(supportFragmentManager, "RescheduleDialog")
            }
        } else {
            showToast(response?.message?:"")
        }
    }

    private fun apiUpdateReschedule(fDate: String, fTime: String, orderId: String, serviceType: String) {
        if (isNetworkConnected) {
            showLoading()
            val jsonObject = JsonObject().apply {
                addProperty("service_type", serviceType)
                addProperty("order_id", orderId)
                addProperty("from_date", fDate)
                addProperty("from_time", fTime)
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            if(serviceType.equals(ProviderTypes.DOCTOR.getType(),ignoreCase = true)) {
                 mViewModel?.apiUpdateRescheduleForDoc(body)
            }  else if(serviceType.equals(ProviderTypes.LAB.getType(),ignoreCase = true)) {
                mViewModel?.apiUpdateRescheduleForLab(body)
            } else mViewModel?.apiUpdateReschedule(body)
        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }

    override fun onAppointmentDetail(response: ModelAppointmentDetails?) {
           hideLoading()
        if (response?.code.equals(SUCCESS_CODE)) {
           bindViews(response?.result)
        } else {
          CommonDialog.showDialogForSingleButton(this, object : DialogClickCallback {
            override fun onConfirm() { finish() }
        }, getString(R.string.appointment_details), response?.message ?: "") }

    }

    private var detailModel: ModelAppointmentDetails.Result? = null
    private fun bindViews(response: ModelAppointmentDetails.Result?) {
       response?.let {
           binding?.run {
               data = it
               orderId = it.order_id?:""
               mHospId = it.hospital_id.orEmpty()
//               mPatientLat = it.patient_lat ?: "0.0"
//               mPatientLng = it.patient_long ?: "0.0"
               if(serviceType.equals(ProviderTypes.DOCTOR.getType(),ignoreCase = true)) {
                   detailModel = it
                   showDoctorUI(response)
                   when {
                       it.acceptance_status.equals(TransactionStatus.PENDING.get(),ignoreCase = true) -> {
                           btnReschedule.visibility = View.VISIBLE
                           btnVideoCall.visibility = View.GONE
                       }
                       else -> {
                           // for refund detail
                           grpRefund.visibility = if(it.rf_text.isNullOrBlank().not()) View.VISIBLE else View.GONE
                           tvRefundedNote.text = (it.rf_text ?: "").parseAsHtml()

                           btnReschedule.visibility = View.GONE
                           btnVideoCall.visibility = View.GONE
                           when {
                                it.acceptance_status.equals(TransactionStatus.ACCEPTED.get(),ignoreCase = true)
                                && it.slot_booking_id.equals(SlotBookingId.ONLINE_BOOKING.get(),ignoreCase = true) -> {
                               //     btnVideoCall.visibility = View.VISIBLE
                                 needToShowVideoCall(it.app_date, it.from_time, btnVideoCall)
                                 btnVideoCall.setOnClickListener { hitVideoCall() }
                               }
                               it.acceptance_status.equals(TransactionStatus.COMPLETED.get(),ignoreCase = true) -> {
                                   btnVideoCall.visibility = View.GONE
                                   showRatingOrNot(it)

                                   // show the rating accordingly
                                   if(it.avg_rating.isNullOrBlank().not() && it.avg_rating?.toInt() != 0){
                                       ratBar.visibility = View.VISIBLE
                                       tvhRated.visibility = View.VISIBLE
                                       btnRateAppointment.visibility = View.GONE
                                       ratBar.rating = it.avg_rating?.toFloat() ?: 0.0f
                                       tvhRated.text = getString(R.string.rated)
                                   } else {
                                       ratBar.visibility = View.GONE
                                       tvhRated.visibility = View.GONE
                                       btnRateAppointment.visibility = View.VISIBLE
                                   }

                               }
                           }
                       }
                   }
                }
                else if(serviceType.equals(ProviderTypes.LAB.getType(),ignoreCase = true)) {
                   detailModel = it
                   grpPresc.visibility = if(it.provider_prescription.isNullOrBlank().not()) {
                       tvPrescDowonload.setOnClickListener { _ ->
                           initializeDownloadManager()
                           downloadFile(it.provider_prescription.orEmpty())
                       }
                       View.VISIBLE } else View.GONE
                   when {
                       it.acceptance_status.equals(TransactionStatus.PENDING.get(),ignoreCase = true) -> {
                           btnReschedule.visibility = View.VISIBLE
                       }
                       else -> {
                           // for refund detail
                           grpRefund.visibility = if(it.rf_text.isNullOrBlank().not()) View.VISIBLE else View.GONE
                           tvRefundedNote.text = (it.rf_text ?: "").parseAsHtml()
                           btnReschedule.visibility = View.GONE
                           when {
                               it.acceptance_status.equals(TransactionStatus.COMPLETED.get(),ignoreCase = true) -> {
                                   showRatingOrNot(it)

                                   // show the rating accordingly
                                   if(it.avg_rating.isNullOrBlank().not() && it.avg_rating?.toInt() != 0){
                                       ratBar.visibility = View.VISIBLE
                                       tvhRated.visibility = View.VISIBLE
                                       btnRateAppointment.visibility = View.GONE
                                       ratBar.rating = it.avg_rating?.toFloat() ?: 0.0f
                                       tvhRated.text = getString(R.string.rated)
                                   } else {
                                       ratBar.visibility = View.GONE
                                       tvhRated.visibility = View.GONE
                                       btnRateAppointment.visibility = View.VISIBLE
                                   }

                               }
                           }
                       }
                   }
                }
                else {
                   when {
                       it.acceptance_status.equals(TransactionStatus.PENDING.get(), ignoreCase = true) -> {
                           btnReschedule.visibility = View.VISIBLE
                       }
                       else -> {
                           // for refund detail
                           grpRefund.visibility = if (it.rf_text.isNullOrBlank().not()) View.VISIBLE else View.GONE
                           tvRefundedNote.text = (it.rf_text ?: "").parseAsHtml()
                           btnReschedule.visibility = View.GONE
                           when {
                               it.acceptance_status.equals( TransactionStatus.REJECTED.get(),ignoreCase = true) ||
                               it.acceptance_status.equals( TransactionStatus.CANCELLED.get(), ignoreCase = true ) -> {
                                   grpEnterOtp.visibility = View.GONE
                               }

                               it.acceptance_status.equals(TransactionStatus.ACCEPTED.get(), ignoreCase = true) -> {
                                   grpEnterOtp.visibility = View.VISIBLE
                                   tvhOtpSuccfully.text = getString(R.string.provide_otp_at_the_end_of_service)
                                   tvhOtpSuccfully.setTextColor(Color.parseColor("#515C6F"))
                                   tvhOtpFilled.setTextColor(Color.parseColor("#515C6F"))
                               }
                               it.acceptance_status.equals(TransactionStatus.COMPLETED.get(), ignoreCase = true) -> {
                                   showRatingOrNot(it)
                                   grpEnterOtp.visibility = if (it.OTP.isNullOrBlank().not()) {
                                       tvhOtpSuccfully.text = getString(R.string.appointment_closed_otp_text)
                                       tvhOtpSuccfully.setTextColor(Color.parseColor("#4FB82A"))
                                       tvhOtpFilled.setTextColor(Color.parseColor("#4FB82A"))
                                       View.VISIBLE
                                   } else View.GONE

                                   // show the rating accordingly
                                   if (it.avg_rating.isNullOrBlank().not() && it.avg_rating?.toInt() != 0) {
                                       ratBar.visibility = View.VISIBLE
                                       tvhRated.visibility = View.VISIBLE
                                       btnRateAppointment.visibility = View.GONE
                                       ratBar.rating = it.avg_rating?.toFloat() ?: 0.0f
                                       tvhRated.text = getString(R.string.rated)
                                   } else {
                                       ratBar.visibility = View.GONE
                                       tvhRated.visibility = View.GONE
                                       btnRateAppointment.visibility = View.VISIBLE
                                   }

                               }
                           }
                       }
                   }
               }


              // set the recyclerview
               if(it.task_details.isNullOrEmpty().not()){
                   relPayment.visibility = View.VISIBLE
                   mAdapterPayment.submitList(it.task_details)
               } else {
                   relPayment.visibility = View.GONE
               }
         }
        }
    }

    private var roomName: String? = null
    private fun hitVideoCall(){
        if (isNetworkConnected) {
            showLoading()
            val videoPushRequest = VideoPushRequest()
            videoPushRequest.fromUserId = mViewModel?.appSharedPref?.userId
            videoPushRequest.toUserId = detailModel?.provider_id.orEmpty().trim()    // provider id
            roomName = "rootvideo_room_" + videoPushRequest.toUserId + "_" + videoPushRequest.fromUserId
            videoPushRequest.orderId = detailModel?.order_id.orEmpty().trim()
            videoPushRequest.roomName = roomName.orEmpty().trim()
            videoPushRequest.type = "patient_to_doctor_video_call"
            videoPushRequest.fromUserName = detailModel?.patient_name.orEmpty().trim()
            videoPushRequest.toUserName = detailModel?.provider_name.orEmpty().trim()
            mViewModel?.apiSendVideoPushNotification( videoPushRequest)

        } else {
           showToast(getString(R.string.check_network_connection))
        }
    }

    override fun successVideoPushResponse(videoPushResponse: VideoPushResponse?) {
        hideLoading()
        if (videoPushResponse?.status == true && videoPushResponse.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            val bundle = Bundle()
            bundle.putString("roomName", roomName)
            bundle.putString("fromUserId", mViewModel?.appSharedPref?.userId.orEmpty())
            bundle.putString("fromUserName", detailModel?.patient_name.orEmpty().trim())
            bundle.putString("toUserId", detailModel?.provider_id.orEmpty())
            bundle.putString("toUserName", detailModel?.provider_name.orEmpty().trim())
            bundle.putString("orderId", detailModel?.order_id.orEmpty().trim())
            bundle.putBoolean("isDoctorCalling", false)

            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        //    activity?.finish()
        } else {
           showToast(videoPushResponse?.message?: getString(R.string.something_went_wrong))
        }
    }

    private fun showDoctorUI(response: ModelAppointmentDetails.Result?) {
        response?.let {
            binding?.run {
                if(it.slot_booking_id?.equals(SlotBookingId.ONLINE_BOOKING.get(),true) == true){
                    tvhDisFare.visibility = View.GONE
                    tvDisFare.visibility = View.GONE
                    vw1.visibility = View.GONE
                }

                grpPresc.visibility = if(it.provider_prescription.isNullOrBlank().not()) {
                tvPrescDowonload.setOnClickListener { _ ->
                  initializeDownloadManager()
                  downloadFile(it.provider_prescription.orEmpty())
                }
                 View.VISIBLE } else View.GONE

                if(it.symptom_text.isNullOrBlank().not()){
                    tSyp.visibility = View.VISIBLE
                    tvSympDesc.visibility = View.VISIBLE
                } else {
                    tSyp.visibility = View.GONE
                    tvSympDesc.visibility = View.GONE
                }

                if(it.patient_prescription.isNullOrBlank().not()) {
                    tvUploadedSumptom.visibility = View.VISIBLE
                    tvUpPresView.visibility = View.VISIBLE
                    tvUpPresView.setOnClickListener { _ ->
                     viewFileEnlarge( BaseMediaUrls.USERIMAGE.url+ it.patient_prescription.orEmpty())
                    }
                } else {
                    tvUploadedSumptom.visibility = View.GONE
                    tvUpPresView.visibility = View.GONE
                }

                if(it.symptom_recording.isNullOrBlank().not()){
                    playerView.visibility = View.VISIBLE
                    //"https://teq-dev-var19.co.in/rootscare/uploads/images/Kalimba.mp3")
                   myExoPlayer = MyExoPlayer(playerView, applicationContext, BaseMediaUrls.AUDIO.url + it.symptom_recording.orEmpty())
                 onStart(); onResume()
                } else playerView.visibility = View.GONE

               if(it.symptom_text.isNullOrBlank() && it.patient_prescription.isNullOrBlank() && it.symptom_recording.isNullOrBlank()){
                   cnsPatientSyptm.visibility = View.GONE
                 } else cnsPatientSyptm.visibility = View.VISIBLE
            }
        }

    }

    /**
     * Initialize download manager
     */
    private fun initializeDownloadManager() {
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }

    /**
     * Set the title and desc of this download, to be displayed in notifications.
     */
    private fun downloadFile(fName: String) {
        try{
            val request = DownloadManager.Request(Uri.parse(BaseMediaUrls.USERIMAGE.url + fName))
//            val request = DownloadManager.Request(Uri.parse("https://teq-dev-var19.co.in/rootscare/uploads/images/Kalimba.mp3"))
            request.setTitle(fName).setDescription("File is downloading...")
            .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, fName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            //Enqueue the download.The download will start automatically once the download manager is ready
            // to execute it and connectivity is available.
            downLoadId = downloadManager?.enqueue(request)
            showToast(getString(R.string.prescription_downloaded_success))
        } catch (e:Exception){
            showToast(getString(R.string.something_went_wrong))
        }

    }

    private fun showRatingOrNot(it: ModelAppointmentDetails.Result) {
        binding?.run {
            if(it.avg_rating.isNullOrBlank().not() && it.avg_rating?.toInt() != 0){
                ratBar.visibility = View.VISIBLE
                tvhRated.visibility = View.VISIBLE
                btnRateAppointment.visibility = View.GONE
                ratBar.rating = it.avg_rating?.toFloat() ?: 0.0f
                tvhRated.text = getString(R.string.rated)

            } else {
                ratBar.visibility = View.GONE
                tvhRated.visibility = View.GONE
                tvhRated.text = getString(R.string.not_rated_yet)
            }
        }
    }

     override fun errorInApi(throwable: Throwable?) {
        hideLoading()
        if (throwable?.message != null) {
       //     showToast(throwable.message ?: getString(R.string.something_went_wrong))
           val badgateWay = DialogBadGateway.newInstance(btsCallback)
            badgateWay.show(supportFragmentManager, "BadgatewayDialog")
        }
    }

    override fun errorInRecheduleApi(throwable: Throwable?) {
        hideLoading()
        showToast(throwable?.message ?: getString(R.string.something_went_wrong))
    }

    override fun reloadPageAfterConnectedToInternet() {

    }


override fun onResume() {
    super.onResume()
    myExoPlayer?.onResume()
}

override fun onStart() {
    super.onStart()
    myExoPlayer?.onStart()
}


override fun onPause() {
    super.onPause()
    myExoPlayer?.onPause()
}


    override fun onDestroy() {
        super.onDestroy()
        myExoPlayer?.onStop()
    }

}