package com.rootscare.ui.nurses.addpatient

import android.util.Log
import com.google.gson.Gson
import com.rootscare.ui.base.BaseViewModel
import com.rootscare.ui.bookingappointment.subfragment.FragmentAddPatientForDoctorBookingNavigator
import io.reactivex.disposables.Disposable
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FragmentNurseAddPatientViewModel : BaseViewModel<FragmentNurseAddPatientNavigator>() {
    fun apiinsertpatientfamily(userId: RequestBody, first_name: RequestBody, last_name: RequestBody, image: MultipartBody.Part? = null, gender: RequestBody, age: RequestBody) {

//        email: RequestBody,phone_number: RequestBody,
//        userId: RequestBody,first_name: RequestBody,last_name: RequestBody,id_number: RequestBody,status: RequestBody,image: MultipartBody.Part? = null
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        var disposable: Disposable? = null
        if (image != null) {
//            email,phone_number
            disposable = apiServiceWithGsonFactory.apiinsertpatientfamily(userId,first_name,last_name,image,gender,age)
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        // Store last login time
                        Log.d("check_response", ": " + Gson().toJson(response))
                        navigator.successAddPatientResponse(response)
                    } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorAddPatientResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })
        }
        compositeDisposable.add(disposable!!)
    }
}