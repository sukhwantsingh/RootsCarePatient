package com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorHomeSlotResponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class DoctorHomeSlotResponse(
    @field:JsonProperty("result")
    @field:SerializedName("result")
    val result: ArrayList<Result?>? = null,
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

//@JsonIgnoreProperties(ignoreUnknown = true)
//data class Result(
//    @field:JsonProperty("id")
//    @field:SerializedName("id")
//    val id: String? = null,
//    @field:JsonProperty("time_to")
//    @field:SerializedName("time_to")
//    val timeTo: String? = null,
//    @field:JsonProperty("time_from")
//    @field:SerializedName("time_from")
//    val timeFrom: String? = null,
//    @field:JsonProperty("doctor_id")
//    @field:SerializedName("doctor_id")
//    val doctorId: String? = null,
//    @field:JsonProperty("day")
//    @field:SerializedName("day")
//    val day: String? = null
//)
data class Result(
    @SerializedName("day")
    val day: String? = null,
    @SerializedName("doctor_id")
    val doctorId: String? = null,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("slot")
    val slot: LinkedList<Slot>? = null
) : Serializable

data class Slot(
    @SerializedName("time_from")
    val timeFrom: String? = null,
    @SerializedName("time_to")
    val timeTo: String? = null
) : Serializable