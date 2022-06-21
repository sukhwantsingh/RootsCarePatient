package com.rootscare.ui.appointment

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
import com.interfaces.OnClickWithTwoButton
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem
import com.rootscare.data.model.api.response.appointmenthistoryresponse.Result
import com.rootscare.databinding.FragmentAppointmentBinding
import com.rootscare.ui.appointment.adapter.*
import com.rootscare.ui.appointment.subfragment.FragmentAppiontmentDetails
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.nurses.review.FragmentNurseReviewSubmit
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.ui.submitfeedback.FragmentSubmitReview
import com.rootscare.ui.todaysappointment.subFragment.FragmentAppointmentDetailsForAll
import java.util.*

class FragmentAppointment :
    BaseFragment<FragmentAppointmentBinding, FragmentAppointmentViewModel>(),
    FragmentAppointmentNavigator {
    private var fragmentAppointmentBinding: FragmentAppointmentBinding? = null
    private var fragmentAppointmentViewModel: FragmentAppointmentViewModel? = null
    var appointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    var doctorAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    var nursesAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    var physiotherapyAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    var caregiverAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    var babysitterAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    var hospitalDoctorAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    var pathologyAppointmentItemArrayList: ArrayList<AppointmentItem?>? = null
    private var isAllVisible: Boolean? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_appointment
    override val viewModel: FragmentAppointmentViewModel
        get() {
            fragmentAppointmentViewModel =
                ViewModelProviders.of(this).get(FragmentAppointmentViewModel::class.java)
            return fragmentAppointmentViewModel as FragmentAppointmentViewModel
        }

    companion object {
        fun newInstance(): FragmentAppointment {
            val args = Bundle()
            val fragment = FragmentAppointment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentAppointmentViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentAppointmentBinding = viewDataBinding



        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val appointmentRequest = AppointmentRequest()
            appointmentRequest.userId = fragmentAppointmentViewModel?.appSharedPref?.userId
//            appointmentRequest?.userId="11"

            fragmentAppointmentViewModel?.apiPatientAppointmentHistory(appointmentRequest)

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
            fragmentAppointmentBinding?.tablayout?.addTab(
                fragmentAppointmentBinding?.tablayout?.newTab()?.setText(
                    tabTitles[i]
                )!!, i
            )
        }
//        fragmentAppointmentBinding?.tablayout?.tabGravity = TabLayout.GRAVITY_CENTER
        for (i in 0 until fragmentAppointmentBinding?.tablayout?.tabCount!!) {
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
            fragmentAppointmentBinding?.tablayout?.getTabAt(i)?.customView = view
        }
        fragmentAppointmentBinding?.tablayout?.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                for (i in 0 until fragmentAppointmentBinding?.tablayout?.tabCount!!) {
//                    val view: View = fragmentProfileBinding?.tablayout?.getTabAt(i)?.customView!!
                    val view: View = Objects.requireNonNull<View>(
                        fragmentAppointmentBinding?.tablayout?.getTabAt(i)?.customView
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
                        fragmentAppointmentBinding?.llAllAppointmentList?.visibility = View.VISIBLE
                        fragmentAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.layoutDoctorList?.tvNoDate?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.layoutNursesList?.tvNoDateNuurses?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.layoutHospitalList?.rgHospital?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                            View.GONE
                    }
                    1 -> {
                        isAllVisible = false
                        fragmentAppointmentBinding?.llAllAppointmentList?.visibility = View.GONE
                        defaultDoctorListSetup(appointmentItemArrayList)
                        fragmentAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                    }
                    2 -> {
                        isAllVisible = false
                        fragmentAppointmentBinding?.llAllAppointmentList?.visibility = View.GONE
                        defaultNursesListSetup(nursesAppointmentItemArrayList)
                        fragmentAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE

                    }
                    3 -> {
                        isAllVisible = false
                        fragmentAppointmentBinding?.llAllAppointmentList?.visibility = View.GONE
                        fragmentAppointmentBinding?.layoutHospitalList?.rgHospital?.visibility =
                            View.VISIBLE
                        fragmentAppointmentBinding?.layoutHospitalList?.rlHospitalRecyclerview?.visibility =
                            View.VISIBLE
                        fragmentAppointmentBinding?.layoutHospitalList?.rlHospitalLabRecyclerview?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.layoutHospitalList?.rbHospitalDoctor?.isChecked =
                            true
                        defaultHospitalDoctorListSetup(hospitalDoctorAppointmentItemArrayList)
                        fragmentAppointmentBinding?.layoutHospitalList?.rgHospital?.setOnCheckedChangeListener { _, i ->
                            if (i == R.id.rb_hospital_doctor) {
                                fragmentAppointmentBinding?.layoutHospitalList?.rlHospitalRecyclerview?.visibility =
                                    View.VISIBLE
                                fragmentAppointmentBinding?.layoutHospitalList?.rlHospitalLabRecyclerview?.visibility =
                                    View.GONE
                                defaultHospitalDoctorListSetup(
                                    hospitalDoctorAppointmentItemArrayList
                                )
                            } else if (i == R.id.rb_hospital_lab) {
                                fragmentAppointmentBinding?.layoutHospitalList?.rlHospitalRecyclerview?.visibility =
                                    View.GONE
                                fragmentAppointmentBinding?.layoutHospitalList?.rlHospitalLabRecyclerview?.visibility =
                                    View.VISIBLE
                                defaultPathologyListSetup(pathologyAppointmentItemArrayList)
                            }

                        }

                        fragmentAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                    }
                    4 -> {
                        isAllVisible = false
                        fragmentAppointmentBinding?.llAllAppointmentList?.visibility = View.GONE
                        defaultBabysitterListSetup(babysitterAppointmentItemArrayList)
                        fragmentAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                    }
                    5 -> {
                        isAllVisible = false
                        fragmentAppointmentBinding?.llAllAppointmentList?.visibility = View.GONE
                        defaultCaregiverListSetup(caregiverAppointmentItemArrayList)
                        fragmentAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.VISIBLE
                        fragmentAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.GONE
                    }
                    6 -> {
                        isAllVisible = false
                        fragmentAppointmentBinding?.llAllAppointmentList?.visibility = View.GONE
                        defaultPhysiotherapyListSetup(physiotherapyAppointmentItemArrayList)
                        fragmentAppointmentBinding?.llDoctorAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llNursesAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llHospitalAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llBabysitterAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llCaregiverAppointmentList?.visibility =
                            View.GONE
                        fragmentAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
                            View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        isAllVisible = true
        fragmentAppointmentBinding?.llAllAppointmentList?.visibility = View.VISIBLE
        fragmentAppointmentBinding?.llDoctorAppointmentList?.visibility = View.GONE
        fragmentAppointmentBinding?.llNursesAppointmentList?.visibility = View.GONE
        fragmentAppointmentBinding?.llHospitalAppointmentList?.visibility = View.GONE
        fragmentAppointmentBinding?.llBabysitterAppointmentList?.visibility =
            View.GONE
        fragmentAppointmentBinding?.llCaregiverAppointmentList?.visibility =
            View.GONE
        fragmentAppointmentBinding?.llPhysitherapyAppointmentList?.visibility =
            View.GONE
    }

    private fun setUpAllAppointmentListingRecyclerView(doctorAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentAppointmentBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist != null)
        val recyclerView =
            fragmentAppointmentBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        doctorAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
        val contactListAdapter =
            AdapterAllAppointmentHistoryListRecyclerView(doctorAppointmentList, requireContext())
        recyclerView?.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClick = object : OnClickWithTwoButton {
            override fun onFirstItemClick(position: Int, id: Int) {
                println("onFirstItemClick  ${doctorAppointmentList?.get(position)!!.id}")
//                println("serviceType  ${doctorAppointmentList?.get(position)!!.}")
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        doctorAppointmentList[position]!!.id!!,
                        doctorAppointmentList[position]!!.userId!!,
                        doctorAppointmentList[position]!!.name!!,
                        doctorAppointmentList[position]!!.orderId!!,
                        if (doctorAppointmentList[position]!!.userType == "hospital_doctor") "doctor" else doctorAppointmentList[position]!!.userType!!
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }

            override fun onSecondItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentSubmitReview.newInstance(id.toString())
                )
            }

        }

    }

    // Set up recycler view for Doctor Appointment listing if available
    private fun setUpAppointmentListingRecyclerView(doctorAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentAppointmentBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist != null)
        val recyclerView =
            fragmentAppointmentBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        doctorAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
        val contactListAdapter =
            AdapterAppointmentListRecyclerView(doctorAppointmentList, requireContext())
        recyclerView?.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClick = object : OnClickWithTwoButton {
            override fun onFirstItemClick(position: Int, id: Int) {
                println("onFirstItemClick  ${doctorAppointmentList?.get(position)!!.id}")
//                println("serviceType  ${doctorAppointmentList?.get(position)!!.}")
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        doctorAppointmentList[position]!!.id!!,
                        doctorAppointmentList[position]!!.userId!!,
                        doctorAppointmentList[position]!!.name!!,
                        doctorAppointmentList[position]!!.orderId!!,
                        "doctor"
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }

            override fun onSecondItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentSubmitReview.newInstance(id.toString())
                )
            }

        }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpNursesAppointmentListingRecyclerView(nurseAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentAppointmentBinding!!.layoutNursesList.recyclerViewRootscareNursesAppointmentlist != null)
        val recyclerView =
            fragmentAppointmentBinding!!.layoutNursesList.recyclerViewRootscareNursesAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        nurseAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterNursesAppointmentlistRecyclerview(nurseAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClick = object : OnClickWithTwoButton {
            override fun onFirstItemClick(position: Int, id: Int) {
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        nurseAppointmentList!![position]!!.id!!,
                        nurseAppointmentList[position]!!.userId!!,
                        nurseAppointmentList[position]!!.name!!,
                        nurseAppointmentList[position]!!.orderId!!,
                        "nurse", nurseAppointmentList[position]!!.taskDetails!!
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }

            override fun onSecondItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentNurseReviewSubmit.newInstance(id.toString())
                )
            }

        }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpHospitalAppointmentListingRecyclerView(pathologyAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        val recyclerView =
            fragmentAppointmentBinding!!.layoutHospitalList.recyclerViewRootscareHospitalAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        pathologyAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterHospitalAppointmentlistRecyclerview(pathologyAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter

        contactListAdapter.recyclerViewItemClickWithView = object : OnClickWithTwoButton {
            override fun onFirstItemClick(position: Int, id: Int) {
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        pathologyAppointmentList?.get(position)!!.id!!,
                        pathologyAppointmentList[position]!!.userId!!,
                        pathologyAppointmentList[position]!!.name!!,
                        pathologyAppointmentList[position]!!.orderId!!,
                        "doctor"
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }

            override fun onSecondItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentSubmitReview.newInstance(id.toString())
                )
            }

        }
//        contactListAdapter.recyclerViewItemClickWithView = object : OnUpcomingAppointmentButtonClickListener {
//            override fun onItemClick(id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                    FragmentAppiontmentDetails.newInstance("1")
//                )
//            }
//        }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpHospitalLabAppointmentListingRecyclerView(pathologyAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        val recyclerView =
            fragmentAppointmentBinding!!.layoutHospitalList.recyclerViewHospitalLabAppointmentList
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        pathologyAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterPathologyAppointmentListRecyclerview(pathologyAppointmentList, requireContext())
        recyclerView.adapter = contactListAdapter

        contactListAdapter.recyclerViewItemClickWithView = object : OnClickWithTwoButton {
            override fun onFirstItemClick(position: Int, id: Int) {
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        pathologyAppointmentList?.get(position)!!.id!!,
                        pathologyAppointmentList[position]!!.userId!!,
                        pathologyAppointmentList[position]!!.name!!,
                        pathologyAppointmentList[position]!!.orderId!!,
                        "doctor"
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }

            override fun onSecondItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentSubmitReview.newInstance(id.toString())
                )
            }

        }
//        contactListAdapter.recyclerViewItemClickWithView = object : OnUpcomingAppointmentButtonClickListener {
//            override fun onItemClick(id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                    FragmentAppiontmentDetails.newInstance("1")
//                )
//            }
//        }

    }

    // Set up recycler view for Nurses Appointment listing if available
    private fun setUpPhysiotherapyAppointmentListingRecyclerView(physiotherapyAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentAppointmentBinding!!.layoutPhysitherapyList.recyclerViewRootscarePhysitherapyAppointmentlist != null)
        val recyclerView =
            fragmentAppointmentBinding!!.layoutPhysitherapyList.recyclerViewRootscarePhysitherapyAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        physiotherapyAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterPhysiotherapyAppointmentListRecyclerView(
                physiotherapyAppointmentList,
                requireContext()
            )
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnClickWithTwoButton {
            override fun onFirstItemClick(position: Int, id: Int) {
                val fragmentAppointmentDetailsForAll: FragmentAppointmentDetailsForAll =
                    FragmentAppointmentDetailsForAll.newInstance(
                        physiotherapyAppointmentList?.get(position)!!.id!!,
                        physiotherapyAppointmentList[position]!!.userId!!,
                        physiotherapyAppointmentList[position]!!.name!!,
                        physiotherapyAppointmentList[position]!!.orderId!!,
                        "physiotherapy"
                    )
                (activity as HomeActivity).checkInBackstack(
                    fragmentAppointmentDetailsForAll
                )
            }

            override fun onSecondItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentSubmitReview.newInstance(id.toString())
                )
            }

        }

    }

    // Set up recycler view for Caregiger Appointment listing if available
    private fun setUpCaregiverAppointmentListingRecyclerView(caregiverAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentAppointmentBinding!!.layoutCaregiverList.recyclerViewRootscareCaregiverAppointmentlist != null)
        val recyclerView =
            fragmentAppointmentBinding!!.layoutCaregiverList.recyclerViewRootscareCaregiverAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        caregiverAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter =
            AdapterCaregiverAppointmentlistRecyclerview(caregiverAppointmentList, requireContext())
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
    private fun setUpBabysitterAppointmentListingRecyclerView(babysitterAppointmentList: ArrayList<AppointmentItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        val recyclerView =
            fragmentAppointmentBinding!!.layoutBabysitterList.recyclerViewRootscareBabysitterAppointmentlist
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        babysitterAppointmentList?.sortWith { o1, o2 -> o2?.timestamp?.compareTo(o1?.timestamp!!)!! }
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerview(trainerList,context!!)
        val contactListAdapter =
            AdapterBabysitterAppointmentlistRecyclerview(
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

        if (doctorAppointmentList != null && doctorAppointmentList.size > 0) {
            fragmentAppointmentBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.VISIBLE
            fragmentAppointmentBinding?.layoutAllList?.tvNoDate?.visibility = View.GONE
            setUpAllAppointmentListingRecyclerView(doctorAppointmentList)


        } else {
            fragmentAppointmentBinding?.layoutAllList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.GONE
//            if (isAllVisible!!)
//                fragmentAppointmentBinding?.layoutAllList?.tvNoDate?.visibility =
//                    View.GONE
//            else
            fragmentAppointmentBinding?.layoutAllList?.tvNoDate?.visibility =
                View.VISIBLE
            fragmentAppointmentBinding?.layoutAllList?.tvNoDate?.text = "No appointment history."
        }

    }

    private fun defaultDoctorListSetup(doctorAppointmentList: ArrayList<AppointmentItem?>?) {

        if (doctorAppointmentList != null && doctorAppointmentList.size > 0) {
            fragmentAppointmentBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.VISIBLE
            fragmentAppointmentBinding?.layoutDoctorList?.tvNoDate?.visibility = View.GONE
            setUpAppointmentListingRecyclerView(doctorAppointmentList)


        } else {
            fragmentAppointmentBinding?.layoutDoctorList?.recyclerViewRootscareAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentAppointmentBinding?.layoutDoctorList?.tvNoDate?.visibility =
                    View.GONE
            else
                fragmentAppointmentBinding?.layoutDoctorList?.tvNoDate?.visibility =
                    View.VISIBLE
            fragmentAppointmentBinding?.layoutDoctorList?.tvNoDate?.text =
                "No appointment for doctor booking."
        }

    }

    private fun defaultNursesListSetup(nurseAppointmentList: ArrayList<AppointmentItem?>?) {

        if (nurseAppointmentList != null && nurseAppointmentList.size > 0) {
            fragmentAppointmentBinding?.layoutNursesList?.recyclerViewRootscareNursesAppointmentlist?.visibility =
                View.VISIBLE
            fragmentAppointmentBinding?.layoutNursesList?.tvNoDateNuurses?.visibility = View.GONE
            setUpNursesAppointmentListingRecyclerView(nurseAppointmentList)


        } else {
            fragmentAppointmentBinding?.layoutNursesList?.recyclerViewRootscareNursesAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentAppointmentBinding?.layoutNursesList?.tvNoDateNuurses?.visibility =
                    View.GONE
            else
                fragmentAppointmentBinding?.layoutNursesList?.tvNoDateNuurses?.visibility =
                    View.VISIBLE
            fragmentAppointmentBinding?.layoutNursesList?.tvNoDateNuurses?.text =
                "No appointment for nurse booking."
        }

    }

    private fun defaultPhysiotherapyListSetup(physiotherapyAppointmentList: ArrayList<AppointmentItem?>?) {

        if (physiotherapyAppointmentList != null && physiotherapyAppointmentList.size > 0) {
            fragmentAppointmentBinding?.layoutPhysitherapyList?.recyclerViewRootscarePhysitherapyAppointmentlist?.visibility =
                View.VISIBLE
            fragmentAppointmentBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                View.GONE
            setUpPhysiotherapyAppointmentListingRecyclerView(physiotherapyAppointmentList)


        } else {
            fragmentAppointmentBinding?.layoutPhysitherapyList?.recyclerViewRootscarePhysitherapyAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentAppointmentBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                    View.GONE
            else
                fragmentAppointmentBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.visibility =
                    View.VISIBLE
            fragmentAppointmentBinding?.layoutPhysitherapyList?.tvNoDatePhysitherapy?.text =
                "No appointment for physiotherapy booking."
        }

    }

    private fun defaultCaregiverListSetup(caregiverAppointmentList: ArrayList<AppointmentItem?>?) {

        if (caregiverAppointmentList != null && caregiverAppointmentList.size > 0) {
            fragmentAppointmentBinding?.layoutCaregiverList?.recyclerViewRootscareCaregiverAppointmentlist?.visibility =
                View.VISIBLE
            fragmentAppointmentBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                View.GONE
            setUpCaregiverAppointmentListingRecyclerView(caregiverAppointmentList)


        } else {
            fragmentAppointmentBinding?.layoutCaregiverList?.recyclerViewRootscareCaregiverAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentAppointmentBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                    View.GONE
            else
                fragmentAppointmentBinding?.layoutCaregiverList?.tvNoDateCaregiver?.visibility =
                    View.VISIBLE
            fragmentAppointmentBinding?.layoutCaregiverList?.tvNoDateCaregiver?.text =
                "No appointment for caregiver booking."
        }

    }

    private fun defaultBabysitterListSetup(babysitterAppointmentList: ArrayList<AppointmentItem?>?) {

        if (babysitterAppointmentList != null && babysitterAppointmentList.size > 0) {
            fragmentAppointmentBinding?.layoutBabysitterList?.recyclerViewRootscareBabysitterAppointmentlist?.visibility =
                View.VISIBLE
            fragmentAppointmentBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                View.GONE
            setUpBabysitterAppointmentListingRecyclerView(babysitterAppointmentList)


        } else {
            fragmentAppointmentBinding?.layoutBabysitterList?.recyclerViewRootscareBabysitterAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentAppointmentBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                    View.GONE
            else
                fragmentAppointmentBinding?.layoutBabysitterList?.tvNoDateBabysitter?.visibility =
                    View.VISIBLE
            fragmentAppointmentBinding?.layoutBabysitterList?.tvNoDateBabysitter?.text =
                "No appointment for babysitter booking."
        }

    }

    private fun defaultHospitalDoctorListSetup(doctorAppointmentList: ArrayList<AppointmentItem?>?) {

        if (doctorAppointmentList != null && doctorAppointmentList.size > 0) {
            fragmentAppointmentBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.VISIBLE
            fragmentAppointmentBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                View.GONE

            setUpHospitalAppointmentListingRecyclerView(doctorAppointmentList)


        } else {
            fragmentAppointmentBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentAppointmentBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                    View.GONE
            else
                fragmentAppointmentBinding?.layoutHospitalList?.tvNoDateHospital?.visibility =
                    View.VISIBLE
            fragmentAppointmentBinding?.layoutHospitalList?.tvNoDateHospital?.text =
                "No appointment for doctor booking."
        }

    }

    private fun defaultPathologyListSetup(pathologyAppointmentList: ArrayList<AppointmentItem?>?) {

        if (pathologyAppointmentList != null && pathologyAppointmentList.size > 0) {
            fragmentAppointmentBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.VISIBLE
            fragmentAppointmentBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                View.GONE

            setUpHospitalLabAppointmentListingRecyclerView(pathologyAppointmentList)

        } else {
            fragmentAppointmentBinding?.layoutHospitalList?.recyclerViewRootscareHospitalAppointmentlist?.visibility =
                View.GONE
            if (isAllVisible!!)
                fragmentAppointmentBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                    View.GONE
            else
                fragmentAppointmentBinding?.layoutHospitalList?.tvNoDateHospitalLab?.visibility =
                    View.VISIBLE
            fragmentAppointmentBinding?.layoutHospitalList?.tvNoDateHospitalLab?.text =
                "No appointment for pathology booking."
        }

    }
}