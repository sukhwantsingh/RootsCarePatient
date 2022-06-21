package com.rootscare.data.model.api.response.nurses.nurseappointmentdetails

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class NurseAppointmentDetailsResponse(
	@field:JsonProperty("result")
	@field:SerializedName("result")
	val result: Result? = null,
	@field:JsonProperty("code")
	@field:SerializedName("code")
	val code: String? = null,
	@field:JsonProperty("message")
	@field:SerializedName("message")
	val message: String? = null,
	@field:JsonProperty("status")
	@field:SerializedName("status")
	val status: Boolean? = null
)
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class Result(
	@field:JsonProperty("nurse_name")
	@field:SerializedName("nurse_name")
	val nurseName: String? = null,
	@field:JsonProperty("nurse_experience")
	@field:SerializedName("nurse_experience")
	val nurseExperience: String? = null,
	@field:JsonProperty("appointment_status")
	@field:SerializedName("appointment_status")
	val appointmentStatus: String? = null,
	@field:JsonProperty("booking_date")
	@field:SerializedName("booking_date")
	val bookingDate: String? = null,
	@field:JsonProperty("appointment_date")
	@field:SerializedName("appointment_date")
	val appointmentDate: String? = null,
	@field:JsonProperty("from_time")
	@field:SerializedName("from_time")
	val fromTime: String? = null,
	@field:JsonProperty("acceptance_status")
	@field:SerializedName("acceptance_status")
	val acceptanceStatus: String? = null,
	@field:JsonProperty("paymentType")
	@field:SerializedName("paymentType")
	val paymentType: String? = null,
	@field:JsonProperty("patient_contact")
	@field:SerializedName("patient_contact")
	val patientContact: String? = null,
	@field:JsonProperty("booked_by")
	@field:SerializedName("booked_by")
	val bookedBy: String? = null,
	@field:JsonProperty("to_date")
	@field:SerializedName("to_date")
	val toDate: String? = null,
	@field:JsonProperty("price")
	@field:SerializedName("price")
	val price: String? = null,
	@field:JsonProperty("patient_name")
	@field:SerializedName("patient_name")
	val patientName: String? = null,
	@field:JsonProperty("id")
	@field:SerializedName("id")
	val id: String? = null,
	@field:JsonProperty("nurse_image")
	@field:SerializedName("nurse_image")
	val nurseImage: String? = null,
	@field:JsonProperty("to_time")
	@field:SerializedName("to_time")
	val toTime: String? = null,
	@field:JsonProperty("order_id")
	@field:SerializedName("order_id")
	val orderId: String? = null,
	@field:JsonProperty("paymentStatus")
	@field:SerializedName("paymentStatus")
	val paymentStatus: String? = null
)
