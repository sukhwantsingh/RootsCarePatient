package com.rootscare.ui.nurses

import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.medicalfiledeleteresponse.MedicalFileDeleteResponse
import com.rootscare.data.model.api.response.nurses.nurselist.GetNurseListResponse

interface FragmentNursesListByGridNavigator {
    fun successGetNurseListResponse(getNurseListResponse: GetNurseListResponse?)
    fun successDoctorDepartmentListingResponse(doctorDepartmentListingResponse: DoctorDepartmentListingResponse?)
    fun errorGetNurseListResponse(throwable: Throwable?)
}