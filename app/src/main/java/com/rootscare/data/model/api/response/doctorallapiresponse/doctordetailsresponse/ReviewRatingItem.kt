package com.rootscare.data.model.api.response.doctorallapiresponse.doctordetailsresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class ReviewRatingItem(
	@field:JsonProperty("review")
	@field:SerializedName("review")
	val review: String? = null,
	@field:JsonProperty("rating")
	@field:SerializedName("rating")
	val rating: String? = null,
	@field:JsonProperty("review_by")
	@field:SerializedName("review_by")
	val reviewBy: String? = null
)