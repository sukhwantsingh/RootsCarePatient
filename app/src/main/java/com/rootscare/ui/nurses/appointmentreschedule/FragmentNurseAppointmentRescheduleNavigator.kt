package com.rootscare.ui.nurses.appointmentreschedule

import com.google.gson.JsonObject
import com.rootscare.data.model.api.response.getpreferredtimeslotresponse.GetPreferredTimeSlotResponse
import com.rootscare.data.model.api.response.getslotsres.GetSlotsResponse
import com.rootscare.data.model.api.response.nurses.nursehourlyslot.GetNurseHourlySlotResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.NueseViewTimingsResponse
import com.rootscare.data.model.api.response.reschedule.doctorreschedule.DoctorRescheduleResponse

interface FragmentNurseAppointmentRescheduleNavigator {
    fun successNurseViewTimingsResponse(nurseViewTimingsResponse: NueseViewTimingsResponse?)
    fun successGetNurseHourlySlotResponse(getNurseHourlySlotResponse: GetNurseHourlySlotResponse?)
    fun successDoctorRescheduleResponse(doctorRescheduleResponse: DoctorRescheduleResponse?)
    fun errorGetPatientFamilyListResponse(throwable: Throwable?)
    fun onSuccessDetails(response: JsonObject)
    fun successPushNotification(response: JsonObject)
    fun onThrowable(throwable: Throwable)

    fun successGetProviderPreferredTime(getSlotsResponse: GetPreferredTimeSlotResponse)

    fun successGetBookedSlots(getSlotsResponse: GetSlotsResponse)

}