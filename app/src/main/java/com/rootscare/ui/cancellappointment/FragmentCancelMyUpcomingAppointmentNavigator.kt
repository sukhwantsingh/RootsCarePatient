package com.rootscare.ui.cancellappointment

import com.rootscare.data.model.api.response.appointmenthistoryresponse.Result

interface FragmentCancelMyUpcomingAppointmentNavigator {
    fun successAppointmentHistoryResponse(appointmentHistoryResponse: Result?)
    fun errorAppointmentHistoryResponse(throwable: Throwable?)
}