package com.rootscare.ui.seeallhospitalbygrid

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.interfaces.OnClickWithTwoButton
import com.model.RowItem
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.adapter.CustomDropDownAdapter
import com.rootscare.data.model.api.request.doctorrequest.doctorlistbydepartmentrequest.DoctorListByDepartmentIdRequest
import com.rootscare.data.model.api.request.doctorrequest.doctorsearchrequest.SeeAllDoctorSearch
import com.rootscare.data.model.api.request.hospital.HospitalNearbyRequest
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.allhospitalistingresponse.AllHospitalListingResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.allhospitalistingresponse.HospitalResultItem
import com.rootscare.databinding.FragmentSeeAllHospitalListBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.ui.hospital.hospitalcategorylisting.FragmentHospitalUpdateCategoryListing
import com.rootscare.ui.hospital.hospitallistdetails.FragmentHospitalListDetails
import com.rootscare.ui.seeallhospitalbygrid.adapter.AdapterSeeAllHospitalByGridRecyclerView


class FragmentSeeAllHospitalByGrid :
    BaseFragment<FragmentSeeAllHospitalListBinding, FragmentSeeAllHospitalByGridViewModel>(),
    FragmentSeeAllHospitalByGridNavigator {
    private var fragmentSeeAllDoctorByGridBinding: FragmentSeeAllHospitalListBinding? = null
    private var fragmentSeeAllDoctorByGridViewModel: FragmentSeeAllHospitalByGridViewModel? = null
    private var hidden = true
    private var selectedSpecialityCodeForFilter: String? = null
    private var selectedSpecialityNameForFilter: String? = null
    private var departmentDropdownList: ArrayList<RowItem?>? = null
    private var departmentId = ""
    private var isGrid = true
    private var lat = ""
    private var longg = ""
    private var dis = "10"
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private lateinit var requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_see_all_hospital_list
    override val viewModel: FragmentSeeAllHospitalByGridViewModel
        get() {
            fragmentSeeAllDoctorByGridViewModel =
                ViewModelProviders.of(this).get(FragmentSeeAllHospitalByGridViewModel::class.java)
            return fragmentSeeAllDoctorByGridViewModel as FragmentSeeAllHospitalByGridViewModel
        }

    companion object {
        fun newInstance(): FragmentSeeAllHospitalByGrid {
            val args = Bundle()
            val fragment = FragmentSeeAllHospitalByGrid()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        accepted()
    }

    private fun accepted() {
        val permissions =
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        requestMultiplePermissionsLauncher.launch(permissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentSeeAllDoctorByGridViewModel!!.navigator = this

        fusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }
        requestMultiplePermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    //Handles permission result
                    Log.e("DEBUG", "${it.key} = ${it.value}")
                    getLastLocation()
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentSeeAllDoctorByGridBinding = viewDataBinding
        fragmentSeeAllDoctorByGridBinding?.btnRootscareNearbyHospital?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentHospitalUpdateCategoryListing.newInstance()
            )
        }

        fragmentSeeAllDoctorByGridBinding?.ivChangeView?.setOnClickListener {
            isGrid = !isGrid
            fragmentSeeAllDoctorByGridBinding?.ivChangeView?.setImageDrawable(
                activity?.resources?.getDrawable(
                    if (!isGrid) {
                        R.drawable.ic_view_grid_item
                    } else {
                        R.drawable.ic_view_list_item
                    }
                )
            )
            if (fragmentSeeAllDoctorByGridBinding!!.recyclerViewRootscareSeeallhospitalList.adapter != null) {
                fragmentSeeAllDoctorByGridBinding!!.recyclerViewRootscareSeeallhospitalList.layoutManager =
                    GridLayoutManager(
                        activity, if (isGrid) {
                            2
                        } else {
                            1
                        }, GridLayoutManager.VERTICAL, false
                    )
                (fragmentSeeAllDoctorByGridBinding!!.recyclerViewRootscareSeeallhospitalList.adapter as AdapterSeeAllHospitalByGridRecyclerView).setType(
                    isGrid
                )
            }
        }

        /*  fragmentSeeAllDoctorByGridBinding?.btnRootscareMoreDoctor?.setOnClickListener(View.OnClickListener {
              (activity as HomeActivity).checkFragmentInBackstackAndOpen(
                  FragmentDoctorCategoriesListing.newInstance())
          })
          fragmentSeeAllDoctorByGridBinding?.llMain?.setOnClickListener(View.OnClickListener {
  //            val inputMethodManager =
  //                activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  //            inputMethodManager.hideSoftInputFromWindow(
  //                activity!!.currentFocus!!.windowToken,
  //                0
  //            )
              baseActivity?.hideKeyboard()
          })*/
//        val inputMethodManager: InputMethodManager = activity!!.getSystemService(
//            Activity.INPUT_METHOD_SERVICE
//        ) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(
//            activity!!.currentFocus!!.windowToken, 0
//        )


        //All department api call
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            fragmentSeeAllDoctorByGridViewModel?.apiDepartmentList()

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }

//        //All Doctor List Api Call
//        if (isNetworkConnected) {
//            baseActivity?.showLoading()
//            fragmentSeeAllDoctorByGridViewModel?.apiDoctorList()
//
//        } else {
//            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
//                .show()
//        }
/*
        fragmentSeeAllDoctorByGridBinding?.txtSelectDoctorDepartment?.setOnClickListener(View.OnClickListener {
            CommonDialog.showDialogForRowItemDropDownList(this!!.activity!!,"Select Department",departmentDropdownList,object:
                DropdownRowItemItemClickOnConfirm {
                override fun onConfirm(title_name: String,title_id:String) {
                    fragmentSeeAllDoctorByGridBinding?.txtSelectDoctorDepartment?.setText(title_name)
                    departmentId=title_id
                    apicalldepartmentdoctorlist(departmentId)
                }
            })
        })
*/

        //Doctor Search Api Call
        fragmentSeeAllDoctorByGridBinding?.txFiter?.setOnClickListener {
            showFilterMenuWithCircularRevealAnimation()

        }

        fragmentSeeAllDoctorByGridBinding?.tvFilterSubmit?.setOnClickListener {
            if (selectedSpecialityCodeForFilter != null) {
                departmentId = selectedSpecialityCodeForFilter.toString()
                apiCallDepartmentDoctorList(departmentId)
            } else {
                apiHitForHospitalList(lat, longg)
//                fragmentSeeAllDoctorByGridViewModel?.apiDoctorList()
//                Toast.makeText(activity, "Please select any speciality.", Toast.LENGTH_SHORT).show()
            }
            showFilterMenuWithCircularRevealAnimation()
        }
        fragmentSeeAllDoctorByGridBinding?.tvFilterClear?.setOnClickListener {
            selectedSpecialityCodeForFilter = null
            selectedSpecialityNameForFilter = null
            fragmentSeeAllDoctorByGridBinding?.spinnerSpeciality?.setSelection(0)
            showFilterMenuWithCircularRevealAnimation()
            apiHitForHospitalList(lat, longg)
        }

//        setupSpecialitySpinner()

        fragmentSeeAllDoctorByGridBinding?.txtSearchHospital?.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable) { // you can call or do what you want with your EditText here
                // yourEditText...
                if (s.toString().isEmpty()) {
                    apiHitForHospitalList(lat, longg)
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
                //   if(s.toString().length>3){
                if (isNetworkConnected) {
                    baseActivity?.showLoading()
                    val seeAllDoctorSearch = SeeAllDoctorSearch()
                    seeAllDoctorSearch.searchContent =
                        fragmentSeeAllDoctorByGridBinding?.txtSearchHospital?.text?.toString()
                    fragmentSeeAllDoctorByGridViewModel?.apiSearchDoctor(seeAllDoctorSearch)
                }
                //}
            }
        })
    }

    private fun apiHitForHospitalList(latitude: String, longitude: String) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val hospitalNearbyRequest = HospitalNearbyRequest()
            hospitalNearbyRequest.latitude = latitude
            hospitalNearbyRequest.longitudes = longitude
            fragmentSeeAllDoctorByGridViewModel?.apiDoctorList(hospitalNearbyRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Set up recycler view for service listing if available
    private fun setUpViewSeeAllDoctorByGridListingRecyclerView(allDoctorList: ArrayList<HospitalResultItem?>?) {
//        assert(fragmentSeeAllDoctorByGridBinding!!.recyclerViewRootscareSeeallhospitalList != null)
        val recyclerView =
            fragmentSeeAllDoctorByGridBinding!!.recyclerViewRootscareSeeallhospitalList
        val gridLayoutManager = GridLayoutManager(
            activity, if (isGrid) {
                2
            } else {
                1
            }, GridLayoutManager.VERTICAL, false
        )
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter =
            AdapterSeeAllHospitalByGridRecyclerView(allDoctorList, requireActivity(), isGrid)
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnClickWithTwoButton {

            override fun onFirstItemClick(position: Int, id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                    FragmentDoctorProfile.newInstance())
            }

            override fun onSecondItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentHospitalListDetails.newInstance(id.toString())
                )
                // FragmentHospitalListDetails.newInstance())
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


    /* Filter Start */

    override fun successAllDoctorListingResponse(allDoctorListingResponse: AllHospitalListingResponse?) {
        baseActivity?.hideLoading()
        if (allDoctorListingResponse?.code.equals("200")) {
            if (allDoctorListingResponse?.result != null && allDoctorListingResponse.result.size > 0) {
                fragmentSeeAllDoctorByGridBinding?.recyclerViewRootscareSeeallhospitalList?.visibility =
                    View.VISIBLE
                fragmentSeeAllDoctorByGridBinding?.tvNoDate?.visibility = View.GONE
                setUpViewSeeAllDoctorByGridListingRecyclerView(allDoctorListingResponse.result)
            } else {
                fragmentSeeAllDoctorByGridBinding?.recyclerViewRootscareSeeallhospitalList?.visibility =
                    View.GONE
                fragmentSeeAllDoctorByGridBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentSeeAllDoctorByGridBinding?.tvNoDate?.text = "No doctor list found"
            }
        } else {
            fragmentSeeAllDoctorByGridBinding?.recyclerViewRootscareSeeallhospitalList?.visibility =
                View.GONE
            fragmentSeeAllDoctorByGridBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentSeeAllDoctorByGridBinding?.tvNoDate?.text = allDoctorListingResponse?.message
//            Toast.makeText(activity, allDoctorListingResponse?.message, Toast.LENGTH_SHORT).show()
        }


    }

    override fun errorDoctorDepartmentListingResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(HomeFragment.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun apiCallDepartmentDoctorList(departmentid: String) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val doctorListByDepartmentIdRequest = DoctorListByDepartmentIdRequest()
            doctorListByDepartmentIdRequest.departmentId = departmentid
            fragmentSeeAllDoctorByGridViewModel?.apiDepartmentDoctorList(
                doctorListByDepartmentIdRequest
            )

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }


    // Circular Reveal Animation for filter layout


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showFilterMenuWithCircularRevealAnimation() {
        with(fragmentSeeAllDoctorByGridBinding!!) {
            val cx: Int =
                filterMenuContainerCardView.left + filterMenuContainerCardView.right - com.rootscare.utils.ViewUtils.dpToPx(
                    30f
                )
            val cy = 0
            val radius: Int = java.lang.Math.max(
                filterMenuContainerCardView.width,
                filterMenuContainerCardView.height
            )
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
                        filterMenuContainerCardView.visibility = android.view.View.VISIBLE
//                        commonYellowToolbar.common_toolbar_parent_layout.foreground = ColorDrawable(ContextCompat.getColor(this@ProviderListingActivity, R.color.transparentBlack))
                        // commonYellowToolbar.transparent_black_view_for_toolbar.visibility = android.view.View.VISIBLE
                        transparentBlackViewForSearch2.visibility = android.view.View.GONE
                        transparentBlackViewForSearch.visibility = android.view.View.GONE
                        transparentBlackViewForContent.visibility = android.view.View.GONE
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
                        filterMenuContainerCardView.visibility = android.view.View.GONE
//                        commonYellowToolbar.common_toolbar_parent_layout.foreground = null
//                        commonYellowToolbar.transparent_black_view_for_toolbar.visibility = android.view.View.GONE
                        transparentBlackViewForSearch2.visibility = android.view.View.GONE
                        transparentBlackViewForSearch.visibility = android.view.View.GONE
                        transparentBlackViewForContent.visibility = android.view.View.GONE
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
                CustomDropDownAdapter(requireActivity(), departmentDropdownList!!)
            fragmentSeeAllDoctorByGridBinding?.spinnerSpeciality?.adapter = spinnerAdapter

//            val customSpinnerAdapter = CustomSpinnerAdapter(this@ClinicsActivity, dataList)
//            fragmentSeeAllDoctorByGridBinding?.spinnerSpeciality?.adapter = customSpinnerAdapter
            fragmentSeeAllDoctorByGridBinding?.spinnerSpeciality?.onItemSelectedListener =
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
            fragmentSeeAllDoctorByGridBinding?.spinnerSpeciality?.setSelection(0)
        }
    }

    private fun getLastLocation() {
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result

                lat = (+(lastLocation)!!.latitude).toString()
                longg = (+(lastLocation)!!.longitude).toString()
                apiHitForHospitalList(lat, longg)
                Log.d(HomeFragment.TAG, "getLastLocation: $lat")
                Log.d(HomeFragment.TAG, "getLastLocation: $longg")
            } else {
                Log.w(HomeFragment.TAG, "getLastLocation:exception", task.exception)
                //showMessage("No location detected. Make sure location is enabled on the device.")
            }
        }
    }
}