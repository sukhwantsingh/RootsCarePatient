package com.rootscare.ui.reviewandrating

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.patientprofilerequest.PatientProfileRequest
import com.rootscare.data.model.api.request.patientreviewandratingrequest.PatientReviewAndRatingRequest
import com.rootscare.ui.base.BaseViewModel
import com.rootscare.ui.doctorlistingdetails.FragmentDoctorListingDetailsNavigator

    class FragmentReviewAndRatingViewModel  : BaseViewModel<FragmentReviewAndRatingNavigator>() {

        fun apipatientreview(patientReviewAndRatingRequest: PatientReviewAndRatingRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
            val disposable = apiServiceWithGsonFactory.apipatientreview(patientReviewAndRatingRequest)
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        // Store last login time
                        Log.d("check_response", ": " + Gson().toJson(response))
                        navigator.successPatientReviewAndRatingResponse(response)
                        /* Saving access token after singup or login */
                        if (response.result!= null){
                        }

                    } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorPatientReviewAndRatingResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })

            compositeDisposable.add(disposable)
        }

    }