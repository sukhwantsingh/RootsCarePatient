package com.rootscare.ui.physiotherapy.bookingappointment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dialog.CommonDialog
import com.interfaces.*
import com.model.SlotModel
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.deletepatientfamilymemberrequest.DeletePatientFamilyMemberRequest
import com.rootscare.data.model.api.request.doctorrequest.getpatientfamilymemberrequest.GetPatientFamilyMemberRequest
import com.rootscare.data.model.api.request.getproviderprefferedtime.GetProviderPreferredTimeRequest
import com.rootscare.data.model.api.request.getslotsrequest.GetSlotRequest
import com.rootscare.data.model.api.request.nurse.nursedetailsrequest.NurseDetailsRequest
import com.rootscare.data.model.api.request.nurse.nurseslots.NurseSlotRequest
import com.rootscare.data.model.api.request.nurse.nursrtask.PhysiotherapyTask
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse.BookingCartResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.data.model.api.response.getpreferredtimeslotresponse.GetPreferredTimeSlotResponse
import com.rootscare.data.model.api.response.getslotsres.GetSlotsResponse
import com.rootscare.data.model.api.response.nurses.nursebookappointment.NurseBookAppointmentResponse
import com.rootscare.data.model.api.response.nurses.nursedetails.NurseDetailsResponse
import com.rootscare.data.model.api.response.nurses.nursehourlyslot.GetNurseHourlySlotResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.NueseViewTimingsResponse
import com.rootscare.data.model.api.response.nurses.taskresponse.GetTaskResponse
import com.rootscare.data.model.api.response.nurses.taskresponse.ResultItem
import com.rootscare.databinding.FragmentBookingLabBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.bookingappointment.adapter.AdapterFromTimeRecyclerview
import com.rootscare.ui.bookingappointment.subfragment.editpatient.FragmentEditPatientFamilyMember
import com.rootscare.ui.bookingcart.FragmentBookingCart
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.ui.nurses.adapter.AdapterNurseBookingSlotsHourly
import com.rootscare.ui.nurses.addpatient.FragmentNurseAddPatient
import com.rootscare.ui.nurses.nursesappointmentbookingdetails.FragmentNursesAppointmentBookingDetails
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterForNursesAddPatientListRecyclerview
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterNurseHourlySlotRecycllerview
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterNurseSlotTiimeRecyclerview
import com.rootscare.ui.physiotherapy.bookingappointment.adapter.AdapterPhysiotherapistTask
import com.rootscare.utilitycommon.BaseMediaUrls
import com.rootscare.utils.ManagePermissions
import com.rootscare.utils.ViewUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.math.RoundingMode
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class FragmentPhysiotherapyBookingAppointment :
    BaseFragment<FragmentBookingLabBinding, FragmentPhysiotherapyBookingAppointmentViewModel>(),
    FragmentPhysiotherapyBookingAppointmentNavigator, AdapterPhysiotherapistTask.AdapterCallback ,
    GetSlotTimingInterface {
    private var fragmentNursesBookingAppointmentBinding: FragmentBookingLabBinding? = null
    private var fragmentNursesBookingAppointmentViewModel: FragmentPhysiotherapyBookingAppointmentViewModel? =
        null
    private var nurseId: String = ""

    //    private var nurseImage: String = ""
//    private var hospitalId: String = ""
    private var familyMemberId = ""
    private var publication_list1: ArrayList<HashMap<String, Int?>>? = null
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var fileName: String? = null
    private var recordingFile: File? = null
    private var lastProgress = 0
    private val mHandler = Handler()
    private val RECORD_AUDIO_REQUEST_CODE = 123
    private var isPlaying = false

    private val GALLERY = 1
    private val CAMERA = 2
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 3
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private var imageFile: File? = null
    private var monthstr: String = ""
    private var dayStr: String = ""
    private var nurseFirstName = ""
    private var nurseStartTime = ""
    private var nurseEndTime = ""
    var slotsList:ArrayList<SlotModel>? = ArrayList()
    private var nurseLastName = ""

    //    private var dailyrate = ""
    private var fromBookingTime: String = ""
    private var toBookingTime: String = ""
    private var nurseVisitPrice = ""
    private var nurseHourlyprice = ""
    private var hourlyDuration = 0
    private var nationalityDropdownlist: ArrayList<String?>? = null

    private var prescriptionimage: RequestBody? = null
    private var prescriptionimagemultipartBody: MultipartBody.Part? = null

    private var symptomsRecordingFile: RequestBody? = null
    private var symptomsRecordingFilemultipartBody: MultipartBody.Part? = null

    private var nurseFromTime = ""
    private var nurseToTime = ""

    var taskId: String? = ""
    var task = StringBuilder()
    var selectedendtime:String =""
    var slotsDate: String =""
    var selectedstartTime:String =""
    //    private var x: String? = ""
//    private var labPrice: String? = ""
    private var format: String? = null

    private var nextFormat: String? = null
    private var displayHourOfTheDay = ""
    private var displaySecondHourOfTheDay = ""
    private var displayMinute = ""
    private var displaySecondMinute = ""
    private var nextHour: Int = 0
    private var total: Int = 0
    private var selectedYear = 0
    private var selectedmonth = 0
    private var selectedday = 0
    private val dec = DecimalFormat("#,###.00")
var nurseDetailsResponseData:NurseDetailsResponse?=null
//    private var fragmentPhysiotherapyBookingAppiontmentBinding: FragmentPhysiotherapyBookingAppiontmentBinding? = null
//    private var fragmentPhysiotherapyBookingAppointmentViewModel: FragmentPhysiotherapyBookingAppointmentViewModel? = null
//    override val bindingVariable: Int
//        get() = BR.viewModel
//    override val layoutId: Int
//        get() = R.layout.fragment_physiotherapy_booking_appiontment
//    override val viewModel: FragmentPhysiotherapyBookingAppointmentViewModel
//        get() {
//            fragmentPhysiotherapyBookingAppointmentViewModel =
//                ViewModelProviders.of(this).get(FragmentPhysiotherapyBookingAppointmentViewModel::class.java!!)
//            return fragmentPhysiotherapyBookingAppointmentViewModel as FragmentPhysiotherapyBookingAppointmentViewModel
//        }

    var bookedslotsList: ArrayList<String>? = ArrayList()
    var labArr =
        arrayListOf<ResultItem?>()
    lateinit var contactListAdapter: AdapterPhysiotherapistTask
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_booking_lab
    override val viewModel: FragmentPhysiotherapyBookingAppointmentViewModel
        get() {
            fragmentNursesBookingAppointmentViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentPhysiotherapyBookingAppointmentViewModel::class.java)
            return fragmentNursesBookingAppointmentViewModel as FragmentPhysiotherapyBookingAppointmentViewModel
        }

    companion object {
        private const val IMAGE_DIRECTORY = "/demonuts"
        fun newInstance(nurseid: String): FragmentPhysiotherapyBookingAppointment {
            val args = Bundle()
            args.putString("nurseid", nurseid)
//            args.putString("nurseImage", nurseImage)
//            args.putString("hospitalId", hospitalId)
            val fragment = FragmentPhysiotherapyBookingAppointment()
            fragment.arguments = args
            return fragment
        }

        var mySlotListTemp: ArrayList<SlotModel> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentNursesBookingAppointmentViewModel!!.navigator = this
        dec.roundingMode = RoundingMode.CEILING
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentNursesBookingAppointmentBinding = viewDataBinding

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionToRecordAudio()
        }

        val list = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this.activity!!, list, PermissionsRequestCode)

        //check permissions states

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            managePermissions.checkPermissions()

        if (arguments != null && arguments?.getString("nurseid") != null) {
            nurseId = arguments?.getString("nurseid")!!
//            nurseImage = arguments?.getString("nurseImage")!!
//            hospitalId = arguments?.getString("hospitalId")!!
            Log.d("nurseid Id", ": $nurseId")
        }
        nationalityDropdownlist = ArrayList<String?>()
        nationalityDropdownlist?.add("Task Slot")
        nationalityDropdownlist?.add("Hourly Slot")

        apiHitForFamilyMemberList()
        apiHitForNurseDetails()
        apiHitForNurseTask()
        fragmentNursesBookingAppointmentBinding?.llMain?.setOnClickListener {
            baseActivity?.hideKeyboard()
        }
        fragmentNursesBookingAppointmentBinding?.btnAddPatient?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentNurseAddPatient.newInstance(nurseId)
            )
        }

        fragmentNursesBookingAppointmentBinding?.btnNurseBookNow?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentNursesAppointmentBookingDetails.newInstance()
            )
        }

        //VOICE RECORDING SECTION

        fragmentNursesBookingAppointmentBinding?.imageViewRecord?.setOnClickListener {
            prepareForRecording()
            startRecording()
        }

        fragmentNursesBookingAppointmentBinding?.imageViewStop?.setOnClickListener {
            prepareForStop()
            stopRecording()
        }

        fragmentNursesBookingAppointmentBinding?.imageViewPlay?.setOnClickListener {
            if (!isPlaying && fileName != null) {
                isPlaying = true
                startPlaying()
            } else {
                fragmentNursesBookingAppointmentBinding?.chronometerTimer?.stop()
                isPlaying = false
                stopPlaying()
            }
        }


        //End of Voice recording section


        fragmentNursesBookingAppointmentBinding?.edtNurseFromTime?.setOnClickListener {
            val formatter: DateFormat = SimpleDateFormat("hh:mm aa")
            val d1: Date?
            val c = Calendar.getInstance(TimeZone.getDefault())
            try {
                d1 = formatter.parse(
                    fragmentNursesBookingAppointmentBinding?.edtNurseFromTime?.text.toString()
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
//                                nextHoursDay += 1
//                                nextHoursDay -= 12
//                                nextFormat = format
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

                val minutes: String =
                    if (minute < 10) "0$minute" else java.lang.String.valueOf(minute)

                val nextMinutes: String =
                    if (nextMinute < 10) "0$nextMinute" else java.lang.String.valueOf(
                        nextMinute
                    )

                val aTime = "$hoursOfDay:$minutes $format"
                val bTime = "$nextHoursDay:$nextMinutes $nextFormat"
                println("aTime==>$aTime")
                fragmentNursesBookingAppointmentBinding?.edtNurseFromTime?.text = aTime
                fragmentNursesBookingAppointmentBinding?.edtNurseToTime?.text =
                    if (isValidTime == true) bTime else ""
            }, chour, cminute, false).show()
        }

        fragmentNursesBookingAppointmentBinding?.txtDoctorbookingUploadPrescriptionimage?.setOnClickListener {
            showPictureDialog()
        }

        // Set todays date and get clinic list and doctor according to todays date
        val c = Calendar.getInstance().time
        println("Current time => $c")

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val formattedDate = df.format(c)

        slotsDate = formattedDate

        fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text = formattedDate
//        if (!fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text?.toString()
//                .equals("") && fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text != null
//        ) {
//            // selectDoctorSlotApiCall(fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()!!)
//            apiHitForNurseViewTiming()
//        }
        //End this section

        fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
//            datePicker.setMinDate(System.currentTimeMillis() - 1000)
            if (selectedYear != 0 && selectedmonth != 0 && selectedday != 0) {
                c.set(selectedYear, selectedmonth, selectedday)
            } else {
                c.set(year, c.get(Calendar.MONTH), c.get(Calendar.DATE))
            }

            val dpd = DatePickerDialog(
                this.activity!!,
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
                    fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text =
                        "$year-$monthstr-$dayStr"
                    slotsDate =     "$year-$monthstr-$dayStr"

                    apiHitGetBookedSlots()
//                    if (!fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text?.toString()
//                            .equals("") && fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text != null
//                    ) {
//                        if (fragmentNursesBookingAppointmentBinding?.txtSelectSlotOrHour?.text?.toString()
//                                .equals("Slots")
//                        ) {
//                            apiHitForNurseViewTiming()
//                        }
//
//                        //selectDoctorSlotApiCall(fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()!!)
//                    }
//
//                    if ((nurseDetailsResponseData?.result?.startTime != null && nurseDetailsResponseData?.result?.startTime != "") &&
//                        (nurseDetailsResponseData?.result?.endTime != null && nurseDetailsResponseData?.result?.endTime != "")
//                    ) {
//                        fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.GONE
//                        fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.VISIBLE
//                        getSlots(slotsDate,slotsDate,nurseDetailsResponseData?.result?.startTime!!,nurseDetailsResponseData?.result?.endTime!!,1800000,30)
//                    }else{
//                        fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.VISIBLE
//                        fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.GONE
//                    }



                    apiHitGetStartTimeEndTime()

                },
                year,
                month,
                day
            )

            dpd.show()
            //Get the DatePicker instance from DatePickerDialog
            //Get the DatePicker instance from DatePickerDialog
            val dp = dpd.datePicker
            if (selectedYear != 0 && selectedmonth != 0 && selectedday != 0) {
                dp.updateDate(selectedYear, selectedmonth, selectedday)
            } else {
                dp.updateDate(year, c.get(Calendar.MONTH), c.get(Calendar.DATE))
//                c.set(year, c.get(Calendar.MONTH), c.get(Calendar.DATE))
            }
            dp.minDate = System.currentTimeMillis() - 1000
        }
//        apiHitForNurseHourlySlot()


//        fragmentNursesBookingAppointmentBinding?.txtSelectSlotOrHour?.setOnClickListener {
//            CommonDialog.showDialogForDropDownList(
//                this.activity!!,
//                "Select Time",
//                nationalityDropdownlist,
//                object :
//                    DropDownDialogCallBack {
//                    override fun onConfirm(text: String) {
//                        fragmentNursesBookingAppointmentBinding?.txtSelectSlotOrHour?.text = text
//                        if (text == "Task Slot") {
//                            apiHitForNurseViewTiming()
//                        } else if (text == "Hourly Slot") {
//                            apiHitForNurseHourlySlot()
//                        }
//                    }
//
//                })
//        }

        fragmentNursesBookingAppointmentBinding?.btnNurseBookNow?.setOnClickListener {
            if (familyMemberId == "") {
                familyMemberId = "0"
            }
            nurseVisitPrice = total.toString()
            fromBookingTime = selectedstartTime
              /*  fragmentNursesBookingAppointmentBinding?.edtNurseFromTime?.text?.toString()!!*/
            toBookingTime = selectedendtime
              /*  fragmentNursesBookingAppointmentBinding?.edtNurseToTime?.text?.toString()!!*/

            println("nurseVisitPrice $nurseVisitPrice")
            println("fromBookingTime $fromBookingTime")
            println("toBookingTime $toBookingTime")
//            if (nurseVisitPrice != "" && fromBookingTime != "" && toBookingTime != "") {
//            if (nurseVisitPrice != "0") {
            if (fromBookingTime != "" && toBookingTime != "") {
                val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val calendar1 = Calendar.getInstance()
                val calendar2 = Calendar.getInstance()
                val calendar3 = Calendar.getInstance()
                val calendar4 = Calendar.getInstance()

                val date1 =
                    dateFormat.parse(/*fragmentNursesBookingAppointmentBinding?.edtNurseFromTime?.text.toString()*/ selectedstartTime)
                val date2 =
                    dateFormat.parse(/*fragmentNursesBookingAppointmentBinding?.edtNurseToTime?.text.toString()*/ selectedendtime)
                val date3 = dateFormat.parse(nurseStartTime)
                val date4 = dateFormat.parse(nurseEndTime)

                calendar1.time = date1
                calendar2.time = date2
                calendar3.time = date3
                calendar4.time = date4

                println("Compare Result 1 : " + calendar3.compareTo(calendar1))
                println("Compare Result 2 : " + calendar4.compareTo(calendar2))
//                        println("Compare Result 3 : " + calendar3.compareTo(calendar1))
//                        println("Compare Result 4 : " + calendar2.compareTo(calendar1))

                /* Returns 0 if the times of the two Calendars are equal, -1 if the time of this Calendar is
                 before the other one, 1 if the time of this Calendar is after the other one.*/

                if (calendar3.compareTo(calendar1) == 1 || calendar4.compareTo(calendar2) == -1) {
                    Toast.makeText(
                        activity,
                        "Physiotherapist operating time is from $nurseStartTime to $nurseEndTime. Please choose in between time.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    if (taskId!! != "") {
                        CommonDialog.showDialog(this.activity!!, object :
                            DialogClickCallback {
                            override fun onDismiss() {
                            }

                            override fun onConfirm() {
                                apiHitForNurseBookingAppointment()
                            }

                        }, "Confirm Appointment", "Are you sure for this lab booking ?")
                    } else {
                        Toast.makeText(
                            activity,
                            "Please select task to continue booking.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            } else {
                Toast.makeText(
                    activity,
                    "Please select hour to continue booking.",
                    Toast.LENGTH_SHORT
                ).show()
            }


//            } else {
//                Toast.makeText(
//                    activity,
//                    "Kindly select a patient to continue booking.",
//                    Toast.LENGTH_SHORT
//                ).show()
//        }

//            (activity as HomeActivity).checkFragmentInBackStackAndOpen(
//                FragmentDoctorBookingDetails.newInstance())
        }
    }

    private fun prepareForStop() {
        TransitionManager.beginDelayedTransition(fragmentNursesBookingAppointmentBinding?.linearLayoutRecorder)
        fragmentNursesBookingAppointmentBinding?.imageViewRecord?.visibility = View.VISIBLE
        fragmentNursesBookingAppointmentBinding?.imageViewStop?.visibility = View.GONE
        fragmentNursesBookingAppointmentBinding?.linearLayoutPlay?.visibility = View.VISIBLE
    }


    private fun stopPlaying() {
        try {
            mPlayer!!.release()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        mPlayer = null
        //showing the play button
        fragmentNursesBookingAppointmentBinding?.imageViewPlay?.setImageResource(R.drawable.play)
        fragmentNursesBookingAppointmentBinding?.chronometerTimer?.stop()
    }

    private fun stopRecording() {
        try {
            mRecorder!!.stop()
            mRecorder!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mRecorder = null
        //starting the chronometer
        fragmentNursesBookingAppointmentBinding?.chronometerTimer?.stop()
        fragmentNursesBookingAppointmentBinding?.chronometerTimer?.base =
            SystemClock.elapsedRealtime()
        //showing the play button
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
        fragmentNursesBookingAppointmentBinding?.imageViewPlay?.setImageResource(R.drawable.pause)
        fragmentNursesBookingAppointmentBinding?.seekBar?.progress = lastProgress
        mPlayer?.seekTo(lastProgress)
        fragmentNursesBookingAppointmentBinding?.seekBar?.max = mPlayer?.duration!!
//        seekUpdation()
        seekUpdation()

//        fragmentBookingBinding?.chronometerTimer?.setBase(SystemClock.elapsedRealtime())
        fragmentNursesBookingAppointmentBinding?.chronometerTimer?.start()
        mPlayer?.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            fragmentNursesBookingAppointmentBinding?.imageViewPlay?.setImageResource(R.drawable.play)
            isPlaying = false
//            mPlayer!!.seekTo(mPlayer?.getDuration()!!)
//            fragmentBookingBinding?.chronometerTimer?.setBase(SystemClock.elapsedRealtime() -mPlayer?.getDuration()!!)
            lastProgress = mPlayer?.duration!!
            fragmentNursesBookingAppointmentBinding?.chronometerTimer?.stop()
            val handler = Handler()
            handler.postDelayed({
                fragmentNursesBookingAppointmentBinding?.chronometerTimer?.base =
                    SystemClock.elapsedRealtime()
                mPlayer!!.seekTo(0)
            }, 100)

        })
        fragmentNursesBookingAppointmentBinding?.seekBar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (mPlayer != null && fromUser) {
                    mPlayer?.seekTo(progress)
                    fragmentNursesBookingAppointmentBinding?.chronometerTimer?.base =
                        SystemClock.elapsedRealtime() - mPlayer?.currentPosition!!
                    lastProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private var runnable = Runnable { seekUpdation() }

    private fun seekUpdation() {
        if (mPlayer != null) {
            val mCurrentPosition = mPlayer?.currentPosition
            fragmentNursesBookingAppointmentBinding?.seekBar?.progress = mCurrentPosition!!
            fragmentNursesBookingAppointmentBinding?.chronometerTimer?.base =
                SystemClock.elapsedRealtime() - mPlayer?.currentPosition!!
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
    }

    private fun prepareForRecording() {
        TransitionManager.beginDelayedTransition(fragmentNursesBookingAppointmentBinding?.linearLayoutRecorder)
        fragmentNursesBookingAppointmentBinding?.imageViewRecord?.visibility = View.GONE
        fragmentNursesBookingAppointmentBinding?.imageViewStop?.visibility = View.VISIBLE
        fragmentNursesBookingAppointmentBinding?.linearLayoutPlay?.visibility = View.GONE
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        val root = activity?.externalCacheDir!!
        val file =
            File(root.absolutePath + "/VoiceRecorderSimplifiedCoding/Audios")



        fileName =
            root.absolutePath + "/VoiceRecorderSimplifiedCoding/Audios/" + (System.currentTimeMillis()
                .toString() + ".mp4")
        Log.d("filename", fileName!!)
        recordingFile = File(fileName)
        Log.d("recording file", recordingFile!!.name)
        if (!file.exists()) {
            file.mkdirs()
        }
        recordingFile!!.name
        mRecorder?.setOutputFile(fileName)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try {
            mRecorder?.prepare()
            mRecorder?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        lastProgress = 0
        fragmentNursesBookingAppointmentBinding?.seekBar?.progress = 0
        stopPlaying()
        // making the imageview a stop button
//starting the chronometer
        fragmentNursesBookingAppointmentBinding?.chronometerTimer?.base =
            SystemClock.elapsedRealtime()
        fragmentNursesBookingAppointmentBinding?.chronometerTimer?.start()
    }

    //End of voice recording
// Set up recycler view for service listing if available
    private fun setUpFromTimeListingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentNursesBookingAppointmentBinding!!.recyclerViewRootscareFromTimeRecyclerview != null)
        val recyclerView =
            fragmentNursesBookingAppointmentBinding!!.recyclerViewRootscareFromTimeRecyclerview
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterFromTimeRecyclerview(context!!)
        recyclerView.adapter = contactListAdapter


    }
    fun apiHitGetBookedSlots() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            var getSlotRequest = GetSlotRequest()
            getSlotRequest.serviceid = nurseId.toInt()
            getSlotRequest.appointmentdate = slotsDate
            getSlotRequest.service_type = "physiotherapy"


            fragmentNursesBookingAppointmentViewModel?.getBookedSlots(getSlotRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun apiHitForNurseTask() {

        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val nurseHourlySlotRequest = PhysiotherapyTask()
//            nurseHourlySlotRequest.hospital_id = "114"
            nurseHourlySlotRequest.physiotherapyId = nurseId.toInt().toString()
            fragmentNursesBookingAppointmentViewModel?.apiNurseTask(nurseHourlySlotRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Set up recycler view for service listing if available
    private fun populateRecyclerView(labList: ArrayList<ResultItem?>?) {
        labArr.clear()
        taskId = ""
        task = StringBuilder()
        labList?.let { labArr.addAll(it) }
//        assert(fragmentNursesBookingAppointmentBinding!!.recyclerViewLabTest1Type != null)
        val recyclerView = fragmentNursesBookingAppointmentBinding!!.recyclerViewLabTest1Type
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        publication_list1 = ArrayList()
        // nationalityDropdownlist?.add("Slots")
        contactListAdapter = AdapterPhysiotherapistTask(
            labArr,
            activity,
            this@FragmentPhysiotherapyBookingAppointment
        )
        recyclerView.adapter = contactListAdapter
        //Search
        fragmentNursesBookingAppointmentBinding!!.etSearch.addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                contactListAdapter.filter?.filter(s.toString())
            }
        })
    }

    private fun addTags() {
        fragmentNursesBookingAppointmentBinding!!.tagLayout.removeAllViews()
        val temLabArr = labArr.filter { it?.isCheck == true } as MutableList

        fragmentNursesBookingAppointmentBinding!!.viewSearch.visibility = if (temLabArr.size == 0) {
            View.GONE
        } else {
            View.VISIBLE
        }
        for (i in temLabArr.indices) {
            val tagView: View = layoutInflater.inflate(R.layout.tag_layout_item, null, false)
            val tvTagName = tagView.findViewById<TextView>(R.id.tv_tagName)
            val tvTagRemove = tagView.findViewById<ImageView>(R.id.im_remove)
            tvTagName.text = "" + temLabArr[i]?.name
            tvTagName.tag = temLabArr[i]?.id
            tvTagRemove.setOnClickListener {
                labArr.filter { it?.id == tvTagName.tag }.map { it?.isCheck = false }
                fragmentNursesBookingAppointmentBinding!!.tagLayout.removeView(tagView)
                addTags()
                contactListAdapter.notifyDataSetChanged()
            }
            fragmentNursesBookingAppointmentBinding!!.tagLayout.addView(tagView)
        }
    }

    // Set up recycler view for service listing if available
    private fun setUpHourlyTimeListingRecyclerview(hourlyList: ArrayList<com.rootscare.data.model.api.response.nurses.nursehourlyslot.ResultItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentNursesBookingAppointmentBinding!!.recyclerViewRootscareHourlyTimeRecyclerview != null)
        val recyclerView =
            fragmentNursesBookingAppointmentBinding!!.recyclerViewRootscareHourlyTimeRecyclerview
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterNurseHourlySlotRecycllerview(hourlyList, context!!)
        recyclerView.adapter = contactListAdapter

        contactListAdapter.recyclerViewItemClickWithView = object : OnHourlyItemClick {
            override fun onConfirm(modelItem: com.rootscare.data.model.api.response.nurses.nursehourlyslot.ResultItem) {
                fragmentNursesBookingAppointmentBinding?.edtNurseFromTime?.text = ""
                fragmentNursesBookingAppointmentBinding?.edtNurseToTime?.text = ""
                nurseHourlyprice = modelItem.price!!
                val index: Int = modelItem.duration?.indexOf(" ")!!
                val firstString: String = modelItem.duration.substring(0, index)
                hourlyDuration = firstString.toInt()
                //  fragmentNursesBookingAppointmentBinding?.txtHourlyPrice?.setText("SR"+" "+modelItem?.price)
            }
        }

    }

    private fun setUpAddPatientListingRecyclerview(patientFamilyMemberList: ArrayList<com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.ResultItem?>?) {
        assert(fragmentNursesBookingAppointmentBinding!!.recyclerViewRootscareAddPatientList != null)
        val recyclerView =
            fragmentNursesBookingAppointmentBinding!!.recyclerViewRootscareAddPatientList
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(true)
        val contactListAdapter =
            AdapterForNursesAddPatientListRecyclerview(patientFamilyMemberList, context!!)
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewOnAddPatientListClick = object :
            OnPatientFamilyMemberListener {
            override fun onItemClick(resultItem: com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.ResultItem) {
                familyMemberId = resultItem.id!!
            }


            override fun onEditButtonClick(modelOfGetAddPatientList: com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.ResultItem) {
                val id = modelOfGetAddPatientList.id
                val imagename = modelOfGetAddPatientList.image
                val firstname = modelOfGetAddPatientList.firstName
                val lastname = modelOfGetAddPatientList.lastName
//                var email = modelOfGetAddPatientList.email
//                var phoneno = modelOfGetAddPatientList.phoneNumber
                val age = modelOfGetAddPatientList.age
                val gender = modelOfGetAddPatientList.gender

                (activity as HomeActivity).checkInBackstack(
                    FragmentEditPatientFamilyMember.newInstance(
                        nurseId, id!!, imagename!!,
                        firstname!!, lastname!!, age!!, gender!!
                    )
                )


//                CommonDialog.showDialog(context!!, object :
//                    DialogClickCallback {
//                    override fun onDismiss() {
//                    }
//
//                    override fun onConfirm() {
////                        email!!, phoneno!!,
//
//                    }
//
//                }, "Edit Member", "Are you sure to edit this family member?")
            }

            override fun onDeleteButtonClick(id: String) {

                CommonDialog.showDialog(context!!, object :
                    DialogClickCallback {
                    override fun onDismiss() {
                    }

                    override fun onConfirm() {
                        if (isNetworkConnected) {
                            baseActivity?.showLoading()
//                            patientProfileRequest?.userId = "11"
                            val deletePatientFamilyMemberRequest =
                                DeletePatientFamilyMemberRequest()
                            deletePatientFamilyMemberRequest.id = id
//                            getPatientFamilyMemberRequest?.userId = "11"
                            fragmentNursesBookingAppointmentViewModel?.apiDeletePatientFamilyMember(
                                deletePatientFamilyMemberRequest
                            )

                        } else {
                            Toast.makeText(
                                activity,
                                "Please check your network connection.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                }, "Delete Member", "Are you sure to delete this family member?")
            }


        }

    }

    override fun successGetPatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?) {
        baseActivity?.hideLoading()
        if (getPatientFamilyListResponse?.code.equals("200")) {
            if (getPatientFamilyListResponse?.result != null && getPatientFamilyListResponse.result.size > 0) {
                fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareAddPatientList?.visibility =
                    View.VISIBLE
                setUpAddPatientListingRecyclerview(getPatientFamilyListResponse.result)
            }
        } else {
            fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareAddPatientList?.visibility =
                View.GONE
            fragmentNursesBookingAppointmentBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentNursesBookingAppointmentBinding?.tvNoDate?.text =
                getPatientFamilyListResponse?.message
//            Toast.makeText(activity, getPatientFamilyListResponse?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun successDeletePatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?) {
        baseActivity?.hideLoading()
        apiHitForFamilyMemberList()
    }

    override fun successNurseViewTimingsResponse(nurseViewTimingsResponse: NueseViewTimingsResponse?) {
        baseActivity?.hideLoading()
        if (nurseViewTimingsResponse?.code.equals("200")) {

            if (nurseViewTimingsResponse?.result != null && nurseViewTimingsResponse.result.size > 0) {
                fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
                    View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.visibility = View.GONE
                setNurseViewTimingListing(nurseViewTimingsResponse.result)
            } else {
                fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
                    View.GONE
                fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.visibility = View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.text =
                    "No timings found."
            }

        } else {
            fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
                View.GONE
            fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.visibility = View.VISIBLE
            fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.text = "No timings found."
        }
    }

    override fun successGetNurseHourlySlotResponse(getNurseHourlySlotResponse: GetNurseHourlySlotResponse?) {
        baseActivity?.hideLoading()
        if (getNurseHourlySlotResponse?.code.equals("200")) {
            println("----------------------------------------------")
            if (getNurseHourlySlotResponse?.result != null && getNurseHourlySlotResponse.result.size > 0) {
                println("----------------------------------------------")
                fragmentNursesBookingAppointmentBinding?.llHourlyTime?.visibility = View.GONE
                fragmentNursesBookingAppointmentBinding?.llHourlyTimeNew?.visibility = View.GONE
                fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                    View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.tvNoDateHourlytime?.visibility = View.GONE
                nurseHourlyprice = getNurseHourlySlotResponse.result[0]?.price!!
                val index: Int = getNurseHourlySlotResponse.result[0]?.duration?.indexOf(" ")!!
                val firstString: String =
                    getNurseHourlySlotResponse.result[0]?.duration?.substring(0, index)!!
//                Toast.makeText(activity,firstString, Toast.LENGTH_SHORT).show()
                hourlyDuration = firstString.toInt()
                setUpHourlyTimeListingRecyclerview(getNurseHourlySlotResponse.result)
            } else {
                fragmentNursesBookingAppointmentBinding?.llHourlyTime?.visibility = View.GONE
                fragmentNursesBookingAppointmentBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                    View.GONE
                fragmentNursesBookingAppointmentBinding?.tvNoDateHourlytime?.visibility =
                    View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.tvNoDateHourlytime?.text =
                    "No hourly slot found."
            }

        } else {
            fragmentNursesBookingAppointmentBinding?.llHourlyTime?.visibility = View.GONE
            fragmentNursesBookingAppointmentBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
            fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
                View.GONE
            fragmentNursesBookingAppointmentBinding?.tvNoDateHourlytime?.visibility = View.VISIBLE
            fragmentNursesBookingAppointmentBinding?.tvNoDateHourlytime?.text =
                "No hourly slot found."
        }
    }
    override fun successGetBookedSlots(getSlotsResponse: GetSlotsResponse) {
        baseActivity?.hideLoading()
        if (getSlotsResponse?.code.equals("200")) {
            if (getSlotsResponse?.result != null && getSlotsResponse.result.size > 0) {
                Log.e("res slopts", "--" + getSlotsResponse.result)
                bookedslotsList?.clear()
                for (i in 0 until getSlotsResponse.result.size) {
                    bookedslotsList?.add(getSlotsResponse.result.get(i).fromTime.toUpperCase())
                }


                Log.e("bookedslotsList", "--" + bookedslotsList)
            /*    if ((nurseDetailsResponseData?.result?.startTime != null && nurseDetailsResponseData?.result?.startTime != "") &&
                    (nurseDetailsResponseData?.result?.endTime != null && nurseDetailsResponseData?.result?.endTime != "")
                ) {
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.GONE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.VISIBLE
                    getSlots(slotsDate,slotsDate,nurseDetailsResponseData?.result?.startTime!!,nurseDetailsResponseData?.result?.endTime!!,1800000,30)
                }else{
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.GONE
                }*/


            }

        }
    }

    override fun successNurseDetailsResponse(nurseDetailsResponse: NurseDetailsResponse?) {
        baseActivity?.hideLoading()
        if (nurseDetailsResponse?.code.equals("200")) {
            Toast.makeText(activity, nurseDetailsResponse?.message, Toast.LENGTH_SHORT).show()
            if (nurseDetailsResponse?.result != null) {

                nurseDetailsResponseData = nurseDetailsResponse
                nurseFirstName =
                    if (nurseDetailsResponse.result.firstName != null && nurseDetailsResponse.result.firstName != ""
                    ) {
                        nurseDetailsResponse.result.firstName
                    } else {
                        ""
                    }
                nurseLastName =
                    if (nurseDetailsResponse.result.lastName != null && nurseDetailsResponse.result.lastName != ""
                    ) {
                        nurseDetailsResponse.result.lastName
                    } else {
                        ""
                    }
                nurseStartTime =
                    if (nurseDetailsResponse.result.startTime != null && nurseDetailsResponse.result.startTime != ""
                    ) {
                        nurseDetailsResponse.result.startTime
                    } else {
                        ""
                    }
                nurseEndTime =
                    if (nurseDetailsResponse.result.endTime != null && nurseDetailsResponse.result.endTime != ""
                    ) {
                        nurseDetailsResponse.result.endTime
                    } else {
                        ""
                    }
                fragmentNursesBookingAppointmentBinding?.txtNursedetailsName?.text =
                    "$nurseFirstName $nurseLastName"
                fragmentNursesBookingAppointmentBinding?.txtNursedetailsQualification?.text =
                    nurseDetailsResponse.result.address

                fragmentNursesBookingAppointmentBinding?.cardPrice?.visibility = View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.rlSlotPrice?.visibility = View.VISIBLE
//                dailyrate = nurseDetailsResponse.result.dailyRate!!
//                nurseVisitPrice = dailyrate
                // fragmentNursesBookingAppointmentBinding?.txtSlotPrice?.setText("SR"+" "+nurseDetailsResponse?.result?.dailyRate)
//                fragmentNursesBookingAppointmentBinding?.txt
                if (nurseDetailsResponse.result.image != null && nurseDetailsResponse.result.image != ""
                ) {

                    Glide.with(this.activity!!)
                        .load(BaseMediaUrls.USERIMAGE.url + (nurseDetailsResponse.result.image))
                        .into(fragmentNursesBookingAppointmentBinding?.imgNursedetailsProfile!!)
                } else {
                    Glide.with(this.activity!!)
                        .load(R.drawable.no_image)
                        .into(fragmentNursesBookingAppointmentBinding?.imgNursedetailsProfile!!)
                }

              /*  if ((nurseDetailsResponse.result.startTime != null && nurseDetailsResponse.result.startTime != "") &&
                    (nurseDetailsResponse.result.endTime != null && nurseDetailsResponse.result.endTime != "")
                ) {
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.GONE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.VISIBLE
                    getSlots(slotsDate,slotsDate,nurseDetailsResponse.result.startTime,nurseDetailsResponse.result.endTime!!,1800000,30)
                }else{
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.GONE
                }
*/



                apiHitGetStartTimeEndTime()
            }

        } else {
            Toast.makeText(activity, nurseDetailsResponse?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun successNurseTaskResponse(nurseDetailsResponse: GetTaskResponse?) {
        if (nurseDetailsResponse?.code.equals("200")) {

            if (nurseDetailsResponse?.result != null && nurseDetailsResponse.result.size > 0) {
                fragmentNursesBookingAppointmentBinding?.llHourlyTime?.visibility = View.GONE
                fragmentNursesBookingAppointmentBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
                    View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.visibility = View.GONE
                fragmentNursesBookingAppointmentBinding?.btnNurseBookNow?.visibility = View.VISIBLE
                populateRecyclerView(nurseDetailsResponse.result)
            } else {
                fragmentNursesBookingAppointmentBinding?.btnNurseBookNow?.visibility = View.GONE
                fragmentNursesBookingAppointmentBinding?.llHourlyTime?.visibility = View.GONE
                fragmentNursesBookingAppointmentBinding?.llHourlyTimeNew?.visibility = View.GONE
                fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
                    View.GONE
                fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.visibility = View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.text =
                    "No tasks found."
            }

        } else {
            fragmentNursesBookingAppointmentBinding?.btnNurseBookNow?.visibility = View.GONE
            fragmentNursesBookingAppointmentBinding?.llHourlyTime?.visibility = View.GONE
            fragmentNursesBookingAppointmentBinding?.llHourlyTimeNew?.visibility = View.GONE
            fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
                View.GONE
            fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.visibility = View.VISIBLE
            fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.text = "No tasks found."
        }


    }

    override fun successNurseBookAppointmentResponse(nurseBookAppointmentResponse: NurseBookAppointmentResponse?) {
        baseActivity?.hideLoading()
        if (nurseBookAppointmentResponse?.code.equals("200")) {

            Toast.makeText(activity, nurseBookAppointmentResponse?.message, Toast.LENGTH_SHORT)
                .show()
            (activity as HomeActivity).checkInBackstack(FragmentBookingCart.newInstance())
        } else {
            Toast.makeText(activity, nurseBookAppointmentResponse?.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun successBookingCartResponse(bookingCartResponse: BookingCartResponse?) {
    }


    override fun errorBookingCartResponse(throwable: Throwable?) {
    }

//    fun getCartItem() {
//        if (isNetworkConnected) {
//            baseActivity?.showLoading()
////            patientProfileRequest?.userId="11"
//            val bookingCartRequest = BookingCartRequest()
//            bookingCartRequest.userId =
//                fragmentNursesBookingAppointmentViewModel?.appSharedPref?.userId
//            fragmentNursesBookingAppointmentViewModel?.apiPatientCart(bookingCartRequest)
//
//        } else {
//            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }

    //Setup recyclerview for nurse view timing recyclerview
    private fun setNurseViewTimingListing(nurseTimingList: ArrayList<com.rootscare.data.model.api.response.nurses.nurseviewtiming.ResultItem?>?) {
        assert(fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview != null)
        val recyclerView =
            fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        val contactListAdapter = AdapterNurseSlotTiimeRecyclerview(nurseTimingList, context!!)
        recyclerView?.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnNurseSlotClick {
            override fun onConfirm(modelItem: com.rootscare.data.model.api.response.nurses.nurseviewtiming.ResultItem) {
                nurseFromTime = modelItem.startTime!!
                nurseToTime = modelItem.endTime!!

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

    private fun apiHitForFamilyMemberList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val getPatientFamilyMemberRequest = GetPatientFamilyMemberRequest()
            getPatientFamilyMemberRequest.userId =
                fragmentNursesBookingAppointmentViewModel?.appSharedPref?.userId
//            getPatientFamilyMemberRequest?.userId="11"
            fragmentNursesBookingAppointmentViewModel?.apiPatientFamilyMember(
                getPatientFamilyMemberRequest
            )

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(activity)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallary() {
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
                            MediaStore.Images.Media.getBitmap(activity?.contentResolver, contentURI)
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
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                //    fragmentProfileBinding?.imgRootscareProfileImage?.setImageBitmap(thumbnail)
                saveImage(thumbnail)
                bitmapToFile(thumbnail)
                Toast.makeText(activity, "Image Saved!", Toast.LENGTH_SHORT).show()


            }
        }

    }

    private fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            activity?.externalCacheDir!!.absolutePath.toString() + IMAGE_DIRECTORY
        )

        // have the object build the directory structure, if needed.
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {

            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg")
            )
            //     File file = new File("/storage/emulated/0/Download/Corrections 6.jpg");
            f.createNewFile()
            // imageFile=f

            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                activity,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)


            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }


    // Receive the permissions request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(grantResults)

                if (isPermissionsGranted) {
                    // Do the task now
                    Toast.makeText(activity, "Permissions granted.", Toast.LENGTH_SHORT).show()
                    //  toast("Permissions granted.")
                } else {
                    Toast.makeText(activity, "Permissions denied.", Toast.LENGTH_SHORT).show()
                    // toast("Permissions denied.")
                }
                return
            }
        }

        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.size == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) { //Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(
                    activity,
                    "You must give permissions to use this app. App is exiting.",
                    Toast.LENGTH_SHORT
                ).show()
                //  finishAffinity()
            }
        }
    }

    // Method to save an bitmap to a file
    private fun bitmapToFile(bitmap: Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(activity)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        imageFile = file

        fragmentNursesBookingAppointmentBinding?.txtDoctorbookingUploadPrescriptionimage?.text =
            imageFile?.name



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

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun getPermissionToRecordAudio() { // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
// checking the build version since Context.checkSelfPermission(...) is only available
// in Marshmallow
// 2) Always check for permission (even if permission has already been granted)
// since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(
                this.activity!!,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this.activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this.activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) { // The permission is NOT already granted.
// Check if the user has been asked about this permission already and denied
// it. If so, we want to give more explanation about why the permission is needed.
// Fire off an async request to actually get the permission
// This will show the standard permission request dialog UI
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                RECORD_AUDIO_REQUEST_CODE
            )
        }
    }

    private fun apiHitForNurseViewTiming() {
        fragmentNursesBookingAppointmentBinding?.rlSlotPrice?.visibility = View.VISIBLE
        //  fragmentNursesBookingAppointmentBinding?.rlHourlyPrice?.visibility=View.GONE
        fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
            View.GONE
        fragmentNursesBookingAppointmentBinding?.tvNoDateHourlytime?.visibility = View.GONE
        fragmentNursesBookingAppointmentBinding?.llHourlyTime?.visibility = View.GONE
        fragmentNursesBookingAppointmentBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
        fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
            View.VISIBLE
        //  fragmentNursesBookingAppointmentBinding?.txtSlotPrice?.setText("SR"+" "+dailyrate)
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val nurseSlotRequest = NurseSlotRequest()
//            nurseSlotRequest?.userId="11"
            nurseSlotRequest.userId =
                fragmentNursesBookingAppointmentViewModel?.appSharedPref?.userId
            nurseSlotRequest.serviceProviderId = nurseId
            nurseSlotRequest.serviceType = "nurse"
            nurseSlotRequest.fromDate =
                fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text.toString()
            nurseSlotRequest.toDate =
                fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text?.toString()
            fragmentNursesBookingAppointmentViewModel?.taskBasedSlots(nurseSlotRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun apiHitForNurseDetails() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val nurseDetailsRequest = NurseDetailsRequest()
            nurseDetailsRequest.id = nurseId.toInt()
//            nurseDetailsRequest.userId =
//                fragmentNursesBookingAppointmentViewModel?.appSharedPref?.userId?.toInt()

            fragmentNursesBookingAppointmentViewModel?.apiNurseDetails(nurseDetailsRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //Book Nurse Api Call
    private fun apiHitForNurseBookingAppointment() {
        baseActivity?.showLoading()
        val patientId = fragmentNursesBookingAppointmentViewModel?.appSharedPref?.userId!!
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val familyMemberId =
            if (familyMemberId == "" || familyMemberId == "0") ("0".toRequestBody(
                "multipart/form-data".toMediaTypeOrNull()
            )) else (familyMemberId.toRequestBody(
                "multipart/form-data".toMediaTypeOrNull()
            ))
//        val family_member_id =
//            familymemberid.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val nurse_id = nurseId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val from_date =
            fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text?.toString()!!
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        //   val to_date = RequestBody.create(MediaType.parse("multipart/form-data"),fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text?.toString())
        val from_time =
            fromBookingTime.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val to_time = toBookingTime.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val price = total.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val symptom_text =
            fragmentNursesBookingAppointmentBinding?.edtSymptomText?.text?.toString()!!
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val task_id = taskId!!.substring(0, taskId!!.length - 1)
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appointment_type =
            "offline".toRequestBody("multipart/form-data".toMediaTypeOrNull())


        if (imageFile != null) {
            prescriptionimage =
                imageFile!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            prescriptionimagemultipartBody = MultipartBody.Part.createFormData(
                "upload_prescription",
                imageFile?.name,
                prescriptionimage!!
            )

        } else {
            prescriptionimage = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            prescriptionimagemultipartBody =
                MultipartBody.Part.createFormData("upload_prescription", "", prescriptionimage!!)
        }

        if (recordingFile != null) {
            symptomsRecordingFile =
                recordingFile!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            symptomsRecordingFilemultipartBody = MultipartBody.Part.createFormData(
                "symptom_recording",
                recordingFile?.name,
                symptomsRecordingFile!!
            )
        } else {
            symptomsRecordingFile =
                "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            symptomsRecordingFilemultipartBody =
                MultipartBody.Part.createFormData("symptom_recording", "", symptomsRecordingFile!!)
        }

        fragmentNursesBookingAppointmentViewModel?.apiBookCartNurse(
            patientId,
            familyMemberId,
            nurse_id,
            from_date,
            from_time,
            to_time,
            price,
            prescriptionimagemultipartBody,
            symptom_text,
            symptomsRecordingFilemultipartBody,
            appointment_type,
            task_id
        )

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
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mPlayer = null
        }
    }
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
    override fun onMethodCallback(position: Int?, id: Int?, price: Int?, isCheck: Boolean?) {
        println("ididid $id")
        if (isCheck == true) {
//            publication_list1?.clear()
            total += price!!
            task.append(id).append(",");

            // removing trailing ","
//            task.deleteCharAt(task.length - 1)

            println("tasktasktask $task")
        } else {
            if (id != null) {
                total -= price!!
            }
//            task.deleteAt(position!!)
//            task.delete(0, task.length - 1)
        }
//        Log.d("TAG", "onMethodCallback: " + publication_list1.toString())
//        taskId = TextUtils.join(",", task.asIterable())
        taskId = task.toString()

//        println("taskId ${task.toList()}")
//        println("result ${taskId!!.substring(0, taskId!!.length - 1)}")

        fragmentNursesBookingAppointmentBinding?.txtHourlyPrice?.text = "SAR " + dec.format(total)
        addTags()
    }



    //Setup recyclerview for nurse view timing recyclerview
    private fun setNurseHourlyAdapter( nurseTimingList: ArrayList<SlotModel> ) {
        assert(fragmentNursesBookingAppointmentBinding!!.recyclerViewSlots != null)
        val recyclerView = fragmentNursesBookingAppointmentBinding!!.recyclerViewSlots
        val gridLayoutManager = GridLayoutManager(activity, 4, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val adapterNurseBookingSlotsHourly = AdapterNurseBookingSlotsHourly( nurseTimingList,  context!!,this)
        recyclerView.adapter = adapterNurseBookingSlotsHourly
    }

//        val data: HashMap<String, Int?> = HashMap()
//        data["id"] = id
//        data["price"] = price
//
//        publication_list1?.add(data)
//
//        Log.d("TAG", "onMethodCallback: " + publication_list1.toString())
//        // Log.d("TAG", "onMethodCallback: "+store)
//        for (i in 0 until publication_list1!!.size) {
//            val current: HashMap<String, Int?> = publication_list1!![i]
//            x = if (x != "") x + "," + current["id"] else current["id"].toString()
//            labPrice =
//                if (labPrice != "") labPrice + "," + current["price"] else current["price"].toString()
//        }
//
//        Log.d("TAG", "onMethodCallback: $x")
//        Log.d("TAG", "onMethodCallback: $labPrice")
//        Log.d("TAG", "total: $total")
//    }

//    override fun onMethodCallbackUncheck(id: Int?, price: Int?) {
//        if (id != null) {
//            total -= price!!
//
//            publication_list1?.clear()
//            for (i in 0 until publication_list1!!.size) {
//                val current: HashMap<String, Int?> = publication_list1!!.removeAt(i)
//                x = if (x != "") x + "," + current["id"] else current["id"].toString()
//                labPrice =
//                    if (labPrice != "") labPrice + "," + current["price"] else current["price"].toString()
//
//            }
//            Log.d("TAG", "onMethodCallback: $x")
//            Log.d("TAG", "onMethodCallback: $labPrice")
//        }
//        Log.d("TAG", "total: $total")
//        fragmentNursesBookingAppointmentBinding?.txtHourlyPrice?.text = "SAR " + dec.format(total)
//        addTags()
//    }
override fun onClickSlot(starttime: String, endtime: String) {
    selectedstartTime = starttime
    selectedendtime= endtime


}

    fun apiHitGetStartTimeEndTime() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            var getSlotRequest = GetProviderPreferredTimeRequest()
            getSlotRequest.user_id = nurseId
            getSlotRequest.slot_date = slotsDate
            getSlotRequest.service_type = "physiotherapy"

            fragmentNursesBookingAppointmentViewModel?.getProviderPreferredTime(getSlotRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun successGetProviderPreferredTime(getSlotsResponse: GetPreferredTimeSlotResponse) {


        baseActivity?.hideLoading()

        apiHitGetBookedSlots()
        if (getSlotsResponse.code.equals("200")) {
            if (getSlotsResponse.result != null) {


                if ((getSlotsResponse.result?.startTime != null && getSlotsResponse.result?.startTime != "") &&
                    (getSlotsResponse.result?.endTime != null && getSlotsResponse.result?.endTime != "")
                ) {
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.GONE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.VISIBLE




                        getSlots(slotsDate,slotsDate,getSlotsResponse.result?.startTime!!,
                            getSlotsResponse.result?.endTime!!,1800000,30)





                }else{
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.GONE
                }

            }else{
                fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.GONE
            }

        }
    }
}