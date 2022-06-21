package com.rootscare.ui.bookingappointment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
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
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.dialog.CommonDialog
import com.interfaces.*
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.deletepatientfamilymemberrequest.DeletePatientFamilyMemberRequest
import com.rootscare.data.model.api.request.doctorrequest.doctordetailsrequest.DoctorDetailsRequest
import com.rootscare.data.model.api.request.doctorrequest.doctorprivatesotrequest.DoctorPrivateSlotRequest
import com.rootscare.data.model.api.request.doctorrequest.getpatientfamilymemberrequest.GetPatientFamilyMemberRequest
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingresponse.DoctorPrivateBooingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorHomeSlotResponse.DoctorHomeSlotResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorHomeSlotResponse.Result
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorHomeSlotResponse.Slot
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.DoctorPrivateSlotResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.SlotItem
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.ResultItem
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordetailsresponse.DoctorDetailsResponse
import com.rootscare.databinding.FragmentBookingBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.bookingappointment.adapter.*
import com.rootscare.ui.bookingappointment.subfragment.FragmentAddPatientForDoctorBooking
import com.rootscare.ui.bookingappointment.subfragment.editpatient.FragmentEditPatientFamilyMember
import com.rootscare.ui.bookingcart.FragmentBookingCart
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.utilitycommon.BaseMediaUrls
import com.rootscare.utils.DateTimeUtils
import com.rootscare.utils.ManagePermissions
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FragmentBookingAppointment :
    BaseFragment<FragmentBookingBinding, FragmentBookingAppointmentViewModel>(),
    FragmentBookingAppointmentNavigator {
    private var fragmentBookingBinding: FragmentBookingBinding? = null
    private var fragmentBookingAppointmentViewModel: FragmentBookingAppointmentViewModel? = null
    private var monthstr: String = ""
    private var dayStr: String = ""
    private var doctorId: String = ""
    private var hospitalId: String = ""
    private val GALLERY = 1
    private val CAMERA = 2
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    var imageFile: File? = null
    private var prescriptionimage: RequestBody? = null
    private var prescriptionImageMultipartBody: MultipartBody.Part? = null

    var patientId = ""
    private var doctorFees = ""
    private var isHospital: Boolean? = null

    private var symptomsRecordingFile: RequestBody? = null
    private var symptomsRecordingFileMultipartBody: MultipartBody.Part? = null

    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var fileName: String? = null
    private var recordingFile: File? = null
    private var lastProgress = 0
    private val mHandler = Handler()
    private val RECORD_AUDIO_REQUEST_CODE = 123
    private var isPlaying = false

    private var familymemberid = ""
    private var clinicId = ""
    private var clinicFromTime = ""
    private var clinicToTime = ""
    private var selectedYear = 0
    private var selectedmonth = 0
    private var selectedday = 0
    var flag = 0
    var visitTypeDropdownList: ArrayList<String?>? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_booking
    override val viewModel: FragmentBookingAppointmentViewModel
        get() {
            fragmentBookingAppointmentViewModel =
                ViewModelProviders.of(this).get(FragmentBookingAppointmentViewModel::class.java)
            return fragmentBookingAppointmentViewModel as FragmentBookingAppointmentViewModel
        }

    companion object {
        private const val IMAGE_DIRECTORY = "/demonuts"
        fun newInstance(
            isHospital: Boolean,
            hospitalId: String,
            doctorId: String
        ): FragmentBookingAppointment {
            val args = Bundle()
            args.putString("doctorId", doctorId)
            args.putString("hospitalId", hospitalId)
            args.putBoolean("isHospital", isHospital)
            val fragment = FragmentBookingAppointment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentBookingAppointmentViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBookingBinding = viewDataBinding


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

        if (arguments != null && arguments?.getString("doctorId") != null) {
            doctorId = arguments?.getString("doctorId")!!
            hospitalId = arguments?.getString("hospitalId")!!
            isHospital = arguments?.getBoolean("isHospital")!!
            Log.d("Doctor Id", ": $doctorId")
        }

        visitTypeDropdownList = ArrayList()
        visitTypeDropdownList?.add("Clinic Visit")
        visitTypeDropdownList?.add("Home Visit")
        if (isHospital!!) {
            fragmentBookingBinding!!.txtSelectSlotOrHour.visibility = View.GONE
            fragmentBookingBinding!!.tvSelectText.visibility = View.GONE
        } else {
            fragmentBookingBinding!!.txtSelectSlotOrHour.visibility = View.VISIBLE
            fragmentBookingBinding!!.tvSelectText.visibility = View.VISIBLE

        }
        fragmentBookingBinding?.llMain?.setOnClickListener {
//            val inputMethodManager =
//                activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.hideSoftInputFromWindow(
//                activity!!.currentFocus!!.windowToken,
//                0
//            )
            baseActivity?.hideKeyboard()
        }
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val getPatientFamilyMemberRequest = GetPatientFamilyMemberRequest()
            getPatientFamilyMemberRequest.userId =
                fragmentBookingAppointmentViewModel?.appSharedPref?.userId
//            getPatientFamilyMemberRequest?.userId="11"
            fragmentBookingAppointmentViewModel?.apiPatientFamilyMember(
                getPatientFamilyMemberRequest
            )

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
//        setUpToTimeListingRecyclerview()
        //All Doctor Details Api Call
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val doctorDetailsRequest = DoctorDetailsRequest()
            doctorDetailsRequest.id = doctorId
            fragmentBookingAppointmentViewModel?.apiDoctorDetails(doctorDetailsRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
        fragmentBookingBinding?.btnAddPatient?.setOnClickListener {
//
            (activity as HomeActivity).checkInBackstack(
                FragmentAddPatientForDoctorBooking.newInstance(doctorId)
            )

        }

        fragmentBookingBinding?.btnAppointmentBooking?.setOnClickListener {
//
            if (familymemberid == "") {
//                familymemberid= fragmentBookingAppointmentViewModel?.appSharedPref?.userId!!
                familymemberid = "0"
            }

//            println("isHospital $isHospital")
//            println("clinicId $clinicId")
//            println("clinicFromTime $clinicFromTime")
//            println("clinicToTime $clinicToTime")
            if (fragmentBookingBinding?.txtSelectSlotOrHour?.text == "Clinic Visit") {
                if (isHospital!! || (!isHospital!! && clinicId != "" && clinicFromTime != "" && clinicToTime != "")) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val strDate =
                        sdf.parse(fragmentBookingBinding?.txtDoctorBookingSelectdate?.text as String)

                    val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

                    val endTime = dateFormat.parse(clinicFromTime)
                    val currentTime: Date = dateFormat.parse(dateFormat.format(Date()))

                    if (System.currentTimeMillis() > strDate!!.time) {
                        if (currentTime.before(endTime)) {
                            CommonDialog.showDialog(
                                this.requireActivity(),
                                object :
                                    DialogClickCallback {
                                    override fun onDismiss() {
                                    }

                                    override fun onConfirm() {
                                        doctorPrivateBookingApiCall()
//                                    if (clinicId != "" && clinicFromTime != "" && clinicToTime != "") {
//
//                                    } else {
//                                        Toast.makeText(
//                                            activity,
//                                            "Please select slot",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }

                                    }

                                },
                                "Confirm Appointment",
                                "Are you sure for this doctor booking ?"
                            )
                        } else {
                            Toast.makeText(this.activity, "Invalid Time", Toast.LENGTH_LONG).show()

                        }
                    } else {
                        CommonDialog.showDialog(
                            this.requireActivity(),
                            object :
                                DialogClickCallback {
                                override fun onDismiss() {
                                }

                                override fun onConfirm() {
                                    doctorPrivateBookingApiCall()
                                }

                            },
                            "Confirm Appointment",
                            "Are you sure for this doctor booking ?"
                        )
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Please select patient & slot to continue booking",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (isHospital!! || (!isHospital!! && clinicFromTime != "" && clinicToTime != "")) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val strDate =
                        sdf.parse(fragmentBookingBinding?.txtDoctorBookingSelectdate?.text as String)

                    val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

                    val endTime = dateFormat.parse(clinicFromTime)
                    val currentTime: Date = dateFormat.parse(dateFormat.format(Date()))

                    if (System.currentTimeMillis() > strDate!!.time) {
                        if (currentTime.before(endTime)) {
                            CommonDialog.showDialog(
                                this.requireActivity(),
                                object :
                                    DialogClickCallback {
                                    override fun onDismiss() {
                                    }

                                    override fun onConfirm() {
                                        doctorPrivateBookingApiCall()
//                                    if (clinicId != "" && clinicFromTime != "" && clinicToTime != "") {
//
//                                    } else {
//                                        Toast.makeText(
//                                            activity,
//                                            "Please select slot",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }

                                    }

                                },
                                "Confirm Appointment",
                                "Are you sure for this doctor booking ?"
                            )
                        } else {
                            Toast.makeText(this.activity, "Invalid Time", Toast.LENGTH_LONG).show()

                        }
                    } else {
                        CommonDialog.showDialog(
                            this.requireActivity(),
                            object :
                                DialogClickCallback {
                                override fun onDismiss() {
                                }

                                override fun onConfirm() {
                                    doctorPrivateBookingApiCall()
                                }

                            },
                            "Confirm Appointment",
                            "Are you sure for this doctor booking ?"
                        )
                    }

                } else {
                    Toast.makeText(
                        activity,
                        "Please select patient & slot to continue booking",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

//            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                FragmentDoctorBookingDetails.newInstance())
        }


        // Set todays date and get clinic list and doctor according to todays date
        val c = Calendar.getInstance().time
        println("Current time => $c")

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val tf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val formattedDate = df.format(c)
        val formattedTime = tf.format(c)
        fragmentBookingBinding?.txtDoctorBookingSelectdate?.text = formattedDate
        if (!fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()
                .equals("") && fragmentBookingBinding?.txtDoctorBookingSelectdate?.text != null
        ) {
            selectDoctorSlotApiCall(
                fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()!!,
                formattedTime
            )
        }
        //End this section

        fragmentBookingBinding?.txtDoctorBookingSelectdate?.setOnClickListener {
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


//                    fragmentBookingBinding?.txtDoctorBookingSelectdate?.text = "" + year + "-" + monthstr + "-" + dayStr
                    fragmentBookingBinding?.txtDoctorBookingSelectdate?.text =
                        "$year-$monthstr-$dayStr"

                    if (!fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()
                            .equals("") && fragmentBookingBinding?.txtDoctorBookingSelectdate?.text != null
                    ) {
                        if (fragmentBookingBinding?.txtSelectSlotOrHour?.text == "Clinic Visit") {
                            val strDate =
                                df.parse(fragmentBookingBinding?.txtDoctorBookingSelectdate?.text as String)
                            if (System.currentTimeMillis() > strDate!!.time) {
                                selectDoctorSlotApiCall(
                                    fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()!!,
                                    formattedTime
                                )
                            } else {
                                selectDoctorSlotApiCall(
                                    fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()!!,
                                    "12:00 AM"
                                )
                            }
                        } else {
//                            val inFormat = SimpleDateFormat("dd-MM-yyyy")
//                            val date =
//                                inFormat.parse(fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString())
//                            println("date ${fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()}")
//                            println("txtDoctorBookingSelectDate $date")
//                            val outFormat = SimpleDateFormat("EEEE")
//                            val goal = outFormat.format(date)
                            val date =
                                DateTimeUtils.getDayOfWeekFromDate(fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString())
                            println("date $date")
                            val doctorPrivateSlotRequest = DoctorPrivateSlotRequest()
                            doctorPrivateSlotRequest.day = date
                            doctorPrivateSlotRequest.doctorId = doctorId
                            fragmentBookingAppointmentViewModel?.apiDoctorHomeVisitSlot(
                                doctorPrivateSlotRequest
                            )
                        }
                    }
                }, year, month, day
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

        fragmentBookingBinding?.txtDoctorbookingUploadPrescriptionimage?.setOnClickListener {
            showPictureDialog()
        }


        //VOICE RECORDING SECTION
        fragmentBookingBinding?.imageViewRecord?.setOnClickListener {
            prepareForRecording()
            startRecording()
        }

        fragmentBookingBinding?.imageViewStop?.setOnClickListener {
            prepareForStop()
            stopRecording()
        }

        fragmentBookingBinding?.imageViewPlay?.setOnClickListener {
            if (!isPlaying && fileName != null) {
                isPlaying = true
                startPlaying()
            } else {
                fragmentBookingBinding?.chronometerTimer?.stop()
                isPlaying = false
                stopPlaying()
            }
        }

        fragmentBookingBinding?.txtSelectSlotOrHour?.setOnClickListener {
            CommonDialog.showDialogForDropDownList(
                this.requireActivity(),
                "Select Visit Type",
                visitTypeDropdownList,
                object :
                    DropDownDialogCallBack {
                    override fun onConfirm(text: String) {
                        fragmentBookingBinding?.txtSelectSlotOrHour?.text = text

                        if (text == "Clinic Visit") {
                            selectDoctorSlotApiCall(
                                fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()!!,
                                "12:00 AM"
                            )
//                            apiHitForNurseViewTiming()
                        } else if (text == "Home Visit") {
                            val date =
                                DateTimeUtils.getDayOfWeekFromDate(fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString())
                            println("date $date")
                            val doctorPrivateSlotRequest = DoctorPrivateSlotRequest()
                            doctorPrivateSlotRequest.day = date
                            doctorPrivateSlotRequest.doctorId = doctorId
                            fragmentBookingAppointmentViewModel?.apiDoctorHomeVisitSlot(
                                doctorPrivateSlotRequest
                            )
//                            apiCallForPrivateDoctor(doctorPrivateSlotRequest)
                        }
                    }

                })
        }

    }

    private fun prepareForStop() {
        TransitionManager.beginDelayedTransition(fragmentBookingBinding?.linearLayoutRecorder)
        fragmentBookingBinding?.imageViewRecord?.visibility = View.VISIBLE
        fragmentBookingBinding?.imageViewStop?.visibility = View.GONE
        fragmentBookingBinding?.linearLayoutPlay?.visibility = View.VISIBLE
    }


    private fun stopPlaying() {
        try {
            mPlayer!!.release()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        mPlayer = null
        //showing the play button
        fragmentBookingBinding?.imageViewPlay?.setImageResource(R.drawable.play)
        fragmentBookingBinding?.chronometerTimer?.stop()
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
        fragmentBookingBinding?.chronometerTimer?.stop()
        fragmentBookingBinding?.chronometerTimer?.base = SystemClock.elapsedRealtime()
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
        fragmentBookingBinding?.imageViewPlay?.setImageResource(R.drawable.pause)
        fragmentBookingBinding?.seekBar?.progress = lastProgress
        mPlayer?.seekTo(lastProgress)
        fragmentBookingBinding?.seekBar?.max = mPlayer?.duration!!
        seekUpdate()

//        fragmentBookingBinding?.chronometerTimer?.setBase(SystemClock.elapsedRealtime())
        fragmentBookingBinding?.chronometerTimer?.start()
        mPlayer?.setOnCompletionListener {
            fragmentBookingBinding?.imageViewPlay?.setImageResource(R.drawable.play)
            isPlaying = false
//            mPlayer!!.seekTo(mPlayer?.getDuration()!!)
//            fragmentBookingBinding?.chronometerTimer?.setBase(SystemClock.elapsedRealtime() -mPlayer?.getDuration()!!)
            lastProgress = mPlayer?.duration!!
            fragmentBookingBinding?.chronometerTimer?.stop()
            val handler = Handler()
            handler.postDelayed({
                fragmentBookingBinding?.chronometerTimer?.base = SystemClock.elapsedRealtime()
                mPlayer!!.seekTo(0)
            }, 100)

        }
        fragmentBookingBinding?.seekBar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (mPlayer != null && fromUser) {
                    mPlayer?.seekTo(progress)
                    fragmentBookingBinding?.chronometerTimer?.base =
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
            fragmentBookingBinding?.seekBar?.progress = mCurrentPosition!!
            fragmentBookingBinding?.chronometerTimer?.base =
                SystemClock.elapsedRealtime() - mPlayer?.currentPosition!!
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
    }

    private fun prepareForRecording() {
        TransitionManager.beginDelayedTransition(fragmentBookingBinding?.linearLayoutRecorder)
        fragmentBookingBinding?.imageViewRecord?.visibility = View.GONE
        fragmentBookingBinding?.imageViewStop?.visibility = View.VISIBLE
        fragmentBookingBinding?.linearLayoutPlay?.visibility = View.GONE
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
        fragmentBookingBinding?.seekBar?.progress = 0
        stopPlaying()
        // making the image view a stop button
        //starting the chronometer
        fragmentBookingBinding?.chronometerTimer?.base = SystemClock.elapsedRealtime()
        fragmentBookingBinding?.chronometerTimer?.start()
    }
    //End of voice recording

    // Set up recycler view for service listing if available
    private fun setUpAddPatientListingRecyclerview(patientfamilymemberList: ArrayList<ResultItem?>?) {
//        assert(fragmentBookingBinding!!.recyclerViewRootscareAddPatientList != null)
        val recyclerView = fragmentBookingBinding!!.recyclerViewRootscareAddPatientList
        val gridLayoutManager =
            GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.isNestedScrollingEnabled = false
//        recyclerView.setHasFixedSize(true)
        recyclerView.setHasFixedSize(true)
        val contactListAdapter =
            AdapterAddPatientListRecyclerview(patientfamilymemberList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewOnAddPatientListClick = object :
            OnPatientFamilyMemberListener {
            override fun onItemClick(resultItem: ResultItem) {
                familymemberid = resultItem.id!!
            }


            override fun onEditButtonClick(modelOfGetAddPatientList: ResultItem) {
                val id = modelOfGetAddPatientList.id
                val imageName = modelOfGetAddPatientList.image
                val firstName = modelOfGetAddPatientList.firstName
                val lastName = modelOfGetAddPatientList.lastName
//                var email=modelOfGetAddPatientList?.email
//                var phoneno=modelOfGetAddPatientList?.phoneNumber
                val age = modelOfGetAddPatientList.age
                val gender = modelOfGetAddPatientList.gender

                (activity as HomeActivity).checkInBackstack(
                    FragmentEditPatientFamilyMember.newInstance(
                        doctorId, id!!, imageName!!,
                        firstName!!, lastName!!, age!!, gender!!
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
                            fragmentBookingAppointmentViewModel?.apiDeletePatientFamilyMember(
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

    // Set up recycler view for service listing if available
    private fun setUpFromTimeListingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentBookingBinding!!.recyclerViewRootscareFromTimeRecyclerview != null)
        val recyclerView = fragmentBookingBinding!!.recyclerViewRootscareFromTimeRecyclerview
        val gridLayoutManager =
            GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterFromTimeRecyclerview(context!!)
        recyclerView.adapter = contactListAdapter


    }

    // Set up recycler view for service listing if available
    private fun setUpToTimeListingRecyclerview(slotList: ArrayList<SlotItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentBookingBinding!!.recyclerViewTimeslotby30minute != null)
        val recyclerView = fragmentBookingBinding!!.recyclerViewTimeslotby30minute
        val gridLayoutManager =
            GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterDoctorSlotDivision(slotList, requireContext())
        recyclerView.adapter = contactListAdapter

        contactListAdapter.recyclerViewItemClickWithView = object : OnDoctorSlotClickListner {
            override fun onSloctClick(position: SlotItem) {
                clinicFromTime = position.timeFrom!!
                clinicToTime = position.timeTo!!
            }
//            override fun onItemClick(modelData: ResultItem?) {
//                clinicId= modelData?.clinicId!!
//                clinicFromTime= modelData?.timeFrom!!
//                clinicToTime= modelData?.timeTo!!
//            }

        }

    }


    // Set up recycler view for service listing if available
    private fun setUpDoctorSlotListingRecyclerView(doctorPrivateList: ArrayList<com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.ResultItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentBookingBinding!!.recyclerViewDoctorslot != null)

        val recyclerView = fragmentBookingBinding!!.recyclerViewDoctorslot
        val gridLayoutManager =
            GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterDoctorSlotRecyclerview(doctorPrivateList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClick = object : OnDoctorPrivateSlotClickListner {
            override fun onItemClick(modelData: com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.ResultItem?) {
                clinicId = modelData?.clinicId!!
                if (modelData.slot != null && modelData.slot.size > 0) {
                    fragmentBookingBinding?.recyclerViewTimeslotby30minute?.visibility =
                        View.VISIBLE
                    fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.visibility =
                        View.GONE
                    for (i in 0 until modelData.slot.size) {
                        flag = if (modelData.slot[i]?.status.equals("Booked")) {
                            1
                        } else {
                            0
                        }

                    }
//                    if (flag == 1) {
////                        fragmentBookingBinding?.txtSlotHeading?.text = "No Slots Available:"
//                        Toast.makeText(
//                            activity,
//                            "No slots available for this time.",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    } else {
//                        Toast.makeText(
//                            activity,
//                            "Slots available for this time.",
//                            Toast.LENGTH_LONG
//                        ).show()
////                        fragmentBookingBinding?.txtSlotHeading?.text = "Available Slots:"
//                    }
                    setUpToTimeListingRecyclerview(modelData.slot)

                } else {
                    fragmentBookingBinding?.recyclerViewTimeslotby30minute?.visibility =
                        View.GONE
                    fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.visibility =
                        View.GONE
                    fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.text =
                        "No slot available for booking."
                }
//                clinicFromTime = modelData?.timeFrom!!
//                clinicToTime = modelData?.timeTo!!
            }

        }

    }

    override fun successGetPatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?) {
        baseActivity?.hideLoading()
        if (getPatientFamilyListResponse?.code.equals("200")) {
            if (getPatientFamilyListResponse?.result != null && getPatientFamilyListResponse.result.size > 0) {
                fragmentBookingBinding?.recyclerViewRootscareAddPatientList?.visibility =
                    View.VISIBLE
                setUpAddPatientListingRecyclerview(getPatientFamilyListResponse.result)
            }
        } else {
            fragmentBookingBinding?.recyclerViewRootscareAddPatientList?.visibility = View.GONE
            fragmentBookingBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentBookingBinding?.tvNoDate?.text = getPatientFamilyListResponse?.message
//            Toast.makeText(activity, getPatientFamilyListResponse?.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun successDoctorPrivateSlotResponse(doctorPrivateSlotResponse: DoctorPrivateSlotResponse?) {
        baseActivity?.hideLoading()
        if (doctorPrivateSlotResponse?.code.equals("200")) {
//            Toast.makeText(activity, doctorPrivateSlotResponse?.message, Toast.LENGTH_SHORT).show()
            if (doctorPrivateSlotResponse?.result != null && doctorPrivateSlotResponse.result.size > 0) {

                fragmentBookingBinding?.btnAppointmentBooking?.visibility = View.VISIBLE
                fragmentBookingBinding?.recyclerViewDoctorslot?.visibility = View.VISIBLE
                fragmentBookingBinding?.tvSelectDoctorSlotNoDate?.visibility = View.GONE
                fragmentBookingBinding?.recyclerViewTimeslotby30minute?.visibility =
                    View.VISIBLE
                fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.visibility = View.GONE
                if (!isHospital!!) {
                    clinicId = doctorPrivateSlotResponse.result[0]?.clinicId!!
                    setUpDoctorSlotListingRecyclerView(doctorPrivateSlotResponse.result)
                } else {
                    fragmentBookingBinding?.txtSlotHeading?.visibility = View.VISIBLE
                    fragmentBookingBinding?.txtSlotHeading?.text = "Available Slots:"
                    for (i in 0 until doctorPrivateSlotResponse.result[0]!!.slot!!.size) {
                        flag =
                            if (doctorPrivateSlotResponse.result[0]?.slot?.get(i)?.status.equals(
                                    "Booked"
                                )
                            ) {
                                1
                            } else {
                                0
                            }

                    }
                    setUpToTimeListingRecyclerview(doctorPrivateSlotResponse.result[0]?.slot)
                }
//                clinicFromTime = doctorPrivateSlotResponse?.result?.get(0)?.timeFrom!!
//                clinicToTime = doctorPrivateSlotResponse?.result?.get(0)?.timeTo!!


                if (doctorPrivateSlotResponse.result[0]?.slot != null && doctorPrivateSlotResponse.result[0]?.slot?.size!! > 0
                ) {
                    fragmentBookingBinding?.recyclerViewTimeslotby30minute?.visibility =
                        View.VISIBLE
                    fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.visibility =
                        View.GONE

                    for (i in 0 until doctorPrivateSlotResponse.result[0]?.slot?.size!!) {
                        flag =
                            if (doctorPrivateSlotResponse.result[0]?.slot?.get(i)?.status.equals(
                                    "Booked"
                                )
                            ) {
                                1
                            } else {
                                0
                            }

                    }
//                    if (flag == 1) {
//                        fragmentBookingBinding?.txtSlotHeading?.text = "No Slots Available:"
//                        Toast.makeText(
//                            activity,
//                            "No slots available for this clinic.",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    } else {
//                        Toast.makeText(
//                            activity,
//                            "Slots available for this clinic.",
//                            Toast.LENGTH_LONG
//                        ).show()
//                        fragmentBookingBinding?.txtSlotHeading?.text = "Available Slots:"
//                    }
                    setUpToTimeListingRecyclerview(doctorPrivateSlotResponse.result[0]?.slot)

                } else {
                    fragmentBookingBinding?.txtSlotHeading?.text = "No Slots Available:"
                    fragmentBookingBinding?.recyclerViewTimeslotby30minute?.visibility =
                        View.GONE
                    fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.visibility =
                        View.GONE
                    fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.text =
                        "No slot available for booking."
                }

            } else {
                fragmentBookingBinding?.btnAppointmentBooking?.visibility = View.GONE
                fragmentBookingBinding?.recyclerViewDoctorslot?.visibility = View.GONE
                fragmentBookingBinding?.tvSelectDoctorSlotNoDate?.visibility = View.VISIBLE
                fragmentBookingBinding?.recyclerViewTimeslotby30minute?.visibility = View.GONE
                fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.visibility = View.GONE
                fragmentBookingBinding?.tvSelectDoctorSlotNoDate?.text = "No Doctor Slot Found"
                fragmentBookingBinding?.txtSlotHeading?.visibility = View.GONE
            }
        } else {
            fragmentBookingBinding?.btnAppointmentBooking?.visibility = View.GONE
            fragmentBookingBinding?.recyclerViewDoctorslot?.visibility = View.GONE
            fragmentBookingBinding?.recyclerViewTimeslotby30minute?.visibility = View.GONE
            fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.visibility = View.GONE
            fragmentBookingBinding?.tvSelectDoctorSlotNoDate?.visibility = View.VISIBLE
            fragmentBookingBinding?.tvSelectDoctorSlotNoDate?.text = "No Doctor Slot Found"
            fragmentBookingBinding?.txtSlotHeading?.visibility = View.GONE
            Toast.makeText(activity, doctorPrivateSlotResponse?.message, Toast.LENGTH_SHORT)
                .show()
        }

    }

    override fun successDoctorHomeVisitSlotResponse(doctorHomeSlotResponse: DoctorHomeSlotResponse?) {
        baseActivity?.hideLoading()
        if (doctorHomeSlotResponse?.code.equals("200")) {
            if (doctorHomeSlotResponse?.result != null && doctorHomeSlotResponse.result.size > 0) {

                fragmentBookingBinding?.btnAppointmentBooking?.visibility = View.VISIBLE
                fragmentBookingBinding?.recyclerViewDoctorslot?.visibility = View.GONE
                fragmentBookingBinding?.tvSelectDoctorSlotNoDate?.visibility = View.GONE
                fragmentBookingBinding?.recyclerViewTimeslotby30minute?.visibility =
                    View.VISIBLE
                fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.visibility = View.GONE
                fragmentBookingBinding?.tvSelectSlot?.visibility = View.GONE
                fragmentBookingBinding?.txtSlotHeading?.visibility = View.VISIBLE
                clinicFromTime = ""
                clinicToTime = ""
                setUpHomeVisitListingRecyclerview(doctorHomeSlotResponse.result)
            } else {
                fragmentBookingBinding?.btnAppointmentBooking?.visibility = View.GONE
                fragmentBookingBinding?.recyclerViewDoctorslot?.visibility = View.GONE
                fragmentBookingBinding?.tvSelectDoctorSlotNoDate?.visibility = View.GONE
                fragmentBookingBinding?.recyclerViewTimeslotby30minute?.visibility = View.GONE
                fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.text =
                    "No Doctor Home Visit Slot Found"
                fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.visibility = View.VISIBLE
                fragmentBookingBinding?.tvSelectSlot?.visibility = View.GONE
                fragmentBookingBinding?.txtSlotHeading?.visibility = View.GONE
            }
        } else {
            fragmentBookingBinding?.btnAppointmentBooking?.visibility = View.GONE
            fragmentBookingBinding?.recyclerViewDoctorslot?.visibility = View.GONE
            fragmentBookingBinding?.tvSelectDoctorSlotNoDate?.visibility = View.GONE
            fragmentBookingBinding?.recyclerViewTimeslotby30minute?.visibility = View.GONE
            fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.visibility = View.VISIBLE
            fragmentBookingBinding?.tvSelectSlot?.visibility = View.GONE
            fragmentBookingBinding?.tvSelectTimeslotby30minuteNoDate?.text =
                "No Doctor Home Visit Slot Found"
            fragmentBookingBinding?.txtSlotHeading?.visibility = View.GONE
            Toast.makeText(activity, doctorHomeSlotResponse?.message, Toast.LENGTH_SHORT).show()
        }
    }

    // Set up recycler view for service listing if available
    private fun setUpHomeVisitListingRecyclerview(resultItem: ArrayList<Result?>?) {
//        assert(fragmentBookingBinding!!.recyclerViewTimeslotby30minute != null)
        val recyclerView = fragmentBookingBinding!!.recyclerViewTimeslotby30minute
        val gridLayoutManager =
            GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterDoctorHomeVisit(resultItem?.get(0)?.slot!!, context!!)
        recyclerView.adapter = contactListAdapter

        contactListAdapter.recyclerViewItemClickWithView =
            object : AdapterDoctorHomeVisit.OnDoctorHomeVisitSlotClickListener {
                override fun onSlotClick(position: Slot) {
                    clinicFromTime = position.timeFrom!!
                    clinicToTime = position.timeTo!!
                }

            }

    }

    override fun successDoctorDetailsResponse(doctorDetailsResponse: DoctorDetailsResponse?) {
        baseActivity?.hideLoading()
        if (doctorDetailsResponse?.code.equals("200")) {
            Toast.makeText(activity, doctorDetailsResponse?.message, Toast.LENGTH_SHORT).show()
            fragmentBookingBinding?.txtDoctorbookingDoctorname?.text =
                doctorDetailsResponse?.result?.firstName + " " + doctorDetailsResponse?.result?.lastName
            if (!doctorDetailsResponse?.result?.image.equals("") && doctorDetailsResponse?.result?.image != null) {
                Glide.with(this.requireActivity())
                    .load(BaseMediaUrls.USERIMAGE.url + (doctorDetailsResponse.result.image))
                    .into(fragmentBookingBinding?.imgDoctorbookingDoctorprofileimage!!)
            }
            if (!doctorDetailsResponse?.result?.qualification.equals("") || doctorDetailsResponse?.result?.qualification != null) {
                fragmentBookingBinding?.txtDoctorbookingdoctorbookingDoctorqualification?.text =
                    doctorDetailsResponse?.result?.qualification
            }

            if (!doctorDetailsResponse?.result?.fees.equals("") || doctorDetailsResponse?.result?.fees != null) {
                doctorFees = doctorDetailsResponse?.result?.fees!!
                fragmentBookingBinding?.txtDoctorbookingDoctorfees?.text =
                    "SAR " + " " + doctorDetailsResponse.result.fees
            }
        } else {
            Toast.makeText(activity, doctorDetailsResponse?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun successDoctorPrivateBooingResponse(doctorPrivateBooingResponse: DoctorPrivateBooingResponse?) {
        baseActivity?.hideLoading()
        if (doctorPrivateBooingResponse?.code.equals("200")) {
            Toast.makeText(activity, doctorPrivateBooingResponse?.message, Toast.LENGTH_SHORT)
                .show()
            (activity as HomeActivity).checkInBackstack(FragmentBookingCart.newInstance())


        } else {
            Toast.makeText(activity, doctorPrivateBooingResponse?.message, Toast.LENGTH_SHORT)
                .show()
        }

    }

    override fun successDeletePatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?) {
        baseActivity?.hideLoading()
        if (isNetworkConnected) {
            baseActivity?.showLoading()
//            patientProfileRequest?.userId="11"
            val getPatientFamilyMemberRequest = GetPatientFamilyMemberRequest()
            getPatientFamilyMemberRequest.userId =
                fragmentBookingAppointmentViewModel?.appSharedPref?.userId
//            getPatientFamilyMemberRequest?.userId="11"
            fragmentBookingAppointmentViewModel?.apiPatientFamilyMember(
                getPatientFamilyMemberRequest
            )

        } else {
            Toast.makeText(
                activity,
                "Please check your network connection.",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    override fun errorGetPatientFamilyListResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(HomeFragment.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectDoctorSlotApiCall(selectDate: String, selectTime: String) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val doctorPrivateSlotRequest = DoctorPrivateSlotRequest()
            doctorPrivateSlotRequest.date = selectDate
            doctorPrivateSlotRequest.doctorId = doctorId
            doctorPrivateSlotRequest.time = selectTime

            if (isHospital!!) {
                doctorPrivateSlotRequest.hospitalId = hospitalId
                fragmentBookingAppointmentViewModel?.apiHospitalDoctorSlot(
                    doctorPrivateSlotRequest
                )
            } else {
                fragmentBookingAppointmentViewModel?.apiDoctorPrivateSlot(
                    doctorPrivateSlotRequest
                )
            }

        } else {
            Toast.makeText(
                activity,
                "Please check your network connection.",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

//    private fun apiCallForPrivateDoctor(doctorPrivateSlotRequest: DoctorPrivateSlotRequest) {
//        if (fragmentBookingBinding?.txtSelectSlotOrHour?.text?.toString()
//                .equals("Clinic Visit")
//        ) {
//            fragmentBookingAppointmentViewModel?.apiDoctorPrivateSlot(
//                doctorPrivateSlotRequest
//            )
//        } else {
//            fragmentBookingAppointmentViewModel?.apiDoctorHomeVisitSlot(
//                doctorPrivateSlotRequest
//            )
//        }
//    }


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
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance().timeInMillis).toString() + ".jpg"))
            //     File file = new File("/storage/emulated/0/Download/Corrections 6.jpg");
            f.createNewFile()
            // imageFile=f
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(activity, arrayOf(f.path), arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)
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

        fragmentBookingBinding?.txtDoctorbookingUploadPrescriptionimage?.text = imageFile?.name

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun getPermissionToRecordAudio() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available in Marshmallow
        // 2) Always check for permission (even if permission has already been granted) since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(
                this.requireActivity(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this.requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this.requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) { // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied it.
            // If so, we want to give more explanation about why the permission is needed.
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                RECORD_AUDIO_REQUEST_CODE
            )
        }
    }

    private fun doctorPrivateBookingApiCall() {
        baseActivity?.showLoading()
        var hospitalId1: RequestBody? = null
        val patientId =
            fragmentBookingAppointmentViewModel?.appSharedPref?.userId?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val familyMemberId =
            if (familymemberid == "" || familymemberid == "0") ("0".toRequestBody(
                "multipart/form-data".toMediaTypeOrNull()
            )) else (familymemberid.toRequestBody(
                "multipart/form-data".toMediaTypeOrNull()
            ))
        val doctorId = doctorId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (isHospital!!) {
            hospitalId1 = hospitalId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }
        val privateClinicId =
            clinicId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appointmentDate =
            fragmentBookingBinding?.txtDoctorBookingSelectdate?.text?.toString()
                ?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val fromTime =
            clinicFromTime.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val toTime = clinicToTime.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val price = doctorFees.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val symptomText = fragmentBookingBinding?.edtSymptomText?.text?.toString()
            ?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val appointmentType =
            fragmentBookingAppointmentViewModel?.appSharedPref?.appointmentType?.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val bookingType =
            if (fragmentBookingBinding?.txtSelectSlotOrHour?.text?.toString()
                    .equals("Clinic Visit")
            ) "clinicvisit".toRequestBody("multipart/form-data".toMediaTypeOrNull()) else "homevisit".toRequestBody(
                "multipart/form-data".toMediaTypeOrNull()
            )

        if (imageFile != null) {
            prescriptionimage =
                imageFile!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            prescriptionImageMultipartBody = MultipartBody.Part.createFormData(
                "upload_prescription", imageFile?.name, prescriptionimage!!
            )

        } else {
            prescriptionimage = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            prescriptionImageMultipartBody =
                MultipartBody.Part.createFormData(
                    "upload_prescription",
                    "",
                    prescriptionimage!!
                )
        }

        if (recordingFile != null) {
            symptomsRecordingFile =
                recordingFile!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            symptomsRecordingFileMultipartBody = MultipartBody.Part.createFormData(
                "symptom_recording", recordingFile?.name, symptomsRecordingFile!!
            )
        } else {
            symptomsRecordingFile =
                "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            symptomsRecordingFileMultipartBody =
                MultipartBody.Part.createFormData(
                    "symptom_recording",
                    "",
                    symptomsRecordingFile!!
                )
        }

        if (isHospital!!) {
            fragmentBookingAppointmentViewModel?.apiBookCartHospitalDoctor(
                patientId!!, familyMemberId, doctorId, hospitalId1!!,
                appointmentDate!!, fromTime, toTime, price, prescriptionImageMultipartBody,
                symptomText!!, symptomsRecordingFileMultipartBody, appointmentType!!
            )
        } else {
            fragmentBookingAppointmentViewModel?.apiBookCartPrivateDoctor(
                patientId!!, familyMemberId, doctorId, privateClinicId, appointmentDate!!, fromTime,
                toTime, price, prescriptionImageMultipartBody, symptomText!!,
                symptomsRecordingFileMultipartBody, appointmentType!!, bookingType
            )
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
}