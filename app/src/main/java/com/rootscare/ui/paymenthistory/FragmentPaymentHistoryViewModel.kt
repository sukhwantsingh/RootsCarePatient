package com.rootscare.ui.paymenthistory

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.patientpaymenthistoryreuest.PatientPaymentHistoryRequest
import com.rootscare.data.model.api.request.patientreviewandratingrequest.PatientReviewAndRatingRequest
import com.rootscare.ui.base.BaseViewModel
import com.rootscare.ui.patientbookpaynow.FragmentPatientbookPayNowNavigator

class FragmentPaymentHistoryViewModel : BaseViewModel<FragmentPaymentHistoryNavigator>() {

    fun apipatientpaymenthistory(patientPaymentHistoryRequest: PatientPaymentHistoryRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apipatientpaymenthistory(patientPaymentHistoryRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successPatientPaymentHistoryResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result!= null){
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorPatientPaymentHistoryResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

}