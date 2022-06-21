package com.rootscare.ui.appointment.subfragment

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.appointmentdetailsrequest.AppointmentDetailsRequest
import com.rootscare.data.model.api.request.doctorrequest.doctordetailsrequest.DoctorDetailsRequest
import com.rootscare.ui.appointment.FragmentAppointmentNavigator
import com.rootscare.ui.base.BaseViewModel

class FragmentAppiontmentDetailsViewModel : BaseViewModel<FragmentAppiontmentDetailsNavigator>() {
    fun apiappointmentdetails(appointmentDetailsRequest: AppointmentDetailsRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apiappointmentdetails(appointmentDetailsRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successDoctorAppointmentResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result!= null){
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorDoctorAppointmentResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
}