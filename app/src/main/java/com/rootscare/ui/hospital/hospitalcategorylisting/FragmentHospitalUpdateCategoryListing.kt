package com.rootscare.ui.hospital.hospitalcategorylisting

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
import android.view.ViewAnimationUtils
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
import com.rootscare.data.model.api.request.hospital.HospitalNearbyRequest
import com.rootscare.data.model.api.request.nurse.departmentnurselist.DepartmentNurseListRequest
import com.rootscare.data.model.api.request.nurse.searchbyname.NurseSearchByNameRequest
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.allhospitalistingresponse.AllHospitalListingResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.allhospitalistingresponse.HospitalResultItem
import com.rootscare.databinding.FragmentHospitalCategoryListBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.ui.hospital.hospitalcategorylisting.adapter.AdapterHospitalUpdateCategoryListingRecyclerview
import com.rootscare.ui.hospital.hospitallistdetails.FragmentHospitalListDetails
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.utils.ViewUtils

class FragmentHospitalUpdateCategoryListing :
    BaseFragment<FragmentHospitalCategoryListBinding, FragmentHospitalUpdateCategoryListingViewModel>(),
    FragmentHospitalUpdateCategoryListingNavigator {
    private var fragmentNursesCategoryListingBinding: FragmentHospitalCategoryListBinding? =
        null
    private var fragmentNursesCategoryListingViewModel: FragmentHospitalUpdateCategoryListingViewModel? =
        null
    var searchByName = ""

    private var hidden = true
    private var departmentDropdownList: ArrayList<RowItem?>? = null
    private var selectedSpecialityCodeForFilter: String? = null
    private var selectedSpecialityNameForFilter: String? = null
    private var departmentId = ""
    private var lat = ""
    private var longg = ""
    private var dis = "10"
    override val bindingVariable: Int
        get() = BR.viewModel
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null

    //    var latitudeLabel: String? = null
//    var longitudeLabel: String? = null
//    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
//    private var isGPSEnabled = false
//    private val locationPermissions = arrayOf(
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION
//    )
    private lateinit var requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>
    override val layoutId: Int
        get() = R.layout.fragment_hospital_category_list
    override val viewModel: FragmentHospitalUpdateCategoryListingViewModel
        get() {
            fragmentNursesCategoryListingViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentHospitalUpdateCategoryListingViewModel::class.java)
            return fragmentNursesCategoryListingViewModel as FragmentHospitalUpdateCategoryListingViewModel
        }

    companion object {
        fun newInstance(): FragmentHospitalUpdateCategoryListing {
            val args = Bundle()
            val fragment = FragmentHospitalUpdateCategoryListing()
            fragment.arguments = args
//            val TAG = "LocationProvider"

            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        accepted()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }
        requestMultiplePermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    //Handles permission result
                    Log.e("DEBUG", "${it.key} = ${it.value}")
                    getLastLocation()
                }
            }
        // locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        fragmentNursesCategoryListingViewModel!!.navigator = this
    }

    private fun accepted() {
        val permissions =
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        requestMultiplePermissionsLauncher.launch(permissions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentNursesCategoryListingBinding = viewDataBinding
        fragmentNursesCategoryListingBinding?.llMain?.setOnClickListener {
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
        apiHitForDepartmentList()
        if (searchByName != "") {
            fragmentNursesCategoryListingBinding?.searchByName?.setText(searchByName)
            if (isNetworkConnected) {
                baseActivity?.showLoading()
                val nurseSearchByNameRequest = NurseSearchByNameRequest()
                nurseSearchByNameRequest.searchContent = searchByName
                fragmentNursesCategoryListingViewModel?.apisearchnurse(nurseSearchByNameRequest)
            }
        } else {
            fragmentNursesCategoryListingBinding?.searchByName?.setText("")
//            apiHitForNurseList(lat, longg, dis)
        }
        //Nurse Search By Name Api Implementation
        fragmentNursesCategoryListingBinding?.searchByName?.addTextChangedListener(object :
            TextWatcher {
            override fun afterTextChanged(s: Editable) { // you can call or do what you want with your EditText here
                // yourEditText...
                if (s.toString().isEmpty()) {
                    apiHitForNurseList(lat, longg, dis)
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
                        fragmentNursesCategoryListingBinding?.searchByName?.text?.toString()
                    fragmentNursesCategoryListingViewModel?.apisearchnurse(nurseSearchByNameRequest)
                }
//                }
            }
        })
        //End Of Search by name api call

        //Nurse Filter By Speciity
        fragmentNursesCategoryListingBinding?.seeAllHospitalFilter?.setOnClickListener {
            showFilterMenuWithCircularRevealAnimation()

        }

        fragmentNursesCategoryListingBinding?.tvFilterSubmit?.setOnClickListener {
            if (selectedSpecialityCodeForFilter != null) {
                departmentId = selectedSpecialityCodeForFilter.toString()
                apiCallDepartmentNurseList(departmentId)
            } else {
                val nurseSearchByNameRequest = HospitalNearbyRequest()
                fragmentNursesCategoryListingViewModel?.apinurselist(nurseSearchByNameRequest)
//                Toast.makeText(activity, "Please select any speciality.", Toast.LENGTH_SHORT).show()
            }
            showFilterMenuWithCircularRevealAnimation()
        }
        fragmentNursesCategoryListingBinding?.tvFilterClear?.setOnClickListener {
            selectedSpecialityCodeForFilter = null
            selectedSpecialityNameForFilter = null
            fragmentNursesCategoryListingBinding?.spinnerSpeciality?.setSelection(0)
            showFilterMenuWithCircularRevealAnimation()
            apiHitForNurseList(lat, longg, dis)
        }

    }

    // Set up recycler view for service listing if available
    private fun setUpViewSeeAllNursesCategoriesListingRecyclerview(nurseList: ArrayList<HospitalResultItem?>?) {
//        assert(fragmentNursesCategoryListingBinding!!.recyclerViewRootsCareHospitalCategoriesListing != null)
        val recyclerView =
            fragmentNursesCategoryListingBinding!!.recyclerViewRootscareHospitalcategorieslisting
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter =
            AdapterHospitalUpdateCategoryListingRecyclerview(nurseList, requireActivity())
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object : OnClickWithTwoButton {
            override fun onFirstItemClick(position: Int, id: Int) {
                (activity as HomeActivity).checkInBackstack(
//                    FragmentNursesBookingAppointment.newInstance(id.toString())
                    FragmentHospitalListDetails.newInstance(id.toString())
                )
            }

            override fun onSecondItemClick(id: Int) {
                (activity as HomeActivity).checkInBackstack(
                    FragmentHospitalListDetails.newInstance(id.toString())
//                    FragmentHospitalUpdateCategoryListingMap.newInstance(nurseList)
                )
//                activity!!.startActivity(FragmentHospitalUpdateCategoryListingMap.newIntent(activity!!))
//                activity!!.finish()
            }
        }


    }

    override fun successGetNurseListResponse(getNurseListResponse: AllHospitalListingResponse?) {
        baseActivity?.hideLoading()
        if (getNurseListResponse?.code.equals("200")) {
            if (getNurseListResponse?.result != null && getNurseListResponse.result.size > 0) {
                fragmentNursesCategoryListingBinding?.recyclerViewRootscareHospitalcategorieslisting?.visibility =
                    View.VISIBLE
                fragmentNursesCategoryListingBinding?.tvNoDate?.visibility = View.GONE
                setUpViewSeeAllNursesCategoriesListingRecyclerview(getNurseListResponse.result)
                // setUpViewSeeAllNursesByGridlistingRecyclerview(getNurseListResponse?.result)
            } else {
                fragmentNursesCategoryListingBinding?.recyclerViewRootscareHospitalcategorieslisting?.visibility =
                    View.GONE
                fragmentNursesCategoryListingBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentNursesCategoryListingBinding?.tvNoDate?.text = "No nearby hospitals found"
            }

        } else {
            fragmentNursesCategoryListingBinding?.recyclerViewRootscareHospitalcategorieslisting?.visibility =
                View.GONE
            fragmentNursesCategoryListingBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentNursesCategoryListingBinding?.tvNoDate?.text = "No nearby hospitals found"
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

    fun apiHitForNurseList(lat: String, longg: String, dis: String) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            baseActivity?.showLoading()
            val departmentNurseListRequest = HospitalNearbyRequest()
            departmentNurseListRequest.latitude = lat
            departmentNurseListRequest.longitudes = longg
            departmentNurseListRequest.distance = dis
            fragmentNursesCategoryListingViewModel?.apinurselist(departmentNurseListRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun apiHitForDepartmentList() {
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
        with(fragmentNursesCategoryListingBinding!!) {
            val cx: Int =
                filterMenuContainerCardView.left + filterMenuContainerCardView.right - ViewUtils.dpToPx(
                    30f
                )
            val cy = 0
            val radius: Int =
                filterMenuContainerCardView.width.coerceAtLeast(filterMenuContainerCardView.height)
            if (hidden) {
                val anim = ViewAnimationUtils.createCircularReveal(
                    filterMenuContainerCardView, cx, cy, 0f, radius.toFloat()
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
                    filterMenuContainerCardView, cx, cy, radius.toFloat(), 0f
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
            val spinnerAdapter =
                CustomDropDownAdapter(requireActivity(), departmentDropdownList!!)
            fragmentNursesCategoryListingBinding?.spinnerSpeciality?.adapter = spinnerAdapter
            fragmentNursesCategoryListingBinding?.spinnerSpeciality?.onItemSelectedListener =
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
            fragmentNursesCategoryListingBinding?.spinnerSpeciality?.setSelection(0)
        }
    }

    private fun apiCallDepartmentNurseList(departmentId: String) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val departmentNurseListRequest = DepartmentNurseListRequest()
            departmentNurseListRequest.departmentId = departmentId
            //   fragmentNursesCategoryListingViewModel?.apiDepartmentNurseList(departmentNurseListRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getLastLocation() {
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result

                lat = (+(lastLocation)!!.latitude).toString()
                longg = (+(lastLocation)!!.longitude).toString()
                apiHitForNurseList(lat, longg, dis)
                Log.d(HomeFragment.TAG, "getLastLocation: $lat")
                Log.d(HomeFragment.TAG, "getLastLocation: $longg")
            } else {
                Log.w(HomeFragment.TAG, "getLastLocation:exception", task.exception)
                //showMessage("No location detected. Make sure location is enabled on the device.")
            }
        }
    }

    private fun showSnackBar(mainTextStringId: String) {
        Toast.makeText(context, mainTextStringId, Toast.LENGTH_LONG).show()
    }

//    private fun checkPermissions(): Boolean {
//        val permissionState = context?.let {
//            ActivityCompat.checkSelfPermission(
//                it,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        }
//        return permissionState == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun startLocationPermissionRequest() {
//        ActivityCompat.requestPermissions(
//            context as Activity,
//            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//            REQUEST_PERMISSIONS_REQUEST_CODE
//        )
//    }
//
//    private fun requestPermissions() {
//        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
//            context as Activity,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        )
//        if (shouldProvideRationale) {
//            Log.i(
//                HomeFragment.TAG,
//                "Displaying permission rationale to provide additional context."
//            )
//            showSnackBar("Location permission is needed for core functionality")
//        } else {
//            Log.i(HomeFragment.TAG, "Requesting permission")
//            startLocationPermissionRequest()
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        Log.i(HomeFragment.TAG, "onRequestPermissionResult")
//        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
//            when {
//                grantResults.isEmpty() -> {
//                    // If user interaction was interrupted, the permission request is cancelled and you
//                    // receive empty arrays.
//                    Log.i(HomeFragment.TAG, "User interaction was cancelled.")
//                }
//                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
//                    // Permission granted.
//                    getLastLocation()
//                }
//                else -> {
//                    showSnackBar(
//                        "Permission was denied"
//                    )
//                }
//            }
//        }
//    }

}