package com.rootscare.ui.addmedicalrecords


import com.rootscare.data.model.api.response.medicalrecordresponse.MedicalRecordListResponse

interface FragmentAddMedicalRecordNavigator {
    fun successMedicalFileDeleteResponse(medicalFileDeleteResponse: MedicalRecordListResponse?)
    fun errorMedicalFileDeleteResponse(throwable: Throwable?)
}