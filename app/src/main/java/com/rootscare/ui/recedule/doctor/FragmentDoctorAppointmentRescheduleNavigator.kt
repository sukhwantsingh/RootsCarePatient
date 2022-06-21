package com.rootscare.ui.recedule.doctor

import com.google.gson.JsonObject
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.DoctorPrivateSlotResponse
import com.rootscare.data.model.api.response.reschedule.doctorreschedule.DoctorRescheduleResponse

interface FragmentDoctorAppointmentRescheduleNavigator {
    fun successDoctorPrivateSlotResponse(doctorPrivateSlotResponse: DoctorPrivateSlotResponse?)
    fun successDoctorRescheduleResponse(doctorRescheduleResponse: DoctorRescheduleResponse?)
    fun successPushNotification(response: JsonObject)
    fun errorGetPatientFamilyListResponse(throwable: Throwable?)
}