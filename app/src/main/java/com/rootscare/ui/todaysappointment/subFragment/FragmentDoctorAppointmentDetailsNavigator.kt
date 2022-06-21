package com.rootscare.ui.todaysappointment.subFragment

import com.google.gson.JsonObject
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.data.model.api.response.appointcancelresponse.AppointmentCancelResponse
import com.rootscare.data.model.api.response.appointmentdetails.DoctorAppointmentResponse
import com.rootscare.data.model.api.response.videoPushResponse.VideoPushResponse

interface FragmentDoctorAppointmentDetailsNavigator {


    fun onSuccessDetails(response: JsonObject)

    fun onThrowable(throwable: Throwable)

    //    fun successGetDoctorRequestAppointmentUpdateResponse(
//        getDoctorRequestAppointmentResponse: GetDoctorRequestAppointmentResponse?
//    )
    fun errorGetDoctorRequestAppointmentResponse(throwable: Throwable?)

    fun onSuccessMarkAsComplete(response: CommonResponse)

    fun errorGetDoctorTodaysAppointmentResponse(throwable: Throwable?)

    fun onSuccessUploadPrescription(response: CommonResponse)

    fun successAppointmentCancelResponse(appointmentCancelResponse: AppointmentCancelResponse?)

    fun errorAppointmentHistoryResponse(throwable: Throwable?)

    fun successVideoPushResponse(videoPushResponse: VideoPushResponse)

    fun errorVideoPushResponse(throwable: Throwable?)
}