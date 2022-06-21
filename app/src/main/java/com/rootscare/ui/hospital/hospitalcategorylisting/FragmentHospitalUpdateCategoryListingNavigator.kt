package com.rootscare.ui.hospital.hospitalcategorylisting

import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.allhospitalistingresponse.AllHospitalListingResponse
import com.rootscare.data.model.api.response.nurses.nurselist.GetNurseListResponse

interface FragmentHospitalUpdateCategoryListingNavigator {
    fun successGetNurseListResponse(getNurseListResponse: AllHospitalListingResponse?)
    fun successDoctorDepartmentListingResponse(doctorDepartmentListingResponse: DoctorDepartmentListingResponse?)
    fun errorGetNurseListResponse(throwable: Throwable?)
}