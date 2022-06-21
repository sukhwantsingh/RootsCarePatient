package com.rootscare.ui.seealldoctorbygridHospital

import com.rootscare.data.model.api.response.doctorallapiresponse.alldoctorlistingresponse.AllDoctorListingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse

interface FragmentSeeAllDoctorHospitalByGridNavigator {

    fun successDoctorDepartmentListingResponse(doctorDepartmentListingResponse: DoctorDepartmentListingResponse?)
    fun successAllDoctorListingResponse(allDoctorListingResponse: AllDoctorListingResponse?)
    fun errorDoctorDepartmentListingResponse(throwable: Throwable?)
}