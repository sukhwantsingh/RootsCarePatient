package com.rootscare.data.model.api.request.deletepatientfamilymemberrequest

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class DeletePatientFamilyMemberRequest(
	@field:JsonProperty("id")
	@field:SerializedName("id")
	var id: String? = null
)
