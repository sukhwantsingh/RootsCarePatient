package com.rootscare.ui.hospital.hospitalcategorylisting

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.JsonObject
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.response.hospitalallapiresponse.allhospitalistingresponse.HospitalResultItem
import com.rootscare.databinding.FragmentMapBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.utils.AlertDialogCallBack
import com.rootscare.utils.location.IGetLocation
import com.rootscare.utils.location.LocationTrack


class FragmentHospitalUpdateCategoryListingMap :
    BaseFragment<FragmentMapBinding, FragmentHospitalUpdateCategoryListingMapViewModel>(),
    FragmentHospitalUpdateCategoryListingMapNavigator, OnMapReadyCallback, IGetLocation,
    GoogleMap.OnMyLocationChangeListener, GoogleMap.OnMarkerClickListener {

    private var fragmentMapBinding: FragmentMapBinding? = null
    private var fragmentNursesCategoryListingViewModel: FragmentHospitalUpdateCategoryListingMapViewModel? =
        null
    override val bindingVariable: Int get() = BR.viewModel
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private var map: GoogleMap? = null
    private var myPosition: LatLng? = null
    private var markerPoints: ArrayList<LatLng>? = null
    private var nurseList: ArrayList<HospitalResultItem>? = null
    private lateinit var requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>>
    override val layoutId: Int get() = R.layout.fragment_map
    override val viewModel: FragmentHospitalUpdateCategoryListingMapViewModel
        get() {
            fragmentNursesCategoryListingViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentHospitalUpdateCategoryListingMapViewModel::class.java)
            return fragmentNursesCategoryListingViewModel as FragmentHospitalUpdateCategoryListingMapViewModel
        }

    companion object {
        fun newInstance(nurseList: ArrayList<HospitalResultItem?>?): FragmentHospitalUpdateCategoryListingMap {
            val args = Bundle()
            args.putSerializable("nurseList", nurseList)
            val fragment = FragmentHospitalUpdateCategoryListingMap()
            fragment.arguments = args
//            val TAG = "LocationProvider"

            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        accepted()
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        if (view != null) {
//            val parent = requireView().parent as ViewGroup
//            parent.removeView(view)
//        }
//        return view;
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }
        requestMultiplePermissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    //Handles permission result
                    Log.e("DEBUG", "${it.key} = ${it.value}")
                    setValues()
                }
            }
        accepted()
        // locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        fragmentNursesCategoryListingViewModel!!.navigator = this
    }

    private fun initializeMap() {
//        if (map == null) {
//            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment?
//            mapFragment!!.getMapAsync(this)
//        }
        if (map == null) {
            val supportMapFragment =
                childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            supportMapFragment!!.getMapAsync(this)
        }
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
        fragmentMapBinding = viewDataBinding
        if (arguments != null && arguments?.getSerializable("nurseList") != null) {
            nurseList =
                (arguments?.getSerializable("nurseList") as ArrayList<HospitalResultItem>?)!!
//            Log.d("Nurse ID", ": $nurseId")
        }
        fragmentMapBinding?.llMain?.setOnClickListener {
            baseActivity?.hideKeyboard()
        }
//        apiHitForDepartmentList()
    }

    private fun setValues() {
        println("setValues ")
        initializeMap()
        val getGPSTracking = LocationTrack(activity)
        if (getGPSTracking.gpsChecking()) {
            showMessageWithOkCancelCallback(
                "GPS is not enabled",
                requireContext(),
                object : AlertDialogCallBack {
                    override fun onSubmit() {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }

                    override fun onCancel() {}
                })
        } else {
            getGPSTracking.getLocation()
            myPosition = LatLng(getGPSTracking.getLatitude(), getGPSTracking.getLongitude())
        }
        showEvent(nurseList!!)
    }


    override fun successGetListResponse(getNurseListResponse: JsonObject?) {
        baseActivity?.hideLoading()
    }

    override fun errorGetListResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
//            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }


    private fun apiHitForDepartmentList() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
//            fragmentNursesCategoryListingViewModel?.apiDataList()

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        println("onMapReady")
        map = googleMap
        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        if (activity != null) {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // ActivityCompat#requestPermissions here to request the missing permissions, and then overriding
                // public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
        }
        map!!.isMyLocationEnabled = true
        map!!.setOnMyLocationChangeListener(this)
//        val lat: Double = jobListDto.latitude.toDouble()
//        val longitude: Double = jobListDto.longitude.toDouble()
        map!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        val b = LatLngBounds.Builder()
        val opt = MarkerOptions()
//        val lt = LatLng(myPosition!!)
        opt.position(myPosition)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            .title("Test")
        val mkr: Marker = map!!.addMarker(opt)
        mkr.hideInfoWindow()
        b.include(mkr.position)
        val cu = CameraUpdateFactory.newLatLngZoom(myPosition, 10f)
        map!!.animateCamera(cu)
    }

    override fun getLocation(location: Location?) {
    }


    override fun onMarkerClick(marker: Marker?): Boolean {
        return false
    }

    override fun onMyLocationChange(location: Location?) {

    }

    private fun showMessageWithOkCancelCallback(
        message: String?, mContext: Context, callback: AlertDialogCallBack
    ) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(mContext)
        dialog.setMessage(message)
        dialog.setCancelable(false)
        dialog.setTitle("Roots Care")
        dialog.setPositiveButton("Cancel") { _, _ -> callback.onCancel() }
        dialog.setNegativeButton("Okay") { _, _ -> callback.onSubmit() }
        (mContext as Activity).runOnUiThread(dialog::show)
    }

    private fun showEvent(mapJobListDtoArrayList: ArrayList<HospitalResultItem>) {
        println("mapJobListDtoArrayList ${mapJobListDtoArrayList.size}")

        try {
            map!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            for (i in 0 until mapJobListDtoArrayList.size) {
                if ((mapJobListDtoArrayList[i].latitude.equals("INVALID") || mapJobListDtoArrayList[i].latitude.equals(
                        ""
                    )) &&
                    (mapJobListDtoArrayList[i].longitudes.equals("INVALID") || mapJobListDtoArrayList[i].longitudes.equals(
                        ""
                    ))
                ) {
                    mapJobListDtoArrayList.removeAt(i)
                }
            }
            markerPoints = ArrayList()
            markerPoints!!.add(myPosition!!)
            for (i in 0 until mapJobListDtoArrayList.size) {

                markerPoints!!.add(
                    LatLng(
                        mapJobListDtoArrayList[i].latitude!!.toDouble(),
                        mapJobListDtoArrayList[i].longitudes!!.toDouble()
                    )
                )
            }
            for (i in 0 until markerPoints!!.size) {
                val conf = Bitmap.Config.ARGB_8888
                val bmp = Bitmap.createBitmap(400, 400, conf)
                val canvas = Canvas(bmp)
                val color = Paint()
                color.textSize = 30f
                color.color = Color.BLACK

                val opt = BitmapFactory.Options()
                opt.inMutable = true

                val imageBitmap = BitmapFactory.decodeResource(
                    resources, R.drawable.nursesphoto, opt
                )
                val resized = Bitmap.createScaledBitmap(
                    imageBitmap, 320, 160, true
                )
                canvas.drawBitmap(resized, 40f, 40f, color)

                canvas.drawText(mapJobListDtoArrayList[i].name!!, 50f, 130f, color)
//                map!!.addMarker(MarkerOptions().position(location).title("Job"))
                // add marker to Map
                map!!.addMarker(
                    MarkerOptions()
                        .position(markerPoints!![i])
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp)) // Specifies the anchor to be at a particular point in the marker image.
                        .anchor(0.5f, 1f)
                )
            }

            val cu = CameraUpdateFactory.newLatLngZoom(myPosition, 10f)
            map!!.animateCamera(cu)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMarkerBitmapFromView(@DrawableRes resId: Int): Bitmap? {
        val customMarkerView =
            (activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!.inflate(
                R.layout.view_custom_marker, null
            )
        val markerImageView = customMarkerView.findViewById<View>(R.id.profile_image) as ImageView
        markerImageView.setImageResource(resId)
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        customMarkerView.layout(
            0, 0, customMarkerView.measuredWidth, customMarkerView.measuredHeight
        )
        customMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.measuredWidth, customMarkerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.background
        drawable?.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    private fun getMarkerBitmapFromView(bitmap: Bitmap): Bitmap? {
        val mCustomMarkerView =
            (activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!.inflate(
                R.layout.view_custom_marker, null
            )
        val mMarkerImageView = mCustomMarkerView.findViewById(R.id.profile_image) as ImageView
        mMarkerImageView.setImageBitmap(bitmap)
        mCustomMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        mCustomMarkerView.layout(
            0, 0, mCustomMarkerView.measuredWidth, mCustomMarkerView.measuredHeight
        )
        mCustomMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            400, 400,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = mCustomMarkerView.background
        drawable?.draw(canvas)
        mCustomMarkerView.draw(canvas)
        return returnedBitmap
    }


}
