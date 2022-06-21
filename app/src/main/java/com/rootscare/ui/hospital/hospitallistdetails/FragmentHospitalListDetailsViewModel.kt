package com.rootscare.ui.hospital.hospitallistdetails

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.hospital.HospitalRequest
import com.rootscare.data.model.api.request.nurse.nursedetailsrequest.NurseDetailsRequest
import com.rootscare.data.model.api.request.nurse.nurseslots.NurseSlotRequest
import com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.HospitalDetailsResponse
import com.rootscare.ui.base.BaseViewModel

class FragmentHospitalListDetailsViewModel : BaseViewModel<FragmentHospitalListDetailsNavigator>() {

    fun apinursedetails(nurseDetailsRequest: HospitalRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apihospinursedetails(nurseDetailsRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successNurseDetailsResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result!= null){
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorNurseDetailsResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

}