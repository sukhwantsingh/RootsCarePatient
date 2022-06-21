package com.rootscare.ui.newaddition.providerlisting


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.dialog.CommonDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.deletepatientfamilymemberrequest.DeletePatientFamilyMemberRequest
import com.rootscare.data.model.api.request.doctorrequest.getpatientfamilymemberrequest.GetPatientFamilyMemberRequest
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.ResultItem
import com.rootscare.databinding.LayoutNewProviderBookingForDoctorBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.newaddition.appointments.ModelAppointmentDetails
import com.rootscare.ui.newaddition.appointments.adapter.AdapterPaymentSplitting
import com.rootscare.ui.newaddition.providerlisting.adapter.*
import com.rootscare.ui.newaddition.providerlisting.models.*
import com.rootscare.ui.newaddition.providerlisting.patientaddition.FragmentAddPatient
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback
import com.rootscare.utilitycommon.*
import com.rootscare.utils.ManagePermissions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class FragmentProvderBookingForDoctor : BaseFragment<LayoutNewProviderBookingForDoctorBinding, ProviderListingViewModel>(),
    ProviderListingNavigator {
    private var binding: LayoutNewProviderBookingForDoctorBinding? = null
    private var mViewModel: ProviderListingViewModel? = null

    private var selectedYear = 0
    private var selectedmonth = 0
    private var selectedday = 0

    var bookingInitialData: ModelBookingIntialForDoctor? = null
    var isLoadedOnce = false


    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.layout_new_provider_booking_for_doctor
    override val viewModel: ProviderListingViewModel
        get() {
            mViewModel =  ViewModelProviders.of(this).get(ProviderListingViewModel::class.java)
            return mViewModel as ProviderListingViewModel
        }
    private var providerId: String = ""
    private var bookingType = ""
    private var providerType = ""

    private var onlineBooking = ""
    private var homeVisitBooking = ""
    private var totalPrice = ""
    private var forApivatPrice = ""
    private var taskPriceTotal = ""
    private var subTotalPrice = ""

    companion object {
        private const val IMAGE_DIRECTORY = "/demonuts"

        const val ARG_PROVIDER_ID = "ARG_PROVIDER_ID"
        const val ARG_PROVIDER_TYPE = "ARG_PROVIDER_TYPE"
        const val ARG_BOOKING_TYPE = "ARG_BOOKING_TYPE"
        const val ARG_BOOKING_ONLINE = "ARG_BOOKING_ONLINE"
        const val ARG_BOOKING_HOMEVISIT = "ARG_BOOKING_HOMEVISIT"

        fun newInstance(providerId: String,  bookType: String = "", userType: String, onlineBkg: String, homeVBkg: String): FragmentProvderBookingForDoctor {
            val args = Bundle()
            args.putString(ARG_PROVIDER_ID, providerId)
            args.putString(ARG_BOOKING_TYPE, bookType)
            args.putString(ARG_PROVIDER_TYPE, userType)
            args.putString(ARG_BOOKING_ONLINE, onlineBkg)
            args.putString(ARG_BOOKING_HOMEVISIT, homeVBkg)
            val fragment = FragmentProvderBookingForDoctor()
            fragment.arguments = args
            return fragment
        }
    }

    private var mOnlineBasePaymentList = ArrayList<ModelAppointmentDetails.Result.TaskDetail?>()
    private var mHomeVisitPaymentList = ArrayList<ModelAppointmentDetails.Result.TaskDetail?>()

    private val mAdapterFamilyMembers: AdapterFamilyMembers by lazy { AdapterFamilyMembers() }
    private val mAdapDateSlots: AdapterDateSlotList by lazy { AdapterDateSlotList() }
    private val mAdapTimeSlots: AdapterTimeSlotList by lazy { AdapterTimeSlotList() }
    private val mAdapterPayment: AdapterPaymentSplitting by lazy { AdapterPaymentSplitting() }

    private var dateSelected = ""
    private var hourTaskIdSelected = ""
    private var vatPricePercent = ""
    private var distancefare = ""

    // Recorder setup
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var fileName: String? = null
    private var recordingFile: File? = null
    private var lastProgress = 0
    private val mHandler = Handler(Looper.getMainLooper())
    private val RECORD_AUDIO_REQUEST_CODE = 123
    private var isPlaying = false
    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 123
    private var symptomsRecordingFile: RequestBody? = null
    private var audioMulitpart: MultipartBody.Part? = null

    private val GALLERY = 1
    private val CAMERA = 2
    var imageFile: File? = null
    private var prescriptionimage: RequestBody? = null
    private var imageMulitpart: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this
        providerId = arguments?.getString(ARG_PROVIDER_ID) ?: ""
        providerType = arguments?.getString(ARG_PROVIDER_TYPE) ?: ""
        bookingType = arguments?.getString(ARG_BOOKING_TYPE) ?: ""

        onlineBooking = arguments?.getString(ARG_BOOKING_ONLINE) ?: "1"
        homeVisitBooking = arguments?.getString(ARG_BOOKING_HOMEVISIT) ?: "1"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToRecordAudio()
        }
        val list = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this.requireActivity(), list, PermissionsRequestCode)

        //check permissions states
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            managePermissions.checkPermissions()

        initViews()
        callingApi()
    }

    private fun proceedToPayment() {
        if (isNetworkConnected) {
            var tsk_type = ""
            var appoint_type = ""
            var taskSelectedBookingIds = ""
            var taskSelectedPrice = ""
            val timeSlotSelected = mAdapTimeSlots.updatedArrayList.find { it?.isChecked == true }?.name ?: ""
            val symptomText = binding?.edtDescription?.text.toString().trim()

            when {
                bookingType.equals(BookingTypes.ONLINE_CONS.get(), ignoreCase = true) -> {
                    tsk_type = BookingTypes.ONLINE_CONS.getApiType()
                    appoint_type = "online"
                }
                bookingType.equals(BookingTypes.HOME_VISIT.get(), ignoreCase = true) -> {
                    tsk_type = BookingTypes.HOME_VISIT.getApiType()
                    appoint_type = "offline"
                }
            }

            taskSelectedBookingIds =
                mAdapterPayment.updatedArrayList.joinToString { it?.id.toString() }
            taskSelectedPrice =
                mAdapterPayment.updatedArrayList.joinToString { it?.price.toString() }

            if (recordingFile != null) {
                symptomsRecordingFile =
                    recordingFile?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                audioMulitpart = MultipartBody.Part.createFormData(
                    "symptom_recording", recordingFile?.name, symptomsRecordingFile!!
                )
            } else {
                symptomsRecordingFile = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                audioMulitpart = MultipartBody.Part.createFormData(
                    "symptom_recording", "", symptomsRecordingFile!!
                )
            }

            if (imageFile != null) {
                prescriptionimage =
                    imageFile?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                imageMulitpart = MultipartBody.Part.createFormData(
                    "upload_prescription", imageFile?.name, prescriptionimage!!
                )
            } else {
                prescriptionimage = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                imageMulitpart = MultipartBody.Part.createFormData(
                    "upload_prescription", "", prescriptionimage!!
                )
            }

            when {
                taskSelectedBookingIds.isBlank() -> {
                    showToast(getString(R.string.please_select_task))
                    return
                }
                timeSlotSelected.isBlank() -> {
                    showToast(getString(R.string.please_select_timeslot))
                    return
                }

                else -> {
                    val RproviderType = providerType.asReqBody()
                    val RloginUserId = mViewModel?.appSharedPref?.userId?.asReqBody()
                    val RfamilyMembId = (mViewModel?.mlFamilyMemberId?.value ?: "").asReqBody()
                    val RproviderId = providerId.asReqBody()
                    val RtaskSelectedBookingIds = taskSelectedBookingIds.asReqBody()
                    val Rtsk_type = tsk_type.asReqBody()
                    val Rappoint_type = appoint_type.asReqBody()
                    val RdateSelected = dateSelected.asReqBody()
                    val RtimeSlotSelected = timeSlotSelected.asReqBody()
                    val RtaskSelectedPrice = taskSelectedPrice.asReqBody()
                    val RtaskPriceTotal = taskPriceTotal.asReqBody()
                    val RforApivatPrice = forApivatPrice.asReqBody()
                    val RvatPricePercent = vatPricePercent.asReqBody()
                    val Rdistancefare = distancefare.asReqBody()
                    val RsubTotalPrice = subTotalPrice.asReqBody()
                    val RtotalPrice = totalPrice.asReqBody()
                    val RsymptomText = symptomText.asReqBody()
                    val RCurrency = mViewModel?.appSharedPref?.currencySymbol?.asReqBody()

                    baseActivity?.hideKeyboard(); baseActivity?.showLoading()
                    mViewModel?.apiBookAppointmentforDoctor(
                    RproviderType, RCurrency, RloginUserId, RfamilyMembId, RproviderId, RtaskSelectedBookingIds,
                    Rtsk_type, Rappoint_type, RdateSelected, RtimeSlotSelected, RtaskSelectedPrice,
                    RtaskPriceTotal, RforApivatPrice, RvatPricePercent, Rdistancefare,RsubTotalPrice,
                    RtotalPrice, RsymptomText, audioMulitpart, imageMulitpart)
                 }

                }

            }
        }

    private fun calculatePrice() {
        when {
            bookingType.equals(BookingTypes.ONLINE_CONS.get(), ignoreCase = true) -> {
                val vPricePerc = vatPricePercent.toDoubleOrNull() ?: 0.0
              //  val disPrice = distancefare.toDoubleOrNull() ?: 0.0
                var tskPrice = 0.0

                mAdapterPayment.updatedArrayList.forEach { tskPrice += it?.price?.toDoubleOrNull() ?: 0.0 }

              //  val subTotal = tskPrice.plus(disPrice)
                val subTotal = tskPrice
                val vPriceValue = (subTotal * vPricePerc) / 100

                val gTotal = subTotal.plus(vPriceValue)

                binding?.run {
                  //  tvDisFare.setAmount(disPrice)
                    tvDisFare.visibility = View.GONE
                    tvhDisFare.visibility = View.GONE

                    tvVat.setAmountWithCurrency(vPriceValue, mViewModel?.appSharedPref?.currencySymbol)
                    tvTotalPrice.setAmountWithCurrency(gTotal.toString(), mViewModel?.appSharedPref?.currencySymbol)
                }
                totalPrice = gTotal.toString()
                forApivatPrice = vPriceValue.toString()

                taskPriceTotal = tskPrice.toString()
                subTotalPrice = subTotal.toString() // vat plus task list price
            }
            bookingType.equals(BookingTypes.HOME_VISIT.get(), ignoreCase = true) -> {
                val vPricePerc = vatPricePercent.toDoubleOrNull() ?: 0.0
                val disPrice = distancefare.toDoubleOrNull() ?: 0.0
                var hourPrice = 0.0
                mAdapterPayment.updatedArrayList.forEach { hourPrice += it?.price?.toDoubleOrNull() ?: 0.0 }

                val subTotal = hourPrice.plus(disPrice)
                val vPriceValue = (subTotal * vPricePerc) / 100
                val gTotal = subTotal.plus(vPriceValue)

                // show to the views
                binding?.run {
                    tvDisFare.visibility = View.VISIBLE
                    tvhDisFare.visibility = View.VISIBLE

                    tvVat.setAmountWithCurrency(vPriceValue, mViewModel?.appSharedPref?.currencySymbol)
                    tvDisFare.setAmountWithCurrency(disPrice, mViewModel?.appSharedPref?.currencySymbol)
                    tvTotalPrice.setAmountWithCurrency(gTotal.toString(), mViewModel?.appSharedPref?.currencySymbol)
                }
                totalPrice = gTotal.toString()
                forApivatPrice = vPriceValue.toString()
                taskPriceTotal = hourPrice.toString()
                subTotalPrice = subTotal.toString() // vat plus task list price
            }
        }


    }

    private fun callingApi() {
        if(bookingInitialData != null) bindViews(bookingInitialData?.result)
        else apiHitForInitialDetails()
    }

    override fun onResume() {
        super.onResume()
        if(FragmentProvderBooking.IS_MEMBER_ADDED){
            FragmentProvderBooking.IS_MEMBER_ADDED = false
            apiHitForFamilyMemberList()
        }
    }

    private fun initViews() {
        binding?.run {
           attachPreAdapters()
            imgPatient.setRemoteProfileImage(mViewModel?.appSharedPref?.userImage)
            tvPatientName.text = mViewModel?.appSharedPref?.userName
            tvSelectDate.text = if (dateSelected.isBlank()) { dateSelected = getCurrentAppDate(); dateSelected } else dateSelected

            mViewModel?.mlFamilyMemberId?.observe(viewLifecycleOwner) {
                binding?.llPatient?.setBackgroundResource(
                    if (it.isBlank()) {
                        R.drawable.sq_back_patient_selected
                    } else 0
                )
            }
            imgPatient.setOnClickListener {
                mViewModel?.mlFamilyMemberId?.value = ""
                mAdapterFamilyMembers.updateStatus(-1, true)
            }
            imgAddPatient.setOnClickListener {(activity as? HomeActivity)?.checkInBackstack(
                FragmentAddPatient.newInstance(providerId))}

            btnSubmit.setOnClickListener { proceedToPayment() }

            imageViewRecord.setOnClickListener {
                prepareForRecording()
                startRecording()
            }

            imageViewStop.setOnClickListener {
                prepareForStop()
                stopRecording()
            }

            imageViewPlay.setOnClickListener {
                if (!isPlaying && fileName != null) {
                    isPlaying = true
                    startPlaying()
                } else {
                   chronometerTimer.stop()
                    isPlaying = false
                    stopPlaying()
                }
            }

         tvUpload.setOnClickListener { showPictureDialog() }

        }
    }

    private fun setupTab(){
       binding?.run {
           val tabTitles: MutableList<String> = when {
               onlineBooking.equals("0", ignoreCase = true) &&
               homeVisitBooking.equals("0", ignoreCase = true) -> {
                   tablayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                   arrayListOf(BookingTypes.ONLINE_CONS.getDisplayHeading(), BookingTypes.HOME_VISIT.getDisplayHeading())
               }
               onlineBooking.equals("0", ignoreCase = true) &&
               (homeVisitBooking.isBlank() || homeVisitBooking.equals("1", ignoreCase = true)) -> {
                   arrayListOf(BookingTypes.ONLINE_CONS.getDisplayHeading())
               }
               homeVisitBooking.equals("0", ignoreCase = true) &&
               (onlineBooking.isBlank() || onlineBooking.equals("1", ignoreCase = true)) -> {
                   arrayListOf(BookingTypes.HOME_VISIT.getDisplayHeading())
               }
               else -> {
                   tablayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                   arrayListOf(BookingTypes.ONLINE_CONS.getDisplayHeading(), BookingTypes.HOME_VISIT.getDisplayHeading())
               }
           }
           for (i in tabTitles.indices) {
               tablayout.newTab().setText(tabTitles[i]).let { tablayout.addTab(it, i) }
           }
           tablayout.setTabTextColors(
               ContextCompat.getColor(requireActivity(), R.color.color_tab_text_normal),
               ContextCompat.getColor(requireActivity(), R.color.color_tab_text_selected)
           )
           tablayout.setSelectedTabIndicatorColor(Color.parseColor("#0888D1"))
           tablayout.addOnTabSelectedListener(object : AppTabListener {
               override fun onTabSelected(tab: TabLayout.Tab) {
                   if (tablayout.tabCount == 2) {
                       when (tab.position) {
                           0 -> {
                               bookingType = BookingTypes.ONLINE_CONS.get()
                               calculatePrice()
                           }
                           else -> {
                               bookingType = BookingTypes.HOME_VISIT.get()
                               calculatePrice()
                           }
                       }

                       updatedPaymentList(null, bookingType)
                       updateTimeSlots(bookingType)
                   }
               }
           })
           when {
               bookingType.equals(BookingTypes.HOME_VISIT.get(), ignoreCase = true) -> {
                   if (tablayout.tabCount == 2) {
                       tablayout.getTabAt(1)?.select()
                   } else tablayout.getTabAt(0)?.select()
               }
               else -> tablayout.getTabAt(0)?.select()
           }
       }
   }

    private fun attachPreAdapters() {
        binding?.run {
            rvPayments.adapter = mAdapterPayment
            mAdapterPayment.mCurrency = mViewModel?.appSharedPref?.currencySymbol
            rvPatientList.adapter = mAdapterFamilyMembers
            rvTimeSlots.adapter = mAdapTimeSlots
            rvDateSlots.adapter = mAdapDateSlots
            setDateSlotsListRecycle()

        }
    }

    private fun setOnlineTaskList(mList: ArrayList<ModelBookingIntialForDoctor.Result.OnlineBaseTask?>?) {
        binding?.run {
            if (mList.isNullOrEmpty().not() && mOnlineBasePaymentList.isNullOrEmpty()) {
                val arrList: ArrayList<ModelAppointmentDetails.Result.TaskDetail?> = ArrayList()
                mList?.forEach {
                    val mNode = ModelAppointmentDetails.Result.TaskDetail(it?.id ?: "",it?.duration ?: "",it?.task_price ?: "0" )
                    arrList.add(mNode)
               }
                mOnlineBasePaymentList.addAll(arrList)
            }
        }
    }

    private fun setHomeVisitList(mList: ArrayList<ModelBookingIntialForDoctor.Result.HomeVisitTask?>?) {
        binding?.run {
            if (mList.isNullOrEmpty().not() && mHomeVisitPaymentList.isNullOrEmpty()) {
                val arrList: ArrayList<ModelAppointmentDetails.Result.TaskDetail?> = ArrayList()
                mList?.forEach {
                    val mNode = ModelAppointmentDetails.Result.TaskDetail(
                        it?.id.orEmpty(), it?.duration,it?.task_price ?: "0")
                    arrList.add(mNode)
                }
                mHomeVisitPaymentList.addAll(arrList)
            }
        }
    }

    private fun updatedPaymentList(node: ModelTaskListWithPrice.Result?, updateDataFor: String) {
        when {
            updateDataFor.equals(BookingTypes.ONLINE_CONS.get(), ignoreCase = true) -> {
                mAdapterPayment.updatedData(mOnlineBasePaymentList)
            }
            updateDataFor.equals(BookingTypes.HOME_VISIT.get(), ignoreCase = true) -> {
                mAdapterPayment.updatedData(mHomeVisitPaymentList)
            }
            else -> Unit
        }

        calculatePrice()
    }

    private fun setupFamilyMemberRecycle(patientFamilyMemberList: ArrayList<ResultItem?>?) {
        binding?.run {
            if (patientFamilyMemberList.isNullOrEmpty().not() && ((patientFamilyMemberList?.size?: 0) != mAdapterFamilyMembers.updatedArrayList.size)) {
                val arrList: ArrayList<ModelPatientFamilyMembers.Result?> = ArrayList()
                patientFamilyMemberList?.forEach {
                    val node = ModelPatientFamilyMembers.Result(
                        it?.id,
                        it?.patientId,
                        it?.firstName,
                        it?.lastName,
                        it?.image
                    )
                    arrList.add(node)
                }
                mAdapterFamilyMembers.updatedData(arrList)
                try { startSmoothScroll(0, binding?.rvPatientList) } catch (e: Exception) { println(e) }
                mViewModel?.mlFamilyMemberId?.value = ""
                mAdapterFamilyMembers.updateStatus(-1, true)
                mAdapterFamilyMembers.mCallback = object : OnFamiliyMemberCallback {
                    override fun onItemClick(resultItem: ModelPatientFamilyMembers.Result?) {
                        mViewModel?.mlFamilyMemberId?.value = resultItem?.id ?: ""
                    }
                    override fun onDeleteButtonClick(id: String,name:String) {
                        CommonDialog.showDialog( requireContext(), object : DialogClickCallback {
                                override fun onConfirm() {
                                    if (isNetworkConnected) {
                                        baseActivity?.showLoading()
                                        val delReq = DeletePatientFamilyMemberRequest()
                                        delReq.id = id
                                        mViewModel?.apiDeleteFamiliyMember(delReq)
                                    } else {
                                        showToast(getString(R.string.check_network_connection))
                                    }
                                }
                            },
                            "${getString(R.string.delete)} $name",
                            getString(R.string.sure_to_delete_family_member), getString(R.string.delete)
                        )
                    }

                    /*
                     override fun onEditButtonClick(modelOfGetAddPatientList: ResultItem) {
                         val id = modelOfGetAddPatientList.id ?: ""
                         val imageName = modelOfGetAddPatientList.image ?: ""
                         val firstName = modelOfGetAddPatientList.firstName ?: ""
                         val lastName = modelOfGetAddPatientList.lastName ?: ""
                         val age = modelOfGetAddPatientList.age ?: ""
                         val gender = modelOfGetAddPatientList.gender ?: ""

                         (activity as HomeActivity).checkInBackstack(
                             FragmentEditPatientFamilyMember.newInstance(
                                 providerId, id, imageName, firstName, lastName, age, gender
                             )
                         )
                     } */

                }
            }
        }

    }

    override fun successGetPatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?) {
        baseActivity?.hideLoading()
        if (getPatientFamilyListResponse?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            if (getPatientFamilyListResponse?.result.isNullOrEmpty().not()) {
                setupFamilyMemberRecycle(getPatientFamilyListResponse?.result)
            }
        }
    }

    override fun successDeletePatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?) {
        baseActivity?.hideLoading()
        apiHitForFamilyMemberList()
    }

    override fun onSuccessInitialData(response: ModelBookingIntialForDoctor?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            if (response?.result != null) {
                apiHitForFamilyMemberList()
                bookingInitialData = response
                bindViews(response.result)
                isLoadedOnce = true
            } else {
                CommonDialog.showDialogWithSingleButton(requireActivity(), object : DialogClickCallback {
                    override fun onConfirm() { (activity as? HomeActivity)?.onBackPressed() }
                }, null, response?.message?:getString(R.string.something_went_wrong))
            }
        } else {
            CommonDialog.showDialogWithSingleButton(requireActivity(), object : DialogClickCallback {
                override fun onConfirm() { (activity as? HomeActivity)?.onBackPressed() }
            }, null, response?.message?:getString(R.string.something_went_wrong))
        }
    }

    private var mOnlineSlotList = ArrayList<String?>()
    private var mHomeVisitSlotList = ArrayList<String?>()

    private fun bindViews(result: ModelBookingIntialForDoctor.Result?) {
        binding?.run {
            result?.let {
                imgProfile.setCircularRemoteImageWithNoImage(it.image)
                tvUsername.text = it.provider_name
                tvhTypeExperience.text = "${it.dispaly_provider_type} - ${it.experience}"
                tvhCertificateDegree.text = it.qualification

                onlineBooking = it.online_enable ?: "1" // needs to change for online
                homeVisitBooking = it.home_visit_enable ?: "1" // needs to change for home_visit

                setupTab()
                // payment initial bind views
                tvhDisFare.text = it.distance_fare_text
                tvhVat.text = it.vat_text

                distancefare = if (it.distance_fare != null && it.distance_fare < 1) "0" else it.distance_fare.toString()
                vatPricePercent = if (it.vat_price.isNullOrBlank().not() && it.vat_price.equals("0", ignoreCase = true )) "0" else it.vat_price ?: "0"

               setOnlineTaskList(it.online_base_task)  // online base tasks
               setHomeVisitList(it.home_visit_task)  // home visit base task listing

               if(isLoadedOnce.not()) {
                it.online_task_time?.split(",")?.map { it1 -> it1.trim() }?.let { it2 ->
                    mOnlineSlotList.clear(); mOnlineSlotList.addAll(it2)
                }
                it.home_visit_time?.split(",")?.map { it1 -> it1.trim() }?.let { it2 ->
                    mHomeVisitSlotList.clear(); mHomeVisitSlotList.addAll(it2)
                }
               }

                updateTimeSlots(bookingType)
                updatedPaymentList(null, bookingType)
            }
        }
    }

    private fun updateTimeSlots(updateDataFor: String) {
        when {
            updateDataFor.equals(BookingTypes.ONLINE_CONS.get(), ignoreCase = true) -> {
                setTimeSlotsListRecycle(mOnlineSlotList)
            }
            updateDataFor.equals(BookingTypes.HOME_VISIT.get(), ignoreCase = true) -> {
                setTimeSlotsListRecycle(mHomeVisitSlotList)
            }
        }
    }

    private fun setTimeSlotsListRecycle(mList: ArrayList<String?>?) {
        binding?.run {
            if (mList.isNullOrEmpty().not()) {
                val arrList: ArrayList<ModelTaskListWithPrice.Result?> = ArrayList()
                mList?.forEach {
                if(isTimeAfterCurrent("$dateSelected $it")) {
                    arrList.add(ModelTaskListWithPrice.Result("", it?.trim(), "", false))
                } }

                if(arrList.isNotEmpty()){
                    rvTimeSlots.visibility = View.VISIBLE
                    tvNoFoundSlotTiming.visibility = View.GONE

                 if (mAdapTimeSlots.updatedArrayList.isNullOrEmpty()) {
                       mAdapTimeSlots.loadDataIntoList(arrList) } else mAdapTimeSlots.updateData(arrList)
                } else {
                    rvTimeSlots.visibility = View.GONE
                    tvNoFoundSlotTiming.visibility = View.VISIBLE
                }
                startSmoothScroll(0, rvTimeSlots)
            } else {
                rvTimeSlots.visibility = View.GONE
                tvNoFoundSlotTiming.visibility = View.VISIBLE
            }
        }
    }

    private fun setDateSlotsListRecycle() {
        binding?.run {
            val arrList = getDateSlots()
            if (arrList.isNullOrEmpty().not()) {
                if (mAdapDateSlots.updatedArrayList.isNullOrEmpty()) {
                    arrList[0]?.isSelected_ = true
                    mAdapDateSlots.loadDataIntoList(arrList)
                    mAdapDateSlots.mCallback = object : OnDateSlotCallback {
                        override fun onDateSlotClicked(node: ModelDateSlots?) {
                            binding?.tvSelectDate?.text = node?.dateValue
                            apiFetchTimeSlots(node?.dateValue ?: "")
                        }
                    }
                }
                startSmoothScroll(0, rvDateSlots)
            }
        }

    }

    override fun onSuccessBooking(response: CommonResponse?) {
        baseActivity?.hideLoading()
      if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
          try {
//           childFragmentManager.popBackStackImmediate()
           (activity as? HomeActivity)?.openCartFromBottom()
          } catch (e:Exception) {
              println(e)
          }
      } else {
          CommonDialog.showDialogWithProceed(requireActivity(), object : DialogClickCallback {
          }, getString(R.string.failure), response?.message ?: getString(R.string.something_went_wrong))
      }
    }

    override fun errorInAPi(throwable: Throwable?) {
        baseActivity?.hideLoading()
        val badgateWay = DialogBadGateway.newInstance(btsCallback)
        badgateWay.show(childFragmentManager, "BadgatewayDialog")
    }

    private val btsCallback = object : OnBottomSheetCallback {
        override fun onGoBack() { (activity as? HomeActivity)?.onBackPressed() }
        override fun onRetry() { apiHitForInitialDetails() }
    }

    private fun apiHitForFamilyMemberList() {
        if (isNetworkConnected) {
            //  baseActivity?.showLoading()
            val getPatientFamilyMemberRequest = GetPatientFamilyMemberRequest()
            getPatientFamilyMemberRequest.userId = mViewModel?.appSharedPref?.userId
            mViewModel?.apipatientfamilymember(getPatientFamilyMemberRequest)

        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }

    private fun apiHitForInitialDetails() {
        if (isNetworkConnected) {

            val jsonObject = JsonObject().apply {
                addProperty("provider_id", providerId)
                addProperty("service_type", providerType)
                addProperty("lgoin_user_id", mViewModel?.appSharedPref?.userId)
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            baseActivity?.showLoading()

           mViewModel?.apiBookingInitialDetailsForDoc(body)

        } else {
            showToast(getString(R.string.check_network_connection))

        }
    }

    private fun apiFetchTimeSlots(dtSel: String) {
        if (dtSel.equals(dateSelected, ignoreCase = true)) return

        if (isNetworkConnected) {
            dateSelected = dtSel
            baseActivity?.showLoading()
            val jsonObject = JsonObject().apply {
                addProperty("date", dtSel)
                addProperty("service_type", providerType)
                addProperty("provider_id", providerId)
             }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            mViewModel?.apiBookingTimeSlotsForDoc(body)

        } else {
            showToast(getString(R.string.check_network_connection))

        }
    }

    override fun onSuccessBookingTimeSlots(response: ModelTImeSlotsForDoctor?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            if (response?.result != null) {
                mHomeVisitSlotList.clear(); mOnlineSlotList.clear()

                response.result.online_task_time?.let { tTime ->
                    if(tTime.isBlank()) { mOnlineSlotList.clear() } else {
                        mOnlineSlotList.addAll(tTime.split(",").map { it1 -> it1.trim() })
                    }
                }
                response.result.home_visit_time?.let { hTime ->
                    if(hTime.isBlank()) { mHomeVisitSlotList.clear() } else {
                        mHomeVisitSlotList.addAll(hTime.split(",").map { it1 -> it1.trim()})
                    }
                }

                updateTimeSlots(bookingType)
            } else showToast(response?.message?:getString(R.string.something_went_wrong))

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun getPermissionToRecordAudio() {
       if (ContextCompat.checkSelfPermission(
                this.requireActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this.requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this.requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { 
                ActivityCompat.requestPermissions( requireActivity(), arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                RECORD_AUDIO_REQUEST_CODE
            )
        }
    }

    // Receive the permissions request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,  grantResults: IntArray) {
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions.processPermissionsResult(grantResults)

                if (isPermissionsGranted) { showToast(getString(R.string.permission_granted)) } else {showToast(getString(R.string.permission_denied)) }
                return
            }
        }

        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.size == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
              showToast(getString(R.string.record_audio_perm_granted))
            } else {
               showToast(getString(R.string.must_give_permission))
                //  finishAffinity()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (mRecorder != null) {
            mRecorder?.stop()
            mRecorder?.reset()
            mRecorder?.release()
            mRecorder = null
        }
        if (mPlayer != null) {
            try {
                mPlayer?.stop()
                mPlayer?.reset()
                mPlayer!!.release()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            mPlayer = null
        }
    }

    private fun prepareForStop() {
        TransitionManager.beginDelayedTransition(binding?.llRecord)
        binding?.imageViewRecord?.visibility = View.VISIBLE
        binding?.imageViewStop?.visibility = View.GONE
        binding?.linearLayoutPlay?.visibility = View.VISIBLE
    }

    private fun stopPlaying() {
        try {
            mPlayer!!.release()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        mPlayer = null
        //showing the play button
        binding?.imageViewPlay?.setImageResource(R.drawable.play)
        binding?.chronometerTimer?.stop()
    }

    private fun stopRecording() {
        try {
            mRecorder?.stop()
            mRecorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mRecorder = null
        // starting the chronometer
        binding?.chronometerTimer?.stop()
        binding?.chronometerTimer?.base = SystemClock.elapsedRealtime()
        // showing the play button
        Toast.makeText(activity, "Recording saved successfully.", Toast.LENGTH_SHORT).show()
    }

    private fun startPlaying() {
        mPlayer = MediaPlayer()
        try {
            mPlayer?.setDataSource(fileName)
            mPlayer?.prepare()
            mPlayer?.start()
        } catch (e: IOException) {
            Log.e("LOG_TAG", "prepare() failed")
        }
        //making the imageview pause button
        binding?.imageViewPlay?.setImageResource(R.drawable.pause)
        binding?.seekBar?.progress = lastProgress
        mPlayer?.seekTo(lastProgress)
        binding?.seekBar?.max = mPlayer?.duration!!
        seekUpdate()

//        binding?.chronometerTimer?.setBase(SystemClock.elapsedRealtime())
        binding?.chronometerTimer?.start()
        mPlayer?.setOnCompletionListener {
            binding?.imageViewPlay?.setImageResource(R.drawable.play)
            isPlaying = false
//            mPlayer!!.seekTo(mPlayer?.getDuration()!!)
//            binding?.chronometerTimer?.setBase(SystemClock.elapsedRealtime() -mPlayer?.getDuration()!!)
            lastProgress = mPlayer?.duration!!
            binding?.chronometerTimer?.stop()
            val handler = Handler()
            handler.postDelayed({
                binding?.chronometerTimer?.base = SystemClock.elapsedRealtime()
                mPlayer!!.seekTo(0)
            }, 100)

        }
        binding?.seekBar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (mPlayer != null && fromUser) {
                    mPlayer?.seekTo(progress)
                    binding?.chronometerTimer?.base =
                        SystemClock.elapsedRealtime() - mPlayer?.currentPosition!!
                    lastProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private var runnable = Runnable { seekUpdate() }

    private fun seekUpdate() {
        if (mPlayer != null) {
            val mCurrentPosition = mPlayer?.currentPosition
            binding?.seekBar?.progress = mCurrentPosition!!
            binding?.chronometerTimer?.base =
                SystemClock.elapsedRealtime() - mPlayer?.currentPosition!!
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
    }

    private fun prepareForRecording() {
        TransitionManager.beginDelayedTransition(binding?.llRecord)
        binding?.imageViewRecord?.visibility = View.GONE
        binding?.imageViewStop?.visibility = View.VISIBLE
        binding?.linearLayoutPlay?.visibility = View.GONE
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        val root = activity?.externalCacheDir!!
        val file = File(root.absolutePath + "/VoiceRecorderSimplifiedCoding/Audios")

        fileName = root.absolutePath + "/VoiceRecorderSimplifiedCoding/Audios/" + (System.currentTimeMillis().toString() + ".mp4")
        Log.wtf("filename", fileName)
        recordingFile = File(fileName.orEmpty())
       Log.wtf("recording file", recordingFile?.name)
        if (!file.exists()) {
            file.mkdirs()
        }
        recordingFile?.name
        mRecorder?.setOutputFile(fileName)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try {
            mRecorder?.prepare()
            mRecorder?.start()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        lastProgress = 0
        binding?.seekBar?.progress = 0
        stopPlaying()
        // making the image view a stop button
        //starting the chronometer
        binding?.chronometerTimer?.base = SystemClock.elapsedRealtime()
        binding?.chronometerTimer?.start()
    }

    //IMAGE SELECTION AND GET IMAGE PATH
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(activity)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems =
            arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(
                                activity?.contentResolver,
                                contentURI
                            )
                        val path = saveImage(bitmap)
                        bitmapToFile(bitmap)
                        Toast.makeText(activity, "Image Saved!", Toast.LENGTH_SHORT).show()

//                    fra?.imgRootscareProfileImage?.setImageBitmap(bitmap)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show()
                    }

                }

            } else if (requestCode == CAMERA) {

                try {
                    val thumbnail = data!!.extras!!.get("data") as Bitmap
                    //    fragmentProfileBinding?.imgRootscareProfileImage?.setImageBitmap(thumbnail)
                    saveImage(thumbnail)
                    bitmapToFile(thumbnail)
                    Toast.makeText(activity, "Image Saved!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    println("Exception===>$e")
                }

            }
        }

    }

    private fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(activity?.externalCacheDir!!.absolutePath.toString() + IMAGE_DIRECTORY)

        // have the object build the directory structure, if needed.
         Log.wtf("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
             Log.wtf("heel", wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance().timeInMillis).toString() + ".jpg"))
            //     File file = new File("/storage/emulated/0/Download/Corrections 6.jpg");
            f.createNewFile()
            // imageFile=f
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(activity, arrayOf(f.path), arrayOf("image/jpeg"), null)
            fo.close()
             Log.wtf("TAG", "File Saved::--->" + f.absolutePath)
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    // Method to save an bitmap to a file
    private fun bitmapToFile(bitmap: Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(activity)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        imageFile = file

        binding?.tvUpload?.text = imageFile?.name

        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //updateProfileImageApiCall(imageFile!!)
        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }


}







