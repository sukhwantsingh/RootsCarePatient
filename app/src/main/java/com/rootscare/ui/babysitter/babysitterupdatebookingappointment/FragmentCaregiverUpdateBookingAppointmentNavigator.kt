package com.rootscare.ui.babysitter.babysitterupdatebookingappointment

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingresponse.DoctorPrivateBooingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.data.model.api.response.getpreferredtimeslotresponse.GetPreferredTimeSlotResponse
import com.rootscare.data.model.api.response.getslotsres.GetSlotsResponse
import com.rootscare.data.model.api.response.nurses.nursebookappointment.NurseBookAppointmentResponse
import com.rootscare.data.model.api.response.nurses.nursedetails.NurseDetailsResponse
import com.rootscare.data.model.api.response.nurses.nursehourlyslot.GetNurseHourlySlotResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.NueseViewTimingsResponse

interface FragmentCaregiverUpdateBookingAppointmentNavigator {
    fun successGetPatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?)
    fun successDeletePatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?)
    fun successNueseViewTimingsResponse(nueseViewTimingsResponse: NueseViewTimingsResponse?)
    fun successGetNurseHourlySlotResponse(getNurseHourlySlotResponse: GetNurseHourlySlotResponse?)
    fun successGetProviderPreferredTime(getSlotsResponse: GetPreferredTimeSlotResponse)

    fun successGetBookedSlots(getSlotsResponse: GetSlotsResponse)
    fun successNurseDetailsResponse(nurseDetailsResponse: NurseDetailsResponse?)
    fun successNurseBookAppointmentResponse(nurseBookAppointmentResponse: NurseBookAppointmentResponse?)
    fun errorGetPatientFamilyListResponse(throwable: Throwable?)
}