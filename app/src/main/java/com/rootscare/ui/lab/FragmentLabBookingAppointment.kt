/*
package com.rootscare.ui.lab

import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentBookingLabBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.bookingappointment.adapter.AdapterFromTimeRecyclerview
import com.rootscare.ui.bookingappointment.adapter.AdapterToTimeRecyclerView
import com.rootscare.ui.caregiver.bookingdetails.FragmentCaregiverBookingDetails
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.lab.Adapter.AdapterExtra
import com.rootscare.ui.lab.Adapter.RecyclerViewAdapter
import com.rootscare.ui.nurses.nursesbookingappointment.adapter.AdapterSelectHourytimeRecyclerView
import java.util.*

class FragmentLabBookingAppointment: BaseFragment<FragmentBookingLabBinding, FragmentLabBookingAppointmentViewModel>(),
    FragmentLabBookingAppointmentNavigator, RecyclerViewAdapter.AdapterCallback {
    private var fragmentCaregiverBookingAppointmentBinding: FragmentBookingLabBinding? = null
    private var fragmentCaregiverBookingAppointmentViewModel: FragmentLabBookingAppointmentViewModel? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    var txt_view: RecyclerView? = null
    private var arrayList: ArrayList<String>? = null
    private var adapter: RecyclerViewAdapter? = null
    var publication_list1: ArrayList<HashMap<String, String>>? =
        null
    override val layoutId: Int

        get() = R.layout.fragment_booking_lab
    var adapterExtra: AdapterExtra? = null

    override val viewModel: FragmentLabBookingAppointmentViewModel
        get() {
            fragmentCaregiverBookingAppointmentViewModel =
                ViewModelProviders.of(this).get(FragmentLabBookingAppointmentViewModel::class.java!!)
            return fragmentCaregiverBookingAppointmentViewModel as FragmentLabBookingAppointmentViewModel
        }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
        fun newInstance(nurseid: String): FragmentLabBookingAppointment {
            val args = Bundle()
            args.putString("nurseid",nurseid)
            val fragment = FragmentLabBookingAppointment()
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
        txt_view = view.findViewById(R.id.recyclerView_lab_test_type)
        publication_list1 = ArrayList()

        with(txt_view) {
            this?.setLayoutManager(
                LinearLayoutManager(
                    activity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
        }

//        setUpAddPatientListingRecyclerview()
        setUpFromTimeListingRecyclerview()
        setUpToTimeListingRecyclerview()
        setUpHourlyTimeListingRecyclerview()
        setUpAddPatientListingRecyclerview()
        populateRecyclerView(view)

        fragmentCaregiverBookingAppointmentBinding?.btnAppointmentBooking?.setOnClickListener(View.OnClickListener {
            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
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

    private fun populateRecyclerView(view: View) {
        val recyclerView: RecyclerView =
            view.findViewById<View>(R.id.recyclerView_lab_test1_type) as RecyclerView
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.setLayoutManager(linearLayoutManager)
        arrayList = ArrayList()
        for (i in 1..5) arrayList!!.add("Blood Test $i") //Adding items to recycler view
        adapter = context?.let { RecyclerViewAdapter(it, arrayList, this@FragmentLabBookingAppointment) }
        recyclerView.setAdapter(adapter)
    }

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
        assert(fragmentCaregiverBookingAppointmentBinding!!.recyclerViewTimeslotby30minute != null)
        val recyclerView = fragmentCaregiverBookingAppointmentBinding!!.recyclerViewTimeslotby30minute
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

    override fun onMethodCallback() {
        publication_list1?.clear()
        val selectedRows: SparseBooleanArray? =
            adapter?.let { RecyclerViewAdapter.getSelectedIds(it) } //Get the selected ids from adapter

        //Check if item is selected or not via size
        //Check if item is selected or not via size
        if (selectedRows != null) {
            if (selectedRows.size() > 0) {
                val stringBuilder = StringBuilder()
                //Loop to all the selected rows array
                for (i in 0 until selectedRows.size()) {

                    //Check if selected rows have value i.e. checked item
                    if (selectedRows.valueAt(i)) {

                        //Get the checked item text from array list by getting keyAt method of selectedRowsarray
                        val selectedRowLabel = arrayList!![selectedRows.keyAt(i)]
                        Log.d(
                            "TAG",
                            "onMethodCallback: " + arrayList!![selectedRows.keyAt(i)]
                        )
                        //append the row label text
                        val list =
                            ArrayList<String>()
                        list.add(arrayList!![selectedRows.keyAt(i)])
                        val hashMap1 =
                            HashMap<String, String>()
                        hashMap1["id"] = arrayList!![selectedRows.keyAt(i)]
                        publication_list1?.add(hashMap1)
                        adapterExtra =
                            AdapterExtra(publication_list1, activity, this@FragmentLabBookingAppointment)
                        txt_view?.setAdapter(adapterExtra)
                        Log.d("TAG", "onMethodCallback: $list")
                        stringBuilder.append(
                            """
                                $selectedRowLabel
                                
                                """.trimIndent()
                        )
                    }
                }
            }
        }
    }


}*/
