package com.rootscare.data.model.api.request.hospital

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class HospitalRequest(
    @field:JsonProperty("id")
    @field:SerializedName("id")
    var id: Int? = null

)