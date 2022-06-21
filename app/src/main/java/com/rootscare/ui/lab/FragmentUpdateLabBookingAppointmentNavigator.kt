package com.rootscare.ui.lab

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse.BookingCartResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingresponse.DoctorPrivateBooingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.data.model.api.response.getpreferredtimeslotresponse.GetPreferredTimeSlotResponse
import com.rootscare.data.model.api.response.getslotsres.GetSlotsResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.HospitalDetailsResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.hospitallabresponse.HospitalLabResponse
import com.rootscare.data.model.api.response.nurses.nursebookappointment.NurseBookAppointmentResponse
import com.rootscare.data.model.api.response.nurses.nursedetails.NurseDetailsResponse
import com.rootscare.data.model.api.response.nurses.nursehourlyslot.GetNurseHourlySlotResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.NueseViewTimingsResponse
import com.rootscare.data.model.api.response.nurses.taskresponse.GetTaskResponse

interface FragmentUpdateLabBookingAppointmentNavigator {
    fun successGetPatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?)
    fun successDeletePatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?)
    fun successNueseViewTimingsResponse(nueseViewTimingsResponse: NueseViewTimingsResponse?)
    fun successGetNurseHourlySlotResponse(getNurseHourlySlotResponse: GetNurseHourlySlotResponse?)
    fun successNurseDetailsResponse(nurseDetailsResponse: HospitalDetailsResponse?)
    fun successNurseTaskResponse(nurseDetailsResponse: HospitalLabResponse?)
    fun successNurseBookAppointmentResponse(nurseBookAppointmentResponse: NurseBookAppointmentResponse?)
    fun successBookingCartResponse(bookingCartResponse: BookingCartResponse?)
    fun errorBookingCartResponse(throwable: Throwable?)

    fun successGetProviderPreferredTime(getSlotsResponse: GetPreferredTimeSlotResponse)

    fun successGetBookedSlots(getSlotsResponse: GetSlotsResponse)

    fun errorGetPatientFamilyListResponse(throwable: Throwable?)
}