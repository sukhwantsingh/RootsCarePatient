package com.rootscare.data.model.api.request.nurse.nursrtask

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class PhysiotherapyTask(
    @field:JsonProperty("physiotherapy_id")
    @field:SerializedName("physiotherapy_id")
    var physiotherapyId: String? = null
)
