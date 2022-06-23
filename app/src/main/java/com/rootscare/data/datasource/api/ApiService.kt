package com.rootscare.data.datasource.api


import com.google.gson.JsonObject
import com.model.ModelServiceFor
import com.rootscare.data.model.api.request.CommonNotificationIdRequest
import com.rootscare.data.model.api.request.CommonUserIdRequest
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.request.cartitemdeleterequest.CartItemDeleteRequest
import com.rootscare.data.model.api.request.deletepatientfamilymemberrequest.DeletePatientFamilyMemberRequest
import com.rootscare.data.model.api.request.doctorrequest.bookingcartrequests.BookingCartRequest
import com.rootscare.data.model.api.request.doctorrequest.doctordetailsrequest.DoctorDetailsRequest
import com.rootscare.data.model.api.request.doctorrequest.doctorprivatesotrequest.DoctorPrivateSlotRequest
import com.rootscare.data.model.api.request.doctorrequest.getpatientfamilymemberrequest.GetPatientFamilyMemberRequest
import com.rootscare.data.model.api.request.forgotpassword.forgotpasswordchangerequest.ForgotPasswordChangeRequest
import com.rootscare.data.model.api.request.forgotpassword.forgotpasswordemailrequest.ForgotPasswordSendEmailRequest
import com.rootscare.data.model.api.request.home.HomeRequest
import com.rootscare.data.model.api.request.home.HomeSearchRequest
import com.rootscare.data.model.api.request.loginrequest.LoginRequest
import com.rootscare.data.model.api.request.patientprofilerequest.PatientProfileRequest
import com.rootscare.data.model.api.request.patientprofilerequest.updateprofilelifestylerequest.ProfileLifestyleUpdateRequest
import com.rootscare.data.model.api.request.patientprofilerequest.updateprofilemedicalrequest.ProfileMedicalUpdateRequest
import com.rootscare.data.model.api.request.patientreviewandratingrequest.PatientReviewAndRatingRequest
import com.rootscare.data.model.api.request.registrationrequest.registrationotpcheckrequest.RegistrationCheckOTPRequest
import com.rootscare.data.model.api.request.registrationrequest.registrationotpsendrequest.RegistrationSendOTPRequest
import com.rootscare.data.model.api.request.twilio.TwilioAccessTokenRequest
import com.rootscare.data.model.api.request.videoPushRequest.VideoPushRequest
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.data.model.api.response.NotificationCountResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.addpatientresponse.AddPatientResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse.cartitemdeleteresponse.DoctorCartItemDeleteResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.data.model.api.response.forgotpasswordresponse.forgotpasswordchangepassword.ForgotPasswordChangePasswordResponse
import com.rootscare.data.model.api.response.forgotpasswordresponse.forgotpasswordsendmailresponse.ForgotPasswordSendMailResponse
import com.rootscare.data.model.api.response.medicalrecordresponse.MedicalRecordListResponse
import com.rootscare.data.model.api.response.nationalityresponse.NationalityResponse
import com.rootscare.data.model.api.response.patientprofileresponse.PatientProfileResponse
import com.rootscare.data.model.api.response.patientreviewandratingresponse.PatientReviewAndRatingResponse
import com.rootscare.data.model.api.response.registrationresponse.registrationcheckotpresponse.RegistrationCheckOTPResponse
import com.rootscare.data.model.api.response.registrationresponse.registrationsendotpresponse.RegistrationSendOTPResponse
import com.rootscare.data.model.api.response.twilio.TwilioAccessTokenResponse
import com.rootscare.data.model.api.response.videoPushResponse.VideoPushResponse
import com.rootscare.ui.bookingcart.models.ModelPatientCartNew
import com.rootscare.ui.home.model.ModelUpdateCurrentLocation
import com.rootscare.ui.login.ModelLoginResponse
import com.rootscare.ui.newaddition.appointments.ModelAppointmentDetails
import com.rootscare.ui.newaddition.appointments.ModelAppointmentsListing
import com.rootscare.ui.newaddition.appointments.models.ModelRescheduleDetail
import com.rootscare.ui.newaddition.providerlisting.models.*
import com.rootscare.ui.notification.models.ModelNotificationResponse
import com.rootscare.ui.notification.models.ModelUpdateRead
import com.rootscare.ui.splash.model.NetworkAppCheck
import com.rootscare.ui.supportmore.models.ModelIssueTypes
import com.rootscare.ui.utilitycommon.NeedSupportRequest
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    //    @Headers({
    //            "x-api-key: 123456"
    //    })

//    @GET("api-product-list")
//    fun getOurProductList(): Single<OurProductListResponse>


    @GET("api-medical-service-area")
    fun apiServiceFor(): Single<ModelServiceFor>

    @POST("api-patient-registration-otp-send")
    fun apipatientregistrationotpsend(@Body registrationRequestBody: RegistrationSendOTPRequest): Single<RegistrationSendOTPResponse>

    @POST("api-patient-registration-otp-check")
    fun apipatientregistrationotpcheck(@Body registrationRequestBody: RegistrationCheckOTPRequest): Single<RegistrationCheckOTPResponse>

    @POST("api-patient-login")
    fun apipatientlogin(@Body loginRequestBody: LoginRequest): Single<ModelLoginResponse>

    @POST("api-forgot-password-email")
    fun apiforgotpasswordemail(@Body forgotPasswordSendEmailRequestBody: ForgotPasswordSendEmailRequest): Single<ForgotPasswordSendMailResponse>

    @POST("api-forgot-change-password")
    fun apiforgotchangepassword(@Body forgotPasswordChangeRequestBody: ForgotPasswordChangeRequest): Single<ForgotPasswordChangePasswordResponse>

    @POST("api-patient-profile")
    fun apipatientprofile(@Body patientProfileRequestBody: PatientProfileRequest): Single<PatientProfileResponse>

    @POST("api-edit-patient-profile-medical")
    fun apieditpatientprofilemedical(@Body ProfileMedicalUpdateRequestBody: ProfileMedicalUpdateRequest): Single<PatientProfileResponse>

    @POST("api-edit-patient-profile-style")
    fun apieditpatientprofilestyle(@Body ProfileLifestyleUpdateRequestBody: ProfileLifestyleUpdateRequest): Single<PatientProfileResponse>


    @Multipart
    @POST("api-edit-patient-profile-personal")
    fun apiEditPatientProfilePersonal(
        @Part("user_id") user_id: RequestBody,
        @Part("first_name") fullname: RequestBody,
        @Part("phone_number") phone_number: RequestBody,
        @Part("id_number") id_number: RequestBody,
        @Part("dob") mDOb: RequestBody,
        @Part("address") address: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("nationality") nationality: RequestBody,
        @Part("work_area") work_area: RequestBody
    ): Single<PatientProfileResponse>

//    @Part("status") status: RequestBody,    @Part image: MultipartBody.Part,

    @POST("api-patient-family-member")
    fun apipatientfamilymember(@Body getPatientFamilyMemberRequestBody: GetPatientFamilyMemberRequest): Single<GetPatientFamilyListResponse>

    @Multipart
    @POST("api-insert-patient-family")
    fun apiinsertpatientfamily(
        @Part("patient_id") patient_id: RequestBody,
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("gender") gender: RequestBody,
        @Part("age") age: RequestBody
    ): Single<AddPatientResponse>

    //    @Part("email") email: RequestBody,
//    @Part("phone_number") phone_number: RequestBody,

    @POST("api-patient-review")
    fun apipatientreview(@Body req: PatientReviewAndRatingRequest): Single<PatientReviewAndRatingResponse>

    @POST("api-delete-patient-family-member")
    fun apideletepatientfamilymember(@Body req: DeletePatientFamilyMemberRequest): Single<GetPatientFamilyListResponse>

    @POST("api-getnationality")
    fun apinationality(@Body req: RequestBody?): Single<NationalityResponse>

    @Multipart
    @POST("api-insert-medical-record")
    fun apiinsertmedicalrecord(
        @Part("user_id") user_id: RequestBody,
        @Part("date") date: RequestBody,
        @Part("title") title: RequestBody,
        @Part file: ArrayList<MultipartBody.Part>
    ): Single<MedicalRecordListResponse>


    @Multipart
    @POST("api-edit-patient-profile-image")
    fun apieditpatientprofileimage(
        @Part("user_id") user_id: RequestBody,
        @Part image: MultipartBody.Part
    ): Single<PatientProfileResponse>

    @POST("api-get-video-access-token")
    fun getAccessTokenForVideo(@Body request: TwilioAccessTokenRequest): Single<TwilioAccessTokenResponse>

    @POST("api-get-video-access-token-with-push-notification")
    fun sendVideoPushNotification(@Body request: VideoPushRequest): Single<VideoPushResponse>

//    @POST("api-status-update-call-disconnects")
//    fun disconnectCall(@Field("appcall") St appCall): Single<JsonObject>

    @FormUrlEncoded
    @POST("api-status-update-call-disconnects")
    fun disconnectCall(
        @Field("status") appCall: String, @Field("order_id") orderId: String
    ): Single<JsonObject?>?

    @POST("api-logout")
    fun apiLogoutUser(@Body appointmentRequestBody: AppointmentRequest): Single<CommonResponse>

    // New addition api's
    @POST("api-insert-need-help")
    fun apiToSubmitNeedSupport(@Body requestBody: NeedSupportRequest): Single<CommonResponse>

    @POST("api-patient-need-help-topic")
    fun getHelpTopics(@Body requestBody: RequestBody?): Single<ModelIssueTypes>

    @POST("api-patient-address-update")
    fun apiUpdateCurrentLocation(@Body reqBody: RequestBody): Single<ModelUpdateCurrentLocation>

    @POST("api-patient-service-provider-list")
    fun apiProviderListing(@Body reqBody: RequestBody?): Single<ModelProviderListing>

    @POST("api-patient-service-provider-details")
    fun apiProviderDetails(@Body reqBody: RequestBody?): Single<ModelProviderDetails>

    // Appointment new api's
    @POST("api-patient-all-appointment")
    fun apiAppointmentAll(@Body reqBody: RequestBody): Single<ModelAppointmentsListing>

    @POST("api-patient-today-appointment")
    fun apiAppointmentOngoing(@Body reqBody: RequestBody): Single<ModelAppointmentsListing>

    @POST("api-patient-upcoming-appointment")
    fun apiAppointmentUpcoming(@Body reqBody: RequestBody): Single<ModelAppointmentsListing>

    @POST("api-patient-past-appointment")
    fun apiAppointmentPast(@Body reqBody: RequestBody): Single<ModelAppointmentsListing>


    @POST("api-patient-reschedule-appointment")
    fun apiReschedule(@Body reqBody: RequestBody): Single<ModelRescheduleDetail>

    @POST("api-patient-update-reschedule-appointment")
    fun apiUpdateReschedule(@Body reqBody: RequestBody): Single<ModelRescheduleDetail>


    @POST("api-patient-doctor-reschedule-appointment")
    fun apiRescheduleForDoc(@Body reqBody: RequestBody): Single<ModelRescheduleDetail>

    @POST("api-patient-update-doctor-reschedule-appointment")
    fun apiUpdateRescheduleForDoc(@Body reqBody: RequestBody): Single<ModelRescheduleDetail>


    @POST("api-patient-appointment-details")
    fun getAppointmentDetails(@Body req: RequestBody): Single<ModelAppointmentDetails>

    @POST("api-patient-insert-review")
    fun apiInserRating(@Body req: RequestBody): Single<CommonResponse>

    @POST("api-get-all-notification")
    fun apiUserNotification(@Body commonUserIdRequestBody: CommonUserIdRequest): Single<ModelNotificationResponse>

    @POST("api-update-notification")
    fun apiUpdateNotification(@Body reqBody: CommonNotificationIdRequest): Single<ModelUpdateRead>

    @POST("api-patient-booking-init-details")
    fun apiBookingInitialData(@Body reqBody: RequestBody?): Single<ModelBookingInitialData>

    @POST("api-patient-doctor-booking-init-details")
    fun apiBookingInitialDataForDoctor(@Body reqBody: RequestBody?): Single<ModelBookingIntialForDoctor>

    @POST("api-patient-next-date-time")
    fun apiBookingTimeSlots(@Body reqBody: RequestBody?): Single<ModelNetworkTimeSlots>

    @POST("api-patient-doctor-next-date-time")
    fun apiBookingTimeSlotsForDoctor(@Body reqBody: RequestBody?): Single<ModelTImeSlotsForDoctor>

    @POST("api-patient-insert-cart")
    fun apiBookAppointment(@Body reqBody: RequestBody?): Single<CommonResponse>

    @POST("api-patient-cart-details")
    fun apipatientcart(@Body bookingCartRequestBody: BookingCartRequest): Single<ModelPatientCartNew>

    @POST("api-patient-remove-cart")
    fun apideletepatientcart(@Body req: CartItemDeleteRequest): Single<DoctorCartItemDeleteResponse>

    @POST("api-patient-insert-appointment")
    fun apiAfterPaymentSuccess(@Body req: RequestBody?): Single<CommonResponse>

    @POST("api-notification-count")
    fun apiNotificationUnreadCounts(@Body req: RequestBody?): Single<NotificationCountResponse>

    @POST("api-language-update")
    fun apiChangelanguage(@Body req: RequestBody?): Single<CommonResponse>

    @Multipart
    @POST("api-patient-insert-cart") // needs to change after testing
    fun apiBookAppointmentForDoctor(
        @Part("service_type") service_type: RequestBody?,
        @Part("currency_symbol") currency: RequestBody?,
        @Part("login_user_id") login_user_id: RequestBody?,
        @Part("family_member_id") family_member_id: RequestBody?,
        @Part("provider_id") provider_id: RequestBody?,
        @Part("task_id") task_id: RequestBody?,
        @Part("task_type") task_type: RequestBody?,
        @Part("appointment_type") appointment_type: RequestBody?,
        @Part("from_date") from_date: RequestBody?,
        @Part("from_time") from_time: RequestBody?,
        @Part("task_price") task_price: RequestBody?,
        @Part("task_price_total") task_price_total: RequestBody?,
        @Part("vat_price") vat_price: RequestBody?,
        @Part("vat_percent_used") vat_percent_used: RequestBody?,
        @Part("distance_fare") distance_fare: RequestBody?,
        @Part("sub_total_price") sub_total_price: RequestBody?,
        @Part("total_price") total_price: RequestBody?,
        @Part("symptom_text") symptom_text: RequestBody?,

        @Part symptom_recording: MultipartBody.Part?,
        @Part upload_prescription: MultipartBody.Part?
    ): Single<CommonResponse>

    @GET("api-android-patient-update")
    fun apiVersionCheck(): Single<NetworkAppCheck>



}
