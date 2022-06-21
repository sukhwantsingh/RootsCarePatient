package com.rootscare.ui.nurses.nursescategorylisting

import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.nurses.nurselist.GetNurseListResponse

interface FragmentNursesCategoryListingNavigator {
    fun successGetNurseListResponse(getNurseListResponse: GetNurseListResponse?)
    fun successDoctorDepartmentListingResponse(doctorDepartmentListingResponse: DoctorDepartmentListingResponse?)
    fun errorGetNurseListResponse(throwable: Throwable?)
}