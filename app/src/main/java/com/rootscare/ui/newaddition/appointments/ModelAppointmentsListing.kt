package com.rootscare.ui.newaddition.appointments


import androidx.annotation.Keep

@Keep
data class ModelAppointmentsListing(
    val code: String?,
    val message: String?,
    val result: ArrayList<Result?>?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val acceptance_status: String?,
        var app_date: String?,
        val appointment_status: String?,
        val appointment_type: String?,
        val booked_by: String?,
        var booking_date: String?,
        var booking_type: String?,
        var app_time: String?,
        val id: String?,
        val order_id: String?,
        val patient_contact: String?,
        val patient_name: String?,
        val paymentStatus: String?,
        val paymentType: String?,
        val price: String?,
        val provider_id: String?,
        val provider_name: String?,
        val provider_type: String?,
        val dispaly_provider_type: String?,
        val slot_time: String?,
        val slot_booking_id: String?,
        val speciality: String?,
        val task_id: String?)
}