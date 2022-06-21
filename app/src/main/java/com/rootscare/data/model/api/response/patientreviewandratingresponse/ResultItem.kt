package com.rootscare.data.model.api.response.patientreviewandratingresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultItem(
	@field:JsonProperty("review_to")
	@field:SerializedName("review_to")
	val reviewTo: String? = null,
	@field:JsonProperty("image")
	@field:SerializedName("image")
	val image: String? = null,
	@field:JsonProperty("rating")
	@field:SerializedName("rating")
	val rating: String? = null,
	@field:JsonProperty("hospital_id")
	@field:SerializedName("hospital_id")
	val hospitalId: String? = null,
	@field:JsonProperty("physiotherapy_id")
	@field:SerializedName("physiotherapy_id")
	val physiotherapyId: String? = null,
	@field:JsonProperty("created_by")
	@field:SerializedName("created_by")
	val createdBy: String? = null,
	@field:JsonProperty("doctor_id")
	@field:SerializedName("doctor_id")
	val doctorId: String? = null,
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	val userId: String? = null,
	@field:JsonProperty("review")
	@field:SerializedName("review")
	val review: String? = null,
	@field:JsonProperty("updated_by")
	@field:SerializedName("updated_by")
	val updatedBy: String? = null,
	@field:JsonProperty("id")
	@field:SerializedName("id")
	val id: String? = null,
	@field:JsonProperty("nurse_id")
	@field:SerializedName("nurse_id")
	val nurseId: String? = null,
	@field:JsonProperty("created_date")
	@field:SerializedName("created_date")
	val createdDate: String? = null,
	@field:JsonProperty("updated_date")
	@field:SerializedName("updated_date")
	val updatedDate: String? = null,
	@field:JsonProperty("caregiver_id")
	@field:SerializedName("caregiver_id")
	val caregiverId: String? = null,
	@field:JsonProperty("babysitter_id")
	@field:SerializedName("babysitter_id")
	val babysitterId: String? = null,
	@field:JsonProperty("status")
	@field:SerializedName("status")
	val status: String? = null
)