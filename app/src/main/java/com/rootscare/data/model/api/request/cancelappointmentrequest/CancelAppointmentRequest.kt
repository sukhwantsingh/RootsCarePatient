package com.rootscare.data.model.api.request.cancelappointmentrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class CancelAppointmentRequest(
    @field:JsonProperty("service_type")
    @field:SerializedName("service_type")
    var serviceType: String? = null,
    @field:JsonProperty("id")
    @field:SerializedName("id")
    var id: String? = null
)
