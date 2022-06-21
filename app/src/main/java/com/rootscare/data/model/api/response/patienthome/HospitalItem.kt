package com.rootscare.data.model.api.response.patienthome

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class HospitalItem(
	@field:JsonProperty("image")
	@field:SerializedName("image")
	val image: String? = null,
	@field:JsonProperty("address")
	@field:SerializedName("address")
	val address: String? = null,
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	val userId: String? = null,
	@field:JsonProperty("contact")
	@field:SerializedName("contact")
	val contact: String? = null,
	@field:JsonProperty("document")
	@field:SerializedName("document")
	val document: String? = null,
	@field:JsonProperty("name")
	@field:SerializedName("name")
	val name: String? = null,
	@field:JsonProperty("updated_by")
	@field:SerializedName("updated_by")
	val updatedBy: String? = null,
	@field:JsonProperty("id")
	@field:SerializedName("id")
	val id: String? = null,
	@field:JsonProperty("created_date")
	@field:SerializedName("created_date")
	val createdDate: String? = null,
	@field:JsonProperty("updated_date")
	@field:SerializedName("updated_date")
	val updatedDate: String? = null,
	@field:JsonProperty("created_by")
	@field:SerializedName("created_by")
	val createdBy: String? = null,
	@field:JsonProperty("status")
	@field:SerializedName("status")
	val status: String? = null
)