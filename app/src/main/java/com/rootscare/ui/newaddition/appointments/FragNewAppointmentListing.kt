package com.rootscare.ui.newaddition.appointments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dialog.CommonDialog
import com.facebook.all.All
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.videoPushRequest.VideoPushRequest
import com.rootscare.data.model.api.response.videoPushResponse.VideoPushResponse
import com.rootscare.databinding.FragNewAppointmentListingBinding
import com.rootscare.twilio.VideoCallActivity
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.newaddition.appointments.FragNewAppointmentListing.Companion.reDate
import com.rootscare.ui.newaddition.appointments.adapter.AdapterAppointmentListingCommon
import com.rootscare.ui.newaddition.appointments.adapter.OnAppointmentListingCallback
import com.rootscare.ui.newaddition.appointments.models.ModelRescheduleDetail
import com.rootscare.ui.newaddition.providerlisting.DialogBadGateway
import com.rootscare.ui.newaddition.providerlisting.models.ModelNetworkTimeSlots
import com.rootscare.ui.newaddition.providerlisting.models.ModelTImeSlotsForDoctor
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback
import com.rootscare.utilitycommon.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

private const val ARG_APPOINT_TYPE = "ARG_APPOINT_TYPE"
class FragNewAppointmentListing : BaseFragment<FragNewAppointmentListingBinding, ViewModelMyAppointments>(),
    AppointmentNavigator {

    private var binding: FragNewAppointmentListingBinding? = null
    private var mViewModel: ViewModelMyAppointments? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.frag_new_appointment_listing
    override val viewModel: ViewModelMyAppointments
        get() {
            mViewModel = ViewModelProviders.of(this).get(ViewModelMyAppointments::class.java)
            return mViewModel as ViewModelMyAppointments
        }

    var lastPosition: Int? = null

    companion object {
       val showSearch = MutableLiveData(false)
       var isRescheduled: Boolean = false
       var reDate :String? = null
       var reTime :String? = null
       fun newInstance(appointType: String) =
            FragNewAppointmentListing().apply {
                arguments = Bundle().apply {
                    putString(ARG_APPOINT_TYPE, appointType)
                }
            }
    }

    private var searchText = ""
    private var pageCount = 1
    private var eof = true
    private var providerTypeSelected = "all"

    private var appointmentType: String? = null
    private var mAppointAdapter: AdapterAppointmentListingCommon? = null
    var mRecheduleBS : BSReschedule? = null

    private var detailModel: ModelAppointmentsListing.Result? = null
    private var roomName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this
        mAppointAdapter = activity?.let { AdapterAppointmentListingCommon(it) }
        appointmentType = arguments?.getString(ARG_APPOINT_TYPE) ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        tweakTabsLoading()

        initViews()
        observers()
        fetchAppointments(ProviderTypes.ALL.getType())
    }

    private fun observers() {
        showSearch.observe(viewLifecycleOwner)  {
            if(it) {
                binding?.inclSearch?.root?.visibility = View.VISIBLE
            } else {
                binding?.inclSearch?.edtSearch?.setText("")
                binding?.inclSearch?.root?.visibility = View.GONE
            }
        }
    }

    private fun filterTasklist(text: String) {
        val temp: ArrayList<ModelAppointmentsListing.Result?> = ArrayList()
        mAppointAdapter?.updatedArrayList?.forEach { d ->
            d?.order_id?.let { if (it.lowercase().contains(text.lowercase())) { temp.add(d) } }
        }

       binding?.tvNoDate?.visibility = if(temp.isNotEmpty()) View.GONE else View.VISIBLE

        // update recyclerview
        mAppointAdapter?.updateAfterFilterList(temp)
    }

    fun ifAlreadyOpened(appType: String) {
        appointmentType = appType
        initViews()
        fetchAppointments(providerTypeSelected)
    }
    private fun initViews() {
        binding?.inclSearch?.edtSearch?.addTextChangedListener { filterTasklist(it.toString()) }
        binding?.inclSearch?.imgCross?.setOnClickListener { hideKeyboard(); showSearch.value = false }

        binding?.rvAppointments?.adapter = mAppointAdapter
        mAppointAdapter?.mCallback = object : OnAppointmentListingCallback {
            override fun onItemClick(pos: Int, node: ModelAppointmentsListing.Result?) {
                  lastPosition = pos
                  AppointmentDetailScreen.appointmentId = node?.id.orEmpty()
                  AppointmentDetailScreen.serviceType = node?.provider_type.orEmpty()
                  AppointmentDetailScreen.providerId = node?.provider_id.orEmpty()
                  AppointmentDetailScreen.bookType = node?.booking_type.orEmpty()
                  navigate<AppointmentDetailScreen>()
            }

            override fun onReschedule(pos: Int, node: ModelAppointmentsListing.Result?) {
             lastPosition = pos
             apiReschedule(node?.id.orEmpty(), node?.provider_type.orEmpty(), node?.booking_type.orEmpty())
            }

            override fun onLoadMore(pos: Int, lastuserId: String) {
                //   isDataAvailable
//                binding?.tvBottomLoadMore?.visibility = View.VISIBLE
//                Handler(Looper.getMainLooper()).postDelayed({
//                    binding?.tvBottomLoadMore?.visibility = View.GONE},2000)
            }

            override fun onVideoCall(dataModel: ModelAppointmentsListing.Result?) {
                detailModel = dataModel
                hitVideoCall()
            }
        }

    }

    private fun hitVideoCall(){
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val videoPushRequest = VideoPushRequest()
            videoPushRequest.fromUserId = mViewModel?.appSharedPref?.userId
            videoPushRequest.toUserId = detailModel?.provider_id.orEmpty().trim()    // provider id
            roomName = "rootvideo_room_" + videoPushRequest.toUserId + "_" + videoPushRequest.fromUserId
            videoPushRequest.orderId = detailModel?.order_id.orEmpty().trim()
            videoPushRequest.roomName = roomName.orEmpty().trim()
            videoPushRequest.type = "patient_to_doctor_video_call"
            videoPushRequest.fromUserName =  detailModel?.patient_name.orEmpty().trim()
            videoPushRequest.toUserName = detailModel?.provider_name.orEmpty().trim()
            mViewModel?.apiSendVideoPushNotification( videoPushRequest)

        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }

    override fun successVideoPushResponse(videoPushResponse: VideoPushResponse?) {
        baseActivity?.hideLoading()
        if (videoPushResponse?.status == true && videoPushResponse.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            val bundle = Bundle()
            bundle.putString("roomName", roomName)
            bundle.putString("fromUserId", mViewModel?.appSharedPref?.userId.orEmpty())
            bundle.putString("fromUserName", detailModel?.patient_name.orEmpty().trim())
            bundle.putString("toUserId", detailModel?.provider_id.orEmpty())
            bundle.putString("toUserName", detailModel?.provider_name.orEmpty().trim())
            bundle.putString("orderId", detailModel?.order_id.orEmpty().trim())
            bundle.putBoolean("isDoctorCalling", false)

            val intent = Intent(activity, VideoCallActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

//          activity?.finish()
        } else {
            showToast(videoPushResponse?.message?: getString(R.string.something_went_wrong))
        }
    }


    private val rescheduleCallback = object : OnBottomSheetCallback {
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
        override fun onGoBack() { (activity as? HomeActivity)?.onBackPressed() }
        override fun onRetry() { fetchAppointments(providerTypeSelected) }
    }


    private fun apiReschedule(id: String, serviceType: String,bookType:String = "") {
        if (isNetworkConnected) {

            val jsonObject = JsonObject().apply {
                addProperty("service_type", serviceType)
                addProperty("order_id", id)
                addProperty("login_user_id", mViewModel?.appSharedPref?.userId)
            }


            if(serviceType.equals(ProviderTypes.DOCTOR.getType(),ignoreCase = true)) {
                val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                baseActivity?.showLoading()
                mViewModel?.apiRescheduleForDoc(body,-1)
            }
            else if(serviceType.equals(ProviderTypes.LAB.getType(),ignoreCase = true)) {

               //   jsonObject.addProperty("task_type", bookType)
              //    val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                // baseActivity?.showLoading()
            //     mViewModel?.apiRescheduleForLab(body,-1)
            }
            else {
                val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                baseActivity?.showLoading()
                mViewModel?.apiReschedule(body,-1)
            }
        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }

    override fun onUpdateReschedule(response: ModelRescheduleDetail?) {
        baseActivity?.hideLoading()
       if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            response?.result?.let {
                showToast(response.message?:"")
                isRescheduled = true
                updateData(it.display_app_date?:"",it.app_time?:"")
            }?: run {
                CommonDialog.showDialogWithSingleButton(requireActivity(), object : DialogClickCallback {
                },null, response?.message?:getString(R.string.something_went_wrong))
            }
        }
    }

    override fun onRescheduleDetails(response: ModelRescheduleDetail?, position: Int) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            if (response?.result != null) {
              mRecheduleBS = BSReschedule.newInstance(response.result, rescheduleCallback)
              mRecheduleBS?.show(childFragmentManager, "RescheduleDialog")
            }
        } else {
            showToast(response?.message?:"")
        }

    }

    private fun apiUpdateReschedule(fDate: String, fTime: String, orderId: String, serviceType: String) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val jsonObject = JsonObject().apply {
                addProperty("service_type", serviceType)
                addProperty("order_id", orderId)
                addProperty("from_date", fDate)
                addProperty("from_time", fTime)
            }

            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            if(serviceType.equals(ProviderTypes.DOCTOR.getType(),ignoreCase = true)) {
                mViewModel?.apiUpdateRescheduleForDoc(body)
            }
            else if(serviceType.equals(ProviderTypes.LAB.getType(),ignoreCase = true)) {
                mViewModel?.apiUpdateRescheduleForLab(body)
            }
            else mViewModel?.apiUpdateReschedule(body)

        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }

    private fun apiFetchTimeSlots(dtSel: String, proType:String, proId:String, orderId:String, taskTyp:String, tskId:String) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
         val jsonObject = JsonObject().apply {
                addProperty("date", dtSel)
                addProperty("service_type", proType)
                addProperty("provider_id", proId)
            }

            if(proType.equals(ProviderTypes.DOCTOR.getType(),ignoreCase = true)) {
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
              mViewModel?.apiBookingTimeSlotsForDoc(body)
            }
            else if(proType.equals(ProviderTypes.LAB.getType(),ignoreCase = true)) {
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
        baseActivity?.hideLoading()
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
        baseActivity?.hideLoading()
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

    private fun tweakTabsLoading() {
        val tabTitles = appointmentsPrefLs()
        for (i in tabTitles.indices) {
            binding?.tablayout?.newTab()?.setText(tabTitles[i])?.let {
                binding?.tablayout?.addTab(it, i)
            }
        }
        binding?.tablayout?.setTabTextColors(ContextCompat.getColor(requireActivity(), R.color.transparent_white), ContextCompat.getColor(requireActivity(), R.color.background_white))
        binding?.tablayout?.addOnTabSelectedListener(object : AppTabListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                fetchAppointments(when (tab.position) {
                    0 -> ProviderTypes.ALL.getType()
                    1 -> ProviderTypes.NURSE.getType()
                    2 -> ProviderTypes.CAREGIVER.getType()
                    3 -> ProviderTypes.BABYSITTER.getType()
                    4 -> ProviderTypes.PHYSIOTHERAPY.getType()
                    5 -> ProviderTypes.DOCTOR.getType()
                    6 -> ProviderTypes.LAB.getType()
                    else -> ProviderTypes.LAB.getType()

                } )

            }
        })

    }

    override fun onResume() {
        super.onResume()
        reDate?.let { rd ->
            reTime?.let { rt ->
            updateData(reDate ?: "", reTime?:"")
            }
        }
    }

    fun updateData(sdate: String, sTime: String) {
        lastPosition?.let {
            if(isRescheduled) {
                mAppointAdapter?.let {
                    it.updateReschedule(lastPosition, sdate, sTime)
                    noMoreData()
                }
                resetStatic()
            }
        }
    }

   private fun resetStatic(){
        lastPosition = null
        isRescheduled = false
       reDate = null
       reTime = null
    }

  private fun noMoreData() {
         if(mAppointAdapter?.updatedArrayList.isNullOrEmpty()){
           noData(getString(R.string.no_more_data_available))
          }
    }

    private fun fetchAppointments(proType: String) {
        providerTypeSelected = proType
        if (isNetworkConnected) {
          val jsonObject = JsonObject().apply {
              addProperty("service_type", providerTypeSelected)
              addProperty("lgoin_user_id", mViewModel?.appSharedPref?.userId)
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            when (appointmentType) {
               AppointmentTypes.UPCOMING.get() -> { baseActivity?.showLoading();  mViewModel?.apiUpcoming(body) }
               AppointmentTypes.ONGOING.get() -> { baseActivity?.showLoading(); mViewModel?.apiOngoing(body) }
               AppointmentTypes.PAST.get() -> { baseActivity?.showLoading(); mViewModel?.apiPast(body) }
               AppointmentTypes.ALL.get() -> { baseActivity?.showLoading(); mViewModel?.apiALl(body) }
               else -> Unit
            }

        } else {
            noData(getString(R.string.check_network_connection))
        }
    }

    override fun onSuccessResponse(response: ModelAppointmentsListing?) {
        try {
            baseActivity?.hideLoading()
            if (response?.code.equals(SUCCESS_CODE)) {
                response?.result?.let {
                    if (it.isNullOrEmpty().not()) {
                        binding?.run {
                            tvNoDate.visibility = View.GONE
                            rvAppointments.visibility = View.VISIBLE
                        }

                        mAppointAdapter?.updatedArrayList?.clear()
                       mAppointAdapter?.loadDataIntoList(it)
                    } else noData(response.message)
                } ?: run { noData(response?.message) }
            } else noData(response?.message)
        } catch (e: Exception) {
            baseActivity?.hideLoading()
            println(e)
        }
    }

    private fun noData(message: String?) {
        binding?.run {
            tvNoDate.visibility = View.VISIBLE
            rvAppointments.visibility = View.GONE
            tvNoDate.text = message ?: getString(R.string.something_went_wrong)
        }
    }

    override fun errorInApi(throwable: Throwable?) {
        baseActivity?.hideLoading()
     //   noData(throwable?.message)
        val badgateWay = DialogBadGateway.newInstance(rescheduleCallback,"show")
        badgateWay.show(childFragmentManager, "BadgatewayDialog")
    }

    override fun errorInRecheduleApi(throwable: Throwable?) {
        baseActivity?.hideLoading()
        showToast(throwable?.message?: getString(R.string.something_went_wrong))
    }

}