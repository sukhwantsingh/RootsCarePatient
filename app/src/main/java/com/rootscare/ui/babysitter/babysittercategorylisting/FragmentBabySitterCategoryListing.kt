/*
package com.rootscare.ui.babysitter.babysittercategorylisting

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.interfaces.OnClickWithTwoButton
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentBabysitterCategorylistingBinding
import com.rootscare.databinding.FragmentCaregiverCategorylistingBinding
import com.rootscare.ui.babysitter.babysitterbookingappointment.FragmentBabysitterBookingAppointment
import com.rootscare.ui.babysitter.babysittercategorylisting.adapter.AdapterBabysitterCategoryListingRecyclerview
import com.rootscare.ui.babysitter.babysitterlistingdetails.FragmentBabySitterListingDetails
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.caregiver.bookingappointment.FragmentCaregiverBookingAppointment
import com.rootscare.ui.caregiver.caregivercategorylisting.FragmentCaregiverCategoryListing
import com.rootscare.ui.caregiver.caregivercategorylisting.FragmentCaregiverCategoryListingNavigator
import com.rootscare.ui.caregiver.caregivercategorylisting.FragmentCaregiverCategoryListingViewModel
import com.rootscare.ui.caregiver.caregivercategorylisting.adapter.AdapterCategoryListingRecyclerview
import com.rootscare.ui.caregiver.caregiverlistingdetails.FragmentCaregiverListingDetails
import com.rootscare.ui.home.HomeActivity

class FragmentBabySitterCategoryListing : BaseFragment<FragmentBabysitterCategorylistingBinding, FragmentBabySitterCategoryListingViewModel>(),
    FragmentBabySitterCategoryListingNavigator {
    private var fragmentBabysitterCategorylistingBinding: FragmentBabysitterCategorylistingBinding? = null
    private var fragmentBabySitterCategoryListingViewModel: FragmentBabySitterCategoryListingViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_babysitter_categorylisting
    override val viewModel: FragmentBabySitterCategoryListingViewModel
        get() {
            fragmentBabySitterCategoryListingViewModel =
                ViewModelProviders.of(this).get(FragmentBabySitterCategoryListingViewModel::class.java!!)
            return fragmentBabySitterCategoryListingViewModel as FragmentBabySitterCategoryListingViewModel
        }
    companion object {
        fun newInstance(): FragmentBabySitterCategoryListing {
            val args = Bundle()
            val fragment = FragmentBabySitterCategoryListing()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentBabySitterCategoryListingViewModel!!.navigator = this
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBabysitterCategorylistingBinding = viewDataBinding
        setUpViewSeeAllNursesCategorieslistingRecyclerview()
    }

    // Set up recycler view for service listing if available
    private fun setUpViewSeeAllNursesCategorieslistingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentBabysitterCategorylistingBinding!!.recyclerViewRootscareNursescategorieslisting != null)
        val recyclerView = fragmentBabysitterCategorylistingBinding!!.recyclerViewRootscareNursescategorieslisting
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterBabysitterCategoryListingRecyclerview(context!!)
        recyclerView.adapter = contactListAdapter
        contactListAdapter?.recyclerViewItemClickWithView= object : OnClickWithTwoButton {
            override fun onFirstItemClick(id: Int) {
                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
                    FragmentBabysitterBookingAppointment.newInstance())
            }

            override fun onSecondItemClick(id: Int) {
                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
                    FragmentBabySitterListingDetails.newInstance())
            }
//

        }


    }


}*/
