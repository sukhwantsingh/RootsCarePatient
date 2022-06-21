package com.rootscare.data.model.api.request.medicalrecorddeleterequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class MedicalFileDeleteRequest(
	@field:JsonProperty("id")
	@field:SerializedName("id")
	var id: String? = null
)
