package com.rootscare.ui.physiotherapy.bookingappointment

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse.BookingCartResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.data.model.api.response.getpreferredtimeslotresponse.GetPreferredTimeSlotResponse
import com.rootscare.data.model.api.response.getslotsres.GetSlotsResponse
import com.rootscare.data.model.api.response.nurses.nursebookappointment.NurseBookAppointmentResponse
import com.rootscare.data.model.api.response.nurses.nursedetails.NurseDetailsResponse
import com.rootscare.data.model.api.response.nurses.nursehourlyslot.GetNurseHourlySlotResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.NueseViewTimingsResponse
import com.rootscare.data.model.api.response.nurses.taskresponse.GetTaskResponse

interface FragmentPhysiotherapyBookingAppointmentNavigator {
    fun successGetPatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?)
    fun successDeletePatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?)
    fun successNurseViewTimingsResponse(nurseViewTimingsResponse: NueseViewTimingsResponse?)
    fun successGetNurseHourlySlotResponse(getNurseHourlySlotResponse: GetNurseHourlySlotResponse?)
    fun successNurseDetailsResponse(nurseDetailsResponse: NurseDetailsResponse?)
    fun successNurseTaskResponse(nurseDetailsResponse: GetTaskResponse?)
    fun successNurseBookAppointmentResponse(nurseBookAppointmentResponse: NurseBookAppointmentResponse?)
    fun successBookingCartResponse(bookingCartResponse: BookingCartResponse?)
    fun errorBookingCartResponse(throwable: Throwable?)



    fun successGetProviderPreferredTime(getSlotsResponse: GetPreferredTimeSlotResponse)
    fun successGetBookedSlots(getSlotsResponse: GetSlotsResponse)


    fun errorGetPatientFamilyListResponse(throwable: Throwable?)
}