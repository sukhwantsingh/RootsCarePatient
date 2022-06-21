package com.rootscare.ui.medicalrecords

import com.rootscare.data.model.api.response.appointcancelresponse.AppointmentCancelResponse
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentHistoryResponse
import com.rootscare.data.model.api.response.medicalfiledeleteresponse.MedicalFileDeleteResponse
import com.rootscare.data.model.api.response.medicalrecordresponse.MedicalRecordListResponse

interface FragmentMedicalRecordsNavigator {
    fun successMedicalRecordListResponse(medicalRecordListResponse: MedicalRecordListResponse?)
    fun successMedicalFileDeleteResponse(medicalFileDeleteResponse: MedicalFileDeleteResponse?)
    fun errorMedicalRecordListResponse(throwable: Throwable?)
}