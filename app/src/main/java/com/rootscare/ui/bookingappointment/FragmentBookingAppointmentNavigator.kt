package com.rootscare.ui.bookingappointment

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingresponse.DoctorPrivateBooingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorHomeSlotResponse.DoctorHomeSlotResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.DoctorPrivateSlotResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordetailsresponse.DoctorDetailsResponse

interface FragmentBookingAppointmentNavigator {
    fun successGetPatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?)
    fun successDoctorPrivateSlotResponse(doctorPrivateSlotResponse: DoctorPrivateSlotResponse?)
    fun successDoctorHomeVisitSlotResponse(doctorHomeSlotResponse: DoctorHomeSlotResponse?)
    fun successDoctorDetailsResponse(doctorDetailsResponse: DoctorDetailsResponse?)
    fun successDoctorPrivateBooingResponse(doctorPrivateBooingResponse: DoctorPrivateBooingResponse?)
    fun successDeletePatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?)
    fun errorGetPatientFamilyListResponse(throwable: Throwable?)
}