package com.rootscare.ui.nurses

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.interfaces.OnClickWithTwoButton
import com.model.RowItem
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.adapter.CustomDropDownAdapter
import com.rootscare.data.model.api.request.nurse.departmentnurselist.DepartmentNurseListRequest
import com.rootscare.data.model.api.request.nurse.searchbyname.NurseSearchByNameRequest
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.nurses.nurselist.GetNurseListResponse
import com.rootscare.data.model.api.response.nurses.nurselist.ResultItem
import com.rootscare.databinding.FragmentSeeAllNursesListByGridBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.nurses.adapter.AdapterSeeAllNursesByGridRecyclerView
import com.rootscare.ui.nurses.nurseslistingdetails.FragmentNursesListingDetails
import com.rootscare.ui.profile.FragmentProfile

class FragmentNursesListByGrid :
    BaseFragment<FragmentSeeAllNursesListByGridBinding, FragmentNursesListByGridViewModel>(),
    FragmentNursesListByGridNavigator {
    private var fragmentSeeAllNursesListByGridBinding: FragmentSeeAllNursesListByGridBinding? = null
    private var fragmentNursesListByGridViewModel: FragmentNursesListByGridViewModel? = null
    private var hidden = true
    private var userType: String? = null
    var departmentDropdownList: ArrayList<RowItem?>? = null
    private var selectedSpecialityCodeForFilter: String? = null
    private var selectedSpecialityNameForFilter: String? = null
    private var departmentId = ""
    var isGrid = true
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_see_all_nurses_list_by_grid
    override val viewModel: FragmentNursesListByGridViewModel
        get() {
            fragmentNursesListByGridViewModel =
                ViewModelProviders.of(this).get(FragmentNursesListByGridViewModel::class.java)
            return fragmentNursesListByGridViewModel as FragmentNursesListByGridViewModel
        }

    companion object {
        //        fun newInstance(s: String): FragmentNursesListByGrid {
//            val args = Bundle()
//            val fragment = FragmentNursesListByGrid()
//            fragment.arguments = args
//            return fragment
//        }
        fun newInstance(userType: String): FragmentNursesListByGrid {
            val args = Bundle()
            args.putString("userType", userType)
            val fragment = FragmentNursesListByGrid()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentNursesListByGridViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null && arguments?.getString("userType") != null) {
            userType = arguments?.getString("userType")!!
            Log.d("user type", ": $userType")
        }

        fragmentSeeAllNursesListByGridBinding = viewDataBinding

        fragmentSeeAllNursesListByGridBinding?.ivChangeView?.setOnClickListener {
            isGrid = !isGrid
            fragmentSeeAllNursesListByGridBinding?.ivChangeView?.setImageDrawable(
                activity?.resources?.getDrawable(
                    if (!isGrid) {
                        R.drawable.ic_view_grid_item
                    } else {
                        R.drawable.ic_view_list_item
                    }
                )
            )
            if (fragmentSeeAllNursesListByGridBinding!!.recyclerViewRootscareSeeallnursesByGrid.adapter != null) {
                fragmentSeeAllNursesListByGridBinding!!.recyclerViewRootscareSeeallnursesByGrid.layoutManager =
                    GridLayoutManager(
                        activity, if (isGrid) {
                            2
                        } else {
                            1
                        }, GridLayoutManager.VERTICAL, false
                    )
                (fragmentSeeAllNursesListByGridBinding!!.recyclerViewRootscareSeeallnursesByGrid.adapter as AdapterSeeAllNursesByGridRecyclerView).setType(
                    isGrid
                )
            }
        }
        if (userType == "nurse") {
            //Department List Api Call
            adiHitForDepartmentList()
            //Nurse List Api Call
            apiHitForNurseList()
        } else {
            apiHitForPhysiotherapyList()
        }


        //Nurse Search By Name Api Implementation
        fragmentSeeAllNursesListByGridBinding?.edtNurseSearchByName?.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable) { // you can call or do what you want with your EditText here
                // yourEditText...
                if (s.toString().isEmpty()) {
                    apiHitForNurseList()
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
                // if(s.toString().length>2){
                if (isNetworkConnected) {
                    baseActivity?.showLoading()
                    val nurseSearchByNameRequest = NurseSearchByNameRequest()
                    nurseSearchByNameRequest.searchContent =
                        fragmentSeeAllNursesListByGridBinding?.edtNurseSearchByName?.text?.toString()
                    fragmentNursesListByGridViewModel?.apisearchnurse(nurseSearchByNameRequest)
                }
                //   }
            }
        })
        //End Of Search by name api call

        //Nurse Filter By Speciity
        fragmentSeeAllNursesListByGridBinding?.txtSeeAllNurseFilter?.setOnClickListener {
            showFilterMenuWithCircularRevealAnimation()

        }

        fragmentSeeAllNursesListByGridBinding?.tvFilterSubmit?.setOnClickListener {
            if (selectedSpecialityCodeForFilter != null) {
                departmentId = selectedSpecialityCodeForFilter.toString()
                apiCallDepartmentNurseList(departmentId)
            } else {
                fragmentNursesListByGridViewModel?.apinurselist()
//                Toast.makeText(activity, "Please select any speciality.", Toast.LENGTH_SHORT).show()
            }
            showFilterMenuWithCircularRevealAnimation()
        }
        fragmentSeeAllNursesListByGridBinding?.tvFilterClear?.setOnClickListener {
            selectedSpecialityCodeForFilter = null
            selectedSpecialityNameForFilter = null
            fragmentSeeAllNursesListByGridBinding?.spinnerSpeciality?.setSelection(0)
            showFilterMenuWithCircularRevealAnimation()
            apiHitForNurseList()
        }


    }

    // Set up recycler view for service listing if available
    private fun setUpViewSeeAllNursesByGridListingRecyclerview(nurseList: ArrayList<ResultItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentSeeAllNursesListByGridBinding!!.recyclerViewRootscareSeeallnursesByGrid != null)
        val recyclerView =
            fragmentSeeAllNursesListByGridBinding!!.recyclerViewRootscareSeeallnursesByGrid
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
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerview(trainerList,context!!)
        val contactListAdapter = AdapterSeeAllNursesByGridRecyclerView(nurseList, context!!, isGrid)
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnClickWithTwoButton {

            override fun onFirstItemClick(position: Int, id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                    FragmentDoctorProfile.newInstance())


            }

            override fun onSecondItemClick(id: Int) {

                (activity as HomeActivity).checkInBackstack(
                    FragmentNursesListingDetails.newInstance(id.toString())
                )
            }

        }


    }

    override fun successGetNurseListResponse(getNurseListResponse: GetNurseListResponse?) {
        baseActivity?.hideLoading()
        if (getNurseListResponse?.code.equals("200")) {
            if (getNurseListResponse?.result != null && getNurseListResponse.result.size > 0) {
                fragmentSeeAllNursesListByGridBinding?.recyclerViewRootscareSeeallnursesByGrid?.visibility =
                    View.VISIBLE
                fragmentSeeAllNursesListByGridBinding?.tvNoDate?.visibility = View.GONE
                setUpViewSeeAllNursesByGridListingRecyclerview(getNurseListResponse.result)
            } else {
                fragmentSeeAllNursesListByGridBinding?.recyclerViewRootscareSeeallnursesByGrid?.visibility =
                    View.GONE
                fragmentSeeAllNursesListByGridBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentSeeAllNursesListByGridBinding?.tvNoDate?.text = "Nurse not found."
            }

        } else {
            fragmentSeeAllNursesListByGridBinding?.recyclerViewRootscareSeeallnursesByGrid?.visibility =
                View.GONE
            fragmentSeeAllNursesListByGridBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentSeeAllNursesListByGridBinding?.tvNoDate?.text = getNurseListResponse?.message
        }
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

    override fun errorGetNurseListResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun apiHitForNurseList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentNursesListByGridViewModel?.apinurselist()
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun apiHitForPhysiotherapyList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentNursesListByGridViewModel?.apiPhysiotherapyList()
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun adiHitForDepartmentList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentNursesListByGridViewModel?.apidepartmentlist()

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Circular Reveal Animation for filter layout
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showFilterMenuWithCircularRevealAnimation() {
        with(fragmentSeeAllNursesListByGridBinding!!) {
            val cx: Int =
                filterMenuContainerCardView.left + filterMenuContainerCardView.right - com.rootscare.utils.ViewUtils.dpToPx(
                    30f
                )
            val cy = 0
            val radius: Int =
                filterMenuContainerCardView.width.coerceAtLeast(filterMenuContainerCardView.height)
            if (hidden) {
                val anim = android.view.ViewAnimationUtils.createCircularReveal(
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
                val anim = android.view.ViewAnimationUtils.createCircularReveal(
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
//                        commonYellowToolbar.transparent_black_view_for_toolbar.visibility = android.view.View.GONE
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

    private fun setupSpecialitySpinner() {
//        var specialityResponseModelForSihatku = Gson().fromJson(clinicsViewModel?.appSharedPref?.sihatkuSectionAllSepeciality, SihatkuSpecialitiesResponseModel::class.java)
        if (departmentDropdownList != null) {
//            var dataList: LinkedList<SihatkuSpecialitiesSubModel> = specialityResponseModelForSihatku.dataList!!
//            dataList.addFirst(SihatkuSpecialitiesSubModel(SpecialityName = resources.getString(R.string.unselect)))
            val spinnerAdapter =
                CustomDropDownAdapter(context!!, departmentDropdownList!!)
            fragmentSeeAllNursesListByGridBinding?.spinnerSpeciality?.adapter = spinnerAdapter

//            val customSpinnerAdapter = CustomSpinnerAdapter(this@ClinicsActivity, dataList)
//            fragmentSeeAllDoctorByGridBinding?.spinnerSpeciality?.adapter = customSpinnerAdapter
            fragmentSeeAllNursesListByGridBinding?.spinnerSpeciality?.onItemSelectedListener =
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
            fragmentSeeAllNursesListByGridBinding?.spinnerSpeciality?.setSelection(0)
        }
    }

    private fun apiCallDepartmentNurseList(departmentId: String) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val departmentNurseListRequest = DepartmentNurseListRequest()
            departmentNurseListRequest.departmentId = departmentId
            fragmentNursesListByGridViewModel?.apidepartmentnurselist(departmentNurseListRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

}