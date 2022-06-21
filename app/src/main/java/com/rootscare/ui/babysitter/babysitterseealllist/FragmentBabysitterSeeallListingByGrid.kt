/*
package com.rootscare.ui.babysitter.babysitterseealllist

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.interfaces.OnClickWithTwoButton
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentSeeAllBabysitterListBinding
import com.rootscare.databinding.FragmentSeeAllCaregiverListByGridBinding
import com.rootscare.ui.babysitter.babysittercategorylisting.FragmentBabySitterCategoryListing
import com.rootscare.ui.babysitter.babysitterseealllist.adapter.AdapterBabysitterSeeallListRecyclerview
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.caregiver.caregivercategorylisting.FragmentCaregiverCategoryListing
import com.rootscare.ui.caregiver.caregiverseealllisting.FragmentCaregiverSeeAllListingByGrid
import com.rootscare.ui.caregiver.caregiverseealllisting.FragmentCaregiverSeeAllListingByGridNavigator
import com.rootscare.ui.caregiver.caregiverseealllisting.FragmentCaregiverSeeAllListingByGridViewModel
import com.rootscare.ui.caregiver.caregiverseealllisting.adapter.AdapterCaregiverSeeAllListRecyclerview
import com.rootscare.ui.home.HomeActivity

class FragmentBabysitterSeeallListingByGrid : BaseFragment<FragmentSeeAllBabysitterListBinding, FragmentBabysitterSeeallListingByGridViewModel>(),
    FragmentBabysitterSeeallListingByGridNavigetor {
    private var fragmentSeeAllBabysitterListBinding: FragmentSeeAllBabysitterListBinding? = null
    private var fragmentBabysitterSeeallListingByGridViewModel: FragmentBabysitterSeeallListingByGridViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_see_all_babysitter_list
    override val viewModel: FragmentBabysitterSeeallListingByGridViewModel
        get() {
            fragmentBabysitterSeeallListingByGridViewModel =
                ViewModelProviders.of(this).get(FragmentBabysitterSeeallListingByGridViewModel::class.java!!)
            return fragmentBabysitterSeeallListingByGridViewModel as FragmentBabysitterSeeallListingByGridViewModel
        }

    companion object {
        fun newInstance(): FragmentBabysitterSeeallListingByGrid {
            val args = Bundle()
            val fragment = FragmentBabysitterSeeallListingByGrid()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentBabysitterSeeallListingByGridViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentSeeAllBabysitterListBinding = viewDataBinding
        setUpViewSeeAllNursesByGridlistingRecyclerview()
        fragmentSeeAllBabysitterListBinding?.btnRootscareMoreNurses?.setOnClickListener(View.OnClickListener {
            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
                FragmentBabySitterCategoryListing.newInstance())
        })

    }

    // Set up recycler view for service listing if available
    private fun setUpViewSeeAllNursesByGridlistingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentSeeAllBabysitterListBinding!!.recyclerViewRootscareSeeallnursesByGrid != null)
        val recyclerView = fragmentSeeAllBabysitterListBinding!!.recyclerViewRootscareSeeallnursesByGrid
        val gridLayoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterBabysitterSeeallListRecyclerview(context!!)
        recyclerView.adapter = contactListAdapter
        contactListAdapter?.recyclerViewItemClickWithView= object : OnClickWithTwoButton {

            override fun onFirstItemClick(id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                    FragmentDoctorProfile.newInstance())
            }

            override fun onSecondItemClick(id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                    FragmentNursesProfile.newInstance())
            }

        }


    }

}*/
