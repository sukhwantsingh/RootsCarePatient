package com.rootscare.ui.babysitter.babysitterlistingdetails

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentBabysitterListingDetailsBinding
import com.rootscare.ui.babysitter.babysitterbookingappointment.FragmentBabysitterBookingAppointment
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.physiotherapy.submitreviewforservice.FragmentSubmitReview

class FragmentBabySitterListingDetails : BaseFragment<FragmentBabysitterListingDetailsBinding, FragmentBabySitterListingDetailsViewModel>(),
    FragmentBabySitterListingDetailsNavigator {
    private var fragmentBabysitterListingDetailsBinding: FragmentBabysitterListingDetailsBinding? = null
    private var fragmentBabySitterListingDetailsViewModel: FragmentBabySitterListingDetailsViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_babysitter_listing_details
    override val viewModel: FragmentBabySitterListingDetailsViewModel
        get() {
            fragmentBabySitterListingDetailsViewModel =
                ViewModelProviders.of(this).get(FragmentBabySitterListingDetailsViewModel::class.java!!)
            return fragmentBabySitterListingDetailsViewModel as FragmentBabySitterListingDetailsViewModel
        }
    companion object {
        fun newInstance(): FragmentBabySitterListingDetails {
            val args = Bundle()
            val fragment = FragmentBabySitterListingDetails()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentBabySitterListingDetailsViewModel!!.navigator = this
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBabysitterListingDetailsBinding = viewDataBinding
//        setUpViewSeeAllNursesCategorieslistingRecyclerview()
        setUpViewNursesFeeslistingRecyclerview()
        fragmentBabysitterListingDetailsBinding?.btnRootscareBookingNurses?.setOnClickListener(View.OnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentBabysitterBookingAppointment.newInstance())
        })

        fragmentBabysitterListingDetailsBinding?.btnBookingAppointment?.setOnClickListener(View.OnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentBabysitterBookingAppointment.newInstance())
        })

        fragmentBabysitterListingDetailsBinding?.txtWriteYourReview?.setOnClickListener(View.OnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentSubmitReview.newInstance())
        })
    }

    // Set up recycler view for service listing if available
    private fun setUpViewNursesFeeslistingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentBabysitterListingDetailsBinding!!.recyclerViewRootscareNursesfeesListing != null)
        val recyclerView = fragmentBabysitterListingDetailsBinding!!.recyclerViewRootscareNursesfeesListing
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
   //     val contactListAdapter = AdapterNursesFeesListingRecyclerView(context!!)
       // recyclerView.adapter = contactListAdapter

    }
}