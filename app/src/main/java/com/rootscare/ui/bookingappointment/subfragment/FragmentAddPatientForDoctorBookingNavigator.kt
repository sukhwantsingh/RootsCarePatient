package com.rootscare.ui.bookingappointment.subfragment

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.addpatientresponse.AddPatientResponse

interface FragmentAddPatientForDoctorBookingNavigator {
    fun successAddPatientResponse(addPatientResponse: AddPatientResponse?)
    fun errorAddPatientResponse(throwable: Throwable?)
}