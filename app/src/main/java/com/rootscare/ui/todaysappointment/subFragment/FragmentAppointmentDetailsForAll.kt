package com.rootscare.ui.todaysappointment.subFragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.dialog.CommonDialog
import com.dialog.inputfilename.FileNameInputDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.appointmentdetailsrequest.AppointmentDetailsRequest
import com.rootscare.data.model.api.request.cancelappointmentrequest.CancelAppointmentRequest
import com.rootscare.data.model.api.request.videoPushRequest.VideoPushRequest
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.data.model.api.response.addmedicallrecords.AddlabTestImageSelectionModel
import com.rootscare.data.model.api.response.appointcancelresponse.AppointmentCancelResponse
import com.rootscare.data.model.api.response.appointmenthistoryresponse.TaskDetails
import com.rootscare.data.model.api.response.videoPushResponse.VideoPushResponse
import com.rootscare.databinding.FragmentDoctorAppointmentDetailsBinding
import com.rootscare.twilio.VideoCallActivity
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.ui.submitfeedback.FragmentSubmitReview
import com.rootscare.utilitycommon.BaseMediaUrls
import com.rootscare.utils.DateTimeUtils
import com.rootscare.utils.MyImageCompress
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_doctor_appointment_details.*
import java.io.File
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class FragmentAppointmentDetailsForAll :
    BaseFragment<FragmentDoctorAppointmentDetailsBinding, FragmentDoctorAppointmentDetailsViewModel>(),
    FragmentDoctorAppointmentDetailsNavigator {

    private var imageSelectionModel: AddlabTestImageSelectionModel? = null
    private var appointmentId: String? = null
    private var serviceType: String? = null
    private var doctorId: String? = null
    private var doctorName: String? = null
    private var orderId: String? = null
    private var statusOfAppointmentForFlowChange: String? = null
//    private var taskDetails: NurseTaskDetails? = null

    private var mediaPlayer: MediaPlayer? = null

    private var fragmentDoctorAppointmentDetailsBinding: FragmentDoctorAppointmentDetailsBinding? =
        null
    private var fragmentDoctorAppointmentDetailsViewModel: FragmentDoctorAppointmentDetailsViewModel? =
        null
    private var roomName: String? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_doctor_appointment_details
    override val viewModel: FragmentDoctorAppointmentDetailsViewModel
        get() {
            fragmentDoctorAppointmentDetailsViewModel = ViewModelProviders.of(this).get(
                FragmentDoctorAppointmentDetailsViewModel::class.java
            )
            return fragmentDoctorAppointmentDetailsViewModel as FragmentDoctorAppointmentDetailsViewModel
        }

    companion object {
        const val VIDEO_CALL_REQUEST = 1001
        private const val TAG = "FragmentAppointmentData"
        fun newInstance(
            appointmentId: String,
            doctorId: String,
            doctorName: String,
            orderId: String,
            serviceType: String
        ): FragmentAppointmentDetailsForAll {
            val args = Bundle()
            args.putString("appointmentId", appointmentId)
            args.putString("doctorId", doctorId)
            args.putString("doctorName", doctorName)
            args.putString("orderId", orderId)
            args.putString("service_type", serviceType)
            val fragment = FragmentAppointmentDetailsForAll()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(
            appointmentId: String,
            doctorId: String,
            doctorName: String,
            orderId: String,
            service_type: String,
            statusOfAppointmentForFlowChange: String
        ): FragmentAppointmentDetailsForAll {
            val args = Bundle()
            args.putString("appointmentId", appointmentId)
            args.putString("doctorId", doctorId)
            args.putString("doctorName", doctorName)
            args.putString("orderId", orderId)
            args.putString("service_type", service_type)
            args.putString("statusOfAppointmentForFlowChange", statusOfAppointmentForFlowChange)
            val fragment = FragmentAppointmentDetailsForAll()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(
            appointmentId: String,
            service_type: String
        ): FragmentAppointmentDetailsForAll {
            val args = Bundle()
            args.putString("appointmentId", appointmentId)
            args.putString("service_type", service_type)
            val fragment = FragmentAppointmentDetailsForAll()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(
            appointmentId: String, service_type: String,
            statusOfAppointmentForFlowChange: String
        ): FragmentAppointmentDetailsForAll {
            val args = Bundle()
            args.putString("appointmentId", appointmentId)
            args.putString("service_type", service_type)
            args.putString("statusOfAppointmentForFlowChange", statusOfAppointmentForFlowChange)
            val fragment = FragmentAppointmentDetailsForAll()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(
            appointmentId: String, service_type: String, statusOfAppointmentForFlowChange: String,
            patientIdForPrescriptionUploadFromTodayAppointment: String
        ): FragmentAppointmentDetailsForAll {
            val args = Bundle()
            args.putString("appointmentId", appointmentId)
            args.putString("service_type", service_type)
            args.putString("statusOfAppointmentForFlowChange", statusOfAppointmentForFlowChange)
            args.putString(
                "patientIdForPrescriptionUploadFromTodayAppointment",
                patientIdForPrescriptionUploadFromTodayAppointment
            )
            val fragment = FragmentAppointmentDetailsForAll()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(
            appointmentId: String,
            doctorId: String,
            doctorName: String,
            orderId: String,
            service_type: String,
            taskDetails: TaskDetails?
        ): FragmentAppointmentDetailsForAll {
            val args = Bundle()
            args.putString("appointmentId", appointmentId)
            args.putString("doctorId", doctorId)
            args.putString("doctorName", doctorName)
            args.putString("orderId", orderId)
            args.putString("service_type", service_type)
            args.putSerializable("taskDetails", taskDetails)
            val fragment = FragmentAppointmentDetailsForAll()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentDoctorAppointmentDetailsViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentDoctorAppointmentDetailsBinding = viewDataBinding
        appointmentId = arguments?.getString("appointmentId")
        serviceType = arguments!!.getString("service_type")
        doctorId = arguments!!.getString("doctorId")
        doctorName = arguments!!.getString("doctorName")
        orderId = arguments!!.getString("orderId")
        statusOfAppointmentForFlowChange = arguments?.getString("statusOfAppointmentForFlowChange")
//        taskDetails = arguments?.getSerializable("taskDetails") as NurseTaskDetails?
        apiHitForDetails()
//        if (serviceType == "doctor") {
//
//        }

//        patientIdForPrescriptionUpload =
//            arguments?.getString("patientIdForPrescriptionUploadFromTodayAppointment")
//

//        service_type = arguments?.getString("service_type")
//        modelDoctorAppointmentItem =
//            arguments!!.getSerializable("modelDoctorAppointmentItem") as DoctorAppointmentItem?
//        isConsultationCompleted = arguments!!.getBoolean("isConsultationCompleted")
//        appointmentId = modelDoctorAppointmentItem?.id

//        fragmentDoctorAppointmentDetailsBinding?.btnRootscareDoctorConsulting?.setOnClickListener {
//            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                FragmentDoctorConsulting.newInstance()
//            )
//        }
    }


    private fun apiHitForDetails() {
        val request = AppointmentDetailsRequest()
        println("appointmentId  $appointmentId")
        println("serviceType  $serviceType")
        request.id = appointmentId
        request.serviceType = serviceType
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentDoctorAppointmentDetailsViewModel!!.getDetails(request)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onSuccessDetails(response: JsonObject) {
        baseActivity?.hideLoading()
        val jsonObject: JsonObject = response
        val status = jsonObject["status"].asBoolean
        val message = jsonObject["message"].asString
        var clinicAddress: String? = null
        if (status) {
            val result = jsonObject["result"].asJsonObject
            if (serviceType.equals("doctor") || serviceType.equals("hospital_doctor")) {
                val doctorImage = result["doctor_image"].asString
                val doctorName = result["doctor_name"].asString
                if (result.has("clinic_address")) {
                    clinicAddress = result["clinic_address"].asString
                }
                tv_addressName.visibility = View.VISIBLE
                tv_address.visibility = View.VISIBLE
                if (doctorImage != null && !TextUtils.isEmpty(doctorImage.trim())) {
                    val options: RequestOptions =
                        RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.profile_no_image)
                            .priority(Priority.HIGH)
                    Glide
                        .with(activity!!)
                        .load(BaseMediaUrls.USERIMAGE.url + doctorImage)
                        .apply(options)
                        .into(imageView)
                }
                if (doctorName != null && !TextUtils.isEmpty(doctorName.trim())) {
                    tvDoctorName.text = doctorName
                }
                if (clinicAddress != null && !TextUtils.isEmpty(clinicAddress.trim())) {
                    tv_address.text = clinicAddress
                }
            } else if (serviceType.equals("nurse")) {
                val slotType = result["slot_type"].asString
                if (slotType == "Hourly_slot") {
                    val timeDiff = result["time_diff"].asString
                    tv_addressName.text = "Slot Details : "
                    tv_address.text = timeDiff
                    llAppointmentTime.visibility = View.VISIBLE
                } else {
                    llAppointmentTime.visibility = View.GONE
                    val taskDetails = result["task_details"].asJsonObject
//                val taskDetails: JsonObject? =  result["taskDetails"].asJsonObject
                    val testName = taskDetails["test_name"].asString
                    tv_addressName.text = "Task Details : "
                    if (taskDetails != null)
                        tv_address.text = testName
                }

                tv_addressName.visibility = View.VISIBLE
                tv_address.visibility = View.VISIBLE
                val doctorImage = result["nurse_image"].asString
                val doctorName = result["nurse_name"].asString
                if (doctorImage != null && !TextUtils.isEmpty(doctorImage.trim())) {
                    val options: RequestOptions =
                        RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.profile_no_image)
                            .priority(Priority.HIGH)
                    Glide
                        .with(activity!!)
                        .load(BaseMediaUrls.USERIMAGE.url + doctorImage)
                        .apply(options)
                        .into(imageView)
                }
                if (doctorName != null && !TextUtils.isEmpty(doctorName.trim())) {
                    tvDoctorName.text = doctorName
                }

            } else if (serviceType.equals("babysitter")) {
                val doctorImage = result["babysitter_image"].asString
                val doctorName = result["babysitter_name"].asString
                if (doctorImage != null && !TextUtils.isEmpty(doctorImage.trim())) {
                    val options: RequestOptions =
                        RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.profile_no_image)
                            .priority(Priority.HIGH)
                    Glide
                        .with(activity!!)
                        .load(BaseMediaUrls.USERIMAGE.url + doctorImage)
                        .apply(options)
                        .into(imageView)
                }
                if (doctorName != null && !TextUtils.isEmpty(doctorName.trim())) {
                    tvDoctorName.text = doctorName
                }
                tv_addressName.visibility = View.GONE
                tv_address.visibility = View.GONE
            } else if (serviceType.equals("caregiver")) {
                val doctorImage = result["caregiver_image"].asString
                val doctorName = result["caregiver_name"].asString
                if (doctorImage != null && !TextUtils.isEmpty(doctorImage.trim())) {
                    val options: RequestOptions =
                        RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.profile_no_image)
                            .priority(Priority.HIGH)
                    Glide
                        .with(activity!!)
                        .load(BaseMediaUrls.USERIMAGE.url + doctorImage)
                        .apply(options)
                        .into(imageView)
                }
                if (doctorName != null && !TextUtils.isEmpty(doctorName.trim())) {
                    tvDoctorName.text = doctorName
                }
                tv_addressName.visibility = View.GONE
                tv_address.visibility = View.GONE
            } else if (serviceType.equals("physiotherapy")) {
                val doctorImage = result["physiotherapist_image"].asString
                val doctorName = result["physiotherapist_name"].asString
                if (doctorImage != null && !TextUtils.isEmpty(doctorImage.trim())) {
                    val options: RequestOptions =
                        RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.profile_no_image)
                            .priority(Priority.HIGH)
                    Glide
                        .with(activity!!)
                        .load(BaseMediaUrls.USERIMAGE.url + doctorImage)
                        .apply(options)
                        .into(imageView)
                }
                if (doctorName != null && !TextUtils.isEmpty(doctorName.trim())) {
                    tvDoctorName.text = doctorName
                }
                tv_addressName.visibility = View.GONE
                tv_address.visibility = View.GONE
            } else if (serviceType.equals("pathology")) {
                val pathologyDetails = result["pathology_details"].asJsonObject
//                val taskDetails: JsonObject? =  result["taskDetails"].asJsonObject
                val pathologyName = pathologyDetails["pathology_name"].asString
                val doctorImage = result["hospital_image"].asString
                val doctorName = result["hospital_name"].asString
//                val taskName = result["pathology_name"].asString
                if (doctorImage != null && !TextUtils.isEmpty(doctorImage.trim())) {
                    val options: RequestOptions =
                        RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.profile_no_image)
                            .priority(Priority.HIGH)
                    Glide
                        .with(activity!!)
                        .load(BaseMediaUrls.USERIMAGE.url + doctorImage)
                        .apply(options)
                        .into(imageView)
                }
                if (doctorName != null && !TextUtils.isEmpty(doctorName.trim())) {
                    tvDoctorName.text = doctorName
                }
                llAppointmentTime.visibility = View.GONE
                tv_addressName.text = "Task Details : "
                tv_address.text = pathologyName
            }

            with(fragmentDoctorAppointmentDetailsBinding!!) {
                crdviewDoctorappoitmentList.visibility = View.VISIBLE
                val patientName = result["patient_name"].asString
                val bookingDate = result["booking_date"].asString
                val patientContact = result["patient_contact"].asString
                val appointmentDate = result["appointment_date"].asString
                val fromTime = result["from_time"].asString
                val toTime = result["to_time"].asString
                val appointmentStatus = result["appointment_status"].asString
                val acceptanceStatus = result["acceptance_status"].asString
                val orderId = result["order_id"].asString
                var reviewRating: JsonArray? = null

                if (result.has("review_rating")) {
                    reviewRating = result["review_rating"].asJsonArray
                }
                val id = result["id"].asString

                if (patientName != null && !TextUtils.isEmpty(patientName.trim())) {
                    txtPatientName.text = patientName
                }
                if (bookingDate != null && !TextUtils.isEmpty(bookingDate.trim())) {
//                  txtBookingDate.text = response.result.bookingDate
                    txtBookingDate.text = formatDateFromString(
                        "yyyy-MM-dd",
                        "dd MMM yyyy",
                        bookingDate
                    )
                }
                if (patientContact != null && !TextUtils.isEmpty(patientContact.trim())) {
                    tvPhoneNumber.text = patientContact
                }
                if (appointmentDate != null && !TextUtils.isEmpty(appointmentDate.trim())) {
                    tvAppointmentDate.text = appointmentDate
                }

                if (fromTime != null && !TextUtils.isEmpty(fromTime.trim())) {
                    llAppointmentTime.visibility = View.VISIBLE
                    tvAppointmentTime.text = fromTime
                }

                if (appointmentStatus != null && !TextUtils.isEmpty(appointmentStatus.trim())) {
                    tvAppointmentStatus.text = appointmentStatus
                }
                if (acceptanceStatus != null && !TextUtils.isEmpty(acceptanceStatus.trim())) {
                    tvAcceptanceStatus.text = acceptanceStatus
                    if (appointmentStatus == "Completed") {
                        tvStatus.text = appointmentStatus
                    } else {
                        tvStatus.text = acceptanceStatus
                    }
                }
                if (orderId != null && !TextUtils.isEmpty(orderId.trim())) {
                    tvOrderId.text = orderId
                }

                when (acceptanceStatus) {
                    "Pending" -> {
                        btnCancel.visibility = View.GONE
                        tvStatus.background = activity?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.accptance_pending_bg
                            )
                        }
                        btnRootscareDoctorConsulting.visibility = View.INVISIBLE
                        btnReview.visibility = View.GONE
                        llReview.visibility = View.GONE
                    }
                    "Rejected" -> {
                        btnCancel.visibility = View.GONE
                        tvStatus.setBackgroundColor(Color.parseColor("#FF0303"))
                        tvStatus.background = activity?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.reject_bg
                            )
                        }
                        btnRootscareDoctorConsulting.visibility = View.INVISIBLE
                        btnReview.visibility = View.GONE
                        llReview.visibility = View.GONE
                    }
                    "Completed" -> {
                        //                                isConsultationCompleted = true
                        btnCancel.visibility = View.GONE
                        tvStatus.setBackgroundColor(Color.parseColor("#FF0303"))
                        tvStatus.background = activity?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.reject_bg
                            )
                        }
                        btnRootscareDoctorConsulting.visibility = View.GONE
                        btnReview.visibility = View.VISIBLE
                        llReview.visibility = View.GONE
                    }
                    else -> {
                        if (reviewRating != null && reviewRating.size() > 0) {
                            btnRootscareDoctorConsulting.visibility = View.INVISIBLE
                            btnCancel.visibility = View.GONE
                            btnReview.visibility = View.GONE
                            llReview.visibility = View.GONE
                            tvStatus.setBackgroundColor(Color.parseColor("#70BE58"))
                            tvStatus.background = activity?.let {
                                ContextCompat.getDrawable(
                                    it,
                                    R.drawable.approved_bg
                                )
                            }
                        } else {
                            if (appointmentStatus == "Completed") {
                                //                                      isConsultationCompleted = true
                                btnCancel.visibility = View.GONE
                                tvStatus.setBackgroundColor(Color.parseColor("#70BE58"))
                                tvStatus.background = activity?.let {
                                    ContextCompat.getDrawable(
                                        it,
                                        R.drawable.approved_bg
                                    )
                                }
                                btnRootscareDoctorConsulting.visibility = View.GONE
                                btnReview.visibility = View.VISIBLE
                                llReview.visibility = View.GONE
                            } else {
                                btnCancel.visibility = View.GONE
                                //                                      btnRootscareDoctorConsulting.visibility = View.VISIBLE
                                tvStatus.setBackgroundColor(Color.parseColor("#70BE58"))
                                tvStatus.background = activity?.let {
                                    ContextCompat.getDrawable(
                                        it,
                                        R.drawable.approved_bg
                                    )
                                }
                                if ((serviceType == "doctor") || serviceType.equals("hospital_doctor")) {
//                                    btnRootscareDoctorConsulting.visibility = View.VISIBLE
                                    val timeString =
                                        if (fromTime?.split(" ")
                                                ?.get(0)?.length == 4
                                        ) "0" + fromTime.split(" ")[0] else fromTime?.split(" ")
                                            ?.get(0)
                                    val input = formatDateFromString(
                                        "yyyy-MM-dd",
                                        "dd-MM-yyyy",
                                        appointmentDate!!
                                    ) + " " + timeString + ":00" + " " + (fromTime?.split(
                                        " "
                                    )?.get(1))
                                    val df: DateFormat =
                                        SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.ENGLISH)
                                    val date: Date
                                    try {
                                        //Converting the input String to Date
                                        date = df.parse(input)
                                        //Changing the format of date and storing it in String
                                        val calendar = Calendar.getInstance()
                                        //Setting the Calendar date and time to the given date and time
                                        calendar.time = date

                                        if ((calendar.timeInMillis - System.currentTimeMillis()) <= TimeUnit.MINUTES.toMillis(
                                                2
                                            )
                                        ) {
                                            btnRootscareDoctorConsulting.visibility = View.VISIBLE
                                        } else {
                                            btnRootscareDoctorConsulting.visibility = View.INVISIBLE
                                        }
                                    } catch (e: ParseException) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    btnRootscareDoctorConsulting.visibility = View.INVISIBLE
                                }
                                btnReview.visibility = View.GONE
                                llReview.visibility = View.GONE
                            }
                        }
                    }
                }

                btnCancel.setOnClickListener {
                    CommonDialog.showDialog(context!!, object :
                        DialogClickCallback {
                        override fun onDismiss() {
                        }

                        override fun onConfirm() {
//

                            if (isNetworkConnected) {
                                baseActivity?.showLoading()
                                val cancelAppointmentRequest = CancelAppointmentRequest()
                                cancelAppointmentRequest.id = id
                                cancelAppointmentRequest.serviceType = serviceType
                                fragmentDoctorAppointmentDetailsViewModel?.apiCancelAppointment(
                                    cancelAppointmentRequest
                                )

                            } else {
                                Toast.makeText(
                                    activity,
                                    "Please check your network connection.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                    }, "Cancel Appointment", "Are you sure to cancel this appointment?")
                }

                btnRootscareDoctorConsulting.setOnClickListener {
                    if (isNetworkConnected) {
                        baseActivity?.showLoading()
                        val videoPushRequest = VideoPushRequest()
                        videoPushRequest.fromUserId =
                            fragmentDoctorAppointmentDetailsViewModel?.appSharedPref?.userId
                        videoPushRequest.toUserId = doctorId
                        roomName =
                            "rootvideo_room_" + videoPushRequest.toUserId + "_" + videoPushRequest.fromUserId
                        videoPushRequest.orderId = orderId
                        videoPushRequest.roomName =
                            "rootvideo_room_" + videoPushRequest.toUserId + "_" + videoPushRequest.fromUserId
                        videoPushRequest.type = "patient_to_doctor_video_call"
                        videoPushRequest.fromUserName =
                            fragmentDoctorAppointmentDetailsViewModel?.appSharedPref?.userName
                        videoPushRequest.toUserName = doctorName
                        fragmentDoctorAppointmentDetailsViewModel?.apiSendVideoPushNotification(
                            videoPushRequest
                        )

                    } else {
                        Toast.makeText(
                            activity,
                            "Please check your network connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                btnReview.setOnClickListener {
//                            ll_review.visibility = View.VISIBLE
//                            btnReview.background = activity?.let {
//                                ContextCompat.getDrawable(
//                                    it,
//                                    R.drawable.review_disabled_bg
//                                )
//                            }
//                        }

                    val fragmentSubmitReview: FragmentSubmitReview? =
                        FragmentSubmitReview.newInstance(
                            doctorId!!,
                            orderId!!
                        )

//                            val fragmentSubmitReview: FragmentSubmitReview? =
//                                modelDoctorAppointmentItem?.doctorId?.let {
//                                    FragmentSubmitReview.newInstance(
//                                        modelDoctorAppointmentItem?.doctorId!!
//                                    )
//                                }
                    if (fragmentSubmitReview != null) {
                        (activity as HomeActivity).checkInBackstack(
                            fragmentSubmitReview
                        )
                    }
                }
//                    }
//                }

//                    if (response.result.prescription != null && response.result.prescription.size > 0) {
//                        llPrescription.visibility = View.VISIBLE
//                        prescriptionRecyclerView.layoutManager = LinearLayoutManager(activity!!)
//                        val prescriptionList = response.result.prescription
//                        val adapter =
//                            ShowPrescriptionsImagesRecyclerviewAdapter(
//                                prescriptionList,
//                                activity!!
//                            )
//                        adapter.recyclerViewItemClickWithView2 =
//                            object : OnClickOfDoctorAppointment {
//                                override fun onItemClick(position: Int) {
//                                    val imageUrl =
//                                        context?.BaseImageUrls.USERIMAGE.url + adapter.todaysAppointList!![position].e_prescription!!
//                                    startActivity(
//                                        TransaprentPopUpActivityForImageShow.newIntent(
//                                            activity!!,
//                                            imageUrl
//                                        )
//                                    )
//                                }
//
//                                override fun onAcceptBtnClick(id: String, text: String) {
//
//                                }
//
//                                override fun onRejectBtnBtnClick(id: String, text: String) {
//
//                                }
//
//                            }
//                        prescriptionRecyclerView.adapter = adapter
//                    } else {
//                        llPrescription.visibility = View.GONE
//                    }

                if (appointmentStatus != null && !TextUtils.isEmpty(
                        appointmentStatus.trim()
                    ) &&
                    acceptanceStatus != null && !TextUtils.isEmpty(
                        acceptanceStatus.trim()
                    )
                ) {
                    if (appointmentId != null) {
                        setUpAcceptRejectSection(
                            appointmentStatus.toLowerCase(Locale.ROOT),
                            acceptanceStatus.toLowerCase(Locale.ROOT),
                            appointmentId?.toInt()!!
                        )
                    }
                }

//                        if (response.result.acceptanceStatus != null && response.result.acceptanceStatus != "Approved") {
//                            btnCancel.visibility = View.GONE
//                            btnRootscareDoctorConsulting?.visibility = View.INVISIBLE
//                        }

//                        if (isConsultationCompleted!!) {
//                            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
//                            anim.duration = 50 //You can manage the blinking time with this parameter
//
//                            anim.startOffset = 20
//                            anim.repeatMode = Animation.REVERSE
//                            anim.repeatCount = Animation.INFINITE
//                            tvBlink.startAnimation(anim)
//                            btnRootscareDoctorConsulting.visibility = View.GONE
//                            llPrescriptionText.visibility = View.VISIBLE
//                            ivPrescription.visibility = View.VISIBLE
//                            btnReview.visibility = View.VISIBLE
//                            btnCancel.visibility = View.GONE
//                        } else {
//                            btnRootscareDoctorConsulting.visibility = View.VISIBLE
//                            btnReview.visibility = View.GONE
//                            tvPrescriptionStatus.visibility = View.GONE
//                            btnCancel.visibility = View.VISIBLE
//                        }
//                    }
            }
        }
    }

    override fun successVideoPushResponse(videoPushResponse: VideoPushResponse) {
        baseActivity?.hideLoading()
        if (videoPushResponse.status!!) {
            val bundle = Bundle()
            bundle.putString("roomName", roomName)
            bundle.putString(
                "fromUserId",
                fragmentDoctorAppointmentDetailsViewModel?.appSharedPref?.userId
            )
            bundle.putString(
                "fromUserName",
                fragmentDoctorAppointmentDetailsViewModel?.appSharedPref?.userName
            )
            bundle.putString("toUserId", doctorId)
            bundle.putString("toUserName", doctorName)
            bundle.putString("orderId", orderId)
            bundle.putBoolean("isDoctorCalling", false)
            val intent = Intent(activity, VideoCallActivity::class.java)
            intent.putExtras(bundle)
            activity?.startActivityForResult(intent, VIDEO_CALL_REQUEST)
//            activity?.finish()
        } else {
            Toast.makeText(activity, videoPushResponse.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun errorVideoPushResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onThrowable(throwable: Throwable) {
        baseActivity?.hideLoading()
    }


    private var isPlaying = false
    private var seekPosition: Int = 0
    private var totalDuration: Int = 0
    private fun setUpAudioLayout(url: String) {
        with(fragmentDoctorAppointmentDetailsBinding!!) {
//            txtElapsedTime.base = seekPosition.toLong()
            setAudioFileToMediaPlayer(url)

            imgPlay.setOnClickListener {
                if (!isPlaying) {
                    startPlaying()
                    val resourceId: Int? = activity?.resources?.getIdentifier(
                        "pause",
                        "drawable", activity?.packageName
                    )
                    if (resourceId != null) {
                        imgPlay.setImageResource(resourceId)
                    }
                    isPlaying = true
                } else {
                    stopPlaying()
                    val resourceId: Int? = activity?.resources?.getIdentifier(
                        "play",
                        "drawable", activity?.packageName
                    )
                    if (resourceId != null) {
                        imgPlay.setImageResource(resourceId)
                    }
                    isPlaying = false
                }
            }

            //seekBar change listener
            seekBar.max = totalDuration
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    seekPosition = seekBar.progress
                    mediaPlayer?.seekTo(seekPosition)
                    fragmentDoctorAppointmentDetailsBinding?.txtElaspsedTime?.base =
                        SystemClock.elapsedRealtime() - seekPosition
                }
            })

            mediaPlayer?.setOnCompletionListener {
                resetAudioFileToMediaPlayer()
            }

        }
    }

    private fun setAudioFileToMediaPlayer(url: String) {
        with(fragmentDoctorAppointmentDetailsBinding!!) {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepare()
            totalDuration = mediaPlayer?.duration!!
            val minutes: Int = totalDuration / 1000 / 60
            val seconds: Int = totalDuration / 1000 % 60
            var tempMinutes = "00"
            var tempSeconds = "00"
            if (minutes != 0) {
                tempMinutes = if (minutes >= 10) {
                    minutes.toString()
                } else {
                    "0$minutes"
                }
            }
            if (seconds != 0) {
                tempSeconds = if (seconds >= 10) {
                    seconds.toString()
                } else {
                    "0$seconds"
                }
            }
            txtTime.text = "$tempMinutes : $tempSeconds"
        }
    }

    var handler: Handler? = null
    var runnable: Runnable? = null
    private fun startPlaying() {
        seekPosition = mediaPlayer?.currentPosition!!
        mediaPlayer?.start()

        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                try {
                    if (mediaPlayer?.currentPosition != null) {
                        seekPosition = mediaPlayer?.currentPosition!!
                        fragmentDoctorAppointmentDetailsBinding!!.seekBar.progress = seekPosition
                        handler?.postDelayed(this, 1000)
                    }
                } catch (ed: IllegalStateException) {
                    ed.printStackTrace()
                }
            }
        }
        handler?.postDelayed(runnable!!, 0)
//        fragmentDoctorAppointmentDetailsBinding?.txtElapsedTime?.start()
        fragmentDoctorAppointmentDetailsBinding?.txtElaspsedTime?.base =
            SystemClock.elapsedRealtime() - seekPosition
        fragmentDoctorAppointmentDetailsBinding?.txtElaspsedTime?.start()
    }

    private fun stopPlaying() {
        if (mediaPlayer !== null) {
            seekPosition = mediaPlayer?.currentPosition!!
            mediaPlayer?.pause()
            handler?.removeCallbacks(runnable!!)
            fragmentDoctorAppointmentDetailsBinding?.txtElaspsedTime?.stop()
        }
    }

    private fun resetAudioFileToMediaPlayer() {
        with(fragmentDoctorAppointmentDetailsBinding!!) {
            val resourceId: Int? = activity?.resources?.getIdentifier(
                "play",
                "drawable", activity?.packageName
            )
            if (resourceId != null) {
                imgPlay.setImageResource(resourceId)
            }
            isPlaying = false
            /*stopPlaying()
            seekPosition = 0
            seekBar.progress = seekPosition
            txtElapsedTime.base = SystemClock.elapsedRealtime()*/
            txtElaspsedTime.stop()
            val handler = Handler()
            handler.postDelayed({
                txtElaspsedTime.base = SystemClock.elapsedRealtime()
                mediaPlayer?.seekTo(0)
            }, 100)
//            txtElapsedTime.base = SystemClock.elapsedRealtime() - seekPosition
//            txtElapsedTime.base = seekPosition.toLong()
        }
    }

    override fun onStop() {
        stopPlaying()
        super.onStop()
    }

    override fun onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
            if (runnable != null && handler != null) {
                handler?.removeCallbacks(runnable!!)
            }
        }
        super.onDestroy()
    }


    private fun setUpAcceptRejectSection(
        appointmentStatus: String,
        acceptanceStatus: String,
        id: Int
    ) {
        with(fragmentDoctorAppointmentDetailsBinding!!) {
            if (serviceType?.toLowerCase(Locale.ROOT).equals("doctor")) {
                if (statusOfAppointmentForFlowChange != null && statusOfAppointmentForFlowChange?.equals(
                        "pending"
                    )!!
                ) {
                    llBottomUploadPrescription.visibility = View.GONE
                    if (acceptanceStatus.contains("pending")) {
                        llBottomAcceptReject.visibility = View.VISIBLE
                        btnAccept.setOnClickListener {
//                            acceptRejectAppointment(id, "Accept")
                        }
                        btnRejectt.setOnClickListener {
//                            acceptRejectAppointment(id, "Reject")
                        }
                    } else {
                        llBottomAcceptReject.visibility = View.GONE
                    }

                    /*if (appointmentStatus.contains("accepted")) {
                        llBottomAcceptReject.visibility = View.GONE
                    }else if (appointmentStatus.contains("complete")) {
                        llBottomAcceptReject.visibility = View.GONE
                    } else if (appointmentStatus.contains("reject")) {
                        llBottomAcceptReject.visibility = View.GONE
                    } else if (appointmentStatus.contains("cancel")) {
                        llBottomAcceptReject.visibility = View.GONE
                    } else if (appointmentStatus.contains("book")) {
                        llBottomAcceptReject.visibility = View.VISIBLE
                        btnAccept.setOnClickListener {
                            acceptRejectAppointment(id, "Accept")
                        }
                        btnRejectt.setOnClickListener {
                            acceptRejectAppointment(id, "Reject")
                        }
                    }*/
                } else if (statusOfAppointmentForFlowChange != null && statusOfAppointmentForFlowChange?.equals(
                        "today"
                    )!!
                ) {
                    llBottomUploadPrescription.visibility = View.VISIBLE
                    llBottomAcceptReject.visibility = View.GONE
                    if (appointmentStatus.contains("complete")) {
                        btnCompleted.visibility = View.GONE
                        btnUploadPrescription.visibility = View.VISIBLE
                        btnUploadPrescription.setOnClickListener {
                            openCamera()
                        }
                    } else {
                        btnCompleted.visibility = View.VISIBLE
                        btnUploadPrescription.visibility = View.GONE
                        btnCompleted.setOnClickListener {
//                            apiHitForCompleted(id.toString())
                        }
                    }
                } else {
                    llBottomAcceptReject.visibility = View.GONE
                    llBottomUploadPrescription.visibility = View.GONE
                }
            } else if (serviceType?.toLowerCase(Locale.ROOT).equals("nurse")) {
                if (statusOfAppointmentForFlowChange != null && statusOfAppointmentForFlowChange?.equals(
                        "pending"
                    )!!
                ) {
                    llBottomUploadPrescription.visibility = View.GONE
                    if (acceptanceStatus.contains("pending")) {
                        llBottomAcceptReject.visibility = View.VISIBLE
                        btnAccept.setOnClickListener {
//                            acceptRejectAppointment(id, "Accept")
                        }
                        btnRejectt.setOnClickListener {
//                            acceptRejectAppointment(id, "Reject")
                        }
                    } else {
                        llBottomAcceptReject.visibility = View.GONE
                    }


                    /*if (appointmentStatus.contains("complete")) {
                        llBottomAcceptReject.visibility = View.GONE
                    } else if (appointmentStatus.contains("reject")) {
                        llBottomAcceptReject.visibility = View.GONE
                    } else if (appointmentStatus.contains("cancel")) {
                        llBottomAcceptReject.visibility = View.GONE
                    } else if (appointmentStatus.contains("book")) {
                        llBottomAcceptReject.visibility = View.VISIBLE
                        btnAccept.setOnClickListener {
                            acceptRejectAppointment(id, "Accept")
                        }
                        btnRejectt.setOnClickListener {
                            acceptRejectAppointment(id, "Reject")
                        }
                    }*/
                } else {
                    llBottomAcceptReject.visibility = View.GONE
                    llBottomUploadPrescription.visibility = View.GONE
                }
            }
        }
    }


    private fun openCamera() {
//        mCameraIntentHelper!!.startCameraIntent()
        CropImage.activity()
            .setCropShape(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) CropImageView.CropShape.RECTANGLE
                else (CropImageView.CropShape.OVAL)
            )
            .setInitialCropWindowPaddingRatio(0F)
            .setAspectRatio(1, 1)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setActivityTitle("Crop")
            .setOutputCompressQuality(10)
            .start(activity!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            imageSelectionModel = AddlabTestImageSelectionModel()
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                imageSelectionModel?.imageDataFromCropLibrary = result
                val resultUri = result.uri
                if (resultUri != null) { // Get file from cache directory
                    Log.d(TAG, "--check_urii--  $resultUri")
                    FileNameInputDialog(
                        activity!!,
                        object : FileNameInputDialog.CallbackAfterDateTimeSelect {
                            override fun selectDateTime(dateTime: String) {
                                val fileCacheDir =
                                    File(activity?.cacheDir, resultUri.lastPathSegment)
                                if (fileCacheDir.exists()) {
//                                    imageSelectionModel?.file = fileCacheDir
                                    imageSelectionModel?.file =
                                        MyImageCompress.compressImageFilGottenFromCache(
                                            activity,
                                            resultUri,
                                            10
                                        )
                                    imageSelectionModel?.filePath = resultUri.toString()
                                    imageSelectionModel?.rawFileName = resultUri.lastPathSegment
                                    var tempNameExtension = ""
                                    if (resultUri.lastPathSegment?.contains(".jpg")!!) {
                                        tempNameExtension = ".jpg"
                                    } else if (resultUri.lastPathSegment?.contains(".png")!!) {
                                        tempNameExtension = ".png"
                                    }
                                    imageSelectionModel?.fileName =
                                        "${dateTime}_${
                                            DateTimeUtils.getFormattedDate(
                                                Date(),
                                                "dd/MM/yyyy_HH:mm:ss"
                                            )
                                        }${tempNameExtension}"
//                                    imageSelectionModel.fileNameAsOriginal = "${dateTime}${tempNameExtension}"
                                    imageSelectionModel?.fileNameAsOriginal = "$dateTime"
                                    if (activity?.contentResolver?.getType(resultUri) == null) {
                                        imageSelectionModel?.type = "image"
                                    } else {
                                        imageSelectionModel?.type =
                                            activity?.contentResolver?.getType(resultUri)
                                    }
                                    if (imageSelectionModel?.file != null && imageSelectionModel?.file?.exists()!!) {
//                                        uploadPrescription(imageSelectionModel!!)
                                    }
                                    Log.d("check_path", ": $resultUri")
                                    Log.d("check_file_get", ": $fileCacheDir")
                                } else {
                                    Log.d("file_does_not_exists", ": " + true)
                                }
                                hideKeyboard()
                            }
                        }).show(activity!!.supportFragmentManager)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.d("check_error", ": " + error.message)
            }
        } else if (requestCode == VIDEO_CALL_REQUEST) {
//            isConsultationCompleted = true
            apiHitForDetails()
        }
    }


    private fun blink() {

    }

//    private fun uploadPrescription(imageSelectionModel: AddlabTestImageSelectionModel) {
//        if (fragmentDoctorAppointmentDetailsViewModel?.appSharedPref?.loginUserId != null && patientIdForPrescriptionUpload != null) {
//            baseActivity?.showLoading()
//            baseActivity?.hideKeyboard()
//            val doctor_id = RequestBody.create(
//                "multipart/form-data".toMediaTypeOrNull(),
//                fragmentDoctorAppointmentDetailsViewModel?.appSharedPref?.loginUserId!!
//            )
//            val patient_id = RequestBody.create(
//                "multipart/form-data".toMediaTypeOrNull(),
//                patientIdForPrescriptionUpload!!
//            )
//            val appointment_id = RequestBody.create(
//                "multipart/form-data".toMediaTypeOrNull(),
//                appointmentId!!
//            )
//            val prescription_number = imageSelectionModel.fileNameAsOriginal?.let {
//                RequestBody.create(
//                    "multipart/form-data".toMediaTypeOrNull(),
//                    it
//                )
//            }
////            fileNameForTempUse = imageSelectionModel.fileNameAsOriginal
//
//            val image =
//                RequestBody.create(
//                    "multipart/form-data".toMediaTypeOrNull(),
//                    imageSelectionModel.file
//                )
//            val imageMultipartBody: MultipartBody.Part? =
//                MultipartBody.Part.createFormData(
//                    "e_prescription",
//                    imageSelectionModel.file?.name,
//                    image
//                )
//            fragmentDoctorAppointmentDetailsViewModel?.uploadPrescription(
//                patient_id,
//                doctor_id,
//                appointment_id,
//                prescription_number,
//                imageMultipartBody
//            )
//        }
//    }

//    private fun apiHitForCompleted(id: String) {
//        val request = CommonUserIdRequest()
//        request.id = id
//        if (isNetworkConnected) {
//            baseActivity?.showLoading()
//            fragmentDoctorAppointmentDetailsViewModel!!.apiHitForMarkAsComplete(
//                request
//            )
//        } else {
//            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }

//    private fun acceptRejectAppointment(id: Int, status: String) {
//        CommonDialog.showDialog(activity!!, object :
//            DialogClickCallback {
//            override fun onDismiss() {
//
//            }
//
//            override fun onConfirm() {
//                if (isNetworkConnected) {
//                    baseActivity?.showLoading()
//                    val updateAppointmentRequest = UpdateAppointmentRequest()
//                    updateAppointmentRequest.id = id.toString()
//                    updateAppointmentRequest.acceptanceStatus = status
//                    fragmentDoctorAppointmentDetailsViewModel!!.apiupdatedoctorappointmentrequest(
//                        updateAppointmentRequest
//                    )
//                } else {
//                    Toast.makeText(
//                        activity,
//                        "Please check your network connection.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//
//        }, "Confirmation", "Are you sure to ${status.toLowerCase(Locale.ROOT)} this appointment?")
//    }

//    override fun successGetDoctorRequestAppointmentUpdateResponse(
//        getDoctorRequestAppointmentResponse: CommonResponse?
//    ) {
//        baseActivity?.hideLoading()
//        apiHitForDetails()
//    }

    override fun errorGetDoctorRequestAppointmentResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
    }

    override fun onSuccessMarkAsComplete(response: CommonResponse) {
        baseActivity?.hideLoading()
        apiHitForDetails()
    }

    override fun errorGetDoctorTodaysAppointmentResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
    }

    override fun onSuccessUploadPrescription(response: CommonResponse) {
        baseActivity?.hideLoading()
        apiHitForDetails()
    }

    override fun successAppointmentCancelResponse(appointmentCancelResponse: AppointmentCancelResponse?) {
        baseActivity?.hideLoading()
        if (appointmentCancelResponse?.code.equals("200")) {
            Toast.makeText(activity, appointmentCancelResponse?.message, Toast.LENGTH_SHORT).show()
            if (isNetworkConnected) {

            } else {
                Toast.makeText(
                    activity,
                    "Please check your network connection.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            Toast.makeText(activity, appointmentCancelResponse?.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun errorAppointmentHistoryResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDateFromString(
        inputFormat: String,
        outputFormat: String,
        inputDate: String
    ): String {
        val parsed: Date?
        var outputDate = ""
        val dfInput =
            SimpleDateFormat(inputFormat, Locale.ENGLISH)
        val dfOutput =
            SimpleDateFormat(outputFormat, Locale.ENGLISH)
        try {
            parsed = dfInput.parse(inputDate)
            outputDate = dfOutput.format(parsed)
        } catch (e: ParseException) {
        }
        return outputDate
    }

}