package com.rootscare.ui.newaddition.providerlisting


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import com.dialog.CommonDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.databinding.LayoutNewProviderBookingForLabBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.newaddition.appointments.ModelAppointmentDetails
import com.rootscare.ui.newaddition.appointments.adapter.AdapterPaymentSplitting
import com.rootscare.ui.newaddition.providerlisting.adapter.*
import com.rootscare.ui.newaddition.providerlisting.models.*
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback
import com.rootscare.utilitycommon.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.collections.ArrayList


class FragmentProvderBookingForLab : BaseFragment<LayoutNewProviderBookingForLabBinding, ProviderListingViewModel>(),
    ProviderListingNavigator {
    private var binding: LayoutNewProviderBookingForLabBinding? = null
    private var mViewModel: ProviderListingViewModel? = null

    private var selectedYear = 0
    private var selectedmonth = 0
    private var selectedday = 0

    var bookingInitialData: ModelBookingInitialLabData? = null
    var isLoadedOnce = false


    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.layout_new_provider_booking_for_lab
    override val viewModel: ProviderListingViewModel
        get() {
            mViewModel =  ViewModelProviders.of(this).get(ProviderListingViewModel::class.java)
            return mViewModel as ProviderListingViewModel
        }
    private var providerId: String = ""
    private var bookingType = ""
    private var providerType = ""

    private var testBooking = ""
    private var packBooking = ""
    private var totalPrice = ""
    private var forApivatPrice = ""
    private var taskPriceTotal = ""
    private var subTotalPrice = ""
    private var hospitalId = ""

    companion object {
         var IS_MEMBER_ADDED = false

        const val ARG_PROVIDER_ID = "ARG_PROVIDER_ID"
        const val ARG_PROVIDER_TYPE = "ARG_PROVIDER_TYPE"
        const val ARG_BOOKING_TYPE = "ARG_BOOKING_TYPE"

        fun newInstance(providerId: String, bookType: String = "", userType: String): FragmentProvderBookingForLab {
            val args = Bundle()
            args.putString(ARG_PROVIDER_ID, providerId)
            args.putString(ARG_BOOKING_TYPE, bookType)
            args.putString(ARG_PROVIDER_TYPE, userType)
             val fragment = FragmentProvderBookingForLab()
            fragment.arguments = args
            return fragment
        }
    }

    private var mPackageBasePaymentList = ArrayList<ModelAppointmentDetails.Result.TaskDetail?>()
    private var mTaskBasePaymentList = ArrayList<ModelAppointmentDetails.Result.TaskDetail?>()

    private val mAdapSelectedTasks: AdapterSelectedTasks by lazy { AdapterSelectedTasks() }
    private val mAdapPackageList: AdapterBookingPackages by lazy { AdapterBookingPackages() }
    private val mAdapTasksList: AdapterPriceListCommon by lazy { AdapterPriceListCommon() }
    private val mAdapTimeSlots: AdapterTimeSlotList by lazy { AdapterTimeSlotList() }
    private val mAdapDateSlots: AdapterDateSlotList by lazy { AdapterDateSlotList() }
    private val mAdapterPayment: AdapterPaymentSplitting by lazy { AdapterPaymentSplitting() }

    private var dateSelected = ""
    private var vatPricePercent = ""
    private var distancefare = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this
        providerId = arguments?.getString(ARG_PROVIDER_ID) ?: ""
        providerType = arguments?.getString(ARG_PROVIDER_TYPE) ?: ""
        bookingType = arguments?.getString(ARG_BOOKING_TYPE) ?: ""

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
                bookingType.equals(BookingTypes.TESTS.get(), ignoreCase = true) -> {
                    tsk_type = BookingTypes.TESTS.getApiType()
                    taskSelectedBookingIds =  mAdapSelectedTasks.updatedArrayList.joinToString { it?.id ?: "" }
                    taskSelectedPrice =  mAdapSelectedTasks.updatedArrayList.joinToString { it?.price ?: "" }
                }

                bookingType.equals(BookingTypes.PACKAGES.get(), ignoreCase = true) -> {
                    tsk_type = BookingTypes.PACKAGES.getApiType()
                    taskSelectedBookingIds = mAdapPackageList.updatedArrayList.find { it?.isChecked == true }?.pid ?: ""
                    taskSelectedPrice = mAdapPackageList.updatedArrayList.find { it?.isChecked == true }?.price ?: ""
                }

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
                    val jsonObject = JsonObject().apply {
                        addProperty("hospital_id", hospitalId)
                        addProperty("service_type", providerType)
                        addProperty("currency_symbol", mViewModel?.appSharedPref?.currencySymbol)
                        addProperty("login_user_id", mViewModel?.appSharedPref?.userId)
                        addProperty("family_member_id",  "")
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
                    Log.wtf("submit_data", "$jsonObject")
                    baseActivity?.hideKeyboard()
                    baseActivity?.showLoading()
                    mViewModel?.apiBookAppointment(body)

                }
            }

        }
    }

    private fun calculatePrice() {
        when {
            bookingType.equals(BookingTypes.TESTS.get(), ignoreCase = true) -> {
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
            bookingType.equals(BookingTypes.PACKAGES.get(), ignoreCase = true) -> {
                val vPricePerc = vatPricePercent.toDoubleOrNull() ?: 0.0
                val disPrice = distancefare.toDoubleOrNull() ?: 0.0
                val hourPrice = mAdapPackageList.updatedArrayList.find { it?.isChecked == true }?.price?.toDoubleOrNull()?: 0.0

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

    private fun initViews() {
        binding?.run {
           attachPreAdapters()
            tvSelectDate.text = if (dateSelected.isBlank()) { dateSelected = getCurrentAppDate(); dateSelected } else dateSelected
            etSearch.addTextChangedListener { filterTasklist(it.toString()) }
            btnSubmit.setOnClickListener {  proceedToPayment() }
            tvhChangeLab.setOnClickListener { changeLabDialog() }
        }
    }

    private fun setupTab(){
       binding?.run {
           val tabTitles: MutableList<String> = when {
               testBooking.equals("0", ignoreCase = true) &&
               packBooking.equals("0", ignoreCase = true) -> {
                   tablayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                   arrayListOf(BookingTypes.TESTS.getDisplayHeading(), BookingTypes.PACKAGES.getDisplayHeading())
               }
               testBooking.equals("0", ignoreCase = true) && (packBooking.isBlank() || packBooking.equals("1", ignoreCase = true)) -> {
                   grpBookingTaskBase.visibility = View.VISIBLE
                   grpBookingHourBase.visibility = View.GONE
                   arrayListOf(BookingTypes.TESTS.getDisplayHeading())
               }
               packBooking.equals("0", ignoreCase = true) &&
               (testBooking.isBlank() || testBooking.equals("1", ignoreCase = true)) -> {
                   grpBookingTaskBase.visibility = View.GONE
                   grpBookingHourBase.visibility = View.VISIBLE
                   arrayListOf(BookingTypes.PACKAGES.getDisplayHeading())
               }
               else -> {
                   tablayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                   arrayListOf(BookingTypes.TESTS.getDisplayHeading(), BookingTypes.PACKAGES.getDisplayHeading())
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
                               bookingType = BookingTypes.TESTS.get()
                               grpBookingTaskBase.visibility = View.VISIBLE
                               grpBookingHourBase.visibility = View.GONE
                               calculatePrice()
                           }
                           else -> {
                               bookingType = BookingTypes.PACKAGES.get()
                               grpBookingTaskBase.visibility = View.GONE
                               grpBookingHourBase.visibility = View.VISIBLE
                               calculatePrice()
                           }
                       }

                       updatedPaymentList(null, bookingType)
                       updateTimeSlots()
                   }
               }
           })
           when {
               bookingType.equals(BookingTypes.PACKAGES.get(), ignoreCase = true) -> {
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
            rvTimeSlots.adapter = mAdapTimeSlots
            rvDateSlots.adapter = mAdapDateSlots
            setDateSlotsListRecycle()
            rvTaskBase.adapter = mAdapTasksList
            rvHourlyBase.adapter = mAdapPackageList
            mAdapTasksList.mCurrency = mViewModel?.appSharedPref?.currencySymbol
            mAdapPackageList.mCurrency = mViewModel?.appSharedPref?.currencySymbol

            mAdapSelectedTasks.mCallback = object : OnSelectedTasksCallback {
                override fun onCancelTask(node: ModelTaskListWithPrice.Result?) {
                    mAdapTasksList.unCheckTaskById(node)
                    updatedPaymentList(node, "")
                }
            }
        }
    }

    private fun setTasksListRecycle(mList: List<ModelBookingInitialData.Result.TaskBaseTask?>?) {
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

    private fun setPackagesListRecycle(mList: ArrayList<ModelBookingInitialLabData.Result.PackageBaseTask?>?) {
        binding?.run {
            if (mList.isNullOrEmpty().not() && mAdapPackageList.updatedArrayList.isEmpty()) {
                mAdapPackageList.loadDataIntoList(mList)
                mAdapPackageList.mCallback = object : OnPacakgePriceCallback {
                    override fun onTaskClick(node: ModelBookingInitialLabData.Result.PackageBaseTask?) {
                        val mNode = ModelAppointmentDetails.Result.TaskDetail(node?.pid.orEmpty(), node?.name.orEmpty(),node?.price ?: "0")

                        mAdapterPayment.updatedNode(mNode)
                        mPackageBasePaymentList.clear()
                        mPackageBasePaymentList.add(mNode)
                        calculatePrice()
                    }
                }
            }
        }
    }

    private fun updatedPaymentList(node: ModelTaskListWithPrice.Result?, updateDataFor: String) {
        when {
            updateDataFor.equals(BookingTypes.TESTS.get(), ignoreCase = true) -> {
                mAdapterPayment.updatedData(mTaskBasePaymentList)
            }
            updateDataFor.equals(BookingTypes.PACKAGES.get(), ignoreCase = true) -> {
                mAdapterPayment.updatedData(mPackageBasePaymentList)
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

    override fun onSuccessInitialData(response: ModelBookingInitialLabData?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            if (response?.result != null) {
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

    private var mTaskBaseSlotList = ArrayList<String?>()

    private fun bindViews(result: ModelBookingInitialLabData.Result?) {
        binding?.run {
            result?.let {
                hospitalId = it.hospital_id.orEmpty()
                imgProfile.setCircularRemoteImageWithNoImage(it.image)
                tvUsername.text = it.provider_name
                tvhIsoCertificate.text = it.iso_text?: "Certified"

                testBooking = it.task_base_enable ?: "1"
                packBooking = it.package_base_enable ?: "1"
                setupTab()
                // payment initial bind views
                tvhDisFare.text = it.distance_fare_text
                tvhVat.text = it.vat_text
                distancefare = if (it.distance_fare != null && it.distance_fare < 1) "0" else it.distance_fare.toString()
                vatPricePercent = if (it.vat_price.isNullOrBlank().not() && it.vat_price.equals("0", ignoreCase = true )) "0" else it.vat_price ?: "0"
                calculatePrice()

                setTasksListRecycle(it.task_base_task?.toDomainModel())

                setPackagesListRecycle(it.package_base_task)

               if(isLoadedOnce.not()) {
                it.task_time?.split(",")?.map { it1 -> it1.trim() }?.let { it2 ->
                    mTaskBaseSlotList.clear(); mTaskBaseSlotList.addAll(it2)
                  }
               }
                updateTimeSlots()
            }
        }
    }

    private fun updateTimeSlots() {
        setTimeSlotsListRecycle(mTaskBaseSlotList)
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

    private fun apiHitForInitialDetails() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val jsonObject = JsonObject().apply {
                addProperty("provider_id", providerId)
                addProperty("service_type", providerType)
                addProperty("lgoin_user_id", mViewModel?.appSharedPref?.userId)
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            mViewModel?.apiBookingInitialDetailsForLab(body)

        } else {
            showToast(getString(R.string.check_network_connection))

        }
    }

    private fun apiFetchTimeSlots(dtSel: String) {
        if (dtSel.equals(dateSelected, ignoreCase = true)) return

        if (isNetworkConnected) {
            dateSelected = dtSel
            val jsonObject = JsonObject().apply {
                addProperty("date", dtSel)
                addProperty("service_type", providerType)
                addProperty("provider_id", providerId)
              }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            baseActivity?.showLoading()
            mViewModel?.apiBookingTimeSlotsForLab(body)

        } else {
            showToast(getString(R.string.check_network_connection))

        }
    }

    override fun onSuccessBookingTimeSlots(response: ModelNetworkTimeSlots?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            if (response?.result != null) {
               mTaskBaseSlotList.clear()
                response.result.task_time?.let { tTime ->
                    if(tTime.isBlank()) { mTaskBaseSlotList.clear() } else {
                        mTaskBaseSlotList.addAll(tTime.split(",").map { it1 -> it1.trim() })
                    }
                }
                updateTimeSlots()
            } else showToast(response?.message?:getString(R.string.something_went_wrong))

        }
    }

    private fun changeLabDialog() {

        if(mAdapSelectedTasks.updatedArrayList.joinToString { it?.id ?: "" }.isBlank()){
            (activity as? HomeActivity)?.openProviderListingForLab(ProviderTypes.LAB.getDisplayName(), ProviderTypes.LAB.getType())
        } else {
            //val data  = "You've added tests from <b>" + bookingInitialData?.result?.provider_name +"</b> in your cart. Do you still wish to change the Lab.?"
            val data  = "You will lost all your tests added in the cart.<br>Do you still wish to <b>Change the Lab.</b>?"
            CommonDialog.showTwoButtonDialog(requireActivity(), object : DialogClickCallback {
                override fun onConfirm() {
                    (activity as? HomeActivity)?.openProviderListingForLab(ProviderTypes.LAB.getDisplayName(), ProviderTypes.LAB.getType())
                }
            }, getString(R.string.change_lab).parseAsHtml(), data.parseAsHtml(), getString(R.string.yes), getString(R.string.cancel))
        }
    }
}







