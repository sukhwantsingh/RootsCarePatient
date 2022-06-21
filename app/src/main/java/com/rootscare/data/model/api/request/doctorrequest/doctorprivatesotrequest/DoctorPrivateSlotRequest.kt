package com.rootscare.data.model.api.request.doctorrequest.doctorprivatesotrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class DoctorPrivateSlotRequest(
    @field:JsonProperty("date")
    @field:SerializedName("date")
    var date: String? = null,
    @field:JsonProperty("time")
    @field:SerializedName("time")
    var time: String? = null,
    @field:JsonProperty("doctor_id")
    @field:SerializedName("doctor_id")
    var doctorId: String? = null,
    @field:JsonProperty("hospital_id")
    @field:SerializedName("hospital_id")
    var hospitalId: String? = null,
    @field:JsonProperty("day")
    @field:SerializedName("day")
    var day: String? = null
)