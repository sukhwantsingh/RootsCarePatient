package com.rootscare.data.model.api.request.nurse.nursedetailsrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class NurseDetailsRequest(
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	var userId: Int? = null,
	@field:JsonProperty("id")
	@field:SerializedName("id")
    var id: Int? = null
)
