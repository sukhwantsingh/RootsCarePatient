package com.rootscare.ui.babysitter.babysitterbookingappointment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentBabysitterBookingAppointmentBinding
import com.rootscare.ui.babysitter.bookingdetails.FragmentBabySitterBookingDetails
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.bookingappointment.adapter.AdapterFromTimeRecyclerview
import com.rootscare.ui.bookingappointment.adapter.AdapterToTimeRecyclerView
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterSelectHourytimeRecyclerView

class FragmentBabysitterBookingAppointment : BaseFragment<FragmentBabysitterBookingAppointmentBinding, FragmentBabysitterBookingAppointmentViewModel>(),
    FragmentBabysitterBookingAppointmentNavigator {
    private var fragmentBabysitterBookingAppointmentBinding: FragmentBabysitterBookingAppointmentBinding? = null
    private var fragmentBabysitterBookingAppointmentViewModel: FragmentBabysitterBookingAppointmentViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_babysitter_booking_appointment
    override val viewModel: FragmentBabysitterBookingAppointmentViewModel
        get() {
            fragmentBabysitterBookingAppointmentViewModel =
                ViewModelProviders.of(this).get(FragmentBabysitterBookingAppointmentViewModel::class.java!!)
            return fragmentBabysitterBookingAppointmentViewModel as FragmentBabysitterBookingAppointmentViewModel
        }
    companion object {
        fun newInstance(): FragmentBabysitterBookingAppointment {
            val args = Bundle()
            val fragment = FragmentBabysitterBookingAppointment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentBabysitterBookingAppointmentViewModel!!.navigator = this
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBabysitterBookingAppointmentBinding = viewDataBinding
//        setUpAddPatientListingRecyclerview()
        setUpFromTimeListingRecyclerview()
        setUpToTimeListingRecyclerview()
        setUpHourlyTimeListingRecyclerview()
        setUpAddPatientListingRecyclerview()

        fragmentBabysitterBookingAppointmentBinding?.btnAppointmentBooking?.setOnClickListener(View.OnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentBabySitterBookingDetails.newInstance())
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
        assert(fragmentBabysitterBookingAppointmentBinding!!.recyclerViewRootscareFromTimeRecyclerview != null)
        val recyclerView = fragmentBabysitterBookingAppointmentBinding!!.recyclerViewRootscareFromTimeRecyclerview
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
        assert(fragmentBabysitterBookingAppointmentBinding!!.recyclerViewRootscareToTimeRecyclerview != null)
        val recyclerView = fragmentBabysitterBookingAppointmentBinding!!.recyclerViewRootscareToTimeRecyclerview
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
        assert(fragmentBabysitterBookingAppointmentBinding!!.recyclerViewRootscareHourlyTimeRecyclerview != null)
        val recyclerView = fragmentBabysitterBookingAppointmentBinding!!.recyclerViewRootscareHourlyTimeRecyclerview
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
        assert(fragmentBabysitterBookingAppointmentBinding!!.recyclerViewRootscareAddPatientList != null)
        val recyclerView = fragmentBabysitterBookingAppointmentBinding!!.recyclerViewRootscareAddPatientList
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