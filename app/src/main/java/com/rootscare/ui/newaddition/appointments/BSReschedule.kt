package com.rootscare.ui.newaddition.appointments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.text.parseAsHtml
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.dialog.CommonDialog.dialog
import com.rootscare.R
import com.rootscare.databinding.LayoutNewBsRescheduleBinding
import com.rootscare.ui.newaddition.appointments.adapter.AdapterPaymentSplitting
import com.rootscare.ui.newaddition.appointments.models.ModelRescheduleDetail
import com.rootscare.ui.newaddition.providerlisting.adapter.*
import com.rootscare.ui.newaddition.providerlisting.models.ModelDateSlots
import com.rootscare.ui.newaddition.providerlisting.models.ModelTaskListWithPrice
import com.rootscare.ui.supportmore.bottomsheet.DIALOG_BACK_RANGE
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback
import com.rootscare.utilitycommon.*
import java.util.*
import kotlin.collections.ArrayList


class BSReschedule : DialogFragment() {

    private lateinit var mContext: Context
    lateinit var binding: LayoutNewBsRescheduleBinding
    private val mAdapTasksList: AdapterPaymentSplitting by lazy { AdapterPaymentSplitting() }
    private val mAdapTimeSlots: AdapterTimeSlotList by lazy { AdapterTimeSlotList() }
    private val mAdapDateSlots: AdapterDateSlotList by lazy { AdapterDateSlotList() }
    private var selectedYear = 0
    private var selectedmonth = 0
    private var selectedday = 0
    private var dateSelected = ""
  //  private var isLoadedFirst = true

    val mHourlyBaseSlotList = ArrayList<String?>()
    val mTaskBaseSlotList = ArrayList<String?>()

    val mOnlineBaseSlotList = ArrayList<String?>()
    val mHomeVisitBaseSlotList = ArrayList<String?>()

    companion object {
        var mData: ModelRescheduleDetail.Result? = null

        var mCallback: OnBottomSheetCallback? = null
        fun newInstance(data: ModelRescheduleDetail.Result?, clb: OnBottomSheetCallback?): BSReschedule {
            this.mData = data
            this.mCallback = clb
            return BSReschedule()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,  savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.layout_new_bs_reschedule,  container,  false)
        binding.lifecycleOwner = this
        dialog?.window?.let {
            with(it) {
                setBackgroundDrawableResource(android.R.color.transparent)
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
                setDimAmount(DIALOG_BACK_RANGE)
            }
        }

        try {
            binding.run {
            rvTimeSlots.adapter = mAdapTimeSlots
            rvDateSlots.adapter = mAdapDateSlots
            setDateSlotsListRecycle()
            mData?.let {
            dateSelected = getCurrentAppDate() // it.app_date?:""
            tvhSlotType.text =  when(it.slot_booking_id) {

            SlotBookingId.TASK_BOOKING.get()  -> {
              if(mData?.task_time.isNullOrBlank()) { mTaskBaseSlotList.clear() }
              else { mData?.task_time?.split(",")?.map { it1 -> it1.trim()}?.let { it2 -> mTaskBaseSlotList.addAll(it2) } }

           //   mTaskBaseSlotList.add(mData?.app_time?.uppercase())
              setTasksListRecycle(it.task_details)
              BookingTypes.TASK_BASED.getDisplayHeading()
            }

            SlotBookingId.HOURLY_BOOKING.get() -> {
            if(mData?.task_time.isNullOrBlank()) { mHourlyBaseSlotList.clear() }
            else { mData?.task_time?.split(",")?.map { it1 -> it1.trim()}?.let { it2-> mHourlyBaseSlotList.addAll(it2) } }

             setupHourlyTask(it.task_details)
             BookingTypes.HOURLY_BASED.getDisplayHeading()
            }

            // need to do yet
            SlotBookingId.ONLINE_BOOKING.get()  -> {
            if(mData?.task_time.isNullOrBlank()) { mOnlineBaseSlotList.clear() }
            else { mData?.task_time?.split(",")?.map { it1 -> it1.trim()}?.let { it2-> mOnlineBaseSlotList.addAll(it2) } }

             setTasksListRecycle(it.task_details)
             BookingTypes.ONLINE_CONS.getDisplayHeading()
             }

            SlotBookingId.HOMEVISIT_BOOKING.get() -> {
            if(mData?.task_time.isNullOrBlank()) { mHomeVisitBaseSlotList.clear() }
            else { mData?.task_time?.split(",")?.map { it1 -> it1.trim()}?.let { it2-> mHomeVisitBaseSlotList.addAll(it2) } }

             setTasksListRecycle(it.task_details)
             BookingTypes.HOME_VISIT.getDisplayHeading()
            }

            else -> "Unknown"
            }

            // tvSelSlot.text = "${getString(R.string.slots_for)} <font><b>${it.slot_time}</b></font> )".parseAsHtml()
            tvSelectDate.text = it.app_date
            tvOrderId.text = it.order_id
            updateTimeSlots()
        }

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

                    val dpd = DatePickerDialog(requireActivity(), { _, year1, monthOfYear, dayOfMonth ->
                        val monthstr = if ((monthOfYear + 1) < 10) {
                            "0" + (monthOfYear + 1)
                        } else {
                            (monthOfYear + 1).toString()
                        }

                        val dayStr = if (dayOfMonth < 10) {
                            "0$dayOfMonth"
                        } else {
                            dayOfMonth.toString()
                        }

                        selectedYear = year1
                        selectedmonth = monthOfYear
                        selectedday = dayOfMonth
                        binding.tvSelectDate.text = "$year1-$monthstr-$dayStr"

                        if(dateSelected.equals("$year1-$monthstr-$dayStr",ignoreCase = true).not()) {
                       //    dateSelected = "$year1-$monthstr-$dayStr"
                        //   mCallback?.onDateChanged(dateSelected, mData?.provider_type ?: "", mData?.provider_id ?: "")
                        }
                     }, year, month, day)

                    dpd.show()
                    //Get the DatePicker instance from DatePickerDialog
                    val dp = dpd.datePicker
                    if (selectedYear != 0 && selectedmonth != 0 && selectedday != 0) {
                        dp.updateDate(selectedYear, selectedmonth, selectedday)
                    } else {
                        dp.updateDate(year, c.get(Calendar.MONTH), c.get(Calendar.DATE))
                    }
                    dp.minDate = System.currentTimeMillis() - 1000
                }
            ivCross.setOnClickListener { dismiss() }
            btnSubmit.setOnClickListener {
                    val timeSlotSelected = mAdapTimeSlots.updatedArrayList.find { it?.isChecked == true }?.name ?: ""
                    if(timeSlotSelected.isBlank()) {
                        Toast.makeText(requireContext(), getString(R.string.please_select_timeslot), Toast.LENGTH_SHORT).show()
                    } else {
                        mCallback?.onSubmitReschedule(dateSelected, timeSlotSelected,
                        mData?.id?:"", mData?.provider_type?:"")
                        dismiss()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            println(e)
        }

        return binding.root
    }

    private fun setDateSlotsListRecycle() {
        binding.run {
            val arrList = getDateSlots()
            if (arrList.isNullOrEmpty().not()) {
                if (mAdapDateSlots.updatedArrayList.isNullOrEmpty()) {
                    arrList[0]?.isSelected_ = true
                    mAdapDateSlots.loadDataIntoList(arrList)
                    mAdapDateSlots.mCallback = object : OnDateSlotCallback {
                        override fun onDateSlotClicked(node: ModelDateSlots?) {
                            tvSelectDate.text = node?.dateValue
                            if(dateSelected.equals(node?.dateValue, ignoreCase = true).not()) {
                                dateSelected = node?.dateValue ?: ""
                                mCallback?.onDateChanged(dateSelected, mData?.provider_type.orEmpty(),
                                    mData?.provider_id.orEmpty(), mData?.order_id.orEmpty(),
                                    mData?.slot_booking_id.orEmpty(),  mData?.task_id.orEmpty())
                            }
                        }
                    }
                }
                startSmoothScroll(0, rvDateSlots)
            }
        }

    }

    private fun setupHourlyTask(taskDetails: ArrayList<ModelRescheduleDetail.Result.TaskDetail?>?) {
        binding.run {
            if(taskDetails.isNullOrEmpty().not()) {
                crdHourSlot.visibility = View.VISIBLE
               taskDetails?.getOrNull(0)?.let {
                 txtHour.text = it.name?.trim()
                 txtPrice.setAmount(it.price?.trim())
            } }
        }
    }


    fun updateTimeSlots() {
        when(mData?.slot_booking_id) {
            SlotBookingId.TASK_BOOKING.get() -> {
                setTimeSlotsListRecycle(mTaskBaseSlotList)
            }
            SlotBookingId.HOURLY_BOOKING.get() -> {
                setTimeSlotsListRecycle(mHourlyBaseSlotList)
            }
           SlotBookingId.ONLINE_BOOKING.get() -> {
                setTimeSlotsListRecycle(mOnlineBaseSlotList)
            }
           SlotBookingId.HOMEVISIT_BOOKING.get()  -> {
                setTimeSlotsListRecycle(mHomeVisitBaseSlotList)
            }
            else -> Unit
        }
    }
    private fun setTimeSlotsListRecycle(mList: ArrayList<String?>?) {
        binding.run {
            if (mList.isNullOrEmpty().not()) {
                rvTimeSlots.visibility = View.VISIBLE
                tvNoFoundSlotTiming.visibility = View.GONE
                val arrList: ArrayList<ModelTaskListWithPrice.Result?> = ArrayList()
                mList?.forEach {
                    if(isTimeAfterCurrent("$dateSelected $it")) {
                        arrList.add(ModelTaskListWithPrice.Result("", it?.trim()?.uppercase(), "", false))
                    }
//                    if(isLoadedFirst.not()) {
//                        if(isTimeAfterCurrent("$dateSelected $it")) {
//                            arrList.add(ModelTaskListWithPrice.Result("", it?.trim()?.uppercase(), "", false))
//                        }
//                    } else {
//                       if(isTimeAfterCurrent("$dateSelected $it")) {
//                            arrList.add(ModelTaskListWithPrice.Result("", it?.trim()?.uppercase(), "", false))
//                       }
//                    }
                }

                if(arrList.isNullOrEmpty()){
                    rvTimeSlots.visibility = View.GONE
                    tvNoFoundSlotTiming.visibility = View.VISIBLE
                } else {
                    if (mAdapTimeSlots.updatedArrayList.isNullOrEmpty()) {
                        mAdapTimeSlots.loadDataIntoList(arrList)
                    } else {
                        mAdapTimeSlots.updateData(arrList)
                    }
                }


            //   if(isLoadedFirst) {
               //    mAdapTimeSlots.markedSelectedSlot((mData?.app_time?.trim() ?: "").uppercase())
               //    isLoadedFirst = false
             //  }
                startSmoothScroll(0, rvTimeSlots)
            } else {
                rvTimeSlots.visibility = View.GONE
                tvNoFoundSlotTiming.visibility = View.VISIBLE
            }
        }
    }

    private fun setTasksListRecycle(mList: ArrayList<ModelRescheduleDetail.Result.TaskDetail?>?) {
        binding.run {
            rvTasks.adapter = mAdapTasksList
            if(mList.isNullOrEmpty().not() && mAdapTasksList.updatedArrayList.isNullOrEmpty()) {
                rvTasks.visibility = View.VISIBLE
               mList?.forEach {
                   val mNode = ModelAppointmentDetails.Result.TaskDetail(it?.id?:"",it?.name?:"",it?.price?:"0")
                   mAdapTasksList.addNode(mNode)
               }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        try {
            dialog.setCanceledOnTouchOutside(false)
        } catch (e: Exception) {
            println(e)
        }
        return dialog
    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
    }

    override fun onStart() {
        super.onStart()
        try {
            dialog?.window?.let {
                with(it) {
                    val metrics = mContext.resources.displayMetrics
                    val width = metrics.widthPixels
                  //  val heigh = metrics.heightPixels
                    setLayout((width), ViewGroup.LayoutParams.WRAP_CONTENT)
                 //   setLayout(width, heigh)
                    setGravity(Gravity.BOTTOM)
                    setWindowAnimations(R.style.DialogAnimation)
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }

}
