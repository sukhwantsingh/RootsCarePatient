package com.rootscare.data.model.api.response.nurses.nursehourlyslot

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetNurseHourlySlotResponse(
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
	@field:JsonProperty("duration")
	@field:SerializedName("duration")
	val duration: String? = null,
	@field:JsonProperty("price")
	@field:SerializedName("price")
	val price: String? = null,
	var setCheck: Boolean = false
)
