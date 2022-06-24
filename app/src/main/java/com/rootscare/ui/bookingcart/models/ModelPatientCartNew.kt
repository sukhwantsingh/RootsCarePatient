package com.rootscare.ui.bookingcart.models


import androidx.annotation.Keep

@Keep
data class ModelPatientCartNew(
    val code: String?,
    val message: String?,
    val result: ArrayList<Result?>?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val appointment_type: String?,
        val babysitter_id: String?,
        val hospital_name: String?,
        val booking_type: String?,
        val slot_booking_id: String?,
        val caregiver_id: String?,
        val date: String?,
        val display_app_date: String?,
        val display_task_time: String?,
        val display_task_type: String?,
        val distance_fare: String?,
        val distance_fare_text: String?,
        val doctor_id: String?,
        val family_member_id: String?,
        val from_date: String?,
        val from_time: String?,
        val hospital_id: String?,
        val id: String?,
        val nurse_id: String?,
        val pathology_id: String?,
        val patient_id: String?,
        val physiotherapy_id: String?,
        val price: String?,
        val private_clinic_id: String?,
        val provider_details: ProviderDetails?,
        val service_display_type: String?,
        val service_type: String?,
        val sub_total_price: String?,
        val symptom_recording: String?,
        val symptom_text: String?,
        val task_details: List<TaskDetail?>?,
        val task_id: String?,
        val task_price: String?,
        val to_date: String?,
        val to_time: String?,
        val total_price: String?,
        val upload_prescription: String?,
        val vat_price: String?,
        val vat_text: String?,
        val isselectitem: Boolean? = true
    ) {
        @Keep
        data class ProviderDetails(
            val provider_name: String?
        )

        @Keep
        data class TaskDetail(
            val id: String?,
            val name: String?,
            val price: String?
        )
    }
}