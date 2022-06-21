package com.rootscare.ui.babysitter.babysitterseealllistingUpdate

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.interfaces.OnClickWithTwoButton
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.adapter.CustomDropDownAdapter
import com.rootscare.data.model.api.request.nurse.searchbyname.NurseSearchByNameRequest
import com.rootscare.data.model.api.response.caregiverallresponse.allcaregiverlistingresponse.AllCaregiverListingResponse
import com.rootscare.data.model.api.response.caregiverallresponse.allcaregiverlistingresponse.ResultCareItem
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.databinding.FragmentSeeAllBabysitterListBinding

import com.model.RowItem
import com.rootscare.ui.babysitter.babysittercategorylistingUpdate.FragmentBabySitterCategoryListing
import com.rootscare.ui.babysitter.babysitterlistingdetails.FragmentBabySitterUpdateListingDetails
import com.rootscare.ui.babysitter.babysitterseealllistingUpdate.adapter.AdapterBabySitterSeeAllListRecyclerview
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.utils.ViewUtils
import kotlin.math.max

class FragmentBabySitterSeeAllListingByGrid :
    BaseFragment<FragmentSeeAllBabysitterListBinding, FragmentBabySitterSeeAllListingByGridViewModel>(),
    FragmentBabySitterSeeAllListingByGridNavigator {
    private var fragmentSeeAllCaregiverListByGridBinding: FragmentSeeAllBabysitterListBinding? =
        null
    private var fragmentCaregiverSeeAllListingByGridViewModel: FragmentBabySitterSeeAllListingByGridViewModel? =
        null
    override val bindingVariable: Int
        get() = BR.viewModel
    private var hidden = true
    var isGrid = true
    var departmentDropdownList: ArrayList<RowItem?>? = null
    private var selectedSpecialityCodeForFilter: String? = null
    private var selectedSpecialityNameForFilter: String? = null

    override val layoutId: Int
        get() = R.layout.fragment_see_all_babysitter_list
    override val viewModel: FragmentBabySitterSeeAllListingByGridViewModel
        get() {
            fragmentCaregiverSeeAllListingByGridViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentBabySitterSeeAllListingByGridViewModel::class.java)
            return fragmentCaregiverSeeAllListingByGridViewModel as FragmentBabySitterSeeAllListingByGridViewModel
        }

    companion object {
        fun newInstance(): FragmentBabySitterSeeAllListingByGrid {
            val args = Bundle()
            val fragment = FragmentBabySitterSeeAllListingByGrid()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentCaregiverSeeAllListingByGridViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentSeeAllCaregiverListByGridBinding = viewDataBinding
        //  setUpViewSeeAllNursesByGridlistingRecyclerview()
        apiHitForDepartmentList()
        fragmentSeeAllCaregiverListByGridBinding?.btnRootscareMoreNurses?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentBabySitterCategoryListing.newInstance()
            )
        }

        fragmentSeeAllCaregiverListByGridBinding?.ivChangeView?.setOnClickListener {
            isGrid = !isGrid
            fragmentSeeAllCaregiverListByGridBinding?.ivChangeView?.setImageDrawable(
                activity?.resources?.getDrawable(
                    if (!isGrid) {
                        R.drawable.ic_view_grid_item
                    } else {
                        R.drawable.ic_view_list_item
                    }
                )
            )
            if (fragmentSeeAllCaregiverListByGridBinding!!.recyclerViewRootscareSeeallhospitalList.adapter != null) {
                fragmentSeeAllCaregiverListByGridBinding!!.recyclerViewRootscareSeeallhospitalList.layoutManager =
                    GridLayoutManager(
                        activity, if (isGrid) {
                            2
                        } else {
                            1
                        }, GridLayoutManager.VERTICAL, false
                    )
                (fragmentSeeAllCaregiverListByGridBinding!!.recyclerViewRootscareSeeallhospitalList.adapter as AdapterBabySitterSeeAllListRecyclerview).setType(
                    isGrid
                )
            }
        }

        fragmentSeeAllCaregiverListByGridBinding?.tvFilter?.setOnClickListener {
            showFilterMenuWithCircularRevealAnimation()

        }
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentCaregiverSeeAllListingByGridViewModel?.apicaregivelist()

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
        fragmentSeeAllCaregiverListByGridBinding?.tvSearch?.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable) { // you can call or do what you want with your EditText here
                // yourEditText...
                if (s.toString().isEmpty()) {
                    apihitforCaregiverList()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
//                if(s.toString().length>2){
                if (isNetworkConnected) {
                    baseActivity?.showLoading()
                    val nurseSearchByNameRequest = NurseSearchByNameRequest()
                    nurseSearchByNameRequest.searchContent =
                        fragmentSeeAllCaregiverListByGridBinding?.tvSearch?.text?.toString()
                    fragmentCaregiverSeeAllListingByGridViewModel?.apisearchcaregiverGrid(
                        nurseSearchByNameRequest
                    )
                }
//                }
            }
        })

    }

    override fun successDoctorDepartmentListingResponse(doctorDepartmentListingResponse: DoctorDepartmentListingResponse?) {
        baseActivity?.hideLoading()
        if (doctorDepartmentListingResponse?.code.equals("200")) {

            if (doctorDepartmentListingResponse?.result != null && doctorDepartmentListingResponse.result.size > 0) {
                departmentDropdownList = ArrayList()
                departmentDropdownList?.add(RowItem("Select Speciality", "0"))
                for (i in 0 until doctorDepartmentListingResponse.result.size) {
                    val item = RowItem(
                        doctorDepartmentListingResponse.result[i]?.title!!,
                        doctorDepartmentListingResponse.result[i]?.id.toString()
                    )
                    departmentDropdownList?.add(item)
                }

            }
            setupSpecialitySpinner()
        } else {
            Toast.makeText(activity, doctorDepartmentListingResponse?.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun successAllCaregiverListingResponse(caregiverListingResponse: AllCaregiverListingResponse?) {
        baseActivity?.hideLoading()
        if (caregiverListingResponse?.code.equals("200")) {
            if (caregiverListingResponse?.result != null && caregiverListingResponse.result.size > 0) {
                fragmentSeeAllCaregiverListByGridBinding?.recyclerViewRootscareSeeallhospitalList?.visibility =
                    View.VISIBLE
                fragmentSeeAllCaregiverListByGridBinding?.tvNoDate?.visibility = View.GONE
                setUpViewSeeAllCaregiverByGridlistingRecyclerview(caregiverListingResponse.result)
            } else {
                fragmentSeeAllCaregiverListByGridBinding?.recyclerViewRootscareSeeallhospitalList?.visibility =
                    View.GONE
                fragmentSeeAllCaregiverListByGridBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentSeeAllCaregiverListByGridBinding?.tvNoDate?.text = "No caregiver list found"
            }
        } else {
            fragmentSeeAllCaregiverListByGridBinding?.recyclerViewRootscareSeeallhospitalList?.visibility =
                View.GONE
            fragmentSeeAllCaregiverListByGridBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentSeeAllCaregiverListByGridBinding?.tvNoDate?.text =
                caregiverListingResponse?.message
//            Toast.makeText(activity, allDoctorListingResponse?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun errorGetNurseListResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }


    override fun errorCaregiverDepartmentListingResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    // Set up recycler view for service listing if available
    private fun setUpViewSeeAllCaregiverByGridlistingRecyclerview(allDoctorList: ArrayList<ResultCareItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentSeeAllCaregiverListByGridBinding!!.recyclerViewRootscareSeeallhospitalList != null)
        val recyclerView =
            fragmentSeeAllCaregiverListByGridBinding!!.recyclerViewRootscareSeeallhospitalList
//        val gridLayoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
        val gridLayoutManager = GridLayoutManager(
            activity, if (isGrid) {
                2
            } else {
                1
            }, GridLayoutManager.VERTICAL, false
        )

        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterBabySitterSeeAllListRecyclerview(allDoctorList, context!!, isGrid)
        recyclerView.adapter = contactListAdapter
        //  setupSpecialitySpinner()
        contactListAdapter.recyclerViewItemClickWithView = object : OnClickWithTwoButton {

            override fun onFirstItemClick(position: Int, id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentBabySitterUpdateListingDetails.newInstance(id.toString())
                )
            }

            override fun onSecondItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentBabySitterUpdateListingDetails.newInstance(id.toString())
                )
            }

        }


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showFilterMenuWithCircularRevealAnimation() {
        with(fragmentSeeAllCaregiverListByGridBinding!!) {
            val cx: Int =
                filterMenuContainerCardView.left + filterMenuContainerCardView.right - ViewUtils.dpToPx(
                    30f
                )
            val cy = 0
            val radius: Int = max(
                filterMenuContainerCardView.width,
                filterMenuContainerCardView.height
            )
            if (hidden) {
                val anim = ViewAnimationUtils.createCircularReveal(
                    filterMenuContainerCardView,
                    cx,
                    cy,
                    0f,
                    radius.toFloat()
                )
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        filterMenuContainerCardView.visibility = View.VISIBLE
//                        commonYellowToolbar.common_toolbar_parent_layout.foreground = ColorDrawable(ContextCompat.getColor(this@ProviderListingActivity, R.color.transparentBlack))
                        // commonYellowToolbar.transparent_black_view_for_toolbar.visibility = android.view.View.VISIBLE
                        transparentBlackViewForSearch2.visibility = View.GONE
                        transparentBlackViewForSearch.visibility = View.GONE
                        transparentBlackViewForContent.visibility = View.GONE
                        hidden = false
                    }
                })
                anim.start()
            } else {
                val anim = ViewAnimationUtils.createCircularReveal(
                    filterMenuContainerCardView,
                    cx,
                    cy,
                    radius.toFloat(),
                    0f
                )
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        filterMenuContainerCardView.visibility = View.GONE
//                        commonYellowToolbar.common_toolbar_parent_layout.foreground = null
//                        commonYellowToolbar.transparent_black_view_for_toolbar.visibility = View.GONE
                        transparentBlackViewForSearch2.visibility = View.GONE
                        transparentBlackViewForSearch.visibility = View.GONE
                        transparentBlackViewForContent.visibility = View.GONE
                        hidden = true
                    }
                })
                anim.start()
            }
        }
    }

    private fun apiHitForDepartmentList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentCaregiverSeeAllListingByGridViewModel?.apidepartmentlist()

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun apihitforCaregiverList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentCaregiverSeeAllListingByGridViewModel?.apicaregivelist()
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun setupSpecialitySpinner() {
//        var specialityResponseModelForSihatku = Gson().fromJson(clinicsViewModel?.appSharedPref?.sihatkuSectionAllSepeciality, SihatkuSpecialitiesResponseModel::class.java)
        if (departmentDropdownList != null) {
//            var dataList: LinkedList<SihatkuSpecialitiesSubModel> = specialityResponseModelForSihatku.dataList!!
//            dataList.addFirst(SihatkuSpecialitiesSubModel(SpecialityName = resources.getString(R.string.unselect)))
            val spinnerAdapter =
                CustomDropDownAdapter(context!!, departmentDropdownList!!)
            fragmentSeeAllCaregiverListByGridBinding?.spinnerSpeciality?.adapter = spinnerAdapter

//            val customSpinnerAdapter = CustomSpinnerAdapter(this@ClinicsActivity, dataList)
//            fragmentSeeAllDoctorByGridBinding?.spinnerSpeciality?.adapter = customSpinnerAdapter
            fragmentSeeAllCaregiverListByGridBinding?.spinnerSpeciality?.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            selectedSpecialityCodeForFilter = null
                            selectedSpecialityNameForFilter = null
                        } else {
                            selectedSpecialityCodeForFilter =
                                departmentDropdownList?.get(position)?.item_id
                            selectedSpecialityNameForFilter =
                                departmentDropdownList?.get(position)?.title_item
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            fragmentSeeAllCaregiverListByGridBinding?.spinnerSpeciality?.setSelection(0)
        }
    }


}