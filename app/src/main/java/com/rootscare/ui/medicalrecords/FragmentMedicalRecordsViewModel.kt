package com.rootscare.ui.medicalrecords

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.request.medicalrecorddeleterequest.MedicalFileDeleteRequest
import com.rootscare.data.model.api.request.medicalrecordsrequest.GetMedicalRecordListRequest
import com.rootscare.ui.base.BaseViewModel
import com.rootscare.ui.login.LoginNavigator

class FragmentMedicalRecordsViewModel: BaseViewModel<FragmentMedicalRecordsNavigator>() {
    fun apipatientmedicalrecord(getMedicalRecordListRequest: GetMedicalRecordListRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apipatientmedicalrecord(getMedicalRecordListRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successMedicalRecordListResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result!= null){
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorMedicalRecordListResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apideletepatientmedicalrecord(medicalFileDeleteRequest: MedicalFileDeleteRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apideletepatientmedicalrecord(medicalFileDeleteRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successMedicalFileDeleteResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result!= null){
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorMedicalRecordListResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
}