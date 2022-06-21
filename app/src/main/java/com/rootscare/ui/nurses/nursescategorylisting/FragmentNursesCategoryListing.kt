package com.rootscare.ui.nurses.nursescategorylisting

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
import com.rootscare.databinding.FragmentNursesCategorylistingBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.nurses.nursesbookingappointment.FragmentNursesBookingAppointment
import com.rootscare.ui.nurses.nursescategorylisting.adapter.AdapterNursesCtegoryListingRecyclerview
import com.rootscare.ui.nurses.nurseslistingdetails.FragmentNursesListingDetails
import com.rootscare.ui.profile.FragmentProfile

class FragmentNursesCategoryListing :
    BaseFragment<FragmentNursesCategorylistingBinding, FragmentNursesCategoryListingViewModel>(),
    FragmentNursesCategoryListingNavigator {
    private var fragmentNursesCategorylistingBinding: FragmentNursesCategorylistingBinding? = null
    private var fragmentNursesCategoryListingViewModel: FragmentNursesCategoryListingViewModel? =
        null
    private var searchByName = ""
    private var hidden = true
    var departmentDropdownList: ArrayList<RowItem?>? = null
    private var selectedSpecialityCodeForFilter: String? = null
    private var selectedSpecialityNameForFilter: String? = null
    private var departmentId = ""
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_nurses_categorylisting
    override val viewModel: FragmentNursesCategoryListingViewModel
        get() {
            fragmentNursesCategoryListingViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentNursesCategoryListingViewModel::class.java)
            return fragmentNursesCategoryListingViewModel as FragmentNursesCategoryListingViewModel
        }

    companion object {
        fun newInstance(searchByName: String): FragmentNursesCategoryListing {
            val args = Bundle()
            args.putString("searchbyname", searchByName)
            val fragment = FragmentNursesCategoryListing()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentNursesCategoryListingViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentNursesCategorylistingBinding = viewDataBinding
        fragmentNursesCategorylistingBinding?.llMain?.setOnClickListener {
//            val inputMethodManager =
//                activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.hideSoftInputFromWindow(
//                activity!!.currentFocus!!.windowToken,
//                0
//            )
            baseActivity?.hideKeyboard()
        }
        if (arguments != null && arguments?.getString("searchbyname") != null) {
            searchByName = arguments?.getString("searchbyname")!!
            Log.d("Search By Name String", ": $searchByName")
        }
        adiHitForDepartmentList()
        if (searchByName != "") {
            fragmentNursesCategorylistingBinding?.edtNurseSearchByName?.setText(searchByName)
            if (isNetworkConnected) {
                baseActivity?.showLoading()
                val nurseSearchByNameRequest = NurseSearchByNameRequest()
                nurseSearchByNameRequest.searchContent = searchByName
                fragmentNursesCategoryListingViewModel?.apisearchnurse(nurseSearchByNameRequest)
            }
        } else {
            fragmentNursesCategorylistingBinding?.edtNurseSearchByName?.setText("")
            apihitforNurseList()
        }
        //Nuese Search By Name Api Implementation
        fragmentNursesCategorylistingBinding?.edtNurseSearchByName?.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable) { // you can call or do what you want with your EditText here
// yourEditText...
                if (s.toString().isEmpty()) {
                    apihitforNurseList()
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
                        fragmentNursesCategorylistingBinding?.edtNurseSearchByName?.text?.toString()
                    fragmentNursesCategoryListingViewModel?.apisearchnurse(nurseSearchByNameRequest)
                }
//                }
            }
        })
        //End Of Search by name api call

        //Nurse Filter By Speciality
        fragmentNursesCategorylistingBinding?.txtSeeAllNurseFilter?.setOnClickListener {
            showFilterMenuWithCircularRevealAnimation()

        }

        fragmentNursesCategorylistingBinding?.tvFilterSubmit?.setOnClickListener {
            if (selectedSpecialityCodeForFilter != null) {
                departmentId = selectedSpecialityCodeForFilter.toString()
                apiCallDepartmentNurseList(departmentId)
            } else {
                fragmentNursesCategoryListingViewModel?.apinurselist()
//                Toast.makeText(activity, "Please select any speciality.", Toast.LENGTH_SHORT).show()
            }
            showFilterMenuWithCircularRevealAnimation()
        }
        fragmentNursesCategorylistingBinding?.tvFilterClear?.setOnClickListener {
            selectedSpecialityCodeForFilter = null
            selectedSpecialityNameForFilter = null
            fragmentNursesCategorylistingBinding?.spinnerSpeciality?.setSelection(0)
            showFilterMenuWithCircularRevealAnimation()
            apihitforNurseList()
        }

    }

    // Set up recycler view for service listing if available
    private fun setUpViewSeeAllNursesCategoriesListingRecyclerview(nurseList: ArrayList<ResultItem?>?) {
        assert(fragmentNursesCategorylistingBinding!!.recyclerViewRootscareNursescategorieslisting != null)
        val recyclerView =
            fragmentNursesCategorylistingBinding!!.recyclerViewRootscareNursescategorieslisting
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterNursesCtegoryListingRecyclerview(nurseList, context!!)
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnClickWithTwoButton {
            override fun onFirstItemClick(position: Int, id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentNursesBookingAppointment.newInstance(id.toString())
                )
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
                fragmentNursesCategorylistingBinding?.recyclerViewRootscareNursescategorieslisting?.visibility =
                    View.VISIBLE
                fragmentNursesCategorylistingBinding?.tvNoDate?.visibility = View.GONE
                setUpViewSeeAllNursesCategoriesListingRecyclerview(getNurseListResponse.result)
                // setUpViewSeeAllNursesByGridListingRecyclerview(getNurseListResponse?.result)
            } else {
                fragmentNursesCategorylistingBinding?.recyclerViewRootscareNursescategorieslisting?.visibility =
                    View.GONE
                fragmentNursesCategorylistingBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentNursesCategorylistingBinding?.tvNoDate?.text = "Nurse not found."
            }

        } else {
            fragmentNursesCategorylistingBinding?.recyclerViewRootscareNursescategorieslisting?.visibility =
                View.GONE
            fragmentNursesCategorylistingBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentNursesCategorylistingBinding?.tvNoDate?.text = getNurseListResponse?.message
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

    fun apihitforNurseList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentNursesCategoryListingViewModel?.apinurselist()
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun adiHitForDepartmentList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentNursesCategoryListingViewModel?.apidepartmentlist()

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showFilterMenuWithCircularRevealAnimation() {
        with(fragmentNursesCategorylistingBinding!!) {
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
            val spinnerAdapter = CustomDropDownAdapter(context!!, departmentDropdownList!!)
            fragmentNursesCategorylistingBinding?.spinnerSpeciality?.adapter = spinnerAdapter
            fragmentNursesCategorylistingBinding?.spinnerSpeciality?.onItemSelectedListener =
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
            fragmentNursesCategorylistingBinding?.spinnerSpeciality?.setSelection(0)
        }
    }

    private fun apiCallDepartmentNurseList(departmentId: String) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val departmentNurseListRequest = DepartmentNurseListRequest()
            departmentNurseListRequest.departmentId = departmentId
            fragmentNursesCategoryListingViewModel?.apidepartmentnurselist(
                departmentNurseListRequest
            )

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }
}