package com.rootscare.ui.caregiver.bookingappointment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentCaregiverBookingAppointmentBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.bookingappointment.adapter.AdapterFromTimeRecyclerview
import com.rootscare.ui.bookingappointment.adapter.AdapterToTimeRecyclerView
import com.rootscare.ui.caregiver.bookingdetails.FragmentCaregiverBookingDetails
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterSelectHourytimeRecyclerView

class FragmentCaregiverBookingAppointment: BaseFragment<FragmentCaregiverBookingAppointmentBinding, FragmentCaregiverBookingAppointmentViewModel>(),
    FragmentCaregiverBookingAppointmentNavigator {
    private var fragmentCaregiverBookingAppointmentBinding: FragmentCaregiverBookingAppointmentBinding? = null
    private var fragmentCaregiverBookingAppointmentViewModel: FragmentCaregiverBookingAppointmentViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_caregiver_booking_appointment
    override val viewModel: FragmentCaregiverBookingAppointmentViewModel
        get() {
            fragmentCaregiverBookingAppointmentViewModel =
                ViewModelProviders.of(this).get(FragmentCaregiverBookingAppointmentViewModel::class.java!!)
            return fragmentCaregiverBookingAppointmentViewModel as FragmentCaregiverBookingAppointmentViewModel
        }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
        fun newInstance(nurseid: String): FragmentCaregiverBookingAppointment {
            val args = Bundle()
            args.putString("nurseid",nurseid)
            val fragment = FragmentCaregiverBookingAppointment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentCaregiverBookingAppointmentViewModel!!.navigator = this
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentCaregiverBookingAppointmentBinding = viewDataBinding
//        setUpAddPatientListingRecyclerview()
        setUpFromTimeListingRecyclerview()
        setUpToTimeListingRecyclerview()
        setUpHourlyTimeListingRecyclerview()
        setUpAddPatientListingRecyclerview()

        fragmentCaregiverBookingAppointmentBinding?.btnAppointmentBooking?.setOnClickListener(View.OnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentCaregiverBookingDetails.newInstance())
        })
    }
    // Set up recycler view for service listing if available
//    private fun setUpAddPatientListingRecyclerview() {
////        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentNursesBookingAppointmentBinding!!.recyclerViewRootscareAddPatientList != null)
//        val recyclerView = fragmentNursesBookingAppointmentBinding!!.recyclerViewRootscareAddPatientList
//        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
//        recyclerView.layoutManager = gridLayoutManager
//        recyclerView.setHasFixedSize(true)
////        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
////        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
//        val contactListAdapter = AdapterAddPatientListRecyclerview(context!!)
//        recyclerView.adapter = contactListAdapter
////        contactListAdapter?.recyclerViewItemClickWithView= object : OnItemClikWithIdListener {
////            override fun onItemClick(id: Int) {
////                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
////                    FragmentAppiontmentDetails.newInstance())
////            }
////
////        }
//
//    }
    // Set up recycler view for service listing if available
    private fun setUpFromTimeListingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentCaregiverBookingAppointmentBinding!!.recyclerViewRootscareFromTimeRecyclerview != null)
        val recyclerView = fragmentCaregiverBookingAppointmentBinding!!.recyclerViewRootscareFromTimeRecyclerview
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterFromTimeRecyclerview(context!!)
        recyclerView.adapter = contactListAdapter


    }
    // Set up recycler view for service listing if available
    private fun setUpToTimeListingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentCaregiverBookingAppointmentBinding!!.recyclerViewRootscareToTimeRecyclerview != null)
        val recyclerView = fragmentCaregiverBookingAppointmentBinding!!.recyclerViewRootscareToTimeRecyclerview
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterToTimeRecyclerView(context!!)
        recyclerView.adapter = contactListAdapter

    }

    // Set up recycler view for service listing if available
    private fun setUpHourlyTimeListingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentCaregiverBookingAppointmentBinding!!.recyclerViewRootscareHourlyTimeRecyclerview != null)
        val recyclerView = fragmentCaregiverBookingAppointmentBinding!!.recyclerViewRootscareHourlyTimeRecyclerview
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterSelectHourytimeRecyclerView(context!!)
        recyclerView.adapter = contactListAdapter

    }


    // Set up recycler view for service listing if available
    private fun setUpAddPatientListingRecyclerview() {
        assert(fragmentCaregiverBookingAppointmentBinding!!.recyclerViewRootscareAddPatientList != null)
        val recyclerView = fragmentCaregiverBookingAppointmentBinding!!.recyclerViewRootscareAddPatientList
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        val contactListAdapter = AdapterForNursesAddPatientListRecyclerview(context!!)
//        recyclerView.adapter = contactListAdapter
//        contactListAdapter?.recyclerViewOnAddPatientListClick= object : OnAddPatientListClick {
//            override fun onItemClick(modelOfGetAddPatientList: ResultItem?) {
//                //  familymemberid= modelOfGetAddPatientList?.id!!
//            }
//
//
//        }

    }


}