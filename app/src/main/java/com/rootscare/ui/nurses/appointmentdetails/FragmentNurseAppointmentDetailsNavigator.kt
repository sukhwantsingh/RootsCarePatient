package com.rootscare.ui.nurses.appointmentdetails

import com.rootscare.data.model.api.response.appointmentdetails.DoctorAppointmentResponse
import com.rootscare.data.model.api.response.nurses.nurseappointmentdetails.NurseAppointmentDetailsResponse

interface FragmentNurseAppointmentDetailsNavigator {
    fun successDoctorAppointmentResponse(surseAppointmentDetailsResponse: NurseAppointmentDetailsResponse?)
    fun errorDoctorAppointmentResponse(throwable: Throwable?)
}