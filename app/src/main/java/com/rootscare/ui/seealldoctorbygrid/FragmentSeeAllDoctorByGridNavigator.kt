package com.rootscare.ui.seealldoctorbygrid

import com.rootscare.data.model.api.response.doctorallapiresponse.alldoctorlistingresponse.AllDoctorListingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse

interface FragmentSeeAllDoctorByGridNavigator {

    fun successDoctorDepartmentListingResponse(doctorDepartmentListingResponse: DoctorDepartmentListingResponse?)
    fun successAllDoctorListingResponse(allDoctorListingResponse: AllDoctorListingResponse?)
    fun errorDoctorDepartmentListingResponse(throwable: Throwable?)
}