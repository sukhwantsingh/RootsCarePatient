package com.rootscare.ui.seealldoctorbygridHospital

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.doctorrequest.doctorlistbydepartmentrequest.DoctorListByDepartmentIdRequest
import com.rootscare.data.model.api.request.doctorrequest.doctorsearchrequest.SeeAllDoctorSearch
import com.rootscare.data.model.api.request.hospital.DoctorRequest
import com.rootscare.ui.base.BaseViewModel

class FragmentSeeAllDoctorHospitalByGridViewModel :
    BaseViewModel<FragmentSeeAllDoctorHospitalByGridNavigator>() {
    fun apidepartmentlist() {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apidepartmentlist()
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successDoctorDepartmentListingResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result != null) {
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorDoctorDepartmentListingResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiDoctorList(homeSearchRequestRequestBody: DoctorRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable =
            apiServiceWithGsonFactory.apidoctorlistviahospital(homeSearchRequestRequestBody)
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        // Store last login time
                        Log.d("check_response", ": " + Gson().toJson(response))
                        navigator.successAllDoctorListingResponse(response)
                        /* Saving access token after singup or login */
                        if (response.result != null) {
                        }

                    } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorDoctorDepartmentListingResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })

        compositeDisposable.add(disposable)
    }

    fun apidepartmentdoctorlist(doctorListByDepartmentIdRequestBody: DoctorListByDepartmentIdRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable =
            apiServiceWithGsonFactory.apidepartmentdoctorlist(doctorListByDepartmentIdRequestBody)
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        // Store last login time
                        Log.d("check_response", ": " + Gson().toJson(response))
                        navigator.successAllDoctorListingResponse(response)
                        /* Saving access token after singup or login */
                        if (response.result != null) {
                        }

                    } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorDoctorDepartmentListingResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })

        compositeDisposable.add(disposable)
    }

    //Doctor Search Api Call
    fun apiSearchDoctor(seeAllDoctorSearchRequestBody: SeeAllDoctorSearch) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apiSearchHospitalDoctor(seeAllDoctorSearchRequestBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successAllDoctorListingResponse(response)
                    /* Saving access token after sign up or login */
//                    if (response.result != null) {
//                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorDoctorDepartmentListingResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
}