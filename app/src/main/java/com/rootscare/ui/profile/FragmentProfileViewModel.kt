package com.rootscare.ui.profile

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.patientprofilerequest.PatientProfileRequest
import com.rootscare.data.model.api.request.patientprofilerequest.updateprofilelifestylerequest.ProfileLifestyleUpdateRequest
import com.rootscare.data.model.api.request.patientprofilerequest.updateprofilemedicalrequest.ProfileMedicalUpdateRequest
import com.rootscare.ui.base.BaseViewModel
import io.reactivex.disposables.Disposable
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FragmentProfileViewModel : BaseViewModel<FragmentProfileNavigator>() {

    fun apipatientprofile(patientProfileRequest: PatientProfileRequest) {
        val disposable = apiServiceWithGsonFactory.apipatientprofile(patientProfileRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.successPatientProfileResponse(response)
                }
            }, { throwable ->
                run {
                    navigator.errorPatientProfileResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apieditpatientprofilemedical(profileMedicalUpdateRequest: ProfileMedicalUpdateRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable =
            apiServiceWithGsonFactory.apieditpatientprofilemedical(profileMedicalUpdateRequest)
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        // Store last login time
                        Log.d("check_response", ": " + Gson().toJson(response))
                        navigator.successPatientProfileResponse(response)

                    } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorPatientProfileResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })

        compositeDisposable.add(disposable)
    }

    fun apieditpatientprofilestyle(profileLifestyleUpdateRequest: ProfileLifestyleUpdateRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable =
            apiServiceWithGsonFactory.apieditpatientprofilestyle(profileLifestyleUpdateRequest)
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        // Store last login time
                        Log.d("check_response", ": " + Gson().toJson(response))
                        navigator.successPatientProfileResponse(response)

                    } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorPatientProfileResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })

        compositeDisposable.add(disposable)
    }


    fun apiEditPatientProfilePersonal(
        userId: RequestBody,
        first_name: RequestBody,
        mobile: RequestBody,
        id_number: RequestBody,
        image: MultipartBody.Part? = null,
        mDOb: RequestBody,
        address: RequestBody,
        gender: RequestBody,
        nationality: RequestBody,  work_area: RequestBody
    ) {
        var disposable: Disposable? = null
        if (image != null) {
            disposable = apiServiceWithGsonFactory.apiEditPatientProfilePersonal(
                userId, first_name, mobile, id_number, mDOb, address, gender, nationality,work_area).subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                       navigator.successPatientProfileResponse(response)
                    }
                }, { throwable ->
                    run {
                        navigator.errorPatientProfileResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })
        }
        compositeDisposable.add(disposable!!)
    }

    fun apinationality(req: RequestBody?) {
        val disposable = apiServiceWithGsonFactory.apinationality(req)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.successNationalityResponse(response)
                  } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorPatientProfileResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apieditpatientprofileimage(userId: RequestBody, image: MultipartBody.Part? = null) {
        var disposable: Disposable? = null
        if (image != null) {
            disposable = apiServiceWithGsonFactory.apieditpatientprofileimage(userId, image)
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        navigator.successPatientProfileResponse(response)
                    }
                }, { throwable ->
                    run {
                        navigator.errorPatientProfileResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })
        }
        compositeDisposable.add(disposable!!)
    }
    fun apiServiceFor(reqBody: RequestBody? = null) {
        val disposable = apiServiceWithGsonFactory.apiServiceFor()
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (null != response) {
                    Log.d("check_response", ": $response")
                    navigator.onSuccessServiceFor(response)
                }
            }, { throwable ->
                run {
                    navigator.errorPatientProfileResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
}