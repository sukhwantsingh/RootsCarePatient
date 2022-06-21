/*
package com.rootscare.ui.hospital.hospitalcategorylisting

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.GetLatLong
import com.androidisland.ezpermission.EzPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.interfaces.OnClickWithTwoButton
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentDoctorCategoriesListingBinding
import com.rootscare.databinding.FragmentHospitalCategoryListBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment.Companion.TAG
import com.rootscare.ui.hospital.hospitalbooking.FragmentHospitalBooking
import com.rootscare.ui.hospital.hospitalcategorylisting.adapter.AdapterHospitalCategoryListingRecyclerView
import com.rootscare.ui.hospital.hospitallistdetails.FragmentHospitalListDetails


class FragmentHospitalCategoryList : BaseFragment<FragmentHospitalCategoryListBinding, FragmentHospitalCategoryListViewModel>(),
    FragmentHospitalCategoryListNavigator {
    private var fragmentHospitalCategoryListBinding: FragmentHospitalCategoryListBinding? = null
    private var fragmentHospitalCategoryListViewModel: FragmentHospitalCategoryListViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private var latitudeLabel: String? = null
    val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private var isGPSEnabled = false
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    override val layoutId: Int
        get() = R.layout.fragment_hospital_category_list
    override val viewModel: FragmentHospitalCategoryListViewModel
        get() {
            fragmentHospitalCategoryListViewModel =
                ViewModelProviders.of(this).get(FragmentHospitalCategoryListViewModel::class.java!!)
            return fragmentHospitalCategoryListViewModel as FragmentHospitalCategoryListViewModel
        }
    companion object {
        fun newInstance(): FragmentHospitalCategoryList {
            val args = Bundle()
            val fragment = FragmentHospitalCategoryList()
            fragment.arguments = args
             val TAG = "LocationProvider"

            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions()
            }
        }
        else {
            getLastLocation()
        }
      //  locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        fragmentHospitalCategoryListViewModel!!.navigator = this
        fusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentHospitalCategoryListBinding = viewDataBinding
        setUpViewSeeAllDoctorCategorieslistingRecyclerview()
    }
    // Set up recycler view for service listing if available
    private fun setUpViewSeeAllDoctorCategorieslistingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentHospitalCategoryListBinding!!.recyclerViewRootscareHospitalcategorieslisting != null)
        val recyclerView = fragmentHospitalCategoryListBinding!!.recyclerViewRootscareHospitalcategorieslisting
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterHospitalCategoryListingRecyclerView(context!!)
        recyclerView.adapter = contactListAdapter
        contactListAdapter?.recyclerViewItemClickWithView= object : OnClickWithTwoButton {
            override fun onFirstItemClick(id: Int) {
                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
                    FragmentHospitalBooking.newInstance())
            }

            override fun onSecondItemClick(id: Int) {
                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
                    FragmentHospitalListDetails.newInstance(id.toString()))

            }
//

        }


    }
*/
/*
    public override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions()
            }
        }
        else {
            getLastLocation()
        }
    }
*//*


    private fun getLastLocation() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        activity?.let {
            fusedLocationClient?.lastLocation!!.addOnCompleteListener(it) { task ->
                if (task.isSuccessful && task.result != null) {
                    lastLocation = task.result
                    */
/* latitudeText!!.text = latitudeLabel + ": " + (lastLocation)!!.latitude
                         longitudeText!!.text = longitudeLabel + ": " + (lastLocation)!!.longitude*//*


                    Log.d(TAG, "getLastLocation: "+ (lastLocation)!!.latitude)
                    Log.d(TAG, "getLastLocation: "+ (lastLocation)!!.longitude)
                } else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)
                    //showMessage("No location detected. Make sure location is enabled on the device.")
                }
            }
        }
    }

    private fun showSnackbar(
        mainTextStringId: String, actionStringId: String,
        listener: View.OnClickListener
    ) {
        Toast.makeText(context, mainTextStringId, Toast.LENGTH_LONG).show()
    }
    private fun checkPermissions(): Boolean {
        val permissionState = context?.let {
            ActivityCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }
    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            context as Activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar("Location permission is needed for core functionality", "Okay",
                View.OnClickListener {
                    startLocationPermissionRequest()
                })
        }
        else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.")
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted.
                    getLastLocation()
                }
                else -> {
                    showSnackbar("Permission was denied", "Settings",
                        View.OnClickListener {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                Build.DISPLAY, null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }



}*/
