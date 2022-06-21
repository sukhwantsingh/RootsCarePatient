package com.rootscare.data.model.api.request.doctorrequest.doctorsearchrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
	data class SeeAllDoctorSearch(
	@field:JsonProperty("search_content")
	@field:SerializedName("search_content")
	var searchContent: String? = null
)