package com.rootscare.data.model.api.response.hospitalallapiresponse.allhospitalistingresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class HospitalResultItem(
    @field:JsonProperty("name")
    @field:SerializedName("name")
    val name: String? = null,
    @field:JsonProperty("image")
    @field:SerializedName("image")
    val image: String? = null,
    @field:JsonProperty("address")
    @field:SerializedName("address")
    val address: String? = null,
    @field:JsonProperty("document")
    @field:SerializedName("document")
    val document: String? = null,
    @field:JsonProperty("user_id")
    @field:SerializedName("user_id")
    val user_id: String? = null,
    @field:JsonProperty("user_type")
    @field:SerializedName("user_type")
    val user_type: String? = null,
    @field:JsonProperty("email")
    @field:SerializedName("email")
    val email: String? = null,
    @field:JsonProperty("username")
    @field:SerializedName("username")
    val username: String? = null,
    @field:JsonProperty("contact")
    @field:SerializedName("contact")
    val contact: String? = null,
    @field:JsonProperty("original_password")
    @field:SerializedName("original_password")
    val original_password: String? = null,
    @field:JsonProperty("forgot_code")
    @field:SerializedName("forgot_code")
    val forgot_code: String? = null,
    @field:JsonProperty("date")
    @field:SerializedName("date")
    val date: String? = null,
    @field:JsonProperty("updated_date")
    @field:SerializedName("updated_date")
    val updated_date: String? = null,
    @field:JsonProperty("created_by")
    @field:SerializedName("created_by")
    val created_by: String? = null,
    @field:JsonProperty("updated_by")
    @field:SerializedName("updated_by")
    val updated_by: String? = null,
    @field:JsonProperty("status")
    @field:SerializedName("status")
    val status: String? = null,

    @field:JsonProperty("rating")
    @field:SerializedName("rating")
    val rating: Float? = null,

    @field:JsonProperty("reviews")
    @field:SerializedName("reviews")
    val reviews: Int? = null,

    @field:JsonProperty("departments")
    @field:SerializedName("departments")
    val departments: String? = null,

    @field:JsonProperty("latitude")
    @field:SerializedName("latitude")
    val latitude: String? = null,
    @field:JsonProperty("longitudes")
    @field:SerializedName("longitudes")
    val longitudes: String? = null,


    )