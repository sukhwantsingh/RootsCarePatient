package com.rootscare.data.model.api.response.patientprescription

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultItem(
	@field:JsonProperty("doctor_id")
	@field:SerializedName("doctor_id")
	val doctorId: String? = null,
	@field:JsonProperty("patient_id")
	@field:SerializedName("patient_id")
	val patientId: String? = null,
	@field:JsonProperty("appointment_id")
	@field:SerializedName("appointment_id")
	val appointmentId: String? = null,
	@field:JsonProperty("prescription_number")
	@field:SerializedName("prescription_number")
	val prescriptionNumber: String? = null,
	@field:JsonProperty("id")
	@field:SerializedName("id")
	val id: String? = null,
	@field:JsonProperty("e_prescription")
	@field:SerializedName("e_prescription")
	val ePrescription: String? = null,
	@field:JsonProperty("created_date")
	@field:SerializedName("created_date")
	val createdDate: String? = null,
	@field:JsonProperty("updated_date")
	@field:SerializedName("updated_date")
	val updatedDate: String? = null,
	@field:JsonProperty("status")
	@field:SerializedName("status")
	val status: String? = null
)