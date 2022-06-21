package com.rootscare.data.model.api.response.patientReport

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultItem(
	@field:JsonProperty("hospital_id")
	@field:SerializedName("hospital_id")
	val hospitalId: String? = null,
	@field:JsonProperty("patient_id")
	@field:SerializedName("patient_id")
	val patientId: String? = null,
	@field:JsonProperty("appointment_id")
	@field:SerializedName("appointment_id")
	val appointmentId: String? = null,
	@field:JsonProperty("report_number")
	@field:SerializedName("report_number")
	val reportNumber: String? = null,
	@field:JsonProperty("id")
	@field:SerializedName("id")
	val id: String? = null,
	@field:JsonProperty("e_report")
	@field:SerializedName("e_report")
	val eReport: String? = null,
	@field:JsonProperty("created_date")
	@field:SerializedName("created_date")
	val createdDate: String? = null,
	@field:JsonProperty("updated_date")
	@field:SerializedName("updated_date")
	val updatedDate: String? = null,
	@field:JsonProperty("status")
	@field:SerializedName("status")
	val status: String? = null,
	@field:JsonProperty("appointment_status")
	@field:SerializedName("appointment_status")
	val appointmentStatus: String? = null,
	@field:JsonProperty("acceptance_status")
	@field:SerializedName("acceptance_status")
	val acceptanceStatus: String? = null,
	@field:JsonProperty("paymentType")
	@field:SerializedName("paymentType")
	val paymentType: String? = null,
	@field:JsonProperty("paymentStatus")
	@field:SerializedName("paymentStatus")
	val paymentStatus: String? = null
)