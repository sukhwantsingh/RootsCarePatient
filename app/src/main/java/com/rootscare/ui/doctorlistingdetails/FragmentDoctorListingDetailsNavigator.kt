package com.rootscare.ui.doctorlistingdetails

import com.rootscare.data.model.api.response.doctorallapiresponse.doctordetailsresponse.DoctorDetailsResponse

interface FragmentDoctorListingDetailsNavigator {
    fun successDoctorDetailsResponse(doctorDetailsResponse: DoctorDetailsResponse?)
    fun errorDoctorDetailsResponse(throwable: Throwable?)
}