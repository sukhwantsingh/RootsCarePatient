package com.rootscare.ui.hospital.hospitallistdetails

import com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.HospitalDetailsResponse

interface FragmentHospitalListDetailsNavigator {
    fun successNurseDetailsResponse(nurseDetailsResponse: HospitalDetailsResponse?)

    fun errorNurseDetailsResponse(throwable: Throwable?)
}