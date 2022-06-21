package com.rootscare.ui.caregiver.caregiverlistingdetails

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.nurse.departmentnurselist.DepartmentNurseListRequest
import com.rootscare.data.model.api.request.nurse.nursedetailsrequest.NurseDetailsRequest
import com.rootscare.data.model.api.request.nurse.nurseslots.NurseSlotRequest
import com.rootscare.ui.base.BaseViewModel
import com.rootscare.ui.nurses.FragmentNursesListByGridNavigator

class FragmentCaregiverUpdateListingDetailsViewModel : BaseViewModel<FragmentCaregiverUpdateListingDetailsNavigator>() {

    fun apicaregiverdetails(nurseDetailsRequest: NurseDetailsRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apicaregiverdetails(nurseDetailsRequest)
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

    fun taskbasedslots(nurseSlotRequestBody: NurseSlotRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.taskbasedslots(nurseSlotRequestBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successNueseViewTimingsResponse(response)
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