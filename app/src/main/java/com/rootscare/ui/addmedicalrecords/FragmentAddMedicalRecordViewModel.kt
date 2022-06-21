package com.rootscare.ui.addmedicalrecords

import android.util.Log
import com.google.gson.Gson
import com.rootscare.ui.appointment.FragmentAppointmentNavigator
import com.rootscare.ui.base.BaseViewModel
import io.reactivex.disposables.Disposable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.ArrayList

class FragmentAddMedicalRecordViewModel : BaseViewModel<FragmentAddMedicalRecordNavigator>() {


    fun apiinsertmedicalrecord(
        user_id: RequestBody,
        date: RequestBody,
        title: RequestBody,
        files: ArrayList<MultipartBody.Part>
    ) {
//        userId: RequestBody,first_name: RequestBody,last_name: RequestBody,id_number: RequestBody,status: RequestBody,image: MultipartBody.Part? = null
//        val body = RequestBody.create(MediaType.parse("application/json"), "") \email: RequestBody, phone_number: RequestBody,email,phone_number,
        var disposable: Disposable? = null
        disposable = apiServiceWithGsonFactory.apiinsertmedicalrecord(user_id, date, title, files)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successMedicalFileDeleteResponse(response)
                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorMedicalFileDeleteResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })
        compositeDisposable.add(disposable!!)
    }
}