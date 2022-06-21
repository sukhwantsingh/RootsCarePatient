package com.rootscare.data.model.api.request.nurse.searchbyname

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class NurseSearchByNameRequest(
	@field:JsonProperty("search_content")
	@field:SerializedName("search_content")
	var searchContent: String? = null
)
