package com.rootscare.ui.home

import `in`.madapps.placesautocomplete.PlaceAPI
import `in`.madapps.placesautocomplete.listener.OnPlacesDetailsListener
import `in`.madapps.placesautocomplete.model.PlaceDetails
import `in`.madapps.placesautocomplete.model.Address
import `in`.madapps.placesautocomplete.model.Place
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.LayoutLocationChooserBinding
import com.rootscare.ui.base.BaseActivity
import com.rootscare.ui.home.HomeActivity.Companion.isCurrLocChoosed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher


class ActivityLocationFetch : BaseActivity<LayoutLocationChooserBinding, HomeActivityViewModel>(),
    HomeActivityNavigator {
    private var activityHomeBinding: LayoutLocationChooserBinding? = null
    private var homeViewModel: HomeActivityViewModel? = null

  private val AUTOCOMPLETE_REQUEST_CODE = 9098

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.layout_location_chooser

    override val viewModel: HomeActivityViewModel
        get() {
            homeViewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)
            return homeViewModel as HomeActivityViewModel
        }

    var street = ""
    var city = ""
    var state = ""
    var country = ""
    var zipCode = ""
    lateinit var placesApi: PlaceAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel?.navigator = this
        activityHomeBinding = viewDataBinding
        initViews()
    }

    private fun initViews() {
        activityHomeBinding?.run {
             placesApi = PlaceAPI.Builder().apiKey(homeViewModel?.appSharedPref?.plcKey.orEmpty()).build(this@ActivityLocationFetch)
             edtSearch.setAdapter(`in`.madapps.placesautocomplete.adapter.PlacesAutoCompleteAdapter(this@ActivityLocationFetch,placesApi))

            edtSearch.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                    val place = parent.getItemAtPosition(position) as Place
                    edtSearch.setText(place.description)
                    edtSearch.setSelection(edtSearch.text.toString().length)
                    getPlaceDetails(place.id)
            }

            imgBackArrow.setOnClickListener { finish() }
//            edtSearch.setOnClickListener {
//                onGoogleSearchCalled()
//            }
            cnsCurrLoc.setOnClickListener {
                isCurrLocChoosed.value = true
                finish()
            }
        }
    }
    private fun getPlaceDetails(placeId: String) {
        placesApi.fetchPlaceDetails(placeId, object :
            OnPlacesDetailsListener {
            override fun onError(errorMessage: String) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@ActivityLocationFetch, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPlaceDetailsFetched(placeDetails: PlaceDetails) {
                setupUI(placeDetails)
            }
        })
    }
    private fun setupUI(placeDetails: PlaceDetails) {
        val address = placeDetails.address
        parseAddress(address)
        runOnUiThread {
        //    activityHomeBinding?.edtSearch?.setText(place.address)

            val latitude = placeDetails.lat.toString()
            val longitude = placeDetails.lng.toString()

            HomeActivity.currLat = placeDetails.lat ?: 0.0
            HomeActivity.currLong = placeDetails.lng ?: 0.0
            HomeActivity.isDiffLocChoosed.value = true
            finish()

            Log.e("PLACE","latitude $latitude")
            Log.e("PLACE","longitude $longitude")
        }
    }

    private fun parseAddress(address: ArrayList<Address>) {
        (0 until address.size).forEach { i ->
            when {
                address[i].type.contains("street_number") -> street += address[i].shortName + " "
                address[i].type.contains("route") -> street += address[i].shortName
                address[i].type.contains("locality") -> city += address[i].shortName
                address[i].type.contains("administrative_area_level_1") -> state += address[i].shortName
                address[i].type.contains("country") -> country += address[i].shortName
                address[i].type.contains("postal_code") -> zipCode += address[i].shortName
            }
        }
    }

    fun onGoogleSearchCalled() {
        // Set the fields to specify which types of place data to return.
//        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
//        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//            //.setCountry("NG")
//            .build(this)
//        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == -1) {
                try {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(it)

                      activityHomeBinding?.edtSearch?.setText(place.address)

                        val destinationLatLng = place.latLng
                        val latitude = destinationLatLng?.latitude.toString()
                        val longitude = destinationLatLng?.longitude.toString()

                        HomeActivity.currLat = destinationLatLng?.latitude ?: 0.0
                        HomeActivity.currLong = destinationLatLng?.longitude ?: 0.0
                        HomeActivity.isDiffLocChoosed.value = true
                        finish()

                        Log.e("PLACE","latitude $latitude")
                        Log.e("PLACE","longitude $longitude")
                    }
                } catch (e:Exception){
                    println(e)
                }



            }
        }

    }


    override fun reloadPageAfterConnectedToInternet() {

    }

    override fun errorLogoutResponse(throwable: Throwable?) {
        // implement it when required
    }

    //        val city: String = addresses[0].getLocality()
    //        val state: String = addresses[0].getAdminArea()
    //        val country: String = addresses[0].getCountryName()
    //        val postalCode: String = addresses[0].getPostalCode()
    //        val knownName: String = addresses[0].getFeatureName() // Only if available else return NULL


}
