package com.rootscare.data.model.api.response.medicalrecordresponse

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class MedicalRecordListResponse(
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
	@field:JsonProperty("date")
	@field:SerializedName("date")
	val date: String? = null,
	@field:JsonProperty("upload_document")
	@field:SerializedName("upload_document")
	val uploadDocument: ArrayList<UploadDocumentItem?>? = null
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class UploadDocumentItem(
	@field:JsonProperty("file")
	@field:SerializedName("file")
	val file: String? = null,
	@field:JsonProperty("id")
	@field:SerializedName("id")
	val id: String? = null,
	@field:JsonProperty("title")
	@field:SerializedName("title")
	val title: String? = null
)
