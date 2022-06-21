package com.rootscare.ui.nurses.nursesbookingappointment

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.data.model.api.response.getpreferredtimeslotresponse.GetPreferredTimeSlotResponse
import com.rootscare.data.model.api.response.getslotsres.GetSlotsResponse
import com.rootscare.data.model.api.response.nurses.nursebookappointment.NurseBookAppointmentResponse
import com.rootscare.data.model.api.response.nurses.nursedetails.NurseDetailsResponse
import com.rootscare.data.model.api.response.nurses.nursehourlyslot.GetNurseHourlySlotResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.NueseViewTimingsResponse
import com.rootscare.data.model.api.response.nurses.taskresponse.GetTaskResponse

interface FragmentNursesBookingAppointmentNavigator {
    fun successGetPatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?)
    fun successDeletePatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?)
    fun successNueseViewTimingsResponse(nueseViewTimingsResponse: NueseViewTimingsResponse?)
    fun successGetNurseHourlySlotResponse(getNurseHourlySlotResponse: GetNurseHourlySlotResponse?)
    fun successNurseDetailsResponse(nurseDetailsResponse: NurseDetailsResponse?)
    fun successNurseTaskResponse(nurseDetailsResponse: GetTaskResponse?)
    fun successNurseBookAppointmentResponse(nurseBookAppointmentResponse: NurseBookAppointmentResponse?)
    fun errorGetPatientFamilyListResponse(throwable: Throwable?)



    fun successGetBookedSlots(getSlotsResponse: GetSlotsResponse)
    fun successGetProviderPreferredTime(getSlotsResponse: GetPreferredTimeSlotResponse)
}