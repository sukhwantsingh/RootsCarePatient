package com.rootscare.data.model.api.request.doctorrequest.doctorlistbydepartmentrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class DoctorListByDepartmentIdRequest(
	@field:JsonProperty("department_id")
	@field:SerializedName("department_id")
	var departmentId: String? = null
)