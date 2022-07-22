package com.rootscare.ui.newaddition.providerlisting


import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
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
import com.rootscare.databinding.LayoutNewProviderBookingBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.newaddition.appointments.ModelAppointmentDetails
import com.rootscare.ui.newaddition.appointments.adapter.AdapterPaymentSplitting
import com.rootscare.ui.newaddition.providerlisting.adapter.*
import com.rootscare.ui.newaddition.providerlisting.models.*
import com.rootscare.ui.newaddition.providerlisting.patientaddition.FragmentAddPatient
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback
import com.rootscare.utilitycommon.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*
import kotlin.collections.ArrayList


class FragmentProvderBooking : BaseFragment<LayoutNewProviderBookingBinding, ProviderListingViewModel>(),
    ProviderListingNavigator {
    private var binding: LayoutNewProviderBookingBinding? = null
    private var mViewModel: ProviderListingViewModel? = null

    private var selectedYear = 0
    private var selectedmonth = 0
    private var selectedday = 0

    var bookingInitialData: ModelBookingInitialData? = null
    var isLoadedOnce = false


    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.layout_new_provider_booking_
    override val viewModel: ProviderListingViewModel
        get() {
            mViewModel =  ViewModelProviders.of(this).get(ProviderListingViewModel::class.java)
            return mViewModel as ProviderListingViewModel
        }
    private var providerId: String = ""
    private var bookingType = ""
    private var providerType = ""

    private var taskBooking = ""
    private var hourlyBooking = ""
    private var totalPrice = ""
    private var forApivatPrice = ""
    private var taskPriceTotal = ""
    private var subTotalPrice = ""

    companion object {
         var IS_MEMBER_ADDED = false

        const val ARG_PROVIDER_ID = "ARG_PROVIDER_ID"
        const val ARG_PROVIDER_TYPE = "ARG_PROVIDER_TYPE"
        const val ARG_BOOKING_TYPE = "ARG_BOOKING_TYPE"
        const val ARG_BOOKING_TASKBASE = "ARG_BOOKING_TASKBASE"
        const val ARG_BOOKING_HOURBASE = "ARG_BOOKING_HOURBASE"

        fun newInstance(providerId: String,  bookType: String = "", userType: String, tkBkg: String, hbBkg: String): FragmentProvderBooking {
            val args = Bundle()
            args.putString(ARG_PROVIDER_ID, providerId)
            args.putString(ARG_BOOKING_TYPE, bookType)
            args.putString(ARG_PROVIDER_TYPE, userType)
            args.putString(ARG_BOOKING_TASKBASE, tkBkg)
            args.putString(ARG_BOOKING_HOURBASE, hbBkg)
            val fragment = FragmentProvderBooking()
            fragment.arguments = args
            return fragment
        }
    }

    private var mHourlyBasePaymentList = ArrayList<ModelAppointmentDetails.Result.TaskDetail?>()
    private var mTaskBasePaymentList = ArrayList<ModelAppointmentDetails.Result.TaskDetail?>()

    private val mAdapSelectedTasks: AdapterSelectedTasks by lazy { AdapterSelectedTasks() }
    private val mAdapHourlyList: AdapterHourlyPriceList by lazy { AdapterHourlyPriceList() }
    private val mAdapTasksList: AdapterPriceListCommon by lazy { AdapterPriceListCommon() }
    private val mAdapTimeSlots: AdapterTimeSlotList by lazy { AdapterTimeSlotList() }
    private val mAdapDateSlots: AdapterDateSlotList by lazy { AdapterDateSlotList() }
    private val mAdapterPayment: AdapterPaymentSplitting by lazy { AdapterPaymentSplitting() }
    private val mAdapterFamilyMembers: AdapterFamilyMembers by lazy { AdapterFamilyMembers() }

    private var dateSelected = ""
    private var hourTaskIdSelected = ""
    private var vatPricePercent = ""
    private var distancefare = ""

    private var isCalledFirstTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this
        providerId = arguments?.getString(ARG_PROVIDER_ID) ?: ""
        bookingType = arguments?.getString(ARG_BOOKING_TYPE) ?: ""
        providerType = arguments?.getString(ARG_PROVIDER_TYPE) ?: ""
        taskBooking = arguments?.getString(ARG_BOOKING_TASKBASE) ?: "1"
        hourlyBooking = arguments?.getString(ARG_BOOKING_HOURBASE) ?: "1"

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        initViews()
        callingApi()
    }

    private fun proceedToPayment() {
        if (isNetworkConnected) {
            var tsk_type = ""
            var taskSelectedBookingIds = ""
            var taskSelectedPrice = ""
            val timeSlotSelected = mAdapTimeSlots.updatedArrayList.find { it?.isChecked == true }?.name ?: ""
            when {
                bookingType.equals(BookingTypes.HOURLY_BASED.get(), ignoreCase = true) -> {
                    tsk_type = BookingTypes.HOURLY_BASED.getApiType()
                    taskSelectedBookingIds = mAdapHourlyList.updatedArrayList.find { it?.isChecked == true }?.id ?: ""
                    taskSelectedPrice = mAdapHourlyList.updatedArrayList.find { it?.isChecked == true }?.price ?: ""
                }
                bookingType.equals(BookingTypes.TASK_BASED.get(), ignoreCase = true) -> {
                    tsk_type = BookingTypes.TASK_BASED.getApiType()
                    taskSelectedBookingIds =  mAdapSelectedTasks.updatedArrayList.joinToString { it?.id ?: "" }
                    taskSelectedPrice =  mAdapSelectedTasks.updatedArrayList.joinToString { it?.price ?: "" }
                }
            }

  //     Log.wtf("proceed","$providerId,${mViewModel?.appSharedPref?.userId}=$dateSelected $timeSlotSelected-:\n$distancefare - $forApivatPrice - $totalPrice -\n$tsk_type- $taskSelectedBookingIds - $taskSelectedPrice")

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
                    val jsonObject = JsonObject().apply {
                        addProperty("service_type", providerType)
                        addProperty("currency_symbol", mViewModel?.appSharedPref?.currencySymbol)
                        addProperty("login_user_id", mViewModel?.appSharedPref?.userId)
                        addProperty("family_member_id", mViewModel?.mlFamilyMemberId?.value ?: "")
                        addProperty("provider_id", providerId)
                        addProperty("task_id", taskSelectedBookingIds)
                        addProperty("task_type", tsk_type)
                        addProperty("appointment_type", "offline")
                        addProperty("from_date", dateSelected)
                        addProperty("from_time", timeSlotSelected)
                        addProperty("task_price", taskSelectedPrice)
                        addProperty("task_price_total", taskPriceTotal)
                        addProperty("vat_price", forApivatPrice)
                        addProperty("vat_percent_used", vatPricePercent)
                        addProperty("distance_fare", distancefare)
                        addProperty("sub_total_price", subTotalPrice)
                        addProperty("total_price", totalPrice)
                    }
                    val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                    Log.wtf("submit_data", "$body")
                    baseActivity?.hideKeyboard()
                    baseActivity?.showLoading()
                    mViewModel?.apiBookAppointment(body)

                }
            }

        }
    }

    private fun calculatePrice() {
        when {
            bookingType.equals(BookingTypes.TASK_BASED.get(), ignoreCase = true) -> {
                val vPricePerc = vatPricePercent.toDoubleOrNull() ?: 0.0
                val disPrice = distancefare.toDoubleOrNull() ?: 0.0
                var tskPrice = 0.0
                mTaskBasePaymentList.forEach { tskPrice += it?.price?.toDoubleOrNull() ?: 0.0 }

                val subTotal = tskPrice.plus(disPrice)
                val vPriceValue = (subTotal * vPricePerc) / 100

                val gTotal = subTotal.plus(vPriceValue)

                binding?.run {

                         tvVat.setAmountWithCurrency(vPriceValue, mViewModel?.appSharedPref?.currencySymbol)
                     tvDisFare.setAmountWithCurrency(disPrice, mViewModel?.appSharedPref?.currencySymbol)
               tvSubTotalPrice.setAmountWithCurrency(subTotal.toString(), mViewModel?.appSharedPref?.currencySymbol)
                  tvTotalPrice.setAmountWithCurrency(gTotal.toString(), mViewModel?.appSharedPref?.currencySymbol)
                }

                totalPrice = gTotal.toString()
                forApivatPrice = vPriceValue.toString()

                taskPriceTotal = tskPrice.toString()
                subTotalPrice = subTotal.toString() // vat plus task list price
            }
            bookingType.equals(BookingTypes.HOURLY_BASED.get(), ignoreCase = true) -> {
                val vPricePerc = vatPricePercent.toDoubleOrNull() ?: 0.0
                val disPrice = distancefare.toDoubleOrNull() ?: 0.0
                val hourPrice = mAdapHourlyList.updatedArrayList.find { it?.isChecked == true }?.price?.toDoubleOrNull()?: 0.0

                val subTotal = hourPrice.plus(disPrice)
                val vPriceValue = (subTotal * vPricePerc) / 100
                val gTotal = subTotal.plus(vPriceValue)

                // show to the views
                binding?.run {
                           tvVat.setAmountWithCurrency(vPriceValue, mViewModel?.appSharedPref?.currencySymbol)
                       tvDisFare.setAmountWithCurrency(disPrice, mViewModel?.appSharedPref?.currencySymbol)
                 tvSubTotalPrice.setAmountWithCurrency(subTotal.toString(), mViewModel?.appSharedPref?.currencySymbol)
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
        if(IS_MEMBER_ADDED){
            IS_MEMBER_ADDED = false
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
            tvSelectDate.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                if (selectedYear != 0 && selectedmonth != 0 && selectedday != 0) {
                    c.set(selectedYear, selectedmonth, selectedday)
                } else {
                    c.set(year, c.get(Calendar.MONTH), c.get(Calendar.DATE))
                }
                val dpd =
                    DatePickerDialog(requireActivity(), { _, year1, monthOfYear, dayOfMonth ->
                        val monthstr = if ((monthOfYear + 1) < 10) {
                            "0" + (monthOfYear + 1)
                        } else (monthOfYear + 1).toString()
                        val dayStr = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()

                        selectedYear = year1
                        selectedmonth = monthOfYear
                        selectedday = dayOfMonth
                    //    binding?.tvSelectDate?.text = "$year1-$monthstr-$dayStr"
                     //   apiFetchTimeSlots("$year1-$monthstr-$dayStr")

                    }, year, month, day)
                dpd.show()
                val dp = dpd.datePicker
                if (selectedYear != 0 && selectedmonth != 0 && selectedday != 0) {
                    dp.updateDate(selectedYear, selectedmonth, selectedday)
                } else {
                    dp.updateDate(year, c.get(Calendar.MONTH), c.get(Calendar.DATE))
                }
                dp.minDate = System.currentTimeMillis() - 1000
            }
            etSearch.addTextChangedListener { filterTasklist(it.toString()) }
            btnSubmit.setOnClickListener { proceedToPayment() }
        }
    }

    private fun setupTab(){
       binding?.run {
           val tabTitles: MutableList<String> = when {
               taskBooking.equals("0", ignoreCase = true) &&
               hourlyBooking.equals("0", ignoreCase = true) -> {
                   tablayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                   arrayListOf(BookingTypes.TASK_BASED.getDisplayHeading(), BookingTypes.HOURLY_BASED.getDisplayHeading())
               }
               taskBooking.equals("0", ignoreCase = true) &&
               (hourlyBooking.isBlank() || hourlyBooking.equals("1", ignoreCase = true)) -> {
                   tvSelSlot.text = getString(R.string.all_slots_for_30_mins)
                   grpBookingTaskBase.visibility = View.VISIBLE
                   grpBookingHourBase.visibility = View.GONE
                   arrayListOf(BookingTypes.TASK_BASED.getDisplayHeading())
               }
               hourlyBooking.equals("0", ignoreCase = true) &&
               (taskBooking.isBlank() || taskBooking.equals("1", ignoreCase = true)) -> {
                   tvSelSlot.text = getString(R.string.all_slots_are_for_2_hours)
                   grpBookingTaskBase.visibility = View.GONE
                   grpBookingHourBase.visibility = View.VISIBLE
                   arrayListOf(BookingTypes.HOURLY_BASED.getDisplayHeading())
               }

/*
                providerType.equals(ProviderTypes.CAREGIVER.getType(), ignoreCase = true) ||
                providerType.equals(ProviderTypes.BABYSITTER.getType(), ignoreCase = true) -> {
                    tvSelSlot.text = getString(R.string.all_slots_are_for_2_hours)
                    grpBookingTaskBase.visibility = View.GONE
                    grpBookingHourBase.visibility = View.VISIBLE
                    arrayListOf(BookingTypes.HOURLY_BASED.get())
                }
                providerType.equals(ProviderTypes.PHYSIOTHERAPY.getType(), ignoreCase = true) -> {
                    tvSelSlot.text = getString(R.string.all_slots_for_30_mins)
                    grpBookingTaskBase.visibility = View.VISIBLE
                    grpBookingHourBase.visibility = View.GONE
                    arrayListOf(BookingTypes.TASK_BASED.get())
                }*/
               else -> {
                   tablayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                   arrayListOf(BookingTypes.TASK_BASED.getDisplayHeading(), BookingTypes.HOURLY_BASED.getDisplayHeading())
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
                               bookingType = BookingTypes.TASK_BASED.get()
                               tvSelSlot.text = getString(R.string.all_slots_for_30_mins)
                               grpBookingTaskBase.visibility = View.VISIBLE
                               grpBookingHourBase.visibility = View.GONE
                               calculatePrice()
                           }
                           else -> {
                               bookingType = BookingTypes.HOURLY_BASED.get()
                               tvSelSlot.text = getString(R.string.all_slots_are_for_2_hours)
                               grpBookingTaskBase.visibility = View.GONE
                               grpBookingHourBase.visibility = View.VISIBLE
                               calculatePrice()
                           }
                       }

                       updatedPaymentList(null, bookingType)
                       updateTimeSlots(bookingType)
                   }
               }
           })
           when {
               bookingType.equals(BookingTypes.HOURLY_BASED.get(), ignoreCase = true) -> {
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
            rvSelectedTasks.adapter = mAdapSelectedTasks
            rvPatientList.adapter = mAdapterFamilyMembers
            rvTimeSlots.adapter = mAdapTimeSlots
            rvDateSlots.adapter = mAdapDateSlots
            setDateSlotsListRecycle()
            rvTaskBase.adapter = mAdapTasksList
            rvHourlyBase.adapter = mAdapHourlyList
            mAdapTasksList.mCurrency = mViewModel?.appSharedPref?.currencySymbol
            mAdapHourlyList.mCurrency = mViewModel?.appSharedPref?.currencySymbol

            mAdapSelectedTasks.mCallback = object : OnSelectedTasksCallback {
                override fun onCancelTask(node: ModelTaskListWithPrice.Result?) {
                    mAdapTasksList.unCheckTaskById(node)
                    updatedPaymentList(node, "")
                }
            }
        }
    }

    private fun setTasksListRecycle(mList: ArrayList<ModelBookingInitialData.Result.TaskBaseTask?>?) {
        binding?.run {
            if (mList.isNullOrEmpty().not() && mAdapTasksList.updatedArrayList.isNullOrEmpty()) {
                val arrList: ArrayList<ModelTaskListWithPrice.Result?> = ArrayList()
                mList?.forEach {
                    val node = ModelTaskListWithPrice.Result(it?.id, it?.name, it?.price, false)
                    arrList.add(node)
                }

                mAdapTasksList.loadDataIntoList(arrList)
                mAdapTasksList.mCallback = object : OnTaskPriceCallback {
                    override fun onTaskClick(node: ModelTaskListWithPrice.Result?) {
                        // for selected tasks
                        if (mAdapSelectedTasks.updatedArrayList.contains(node)) {
                            mAdapSelectedTasks.removeNode(node)
                        } else {
                            mAdapSelectedTasks.loadDataIntoList(node)
                        }
                        // payment adapter setting
                        updatedPaymentList(node, "")
                    }
                }
            }
        }
    }

    private fun setHourlyListRecycle(mList: ArrayList<ModelBookingInitialData.Result.HourBaseTask?>?) {
        binding?.run {
            if (mList.isNullOrEmpty().not() && mAdapHourlyList.updatedArrayList.isNullOrEmpty()) {
                val arrList: ArrayList<ModelTaskListWithPrice.Result?> = ArrayList()
                mList?.forEach {
                    val node = ModelTaskListWithPrice.Result(it?.id, it?.duration, it?.price, false)
                    arrList.add(node)
                }
                mAdapHourlyList.loadDataIntoList(arrList)
                mAdapHourlyList.mCallback = object : OnHourlyPriceCallback {
                    override fun onTaskClick(node: ModelTaskListWithPrice.Result?) {
                        val mNode = ModelAppointmentDetails.Result.TaskDetail(
                            node?.id.orEmpty(), node?.name.orEmpty(),node?.price ?: "0")

                        mAdapterPayment.updatedNode(mNode)
                        mHourlyBasePaymentList.clear()
                        mHourlyBasePaymentList.add(mNode)
                        calculatePrice()
                        apiFetchTimeSlotsWhenTaskSelected(dateSelected, node?.id.orEmpty())
                    }
                }
            }
        }
    }

    private fun updatedPaymentList(node: ModelTaskListWithPrice.Result?, updateDataFor: String) {
        when {
            updateDataFor.equals(BookingTypes.TASK_BASED.get(), ignoreCase = true) -> {
                mAdapterPayment.updatedData(mTaskBasePaymentList)
            }
            updateDataFor.equals(BookingTypes.HOURLY_BASED.get(), ignoreCase = true) -> {
                mAdapterPayment.updatedData(mHourlyBasePaymentList)
            }
            else -> {
                node?.let {
                    val mNode = ModelAppointmentDetails.Result.TaskDetail(it.id ?: "",it.name ?: "",it.price ?: "0")
                    val isHave = mAdapterPayment.updatedArrayList.find { inLst -> inLst?.id.equals(it.id) }
                    if (isHave != null) {
                        mAdapterPayment.removeById(isHave.id ?: "")
                        mTaskBasePaymentList.remove(mNode)
                    } else {
                        mAdapterPayment.addNode(mNode)
                        mTaskBasePaymentList.add(mNode)
                    }
                }
            }
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
                startSmoothScroll(0,binding?.rvPatientList)
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

    private fun filterTasklist(text: String) {
        val temp: ArrayList<ModelTaskListWithPrice.Result?> = ArrayList()
        for (d in mAdapTasksList.updatedArrayList) {
            d?.name?.let {
                if (it.lowercase().contains(text.lowercase())) {
                    temp.add(d)
                }
            }
        }

        // update recyclerview
        mAdapTasksList.updateList(temp)
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

    override fun onSuccessInitialData(response: ModelBookingInitialData?) {
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

    private var mHourlyBaseSlotList = ArrayList<String?>()
    private var mTaskBaseSlotList = ArrayList<String?>()

    private fun bindViews(result: ModelBookingInitialData.Result?) {
        binding?.run {
            result?.let {
                imgProfile.setCircularRemoteImageWithNoImage(it.image)
                tvUsername.text = it.provider_name
                tvhTypeExperience.text = "${it.dispaly_provider_type} - ${it.experience}"
                tvhCertificateDegree.text = it.qualification
                taskBooking = it.task_base_enable ?: "1"
                hourlyBooking = it.hour_base_enable ?: "1"
                setupTab()
                // payment initial bind views
                tvhDisFare.text = it.distance_fare_text
                tvhVat.text = it.vat_text
                distancefare = if (it.distance_fare != null && it.distance_fare < 1) "0" else it.distance_fare.toString()
                vatPricePercent = if (it.vat_price.isNullOrBlank().not() && it.vat_price.equals("0", ignoreCase = true )) "0" else it.vat_price ?: "0"
                calculatePrice()
                setTasksListRecycle(it.task_base_task)
                setHourlyListRecycle(it.hour_base_task)
               if(isLoadedOnce.not()) {
                it.hourly_time?.split(",")?.map { it1 -> it1.trim() }?.let { it2 ->
                    mHourlyBaseSlotList.clear(); mHourlyBaseSlotList.addAll(it2)
                }
                it.task_time?.split(",")?.map { it1 -> it1.trim() }?.let { it2 ->
                    mTaskBaseSlotList.clear(); mTaskBaseSlotList.addAll(it2)
                }
               }
                updateTimeSlots(bookingType)
            }
        }
    }

    private fun updateTimeSlots(updateDataFor: String) {
        when {
            updateDataFor.equals(BookingTypes.TASK_BASED.get(), ignoreCase = true) -> {
                setTimeSlotsListRecycle(mTaskBaseSlotList)
            }
            updateDataFor.equals(BookingTypes.HOURLY_BASED.get(), ignoreCase = true) -> {
                setTimeSlotsListRecycle(mHourlyBaseSlotList)
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
            baseActivity?.showLoading()
            val jsonObject = JsonObject().apply {
                addProperty("provider_id", providerId)
                addProperty("service_type", providerType)
                addProperty("lgoin_user_id", mViewModel?.appSharedPref?.userId)
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            mViewModel?.apiBookingInitialDetails(body)

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
                addProperty("appid", "")
                addProperty("hour_task_id", hourTaskIdSelected)
                addProperty("task_type",  when {
                    bookingType.equals(BookingTypes.TASK_BASED.get(), ignoreCase = true) -> {
                        BookingTypes.TASK_BASED.getSlotType() }  else -> BookingTypes.HOURLY_BASED.getSlotType()
                })
              }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            mViewModel?.apiBookingTimeSlots(body)

        } else {
            showToast(getString(R.string.check_network_connection))

        }
    }

    private fun apiFetchTimeSlotsWhenTaskSelected(dtSel: String, taskId: String){
        if (isNetworkConnected) {
            if (taskId.equals(hourTaskIdSelected, ignoreCase = true)) return
                hourTaskIdSelected = taskId

            baseActivity?.showLoading()
            val jsonObject = JsonObject().apply {
                addProperty("appid", "")
                addProperty("date", dtSel)
                addProperty("service_type", providerType)
                addProperty("provider_id", providerId)
                addProperty("hour_task_id", hourTaskIdSelected)
                addProperty("task_type", BookingTypes.HOURLY_BASED.getSlotType())
              }

            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            mViewModel?.apiBookingTimeSlots(body)
        } else {
            showToast(getString(R.string.check_network_connection))

        }
    }

    override fun onSuccessBookingTimeSlots(response: ModelNetworkTimeSlots?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            if (response?.result != null) {
                mHourlyBaseSlotList.clear(); mTaskBaseSlotList.clear()

                response.result.task_time?.let { tTime ->
                    if(tTime.isBlank()) { mTaskBaseSlotList.clear() } else {
                        mTaskBaseSlotList.addAll(tTime.split(",").map { it1 -> it1.trim() })
                    }
                }
                response.result.hourly_time?.let { hTime ->
                    if(hTime.isBlank()) { mHourlyBaseSlotList.clear() } else {
                        mHourlyBaseSlotList.addAll(hTime.split(",").map { it1 -> it1.trim()})
                    }
                }

                updateTimeSlots(bookingType)
            } else showToast(response?.message?:getString(R.string.something_went_wrong))

        }
    }

}







