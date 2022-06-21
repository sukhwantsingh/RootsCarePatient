package com.rootscare.data.model.api.request.doctorrequest.doctordetailsrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class DoctorDetailsRequest(
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	var userId: String? = null,
	@field:JsonProperty("id")
	@field:SerializedName("id")
	var id: String? = null
)