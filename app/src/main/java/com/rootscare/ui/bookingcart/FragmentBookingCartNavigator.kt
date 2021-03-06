package com.rootscare.ui.bookingcart

import com.google.gson.JsonObject
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.checkoutdoctorbookingresponse.CheckoutDoctorBookingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse.BookingCartResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse.cartitemdeleteresponse.DoctorCartItemDeleteResponse
import com.rootscare.ui.bookingcart.models.ModelPatientCartNew

interface FragmentBookingCartNavigator {
    fun successBookingCartResponse(response: ModelPatientCartNew?)
    fun successDoctorCartItemDeleteResponse(doctorCartItemDeleteResponse: DoctorCartItemDeleteResponse?)
    fun onAfterPaymentSuccess(response: CommonResponse?){}
    fun errorBookingCartResponse(throwable: Throwable?)
}