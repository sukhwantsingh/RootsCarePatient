package com.rootscare.ui.babysitter.babysittercategorylistingUpdate

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
import com.model.RowItem
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.adapter.CustomDropDownAdapter
import com.rootscare.data.model.api.request.nurse.searchbyname.NurseSearchByNameRequest
import com.rootscare.data.model.api.response.caregiverallresponse.caregiverlist.CaregiverResultItem
import com.rootscare.data.model.api.response.caregiverallresponse.caregiverlist.GetCaregiverListResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.databinding.FragmentBabysitterCategorylistingBinding
import com.rootscare.ui.babysitter.babysittercategorylistingUpdate.adapter.AdapterCategoryListingRecyclerviewBibySitter
import com.rootscare.ui.babysitter.babysitterlistingdetails.FragmentBabySitterUpdateListingDetails
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.profile.FragmentProfile
import kotlin.math.max

class FragmentBabySitterCategoryListing :
    BaseFragment<FragmentBabysitterCategorylistingBinding, FragmentBabySitterCategoryListingViewModel>(),
    FragmentBabySitterCategoryListingNavigator {
    private var fragmentCaregiverCategoryListingBinding: FragmentBabysitterCategorylistingBinding? =
        null
    private var fragmentCaregiverCategoryListingViewModel: FragmentBabySitterCategoryListingViewModel? =
        null
    private var hidden = true
    private var selectedSpecialityNameForFilter: String? = null
    private var searchByName = ""

    var departmentDropdownList: ArrayList<RowItem?>? = null
    private var selectedSpecialityCodeForFilter: String? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_babysitter_categorylisting
    override val viewModel: FragmentBabySitterCategoryListingViewModel
        get() {
            fragmentCaregiverCategoryListingViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentBabySitterCategoryListingViewModel::class.java)
            return fragmentCaregiverCategoryListingViewModel as FragmentBabySitterCategoryListingViewModel
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
        fragmentCaregiverCategoryListingViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentCaregiverCategoryListingBinding = viewDataBinding
        // setUpViewSeeAllNursesCategorieslistingRecyclerview()


        fragmentCaregiverCategoryListingBinding?.tvFilter?.setOnClickListener {
            showFilterMenuWithCircularRevealAnimation()

        }
        adiHitForDepartmentList()
        apiHitForNurseList()
        if (searchByName != "") {
            fragmentCaregiverCategoryListingBinding?.edtSearch?.setText(searchByName)
            if (isNetworkConnected) {
                baseActivity?.showLoading()
                val nurseSearchByNameRequest = NurseSearchByNameRequest()
                nurseSearchByNameRequest.searchContent = searchByName
                fragmentCaregiverCategoryListingViewModel?.apisearchcaregiver(
                    nurseSearchByNameRequest
                )
            }
        } else {
            fragmentCaregiverCategoryListingBinding?.edtSearch?.setText("")
            apiHitForNurseList()
        }

        fragmentCaregiverCategoryListingBinding?.edtSearch?.addTextChangedListener(object :
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
//                if(s.toString().length>2){
                if (isNetworkConnected) {
                    baseActivity?.showLoading()
                    val nurseSearchByNameRequest = NurseSearchByNameRequest()
                    nurseSearchByNameRequest.searchContent =
                        fragmentCaregiverCategoryListingBinding?.edtSearch?.text?.toString()
                    fragmentCaregiverCategoryListingViewModel?.apisearchcaregiver(
                        nurseSearchByNameRequest
                    )
                }
//                }
            }
        })
    }

    private fun adiHitForDepartmentList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentCaregiverCategoryListingViewModel?.apidepartmentlist()

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun apiHitForNurseList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentCaregiverCategoryListingViewModel?.apicaregivelist1()
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Set up recycler view for service listing if available
    private fun setUpViewSeeAllNursesCategoriesListingRecyclerview(nurseList: ArrayList<CaregiverResultItem?>?) {
        assert(fragmentCaregiverCategoryListingBinding!!.recyclerViewRootscareNursescategorieslisting != null)
        val recyclerView =
            fragmentCaregiverCategoryListingBinding!!.recyclerViewRootscareNursescategorieslisting
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterCategoryListingRecyclerviewBibySitter(nurseList, context!!)
        recyclerView.adapter = contactListAdapter
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
        with(fragmentCaregiverCategoryListingBinding!!) {
            val cx: Int =
                filterMenuContainerCardView.left + filterMenuContainerCardView.right - com.rootscare.utils.ViewUtils.dpToPx(
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


    private fun setupSpecialitySpinner() {
//        var specialityResponseModelForSihatku = Gson().fromJson(clinicsViewModel?.appSharedPref?.sihatkuSectionAllSepeciality, SihatkuSpecialitiesResponseModel::class.java)
        if (departmentDropdownList != null) {
//            var dataList: LinkedList<SihatkuSpecialitiesSubModel> = specialityResponseModelForSihatku.dataList!!
//            dataList.addFirst(SihatkuSpecialitiesSubModel(SpecialityName = resources.getString(R.string.unselect)))
            val spinnerAdapter =
                CustomDropDownAdapter(context!!, departmentDropdownList!!)
            fragmentCaregiverCategoryListingBinding?.spinnerSpeciality?.adapter = spinnerAdapter

//            val customSpinnerAdapter = CustomSpinnerAdapter(this@ClinicsActivity, dataList)
//            fragmentSeeAllDoctorByGridBinding?.spinnerSpeciality?.adapter = customSpinnerAdapter
            fragmentCaregiverCategoryListingBinding?.spinnerSpeciality?.onItemSelectedListener =
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
            fragmentCaregiverCategoryListingBinding?.spinnerSpeciality?.setSelection(0)
        }
    }


    override fun successGetNurseListResponse(getCaregiverListResponse: GetCaregiverListResponse?) {

        baseActivity?.hideLoading()
        if (getCaregiverListResponse?.code.equals("200")) {
            if (getCaregiverListResponse?.result != null && getCaregiverListResponse.result.size > 0) {
                fragmentCaregiverCategoryListingBinding?.recyclerViewRootscareNursescategorieslisting?.visibility =
                    View.VISIBLE
                fragmentCaregiverCategoryListingBinding?.tvNoDate?.visibility = View.GONE
                setUpViewSeeAllNursesCategoriesListingRecyclerview(getCaregiverListResponse.result)
                // setUpViewSeeAllNursesByGridListingRecyclerview(getNurseListResponse?.result)
            } else {
                fragmentCaregiverCategoryListingBinding?.recyclerViewRootscareNursescategorieslisting?.visibility =
                    View.GONE
                fragmentCaregiverCategoryListingBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentCaregiverCategoryListingBinding?.tvNoDate?.text = "Nurse not found."
            }

        } else {
            fragmentCaregiverCategoryListingBinding?.recyclerViewRootscareNursescategorieslisting?.visibility =
                View.GONE
            fragmentCaregiverCategoryListingBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentCaregiverCategoryListingBinding?.tvNoDate?.text =
                getCaregiverListResponse?.message
        }
    }

    override fun errorGetCAregiverListResponse(throwable: Throwable?) {
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

}