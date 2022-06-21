package com.rootscare.ui.bookingappointment

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.deletepatientfamilymemberrequest.DeletePatientFamilyMemberRequest
import com.rootscare.data.model.api.request.doctorrequest.doctordetailsrequest.DoctorDetailsRequest
import com.rootscare.data.model.api.request.doctorrequest.doctorprivatesotrequest.DoctorPrivateSlotRequest
import com.rootscare.data.model.api.request.doctorrequest.getpatientfamilymemberrequest.GetPatientFamilyMemberRequest
import com.rootscare.ui.base.BaseViewModel
import io.reactivex.disposables.Disposable
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FragmentBookingAppointmentViewModel : BaseViewModel<FragmentBookingAppointmentNavigator>() {
    fun apiPatientFamilyMember(getPatientFamilyMemberRequest: GetPatientFamilyMemberRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable =
            apiServiceWithGsonFactory.apipatientfamilymember(getPatientFamilyMemberRequest)
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        // Store last login time
                        Log.d("check_response", ": " + Gson().toJson(response))
                        navigator.successGetPatientFamilyListResponse(response)
                        /* Saving access token after singup or login */
                        if (response.result != null) {
                        }

                    } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorGetPatientFamilyListResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })

        compositeDisposable.add(disposable)
    }


    fun apiDoctorPrivateSlot(doctorPrivateSlotRequest: DoctorPrivateSlotRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apidoctorprivateslot(doctorPrivateSlotRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successDoctorPrivateSlotResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result != null) {
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorGetPatientFamilyListResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiDoctorHomeVisitSlot(doctorPrivateSlotRequest: DoctorPrivateSlotRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apiDoctorHomeVisit(doctorPrivateSlotRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successDoctorHomeVisitSlotResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result != null) {
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorGetPatientFamilyListResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiHospitalDoctorSlot(doctorPrivateSlotRequest: DoctorPrivateSlotRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apiHospitalDoctorSlot(doctorPrivateSlotRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successDoctorPrivateSlotResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result != null) {
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorGetPatientFamilyListResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiDoctorDetails(doctorDetailsRequest: DoctorDetailsRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apidoctordetails(doctorDetailsRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successDoctorDetailsResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result != null) {
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorGetPatientFamilyListResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }


    fun apiBookCartPrivateDoctor(
        patient_id: RequestBody,
        family_member_id: RequestBody,
        doctor_id: RequestBody,
        private_clinic_id: RequestBody,
        appointment_date: RequestBody,
        from_time: RequestBody,
        to_time: RequestBody,
        price: RequestBody,
        symptom_recording: MultipartBody.Part? = null,
        symptom_text: RequestBody,
        upload_prescription: MultipartBody.Part? = null,
        appointment_type: RequestBody,
        booking_type: RequestBody
    ) {
//        userId: RequestBody,first_name: RequestBody,last_name: RequestBody,id_number: RequestBody,status: RequestBody,image: MultipartBody.Part? = null
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        var disposable: Disposable? = null
        if (symptom_recording != null) {


            disposable = apiServiceWithGsonFactory.apibookcartprivatedoctor(
                patient_id,
                family_member_id,
                doctor_id,
                private_clinic_id,
                appointment_date,
                from_time,
                to_time,
                price,
                symptom_recording,
                symptom_text,
                upload_prescription!!,
                appointment_type,
                booking_type
            )
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        // Store last login time
                        Log.d("check_response", ": " + Gson().toJson(response))
                        navigator.successDoctorPrivateBooingResponse(response)
                    } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorGetPatientFamilyListResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })
        }
        compositeDisposable.add(disposable!!)
    }

    fun apiBookCartHospitalDoctor(
        patient_id: RequestBody,
        family_member_id: RequestBody,
        doctor_id: RequestBody,
        private_clinic_id: RequestBody,
        appointment_date: RequestBody,
        from_time: RequestBody,
        to_time: RequestBody,
        price: RequestBody,
        symptom_recording: MultipartBody.Part? = null,
        symptom_text: RequestBody,
        upload_prescription: MultipartBody.Part? = null,
        appointment_type: RequestBody
    ) {
//        userId: RequestBody,first_name: RequestBody,last_name: RequestBody,id_number: RequestBody,status: RequestBody,image: MultipartBody.Part? = null
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        var disposable: Disposable? = null
        if (symptom_recording != null) {


            disposable = apiServiceWithGsonFactory.apiBookCartHospitalDoctor(
                patient_id,
                family_member_id,
                doctor_id,
                private_clinic_id,
                appointment_date,
                from_time,
                to_time,
                price,
                symptom_recording,
                symptom_text,
                upload_prescription!!,
                appointment_type
            )
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        // Store last login time
                        Log.d("check_response", ": " + Gson().toJson(response))
                        navigator.successDoctorPrivateBooingResponse(response)
                    } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorGetPatientFamilyListResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })
        }
        compositeDisposable.add(disposable!!)
    }

    fun apiDeletePatientFamilyMember(deletePatientFamilyMemberRequest: DeletePatientFamilyMemberRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable =
            apiServiceWithGsonFactory.apideletepatientfamilymember(deletePatientFamilyMemberRequest)
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        // Store last login time
                        Log.d("check_response", ": " + Gson().toJson(response))
                        navigator.successDeletePatientFamilyListResponse(response)
                        /* Saving access token after singup or login */
                        if (response.result != null) {
                        }

                    } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorGetPatientFamilyListResponse(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })

        compositeDisposable.add(disposable)
    }

}