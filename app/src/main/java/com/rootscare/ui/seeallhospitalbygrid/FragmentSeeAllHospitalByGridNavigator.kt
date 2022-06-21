package com.rootscare.ui.seeallhospitalbygrid

import com.rootscare.data.model.api.response.doctorallapiresponse.alldoctorlistingresponse.AllDoctorListingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.allhospitalistingresponse.AllHospitalListingResponse

interface FragmentSeeAllHospitalByGridNavigator {

    fun successDoctorDepartmentListingResponse(doctorDepartmentListingResponse: DoctorDepartmentListingResponse?)
    fun successAllDoctorListingResponse(allDoctorListingResponse: AllHospitalListingResponse?)
    fun errorDoctorDepartmentListingResponse(throwable: Throwable?)
}