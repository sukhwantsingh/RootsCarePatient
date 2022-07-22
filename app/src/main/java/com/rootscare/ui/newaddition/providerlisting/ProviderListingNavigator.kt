package com.rootscare.ui.newaddition.providerlisting

import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.ui.newaddition.providerlisting.models.*
import com.rootscare.ui.supportmore.models.ModelIssueTypes

interface ProviderListingNavigator {
    fun onSuccessProviderDetails(response: ModelProviderDetails?){}
    fun onSuccessProviderListing(response: ModelProviderListing?){}
    fun onSuccessProviderListing(response: NetworkHospitalListing?){}
    fun onSuccessInitialData(response: ModelBookingInitialData?){}
    fun onSuccessInitialData(response: ModelBookingIntialForDoctor?){}
    fun onSuccessBookingTimeSlots(response: ModelNetworkTimeSlots?){}
    fun onSuccessBookingTimeSlots(response: ModelTImeSlotsForDoctor?){}
    fun onSuccessBooking(response: CommonResponse?){}
    fun onSuccessSpeciality(specialityList: ModelIssueTypes?){}

    fun errorInAPi(throwable: Throwable?)


    fun successGetPatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?){}
    fun successDeletePatientFamilyListResponse(getPatientFamilyListResponse: GetPatientFamilyListResponse?){}

}