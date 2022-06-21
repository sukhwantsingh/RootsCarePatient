package com.rootscare.ui.newaddition.appointments

import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.data.model.api.response.videoPushResponse.VideoPushResponse
import com.rootscare.ui.newaddition.appointments.models.ModelRescheduleDetail
import com.rootscare.ui.newaddition.providerlisting.models.ModelNetworkTimeSlots
import com.rootscare.ui.newaddition.providerlisting.models.ModelTImeSlotsForDoctor

interface AppointmentNavigator {
    fun onRescheduleDetails(response: ModelRescheduleDetail?, position: Int) {}
    fun onUpdateReschedule(response: ModelRescheduleDetail?) {}
    fun onAppointmentDetail(response: ModelAppointmentDetails?) {}
    fun onSuccessResponse(response: ModelAppointmentsListing?) {}
    fun onSuccessRating(response: CommonResponse?) {}
    fun onSuccessBookingTimeSlots(response: ModelNetworkTimeSlots?) {}
    fun onSuccessBookingTimeSlots(response: ModelTImeSlotsForDoctor?){}
    fun successVideoPushResponse(videoPushResponse: VideoPushResponse?){}
    fun errorInApi(throwable: Throwable?)
    fun errorInRecheduleApi(throwable: Throwable?)

}