package com.rootscare.ui.cancellappointment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem
import com.rootscare.data.model.api.response.appointmenthistoryresponse.Result
import com.rootscare.databinding.FragmentCancelMyUpcomingAppointmentBinding
import com.rootscare.ui.appointment.subfragment.FragmentAppiontmentDetails
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.cancellappointment.adapter.*
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.profile.FragmentProfile
import java.util.*

class FragmentCancelMyUpcomingAppointment :
    BaseFragment<FragmentCancelMyUpcomingAppointmentBinding, FragmentCancelMyUpcomingAppointmentViewModel>(),
    FragmentCancelMyUpcomingAppointmentNavigator {

    private var fragmentCancelMyUpcomingAppointmentBinding: FragmentCancelMyUpcomingAppointmentBinding? =
        null
    private var fragmentCancelMyUpcomingAppointmentViewModel: FragmentCancelMyUpcomingAppointmentViewModel? =
        null

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
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_cancel_my_upcoming_appointment
    override val viewModel: FragmentCancelMyUpcomingAppointmentViewModel
        get() {
            fragmentCancelMyUpcomingAppointmentViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentCancelMyUpcomingAppointmentViewModel::class.java)
            return fragmentCancelMyUpcomingAppointmentViewModel as FragmentCancelMyUpcomingAppointmentViewModel
        }

    companion object {
        fun newInstance(): FragmentCancelMyUpcomingAppointment {
            val args = Bundle()
            val fragment = FragmentCancelMyUpcomingAppointment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentCancelMyUpcomingAppointmentViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentCancelMyUpcomingAppointmentBinding = viewDataBinding
//        setUpAppointmentListingRecyclerView()
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val appointmentRequest = AppointmentRequest()
            appointmentRequest.userId =
                fragmentCancelMyUpcomingAppointmentViewModel?.appSharedPref?.userId
//            appointmentRequest?.userId="11"

            fragmentCancelMyUpcomingAppointmentViewModel?.apiPatientUpcomingCancelAppointment(
                appointmentRequest
            )

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
        setUpTabLayout()
    }


    private fun setUpTabLayout() {
        val tabTitles: MutableList<String> =
            ArrayList()
        tabTitles.add("All")
        tabTitles.add("Doctor")
        tabTitles.add("Nurses")
        tabTitles.add("Hospital")
        tabTitles.add("Babysitter")
        tabTitles.add("Caregiver")
        tabTitles.add("Physiotherapy")
        for (i in tabTitles.indices) {
            fragmentCancelMyUpcomingAppointmentBinding?.tablayout?.addTab(
                fragmentCancelMyUpcomingAppointmentBinding?.tablayout?.newTab()?.setText(
                    tabTitles[i]
                )!!, i
            )
        }
        //        activityOrderBinding.tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        for (i in 0 until fragmentCancelMyUpcomingAppointmentBinding?.tablayout?.tabCount!!) {
            val view: View =
                LayoutInflater.from(activity).inflate(R.layout.tab_item_appointment_layout, null)
            val tab_item_tv = view.findViewById<TextView>(R.id.tab_item_tv)
            tab_item_tv.text = tabTitles[i]
            if (i == 0) {
                tab_item_tv.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.background_white
                    )
                )
                view.background = resources.getDrawable(R.drawable.tab_background_selected)
            } else {
                tab_item_tv.setTextColor(
                    ContextCompat.getColor(
                        requireActivity(),
                        R.color.background_white
                    )
                )
//                tab_item_tv.setTextColor(resources.getColor(R.color.modified_black_1))
                view.background = resources.getDrawable(R.drawable.tab_background_unselected)
            }
            fragmentCancelMyUpcomingAppointmentBinding?.tablayout?.getTabAt(i)?.customView = view
        }
        fragmentCancelMyUpcomingAppointmentBinding?.tablayout?.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                for (i in 0 until fragmentCancelMyUpcomingAppointmentBinding?.tablayout?.tabCount!!) {
//                    val view: View = fragmentProfileBinding?.tablayout?.getTabAt(i)?.customView!!
                    val view: View = Objects.requireNonNull<View>(
                        fragmentCancelMyUpcomingAppointmentBinding?.tablayout?.getTabAt(i)?.customView
                    )

                    val tab_item_tv =
                        Objects.requireNonNull(view)
                            .findViewById<TextView>(R.id.tab_item_tv)
                    if (i == tab.position) {
                        tab_item_tv.setTextColor(resources.getColor(R.color.background_white))
                        Objects.requireNonNull(view).background =
                            resources.getDrawable(R.drawable.tab_background_selected)
                    } else {
                        tab_item_tv.setTextColor(
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
                when (tab.position) {
                    0 -> {
                        isAllVisible = true
                        defaultListSetup(appointmentItemArrayList)
                        fragmentCancelMyUpcomingAppointmentBinding?.llAllAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutDoctorList?.tvNoDate?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutNursesList?.tvNoDateNuurses?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.rgHospital?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                            View.GONE
                    }
                    1 -> {
                        isAllVisible = false
                        fragmentCancelMyUpcomingAppointmentBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        defaultDoctorListSetup(doctorAppointmentItemArrayList)
                        fragmentCancelMyUpcomingAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                    }
                    2 -> {
                        isAllVisible = false
                        fragmentCancelMyUpcomingAppointmentBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        defaultNursesListSetup(nursesAppointmentItemArrayList)
                        fragmentCancelMyUpcomingAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE

                    }
                    3 -> {
                        isAllVisible = false
                        fragmentCancelMyUpcomingAppointmentBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.rgHospital?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.rlHospitalRecyclerview?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.rlHospitalLabRecyclerview?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.rbHospitalDoctor?.isChecked =
                            true
                        defaultHospitalDoctorListSetup(hospitalDoctorAppointmentItemArrayList)
                        fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.rgHospital?.setOnCheckedChangeListener { _, i ->
                            if (i == R.id.rb_hospital_doctor) {
                                fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.rlHospitalRecyclerview?.visibility =
                                    View.VISIBLE
                                fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.rlHospitalLabRecyclerview?.visibility =
                                    View.GONE
                                defaultHospitalDoctorListSetup(
                                    hospitalDoctorAppointmentItemArrayList
                                )
                            } else if (i == R.id.rb_hospital_lab) {
                                fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.rlHospitalRecyclerview?.visibility =
                                    View.GONE
                                fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.rlHospitalLabRecyclerview?.visibility =
                                    View.VISIBLE
                                defaultPathologyListSetup(pathologyAppointmentItemArrayList)
                            }

                        }
                        fragmentCancelMyUpcomingAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                    }
                    4 -> {
                        isAllVisible = false
                        fragmentCancelMyUpcomingAppointmentBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        defaultBabysitterListSetup(babysitterAppointmentItemArrayList)
                        fragmentCancelMyUpcomingAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                    }
                    5 -> {
                        isAllVisible = false
                        fragmentCancelMyUpcomingAppointmentBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        defaultCaregiverListSetup(caregiverAppointmentItemArrayList)
                        fragmentCancelMyUpcomingAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentCancelMyUpcomingAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                    }
                    6 -> {
                        isAllVisible = false
                        fragmentCancelMyUpcomingAppointmentBinding?.llAllAppointmentList?.visibility =
                            View.GONE
                        defaultPhysiotherapyListSetup(physiotherapyAppointmentItemArrayList)
                        fragmentCancelMyUpcomingAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentCancelMyUpcomingAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        isAllVisible = true
        fragmentCancelMyUpcomingAppointmentBinding?.llAllAppointmentList?.visibility = View.VISIBLE
        fragmentCancelMyUpcomingAppointmentBinding?.llDoctorAppointmentList?.visibility = View.GONE
        fragmentCancelMyUpcomingAppointmentBinding?.llNursesAppointmentList?.visibility = View.GONE
        fragmentCancelMyUpcomingAppointmentBinding?.llHospitalAppointmentList?.visibility =
            View.GONE
        fragmentCancelMyUpcomingAppointmentBinding?.llBabysitterAppointmentList?.visibility =
            View.GONE
        fragmentCancelMyUpcomingAppointmentBinding?.llCaregiverAppointmentList?.visibility =
            View.GONE
        fragmentCancelMyUpcomingAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
            View.GONE

    }

    // Set up recycler view for Doctor Appointment listing if available
    private fun setUpAllAppointmentListingRecyclerview(doctorAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentCancelMyUpcomingAppointmentBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist != null)
        val recyclerView =
            fragmentCancelMyUpcomingAppointmentBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        doctorAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
        val contactListAdapter =
            AdapterAllCancelAppointment(doctorAppointmentList, requireContext())
        recyclerView?.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnItemClikWithIdListener {
            override fun onItemClick(id: Int) {

            }

//            override fun onFirstItemClick(id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(FragmentAppiontmentDetails.newInstance(id.toString()))
//            }
//
//            override fun onSecondItemClick(id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                    FragmentSubmitReview.newInstance(id.toString()))
//            }

        }

    }

    // Set up recycler view for Doctor Appointment listing if available
    private fun setUpAppointmentListingRecyclerView(doctorAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentCancelMyUpcomingAppointmentBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist != null)
        val recyclerView =
            fragmentCancelMyUpcomingAppointmentBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        doctorAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
        val contactListAdapter =
            AdapterCancelMyUpcomingAppiontment(doctorAppointmentList, requireContext())
        recyclerView?.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnItemClikWithIdListener {
            override fun onItemClick(id: Int) {

            }

//            override fun onFirstItemClick(id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(FragmentAppiontmentDetails.newInstance(id.toString()))
//            }
//
//            override fun onSecondItemClick(id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                    FragmentSubmitReview.newInstance(id.toString()))
//            }

        }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpNursesAppointmentListingRecyclerview(nurseAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentCancelMyUpcomingAppointmentBinding!!.layoutNursesList.recyclerViewRootscareNursesAppointmentlist != null)
        val recyclerView =
            fragmentCancelMyUpcomingAppointmentBinding!!.layoutNursesList.recyclerViewRootscareNursesAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        nurseAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterNurseCancelMyUpcomingAppiontment(nurseAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnItemClikWithIdListener {
            override fun onItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentAppiontmentDetails.newInstance("1")
                )
            }

        }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpHospitalAppointmentListingRecyclerView(pathologyAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentCancelMyUpcomingAppointmentBinding!!.layoutHospitalList.recyclerViewRootscareHospitalAppointmentlist != null)
        val recyclerView =
            fragmentCancelMyUpcomingAppointmentBinding!!.layoutHospitalList.recyclerViewRootscareHospitalAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        pathologyAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterHospitalCancelMyUpcomingAppiontment(pathologyAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnItemClikWithIdListener {
            override fun onItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentAppiontmentDetails.newInstance("1")
                )
            }
        }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpHospitalLabAppointmentListingRecyclerView(pathologyAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentCancelMyUpcomingAppointmentBinding!!.layoutHospitalList.recyclerViewHospitalLabAppointmentList != null)
        val recyclerView =
            fragmentCancelMyUpcomingAppointmentBinding!!.layoutHospitalList.recyclerViewHospitalLabAppointmentList
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        pathologyAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterPathologyCancelMyUpcomingAppiontment(pathologyAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnItemClikWithIdListener {
            override fun onItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentAppiontmentDetails.newInstance("1")
                )
            }
        }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpPhysitherapyAppointmentlistingRecyclerview(physiotherapyAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentCancelMyUpcomingAppointmentBinding!!.layoutPhysitherapyList.recyclerViewRootscarePhysitherapyAppointmentlist != null)
        val recyclerView =
            fragmentCancelMyUpcomingAppointmentBinding!!.layoutPhysitherapyList.recyclerViewRootscarePhysitherapyAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        physiotherapyAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterPhysitherapyCancelMyUpcomingAppiontment(
                physiotherapyAppointmentList,
                requireContext()
            )
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnItemClikWithIdListener {
            override fun onItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentAppiontmentDetails.newInstance("1")
                )
            }

        }

    }

    // Set up recycler view for Caregiver Appointment listing if available
    private fun setUpCaregiverAppointmentListingRecyclerView(caregiverAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentCancelMyUpcomingAppointmentBinding!!.layoutCaregiverList.recyclerViewRootscareCaregiverAppointmentlist != null)
        val recyclerView =
            fragmentCancelMyUpcomingAppointmentBinding!!.layoutCaregiverList.recyclerViewRootscareCaregiverAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        caregiverAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterCaregiverCancelMyUpcomingAppiontment(caregiverAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnItemClikWithIdListener {
            override fun onItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentAppiontmentDetails.newInstance("1")
                )
            }

        }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpBabysitterAppointmentlistingRecyclerview(babysitterAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentCancelMyUpcomingAppointmentBinding!!.layoutBabysitterList.recyclerViewRootscareBabysitterAppointmentlist != null)
        val recyclerView =
            fragmentCancelMyUpcomingAppointmentBinding!!.layoutBabysitterList.recyclerViewRootscareBabysitterAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        babysitterAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterBabySitterCancelMyUpcomingAppiontment(
                babysitterAppointmentList,
                requireContext()
            )
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnItemClikWithIdListener {
            override fun onItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentAppiontmentDetails.newInstance("1")
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
            fragmentCancelMyUpcomingAppointmentBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutAllList?.tvNoDate?.visibility =
                View.GONE
            setUpAllAppointmentListingRecyclerview(doctorAppointmentList)


        } else {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.GONE
//            if (isAllVisible!!)
//                fragmentCancelMyUpcomingAppointmentBinding?.layoutAllList?.tvNoDate?.visibility =
//                    View.GONE
//            else
            fragmentCancelMyUpcomingAppointmentBinding?.layoutAllList?.tvNoDate?.visibility =
                View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutAllList?.tvNoDate?.text =
                "No cancelled appointments."
        }

    }

    private fun defaultDoctorListSetup(doctorAppointmentList: ArrayList<AppointmentItem?>?) {

        if (doctorAppointmentList != null && doctorAppointmentList.size > 0) {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutDoctorList?.tvNoDate?.visibility =
                View.GONE
            setUpAppointmentListingRecyclerView(doctorAppointmentList)


        } else {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentCancelMyUpcomingAppointmentBinding?.layoutDoctorList?.tvNoDate?.visibility =
                    View.GONE
            else
                fragmentCancelMyUpcomingAppointmentBinding?.layoutDoctorList?.tvNoDate?.visibility =
                    View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutDoctorList?.tvNoDate?.text =
                "No appointment for doctor booking."
        }

    }

    private fun defaultNursesListSetup(nurseAppointmentList: ArrayList<AppointmentItem?>?) {

        if (nurseAppointmentList != null && nurseAppointmentList.size > 0) {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutNursesList?.recyclerViewRootscareNursesAppointmentlist?.visibility =
                View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutNursesList?.tvNoDateNuurses?.visibility =
                View.GONE
            setUpNursesAppointmentListingRecyclerview(nurseAppointmentList)


        } else {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutNursesList?.recyclerViewRootscareNursesAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentCancelMyUpcomingAppointmentBinding?.layoutNursesList?.tvNoDateNuurses?.visibility =
                    View.GONE
            else
                fragmentCancelMyUpcomingAppointmentBinding?.layoutNursesList?.tvNoDateNuurses?.visibility =
                    View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutNursesList?.tvNoDateNuurses?.text =
                "No appointment for nurse booking."
        }

    }

    private fun defaultPhysiotherapyListSetup(physiotherapyAppointmentList: ArrayList<AppointmentItem?>?) {

        if (physiotherapyAppointmentList != null && physiotherapyAppointmentList.size > 0) {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutPhysitherapyList?.recyclerViewRootscarePhysitherapyAppointmentlist?.visibility =
                View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                View.GONE
            setUpPhysitherapyAppointmentlistingRecyclerview(physiotherapyAppointmentList)


        } else {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutPhysitherapyList?.recyclerViewRootscarePhysitherapyAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentCancelMyUpcomingAppointmentBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                    View.GONE
            else
                fragmentCancelMyUpcomingAppointmentBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                    View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.text =
                "No appointment for physiotherapy booking."
        }

    }

    private fun defaultCaregiverListSetup(caregiverAppointmentList: ArrayList<AppointmentItem?>?) {

        if (caregiverAppointmentList != null && caregiverAppointmentList.size > 0) {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutCaregiverList?.recyclerViewRootscareCaregiverAppointmentlist?.visibility =
                View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                View.GONE
            setUpCaregiverAppointmentListingRecyclerView(caregiverAppointmentList)


        } else {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutCaregiverList?.recyclerViewRootscareCaregiverAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentCancelMyUpcomingAppointmentBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                    View.GONE
            else
                fragmentCancelMyUpcomingAppointmentBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                    View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutCaregiverList?.tvNoDateCaregiver?.text =
                "No appointment for caregiver booking."
        }

    }

    private fun defaultBabysitterListSetup(babysitterAppointmentList: ArrayList<AppointmentItem?>?) {

        if (babysitterAppointmentList != null && babysitterAppointmentList.size > 0) {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutBabysitterList?.recyclerViewRootscareBabysitterAppointmentlist?.visibility =
                View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                View.GONE
            setUpBabysitterAppointmentlistingRecyclerview(babysitterAppointmentList)


        } else {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutBabysitterList?.recyclerViewRootscareBabysitterAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentCancelMyUpcomingAppointmentBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                    View.GONE
            else
                fragmentCancelMyUpcomingAppointmentBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                    View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutBabysitterList?.tvNoDateBabysitter?.text =
                "No appointment for babysitter booking."
        }

    }

    private fun defaultHospitalDoctorListSetup(doctorAppointmentList: ArrayList<AppointmentItem?>?) {

        if (doctorAppointmentList != null && doctorAppointmentList.size > 0) {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                View.GONE

            setUpHospitalAppointmentListingRecyclerView(doctorAppointmentList)


        } else {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                    View.GONE
            else
                fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                    View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.tvNoDateHospital?.text =
                "No appointment for doctor booking."
        }

    }

    private fun defaultPathologyListSetup(pathologyAppointmentList: ArrayList<AppointmentItem?>?) {

        if (pathologyAppointmentList != null && pathologyAppointmentList.size > 0) {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                View.GONE

            setUpHospitalLabAppointmentListingRecyclerView(pathologyAppointmentList)

        } else {
            fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                    View.GONE
            else
                fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                    View.VISIBLE
            fragmentCancelMyUpcomingAppointmentBinding?.layoutHospitalList?.tvNoDateHospitalLab?.text =
                "No appointment for pathology booking."
        }

    }
}