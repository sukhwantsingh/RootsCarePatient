package com.rootscare.data.model.api.response.patienthome

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class Result(
	@field:JsonProperty("doctor")
	@field:SerializedName("doctor")
	val doctor: ArrayList<DoctorItem?>? = null,
	@field:JsonProperty("babysitter")
	@field:SerializedName("babysitter")
	val babysitter: ArrayList<BabysitterItem?>? = null,
	@field:JsonProperty("caregiver")
	@field:SerializedName("caregiver")
	val caregiver: ArrayList<CaregiverItem?>? = null,
	@field:JsonProperty("physiotherapy")
	@field:SerializedName("physiotherapy")
	val physiotherapy: ArrayList<PhysiotherapyItem?>? = null,
	@field:JsonProperty("nurse")
	@field:SerializedName("nurse")
	val nurse: ArrayList<NurseItem?>? = null,
	@field:JsonProperty("hospital")
	@field:SerializedName("hospital")
	val hospital: ArrayList<HospitalItem?>? = null
)