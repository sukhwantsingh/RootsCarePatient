package com.rootscare.ui.appointment.subfragment

import com.rootscare.data.model.api.response.appointmentdetails.DoctorAppointmentResponse

interface FragmentAppiontmentDetailsNavigator {
    fun successDoctorAppointmentResponse(doctorAppointmentResponse: DoctorAppointmentResponse?)
    fun errorDoctorAppointmentResponse(throwable: Throwable?)
}