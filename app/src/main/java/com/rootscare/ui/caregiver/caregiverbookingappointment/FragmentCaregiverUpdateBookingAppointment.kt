package com.rootscare.ui.caregiver.caregiverbookingappointment

import android.Manifest
import android.app.Activity.RESULT_CANCELED
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
import android.os.*
import android.provider.MediaStore
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
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
import com.rootscare.data.model.api.request.nurse.hourlyslot.NurseHourlySlotRequest
import com.rootscare.data.model.api.request.nurse.nursedetailsrequest.NurseDetailsRequest
import com.rootscare.data.model.api.request.nurse.nurseslots.NurseSlotRequest
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.data.model.api.response.getpreferredtimeslotresponse.GetPreferredTimeSlotResponse
import com.rootscare.data.model.api.response.getslotsres.GetSlotsResponse
import com.rootscare.data.model.api.response.nurses.nursebookappointment.NurseBookAppointmentResponse
import com.rootscare.data.model.api.response.nurses.nursedetails.NurseDetailsResponse
import com.rootscare.data.model.api.response.nurses.nursehourlyslot.GetNurseHourlySlotResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.NueseViewTimingsResponse
import com.rootscare.databinding.FragmentCaregiverUpdateBookingAppointmentBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.bookingappointment.adapter.AdapterFromTimeRecyclerview
import com.rootscare.ui.bookingappointment.subfragment.editpatient.FragmentEditPatientFamilyMember
import com.rootscare.ui.bookingcart.FragmentBookingCart
import com.rootscare.ui.caregiver.patient.FragmentCareGiverAddPatient
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.ui.nurses.adapter.AdapterNurseBookingSlotsHourly
import com.rootscare.ui.nurses.nursesappointmentbookingdetails.FragmentNursesAppointmentBookingDetails
import com.rootscare.ui.nurses.nursesbookingappointment.FragmentNursesBookingAppointment
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterForNursesAddPatientListRecyclerview
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterNurseHourlySlotRecycllerview
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterNurseSlotTiimeRecyclerview
import com.rootscare.utilitycommon.BaseMediaUrls
import com.rootscare.utils.ManagePermissions
import com.rootscare.utils.ViewUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class FragmentCaregiverUpdateBookingAppointment :
    BaseFragment<FragmentCaregiverUpdateBookingAppointmentBinding, FragmentCaregiverUpdateBookingAppointmentViewModel>(),
    FragmentCaregiverUpdateBookingAppointmentNavigator, GetSlotTimingInterface {
    private var fragmentNursesBookingAppointmentBinding: FragmentCaregiverUpdateBookingAppointmentBinding? =
        null
    private var fragmentNursesBookingAppointmentViewModel: FragmentCaregiverUpdateBookingAppointmentViewModel? =
        null
    private var nurseId: String = ""
    private var familymemberid = ""

    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var fileName: String? = null
    var recordingFile: File? = null
    private var lastProgress = 0
    private val mHandler = Handler()
    private val RECORD_AUDIO_REQUEST_CODE = 123
    private var isPlaying = false

    private val GALLERY = 1
    private val CAMERA = 2
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 3
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    var imageFile: File? = null
    var monthstr: String = ""
    var dayStr: String = ""
    var nurseFirstNmae = ""
    var nurseLastName = ""
    var dailyrate = ""
    var fromBookingTime: String = ""
    var toBookingTime: String = ""
    var nurseVisitPrice = ""
    var nurseHourlyprice = ""
    var hourlyDuration = 0
    var nationalityDropdownlist: ArrayList<String?>? = null

    var prescriptionimage: RequestBody? = null
    var prescriptionimagemultipartBody: MultipartBody.Part? = null

    var symptomsRecordingFile: RequestBody? = null
    var symptomsRecordingFilemultipartBody: MultipartBody.Part? = null
    var slotsDate: String = ""
    var selectedendtime: String = ""
    var selectedstartTime: String = ""
    var nurseFromTime = ""
    var nurseToTime = ""
    var nurseDetailsResponseData: NurseDetailsResponse? = null
    var format: String? = null
    var nextFormat: String? = null
    var displayHourOfTheDay = ""
    var displaySecondHourOfTheDay = ""
    var displayMinute = ""
    var displaySecondMinute = ""
    var nextHour: Int = 0
    var nextminute: Int = 0
    var slotsList: ArrayList<SlotModel>? = ArrayList()
    var selectedYear = 0
    var selectedmonth = 0
    var selectedday = 0
    private val dec = DecimalFormat("#,###.00")


    var bookedslotsList: ArrayList<String>? = ArrayList()
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_caregiver_update_booking_appointment
    override val viewModel: FragmentCaregiverUpdateBookingAppointmentViewModel
        get() {
            fragmentNursesBookingAppointmentViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentCaregiverUpdateBookingAppointmentViewModel::class.java)
            return fragmentNursesBookingAppointmentViewModel as FragmentCaregiverUpdateBookingAppointmentViewModel
        }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
        fun newInstance(nurseid: String): FragmentCaregiverUpdateBookingAppointment {
            val args = Bundle()
            args.putString("nurseid", nurseid)
            val fragment = FragmentCaregiverUpdateBookingAppointment()
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

        val list = listOf<String>(
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
            Log.d("nurseid Id", ": $nurseId")
        }
        nationalityDropdownlist = ArrayList<String?>()
        //  nationalityDropdownlist?.add("Slots")
        nationalityDropdownlist?.add("Hourly")

        apiHitForFamilyMemberList()
        //  apiHitForNurseViewTiming()
        apiHitForNurseHourlySlot()
        apiHitForNurseDetails()
//        setUpFromTimeListingRecyclerview()
        fragmentNursesBookingAppointmentBinding?.llMain?.setOnClickListener(View.OnClickListener {
            baseActivity?.hideKeyboard()
        })
        fragmentNursesBookingAppointmentBinding?.btnAddPatient?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentCareGiverAddPatient.newInstance(nurseId)
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
            prepareforStop()
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
            val calendar = Calendar.getInstance()
            val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
            val calendarMinute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val datetime = Calendar.getInstance()
                    val calendar = Calendar.getInstance()
                    datetime[Calendar.HOUR_OF_DAY] = hourOfDay
                    datetime[Calendar.MINUTE] = minute
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val strDate =
                        sdf.parse(fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text as String)
//                    if () {
//                        println("is here")
//                    } else {
//                        println("is not here")
//                    }
                    if (System.currentTimeMillis() > strDate!!.time) {
                        if (datetime.timeInMillis >= calendar.timeInMillis) {
//                        val hour = hourOfDay % 12
                            var hourOfDay = hourOfDay
                            println("hourOfDay 123 $hourOfDay")
                            nextHour = hourOfDay + hourlyDuration
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

                            fragmentNursesBookingAppointmentBinding?.edtNurseFromTime?.text =
                                "$displayHourOfTheDay:$displayMinute $format"
                            fragmentNursesBookingAppointmentBinding?.edtNurseToTime?.text =
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
                        println("hourOfDay 245 $hourOfDay")
                        nextHour = hourOfDay + hourlyDuration
//                        println("nextHour 245 $nextHour")
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

                        fragmentNursesBookingAppointmentBinding?.edtNurseFromTime?.text =
                            "$displayHourOfTheDay:$displayMinute $format"
                        fragmentNursesBookingAppointmentBinding?.edtNurseToTime?.text =
                            "$displaySecondHourOfTheDay:$displaySecondMinute $nextFormat"
                    }
                }, calendarHour, calendarMinute, false
            )
            timePickerDialog.show()
        }


        fragmentNursesBookingAppointmentBinding?.txtDoctorbookingUploadPrescriptionimage?.setOnClickListener {
            showPictureDialog()
        }

        // Set todays date and get clinic list and doctor according to todays date
        val c = Calendar.getInstance().time
        println("Current time => $c")

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val formattedDate = df.format(c)
        apiHitGetBookedSlots()
        fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text = formattedDate
        if (!fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text?.toString()
                .equals("") && fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text != null
        ) {
            // selectDoctorSlotApiCall(fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()!!)
            //  apiHitForNurseViewTiming()
        }
        //End this section
        slotsDate = formattedDate
        fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            var month = c.get(Calendar.MONTH)
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
                    slotsDate =       "$year-$monthstr-$dayStr"
                    apiHitGetBookedSlots()


                    apiHitGetStartTimeEndTime()

                    if (!fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text?.toString()
                            .equals("") && fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text != null
                    ) {
                        if (fragmentNursesBookingAppointmentBinding?.txtSelectSlotOrHour?.text?.toString()
                                .equals("Hourly")
                        ) {
                            //  fragmentNursesBookingAppointmentBinding?.txSelectTime?.setText(R.string.select_time)
                            //  apiHitForNurseViewTiming()
                            apiHitForNurseHourlySlot()
                        }

                        //selectDoctorSlotApiCall(fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()!!)
                    }

/*
                    if ((nurseDetailsResponseData?.result?.startTime != null && nurseDetailsResponseData?.result?.startTime != "") &&
                        (nurseDetailsResponseData?.result?.endTime != null && nurseDetailsResponseData?.result?.endTime != "")
                    ) {
                        fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.GONE
                        fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.VISIBLE
                        getSlots(slotsDate,slotsDate,nurseDetailsResponseData?.result?.startTime!!,nurseDetailsResponseData?.result?.endTime!!,7200000,120)
                    }else{
                        fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.VISIBLE
                        fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.GONE
                    }*/


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
//                        /* if(text.equals("Slots")){
//                          //   fragmentNursesBookingAppointmentBinding?.txSelectTime?.setText(R.string.select_time)
//
//
//                             apiHitForNurseViewTiming()
//                         }else*/
//                        if (text.equals("Hourly")) {
//                            //  fragmentNursesBookingAppointmentBinding?.txSelectTime?.setText(R.string.select_time_sl)
//                            apiHitForNurseHourlySlot()
//                        }
//                    }
//
//                })
//        }

        fragmentNursesBookingAppointmentBinding?.btnNurseBookNow?.setOnClickListener {
//
            if (familymemberid == "") {
//                familymemberid= fragmentBookingAppointmentViewModel?.appSharedPref?.userId!!
                familymemberid = "0"
//                Toast.makeText(
//                    activity,
//                    "Please select a patient to continue booking.",
//                    Toast.LENGTH_SHORT
//                ).show()
            }

            if (fragmentNursesBookingAppointmentBinding?.txtSelectSlotOrHour?.text?.toString()
                    .equals("Slots")
            ) {
                nurseVisitPrice = dailyrate
            } else if (fragmentNursesBookingAppointmentBinding?.txtSelectSlotOrHour?.text?.toString()
                    .equals("Hourly")
            ) {
                nurseVisitPrice = nurseHourlyprice
            }

            if (fragmentNursesBookingAppointmentBinding?.txtSelectSlotOrHour?.text?.toString()
                    .equals("Slots")
            ) {
                fromBookingTime = nurseFromTime
                toBookingTime = nurseToTime
            } else if (fragmentNursesBookingAppointmentBinding?.txtSelectSlotOrHour?.text?.toString()
                    .equals("Hourly")
            ) {
                fromBookingTime = selectedstartTime
                /* fragmentNursesBookingAppointmentBinding?.edtNurseFromTime?.text?.toString()!!*/
                toBookingTime = selectedendtime
                /*  fragmentNursesBookingAppointmentBinding?.edtNurseToTime?.text?.toString()!!*/
            }
            if (nurseVisitPrice != "" && fromBookingTime != "" && toBookingTime != "") {
                CommonDialog.showDialog(this.activity!!, object : DialogClickCallback {
                    override fun onDismiss() {

                    }

                    override fun onConfirm() {
                        apiHitForNurseBookingAppointment()
                    }

                }, "Confirm Appointment", "Are you sure for this caregiver booking ?")
            } else {
                Toast.makeText(
                    activity,
                    "Please select patient & hour to continue booking.",
                    Toast.LENGTH_SHORT
                ).show()
            }

//            (activity as HomeActivity).checkFragmentInBackStackAndOpen(
//                FragmentDoctorBookingDetails.newInstance())
        }
    }

    private fun prepareforStop() {
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
        seekUpdating()

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

    private var runnable = Runnable { seekUpdating() }

    private fun seekUpdating() {
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
    // Set up recycler view for service listing if available


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
        fragmentNursesBookingAppointmentBinding?.txtHourlyPrice?.text =
            "SAR" + " " + dec.format(Integer.parseInt(hourlyList?.get(0)?.price))

        contactListAdapter.recyclerViewItemClickWithView = object : OnHourlyItemClick {
            override fun onConfirm(modelItem: com.rootscare.data.model.api.response.nurses.nursehourlyslot.ResultItem) {
                fragmentNursesBookingAppointmentBinding?.edtNurseFromTime?.text = ""
                fragmentNursesBookingAppointmentBinding?.edtNurseToTime?.text = ""
                nurseHourlyprice = modelItem.price!!
                val index: Int = modelItem.duration?.indexOf(" ")!!
                val firstString: String = modelItem.duration.substring(0, index)
                hourlyDuration = firstString.toInt()
                fragmentNursesBookingAppointmentBinding?.txtHourlyPrice?.text =
                    "SAR" + " " + modelItem.price

                apiHitGetStartTimeEndTime()

              /*  if ((nurseDetailsResponseData?.result?.startTime != null && nurseDetailsResponseData?.result?.startTime != "") &&
                    (nurseDetailsResponseData?.result?.endTime != null && nurseDetailsResponseData?.result?.endTime != "")
                ) {
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.GONE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.VISIBLE

                    if (hourlyDuration==2) {
                        getSlots(slotsDate,slotsDate,nurseDetailsResponseData?.result?.startTime!!,
                            nurseDetailsResponseData?.result?.endTime!!,7200000,120)
                    } else if (hourlyDuration==4) {
                        *//*
                        Log.e("ll","--"+nurseDetailsResponseData?.result?.startTime)*//*
                        getSlots(slotsDate,slotsDate,nurseDetailsResponseData?.result?.startTime!!,
                            nurseDetailsResponseData?.result?.endTime!!,14400000,240)
                    }else if (hourlyDuration==6) {
                        getSlots(slotsDate,slotsDate,nurseDetailsResponseData?.result?.startTime!!,
                            nurseDetailsResponseData?.result?.endTime!!,21600000,360)
                    }else if (hourlyDuration==8) {
                        getSlots(slotsDate,slotsDate,nurseDetailsResponseData?.result?.startTime!!,
                            nurseDetailsResponseData?.result?.endTime!!,28800000,480)
                    }else if (hourlyDuration==12) {
                        getSlots(slotsDate,slotsDate,nurseDetailsResponseData?.result?.startTime!!,
                            nurseDetailsResponseData?.result?.endTime!!,43200000,720)
                    }else{
                        Toast.makeText(context!!,"hourly"+hourlyDuration,Toast.LENGTH_SHORT).show()
                    }


                }else{
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.GONE
                }*/


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
                familymemberid = resultItem.id!!
            }


            override fun onEditButtonClick(modelOfGetAddPatientList: com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.ResultItem) {
                val id = modelOfGetAddPatientList.id
                val imagename = modelOfGetAddPatientList.image
                val firstname = modelOfGetAddPatientList.firstName
                val lastname = modelOfGetAddPatientList.lastName
//                var email=modelOfGetAddPatientList?.email
//                var phoneno=modelOfGetAddPatientList?.phoneNumber
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
//                          patientProfileRequest?.userId="11"
                            val deletePatientFamilyMemberRequest =
                                DeletePatientFamilyMemberRequest()
                            deletePatientFamilyMemberRequest.id = id
//                          getPatientFamilyMemberRequest?.userId="11"
                            fragmentNursesBookingAppointmentViewModel?.apideletepatientfamilymember(
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
               /* if ((nurseDetailsResponseData?.result?.startTime != null && nurseDetailsResponseData?.result?.startTime != "") &&
                    (nurseDetailsResponseData?.result?.endTime != null && nurseDetailsResponseData?.result?.endTime != "")
                ) {
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.GONE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility =
                        View.VISIBLE
                    getSlots(
                        slotsDate,
                        slotsDate,
                        nurseDetailsResponseData?.result?.startTime!!,
                        nurseDetailsResponseData?.result?.endTime!!,
                        7200000,
                        120
                    )
                } else {
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility =
                        View.GONE
                }*/

                apiHitGetStartTimeEndTime()
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

    override fun successNueseViewTimingsResponse(nueseViewTimingsResponse: NueseViewTimingsResponse?) {
        baseActivity?.hideLoading()
        if (nueseViewTimingsResponse?.code.equals("200")) {

            if (nueseViewTimingsResponse?.result != null && nueseViewTimingsResponse.result.size > 0) {
                fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
                    View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.visibility = View.GONE
                setNurseViewTimingListing(nueseViewTimingsResponse.result)
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
            if (getNurseHourlySlotResponse?.result != null && getNurseHourlySlotResponse.result.size > 0) {
                fragmentNursesBookingAppointmentBinding?.llHourlyTime?.visibility = View.GONE
                fragmentNursesBookingAppointmentBinding?.llHourlyTimeNew?.visibility = View.VISIBLE
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

    override fun successNurseDetailsResponse(nurseDetailsResponse: NurseDetailsResponse?) {
        baseActivity?.hideLoading()
        if (nurseDetailsResponse?.code.equals("200")) {
            Toast.makeText(activity, nurseDetailsResponse?.message, Toast.LENGTH_SHORT).show()
            if (nurseDetailsResponse?.result != null) {

                nurseDetailsResponseData = nurseDetailsResponse
                nurseFirstNmae =
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
                fragmentNursesBookingAppointmentBinding?.txtNursedetailsName?.text =
                    "$nurseFirstNmae $nurseLastName"
                fragmentNursesBookingAppointmentBinding?.txtNursedetailsQualification?.text =
                    nurseDetailsResponse.result.qualification

                fragmentNursesBookingAppointmentBinding?.cardPrice?.visibility = View.GONE
                fragmentNursesBookingAppointmentBinding?.rlSlotPrice?.visibility = View.VISIBLE
                dailyrate = nurseDetailsResponse.result.dailyRate!!
                nurseVisitPrice = dailyrate
                fragmentNursesBookingAppointmentBinding?.txtSlotPrice?.text =
                    "SAR" + " " + nurseDetailsResponse.result.dailyRate
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
                apiHitGetStartTimeEndTime()

          /*      if ((nurseDetailsResponseData?.result?.startTime != null && nurseDetailsResponseData?.result?.startTime != "") &&
                    (nurseDetailsResponseData?.result?.endTime != null && nurseDetailsResponseData?.result?.endTime != "")
                ) {
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.GONE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.VISIBLE
                    getSlots(slotsDate,slotsDate,nurseDetailsResponseData?.result?.startTime!!,nurseDetailsResponseData?.result?.endTime!!,7200000,120)
                }else{
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility = View.GONE
                }*/


            }

        } else {
            Toast.makeText(activity, nurseDetailsResponse?.message, Toast.LENGTH_SHORT).show()
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

    fun apiHitForFamilyMemberList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            var getPatientFamilyMemberRequest = GetPatientFamilyMemberRequest()
            getPatientFamilyMemberRequest.userId =
                fragmentNursesBookingAppointmentViewModel?.appSharedPref?.userId
//            getPatientFamilyMemberRequest?.userId="11"
            fragmentNursesBookingAppointmentViewModel?.apipatientfamilymember(
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
        if (resultCode != RESULT_CANCELED) {
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

    fun apiHitForNurseViewTiming() {
        fragmentNursesBookingAppointmentBinding?.rlSlotPrice?.visibility = View.VISIBLE
        fragmentNursesBookingAppointmentBinding?.rlHourlyPrice?.visibility = View.GONE
        fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
            View.GONE
        fragmentNursesBookingAppointmentBinding?.tvNoDateHourlytime?.visibility = View.GONE
        fragmentNursesBookingAppointmentBinding?.llHourlyTime?.visibility = View.GONE
        fragmentNursesBookingAppointmentBinding?.llHourlyTimeNew?.visibility = View.GONE
        fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
            View.VISIBLE
        fragmentNursesBookingAppointmentBinding?.txtSlotPrice?.text = "SR $dailyrate"
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val nurseSlotRequest = NurseSlotRequest()
//            nurseSlotRequest?.userId="11"
            nurseSlotRequest.userId =
                fragmentNursesBookingAppointmentViewModel?.appSharedPref?.userId
            nurseSlotRequest.serviceProviderId = nurseId
            nurseSlotRequest.serviceType = "caregiver"
            nurseSlotRequest.fromDate =
                fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text.toString()
            nurseSlotRequest.toDate =
                fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text?.toString()
            fragmentNursesBookingAppointmentViewModel?.taskbasedslots(nurseSlotRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun apiHitForNurseHourlySlot() {
        fragmentNursesBookingAppointmentBinding?.rlSlotPrice?.visibility = View.GONE
        fragmentNursesBookingAppointmentBinding?.rlHourlyPrice?.visibility = View.VISIBLE
        fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareFromTimeRecyclerview?.visibility =
            View.GONE
        fragmentNursesBookingAppointmentBinding?.tvNoDateSlottime?.visibility = View.GONE
        fragmentNursesBookingAppointmentBinding?.recyclerViewRootscareHourlyTimeRecyclerview?.visibility =
            View.VISIBLE
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val nurseHourlySlotRequest = NurseHourlySlotRequest()
            nurseHourlySlotRequest.userId = nurseId.toInt()
            fragmentNursesBookingAppointmentViewModel?.apigethourlyrates(nurseHourlySlotRequest)
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
            nurseDetailsRequest.userId =
                fragmentNursesBookingAppointmentViewModel?.appSharedPref?.userId?.toInt()

            fragmentNursesBookingAppointmentViewModel?.apicaregiverdetails(nurseDetailsRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }
    fun apiHitGetBookedSlots() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            var getSlotRequest = GetSlotRequest()
            getSlotRequest.serviceid = nurseId.toInt()
            getSlotRequest.appointmentdate = slotsDate
            getSlotRequest.service_type = "caregiver"


            fragmentNursesBookingAppointmentViewModel?.getBookedSlots(getSlotRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }
    //Book Nurse Api Call
    private fun apiHitForNurseBookingAppointment() {

        baseActivity?.showLoading()
        val patientId = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            fragmentNursesBookingAppointmentViewModel?.appSharedPref?.userId!!
        )
        val familyMemberId =
            if (familymemberid == "" || familymemberid == "0") ("0".toRequestBody(
                "multipart/form-data".toMediaTypeOrNull()
            )) else (familymemberid.toRequestBody(
                "multipart/form-data".toMediaTypeOrNull()
            ))
        val nurse_id = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), nurseId)
        val from_date = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text?.toString()!!
        )
        val to_date = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            fragmentNursesBookingAppointmentBinding?.txtDoctorBookingSelectdate?.text?.toString()!!
        )
        val from_time =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), fromBookingTime)
        val to_time = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), toBookingTime)
        val price = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), nurseVisitPrice)
        val symptom_text = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            fragmentNursesBookingAppointmentBinding?.edtSymptomText?.text?.toString()!!
        )
        val appointment_type =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "offline")


        if (imageFile != null) {
            prescriptionimage =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile!!)
            prescriptionimagemultipartBody = MultipartBody.Part.createFormData(
                "upload_prescription",
                imageFile?.name,
                prescriptionimage!!
            )

        } else {
            prescriptionimage = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
            prescriptionimagemultipartBody =
                MultipartBody.Part.createFormData("upload_prescription", "", prescriptionimage!!)
        }

        if (recordingFile != null) {
            symptomsRecordingFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), recordingFile!!)
            symptomsRecordingFilemultipartBody = MultipartBody.Part.createFormData(
                "symptom_recording",
                recordingFile?.name,
                symptomsRecordingFile!!
            )
        } else {
            symptomsRecordingFile =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "")
            symptomsRecordingFilemultipartBody =
                MultipartBody.Part.createFormData("symptom_recording", "", symptomsRecordingFile!!)
        }

        fragmentNursesBookingAppointmentViewModel?.apibookcartnurse(
            patientId,
            familyMemberId,
            nurse_id,
            from_date,
            to_date,
            from_time,
            to_time,
            price,
            prescriptionimagemultipartBody,
            symptom_text,
            symptomsRecordingFilemultipartBody,
            appointment_type
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
            } catch (e: java.lang.Exception) {
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
        FragmentNursesBookingAppointment.mySlotListTemp?.clear()


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
    fun apiHitGetStartTimeEndTime() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            var getSlotRequest = GetProviderPreferredTimeRequest()
            getSlotRequest.user_id = nurseId
            getSlotRequest.slot_date = slotsDate
            getSlotRequest.service_type = "caregiver"

            fragmentNursesBookingAppointmentViewModel?.getProviderPreferredTime(getSlotRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }
    override fun onClickSlot(starttime: String, endtime: String) {
        selectedstartTime = starttime
        selectedendtime = endtime
    }

    //Setup recyclerview for nurse view timing recyclerview
    private fun setNurseHourlyAdapter(nurseTimingList: ArrayList<SlotModel>) {
        assert(fragmentNursesBookingAppointmentBinding!!.recyclerViewSlots != null)
        val recyclerView = fragmentNursesBookingAppointmentBinding!!.recyclerViewSlots
        val gridLayoutManager = GridLayoutManager(activity, 4, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val adapterNurseBookingSlotsHourly =
            AdapterNurseBookingSlotsHourly(nurseTimingList, context!!, this)
        recyclerView.adapter = adapterNurseBookingSlotsHourly
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
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility =
                        View.VISIBLE


                        if (hourlyDuration==2) {
                            getSlots(slotsDate,slotsDate,getSlotsResponse.result?.startTime!!,
                                getSlotsResponse.result?.endTime!!,7200000,120)
                        } else if (hourlyDuration==4) {
                            /*
                            Log.e("ll","--"+nurseDetailsResponseData?.result?.startTime)*/
                            getSlots(slotsDate,slotsDate,getSlotsResponse.result?.startTime!!,
                                getSlotsResponse.result?.endTime!!,14400000,240)
                        }else if (hourlyDuration==6) {
                            getSlots(slotsDate,slotsDate,getSlotsResponse.result?.startTime!!,
                                getSlotsResponse.result?.endTime!!,21600000,360)
                        }else if (hourlyDuration==8) {
                            getSlots(slotsDate,slotsDate,getSlotsResponse.result?.startTime!!,
                                getSlotsResponse.result?.endTime!!,28800000,480)
                        }else if (hourlyDuration==12) {
                            getSlots(slotsDate,slotsDate,getSlotsResponse.result?.startTime!!,
                                getSlotsResponse.result?.endTime!!,43200000,720)
                        }else{

                            getSlots(
                                slotsDate,
                                slotsDate,
                                getSlotsResponse.result?.startTime!!,
                                getSlotsResponse.result?.endTime!!,
                                7200000,
                                120
                            )
                         }







                } else {
                    fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.VISIBLE
                    fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility =
                        View.GONE
                }

            }else{
                fragmentNursesBookingAppointmentBinding?.tvNoSlots?.visibility = View.VISIBLE
                fragmentNursesBookingAppointmentBinding?.recyclerViewSlots?.visibility =
                    View.GONE
            }
        }

    }
}