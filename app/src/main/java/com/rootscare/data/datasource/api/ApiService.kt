package com.rootscare.data.datasource.api


import com.google.gson.JsonObject
import com.model.ModelServiceFor
import com.rootscare.data.model.api.request.CommonNotificationIdRequest
import com.rootscare.data.model.api.request.CommonUserIdRequest
import com.rootscare.data.model.api.request.appointmentdetailsrequest.AppointmentDetailsRequest
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.request.cancelappointmentrequest.CancelAppointmentRequest
import com.rootscare.data.model.api.request.cartitemdeleterequest.CartItemDeleteRequest
import com.rootscare.data.model.api.request.checkoutdoctorbookingrequest.CheckoutDoctorBookingRequest
import com.rootscare.data.model.api.request.deletepatientfamilymemberrequest.DeletePatientFamilyMemberRequest
import com.rootscare.data.model.api.request.doctorrequest.bookingcartrequests.BookingCartRequest
import com.rootscare.data.model.api.request.doctorrequest.doctordetailsrequest.DoctorDetailsRequest
import com.rootscare.data.model.api.request.doctorrequest.doctorlistbydepartmentrequest.DoctorListByDepartmentIdRequest
import com.rootscare.data.model.api.request.doctorrequest.doctorprivatesotrequest.DoctorPrivateSlotRequest
import com.rootscare.data.model.api.request.doctorrequest.doctorsearchrequest.SeeAllDoctorSearch
import com.rootscare.data.model.api.request.doctorrequest.getpatientfamilymemberrequest.GetPatientFamilyMemberRequest
import com.rootscare.data.model.api.request.forgotpassword.forgotpasswordchangerequest.ForgotPasswordChangeRequest
import com.rootscare.data.model.api.request.forgotpassword.forgotpasswordemailrequest.ForgotPasswordSendEmailRequest
import com.rootscare.data.model.api.request.getproviderprefferedtime.GetProviderPreferredTimeRequest
import com.rootscare.data.model.api.request.getslotsrequest.GetSlotRequest
import com.rootscare.data.model.api.request.home.HomeRequest
import com.rootscare.data.model.api.request.home.HomeSearchRequest
import com.rootscare.data.model.api.request.hospital.DoctorRequest
import com.rootscare.data.model.api.request.hospital.HospitalNearbyRequest
import com.rootscare.data.model.api.request.hospital.HospitalRequest
import com.rootscare.data.model.api.request.insertdoctorreviewratingrequest.InsertDoctorReviewRatingRequest
import com.rootscare.data.model.api.request.loginrequest.LoginRequest
import com.rootscare.data.model.api.request.medicalrecorddeleterequest.MedicalFileDeleteRequest
import com.rootscare.data.model.api.request.medicalrecordsrequest.GetMedicalRecordListRequest
import com.rootscare.data.model.api.request.nurse.departmentnurselist.DepartmentNurseListRequest
import com.rootscare.data.model.api.request.nurse.hourlyslot.NurseHourlySlotRequest
import com.rootscare.data.model.api.request.nurse.nursedetailsrequest.NurseDetailsRequest
import com.rootscare.data.model.api.request.nurse.nurseslots.NurseSlotRequest
import com.rootscare.data.model.api.request.nurse.nursrtask.PhysiotherapyTask
import com.rootscare.data.model.api.request.nurse.nursrtask.nursetask
import com.rootscare.data.model.api.request.nurse.review.InsertNurseReviewRequest
import com.rootscare.data.model.api.request.nurse.searchbyname.NurseSearchByNameRequest
import com.rootscare.data.model.api.request.patientpaymenthistoryreuest.PatientPaymentHistoryRequest
import com.rootscare.data.model.api.request.patientprofilerequest.PatientProfileRequest
import com.rootscare.data.model.api.request.patientprofilerequest.updateprofilelifestylerequest.ProfileLifestyleUpdateRequest
import com.rootscare.data.model.api.request.patientprofilerequest.updateprofilemedicalrequest.ProfileMedicalUpdateRequest
import com.rootscare.data.model.api.request.patientreviewandratingrequest.PatientReviewAndRatingRequest
import com.rootscare.data.model.api.request.pushNotificationRequest.PushNotificationRequest
import com.rootscare.data.model.api.request.registrationrequest.registrationotpcheckrequest.RegistrationCheckOTPRequest
import com.rootscare.data.model.api.request.registrationrequest.registrationotpsendrequest.RegistrationSendOTPRequest
import com.rootscare.data.model.api.request.reschedule.DoctorAppointmentRescheduleRequest
import com.rootscare.data.model.api.request.twilio.TwilioAccessTokenRequest
import com.rootscare.data.model.api.request.videoPushRequest.VideoPushRequest
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.data.model.api.response.NotificationCountResponse
import com.rootscare.data.model.api.response.appointcancelresponse.AppointmentCancelResponse
import com.rootscare.data.model.api.response.appointmentdetails.DoctorAppointmentResponse
import com.rootscare.data.model.api.response.appointmenthistoryresponse.Result
import com.rootscare.data.model.api.response.caregiverallresponse.allcaregiverlistingresponse.AllCaregiverListingResponse
import com.rootscare.data.model.api.response.caregiverallresponse.caregiverlist.GetCaregiverListResponse
import com.rootscare.data.model.api.response.deactivateaccountresponse.DeactivateAccountResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.alldoctorlistingresponse.AllDoctorListingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.checkoutdoctorbookingresponse.CheckoutDoctorBookingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.addpatientresponse.AddPatientResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse.BookingCartResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse.cartitemdeleteresponse.DoctorCartItemDeleteResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingresponse.DoctorPrivateBooingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorHomeSlotResponse.DoctorHomeSlotResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.DoctorPrivateSlotResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.GetPatientFamilyListResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordetailsresponse.DoctorDetailsResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorreviewsubmitresponse.DoctorReviewRatingSubmiteResponse
import com.rootscare.data.model.api.response.editpatientfamilymemberresponse.EditFamilyMemberResponse
import com.rootscare.data.model.api.response.forgotpasswordresponse.forgotpasswordchangepassword.ForgotPasswordChangePasswordResponse
import com.rootscare.data.model.api.response.forgotpasswordresponse.forgotpasswordsendmailresponse.ForgotPasswordSendMailResponse
import com.rootscare.data.model.api.response.getpreferredtimeslotresponse.GetPreferredTimeSlotResponse
import com.rootscare.data.model.api.response.getslotsres.GetSlotsResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.allhospitalistingresponse.AllHospitalListingResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.HospitalDetailsResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.hospitallabresponse.HospitalLabResponse
import com.rootscare.data.model.api.response.loginresponse.LoginResponse
import com.rootscare.data.model.api.response.medicalfiledeleteresponse.MedicalFileDeleteResponse
import com.rootscare.data.model.api.response.medicalrecordresponse.MedicalRecordListResponse
import com.rootscare.data.model.api.response.nationalityresponse.NationalityResponse
import com.rootscare.data.model.api.response.nurses.nurseappointmentdetails.NurseAppointmentDetailsResponse
import com.rootscare.data.model.api.response.nurses.nursebookappointment.NurseBookAppointmentResponse
import com.rootscare.data.model.api.response.nurses.nursedetails.NurseDetailsResponse
import com.rootscare.data.model.api.response.nurses.nursehourlyslot.GetNurseHourlySlotResponse
import com.rootscare.data.model.api.response.nurses.nurselist.GetNurseListResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.NueseViewTimingsResponse
import com.rootscare.data.model.api.response.nurses.taskresponse.GetTaskResponse
import com.rootscare.data.model.api.response.patientReport.PatientReportResponse
import com.rootscare.data.model.api.response.patienthome.PatientHomeApiResponse
import com.rootscare.data.model.api.response.patientprescription.PatientPrescriptionResponse
import com.rootscare.data.model.api.response.patientprofileresponse.PatientProfileResponse
import com.rootscare.data.model.api.response.patientreviewandratingresponse.PatientReviewAndRatingResponse
import com.rootscare.data.model.api.response.paymenthistoryresponse.PatientPaymentHistoryResponse
import com.rootscare.data.model.api.response.registrationresponse.registrationcheckotpresponse.RegistrationCheckOTPResponse
import com.rootscare.data.model.api.response.registrationresponse.registrationsendotpresponse.RegistrationSendOTPResponse
import com.rootscare.data.model.api.response.reschedule.doctorreschedule.DoctorRescheduleResponse
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
import com.rootscare.ui.supportmore.models.ModelIssueTypes
import com.rootscare.ui.utilitycommon.NeedSupportRequest
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import java.util.*

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

    @POST("api-patient-home")
    fun apiPatientHome(@Body homeRequest: HomeRequest): Single<PatientHomeApiResponse>

    @POST("api-patient-home-search")
    fun apiPatientHomeSearch(@Body homeSearchRequestRequestBody: HomeSearchRequest): Single<PatientHomeApiResponse>

    @POST("api-department-list")
    fun apidepartmentlist(): Single<DoctorDepartmentListingResponse>

    @POST("api-doctor-list")
    fun apidoctorlist(): Single<AllDoctorListingResponse>

    @POST("api-hospital-doctor-list")
    fun apidoctorlistviahospital(@Body homeSearchRequestRequestBody: DoctorRequest): Single<AllDoctorListingResponse>

    @POST("api-hospital-list")
    fun apiHospitalList(@Body hospitalRequest: HospitalNearbyRequest): Single<AllHospitalListingResponse>


    @GET("api-caregiver-list")
    fun apicaregivelist(): Single<AllCaregiverListingResponse>


    @GET("api-babysitter-list")
    fun apibabysitter(): Single<AllCaregiverListingResponse>

    @GET("api-babysitter-list")
    fun apibabysitter1(): Single<GetCaregiverListResponse>


    @GET("api-caregiver-list")
    fun apicaregivelist1(): Single<GetCaregiverListResponse>

    @POST("api-department-doctor-list")
    fun apidepartmentdoctorlist(@Body doctorListByDepartmentIdRequestBody: DoctorListByDepartmentIdRequest): Single<AllDoctorListingResponse>


    @POST("api-department-hospital-list")
    fun apiDepartmentHospitalList(@Body doctorListByDepartmentIdRequestBody: DoctorListByDepartmentIdRequest): Single<AllHospitalListingResponse>

    @POST("api-doctor-details")
    fun apidoctordetails(@Body doctorDetailsRequestBody: DoctorDetailsRequest): Single<DoctorDetailsResponse>

    @POST("api-patient-family-member")
    fun apipatientfamilymember(@Body getPatientFamilyMemberRequestBody: GetPatientFamilyMemberRequest): Single<GetPatientFamilyListResponse>

    @POST("api-doctor-private-slot")
    fun apidoctorprivateslot(@Body doctorPrivateSlotRequestBody: DoctorPrivateSlotRequest): Single<DoctorPrivateSlotResponse>

    @POST("api-doctor-homevisit-slot-filter")
    fun apiDoctorHomeVisit(@Body doctorPrivateSlotRequestBody: DoctorPrivateSlotRequest): Single<DoctorHomeSlotResponse>

    @POST("api-doctor-slot-under-hospital")
    fun apiHospitalDoctorSlot(@Body doctorPrivateSlotRequestBody: DoctorPrivateSlotRequest): Single<DoctorPrivateSlotResponse>

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
    @Multipart
    @POST("api-book-cart-private-doctor")
    fun apibookcartprivatedoctor(
        @Part("patient_id") patient_id: RequestBody,
        @Part("family_member_id") family_member_id: RequestBody,
        @Part("doctor_id") doctor_id: RequestBody,
        @Part("private_clinic_id") private_clinic_id: RequestBody,
        @Part("appointment_date") appointment_date: RequestBody,
        @Part("from_time") from_time: RequestBody,
        @Part("to_time") to_time: RequestBody,
        @Part("price") price: RequestBody,
        @Part symptom_recording: MultipartBody.Part,
        @Part("symptom_text") symptom_text: RequestBody,
        @Part upload_prescription: MultipartBody.Part,
        @Part("appointment_type") appointment_type: RequestBody,
        @Part("booking_type") booking_type: RequestBody
    ): Single<DoctorPrivateBooingResponse>

    @Multipart
    @POST("api-book-cart-hospital-doctor")
    fun apiBookCartHospitalDoctor(
        @Part("patient_id") patient_id: RequestBody,
        @Part("family_member_id") family_member_id: RequestBody,
        @Part("doctor_id") doctor_id: RequestBody,
        @Part("hospital_id") hospital_id: RequestBody,
        @Part("appointment_date") appointment_date: RequestBody,
        @Part("from_time") from_time: RequestBody,
        @Part("to_time") to_time: RequestBody,
        @Part("price") price: RequestBody,
        @Part symptom_recording: MultipartBody.Part,
        @Part("symptom_text") symptom_text: RequestBody,
        @Part upload_prescription: MultipartBody.Part,
        @Part("appointment_type") appointment_type: RequestBody
    ): Single<DoctorPrivateBooingResponse>

     @POST("api-insert-doctor-review")
    fun apiinsertdoctorreview(@Body req: InsertDoctorReviewRatingRequest): Single<DoctorReviewRatingSubmiteResponse>

    @POST("api-patient-book-appointment-offline-payment")
    fun apipatientbookappointmentofflinepayment(@Body req: CheckoutDoctorBookingRequest): Single<CheckoutDoctorBookingResponse>

    @POST("api-patient-review")
    fun apipatientreview(@Body req: PatientReviewAndRatingRequest): Single<PatientReviewAndRatingResponse>

    @POST("api-patient-payment-history")
    fun apipatientpaymenthistory(@Body req: PatientPaymentHistoryRequest): Single<PatientPaymentHistoryResponse>

    @POST("api-patient-appointment-history")
    fun apipatientappointmenthistory(@Body req: AppointmentRequest): Single<Result>

    //    @POST("api-search-doctor")
    @POST("api-search-hospital-doctor")
    fun apiSearchHospitalDoctor(@Body req: SeeAllDoctorSearch): Single<AllDoctorListingResponse>

    @POST("api-search-doctor")
    fun apiSearchDoctor(@Body seeAllDoctorSearchRequestBody: SeeAllDoctorSearch): Single<AllDoctorListingResponse>

    @POST("api-search-hospital")
    fun apisearchhospital(@Body seeAllDoctorSearchRequestBody: SeeAllDoctorSearch): Single<AllHospitalListingResponse>

    @POST("api-appointment-details")
    fun apiappointmentdetails(@Body appointmentDetailsRequestBody: AppointmentDetailsRequest): Single<DoctorAppointmentResponse>

    @POST("api-appointment-details")
    fun apinurseappointmentdetails(@Body req: AppointmentDetailsRequest): Single<NurseAppointmentDetailsResponse>

    @POST("api-patient-upcoming-appointment")
    fun apipatientupcomingappointment(@Body appointmentRequestBody: AppointmentRequest): Single<Result>

    @POST("api-patient-today-appointment")
    fun apipatienttodayappointment(@Body appointmentRequestBody: AppointmentRequest): Single<Result>

    @POST("api-patient-all-appointment")
    fun apipatientallappointment(@Body appointmentRequestBody: AppointmentRequest): Single<Result>

    @POST("api-patient-upcoming-cancel-appointment")
    fun apipatientupcomingcancelappointment(@Body appointmentRequestBody: AppointmentRequest): Single<Result>

    @POST("api-patient-prescription")
    fun apipatientprescription(@Body appointmentRequestBody: AppointmentRequest): Single<PatientPrescriptionResponse>

    @POST("api-patient-report")
    fun apiPatientReport(@Body appointmentRequestBody: AppointmentRequest): Single<PatientReportResponse>

    @POST("api-deactivate-user")
    fun apideactivateuser(@Body appointmentRequestBody: AppointmentRequest): Single<DeactivateAccountResponse>

    @POST("api-delete-patient-family-member")
    fun apideletepatientfamilymember(@Body req: DeletePatientFamilyMemberRequest): Single<GetPatientFamilyListResponse>

    @Multipart
    @POST("api-edit-patient-family")
    fun apieditpatientfamily(
        @Part("id") id: RequestBody,
        @Part("first_name") first_name: RequestBody,
        @Part("last_name") last_name: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("gender") gender: RequestBody,
        @Part("age") age: RequestBody
    ): Single<EditFamilyMemberResponse>

    @POST("api-getnationality")
    fun apinationality(@Body req: RequestBody?): Single<NationalityResponse>

    @POST("api-cancel-appointment")
    fun apicancelappointment(@Body req: CancelAppointmentRequest): Single<AppointmentCancelResponse>

    @POST("api-reschedule-appointment")
    fun apirescheduleappointment(@Body req: DoctorAppointmentRescheduleRequest): Single<DoctorRescheduleResponse>

    @POST("api-patient-medical-record")
    fun apipatientmedicalrecord(@Body req: GetMedicalRecordListRequest): Single<MedicalRecordListResponse>

    @POST("api-delete-patient-medical-record")
    fun apideletepatientmedicalrecord(@Body req: MedicalFileDeleteRequest): Single<MedicalFileDeleteResponse>


    @Multipart
    @POST("api-insert-medical-record")
    fun apiinsertmedicalrecord(
        @Part("user_id") user_id: RequestBody,
        @Part("date") date: RequestBody,
        @Part("title") title: RequestBody,
        @Part file: ArrayList<MultipartBody.Part>
    ): Single<MedicalRecordListResponse>

    @POST("api-nurse-details")
    fun apinursedetails(@Body nurseDetailsRequestBody: NurseDetailsRequest): Single<NurseDetailsResponse>

    @GET("api-nurse-list")
    fun apinurselist(): Single<GetNurseListResponse>

    @GET("api-physiotherapy-list")
    fun apiPhysiotherapyList(): Single<GetNurseListResponse>

    @POST("api-physiotherapy-details")
    fun apiPhysiotherapyDetails(@Body nurseDetailsRequestBody: NurseDetailsRequest): Single<NurseDetailsResponse>

    @POST("api-physiotherapy-task")
    fun apiPhysiotherapyTaskList(@Body physiotherapyTask: PhysiotherapyTask): Single<GetTaskResponse>

    @POST("api-search-nurse")
    fun apisearchnurse(@Body nurseSearchByNameRequestBody: NurseSearchByNameRequest): Single<GetNurseListResponse>

    @POST("api-search-hospital")
    fun apisearchhospital(@Body nurseSearchByNameRequestBody: NurseSearchByNameRequest): Single<AllHospitalListingResponse>


    @POST("api-lat-long-nearby-hospital-list")
    fun apihospitallistlat(@Body nurseSearchByNameRequestBody: HospitalNearbyRequest): Single<AllHospitalListingResponse>


    @POST("api-search-caregiver")
    fun apisearchcaregiver(@Body nurseSearchByNameRequestBody: NurseSearchByNameRequest): Single<GetCaregiverListResponse>


    @POST("api-search-caregiver")
    fun apisearchcaregiverGrid(@Body nurseSearchByNameRequestBody: NurseSearchByNameRequest): Single<AllCaregiverListingResponse>


    @POST("api-search-babysitter")
    fun apisearchbabysitterrGrid(@Body nurseSearchByNameRequestBody: NurseSearchByNameRequest): Single<AllCaregiverListingResponse>

    @POST("api-search-babysitter")
    fun apisearchbabysitterrGrid1(@Body nurseSearchByNameRequestBody: NurseSearchByNameRequest): Single<GetCaregiverListResponse>

    @POST("api-department-nurse-list")
    fun apidepartmentnurselist(@Body departmentNurseListRequestBody: DepartmentNurseListRequest): Single<GetNurseListResponse>

    @POST("api-nurse-task")
    fun apinurstasks(@Body nurseDetailsRequestBody: nursetask): Single<GetTaskResponse>

    @POST("api-hospital-pathology-test-list")
    fun apipatholist(@Body nurseDetailsRequestBody: DoctorRequest): Single<HospitalLabResponse>

    @POST("api-hospital-details")
    fun apihospinursedetails(@Body nurseDetailsRequestBody: HospitalRequest): Single<HospitalDetailsResponse>


    @POST("api-caregiver-details")
    fun apicaregiverdetails(@Body nurseDetailsRequestBody: NurseDetailsRequest): Single<NurseDetailsResponse>

    @POST("api-babysitter-details")
    fun apibabysitterdetails(@Body nurseDetailsRequestBody: NurseDetailsRequest): Single<NurseDetailsResponse>

    @POST("time-slot")
    fun getBookedSlots(@Body getSlotRequest: GetSlotRequest): Single<GetSlotsResponse>

    @POST("patient-get-time-slot")
    fun getProviderPreferredTime(@Body getSlotRequest: GetProviderPreferredTimeRequest): Single<GetPreferredTimeSlotResponse>

    @POST("task-based-slots")
    fun taskbasedslots(@Body nurseSlotRequestBody: NurseSlotRequest): Single<NueseViewTimingsResponse>

    @POST("api-insert-nurse-review")
    fun apiinsertnursereview(@Body InsertNurseReviewRequestBody: InsertNurseReviewRequest): Single<DoctorReviewRatingSubmiteResponse>

    @POST("api-get-hourly-rates")
    fun apigethourlyrates(@Body nurseHourlySlotRequestBody: NurseHourlySlotRequest): Single<GetNurseHourlySlotResponse>

    @Multipart
    @POST("api-book-cart-nurse")
    fun apibookcartnurse(
        @Part("patient_id") patient_id: RequestBody,
        @Part("family_member_id") family_member_id: RequestBody,
        @Part("nurse_id") nurse_id: RequestBody,
        @Part("from_date") from_date: RequestBody,
        @Part("to_date") to_date: RequestBody,
        @Part("from_time") from_time: RequestBody,
        @Part("to_time") to_time: RequestBody,
        @Part("price") price: RequestBody,
        @Part symptom_recording: MultipartBody.Part,
        @Part("symptom_text") symptom_text: RequestBody,
        @Part upload_prescription: MultipartBody.Part,
        @Part("appointment_type") appointment_type: RequestBody,
//        @Part("task_id") task_id: RequestBody
    ): Single<NurseBookAppointmentResponse>

    @Multipart
    @POST("api-book-cart-nurse-task")
    fun apiBookCartNurseTaskBased(
        @Part("patient_id") patient_id: RequestBody,
        @Part("family_member_id") family_member_id: RequestBody,
        @Part("nurse_id") nurse_id: RequestBody,
        @Part("from_date") from_date: RequestBody,
        @Part("to_date") to_date: RequestBody,
        @Part("from_time") from_time: RequestBody,
        @Part("to_time") to_time: RequestBody,
        @Part("price") price: RequestBody,
        @Part symptom_recording: MultipartBody.Part,
        @Part("symptom_text") symptom_text: RequestBody,
        @Part upload_prescription: MultipartBody.Part,
        @Part("appointment_type") appointment_type: RequestBody,
        @Part("task_id") task_id: RequestBody
    ): Single<NurseBookAppointmentResponse>

    @Multipart
    @POST("api-book-cart-hospital-pathology")
    fun apibookcartpatho(
        @Part("patient_id") patient_id: RequestBody,
        @Part("family_member_id") family_member_id: RequestBody,
        @Part("hospital_id") nurse_id: RequestBody,
        @Part("appointment_date") from_date: RequestBody,
        // @Part("to_date") to_date: RequestBody,
        @Part("from_time") from_time: RequestBody,
        @Part("to_time") to_time: RequestBody,
        @Part("price") price: RequestBody,
        @Part symptom_recording: MultipartBody.Part,
        @Part("symptom_text") symptom_text: RequestBody,
        @Part upload_prescription: MultipartBody.Part,
        @Part("appointment_type") appointment_type: RequestBody,
        @Part("pathology_id") task_id: RequestBody
    ): Single<NurseBookAppointmentResponse>

    @Multipart
    @POST("api-book-cart-caregiver")
    fun apibookcartcaregiver(
        @Part("patient_id") patient_id: RequestBody,
        @Part("family_member_id") family_member_id: RequestBody,
        @Part("caregiver_id") nurse_id: RequestBody,
        @Part("from_date") from_date: RequestBody,
        @Part("to_date") to_date: RequestBody,
        @Part("from_time") from_time: RequestBody,
        @Part("to_time") to_time: RequestBody,
        @Part("price") price: RequestBody,
        @Part symptom_recording: MultipartBody.Part,
        @Part("symptom_text") symptom_text: RequestBody,
        @Part upload_prescription: MultipartBody.Part,
        @Part("appointment_type") appointment_type: RequestBody
    ): Single<NurseBookAppointmentResponse>

    @Multipart
    @POST("api-book-cart-babysitter")
    fun apiBookCartBabySitter(
        @Part("patient_id") patient_id: RequestBody,
        @Part("family_member_id") family_member_id: RequestBody,
        @Part("babysitter_id") nurse_id: RequestBody,
        @Part("from_date") from_date: RequestBody,
        @Part("to_date") to_date: RequestBody,
        @Part("from_time") from_time: RequestBody,
        @Part("to_time") to_time: RequestBody,
        @Part("price") price: RequestBody,
        @Part symptom_recording: MultipartBody.Part,
        @Part("symptom_text") symptom_text: RequestBody,
        @Part upload_prescription: MultipartBody.Part,
        @Part("appointment_type") appointment_type: RequestBody
    ): Single<NurseBookAppointmentResponse>

    @Multipart
    @POST("api-book-cart-physiotherapy")
    fun apiBookCartPhysiotherapist(
        @Part("patient_id") patient_id: RequestBody,
        @Part("family_member_id") family_member_id: RequestBody,
        @Part("physiotherapy_id") nurse_id: RequestBody,
        @Part("appointment_date") from_date: RequestBody,
        @Part("from_time") from_time: RequestBody,
        @Part("to_time") to_time: RequestBody,
        @Part("price") price: RequestBody,
        @Part symptom_recording: MultipartBody.Part,
        @Part("symptom_text") symptom_text: RequestBody,
        @Part upload_prescription: MultipartBody.Part,
        @Part("appointment_type") appointment_type: RequestBody,
        @Part("task_id") task_id: RequestBody
    ): Single<NurseBookAppointmentResponse>

    @Multipart
    @POST("api-edit-patient-profile-image")
    fun apieditpatientprofileimage(
        @Part("user_id") user_id: RequestBody,
        @Part image: MultipartBody.Part
    ): Single<PatientProfileResponse>


    @POST("api-appointment-details")
    fun getAppointmentDetails(@Body request: AppointmentDetailsRequest): Single<JsonObject>

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

    @POST("api-send_notification")
    fun sendPushNotification(@Body request: PushNotificationRequest): Single<JsonObject>

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
//  "service_type": "nurse",
//  "login_user_id": "48",
//  "family_member_id": "1",
//  "provider_id": "39"."task_id": "1,2",
//  "task_price": "30,40",
//  "task_type": "task_base",
//  "from_date": "2022-02-01",
//  "from_time": "09:00 AM",
//  "appointment_type": "offline",
//  "task_price_total": "500",
//  "vat_price": "20",
//  "distance_fare": "12",
//  "sub_total_price": "520",
//  "total_price": "532",
//  "symptom_recording": "symptom_recording",
//  "upload_prescription": "upload_prescription"
//}
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


}
