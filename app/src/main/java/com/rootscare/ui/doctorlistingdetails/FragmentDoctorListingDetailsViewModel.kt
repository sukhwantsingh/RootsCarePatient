package com.rootscare.ui.doctorlistingdetails

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.doctorrequest.doctordetailsrequest.DoctorDetailsRequest
import com.rootscare.data.model.api.request.doctorrequest.doctorlistbydepartmentrequest.DoctorListByDepartmentIdRequest
import com.rootscare.ui.base.BaseViewModel
import com.rootscare.ui.doctorcategorieslisting.FragmentDoctorCategoriesListingNavigator

class FragmentDoctorListingDetailsViewModel : BaseViewModel<FragmentDoctorListingDetailsNavigator>() {

    fun apidoctordetails(doctorDetailsRequest: DoctorDetailsRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apidoctordetails(doctorDetailsRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successDoctorDetailsResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result!= null){
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorDoctorDetailsResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
}