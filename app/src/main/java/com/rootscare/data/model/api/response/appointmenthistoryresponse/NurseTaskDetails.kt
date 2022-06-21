package com.rootscare.data.model.api.response.appointmenthistoryresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class NurseTaskDetails(
	@field:JsonProperty("test_ids")
	@field:SerializedName("test_ids")
	val testId: String? = null,
	@field:JsonProperty("test_name")
	@field:SerializedName("test_name")
	val testName: String? = null,
	@field:JsonProperty("test_price")
	@field:SerializedName("test_price")
	val testPrice: String? = null
) : Serializable