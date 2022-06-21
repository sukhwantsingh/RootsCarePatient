package com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class PathologyDetails(
	@field:JsonProperty("name")
	@field:SerializedName("name")
	val name: String? = null,
	@field:JsonProperty("test_ids")
	@field:SerializedName("test_ids")
	val testId: String? = null,
	@field:JsonProperty("test_name")
	@field:SerializedName("test_name")
	val testName: String? = null,
	@field:JsonProperty("test_price")
	@field:SerializedName("test_price")
	val testPrice: String? = null

)