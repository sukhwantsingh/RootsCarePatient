package com.rootscare.ui.submitfeedback

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.doctorrequest.doctordetailsrequest.DoctorDetailsRequest
import com.rootscare.data.model.api.request.insertdoctorreviewratingrequest.InsertDoctorReviewRatingRequest
import com.rootscare.ui.base.BaseViewModel

class FragmentSubmitReviewViewModel : BaseViewModel<FragmentSubmitReviewNavigator>() {

    fun apiinsertdoctorreview(insertDoctorReviewRatingRequest: InsertDoctorReviewRatingRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apiinsertdoctorreview(insertDoctorReviewRatingRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successDoctorReviewRatingSubmiteResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result!= null){
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorDoctorReviewRatingSubmiteResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
}