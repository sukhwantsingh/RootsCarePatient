package com.rootscare.ui.nurses.appointmentreschedule

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.dialog.CommonDialog
import com.google.gson.JsonObject
import com.interfaces.*
import com.model.SlotModel
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.appointmentdetailsrequest.AppointmentDetailsRequest
import com.rootscare.data.model.api.request.getproviderprefferedtime.GetProviderPreferredTimeRequest
import com.rootscare.data.model.api.request.getslotsrequest.GetSlotRequest
import com.rootscare.data.model.api.request.nurse.hourlyslot.NurseHourlySlotRequest
import com.rootscare.data.model.api.request.nurse.nurseslots.NurseSlotRequest
import com.rootscare.data.model.api.request.pushNotificationRequest.PushNotificationRequest
import com.rootscare.data.model.api.request.reschedule.DoctorAppointmentRescheduleRequest
import com.rootscare.data.model.api.response.getpreferredtimeslotresponse.GetPreferredTimeSlotResponse
import com.rootscare.data.model.api.response.getslotsres.GetSlotsResponse
import com.rootscare.data.model.api.response.nurses.nursehourlyslot.GetNurseHourlySlotResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.NueseViewTimingsResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.ResultItem
import com.rootscare.data.model.api.response.reschedule.doctorreschedule.DoctorRescheduleResponse
import com.rootscare.databinding.FragmentNurseAppointmentRescheduleBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.ui.myappointment.FragmentMyAppointment
import com.rootscare.ui.nurses.adapter.AdapterNurseBookingSlotsHourly
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterNurseHourlySlotRecycllerview
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterNurseSlotTiimeRecyclerview
import com.rootscare.utils.AppConstants
import com.rootscare.utils.ViewUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import kotlin.collections.ArrayList

class FragmentNurseAppointmentReschedule :
    BaseFragment<FragmentNurseAppointmentRescheduleBinding, FragmentNurseAppointmentRescheduleViewModel>(),
    FragmentNurseAppointmentRescheduleNavigator , GetSlotTimingInterface {
    private var fragmentNurseAppointmentRescheduleBinding: FragmentNurseAppointmentRescheduleBinding? =
        null
    private var fragmentNurseAppointmentRescheduleViewModel: FragmentNurseAppointmentRescheduleViewModel? =
        null

    private var nurseId: String = ""
    private var patientName: String = ""
    private var bookingFromTime: String = ""
    private var bookingToTime: String = ""
    private var fromDate: String = ""


    var bookedslotsList: ArrayList<String>? = ArrayList()
    //    private var toDate: String = ""
    private var appointmentId: String = ""
    private var displayHourOfTheDay = ""
    private var displaySecondHourOfTheDay = ""
    private var displayMinute = ""
    private var displaySecondMinute = ""
    private var nextHour: Int = 0
    private var hourlyDuration = 0
    private var monthstr: String = ""
    private var apiStartTime: String = ""
    private var apiEndTime: String = ""
    private var dayStr: String = ""
    private var format: String? = null
    private var nextFormat: String? = null
    private var serviceType: String? = null
    private var slotType: String? = null
    private var selectedYear = 0
    private var selectedmonth = 0
    private var selectedday = 0
    var selectedendtime:String =""
    var slotsDate: String =""
    var selectedstartTime:String =""
    private var timeDifference: String? = null
    private var rescheduleMessage: String? = ""
    var slotsList:ArrayList<SlotModel>? = ArrayList()
    private var nationalityDropdownList: ArrayList<String?>? = null
    var appointmentDetailData: JsonObject ? = null
    override val bindingVariable: Int

        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_nurse_appointment_reschedule
    override val viewModel: FragmentNurseAppointmentRescheduleViewModel
        get() {
            fragmentNurseAppointmentRescheduleViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentNurseAppointmentRescheduleViewModel::class.java)
            return fragmentNurseAppointmentRescheduleViewModel as FragmentNurseAppointmentRescheduleViewModel
        }

    companion object {
        fun newInstance(
            id: String,
            nurseId: String,
            patientName: String,
            fromTime: String,
            toTime: String,
            fromDate: String,
            serviceType: String,
            slotType: String
        ): FragmentNurseAppointmentReschedule {
            val args = Bundle()
            println("serviceType  $serviceType")
            args.putString("id", id)
            args.putString("nurseid", nurseId)
            args.putString("patientname", patientName)
            args.putString("fromtime", fromTime)
            args.putString("totime", toTime)
            args.putString("fromdate", fromDate)
//            args.putString("todate", toDate)
            args.putString("serviceType", serviceType)
            args.putString("slotType", slotType)
            val fragment = FragmentNurseAppointmentReschedule()
            fragment.arguments = args
            return fragment
        }

        var mySlotListTemp: ArrayList<SlotModel> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentNurseAppointmentRescheduleViewModel!!.navigator = this
    }

    private fun apiHitForDetails() {
        val request = AppointmentDetailsRequest()
        println("appointmentId  $appointmentId")
        println("serviceType  $serviceType")
//        serviceType = "nurse"
        request.id = appointmentId
        request.serviceType = serviceType
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentNurseAppointmentRescheduleViewModel!!.getAppointmentDetails(request)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentNurseAppointmentRescheduleBinding = viewDataBinding

        if (arguments != null && arguments?.getString("id") != null) {
            appointmentId = arguments?.getString("id")!!
            Log.d("Doctor Id", ": $appointmentId")
        }



        if (arguments != null && arguments?.getString("slotType") != null) {
            slotType = arguments?.getString("slotType")!!
            Log.d("Slot Type", ": $slotType")
        }
        if (arguments != null && arguments?.getString("nurseid") != null) {
            nurseId = arguments?.getString("nurseid")!!
            Log.d("Nurse Id", ": $nurseId")
        }

        if (arguments != null && arguments?.getString("patientname") != null) {
            patientName = arguments?.getString("patientname")!!
            Log.d("patientname", ": $patientName")
        }

        if (arguments != null && arguments?.getString("fromtime") != null) {
            bookingFromTime = arguments?.getString("fromtime")!!

            Log.d("totime", ": $bookingFromTime")
        }

        if (arguments != null && arguments?.getString("totime") != null) {
            bookingToTime = arguments?.getString("totime")!!
            Log.d("bookingTotime", ": $bookingToTime")
        }

        if (arguments != null && arguments?.getString("fromdate") != null) {
            fromDate = arguments?.getString("fromdate")!!
            Log.d("fromDate", ": $fromDate")


        }
        if (arguments != null && arguments?.getString("serviceType") != null) {
            serviceType = arguments?.getString("serviceType")!!
            Log.d("Service Type", ": $serviceType")
            apiHitForDetails()


        }
//        if (arguments != null && arguments?.getString("todate") != null) {
//            toDate = arguments?.getString("todate")!!
//            Log.d("todate", ": $toDate")
//        }

        if (patientName != "") {
            fragmentNurseAppointmentRescheduleBinding?.edtReschedulePatientname?.text = patientName
        } else {
            fragmentNurseAppointmentRescheduleBinding?.edtReschedulePatientname?.text = ""
        }

        println("slotType $slotType")

        var dt = fromDate // Start date
        val c = Calendar.getInstance()
        val sdf: SimpleDateFormat = if (serviceType.equals("pathology")) {
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
        } else {
            SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        }

        c.time = sdf.parse(dt)
        c.add(Calendar.DATE, 0) // number of days to add

        dt = sdf.format(c.time) // dt is now the new date

        println("dt - $dt")
        if (serviceType.equals("pathology")) {
            if (fromDate != "") {
                fragmentNurseAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text =
                    formatDateFromString("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd", fromDate)

                fragmentNurseAppointmentRescheduleBinding?.txtDoctorBookingSelectdate?.text =
                    formatDateFromString("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd", fromDate)

                slotsDate =  formatDateFromString("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd", fromDate)

            } else {
                fragmentNurseAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text = ""
                fragmentNurseAppointmentRescheduleBinding?.txtDoctorBookingSelectdate?.text = ""
                slotsDate =""


            }
        } else {
            if (fromDate != "") {
                fragmentNurseAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text =
                    formatDateFromString("yyyy-MM-dd", "yyyy-MM-dd", fromDate)
                fragmentNurseAppointmentRescheduleBinding?.txtDoctorBookingSelectdate?.text =
                    formatDateFromString("yyyy-MM-dd", "yyyy-MM-dd", fromDate)

                slotsDate =  formatDateFromString("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd", fromDate)
            } else {
                fragmentNurseAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text = ""
                fragmentNurseAppointmentRescheduleBinding?.txtDoctorBookingSelectdate?.text = ""


                slotsDate = ""
            }
        }

        selectedmonth = c[Calendar.MONTH]
        println("SELECTED Month - " + String.format("%02d", selectedmonth + 1))
        selectedday = c[Calendar.DAY_OF_MONTH]
        println("SELECTED Day - " + String.format("%02d", selectedday))
        selectedYear = c[Calendar.YEAR]
        println("SELECTED Year - $selectedYear")


        if (bookingFromTime != "") {
            fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.text =
                bookingFromTime
        } else {
            fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.text = ""
        }

        if (bookingToTime != "") {
            fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.text = bookingToTime
        } else {
            fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.text = ""
        }

        nationalityDropdownList = ArrayList()
        nationalityDropdownList?.add("Slots")
        nationalityDropdownList?.add("Hourly")

        fragmentNurseAppointmentRescheduleBinding?.txtSelectSlotOrHour?.setOnClickListener {
            CommonDialog.showDialogForDropDownList(
                this.requireActivity(), "Select Time", nationalityDropdownList,
                object :
                    DropDownDialogCallBack {
                    override fun onConfirm(text: String) {
                        fragmentNurseAppointmentRescheduleBinding?.txtSelectSlotOrHour?.text = text
                        if (text == "Slots") {
                            apiHitForNurseViewTiming()
                        } else if (text == "Hourly") {
                            apiHitForNurseHourlySlot()
                        }
                    }

                })
        }

//        apiHitForNurseViewTiming()

        if (serviceType.equals("physiotherapy") || serviceType.equals("nurse") ) {
            fragmentNurseAppointmentRescheduleBinding?.edtNurseFromTime?.setOnClickListener {
                val formatter: DateFormat = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
                val d1: Date?
                val c = Calendar.getInstance(TimeZone.getDefault())
                try {
                    d1 = formatter.parse(
                        fragmentNurseAppointmentRescheduleBinding?.edtNurseFromTime?.text.toString()
                            .trim()
                    )
                    c.time = d1
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val chour = c[Calendar.HOUR_OF_DAY]
                val cminute = c[Calendar.MINUTE]
                TimePickerDialog(requireActivity(), { _, hourOfDay, minute ->
                    var hoursOfDay = hourOfDay
                    var nextHoursDay = hourOfDay
                    var nextMinute = minute + 30
                    var nextFormat: String? = null
                    var isValidTime: Boolean? = true
                    when {
                        hourOfDay > 12 -> {
                            println("hourOfDay > 12")
                            hoursOfDay -= 12
                            format = "PM"
                        }
                        hourOfDay == 0 -> {
                            println("hourOfDay == 0")
                            hoursOfDay += 12
                            format = "AM"
                        }
                        hourOfDay == 12 -> {
                            println("hourOfDay == 12")
                            format = "PM"
                        }
                        else -> {
                            format = "AM"
                        }
                    }
                    when {
                        nextMinute > 60 -> {
                            println("nextHoursDay122333 $nextHoursDay")
                            nextMinute -= 60
                            when {
                                nextHoursDay > 12 -> {
                                    isValidTime = false
                                    Toast.makeText(
                                        this.activity,
                                        "Invalid End Time. Please maintain 30 minutes gap",
                                        Toast.LENGTH_LONG
                                    ).show()
//                                    nextHoursDay += 1
//                                    nextHoursDay -= 12
//                                    nextFormat = format
                                }
                                nextHoursDay == 0 -> {
                                    nextHoursDay += 1
                                    nextFormat = "AM"
                                }
                                nextHoursDay in 12 downTo 11 -> {
                                    nextHoursDay += 1
                                    nextFormat = "PM"
                                }
                                else -> {
                                    nextHoursDay += 1
                                    nextFormat = "AM"
                                }
                            }
                        }
                        nextMinute < 60 -> {
                            println("hourOfDay == 0")
                            println("nextHoursDay1121 $nextHoursDay")
                            when {
                                nextHoursDay > 12 -> {
                                    nextHoursDay -= 12
                                    nextFormat = "PM"
                                }
                                nextHoursDay == 0 -> {
                                    nextHoursDay += 1
                                    nextFormat = "AM"
                                }
                                nextHoursDay == 12 -> {
                                    nextFormat = "AM"
                                }
                                else -> {
                                    nextFormat = "AM"
                                }
                            }
                        }
                        nextMinute == 60 -> {
                            nextMinute -= 60
                            println("hourOfDay == 12")
                            println("nextHoursDay3332222 $nextHoursDay")
                            when {
                                nextHoursDay > 12 -> {
//                                    nextHoursDay -= 11
//                                    nextFormat = "AM"
                                    isValidTime = false
                                    Toast.makeText(
                                        this.activity,
                                        "Invalid End Time. Please maintain 30 minutes gap",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                nextHoursDay == 0 -> {
                                    nextHoursDay += 1
                                    nextFormat = "AM"
                                }
                                nextHoursDay in 12 downTo 11 -> {
                                    nextHoursDay += 1
                                    nextFormat = "PM"
                                }
                                else -> {
                                    nextHoursDay += 1
                                    nextFormat = "AM"
                                }
                            }
                        }
                    }
                    println("nextHoursDay $nextHoursDay")
                    println("nextMinute $nextMinute")
                    println("nextFormat $nextFormat")

//                    val hoursOfDay: String =
//                        if (hourOfDay < 10) "0$hourOfDay" else java.lang.String.valueOf(hourOfDay)
                    val minutes: String =
                        if (minute < 10) "0$minute" else java.lang.String.valueOf(minute)

                    val nextMinutes: String =
                        if (nextMinute < 10) "0$nextMinute" else java.lang.String.valueOf(
                            nextMinute
                        )

                    val aTime = "$hoursOfDay:$minutes $format"
                    val bTime = "$nextHoursDay:$nextMinutes $nextFormat"
                    println("aTime==>$aTime")
                    fragmentNurseAppointmentRescheduleBinding?.edtNurseFromTime?.text = aTime
                    fragmentNurseAppointmentRescheduleBinding?.edtNurseToTime?.text =
                        if (isValidTime == true) bTime else ""

                    fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.text =
                        aTime
                    fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.text =
                        if (isValidTime == true) bTime else ""
                }, chour, cminute, false).show()
            }
        } else {
            fragmentNurseAppointmentRescheduleBinding?.edtNurseFromTime?.setOnClickListener {
                val calendar = Calendar.getInstance()
                val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
                val calendarMinute = calendar.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(
                    context, { _, hourOfDay, minute ->
                        val datetime = Calendar.getInstance()
                        val calendar = Calendar.getInstance()
                        datetime[Calendar.HOUR_OF_DAY] = hourOfDay
                        datetime[Calendar.MINUTE] = minute
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                        val strDate =
                           /* sdf.parse(fragmentNurseAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text.toString())*/
                            sdf.parse(fragmentNurseAppointmentRescheduleBinding?.txtDoctorBookingSelectdate?.text.toString())


                         if (System.currentTimeMillis() > strDate!!.time) {
                            if (datetime.timeInMillis >= calendar.timeInMillis) {
//                        val hour = hourOfDay % 12
                                var hourOfDay = hourOfDay
                                nextHour = hourOfDay + hourlyDuration
                                println("hourOfDay 123 $hourOfDay")
                                when {
                                    hourOfDay > 12 -> {
                                        println("hourOfDay > 12")
                                        hourOfDay -= 12
                                        format = "PM"
                                    }
                                    hourOfDay == 0 -> {
                                        println("hourOfDay == 0")
                                        hourOfDay += 12
                                        format = "AM"
                                    }
                                    hourOfDay == 12 -> {
                                        println("hourOfDay == 12")
                                        format = "PM"
                                    }
                                    else -> {
                                        format = "AM"
                                    }
                                }

                                when {
                                    nextHour > 24 -> {
                                        nextHour -= 24
                                        nextFormat = "AM"
                                    }
                                    nextHour == 0 -> {
                                        nextHour += 24
                                        nextFormat = "AM"
                                    }
                                    nextHour == 24 -> {
                                        nextHour -= 12
                                        nextFormat = "AM"
                                    }
                                    else -> {
                                        if (nextHour < 12) {
                                            nextHour = (12 + nextHour) - 12
                                            nextFormat = "AM"
                                        } else {
                                            nextHour -= 12
                                            if (nextHour == 0) {
                                                nextHour = 12
                                            }
                                            nextFormat = "PM"

                                        }
                                    }
                                }
                                println("nextHour  $nextHour")

                                displayHourOfTheDay = if (hourOfDay < 10) {
                                    "0$hourOfDay"

                                } else {
                                    hourOfDay.toString()
                                }

                                displayMinute = if (minute < 10) {
                                    "0$minute"

                                } else {
                                    minute.toString()
                                }

                                displaySecondHourOfTheDay = if (nextHour < 10) {
                                    "0$nextHour"

                                } else {
                                    nextHour.toString()
                                }
                                displaySecondMinute = if (minute < 10) {
                                    "0$minute"

                                } else {
                                    minute.toString()
                                }

                                fragmentNurseAppointmentRescheduleBinding?.edtNurseFromTime?.text =
                                    "$displayHourOfTheDay:$displayMinute $format"
                                fragmentNurseAppointmentRescheduleBinding?.edtNurseToTime?.text =
                                    "$displaySecondHourOfTheDay:$displaySecondMinute $nextFormat"

                                fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.text =
                                    "$displayHourOfTheDay:$displayMinute $format"
                                fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.text =
                                    "$displaySecondHourOfTheDay:$displaySecondMinute $nextFormat"

                            } else {
                                Toast.makeText(
                                    this.activity,
                                    "Invalid Time",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            var hourOfDay = hourOfDay
                            println("hourOfDay 123 $hourOfDay")
                            nextHour = hourOfDay + hourlyDuration
                            println("nextHour 123 $nextHour")

                            when {
                                hourOfDay > 12 -> {
                                    println("hourOfDay > 12")
                                    hourOfDay -= 12
                                    format = "PM"
                                }
                                hourOfDay == 0 -> {
                                    println("hourOfDay == 0")
                                    hourOfDay += 12
                                    format = "AM"
                                }
                                hourOfDay == 12 -> {
                                    println("hourOfDay == 12")
                                    format = "PM"
                                }
                                else -> {
                                    format = "AM"
                                }
                            }

                            when {
                                nextHour > 24 -> {
                                    nextHour -= 24
                                    nextFormat = "AM"
                                }
                                nextHour == 0 -> {
                                    nextHour += 24
                                    nextFormat = "AM"
                                }
                                nextHour == 24 -> {
                                    nextHour -= 12
                                    nextFormat = "AM"
                                }
                                else -> {
                                    if (nextHour < 12) {
                                        nextHour = (12 + nextHour) - 12
                                        nextFormat = "AM"
                                    } else {
                                        nextHour -= 12
                                        if (nextHour == 0) {
                                            nextHour = 12
                                        }
                                        nextFormat = "PM"

                                    }
                                }
                            }
                            displayHourOfTheDay = if (hourOfDay < 10) {
                                "0$hourOfDay"

                            } else {
                                hourOfDay.toString()
                            }

                            displayMinute = if (minute < 10) {
                                "0$minute"

                            } else {
                                minute.toString()
                            }

                            displaySecondHourOfTheDay = if (nextHour < 10) {
                                "0$nextHour"

                            } else {
                                nextHour.toString()
                            }
                            displaySecondMinute = if (minute < 10) {
                                "0$minute"

                            } else {
                                minute.toString()
                            }

                            fragmentNurseAppointmentRescheduleBinding?.edtNurseFromTime?.text =
                                "$displayHourOfTheDay:$displayMinute $format"
                            fragmentNurseAppointmentRescheduleBinding?.edtNurseToTime?.text =
                                "$displaySecondHourOfTheDay:$displaySecondMinute $nextFormat"

                            fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.text =
                                "$displayHourOfTheDay:$displayMinute $format"
                            fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.text =
                                "$displaySecondHourOfTheDay:$displaySecondMinute $nextFormat"
                        }

                    }, calendarHour, calendarMinute, false
                )
                timePickerDialog.show()
            }
        }


//        fragmentNurseAppointmentRescheduleBinding?.edtNurseFromTime?.setOnClickListener(View.OnClickListener {
//            val calendar = Calendar.getInstance()
//            val CalendarHour = calendar.get(Calendar.HOUR_OF_DAY)
//            val  CalendarMinute = calendar.get(Calendar.MINUTE)
//            val  timepickerdialog = TimePickerDialog(
//                context,
//                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
//                    var hourOfDay = hourOfDay
//                    if (hourOfDay == 0) {
//                        hourOfDay += 12
//                        format = "AM"
//                    } else if (hourOfDay == 12) {
//                        format = "PM"
//                    } else if (hourOfDay > 12) {
//                        hourOfDay -= 12
//                        format = "PM"
//                    } else {
//                        format = "AM"
//                    }
//                    fragmentNurseAppointmentRescheduleBinding?.edtNurseFromTime?.setText("$hourOfDay:$minute$format")
//                    fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.setText("$hourOfDay:$minute$format")
//                }, CalendarHour, CalendarMinute, false
//            )
//            timepickerdialog.show()
//        })
//
//
//        fragmentNurseAppointmentRescheduleBinding?.edtNurseToTime?.setOnClickListener(View.OnClickListener {
//            val calendar = Calendar.getInstance()
//            val CalendarHour = calendar.get(Calendar.HOUR_OF_DAY)
//            val  CalendarMinute = calendar.get(Calendar.MINUTE)
//            val  timepickerdialog = TimePickerDialog(
//                context,
//                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
//                    var hourOfDay = hourOfDay
//                    if (hourOfDay == 0) {
//                        hourOfDay += 12
//                        format = "AM"
//                    } else if (hourOfDay == 12) {
//                        format = "PM"
//                    } else if (hourOfDay > 12) {
//                        hourOfDay -= 12
//                        format = "PM"
//                    } else {
//                        format = "AM"
//                    }
//                    fragmentNurseAppointmentRescheduleBinding?.edtNurseToTime?.setText("$hourOfDay:$minute$format")
//                    fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.setText("$hourOfDay:$minute$format")
//                }, CalendarHour, CalendarMinute, false
//            )
//            timepickerdialog.show()
//        })

//        fragmentNurseAppointmentRescheduleBinding?.edtNurseFromTime?.setOnClickListener {
//            val calendar = Calendar.getInstance()
//            val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
//            val calendarMinute = calendar.get(Calendar.MINUTE)
//            val timePickerDialog = TimePickerDialog(
//                context, { _, hourOfDay, minute ->
//
//                }, calendarHour, calendarMinute, false
//            )
//            timePickerDialog.show()
//
//        }


//        fragmentNurseAppointmentRescheduleBinding?.edtNurseToTime?.addTextChangedListener(object :
//            TextWatcher {
//            override fun afterTextChanged(s: Editable) { // you can call or do what you want with your EditText here
//                if (s.toString().isNotEmpty()) {
//                    fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.text =
//                        s.toString()
//                }
//            }
//
//            override fun beforeTextChanged(
//                s: CharSequence,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(
//                s: CharSequence,
//                start: Int,
//                before: Int,
//                count: Int
//            ) {
////                if(s.toString().length>0){
////                 fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.setText(s.toString())
////                }
//            }
//        })

/*
        fragmentNurseAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.setOnClickListener { */
            fragmentNurseAppointmentRescheduleBinding?.txtDoctorBookingSelectdate?.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                var month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
//            datePicker.setMinDate(System.currentTimeMillis() - 1000)


                val dpd = DatePickerDialog(
                    this.requireActivity(),
                    { _, year, monthOfYear, dayOfMonth ->

                        // Display Selected date in text box
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
                        fragmentNurseAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text =
                            "$year-$monthstr-$dayStr"
                        fragmentNurseAppointmentRescheduleBinding?.txtDoctorBookingSelectdate?.text =
                            "$year-$monthstr-$dayStr"
                        fromDate =
                            fragmentNurseAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text?.toString()!!
                        fromDate = fragmentNurseAppointmentRescheduleBinding?.txtDoctorBookingSelectdate?.text?.toString()!!
                 slotsDate =  "$year-$monthstr-$dayStr"

apiHitGetStartTimeEndTime()

                   //     apiHitGetBookedSlots(serviceType!!,nurseId.toInt(),slotsDate)
                      /*  if ((apiStartTime != null && apiStartTime != "") &&
                            (apiEndTime != null && apiEndTime != "")
                        ) {
                            fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.GONE
                            fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.VISIBLE
                            getSlots(slotsDate,slotsDate,apiStartTime,apiEndTime,1800000,30)


                        }else{
                            fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                            fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE
                        }
*/
//
//                    toDate =
//                        fragmentNurseAppointmentRescheduleBinding?.edtRescheduleAppointmentdate?.text?.toString()!!
                        if (fragmentNurseAppointmentRescheduleBinding?.txtSelectSlotOrHour?.text?.toString()
                                .equals("Slots")
                        ) {
//                        if (isNetworkConnected) {
//                            baseActivity?.showLoading()
//                            val nurseSlotRequest = NurseSlotRequest()
//                            nurseSlotRequest.userId =
//                                fragmentNurseAppointmentRescheduleViewModel?.appSharedPref?.userId
//                            nurseSlotRequest.serviceProviderId = nurseId
//                            nurseSlotRequest.serviceType = "nurse"
//                            nurseSlotRequest.fromDate = fromDate
//                            nurseSlotRequest.toDate = toDate
//                            fragmentNurseAppointmentRescheduleViewModel?.taskBasedSlots(
//                                nurseSlotRequest
//                            )
//                        } else {
//                            Toast.makeText(
//                                activity,
//                                "Please check your network connection.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                        }

                    },
                    selectedYear,
                    selectedmonth,
                    selectedday
                )

                dpd.show()
                //Get the DatePicker instance from DatePickerDialog
                //Get the DatePicker instance from DatePickerDialog
                val dp = dpd.datePicker
//            if(selectedYear!=0 && selectedmonth!=0 && selectedday!=0){
//                dp.updateDate(selectedYear, selectedmonth, selectedday)
//            }else{
//                dp.updateDate(year,  c.get(Calendar.MONTH), c.get(Calendar.DATE))
////                c.set(year, c.get(Calendar.MONTH), c.get(Calendar.DATE))
//            }
                dp.minDate = System.currentTimeMillis() - 1000
            }

            fragmentNurseAppointmentRescheduleBinding?.btnNurseBookNow?.setOnClickListener {
                bookingFromTime =selectedstartTime
                  /*  fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.text?.toString()!!*/
                bookingToTime = selectedendtime
                   /* fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.text?.toString()!!*/
                if (serviceType.equals("nurse")) {
                    if (slotType.equals("Hourly_slot")) {
                        if (bookingFromTime != "" && bookingToTime != "") {
                            CommonDialog.showDialog(
                                requireContext(),
                                object :
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
                                            doctorAppointmentRescheduleRequest.fromDate = fromDate
                                            doctorAppointmentRescheduleRequest.toDate = fromDate
                                            doctorAppointmentRescheduleRequest.fromTime =
                                                bookingFromTime
                                            doctorAppointmentRescheduleRequest.toTime = bookingToTime
                                            fragmentNurseAppointmentRescheduleViewModel?.apiRescheduleAppointment(
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

                                },
                                "Reschedule Appointment",
                                "Are you sure to reschedule this family member?"
                            )
                        } else {
                            Toast.makeText(
                                activity,
                                "Please  select time slot/hourly to reschedule booking.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        CommonDialog.showDialog(
                            requireContext(),
                            object :
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
                                        doctorAppointmentRescheduleRequest.fromDate = fromDate
                                        doctorAppointmentRescheduleRequest.toDate = fromDate
                                        doctorAppointmentRescheduleRequest.fromTime = /*"10:00 AM"*/ bookingFromTime
                                        doctorAppointmentRescheduleRequest.toTime = /*"11:00 AM"*/ bookingToTime
                                        fragmentNurseAppointmentRescheduleViewModel?.apiRescheduleAppointment(
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

                            },
                            "Reschedule Appointment",
                            "Are you sure to reschedule this family member?"
                        )
                    }
                } else {
                    if (bookingFromTime != "" && bookingToTime != "") {
                        CommonDialog.showDialog(
                            requireContext(),
                            object :
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
                                        doctorAppointmentRescheduleRequest.fromDate = fromDate
                                        doctorAppointmentRescheduleRequest.toDate = fromDate
                                        doctorAppointmentRescheduleRequest.fromTime =
                                            bookingFromTime
                                        doctorAppointmentRescheduleRequest.toTime = bookingToTime
                                        fragmentNurseAppointmentRescheduleViewModel?.apiRescheduleAppointment(
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

                            },
                            "Reschedule Appointment",
                            "Are you sure to reschedule this family member?"
                        )
                    } else {
                        Toast.makeText(
                            activity,
                            "Please  select time slot/hourly to reschedule booking.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


    override fun successNurseViewTimingsResponse(nurseViewTimingsResponse: NueseViewTimingsResponse?) {
        baseActivity?.hideLoading()
        if (nurseViewTimingsResponse?.code.equals("200")) {

            if (nurseViewTimingsResponse?.result != null && nurseViewTimingsResponse.result.size > 0) {
                fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
                    View.VISIBLE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateSlottime?.visibility = View.GONE
                setNurseViewTimingListing(nurseViewTimingsResponse.result)
            } else {
                fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
                    View.GONE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateSlottime?.visibility =
                    View.VISIBLE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateSlottime?.text =
                    "No timings found."
            }

        } else {
            fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
                View.GONE
            fragmentNurseAppointmentRescheduleBinding?.tvNoDateSlottime?.visibility = View.VISIBLE
            fragmentNurseAppointmentRescheduleBinding?.tvNoDateSlottime?.text = "No timings found."
        }

    }


    override fun successGetBookedSlots(getSlotsResponse: GetSlotsResponse) {
        baseActivity?.hideLoading()
        bookedslotsList?.clear()
        if (getSlotsResponse?.code.equals("200")) {
            if (getSlotsResponse?.result != null && getSlotsResponse.result.size > 0) {
                Log.e("res slopts", "--" + getSlotsResponse.result)
                bookedslotsList?.clear()
                for (i in 0 until getSlotsResponse.result.size) {
                    bookedslotsList?.add(getSlotsResponse.result.get(i).fromTime.toUpperCase())
                }

                Log.e("bookedslotsList", "--" + bookedslotsList)



            }else{

            }
            setAppointmentData(appointmentDetailData!!)
        }
    }

    override fun successGetNurseHourlySlotResponse(getNurseHourlySlotResponse: GetNurseHourlySlotResponse?) {
        baseActivity?.hideLoading()
        if (getNurseHourlySlotResponse?.code.equals("200")) {
            if (getNurseHourlySlotResponse?.result != null && getNurseHourlySlotResponse.result.size > 0) {
                fragmentNurseAppointmentRescheduleBinding?.llHourlyTime?.visibility = View.GONE
                fragmentNurseAppointmentRescheduleBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
                fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                    View.VISIBLE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.visibility =
                    View.GONE
                val index: Int = getNurseHourlySlotResponse.result[0]?.duration?.indexOf(" ")!!
                val firstString: String =
                    getNurseHourlySlotResponse.result[0]?.duration?.substring(0, index)!!
//                Toast.makeText(activity,firstString, Toast.LENGTH_SHORT).show()
                hourlyDuration = timeDifference?.split(" ")?.get(0)?.toInt()!!
                println("hourlyDuration outside  $hourlyDuration")
                var hourlyList: ArrayList<com.rootscare.data.model.api.response.nurses.nursehourlyslot.ResultItem?>? =
                    null
                for (nurseHourlyRates in getNurseHourlySlotResponse.result) {
//                    println("hourlyDuration  ${nurseHourlyRates?.duration}")
                    if (nurseHourlyRates != null) {
                        if (timeDifference?.split(" ")
                                ?.get(0) == nurseHourlyRates.duration?.split(" ")?.get(0)
                        ) {
                            println("hourlyDuration  $hourlyDuration")
                            hourlyList = ArrayList()

                            hourlyList.add(nurseHourlyRates)
                        }
                    }
                }
                if (hourlyList != null && hourlyList.size > 0)
                    setUpHourlyTimeListingRecyclerview(hourlyList)
//                setUpHourlyTimeListingRecyclerview(getNurseHourlySlotResponse.result)
            } else {
                fragmentNurseAppointmentRescheduleBinding?.llHourlyTime?.visibility = View.GONE
                fragmentNurseAppointmentRescheduleBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
                fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                    View.GONE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.visibility =
                    View.GONE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.text =
                    "No hourly slot found."
            }

        } else {
            fragmentNurseAppointmentRescheduleBinding?.llHourlyTime?.visibility = View.GONE
            fragmentNurseAppointmentRescheduleBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
            fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                View.GONE
            fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.visibility = View.GONE
            fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.text =
                "No hourly slot found."
        }
    }

    override fun successDoctorRescheduleResponse(doctorRescheduleResponse: DoctorRescheduleResponse?) {
        baseActivity?.hideLoading()
        if (doctorRescheduleResponse?.code.equals("200")) {
            rescheduleMessage = doctorRescheduleResponse?.message.toString()
            val pushNotificationRequest = PushNotificationRequest()
            pushNotificationRequest.name = patientName
            pushNotificationRequest.userId = nurseId
            pushNotificationRequest.userType = "All"
            pushNotificationRequest.notificationFor = "Reschedule"
            pushNotificationRequest.message =
                "$patientName rescheduled this appointment for this date $fromDate"
            fragmentNurseAppointmentRescheduleViewModel?.apiSendPush(pushNotificationRequest)
        } else {
            AppConstants.IS_NURSE_RESCHEDULE = false
            Toast.makeText(activity, doctorRescheduleResponse?.message, Toast.LENGTH_SHORT).show()
        }

    }


    override fun errorGetPatientFamilyListResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(HomeFragment.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    //Setup recyclerview for nurse view timing recyclerview
    private fun setNurseViewTimingListing(nurseTimingList: ArrayList<ResultItem?>?) {
        assert(fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareFromTimeRecyclerview != null)
        val recyclerView =
            fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareFromTimeRecyclerview
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        val contactListAdapter =
            AdapterNurseSlotTiimeRecyclerview(nurseTimingList, requireContext())
//        hourlyDuration = timeDifference?.split(" ")?.get(0)?.toInt()!!
        recyclerView?.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnNurseSlotClick {
            override fun onConfirm(modelItem: ResultItem) {
                println("modelItem $modelItem")
//                nurseFromTime=modelItem?.startTime!!
//                nurseToTime=modelItem?.endTime!!

                fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.text =
                    modelItem.startTime
                nextHour = modelItem.startTime?.split(" ")?.get(0)?.split(":")?.get(0)
                    ?.toInt()!! + hourlyDuration

                println("nextHour $nextHour")
                when {
                    nextHour > 24 -> {
                        nextHour -= 24
                        nextFormat = "AM"
                    }
                    nextHour == 0 -> {
                        nextHour += 24
                        nextFormat = "AM"
                    }
                    nextHour == 24 -> {
                        nextHour -= 12
                        nextFormat = "AM"
                    }
                    else -> {
                        if (nextHour > 12) {
                            nextHour -= 12
                            nextFormat = "PM"
                        } else {
                            nextFormat = "AM"
                        }

                    }
                }
                fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.text =
                    "$nextHour:${
                        modelItem.endTime?.split(" ")?.get(0)?.split(":")?.get(1)
                    } $nextFormat"

            }

        }
    }

    // Set up recycler view for service listing if available
    private fun setUpHourlyTimeListingRecyclerview(hourlyList: ArrayList<com.rootscare.data.model.api.response.nurses.nursehourlyslot.ResultItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentNurseAppointmentRescheduleBinding!!.recyclerViewRootscareHourlyTimeRecyclerview != null)
        val recyclerView =
            fragmentNurseAppointmentRescheduleBinding!!.recyclerViewRootscareHourlyTimeRecyclerview
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterNurseHourlySlotRecycllerview(hourlyList, requireContext())
        recyclerView.adapter = contactListAdapter

        contactListAdapter.recyclerViewItemClickWithView = object : OnHourlyItemClick {
            override fun onConfirm(modelItem: com.rootscare.data.model.api.response.nurses.nursehourlyslot.ResultItem) {
//                nurseHourlyprice=modelItem?.price!!
//                fragmentNurseAppointmentRescheduleBinding?.txtHourlyPrice?.setText("SR"+" "+modelItem?.price)
                fragmentNurseAppointmentRescheduleBinding?.edtNurseFromTime?.text = ""
                fragmentNurseAppointmentRescheduleBinding?.edtNurseToTime?.text = ""
                fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.text = ""
                fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.text = ""
                val index: Int = modelItem.duration?.indexOf(" ")!!
                val firstString: String = modelItem.duration.substring(0, index)
//                Toast.makeText(activity,firstString, Toast.LENGTH_SHORT).show()
                hourlyDuration = firstString.toInt()
            }
        }

    }

    fun apiHitForNurseViewTiming() {
        fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
            View.GONE
        fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.visibility = View.GONE
        fragmentNurseAppointmentRescheduleBinding?.llHourlyTime?.visibility = View.GONE
        fragmentNurseAppointmentRescheduleBinding?.llHourlyTimeNew?.visibility = View.GONE
        fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
            View.VISIBLE
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val nurseSlotRequest = NurseSlotRequest()
            nurseSlotRequest.userId =
                fragmentNurseAppointmentRescheduleViewModel?.appSharedPref?.userId
            nurseSlotRequest.serviceProviderId = nurseId
            nurseSlotRequest.serviceType = "nurse"
            nurseSlotRequest.fromDate =
                fragmentNurseAppointmentRescheduleBinding?.txtDoctorBookingSelectdate?.text.toString()
            nurseSlotRequest.toDate =
                fragmentNurseAppointmentRescheduleBinding?.txtDoctorBookingSelectdate?.text?.toString()

            slotsDate = fragmentNurseAppointmentRescheduleBinding?.txtDoctorBookingSelectdate?.text?.toString()!!
            fragmentNurseAppointmentRescheduleViewModel?.taskBasedSlots(nurseSlotRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun apiHitForNurseHourlySlot() {
        fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
            View.GONE
        fragmentNurseAppointmentRescheduleBinding?.tvNoDateSlottime?.visibility = View.GONE
        fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
            View.VISIBLE
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val nurseHourlySlotRequest = NurseHourlySlotRequest()
            nurseHourlySlotRequest.userId = nurseId.toInt()
            fragmentNurseAppointmentRescheduleViewModel?.apiGetHourlyRates(nurseHourlySlotRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun apiHitForNurseAppointmentReschedule() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val doctorAppointmentRescheduleRequest = DoctorAppointmentRescheduleRequest()
            doctorAppointmentRescheduleRequest.id = appointmentId
            doctorAppointmentRescheduleRequest.serviceType = "nurse"
            doctorAppointmentRescheduleRequest.fromDate = fromDate
            doctorAppointmentRescheduleRequest.toDate = fromDate
            doctorAppointmentRescheduleRequest.fromTime = bookingFromTime
            doctorAppointmentRescheduleRequest.toTime = bookingToTime

            fragmentNurseAppointmentRescheduleViewModel?.apiRescheduleAppointment(
                doctorAppointmentRescheduleRequest
            )

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }
    fun apiHitGetBookedSlots(servicetype:String,serviceid:Int,slotsdate:String) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            var getSlotRequest = GetSlotRequest()
            getSlotRequest.serviceid = serviceid.toInt()
            getSlotRequest.appointmentdate = slotsdate
            getSlotRequest.service_type = servicetype


            fragmentNurseAppointmentRescheduleViewModel?.getBookedSlots(getSlotRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }
    fun setAppointmentData(result: JsonObject){





         /*   apiStartTime = result["start_time"].asString.trim()
            apiEndTime = result["end_time"].asString.trim()
            slotsDate = result["to_date"].asString.trim()



            Log.e("start_time","--"+apiStartTime.trim())*/

       // apiHitGetBookedSlots(serviceType!!,nurseId.toInt(),slotsDate)


            if (serviceType.equals("nurse")) {
                if (slotType.equals("Hourly_slot")) {


                    fragmentNurseAppointmentRescheduleBinding?.tvRescheduleStartTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.tvRescheduleEndTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.tvSelectTime?.visibility =
                        View.VISIBLE
                    apiHitForNurseHourlySlot()


                    timeDifference = result["time_diff"].asString
                    hourlyDuration = timeDifference?.split(" ")?.get(0)?.toInt()!!



                    if ((apiStartTime != null && apiStartTime != "") &&
                        (apiEndTime != null && apiEndTime != "")
                    ) {
                        fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.GONE
                        fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.VISIBLE

                        if (hourlyDuration==2) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,7200000,120)
                        } else if (hourlyDuration==4) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,14400000,240)
                        }else if (hourlyDuration==6) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,21600000,360)
                        }else if (hourlyDuration==8) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,28800000,480)
                        }else if (hourlyDuration==12) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,43200000,720)
                        }

                    }else{
                        fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                        fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE
                    }




                }else{


                    fragmentNurseAppointmentRescheduleBinding?.llHourlyTime?.visibility = View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                        View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.visibility =
                        View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.text =
                        "No hourly slot found."


                    getSlots(slotsDate,slotsDate,apiStartTime,
                        apiEndTime,1800000,30)
                }
            } else if (serviceType.equals("caregiver")  ) {
                timeDifference = result["time_diff"].asString


                hourlyDuration = timeDifference?.split(" ")?.get(0)?.toInt()!!


                if ((apiStartTime != null && apiStartTime != "") &&
                    (apiEndTime != null && apiEndTime != "")
                ) {
                    fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.VISIBLE

                    if (hourlyDuration==2) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,7200000,120)
                    } else if (hourlyDuration==4) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,14400000,240)
                    }else if (hourlyDuration==6) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,21600000,360)
                    }else if (hourlyDuration==8) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,28800000,480)
                    }else if (hourlyDuration==12) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,43200000,720)
                    }

                }else{
                    fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE
                }

            } else if  (serviceType.equals("babysitter")) {
                timeDifference = result["time_diff"].asString


                hourlyDuration = timeDifference?.split(" ")?.get(0)?.toInt()!!


                if ((apiStartTime != null && apiStartTime != "") &&
                    (apiEndTime != null && apiEndTime != "")
                ) {
                    fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.VISIBLE

                    if (hourlyDuration==2) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,7200000,120)
                    } else if (hourlyDuration==4) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,14400000,240)
                    }else if (hourlyDuration==6) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,21600000,360)
                    }else if (hourlyDuration==8) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,28800000,480)
                    }else if (hourlyDuration==12) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,43200000,720)
                    }

                }else{
                    fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE
                }

            }
            /*else  if (serviceType.equals("nurse")) {
                if (slotType.equals("Hourly_slot")) {
                    fragmentNurseAppointmentRescheduleBinding?.tvRescheduleStartTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.tvRescheduleEndTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.tvSelectTime?.visibility =
                        View.VISIBLE
                    apiHitForNurseHourlySlot()

                    timeDifference = result["time_diff"].asString
                    hourlyDuration = timeDifference?.split(" ")?.get(0)?.toInt()!!


                    if ((apiStartTime != null && apiStartTime != "") &&
                        (apiEndTime != null && apiEndTime != "")
                    ) {
                        fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.GONE
                        fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.VISIBLE

                        if (hourlyDuration==2) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,7200000,120)
                        } else if (hourlyDuration==4) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,14400000,240)
                        }else if (hourlyDuration==6) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,21600000,360)
                        }else if (hourlyDuration==8) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,28800000,480)
                        }else if (hourlyDuration==12) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,43200000,720)
                        }

                    }else{
                        fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                        fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE
                    }



                } else {
                    *//*  fragmentNurseAppointmentRescheduleBinding?.tvRescheduleStartTime?.visibility =
                          View.VISIBLE
                      fragmentNurseAppointmentRescheduleBinding?.tvRescheduleEndTime?.visibility =
                          View.VISIBLE
                      fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.visibility =
                          View.VISIBLE
                      fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.visibility =
                          View.VISIBLE
                      fragmentNurseAppointmentRescheduleBinding?.tvSelectTime?.visibility = View.VISIBLE
                    // apiHitForNurseViewTiming()
  *//*

                    fragmentNurseAppointmentRescheduleBinding?.llHourlyTime?.visibility = View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                        View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.visibility =
                        View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.text =
                        "No hourly slot found."



                }
            }*/ else if (serviceType.equals("physiotherapy")) {
                fragmentNurseAppointmentRescheduleBinding?.llHourlyTime?.visibility = View.GONE
                fragmentNurseAppointmentRescheduleBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
                fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                    View.GONE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.visibility =
                    View.GONE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.text =
                    "No hourly slot found."
//                apiHitForNurseHourlySlot()


                getSlots(slotsDate,slotsDate,apiStartTime,
                    apiEndTime,1800000,30)


            }else if (serviceType.equals("pathology")) {
                fragmentNurseAppointmentRescheduleBinding?.llHourlyTime?.visibility = View.GONE
                fragmentNurseAppointmentRescheduleBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
                fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                    View.GONE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.visibility =
                    View.GONE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.text =
                    "No hourly slot found."
//                apiHitForNurseHourlySlot()


                getSlots(slotsDate,slotsDate,apiStartTime,
                    apiEndTime,1800000,30)


            } else {
                apiHitForNurseHourlySlot()
            }

    }

    override fun onSuccessDetails(response: JsonObject) {
        baseActivity?.hideLoading()
        val jsonObject: JsonObject = response

        val status = jsonObject["status"].asBoolean
        val message = jsonObject["message"].asString

        println("slotType $slotType")

        if (status) {


            val result = jsonObject["result"].asJsonObject


            appointmentDetailData = result
               apiStartTime = result["start_time"].asString.trim()
              apiEndTime = result["end_time"].asString.trim()
              slotsDate = result["to_date"].asString.trim()


           // apiHitGetBookedSlots(serviceType!!,nurseId.toInt(),slotsDate)


          apiHitGetStartTimeEndTime()



/*

            if ((apiStartTime != null && apiStartTime != "") &&
                (apiEndTime != null && apiEndTime != "")
            ) {
                fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.GONE
                fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.VISIBLE
                getSlots(slotsDate,slotsDate,apiStartTime,apiEndTime,1800000,30)
            }else{
                fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE
            }
*/


/*

            if (serviceType.equals("nurse")) {
                if (slotType.equals("Hourly_slot")) {
                    timeDifference = result["time_diff"].asString
                    hourlyDuration = timeDifference?.split(" ")?.get(0)?.toInt()!!



                    if ((apiStartTime != null && apiStartTime != "") &&
                        (apiEndTime != null && apiEndTime != "")
                    ) {
                        fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.GONE
                        fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.VISIBLE

                        if (hourlyDuration==2) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,7200000,120)
                        } else if (hourlyDuration==4) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,14400000,240)
                        }else if (hourlyDuration==6) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,21600000,360)
                        }else if (hourlyDuration==8) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,28800000,480)
                        }else if (hourlyDuration==12) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,43200000,720)
                        }

                    }else{
                        fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                        fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE
                    }




                }else{
                    getSlots(slotsDate,slotsDate,apiStartTime,
                        apiEndTime,1800000,30)
                }
            } else if (serviceType.equals("caregiver")  ) {
                timeDifference = result["time_diff"].asString


                hourlyDuration = timeDifference?.split(" ")?.get(0)?.toInt()!!


                if ((apiStartTime != null && apiStartTime != "") &&
                    (apiEndTime != null && apiEndTime != "")
                ) {
                    fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.VISIBLE

                    if (hourlyDuration==2) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,7200000,120)
                    } else if (hourlyDuration==4) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,14400000,240)
                    }else if (hourlyDuration==6) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,21600000,360)
                    }else if (hourlyDuration==8) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,28800000,480)
                    }else if (hourlyDuration==12) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,43200000,720)
                    }

                }else{
                    fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE
                }

            } else if  (serviceType.equals("babysitter")) {
                timeDifference = result["time_diff"].asString


                hourlyDuration = timeDifference?.split(" ")?.get(0)?.toInt()!!


                if ((apiStartTime != null && apiStartTime != "") &&
                    (apiEndTime != null && apiEndTime != "")
                ) {
                    fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.VISIBLE

                    if (hourlyDuration==2) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,7200000,120)
                    } else if (hourlyDuration==4) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,14400000,240)
                    }else if (hourlyDuration==6) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,21600000,360)
                    }else if (hourlyDuration==8) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,28800000,480)
                    }else if (hourlyDuration==12) {
                        getSlots(slotsDate,slotsDate,apiStartTime,
                            apiEndTime,43200000,720)
                    }

                }else{
                    fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE
                }

            }
          else  if (serviceType.equals("nurse")) {
                if (slotType.equals("Hourly_slot")) {
                    fragmentNurseAppointmentRescheduleBinding?.tvRescheduleStartTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.tvRescheduleEndTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.visibility =
                        View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.tvSelectTime?.visibility =
                        View.VISIBLE
                    apiHitForNurseHourlySlot()

                    timeDifference = result["time_diff"].asString
                    hourlyDuration = timeDifference?.split(" ")?.get(0)?.toInt()!!


                    if ((apiStartTime != null && apiStartTime != "") &&
                        (apiEndTime != null && apiEndTime != "")
                    ) {
                        fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.GONE
                        fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.VISIBLE

                        if (hourlyDuration==2) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,7200000,120)
                        } else if (hourlyDuration==4) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,14400000,240)
                        }else if (hourlyDuration==6) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,21600000,360)
                        }else if (hourlyDuration==8) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,28800000,480)
                        }else if (hourlyDuration==12) {
                            getSlots(slotsDate,slotsDate,apiStartTime,
                                apiEndTime,43200000,720)
                        }

                    }else{
                        fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                        fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE
                    }



                } else {

                    fragmentNurseAppointmentRescheduleBinding?.llHourlyTime?.visibility = View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                        View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.visibility =
                        View.GONE
                    fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.text =
                        "No hourly slot found."



                }
            } else if (serviceType.equals("physiotherapy")) {
                fragmentNurseAppointmentRescheduleBinding?.llHourlyTime?.visibility = View.GONE
                fragmentNurseAppointmentRescheduleBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
                fragmentNurseAppointmentRescheduleBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                    View.GONE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.visibility =
                    View.GONE
                fragmentNurseAppointmentRescheduleBinding?.tvNoDateHourlytime?.text =
                    "No hourly slot found."
//                apiHitForNurseHourlySlot()





                getSlots(slotsDate,slotsDate,apiStartTime,
                    apiEndTime,1800000,30)

            } else {
                apiHitForNurseHourlySlot()
            }*/
        }
    }

    override fun successPushNotification(response: JsonObject) {
        baseActivity?.hideLoading()
        val jsonObject: JsonObject = response
//        val status = jsonObject["status"].asBoolean
        val code = jsonObject["code"].asString
//        val message = jsonObject["message"].asString
        if (code == "200") {
            AppConstants.IS_NURSE_RESCHEDULE = true
            Toast.makeText(activity, rescheduleMessage, Toast.LENGTH_SHORT).show()
            if (AppConstants.rescheduleFrom != "") {
                when (AppConstants.rescheduleFrom) {
//                    "Today's Appointment" -> {
//                        (activity as HomeActivity).checkFragmentInBackStackAndOpen(
//                            FragmentTodaysAppointment.newInstance()
//                        )
//                        AppConstants.rescheduleFrom = ""
//                    }
//                    "Upcoming Appointment" -> {
//                        (activity as HomeActivity).checkFragmentInBackStackAndOpen(
//                            FragmentMyUpCommingAppointment.newInstance()
//                        )
//                        AppConstants.rescheduleFrom = ""
//                    }
                    "My Appointment" -> {
                        (activity as HomeActivity).checkInBackstack(
                            FragmentMyAppointment.newInstance()
                        )
                        AppConstants.rescheduleFrom = ""
                    }
                }
            } else {
                (activity as HomeActivity).checkInBackstack(
                    FragmentMyAppointment.newInstance()
                )
                AppConstants.rescheduleFrom = ""
            }
        }
    }

    override fun onThrowable(throwable: Throwable) {
    }
/*
    fun getSlots( fisrt_date : String ,second_date : String , starttime: String , endtime: String,diff:Long,timetoadd:Int){

        slotsList?.clear()
      mySlotListTemp?.clear()


        val date1 = */
/*"26/02/2011"*//*
 fisrt_date
        val time1 =*/
/* "00:00 AM"*//*
starttime
        val date2 = */
/*"26/02/2011"*//*
 second_date
        val time2 = */
/*"12:00 PM"*//*
endtime
        */
/* yyyy-MM-dd*//*

        val format = "yyyy-MM-dd hh:mm a"

        val sdf = SimpleDateFormat(format)

        val dateObj1 = sdf.parse("$date1 $time1")
        val dateObj2 = sdf.parse("$date2 $time2")
        println("Date Start: $dateObj1")
        println("Date End: $dateObj2")

//Date d = new Date(dateObj1.getTime() + 3600000);


//Date d = new Date(dateObj1.getTime() + 3600000);
        var dif = dateObj1.time
        while (dif < dateObj2.time) {
            val slot = Date(dif)

            val formateDate = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(slot)
            Log.v("output date ", formateDate)
            Log.e("slot:--","--"+formateDate)
            println("Hour Slot --->$slot")
            dif += */
/*1800000*//*
 diff



            //check for todays days time
            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH)
            val sdff = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val strDate = sdf.parse(fisrt_date +" " +formateDate)
            Log.e("System.currentMillis:--","--"+System.currentTimeMillis())
            Log.e("strDate.time:--","--"+strDate.time)
            Log.e("aaaaaa:--","--"+sdff.parse(ViewUtils.getCurrentDate()))
            Log.e("bbbbbb:--","--"+sdff.parse(fisrt_date))

            if (sdff.parse(ViewUtils.getCurrentDate())==sdff.parse(fisrt_date))
            {

                if (System.currentTimeMillis() > strDate.time) {

                    slotsList?.add(SlotModel(false,formateDate,false,timetoadd))
                    mySlotListTemp.add(SlotModel(false,formateDate,false,timetoadd))
                }else{

                    slotsList?.add(SlotModel(false,formateDate,true,timetoadd))
                    mySlotListTemp.add(SlotModel(false,formateDate,true,timetoadd))
                }}
            else
            {
                slotsList?.add(SlotModel(false,formateDate,true,timetoadd))
                mySlotListTemp.add(SlotModel(false,formateDate,true,timetoadd))
            }



            Log.e("slotsList:--","--"+slotsList)

            setNurseHourlyAdapter(slotsList!!)
        }
    }

*/

    fun getSlots(
        fisrt_date: String,
        second_date: String,
        starttime: String,
        endtime: String,
        diff: Long,
        timetoadd: Int
    ) {

        slotsList?.clear()
        mySlotListTemp?.clear()


        val date1 = /*"26/02/2011"*/ fisrt_date
        val time1 =/* "00:00 AM"*/starttime
        val date2 = /*"26/02/2011"*/ second_date
        val time2 = /*"12:00 PM"*/endtime
        /* yyyy-MM-dd*/
        val format = "yyyy-MM-dd hh:mm a"

        val sdf = SimpleDateFormat(format)

        val dateObj1 = sdf.parse("$date1 $time1")
        val dateObj2 = sdf.parse("$date2 $time2")
        println("Date Start: $dateObj1")
        println("Date End: $dateObj2")

//Date d = new Date(dateObj1.getTime() + 3600000);


//Date d = new Date(dateObj1.getTime() + 3600000);
        var dif = dateObj1.time
        while (dif < dateObj2.time) {
            val slot = Date(dif)

            val formateDate = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(slot)
            Log.v("output date ", formateDate)
            Log.e("slot:--", "--" + formateDate)
            println("Hour Slot --->$slot")
            dif += /*1800000*/ diff


            //check for todays days time
            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH)
            val sdff = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val strDate = sdf.parse(fisrt_date + " " + formateDate)
            Log.e("System.currentMillis:--", "--" + System.currentTimeMillis())
            Log.e("strDate.time:--", "--" + strDate.time)
            Log.e("aaaaaa:--", "--" + sdff.parse(ViewUtils.getCurrentDate()))
            Log.e("bbbbbb:--", "--" + sdff.parse(fisrt_date))

            if (sdff.parse(ViewUtils.getCurrentDate()) == sdff.parse(fisrt_date)) {

                if (System.currentTimeMillis() > strDate.time) {

                    slotsList?.add(SlotModel(false, formateDate, false, timetoadd))
                    mySlotListTemp.add(SlotModel(false, formateDate, false, timetoadd))
                } else {

                    slotsList?.add(SlotModel(false, formateDate, true, timetoadd))
                    mySlotListTemp.add(SlotModel(false, formateDate, true, timetoadd))
                }
            } else {
                slotsList?.add(SlotModel(false, formateDate, true, timetoadd))
                mySlotListTemp.add(SlotModel(false, formateDate, true, timetoadd))
            }
            Log.e("bookedslotsList new:--", "--" + bookedslotsList)

            //   bookedslotsList!!.add("06:00 PM") ////just to check that condition works or not
            for (i in 0 until slotsList?.size!!) {
                for (j in 0 until bookedslotsList?.size!!) {
                    Log.e("t1", bookedslotsList?.get(j).toString())
                    Log.e("t2", slotsList?.get(i)?.time.toString())
                    var tempTime: String? = ""
                    if (slotsList?.get(i)?.time.toString().startsWith("0")) {
                        tempTime = slotsList?.get(i)?.time.toString()
                            .substring(1, slotsList?.get(i)?.time.toString().length)
                    } else {
                        tempTime = slotsList?.get(i)?.time.toString()
                    }
                    var tempTime2: String? = ""
                    if (bookedslotsList?.get(j).toString().startsWith("0")) {
                        tempTime2 = bookedslotsList?.get(j).toString()
                            .substring(1, bookedslotsList?.get(j).toString().length)
                    } else {
                        tempTime2 = bookedslotsList?.get(j).toString()
                    }
                    Log.e("t3", tempTime.toString())
                    if (tempTime2.equals(tempTime)!!) {
                        slotsList?.set(
                            i,
                            SlotModel(false, slotsList?.get(i)?.time.toString(), false, timetoadd)
                        )
                    }
                }
            }



            Log.e("slotsList new:--", "--" + slotsList)


            setNurseHourlyAdapter(slotsList!!)
        }
    }

    //Setup recyclerview for nurse view timing recyclerview
    private fun setNurseHourlyAdapter( nurseTimingList: ArrayList<SlotModel> ) {
        assert(fragmentNurseAppointmentRescheduleBinding!!.recyclerViewSlots != null)
        val recyclerView = fragmentNurseAppointmentRescheduleBinding!!.recyclerViewSlots
        val gridLayoutManager = GridLayoutManager(activity, 4, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val adapterNurseBookingSlotsHourly = AdapterNurseBookingSlotsHourly( nurseTimingList,  context!!,this)
        recyclerView.adapter = adapterNurseBookingSlotsHourly
    }

    private fun formatDateFromString(
        inputFormat: String?,
        outputFormat: String?,
        inputDate: String?
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
    override fun onClickSlot(starttime: String, endtime: String) {
        selectedstartTime = starttime
        selectedendtime= endtime


        fragmentNurseAppointmentRescheduleBinding?.edtRescheduleStartTime?.setText(selectedstartTime)
        fragmentNurseAppointmentRescheduleBinding?.edtRescheduleEndTime?.setText(selectedendtime)
    }

    fun apiHitGetStartTimeEndTime() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            var getSlotRequest = GetProviderPreferredTimeRequest()
            getSlotRequest.user_id = nurseId
            getSlotRequest.slot_date = slotsDate
            getSlotRequest.service_type = serviceType

            fragmentNurseAppointmentRescheduleViewModel?.getProviderPreferredTime(getSlotRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }


    override fun successGetProviderPreferredTime(getSlotsResponse: GetPreferredTimeSlotResponse) {


        baseActivity?.hideLoading()



        if (getSlotsResponse.code.equals("200")) {
            if (getSlotsResponse.result != null) {


                if ((apiStartTime != null && apiStartTime != "") &&
                    (apiEndTime != null && apiEndTime != "")
                ) {


                    setAppointmentData(appointmentDetailData!!)

                }else{
                    fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE

                }

            }else{
                fragmentNurseAppointmentRescheduleBinding?.tvNoSlots?.visibility = View.VISIBLE
                fragmentNurseAppointmentRescheduleBinding?.recyclerViewSlots?.visibility = View.GONE
            }

        }


    }


}