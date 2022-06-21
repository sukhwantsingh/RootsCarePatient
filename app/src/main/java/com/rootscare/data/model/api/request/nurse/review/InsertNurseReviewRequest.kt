package com.rootscare.data.model.api.request.nurse.review

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class InsertNurseReviewRequest(
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	var userId: String? = null,
	@field:JsonProperty("review")
	@field:SerializedName("review")
	var review: String? = null,
	@field:JsonProperty("rating")
	@field:SerializedName("rating")
	var rating: String? = null,
	@field:JsonProperty("nurse_id")
	@field:SerializedName("nurse_id")
	var nurseId: String? = null
)
