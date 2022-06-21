package com.rootscare.ui.hospital.hospitalbooking

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentHospitalBookingBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.hospital.hospitalbookingdetails.FragmentHospitalBookingDetails
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterSelectHourytimeRecyclerView

class FragmentHospitalBooking : BaseFragment<FragmentHospitalBookingBinding, FragmentHospitalBookingViewModel>(),
    FragmentHospitalBookingNavigator {
    private var fragmentHospitalBookingBinding: FragmentHospitalBookingBinding? = null
    private var fragmentHospitalBookingViewModel: FragmentHospitalBookingViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_hospital_booking
    override val viewModel: FragmentHospitalBookingViewModel
        get() {
            fragmentHospitalBookingViewModel =
                ViewModelProviders.of(this).get(FragmentHospitalBookingViewModel::class.java!!)
            return fragmentHospitalBookingViewModel as FragmentHospitalBookingViewModel
        }
    companion object {
        fun newInstance(): FragmentHospitalBooking {
            val args = Bundle()
            val fragment = FragmentHospitalBooking()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentHospitalBookingViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentHospitalBookingBinding = viewDataBinding
//        setUpAddPatientListingRecyclerview()
        setUpHourlyTimeListingRecyclerview()

        setUpAddPatientListingRecyclerview()

        fragmentHospitalBookingBinding?.btnAppointmentBookingForHourly?.setOnClickListener(View.OnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentHospitalBookingDetails.newInstance())
        })
    }

    // Set up recycler view for service listing if available
//    private fun setUpAddPatientListingRecyclerview() {
////        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentHospitalBookingBinding!!.recyclerViewRootscareAddPatientList != null)
//        val recyclerView = fragmentHospitalBookingBinding!!.recyclerViewRootscareAddPatientList
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
    private fun setUpHourlyTimeListingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentHospitalBookingBinding!!.recyclerViewRootscareHourlyTimeRecyclerview != null)
        val recyclerView = fragmentHospitalBookingBinding!!.recyclerViewRootscareHourlyTimeRecyclerview
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
        assert(fragmentHospitalBookingBinding!!.recyclerViewRootscareAddPatientList != null)
        val recyclerView = fragmentHospitalBookingBinding!!.recyclerViewRootscareAddPatientList
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