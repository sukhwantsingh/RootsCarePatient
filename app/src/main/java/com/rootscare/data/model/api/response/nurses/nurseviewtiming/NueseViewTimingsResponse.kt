package com.rootscare.data.model.api.response.nurses.nurseviewtiming

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class NueseViewTimingsResponse(
	@field:JsonProperty("result")
	@field:SerializedName("result")
	val result: ArrayList<ResultItem?>? = null,
	@field:JsonProperty("code")
	@field:SerializedName("code")
	val code: String? = null,
	@field:JsonProperty("message")
	@field:SerializedName("message")
	val message: String? = null,
	@field:JsonProperty("status")
	@field:SerializedName("status")
	val status: Boolean? = null
)
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultItem(
	@field:JsonProperty("start_time")
	@field:SerializedName("start_time")
	val startTime: String? = null,
	@field:JsonProperty("end_time")
	@field:SerializedName("end_time")
	val endTime: String? = null,
	@field:JsonProperty("status")
	@field:SerializedName("status")
	val status: String? = null

)
