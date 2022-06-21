package com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class DoctorPrivateSlotResponse(
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class SlotItem(
	@field:JsonProperty("time_to")
	@field:SerializedName("time_to")
	val timeTo: String? = null,
	@field:JsonProperty("time_from")
	@field:SerializedName("time_from")
	val timeFrom: String? = null,
	@field:JsonProperty("status")
	@field:SerializedName("status")
	val status: String? = null

)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultItem(
	@field:JsonProperty("hospital_address")
	@field:SerializedName("hospital_address")
	val hospitalAddress: String? = null,
	@field:JsonProperty("hospital_name")
	@field:SerializedName("hospital_name")
	val hospitalName: String? = null,
	@field:JsonProperty("clinic_address")
	@field:SerializedName("clinic_address")
	val clinicAddress: String? = null,
	@field:JsonProperty("clinic_name")
	@field:SerializedName("clinic_name")
	val clinicName: String? = null,
	@field:JsonProperty("slot")
	@field:SerializedName("slot")
	val slot: ArrayList<SlotItem?>? = null,
	@field:JsonProperty("hospital_id")
	@field:SerializedName("hospital_id")
	val hospitalId: String? = null,
	@field:JsonProperty("clinic_id")
	@field:SerializedName("clinic_id")
	val clinicId: String? = null,
	@field:JsonProperty("day")
	@field:SerializedName("day")
	val day: String? = null
)
