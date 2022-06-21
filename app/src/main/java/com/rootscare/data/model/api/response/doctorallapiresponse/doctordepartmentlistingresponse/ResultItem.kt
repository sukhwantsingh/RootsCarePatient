package com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultItem(
	@field:JsonProperty("id")
	@field:SerializedName("id")
	val id: String? = null,
	@field:JsonProperty("title")
	@field:SerializedName("title")
	val title: String? = null
)