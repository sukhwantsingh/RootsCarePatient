package com.rootscare.data.model.api.request.insertdoctorreviewratingrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class InsertDoctorReviewRatingRequest(
	@field:JsonProperty("doctor_id")
	@field:SerializedName("doctor_id")
	var doctorId: String? = null,
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	var userId: String? = null,
	@field:JsonProperty("review")
	@field:SerializedName("review")
	var review: String? = null,
	@field:JsonProperty("rating")
	@field:SerializedName("rating")
	var rating: String? = null,
	@field:JsonProperty("order_id")
	@field:SerializedName("order_id")
	var orderId: String? = null
)