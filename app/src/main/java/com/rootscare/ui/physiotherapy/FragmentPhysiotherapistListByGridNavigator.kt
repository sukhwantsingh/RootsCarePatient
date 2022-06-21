package com.rootscare.ui.physiotherapy

import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.medicalfiledeleteresponse.MedicalFileDeleteResponse
import com.rootscare.data.model.api.response.nurses.nurselist.GetNurseListResponse

interface FragmentPhysiotherapistListByGridNavigator {
    fun successGetNurseListResponse(getNurseListResponse: GetNurseListResponse?)
    fun successDoctorDepartmentListingResponse(doctorDepartmentListingResponse: DoctorDepartmentListingResponse?)
    fun errorGetNurseListResponse(throwable: Throwable?)
}