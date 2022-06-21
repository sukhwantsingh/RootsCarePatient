package com.rootscare.ui.recedule.doctor

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.dialog.CommonDialog
import com.google.gson.JsonObject
import com.interfaces.DialogClickCallback
import com.interfaces.OnDoctorPrivateSlotClickListner
import com.interfaces.OnDoctorSlotClickListner
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.doctorrequest.doctorprivatesotrequest.DoctorPrivateSlotRequest
import com.rootscare.data.model.api.request.pushNotificationRequest.PushNotificationRequest
import com.rootscare.data.model.api.request.reschedule.DoctorAppointmentRescheduleRequest
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.DoctorPrivateSlotResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.ResultItem
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.SlotItem
import com.rootscare.data.model.api.response.reschedule.doctorreschedule.DoctorRescheduleResponse
import com.rootscare.databinding.FragmentDoctorAppointmentRescheduleBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.bookingappointment.adapter.AdapterDoctorSlotDivision
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.ui.myappointment.FragmentMyAppointment
import com.rootscare.ui.myupcomingappointment.FragmentMyUpCommingAppointment
import com.rootscare.ui.recedule.doctor.adapter.AdapterDoctorPrivateSlot
import com.rootscare.ui.todaysappointment.FragmentTodaysAppointment
import com.rootscare.utils.AppConstants
import java.text.SimpleDateFormat
import java.util.*


class FragmentDoctorAppointmentReschedule :
    BaseFragment<FragmentDoctorAppointmentRescheduleBinding, FragmentDoctorAppointmentRescheduleViewModel>(),
    FragmentDoctorAppointmentRescheduleNavigator {
    private var fragmentDoctorAppointmentRescheduleBinding: FragmentDoctorAppointmentRescheduleBinding? =
        null
    private var fragmentDoctorAppointmentRescheduleViewModel: FragmentDoctorAppointmentRescheduleViewModel? =
        null
    private var monthstr: String = ""
    private var dayStr: String = ""
    private var doctorId: String = ""
    private var patientName: String = ""
    private var doctorName: String = ""
    private var appointmentDate: String = ""
    private var fromTime: String = ""
    private var toTime: String = ""
    private var clinicname: String = ""
    private var serviceType: String = ""
    private var hospitalId: String = ""
    private var appointmentId: String = ""
    private var selectedYear = 0
    private var selectedmonth = 0
    private var selectedday = 0
    private var rescheduleMessage: String = ""


    var flag = 0
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_doctor_appointment_reschedule
    override val viewModel: FragmentDoctorAppointmentRescheduleViewModel
        get() {
            fragmentDoctorAppointmentRescheduleViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentDoctorAppointmentRescheduleViewModel::class.java)
            return fragmentDoctorAppointmentRescheduleViewModel as FragmentDoctorAppointmentRescheduleViewModel
        }

    companion object {
        fun newInstance(
            id: String,
            doctorid: String,
            patientname: String,
            doctorName: String,
            appointmentdate: String,
            fromdate: String,
            todate: String,
            clinicName: String,
            hospitalId: String,
            serviceType: String
        ): FragmentDoctorAppointmentReschedule {
            val args = Bundle()
            args.putString("id", id)
            args.putString("doctorid", doctorid)
            args.putString("patientname", patientname)
            args.putString("doctorName", doctorName)
            args.putString("appointmentdate", appointmentdate)
            args.putString("fromdate", fromdate)
            args.putString("todate", todate)
            args.putString("clinicName", clinicName)
            args.putString("hospitalId", hospitalId)
            args.putString("serviceType", serviceType)
            val fragment = FragmentDoctorAppointmentReschedule()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentDoctorAppointmentRescheduleViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentDoctorAppointmentRescheduleBinding = viewDataBinding

        if (arguments != null && arguments?.getString("id") != null) {
            appointmentId = arguments?.getString("id")!!
            Log.d("Doctor Id", ": $appointmentId")
        }
        if (arguments != null && arguments?.getString("doctorid") != null) {
            doctorId = arguments?.getString("doctorid")!!
            Log.d("Doctor Id", ": $doctorId")
        }

        if (arguments != null && arguments?.getString("patientname") != null) {
            patientName = arguments?.getString("patientname")!!
            Log.d("patientname", ": $patientName")
        }

        if (arguments != null && arguments?.getString("doctorName") != null) {
            doctorName = arguments?.getString("doctorName")!!
            Log.d("doctorName", ": $doctorName")
        }

        if (arguments != null && arguments?.getString("appointmentdate") != null) {
            appointmentDate = arguments?.getString("appointmentdate")!!
            Log.d("appointmentdate", ": $appointmentDate")
        }

        if (arguments != null && arguments?.getString("fromdate") != null) {
            fromTime = arguments?.getString("fromdate")!!
            Log.d("fromdate", ": $fromTime")
        }

        if (arguments != null && arguments?.getString("todate") != null) {
            toTime = arguments?.getString("todate")!!
            Log.d("todate", ": $toTime")
        }

        if (arguments != null && arguments?.getString("clinicName") != null) {
            clinicname = arguments?.getString("clinicName")!!
            Log.d("clinicName", ": $clinicname")
        }
        if (arguments != null && arguments?.getString("serviceType") != null) {
            serviceType = arguments?.getString("serviceType")!!
            Log.d("serviceType", ": $serviceType")
        }
        if (arguments != null && arguments?.getString("hospitalId") != null) {
            hospitalId = arguments?.getString("hospitalId")!!
            Log.d("hospitalId", ": $hospitalId")

            fragmentDoctorAppointmentRescheduleBinding?.etName?.text = "Hospital Name"
        }
        // Set todays date and get clinic list and doctor according to todays date
//        var c = Calendar.getInstance().getTime();
//        System.out.println("Current time => " + c);
//
//        var df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        var formattedDate = df.format(c);
        selectDoctorSlotApiCall(appointmentDate)

//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//        val firstDate = sdf.parse(appointmentDate)
//       selectedYear= firstDate.year
//        selectedmonth=firstDate.month
//        selectedday=firstDate.day

//        try {
//            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//            val d = sdf.parse(appointmentDate)
//            val cal = Calendar.getInstance()
//            cal.time = d
//            selectedmonth = checkDigit(cal[Calendar.MONTH] + 1)?.toInt()!!
//            selectedday = checkDigit(cal[Calendar.DATE])?.toInt()!!
//            selectedYear = checkDigit(cal[Calendar.YEAR])?.toInt()!!
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        // val dob = "01/08/1990"

//        val currentString = appointmentDate
//        val separated =
//            currentString.split("-".toRegex()).toTypedArray()
//        var yearString=separated[0]
//        var monthString=separated[1]
//        var dayString=separated[2]
//
//        System.out.println("First String => " + separated[0])
//        System.out.println("Second String => " + separated[1])
//        System.out.println("Third String => " + separated[2])
//         // this will contain "Fruit"
//
//        selectedmonth = yearString.toInt()
//            selectedday = monthString.toInt()
//            selectedYear = dayString.toInt()

//        var month = 0
//        var dd = 0
//        var yer = 0
//
//        try {
//            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//            val d = sdf.parse(appointmentDate)
//            val cal = Calendar.getInstance()
//            cal.time = d
//            selectedmonth = cal[Calendar.MONTH]+1
//            selectedday = cal[Calendar.DAY_OF_MONTH]
//            selectedYear = cal[Calendar.YEAR]
//            println("Month - " + String.format("%02d", selectedmonth + 1))
//            println("Day - " + String.format("%02d", selectedday))
//            println("Year - $selectedYear")
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        var dt = appointmentDate // Start date

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val c = Calendar.getInstance()
        c.time = sdf.parse(dt)
        c.add(Calendar.DATE, 0) // number of days to add

        dt = sdf.format(c.time) // dt is now the new date
        selectedmonth = c[Calendar.MONTH]
        println("SELECTED Month - " + String.format("%02d", selectedmonth + 1))
        selectedday = c[Calendar.DAY_OF_MONTH]
        println("SELECTED Day - " + String.format("%02d", selectedday))
        selectedYear = c[Calendar.YEAR]
        println("SELECTED Year - $selectedYear")


        fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.setOnClickListener {
//            val c = Calendar.getInstance()
//            val year = c.get(Calendar.YEAR)
//            var month = c.get(Calendar.MONTH)
//            val day = c.get(Calendar.DAY_OF_MONTH)
//            datePicker.setMinDate(System.currentTimeMillis() - 1000)


            val dpd = DatePickerDialog(
                this.requireActivity(),
                { _, year, monthOfYear, dayOfMonth ->

                    // Display Selected date in textbox
                    // fragmentAdmissionFormBinding?.txtDob?.setText("" + dayOfMonth + "-" + (monthOfYear+1) + "-" + year)
                    monthstr = if ((monthOfYear + 1) < 10) {
                        "0" + (monthOfYear + 1)
                    } else {
                        (monthOfYear + 1).toString()
                    }

                    dayStr = if (dayOfMonth < 10) {
                        "0$dayOfMonth"

                    } else {
                        dayOfMonth.toString()
                    }

                    selectedYear = year
                    selectedmonth = monthOfYear
                    selectedday = dayOfMonth
//                fragmentSeedStockedBinding?.txtLsfSeedstockDateofStocking?.setText("" + year + "-" + monthstr + "-" + dayStr)
                    fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text =
                        "$year-$monthstr-$dayStr"
                    appointmentDate =
                        fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text?.toString()!!
                    if (!fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text?.toString()
                            .equals("") && fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text != null
                    ) {
                        selectDoctorSlotApiCall(fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text?.toString()!!)
                    }

                },
                selectedYear,
                selectedmonth,
                selectedday
            )


            dpd.show()
            //    dpd.setOnDateSetListener { view, year, month, dayOfMonth ->  }
            //Get the DatePicker instance from DatePickerDialog
            //Get the DatePicker instance from DatePickerDialog
            val dp = dpd.datePicker
//            dp.init(selectedYear, selectedmonth, selectedday, null);
//
//            if(selectedYear!=0 && selectedmonth!=0 && selectedday!=0){
//              dp.updateDate(selectedYear, selectedmonth, selectedday)
//            }
//
//            dpd.datePicker.minDate = Date().time
            dp.minDate = System.currentTimeMillis() - 1000
        }

        fragmentDoctorAppointmentRescheduleBinding?.edtReschedulePatientname?.setText(patientName)
        fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text =
            appointmentDate
        fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleStartTime?.setText(fromTime)
        fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleEndTime?.setText(toTime)
        fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleClinicName?.setText(clinicname)

        fragmentDoctorAppointmentRescheduleBinding?.btnAppointmentBooking?.setOnClickListener {

//            fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleStartTime?.setText(fromTime)
//            fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleEndTime?.setText(toTime)
            fromTime =
                fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleStartTime?.text?.toString()!!
            toTime =
                fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleEndTime?.text?.toString()!!
            if (fromTime != "" && toTime != "") {
                CommonDialog.showDialog(requireContext(), object :
                    DialogClickCallback {
                    override fun onDismiss() {
                    }

                    override fun onConfirm() {

                        if (isNetworkConnected) {
                            baseActivity?.showLoading()
                            val doctorAppointmentRescheduleRequest =
                                DoctorAppointmentRescheduleRequest()
                            doctorAppointmentRescheduleRequest.id = appointmentId
                            doctorAppointmentRescheduleRequest.serviceType = serviceType
                            doctorAppointmentRescheduleRequest.fromDate = appointmentDate
                            doctorAppointmentRescheduleRequest.toDate = appointmentDate
                            doctorAppointmentRescheduleRequest.fromTime = fromTime
                            doctorAppointmentRescheduleRequest.toTime = toTime

                            fragmentDoctorAppointmentRescheduleViewModel?.apiRescheduleAppointment(
                                doctorAppointmentRescheduleRequest
                            )

                        } else {
                            Toast.makeText(
                                activity,
                                "Please check your network connection.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                }, "Reschedule Appointment", "Are you sure to reschedule this family member?")
            } else {
                Toast.makeText(
                    activity,
                    "Please  select time slot to reschedule booking.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }

    // Set up recycler view for service listing if available
    private fun setUpDoctorSloytListingRecyclerview(doctorPrivateList: ArrayList<ResultItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentDoctorAppointmentRescheduleBinding!!.recyclerViewDoctorslot != null)
        val recyclerView = fragmentDoctorAppointmentRescheduleBinding!!.recyclerViewDoctorslot
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterDoctorPrivateSlot(doctorPrivateList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClick = object : OnDoctorPrivateSlotClickListner {
            override fun onItemClick(modelData: ResultItem?) {
//                clinicId= modelData?.clinicId!!
//                fromTime= modelData?.timeFrom!!
//                toTime= modelData?.timeTo!!
                if (modelData?.hospitalId != "") {
                    fragmentDoctorAppointmentRescheduleBinding?.etName?.text = "Hospital Name"
                    fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleClinicName?.setText(
                        modelData?.hospitalName
                    )
                } else {
                    fragmentDoctorAppointmentRescheduleBinding?.etName?.text = "Clinic Name"
                    fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleClinicName?.setText(
                        modelData.clinicName
                    )
                }

                fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleStartTime?.setText("")
                fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleEndTime?.setText("")
                if (modelData?.slot != null && modelData.slot.size > 0) {
                    fragmentDoctorAppointmentRescheduleBinding?.recyclerViewTimeslotby30minute?.visibility =
                        View.VISIBLE
                    fragmentDoctorAppointmentRescheduleBinding?.tvSelectTimeslotby30minuteNoDate?.visibility =
                        View.GONE
                    setUpToTimeListingRecyclerview(modelData.slot)
                } else {
                    fragmentDoctorAppointmentRescheduleBinding?.recyclerViewTimeslotby30minute?.visibility =
                        View.GONE
                    fragmentDoctorAppointmentRescheduleBinding?.tvSelectTimeslotby30minuteNoDate?.visibility =
                        View.VISIBLE
                    fragmentDoctorAppointmentRescheduleBinding?.tvSelectTimeslotby30minuteNoDate?.text =
                        "No slot available for booking."
                }

                for (i in 0 until modelData?.slot?.size!!) {
                    flag = if (modelData.slot[i]?.status.equals("Booked")) {
                        1
                    } else {
                        0
                    }

                }
                if (flag == 1) {
                    fragmentDoctorAppointmentRescheduleBinding?.txtSlotHeading?.text =
                        "No Slots Available:"
                    Toast.makeText(activity, "No slots available for this time.", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(activity, "Slots available for this time.", Toast.LENGTH_LONG)
                        .show()
                    fragmentDoctorAppointmentRescheduleBinding?.txtSlotHeading?.text =
                        "Available Slots:"
                }
//

            }


        }

    }

    // Set up recycler view for service listing if available
    private fun setUpToTimeListingRecyclerview(slotList: ArrayList<SlotItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentDoctorAppointmentRescheduleBinding!!.recyclerViewTimeslotby30minute != null)
        val recyclerView =
            fragmentDoctorAppointmentRescheduleBinding!!.recyclerViewTimeslotby30minute
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterDoctorSlotDivision(slotList, requireContext())
        recyclerView.adapter = contactListAdapter

        contactListAdapter.recyclerViewItemClickWithView = object : OnDoctorSlotClickListner {
            override fun onSloctClick(position: SlotItem) {
                fromTime = position.timeFrom!!
                toTime = position.timeTo!!
                fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleStartTime?.setText(fromTime)
                fragmentDoctorAppointmentRescheduleBinding?.edtRescheduleEndTime?.setText(toTime)
            }
//            override fun onItemClick(modelData: ResultItem?) {
//                clinicId= modelData?.clinicId!!
//                clinicFromTime= modelData?.timeFrom!!
//                clinicToTime= modelData?.timeTo!!
//            }
        }

    }

    override fun successDoctorPrivateSlotResponse(doctorPrivateSlotResponse: DoctorPrivateSlotResponse?) {
        baseActivity?.hideLoading()
        if (doctorPrivateSlotResponse?.code.equals("200")) {
            Toast.makeText(activity, doctorPrivateSlotResponse?.message, Toast.LENGTH_SHORT).show()
            if (doctorPrivateSlotResponse?.result != null && doctorPrivateSlotResponse.result.size > 0) {

                fragmentDoctorAppointmentRescheduleBinding?.btnAppointmentBooking?.visibility =
                    View.VISIBLE
                fragmentDoctorAppointmentRescheduleBinding?.recyclerViewDoctorslot?.visibility =
                    View.VISIBLE
                fragmentDoctorAppointmentRescheduleBinding?.recyclerViewTimeslotby30minute?.visibility =
                    View.VISIBLE
                fragmentDoctorAppointmentRescheduleBinding?.tvSelectTimeslotby30minuteNoDate?.visibility =
                    View.GONE
                fragmentDoctorAppointmentRescheduleBinding?.tvSelectDoctorSlotNoDate?.visibility =
                    View.GONE
//                clinicId= doctorPrivateSlotResponse?.result?.get(0)?.clinicId!!
//                clinicFromTime= doctorPrivateSlotResponse?.result?.get(0)?.timeFrom!!
//                clinicToTime= doctorPrivateSlotResponse?.result?.get(0)?.timeTo!!
                setUpDoctorSloytListingRecyclerview(doctorPrivateSlotResponse.result)

                if (doctorPrivateSlotResponse.result[0]?.slot != null && doctorPrivateSlotResponse.result[0]?.slot?.size!! > 0
                ) {
                    fragmentDoctorAppointmentRescheduleBinding?.recyclerViewTimeslotby30minute?.visibility =
                        View.VISIBLE
                    fragmentDoctorAppointmentRescheduleBinding?.tvSelectTimeslotby30minuteNoDate?.visibility =
                        View.GONE
                    setUpToTimeListingRecyclerview(doctorPrivateSlotResponse.result[0]?.slot)
                    for (i in 0 until doctorPrivateSlotResponse.result[0]?.slot?.size!!) {
                        flag =
                            if (doctorPrivateSlotResponse.result[0]?.slot?.get(i)?.status.equals("Booked")) {
                                1
                            } else {
                                0
                            }

                    }
                    if (flag == 1) {
                        fragmentDoctorAppointmentRescheduleBinding?.txtSlotHeading?.text =
                            "No Slots Available:"
                        Toast.makeText(
                            activity,
                            "No slots available for this clinic.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            activity,
                            "Slots available for this clinic.",
                            Toast.LENGTH_LONG
                        ).show()
                        fragmentDoctorAppointmentRescheduleBinding?.txtSlotHeading?.text =
                            "Available Slots:"
                    }
                } else {
                    fragmentDoctorAppointmentRescheduleBinding?.recyclerViewTimeslotby30minute?.visibility =
                        View.GONE
                    fragmentDoctorAppointmentRescheduleBinding?.tvSelectTimeslotby30minuteNoDate?.visibility =
                        View.VISIBLE
                    fragmentDoctorAppointmentRescheduleBinding?.tvSelectTimeslotby30minuteNoDate?.text =
                        "No slot available for booking."
                }
            } else {
                fragmentDoctorAppointmentRescheduleBinding?.btnAppointmentBooking?.visibility =
                    View.GONE
                fragmentDoctorAppointmentRescheduleBinding?.recyclerViewDoctorslot?.visibility =
                    View.GONE
                fragmentDoctorAppointmentRescheduleBinding?.recyclerViewTimeslotby30minute?.visibility =
                    View.GONE
                fragmentDoctorAppointmentRescheduleBinding?.tvSelectTimeslotby30minuteNoDate?.visibility =
                    View.GONE
                fragmentDoctorAppointmentRescheduleBinding?.tvSelectDoctorSlotNoDate?.visibility =
                    View.VISIBLE
                fragmentDoctorAppointmentRescheduleBinding?.tvSelectDoctorSlotNoDate?.text =
                    "No Doctor Slot Found"
            }
        } else {
            fragmentDoctorAppointmentRescheduleBinding?.btnAppointmentBooking?.visibility =
                View.GONE
            fragmentDoctorAppointmentRescheduleBinding?.recyclerViewDoctorslot?.visibility =
                View.GONE
            fragmentDoctorAppointmentRescheduleBinding?.recyclerViewTimeslotby30minute?.visibility =
                View.GONE
            fragmentDoctorAppointmentRescheduleBinding?.tvSelectTimeslotby30minuteNoDate?.visibility =
                View.GONE
            fragmentDoctorAppointmentRescheduleBinding?.tvSelectDoctorSlotNoDate?.visibility =
                View.VISIBLE
            fragmentDoctorAppointmentRescheduleBinding?.tvSelectDoctorSlotNoDate?.text =
                "No Doctor Slot Found"
            Toast.makeText(activity, doctorPrivateSlotResponse?.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun successDoctorRescheduleResponse(doctorRescheduleResponse: DoctorRescheduleResponse?) {
        baseActivity?.hideLoading()
        if (doctorRescheduleResponse?.code.equals("200")) {
            rescheduleMessage = doctorRescheduleResponse?.message.toString()
            if (doctorRescheduleResponse?.message == "Appointment Successfully Rescheduled.") {
                val pushNotificationRequest = PushNotificationRequest()
                pushNotificationRequest.name = patientName
                pushNotificationRequest.userId = doctorId
                pushNotificationRequest.userType = "All"
                pushNotificationRequest.notificationFor = "Reschedule"
                pushNotificationRequest.message =
                    "$patientName rescheduled this appointment for this date $appointmentDate"
                fragmentDoctorAppointmentRescheduleViewModel?.apiSendPush(pushNotificationRequest)

            } else {
                Toast.makeText(activity, doctorRescheduleResponse?.message, Toast.LENGTH_SHORT)
                    .show()
            }

//            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                FragmentMyUpCommingAppointment.newInstance())
        } else {
            //  AppConstants.IS_DOCTORRESCHEDULE=false
            Toast.makeText(activity, doctorRescheduleResponse?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun successPushNotification(response: JsonObject) {
        baseActivity?.hideLoading()
        val jsonObject: JsonObject = response
        val status = jsonObject["status"].asBoolean
        val code = jsonObject["code"].asString
        val message = jsonObject["message"].asString
        if (code == "200") {
            AppConstants.IS_DOCTOR_RESCHEDULE = true
            Toast.makeText(activity, rescheduleMessage, Toast.LENGTH_SHORT).show()
            if (AppConstants.rescheduleFrom != "") {
                when (AppConstants.rescheduleFrom) {
                    "Todays Appointment" -> {
                        (activity as HomeActivity).checkInBackstack(
                            FragmentTodaysAppointment.newInstance()
                        )
                        AppConstants.rescheduleFrom = ""
                    }
                    "Upcoming Appointment" -> {
                        (activity as HomeActivity).checkInBackstack(
                            FragmentMyUpCommingAppointment.newInstance()
                        )
                        AppConstants.rescheduleFrom = ""
                    }
                    "My Appointment" -> {
                        (activity as HomeActivity).checkInBackstack(
                            FragmentMyAppointment.newInstance()
                        )
                        AppConstants.rescheduleFrom = ""
                    }
                }
            } else {
                AppConstants.IS_DOCTOR_RESCHEDULE = false
                (activity as HomeActivity).checkInBackstack(
                    FragmentMyAppointment.newInstance()
                )
                AppConstants.rescheduleFrom = ""
            }
        }
    }

    override fun errorGetPatientFamilyListResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(HomeFragment.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }


    private fun selectDoctorSlotApiCall(selectDate: String) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val doctorPrivateSlotRequest = DoctorPrivateSlotRequest()
            doctorPrivateSlotRequest.date = selectDate
            doctorPrivateSlotRequest.doctorId = doctorId
            doctorPrivateSlotRequest.time = fromTime

//            if (serviceType == "hospital_doctor") {
            if (hospitalId != "") {


                doctorPrivateSlotRequest.hospitalId = hospitalId
                fragmentDoctorAppointmentRescheduleViewModel?.apiHospitalDoctorSlot(
                    doctorPrivateSlotRequest
                )
            } else {
                fragmentDoctorAppointmentRescheduleViewModel?.apiDoctorPrivateSlot(
                    doctorPrivateSlotRequest
                )
            }

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }
}