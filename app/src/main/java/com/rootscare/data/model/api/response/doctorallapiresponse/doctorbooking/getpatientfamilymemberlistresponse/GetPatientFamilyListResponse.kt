package com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import java.util.ArrayList
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetPatientFamilyListResponse(
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