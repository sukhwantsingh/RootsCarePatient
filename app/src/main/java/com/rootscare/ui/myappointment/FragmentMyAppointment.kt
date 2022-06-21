package com.rootscare.ui.myappointment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.dialog.CommonDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.interfaces.*
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.request.cancelappointmentrequest.CancelAppointmentRequest
import com.rootscare.data.model.api.request.pushNotificationRequest.PushNotificationRequest
import com.rootscare.data.model.api.request.videoPushRequest.VideoPushRequest
import com.rootscare.data.model.api.response.appointcancelresponse.AppointmentCancelResponse
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem
import com.rootscare.data.model.api.response.appointmenthistoryresponse.Result
import com.rootscare.data.model.api.response.videoPushResponse.VideoPushResponse
import com.rootscare.databinding.FragmentUpcommingAppointmentNewBinding
import com.rootscare.twilio.VideoCallActivity
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.myupcomingappointment.adapter.*
import com.rootscare.ui.nurses.appointmentreschedule.FragmentNurseAppointmentReschedule
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.ui.recedule.doctor.FragmentDoctorAppointmentReschedule
import com.rootscare.ui.todaysappointment.subFragment.FragmentAppointmentDetailsForAll
import com.rootscare.utils.AppConstants
import java.util.*

class FragmentMyAppointment :
    BaseFragment<FragmentUpcommingAppointmentNewBinding, FragmentMyAppointmentViewModel>(),
    FragmentMyAppointmentNavigator {
    private var fragmentUpcomingAppointmentNewBinding: FragmentUpcommingAppointmentNewBinding? =
        null
    private var fragmentMyUpComingAppointmentViewModel: FragmentMyAppointmentViewModel? = null
    private var appointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    private var doctorAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null

    private var nursesAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null

    private var physiotherapyAppointmentItemArrayList: ArrayList<AppointmentItem?>? =
        null
    private var caregiverAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    private var babysitterAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    private var hospitalDoctorAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    private var pathologyAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    private var isAllVisible: Boolean? = null
    private var roomName: String? = null
    private var appointmentCancelMessage: String? = null
    private var localPosition: Int = 0
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_upcomming_appointment_new
    override val viewModel: FragmentMyAppointmentViewModel
        get() {
            fragmentMyUpComingAppointmentViewModel =
                ViewModelProviders.of(this).get(FragmentMyAppointmentViewModel::class.java)
            return fragmentMyUpComingAppointmentViewModel as FragmentMyAppointmentViewModel
        }

    companion object {
        const val VIDEO_CALL_REQUEST = 1001
        fun newInstance(): FragmentMyAppointment {
            val args = Bundle()
            val fragment = FragmentMyAppointment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentMyUpComingAppointmentViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentUpcomingAppointmentNewBinding = viewDataBinding
        //setUpAppointmentlistingRecyclerview()

        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val appointmentRequest = AppointmentRequest()
            appointmentRequest.userId =
                fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userId
//            appointmentRequest?.userId="11"

            fragmentMyUpComingAppointmentViewModel?.apiPatientAllAppointment(appointmentRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
        setUpTabLayout()
    }


    private fun setUpTabLayout() {
        val tabTitles: MutableList<String> = ArrayList()
        tabTitles.add("All")
        tabTitles.add("Doctor")
        tabTitles.add("Nurses")
        tabTitles.add("Hospital")
        tabTitles.add("Babysitter")
        tabTitles.add("Caregiver")
        tabTitles.add("Physiotherapy")
        for (i in tabTitles.indices) {
            fragmentUpcomingAppointmentNewBinding?.tablayout?.addTab(
                fragmentUpcomingAppointmentNewBinding?.tablayout?.newTab()?.setText(
                    tabTitles[i]
                )!!, i
            )
        }
        //        activityOrderBinding.tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        for (i in 0 until fragmentUpcomingAppointmentNewBinding?.tablayout?.tabCount!!) {
            val view: View =
                LayoutInflater.from(activity).inflate(R.layout.tab_item_appointment_layout, null)
            val tabItemTv = view.findViewById<TextView>(R.id.tab_item_tv)
            tabItemTv.text = tabTitles[i]
            if (i == 0) {
                tabItemTv.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.background_white
                    )
                )
                view.background = resources.getDrawable(R.drawable.tab_background_selected)
            } else {
                tabItemTv.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.background_white
                    )
                )
//                tab_item_tv.setTextColor(resources.getColor(R.color.modified_black_1))
                view.background = resources.getDrawable(R.drawable.tab_background_unselected)
            }
            fragmentUpcomingAppointmentNewBinding?.tablayout?.getTabAt(i)?.customView = view
        }
        fragmentUpcomingAppointmentNewBinding?.tablayout?.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                for (i in 0 until fragmentUpcomingAppointmentNewBinding?.tablayout?.tabCount!!) {
//                    val view: View = fragmentProfileBinding?.tablayout?.getTabAt(i)?.customView!!
                    val view: View = Objects.requireNonNull<View>(
                        fragmentUpcomingAppointmentNewBinding?.tablayout?.getTabAt(i)?.customView
                    )

                    val tabItemTv =
                        Objects.requireNonNull(view)
                            .findViewById<TextView>(R.id.tab_item_tv)
                    if (i == tab.position) {
                        tabItemTv.setTextColor(resources.getColor(R.color.background_white))
                        Objects.requireNonNull(view).background =
                            resources.getDrawable(R.drawable.tab_background_selected)
                    } else {
                        tabItemTv.setTextColor(
                            ContextCompat.getColor(
                                activity!!,
                                R.color.background_white
                            )
                        )
//                        tab_item_tv.setTextColor(resources.getColor(R.color.modified_black_1))
                        Objects.requireNonNull(view).background =
                            resources.getDrawable(R.drawable.tab_background_unselected)
                    }
                }
//                println("tab.position ${tab.position}")
                when (tab.position) {
                    0 -> {
                        isAllVisible = true
                        defaultListSetup(appointmentItemArrayList)
                        fragmentUpcomingAppointmentNewBinding?.llAllAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentUpcomingAppointmentNewBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.layoutDoctorList?.tvNoDate?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.layoutNursesList?.tvNoDateNuurses?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.rgHospital?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                            View.GONE
                    }
                    1 -> {
                        isAllVisible = false
                        defaultDoctorListSetup(doctorAppointmentItemArrayList)
                        fragmentUpcomingAppointmentNewBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llDoctorAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentUpcomingAppointmentNewBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                    }
                    2 -> {
                        isAllVisible = false
                        defaultNursesListSetup(nursesAppointmentItemArrayList)
                        fragmentUpcomingAppointmentNewBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llNursesAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentUpcomingAppointmentNewBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE

                    }
                    3 -> {
                        isAllVisible = false
                        fragmentUpcomingAppointmentNewBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.rgHospital?.visibility =
                            View.VISIBLE
                        fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.rlHospitalRecyclerview?.visibility =
                            View.VISIBLE
                        fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.rlHospitalLabRecyclerview?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.rbHospitalDoctor?.isChecked =
                            true
                        defaultHospitalDoctorListSetup(hospitalDoctorAppointmentItemArrayList)
                        fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.rgHospital?.setOnCheckedChangeListener { _, i ->
                            if (i == R.id.rb_hospital_doctor) {
                                fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.rlHospitalRecyclerview?.visibility =
                                    View.VISIBLE
                                fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.rlHospitalLabRecyclerview?.visibility =
                                    View.GONE
                                defaultHospitalDoctorListSetup(
                                    hospitalDoctorAppointmentItemArrayList
                                )
                            } else if (i == R.id.rb_hospital_lab) {
                                fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.rlHospitalRecyclerview?.visibility =
                                    View.GONE
                                fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.rlHospitalLabRecyclerview?.visibility =
                                    View.VISIBLE
                                defaultPathologyListSetup(pathologyAppointmentItemArrayList)
                            }

                        }
                        fragmentUpcomingAppointmentNewBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llHospitalAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentUpcomingAppointmentNewBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                    }
                    4 -> {
                        isAllVisible = false
                        defaultBabysitterListSetup(babysitterAppointmentItemArrayList)
                        fragmentUpcomingAppointmentNewBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llBabysitterAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentUpcomingAppointmentNewBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                    }
                    5 -> {
                        isAllVisible = false
                        defaultCaregiverListSetup(caregiverAppointmentItemArrayList)
                        fragmentUpcomingAppointmentNewBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llCaregiverAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentUpcomingAppointmentNewBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                    }
                    6 -> {
                        isAllVisible = false
                        defaultPhysiotherapyListSetup(physiotherapyAppointmentItemArrayList)
                        fragmentUpcomingAppointmentNewBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentUpcomingAppointmentNewBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        isAllVisible = true
        fragmentUpcomingAppointmentNewBinding?.llAllAppointmentList?.visibility = View.VISIBLE
        fragmentUpcomingAppointmentNewBinding?.llDoctorAppointmentList?.visibility = View.GONE
        fragmentUpcomingAppointmentNewBinding?.llNursesAppointmentList?.visibility = View.GONE
        fragmentUpcomingAppointmentNewBinding?.llHospitalAppointmentList?.visibility = View.GONE
        fragmentUpcomingAppointmentNewBinding?.llBabysitterAppointmentList?.visibility = View.GONE
        fragmentUpcomingAppointmentNewBinding?.llCaregiverAppointmentList?.visibility = View.GONE
        fragmentUpcomingAppointmentNewBinding?.llPhysitherapyAppointmentList?.visibility = View.GONE
    }

    // Set up recycler view for Doctor Appointment listing if available
    private fun setUpAllAppointmentListingRecyclerview(appointmentList: ArrayList<AppointmentItem?>?) {
        assert(fragmentUpcomingAppointmentNewBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist != null)
        val recyclerView =
            fragmentUpcomingAppointmentNewBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        appointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
        val contactListAdapter = AdapterAllAppointment(appointmentList, requireContext())
        recyclerView?.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object :
            OnUpcomingAppointmentButtonClickListener {
            override fun onCancelBtnClick(
                modelDoctorAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                CommonDialog.showDialog(context!!, object :
                    DialogClickCallback {
                    override fun onDismiss() {

                    }

                    override fun onConfirm() {
//

                        if (isNetworkConnected) {
                            baseActivity?.showLoading()
                            val cancelAppointmentRequest = CancelAppointmentRequest()
                            cancelAppointmentRequest.id = modelDoctorAppointmentItem.id
                            cancelAppointmentRequest.serviceType =
                                if (modelDoctorAppointmentItem.userType == "hospital_doctor")
                                    "doctor"
                                else modelDoctorAppointmentItem.userType!!
                            fragmentMyUpComingAppointmentViewModel?.apiCancelAppointment(
                                cancelAppointmentRequest, modelDoctorAppointmentItem.userId
                            )

                        } else {
                            Toast.makeText(
                                activity,
                                "Please check your network connection.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                }, "Cancel Appointment", "Are you sure to cancel this appointment?")


            }

            override fun onRescheduleBtnClick(
                modelDoctorAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                if ((modelDoctorAppointmentItem.userType == "doctor") || (modelDoctorAppointmentItem.userType == "hospital_doctor")) {
                    AppConstants.DoctorRescheduleClickPosition = clickPosition.toInt()
                    AppConstants.rescheduleFrom = "My Appointment"
                    (activity as HomeActivity).checkInBackstack(
                        FragmentDoctorAppointmentReschedule.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.userId!!,
                            modelDoctorAppointmentItem.patientName!!,
                            modelDoctorAppointmentItem.name!!,
                            modelDoctorAppointmentItem.appointmentDate!!,
                            modelDoctorAppointmentItem.fromTime!!,
                            modelDoctorAppointmentItem.toTime!!,
                            if (modelDoctorAppointmentItem.userType == "doctor") modelDoctorAppointmentItem.clinicName!! else modelDoctorAppointmentItem.hospitalName!!,
                            if (modelDoctorAppointmentItem.userType == "doctor") "" else modelDoctorAppointmentItem.hospitalId!!,
                            "doctor"
                        )
                    )
                } else if (modelDoctorAppointmentItem.userType == "nurse") {
                    AppConstants.NurseRescheduleClickPosition = clickPosition.toInt()
                    AppConstants.rescheduleFrom = "My Appointment"
                    (activity as HomeActivity).checkInBackstack(
                        FragmentNurseAppointmentReschedule.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.userId!!,
                            modelDoctorAppointmentItem.patientName!!,
                            modelDoctorAppointmentItem.fromTime!!,
                            modelDoctorAppointmentItem.toTime!!,
                            modelDoctorAppointmentItem.appointmentDate!!,
                            modelDoctorAppointmentItem.userType,
                            modelDoctorAppointmentItem.slotType!!
                        )
                    )
                } else if (modelDoctorAppointmentItem.userType == "pathology") {
                    AppConstants.DoctorRescheduleClickPosition = clickPosition.toInt()
                    AppConstants.rescheduleFrom = "My Appointment"
                    (activity as HomeActivity).checkInBackstack(
                        FragmentNurseAppointmentReschedule.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.hospitalId!!,
                            modelDoctorAppointmentItem.patientName!!,
                            modelDoctorAppointmentItem.fromTime!!,
                            modelDoctorAppointmentItem.toTime!!,
                            modelDoctorAppointmentItem.bookingDate!!,
                            modelDoctorAppointmentItem.userType, "",
                        )
                    )

                } else if (modelDoctorAppointmentItem.userType == "physiotherapy") {
                    AppConstants.PhysiotherapistRescheduleClickPosition = clickPosition.toInt()
                    AppConstants.rescheduleFrom = "My Appointment"
                    (activity as HomeActivity).checkInBackstack(
                        FragmentNurseAppointmentReschedule.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.userId!!,
                            modelDoctorAppointmentItem.patientName!!,
                            modelDoctorAppointmentItem.fromTime!!,
                            modelDoctorAppointmentItem.toTime!!,
                            modelDoctorAppointmentItem.appointmentDate!!,
                            modelDoctorAppointmentItem.userType, ""
                        )
                    )
                } else if (modelDoctorAppointmentItem.userType == "caregiver") {
                    AppConstants.CaregiverRescheduleClickPosition = clickPosition.toInt()
                    AppConstants.rescheduleFrom = "My Appointment"
                    (activity as HomeActivity).checkInBackstack(
                        FragmentNurseAppointmentReschedule.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.userId!!,
                            modelDoctorAppointmentItem.patientName!!,
                            modelDoctorAppointmentItem.fromTime!!,
                            modelDoctorAppointmentItem.toTime!!,
                            modelDoctorAppointmentItem.appointmentDate!!,
                            modelDoctorAppointmentItem.userType, ""
                        )
                    )
                } else if (modelDoctorAppointmentItem.userType == "babysitter") {
                    AppConstants.BabysitterRescheduleClickPosition = clickPosition.toInt()
                    AppConstants.rescheduleFrom = "My Appointment"
                    (activity as HomeActivity).checkInBackstack(
                        FragmentNurseAppointmentReschedule.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.userId!!,
                            modelDoctorAppointmentItem.patientName!!,
                            modelDoctorAppointmentItem.fromTime!!,
                            modelDoctorAppointmentItem.toTime!!,
                            modelDoctorAppointmentItem.appointmentDate!!,
                            modelDoctorAppointmentItem.userType, ""
                        )
                    )
                }
            }

            override fun onViewDetailsClick(
                modelDoctorAppointmentItem: AppointmentItem,
                position: String
            ) {
//                println("appointmentId  ${modelDoctorAppointmentItem.id!!}")
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        modelDoctorAppointmentItem.id!!,
                        modelDoctorAppointmentItem.userId!!,
                        modelDoctorAppointmentItem.name!!,
                        modelDoctorAppointmentItem.orderId!!,
                        if (modelDoctorAppointmentItem.userType == "hospital_doctor") "doctor" else modelDoctorAppointmentItem.userType!!
//                        modelDoctorAppointmentItem.userType!!
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }

            override fun onVideoCallClick(
                modelDoctorAppointmentItem: AppointmentItem,
                position: String
            ) {
                localPosition = Integer.parseInt(position)
                if (isNetworkConnected) {
                    baseActivity?.showLoading()
                    val videoPushRequest = VideoPushRequest()
                    videoPushRequest.fromUserId =
                        fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userId
                    videoPushRequest.toUserId = modelDoctorAppointmentItem.userId
                    roomName =
                        "rootvideo_room_" + videoPushRequest.toUserId + "_" + videoPushRequest.fromUserId
                    videoPushRequest.orderId = modelDoctorAppointmentItem.orderId
                    videoPushRequest.roomName =
                        "rootvideo_room_" + videoPushRequest.toUserId + "_" + videoPushRequest.fromUserId
                    videoPushRequest.type = "patient_to_doctor_video_call"
                    videoPushRequest.fromUserName =
                        fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userName
                    videoPushRequest.toUserName = modelDoctorAppointmentItem.name
                    fragmentMyUpComingAppointmentViewModel?.apiSendVideoPushNotification(
                        videoPushRequest
                    )

                } else {
                    Toast.makeText(
                        activity,
                        "Please check your network connection.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Set up recycler view for Doctor Appointment listing if available
    private fun setUpAppointmentListingRecyclerview(doctorAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentUpcomingAppointmentNewBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist != null)
        val recyclerView =
            fragmentUpcomingAppointmentNewBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        doctorAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
        val contactListAdapter =
            AdapteMyUpComingAppointment(doctorAppointmentList, requireContext())
        recyclerView?.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object :
            OnUpcomingAppointmentButtonClickListener {
            override fun onCancelBtnClick(
                modelDoctorAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                CommonDialog.showDialog(context!!, object :
                    DialogClickCallback {
                    override fun onDismiss() {

                    }

                    override fun onConfirm() {
//

                        if (isNetworkConnected) {
                            baseActivity?.showLoading()
                            val cancelAppointmentRequest = CancelAppointmentRequest()
                            cancelAppointmentRequest.id = modelDoctorAppointmentItem.id
                            cancelAppointmentRequest.serviceType =
                                modelDoctorAppointmentItem.userType
                            fragmentMyUpComingAppointmentViewModel?.apiCancelAppointment(
                                cancelAppointmentRequest,
                                modelDoctorAppointmentItem.userId
                            )

                        } else {
                            Toast.makeText(
                                activity,
                                "Please check your network connection.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                }, "Cancel Appointment", "Are you sure to cancel this appointment?")


            }

            override fun onRescheduleBtnClick(
                modelDoctorAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                AppConstants.DoctorRescheduleClickPosition = clickPosition.toInt()
                AppConstants.rescheduleFrom = "My Appointment"
                (activity as HomeActivity).checkInBackstack(
                    FragmentDoctorAppointmentReschedule.newInstance(
                        modelDoctorAppointmentItem.id!!,
                        modelDoctorAppointmentItem.userId!!,
                        modelDoctorAppointmentItem.patientName!!,
                        modelDoctorAppointmentItem.name!!,
                        modelDoctorAppointmentItem.appointmentDate!!,
                        modelDoctorAppointmentItem.fromTime!!,
                        modelDoctorAppointmentItem.toTime!!,
                        modelDoctorAppointmentItem.clinicName!!,
                        "",
                        modelDoctorAppointmentItem.userType!!
                    )
                )
            }

            override fun onViewDetailsClick(
                modelDoctorAppointmentItem: AppointmentItem,
                position: String
            ) {
//                println("appointmentId  ${modelDoctorAppointmentItem.id!!}")
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        modelDoctorAppointmentItem.id!!,
                        modelDoctorAppointmentItem.userId!!,
                        modelDoctorAppointmentItem.name!!,
                        modelDoctorAppointmentItem.orderId!!,
                        modelDoctorAppointmentItem.userType!!
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }

            override fun onVideoCallClick(
                modelDoctorAppointmentItem: AppointmentItem,
                position: String
            ) {
                localPosition = Integer.parseInt(position)
                if (isNetworkConnected) {
                    baseActivity?.showLoading()
                    val videoPushRequest = VideoPushRequest()
                    videoPushRequest.fromUserId =
                        fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userId
                    videoPushRequest.toUserId = modelDoctorAppointmentItem.userId
                    roomName =
                        "rootvideo_room_" + videoPushRequest.toUserId + "_" + videoPushRequest.fromUserId
                    videoPushRequest.orderId = modelDoctorAppointmentItem.orderId
                    videoPushRequest.roomName =
                        "rootvideo_room_" + videoPushRequest.toUserId + "_" + videoPushRequest.fromUserId
                    videoPushRequest.type = "patient_to_doctor_video_call"
                    videoPushRequest.fromUserName =
                        fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userName
                    videoPushRequest.toUserName = modelDoctorAppointmentItem.name
                    fragmentMyUpComingAppointmentViewModel?.apiSendVideoPushNotification(
                        videoPushRequest
                    )

                } else {
                    Toast.makeText(
                        activity,
                        "Please check your network connection.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun successVideoPushResponse(videoPushResponse: VideoPushResponse) {
        baseActivity?.hideLoading()
        if (videoPushResponse.status!!) {
            val bundle = Bundle()
            bundle.putString("roomName", roomName)
            bundle.putString(
                "fromUserId",
                fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userId
            )
            bundle.putString(
                "fromUserName",
                fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userName
            )
            if (appointmentItemArrayList != null && appointmentItemArrayList!!.size > 0) {
                bundle.putString("toUserId", appointmentItemArrayList?.get(localPosition)?.userId)
                bundle.putString("toUserName", appointmentItemArrayList?.get(localPosition)?.name)
                bundle.putString("orderId", appointmentItemArrayList?.get(localPosition)?.orderId)
            } else if (doctorAppointmentItemArrayList != null && doctorAppointmentItemArrayList!!.size > 0) {
                bundle.putString(
                    "toUserId",
                    doctorAppointmentItemArrayList?.get(localPosition)?.userId
                )
                bundle.putString(
                    "toUserName",
                    doctorAppointmentItemArrayList?.get(localPosition)?.name
                )
                bundle.putString(
                    "orderId",
                    doctorAppointmentItemArrayList?.get(localPosition)?.orderId
                )
            } else if (hospitalDoctorAppointmentItemArrayList != null && hospitalDoctorAppointmentItemArrayList!!.size > 0) {
                bundle.putString(
                    "toUserId",
                    hospitalDoctorAppointmentItemArrayList?.get(localPosition)?.userId
                )
                bundle.putString(
                    "toUserName",
                    hospitalDoctorAppointmentItemArrayList?.get(localPosition)?.name
                )
                bundle.putString(
                    "orderId",
                    hospitalDoctorAppointmentItemArrayList?.get(localPosition)?.orderId
                )
            } else if (pathologyAppointmentItemArrayList != null && pathologyAppointmentItemArrayList!!.size > 0) {
                bundle.putString(
                    "toUserId",
                    pathologyAppointmentItemArrayList?.get(localPosition)?.userId
                )
                bundle.putString(
                    "toUserName",
                    pathologyAppointmentItemArrayList?.get(localPosition)?.name
                )
                bundle.putString(
                    "orderId",
                    pathologyAppointmentItemArrayList?.get(localPosition)?.orderId
                )
            }

            bundle.putBoolean("isDoctorCalling", false)
            val intent = Intent(activity, VideoCallActivity::class.java)
            intent.putExtras(bundle)
            activity?.startActivityForResult(intent, VIDEO_CALL_REQUEST)
//            activity?.finish()
        } else {
            Toast.makeText(activity, videoPushResponse.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == VIDEO_CALL_REQUEST) {
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        doctorAppointmentItemArrayList?.get(localPosition)?.id!!,
                        doctorAppointmentItemArrayList?.get(localPosition)?.userId!!,
                        doctorAppointmentItemArrayList?.get(localPosition)?.name!!,
                        doctorAppointmentItemArrayList?.get(localPosition)?.orderId!!,
                        doctorAppointmentItemArrayList?.get(localPosition)?.userType!!
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }
        }
    }

    override fun errorVideoPushResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpNursesAppointmentlistingRecyclerview(nurseAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentUpcomingAppointmentNewBinding!!.layoutNursesList.recyclerViewRootscareNursesAppointmentlist != null)
        val recyclerView =
            fragmentUpcomingAppointmentNewBinding!!.layoutNursesList.recyclerViewRootscareNursesAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        nurseAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapteNurseUpComingAppointment(nurseAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnNurseAppointmentCancel {


            override fun onCancelBtnClick(
                nurseAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                CommonDialog.showDialog(context!!, object :
                    DialogClickCallback {
                    override fun onDismiss() {
                    }

                    override fun onConfirm() {
//

                        if (isNetworkConnected) {
                            baseActivity?.showLoading()
                            val cancelAppointmentRequest = CancelAppointmentRequest()
                            cancelAppointmentRequest.id = nurseAppointmentItem.id
                            cancelAppointmentRequest.serviceType = nurseAppointmentItem.userType
                            fragmentMyUpComingAppointmentViewModel?.apiCancelAppointment(
                                cancelAppointmentRequest,
                                nurseAppointmentItem.userId
                            )

                        } else {
                            Toast.makeText(
                                activity,
                                "Please check your network connection.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                }, "Cancel Appointment", "Are you sure to cancel this appointment?")
            }

            override fun onRescheduleBtnClick(
                nurseAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                AppConstants.NurseRescheduleClickPosition = clickPosition.toInt()
                AppConstants.rescheduleFrom = "My Appointment"
                (activity as HomeActivity).checkInBackstack(
                    FragmentNurseAppointmentReschedule.newInstance(
                        nurseAppointmentItem.id!!,
                        nurseAppointmentItem.userId!!,
                        nurseAppointmentItem.patientName!!,
                        nurseAppointmentItem.fromTime!!,
                        nurseAppointmentItem.toTime!!,
                        nurseAppointmentItem.appointmentDate!!,
                        "nurse",
                        nurseAppointmentItem.slotType!!
                    )
                )
            }

            override fun onViewDetailsClick(
                nurseAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        nurseAppointmentItem.id!!, nurseAppointmentItem.userId!!,
                        nurseAppointmentItem.name!!, nurseAppointmentItem.orderId!!,
                        "nurse", nurseAppointmentItem.taskDetails
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }

        }

    }

    //    "message": "User Type must be from the list. (doctor,hospital,nurse,caregiver,babysitter,physiotherapy,lab-technician)",
// Set up recycler view for Nurses Appointment listing if available
    private fun setUpHospitalAppointmentListingRecyclerView(pathologyAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentUpcomingAppointmentNewBinding!!.layoutHospitalList.recyclerViewRootscareHospitalAppointmentlist != null)
        val recyclerView =
            fragmentUpcomingAppointmentNewBinding!!.layoutHospitalList.recyclerViewRootscareHospitalAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        pathologyAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapteHospitalUpComingAppointment(pathologyAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView =
            object : OnUpcomingAppointmentButtonClickListener {
                override fun onCancelBtnClick(
                    modelDoctorAppointmentItem: AppointmentItem,
                    clickPosition: String
                ) {
                    CommonDialog.showDialog(context!!, object :
                        DialogClickCallback {
                        override fun onDismiss() {
                        }

                        override fun onConfirm() {
                            if (isNetworkConnected) {
                                baseActivity?.showLoading()
                                val cancelAppointmentRequest = CancelAppointmentRequest()
                                cancelAppointmentRequest.id = modelDoctorAppointmentItem.id
                                cancelAppointmentRequest.serviceType =
                                    if (modelDoctorAppointmentItem.userType == "hospital_doctor") "doctor" else modelDoctorAppointmentItem.userType!!
                                fragmentMyUpComingAppointmentViewModel?.apiCancelAppointment(
                                    cancelAppointmentRequest,
                                    modelDoctorAppointmentItem.userId
                                )

                            } else {
                                Toast.makeText(
                                    activity,
                                    "Please check your network connection.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                    }, "Cancel Appointment", "Are you sure to cancel this appointment?")
                }

                override fun onRescheduleBtnClick(
                    modelDoctorAppointmentItem: AppointmentItem,
                    clickPosition: String
                ) {
                    AppConstants.DoctorRescheduleClickPosition = clickPosition.toInt()
                    AppConstants.rescheduleFrom = "My Appointment"
                    (activity as HomeActivity).checkInBackstack(
                        FragmentDoctorAppointmentReschedule.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.userId!!,
                            modelDoctorAppointmentItem.patientName!!,
                            modelDoctorAppointmentItem.name!!,
                            modelDoctorAppointmentItem.appointmentDate!!,
                            modelDoctorAppointmentItem.fromTime!!,
                            modelDoctorAppointmentItem.toTime!!,
                            modelDoctorAppointmentItem.hospitalName!!,
                            modelDoctorAppointmentItem.hospitalId!!,
                            "doctor"
                        )
                    )
                }

                override fun onViewDetailsClick(
                    modelDoctorAppointmentItem: AppointmentItem,
                    position: String
                ) {
                    val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                        FragmentAppointmentDetailsForAll.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.userId!!,
                            modelDoctorAppointmentItem.name!!,
                            modelDoctorAppointmentItem.orderId!!,
                            "doctor"
                        )
                    (activity as HomeActivity).checkInBackstack(
                        fragmentAppointmentDetailsForAll
                    )
                }

                override fun onVideoCallClick(
                    modelDoctorAppointmentItem: AppointmentItem,
                    position: String
                ) {
                    localPosition = Integer.parseInt(position)
                    if (isNetworkConnected) {
                        baseActivity?.showLoading()
                        val videoPushRequest = VideoPushRequest()
                        videoPushRequest.fromUserId =
                            fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userId
                        videoPushRequest.toUserId = modelDoctorAppointmentItem.userId
                        roomName =
                            "rootvideo_room_" + videoPushRequest.toUserId + "_" + videoPushRequest.fromUserId
                        videoPushRequest.orderId = modelDoctorAppointmentItem.orderId
                        videoPushRequest.roomName = roomName
                        videoPushRequest.type = "patient_to_doctor_video_call"
                        videoPushRequest.fromUserName =
                            fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userName
                        videoPushRequest.toUserName = modelDoctorAppointmentItem.name
                        fragmentMyUpComingAppointmentViewModel?.apiSendVideoPushNotification(
                            videoPushRequest
                        )

                    } else {
                        Toast.makeText(
                            activity,
                            "Please check your network connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpHospitalLabAppointmentListingRecyclerView(pathologyAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentUpcomingAppointmentNewBinding!!.layoutHospitalList.recyclerViewHospitalLabAppointmentList != null)
        val recyclerView =
            fragmentUpcomingAppointmentNewBinding!!.layoutHospitalList.recyclerViewHospitalLabAppointmentList
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        pathologyAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterPathologyUpComingAppointment(pathologyAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView =
            object : OnUpcomingLabAppointmentButtonClickListener {
                override fun onCancelBtnClick(
                    modelDoctorAppointmentItem: AppointmentItem,
                    clickPosition: String
                ) {
                    CommonDialog.showDialog(context!!, object :
                        DialogClickCallback {
                        override fun onDismiss() {
                        }

                        override fun onConfirm() {
                            if (isNetworkConnected) {
                                baseActivity?.showLoading()
                                val cancelAppointmentRequest = CancelAppointmentRequest()
                                cancelAppointmentRequest.id = modelDoctorAppointmentItem.id
                                cancelAppointmentRequest.serviceType =
                                    modelDoctorAppointmentItem.userType
                                fragmentMyUpComingAppointmentViewModel?.apiCancelAppointment(
                                    cancelAppointmentRequest, modelDoctorAppointmentItem.userId
                                )

                            } else {
                                Toast.makeText(
                                    activity,
                                    "Please check your network connection.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                    }, "Cancel Appointment", "Are you sure to cancel this appointment?")
                }

                override fun onRescheduleBtnClick(
                    modelDoctorAppointmentItem: AppointmentItem,
                    clickPosition: String
                ) {
                    AppConstants.DoctorRescheduleClickPosition = clickPosition.toInt()
                    AppConstants.rescheduleFrom = "My Appointment"
                    (activity as HomeActivity).checkInBackstack(
                        FragmentNurseAppointmentReschedule.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.hospitalId!!,
                            modelDoctorAppointmentItem.patientName!!,
                            modelDoctorAppointmentItem.fromTime!!,
                            modelDoctorAppointmentItem.toTime!!,
                            modelDoctorAppointmentItem.bookingDate!!,
                            "pathology", "",
                        )

//                        FragmentDoctorAppointmentReschedule.newInstance(
//                            modelDoctorAppointmentItem.id!!,
//                            modelDoctorAppointmentItem.hospitalId!!,
//                            modelDoctorAppointmentItem.patientName!!,
//                            modelDoctorAppointmentItem.doctorName!!,
//                            modelDoctorAppointmentItem.appointmentDate!!,
//                            modelDoctorAppointmentItem.fromTime!!,
//                            modelDoctorAppointmentItem.toTime!!,
//                            modelDoctorAppointmentItem.hospitalName!!
//                        )
                    )
                }

                override fun onViewDetailsClick(
                    modelDoctorAppointmentItem: AppointmentItem,
                    position: String
                ) {
                    val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                        FragmentAppointmentDetailsForAll.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.hospitalId!!,
                            modelDoctorAppointmentItem.hospitalName!!,
                            modelDoctorAppointmentItem.orderId!!,
                            "pathology"
                        )
                    (activity as HomeActivity).checkInBackstack(
                        fragmentAppointmentDetailsForAll
                    )
                }

                override fun onVideoCallClick(
                    modelDoctorAppointmentItem: AppointmentItem,
                    position: String
                ) {
                    localPosition = Integer.parseInt(position)
                    if (isNetworkConnected) {
                        baseActivity?.showLoading()
                        val videoPushRequest = VideoPushRequest()
                        videoPushRequest.fromUserId =
                            fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userId
                        videoPushRequest.toUserId = modelDoctorAppointmentItem.userId
                        roomName =
                            "rootvideo_room_" + videoPushRequest.toUserId + "_" + videoPushRequest.fromUserId
                        videoPushRequest.orderId = modelDoctorAppointmentItem.orderId
                        videoPushRequest.roomName = roomName
                        videoPushRequest.type = "patient_to_doctor_video_call"
                        videoPushRequest.fromUserName =
                            fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userName
                        videoPushRequest.toUserName = modelDoctorAppointmentItem.name
                        fragmentMyUpComingAppointmentViewModel?.apiSendVideoPushNotification(
                            videoPushRequest
                        )

                    } else {
                        Toast.makeText(
                            activity,
                            "Please check your network connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpPhysiotherapyAppointmentListingRecyclerView(physiotherapyAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentUpcomingAppointmentNewBinding!!.layoutPhysitherapyList.recyclerViewRootscarePhysitherapyAppointmentlist != null)
        val recyclerView =
            fragmentUpcomingAppointmentNewBinding!!.layoutPhysitherapyList.recyclerViewRootscarePhysitherapyAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        physiotherapyAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterPhysiotherapyUpComingAppointment(physiotherapyAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnPhysiotherapyAppointment {

            override fun onCancelBtnClick(
                physiotherapyAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                CommonDialog.showDialog(context!!, object :
                    DialogClickCallback {
                    override fun onDismiss() {
                    }

                    override fun onConfirm() {
                        if (isNetworkConnected) {
                            baseActivity?.showLoading()
                            val cancelAppointmentRequest = CancelAppointmentRequest()
                            cancelAppointmentRequest.id = physiotherapyAppointmentItem.id
                            cancelAppointmentRequest.serviceType =
                                physiotherapyAppointmentItem.userType
                            fragmentMyUpComingAppointmentViewModel?.apiCancelAppointment(
                                cancelAppointmentRequest,
                                physiotherapyAppointmentItem.userId
                            )

                        } else {
                            Toast.makeText(
                                activity,
                                "Please check your network connection.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                }, "Cancel Appointment", "Are you sure to cancel this appointment?")
            }

            override fun onRescheduleBtnClick(
                physiotherapyAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                AppConstants.PhysiotherapistRescheduleClickPosition = clickPosition.toInt()
                AppConstants.rescheduleFrom = "My Appointment"
                (activity as HomeActivity).checkInBackstack(
                    FragmentNurseAppointmentReschedule.newInstance(
                        physiotherapyAppointmentItem.id!!,
                        physiotherapyAppointmentItem.userId!!,
                        physiotherapyAppointmentItem.patientName!!,
                        physiotherapyAppointmentItem.fromTime!!,
                        physiotherapyAppointmentItem.toTime!!,
                        physiotherapyAppointmentItem.appointmentDate!!, "physiotherapy", ""
                    )
                )
            }

            override fun onViewDetailsClick(
                physiotherapyAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        physiotherapyAppointmentItem.id!!,
                        physiotherapyAppointmentItem.userId!!,
                        physiotherapyAppointmentItem.name!!,
                        physiotherapyAppointmentItem.orderId!!,
                        "physiotherapy"
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }

        }

    }

    // Set up recycler view for Caregiver Appointment listing if available
    private fun setUpCaregiverAppointmentListingRecyclerview(caregiverAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentUpcomingAppointmentNewBinding!!.layoutCaregiverList.recyclerViewRootscareCaregiverAppointmentlist != null)
        val recyclerView =
            fragmentUpcomingAppointmentNewBinding!!.layoutCaregiverList.recyclerViewRootscareCaregiverAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        caregiverAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapteCareGiverUpComingAppointment(caregiverAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnCaregiverAppointment {
            override fun onCancelBtnClick(
                caregiverAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                CommonDialog.showDialog(context!!, object :
                    DialogClickCallback {
                    override fun onDismiss() {
                    }

                    override fun onConfirm() {
//

                        if (isNetworkConnected) {
                            baseActivity?.showLoading()
                            val cancelAppointmentRequest = CancelAppointmentRequest()
                            cancelAppointmentRequest.id = caregiverAppointmentItem.id
                            cancelAppointmentRequest.serviceType =
                                caregiverAppointmentItem.userType
                            fragmentMyUpComingAppointmentViewModel?.apiCancelAppointment(
                                cancelAppointmentRequest,
                                caregiverAppointmentItem.userId
                            )

                        } else {
                            Toast.makeText(
                                activity,
                                "Please check your network connection.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                }, "Cancel Appointment", "Are you sure to cancel this appointment?")
            }

            override fun onRescheduleBtnClick(
                caregiverAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                AppConstants.CaregiverRescheduleClickPosition = clickPosition.toInt()
                AppConstants.rescheduleFrom = "My Appointment"
                (activity as HomeActivity).checkInBackstack(
                    FragmentNurseAppointmentReschedule.newInstance(
                        caregiverAppointmentItem.id!!, caregiverAppointmentItem.userId!!,
                        caregiverAppointmentItem.patientName!!,
                        caregiverAppointmentItem.fromTime!!,
                        caregiverAppointmentItem.toTime!!,
                        caregiverAppointmentItem.appointmentDate!!, "caregiver", ""
                    )
                )
            }

            override fun onViewDetailsClick(
                caregiverAppointmentItem: AppointmentItem,
                clickPosition: String
            ) {
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        caregiverAppointmentItem.id!!,
                        caregiverAppointmentItem.userId!!,
                        caregiverAppointmentItem.name!!,
                        caregiverAppointmentItem.orderId!!,
                        "caregiver"
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }
        }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpBabysitterAppointmentListingRecyclerview(babysitterAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentUpcomingAppointmentNewBinding!!.layoutBabysitterList.recyclerViewRootscareBabysitterAppointmentlist != null)
        val recyclerView =
            fragmentUpcomingAppointmentNewBinding!!.layoutBabysitterList.recyclerViewRootscareBabysitterAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        babysitterAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapteBabySitterUpComingAppointment(babysitterAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView =
            object : AdapteBabySitterUpComingAppointment.OnUpcomingAppointmentButtonClickListener {
                override fun onCancelBtnClick(
                    modelDoctorAppointmentItem: AppointmentItem,
                    clickPosition: String
                ) {
                    CommonDialog.showDialog(context!!, object :
                        DialogClickCallback {
                        override fun onDismiss() {
                        }

                        override fun onConfirm() {
                            if (isNetworkConnected) {
                                baseActivity?.showLoading()
                                val cancelAppointmentRequest = CancelAppointmentRequest()
                                cancelAppointmentRequest.id = modelDoctorAppointmentItem.id
                                cancelAppointmentRequest.serviceType =
                                    modelDoctorAppointmentItem.userType
                                fragmentMyUpComingAppointmentViewModel?.apiCancelAppointment(
                                    cancelAppointmentRequest,
                                    modelDoctorAppointmentItem.userId
                                )

                            } else {
                                Toast.makeText(
                                    activity,
                                    "Please check your network connection.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }

                    }, "Cancel Appointment", "Are you sure to cancel this appointment?")
                }

                override fun onRescheduleBtnClick(
                    modelDoctorAppointmentItem: AppointmentItem,
                    clickPosition: String
                ) {
                    AppConstants.BabysitterRescheduleClickPosition = clickPosition.toInt()
                    AppConstants.rescheduleFrom = "My Appointment"
                    (activity as HomeActivity).checkInBackstack(
                        FragmentNurseAppointmentReschedule.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.userId!!,
                            modelDoctorAppointmentItem.patientName!!,
                            modelDoctorAppointmentItem.fromTime!!,
                            modelDoctorAppointmentItem.toTime!!,
                            modelDoctorAppointmentItem.appointmentDate!!, "babysitter", ""
                        )
                    )
                }

                override fun onViewDetailsClick(
                    modelDoctorAppointmentItem: AppointmentItem,
                    position: String
                ) {
                    val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                        FragmentAppointmentDetailsForAll.newInstance(
                            modelDoctorAppointmentItem.id!!,
                            modelDoctorAppointmentItem.userId!!,
                            modelDoctorAppointmentItem.name!!,
                            modelDoctorAppointmentItem.orderId!!,
                            "babysitter"
                        )
                    (activity as HomeActivity).checkInBackstack(
                        fragmentAppointmentDetailsForAll
                    )
                }

            }

    }

    override fun successAppointmentHistoryResponse(appointmentHistoryResponse: Result?) {
        baseActivity?.hideLoading()
        if (appointmentHistoryResponse?.code.equals("200")) {
            appointmentItemArrayList = ArrayList<AppointmentItem?>()
            doctorAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            nursesAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            physiotherapyAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            caregiverAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            babysitterAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            hospitalDoctorAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            pathologyAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            if (appointmentHistoryResponse?.result?.appointment != null && appointmentHistoryResponse.result.appointment.isNotEmpty()) {
                appointmentItemArrayList =
                    appointmentHistoryResponse.result.appointment as ArrayList<AppointmentItem?>?
            }
            if (appointmentHistoryResponse?.result?.appointment != null && appointmentHistoryResponse.result.appointment.isNotEmpty()) {
                doctorAppointmentItemArrayList =
                    appointmentHistoryResponse.result.appointment.filter { it?.userType == "doctor" } as ArrayList<AppointmentItem?>?
            }
            if (appointmentHistoryResponse?.result?.appointment != null && appointmentHistoryResponse.result.appointment.isNotEmpty()) {
                nursesAppointmentItemArrayList =
                    appointmentHistoryResponse.result.appointment.filter { it?.userType == "nurse" } as ArrayList<AppointmentItem?>?
            }
            if (appointmentHistoryResponse?.result?.appointment != null && appointmentHistoryResponse.result.appointment.isNotEmpty()) {

                physiotherapyAppointmentItemArrayList =
                    appointmentHistoryResponse.result.appointment.filter { it?.userType == "physiotherapy" } as ArrayList<AppointmentItem?>?
            }

            if (appointmentHistoryResponse?.result?.appointment != null && appointmentHistoryResponse.result.appointment.isNotEmpty()) {

                caregiverAppointmentItemArrayList =
                    appointmentHistoryResponse.result.appointment.filter { it?.userType == "caregiver" } as ArrayList<AppointmentItem?>?
            }
            if (appointmentHistoryResponse?.result?.appointment != null && appointmentHistoryResponse.result.appointment.isNotEmpty()) {

                babysitterAppointmentItemArrayList =
                    appointmentHistoryResponse.result.appointment.filter { it?.userType == "babysitter" } as ArrayList<AppointmentItem?>?
            }
//
            if (appointmentHistoryResponse?.result?.appointment != null && appointmentHistoryResponse.result.appointment.isNotEmpty()) {
                hospitalDoctorAppointmentItemArrayList = ArrayList<AppointmentItem?>()
                hospitalDoctorAppointmentItemArrayList =
                    appointmentHistoryResponse.result.appointment.filter { it?.userType == "hospital_doctor" } as ArrayList<AppointmentItem?>?
            }

            if (appointmentHistoryResponse?.result?.appointment != null && appointmentHistoryResponse.result.appointment.isNotEmpty()) {
                pathologyAppointmentItemArrayList = ArrayList<AppointmentItem?>()
                pathologyAppointmentItemArrayList =
                    appointmentHistoryResponse.result.appointment.filter { it?.userType == "pathology" } as ArrayList<AppointmentItem?>?
            }
            defaultListSetup(appointmentItemArrayList)
            defaultDoctorListSetup(doctorAppointmentItemArrayList)
            defaultNursesListSetup(nursesAppointmentItemArrayList)
            defaultPhysiotherapyListSetup(physiotherapyAppointmentItemArrayList)
            defaultCaregiverListSetup(caregiverAppointmentItemArrayList)
            defaultBabysitterListSetup(babysitterAppointmentItemArrayList)
            defaultHospitalDoctorListSetup(hospitalDoctorAppointmentItemArrayList)
            defaultPathologyListSetup(pathologyAppointmentItemArrayList)

        } else {
            appointmentItemArrayList = ArrayList<AppointmentItem?>()
            doctorAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            nursesAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            physiotherapyAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            caregiverAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            babysitterAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            hospitalDoctorAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            pathologyAppointmentItemArrayList = ArrayList<AppointmentItem?>()
            defaultListSetup(appointmentItemArrayList)
            defaultDoctorListSetup(doctorAppointmentItemArrayList)
            defaultNursesListSetup(nursesAppointmentItemArrayList)
            defaultPhysiotherapyListSetup(physiotherapyAppointmentItemArrayList)
            defaultCaregiverListSetup(caregiverAppointmentItemArrayList)
            defaultBabysitterListSetup(babysitterAppointmentItemArrayList)
            defaultHospitalDoctorListSetup(hospitalDoctorAppointmentItemArrayList)
            defaultPathologyListSetup(pathologyAppointmentItemArrayList)

        }

    }

    override fun successAppointmentCancelResponse(
        appointmentCancelResponse: AppointmentCancelResponse?, userId: String?, userType: String?
    ) {
//        baseActivity?.hideLoading()
        if (appointmentCancelResponse?.code.equals("200")) {
            appointmentCancelMessage = appointmentCancelResponse?.message
            if (isNetworkConnected) {
//                baseActivity?.showLoading()
                val pushNotificationRequest = PushNotificationRequest()
                pushNotificationRequest.name =
                    fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userName
                pushNotificationRequest.userId = userId
                pushNotificationRequest.userType = "All"
                pushNotificationRequest.notificationFor = "Cancel"
                pushNotificationRequest.message =
                    "${fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userName} " +
                            "cancelled this appointment."
                fragmentMyUpComingAppointmentViewModel?.apiSendPush(pushNotificationRequest)


            } else {
                baseActivity?.hideLoading()
                Toast.makeText(
                    activity,
                    "Please check your network connection.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            baseActivity?.hideLoading()
            Toast.makeText(activity, appointmentCancelResponse?.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun successPushNotification(response: JsonObject) {
        Toast.makeText(activity, appointmentCancelMessage, Toast.LENGTH_SHORT).show()
        baseActivity?.hideLoading()
        val jsonObject: JsonObject = response
        val status = jsonObject["status"].asBoolean
        val code = jsonObject["code"].asString
        val message = jsonObject["message"].asString
        if (code == "200") {
            val appointmentRequest = AppointmentRequest()
            appointmentRequest.userId =
                fragmentMyUpComingAppointmentViewModel?.appSharedPref?.userId
            fragmentMyUpComingAppointmentViewModel?.apiPatientAllAppointment(appointmentRequest)
        }
    }

    override fun errorAppointmentHistoryResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun defaultListSetup(doctorAppointmentList: ArrayList<AppointmentItem?>?) {
        println("AppointmentList ${doctorAppointmentList?.size}")

        if (doctorAppointmentList != null && doctorAppointmentList.size > 0) {
            fragmentUpcomingAppointmentNewBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutAllList?.tvNoDate?.visibility =
                View.GONE
            setUpAllAppointmentListingRecyclerview(doctorAppointmentList)


        } else {
            fragmentUpcomingAppointmentNewBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.GONE
//            if (isAllVisible!!)
//                fragmentUpcomingAppointmentNewBinding?.layoutAllList?.tvNoDate?.visibility =
//                    View.GONE
//            else
            fragmentUpcomingAppointmentNewBinding?.layoutAllList?.tvNoDate?.visibility =
                View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutAllList?.tvNoDate?.text = "No appointment."
        }

    }

    private fun defaultDoctorListSetup(doctorAppointmentList: ArrayList<AppointmentItem?>?) {
        println("doctorAppointmentList")

        if (doctorAppointmentList != null && doctorAppointmentList.size > 0) {
            fragmentUpcomingAppointmentNewBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutDoctorList?.tvNoDate?.visibility =
                View.GONE
            setUpAppointmentListingRecyclerview(doctorAppointmentList)


        } else {
            fragmentUpcomingAppointmentNewBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentUpcomingAppointmentNewBinding?.layoutDoctorList?.tvNoDate?.visibility =
                    View.GONE
            else
                fragmentUpcomingAppointmentNewBinding?.layoutDoctorList?.tvNoDate?.visibility =
                    View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutDoctorList?.tvNoDate?.text =
                "No appointment for doctor booking."
        }

    }

    private fun defaultNursesListSetup(nurseAppointmentList: ArrayList<AppointmentItem?>?) {
        println("nurseAppointmentList")
        if (nurseAppointmentList != null && nurseAppointmentList.size > 0) {
            fragmentUpcomingAppointmentNewBinding?.layoutNursesList?.recyclerViewRootscareNursesAppointmentlist?.visibility =
                View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutNursesList?.tvNoDateNuurses?.visibility =
                View.GONE
            setUpNursesAppointmentlistingRecyclerview(nurseAppointmentList)


        } else {
            fragmentUpcomingAppointmentNewBinding?.layoutNursesList?.recyclerViewRootscareNursesAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentUpcomingAppointmentNewBinding?.layoutNursesList?.tvNoDateNuurses?.visibility =
                    View.GONE
            else
                fragmentUpcomingAppointmentNewBinding?.layoutNursesList?.tvNoDateNuurses?.visibility =
                    View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutNursesList?.tvNoDateNuurses?.text =
                "No appointment for nurse booking."
        }

    }

    private fun defaultPhysiotherapyListSetup(physiotherapyAppointmentList: ArrayList<AppointmentItem?>?) {
        println("physiotherapyAppointmentList")
        if (physiotherapyAppointmentList != null && physiotherapyAppointmentList.size > 0) {
            fragmentUpcomingAppointmentNewBinding?.layoutPhysitherapyList?.recyclerViewRootscarePhysitherapyAppointmentlist?.visibility =
                View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                View.GONE
            setUpPhysiotherapyAppointmentListingRecyclerView(physiotherapyAppointmentList)


        } else {
            fragmentUpcomingAppointmentNewBinding?.layoutPhysitherapyList?.recyclerViewRootscarePhysitherapyAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentUpcomingAppointmentNewBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                    View.GONE
            else
                fragmentUpcomingAppointmentNewBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                    View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.text =
                "No appointment for physiotherapy booking."
        }

    }

    private fun defaultCaregiverListSetup(caregiverAppointmentList: ArrayList<AppointmentItem?>?) {
        println("caregiverAppointmentList")
        if (caregiverAppointmentList != null && caregiverAppointmentList.size > 0) {
            fragmentUpcomingAppointmentNewBinding?.layoutCaregiverList?.recyclerViewRootscareCaregiverAppointmentlist?.visibility =
                View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                View.GONE
            setUpCaregiverAppointmentListingRecyclerview(caregiverAppointmentList)


        } else {
            fragmentUpcomingAppointmentNewBinding?.layoutCaregiverList?.recyclerViewRootscareCaregiverAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentUpcomingAppointmentNewBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                    View.GONE
            else
                fragmentUpcomingAppointmentNewBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                    View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutCaregiverList?.tvNoDateCaregiver?.text =
                "No appointment for caregiver booking."
        }

    }

    private fun defaultBabysitterListSetup(babysitterAppointmentList: ArrayList<AppointmentItem?>?) {
        println("babysitterAppointmentList")
        if (babysitterAppointmentList != null && babysitterAppointmentList.size > 0) {
            fragmentUpcomingAppointmentNewBinding?.layoutBabysitterList?.recyclerViewRootscareBabysitterAppointmentlist?.visibility =
                View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                View.GONE
            setUpBabysitterAppointmentListingRecyclerview(babysitterAppointmentList)


        } else {
            fragmentUpcomingAppointmentNewBinding?.layoutBabysitterList?.recyclerViewRootscareBabysitterAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentUpcomingAppointmentNewBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                    View.GONE
            else
                fragmentUpcomingAppointmentNewBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                    View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutBabysitterList?.tvNoDateBabysitter?.text =
                "No appointment for babysitter booking."
        }

    }

    private fun defaultHospitalDoctorListSetup(doctorAppointmentList: ArrayList<AppointmentItem?>?) {

        if (doctorAppointmentList != null && doctorAppointmentList.size > 0) {
            fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                View.GONE

            setUpHospitalAppointmentListingRecyclerView(doctorAppointmentList)


        } else {
            fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                    View.GONE
            else
                fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                    View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.tvNoDateHospital?.text =
                "No appointment for doctor booking."
        }

    }

    private fun defaultPathologyListSetup(pathologyAppointmentList: ArrayList<AppointmentItem?>?) {

        if (pathologyAppointmentList != null && pathologyAppointmentList.size > 0) {

            fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                View.GONE

            setUpHospitalLabAppointmentListingRecyclerView(pathologyAppointmentList)

        } else {
            fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                    View.GONE
            else
                fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                    View.VISIBLE
            fragmentUpcomingAppointmentNewBinding?.layoutHospitalList?.tvNoDateHospitalLab?.text =
                "No appointment for pathology booking."
        }

    }
}