package com.rootscare.ui.appointment

import com.rootscare.data.model.api.response.appointmenthistoryresponse.Result

interface FragmentAppointmentNavigator {
    fun successAppointmentHistoryResponse(appointmentHistoryResponse: Result?)
    fun errorAppointmentHistoryResponse(throwable: Throwable?)
}