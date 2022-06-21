package com.rootscare.ui.myappointment

import com.google.gson.JsonObject
import com.rootscare.data.model.api.response.appointcancelresponse.AppointmentCancelResponse
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentHistoryResponse
import com.rootscare.data.model.api.response.appointmenthistoryresponse.Result
import com.rootscare.data.model.api.response.videoPushResponse.VideoPushResponse

interface FragmentMyAppointmentNavigator {

    fun successAppointmentHistoryResponse(appointmentHistoryResponse: Result?)
    fun successAppointmentCancelResponse(
        appointmentCancelResponse: AppointmentCancelResponse?, userId: String?, userType: String?
    )

    fun errorAppointmentHistoryResponse(throwable: Throwable?)
    fun successVideoPushResponse(videoPushResponse: VideoPushResponse)
    fun successPushNotification(response: JsonObject)
    fun errorVideoPushResponse(throwable: Throwable?)
}