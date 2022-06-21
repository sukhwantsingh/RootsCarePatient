package com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultItem(
	@field:JsonProperty("date")
	@field:SerializedName("date")
	val date: String? = null,
	@field:JsonProperty("doctor_details")
	@field:SerializedName("doctor_details")
	val doctorDetails: DoctorDetails? = null,
	@field:JsonProperty("from_date")
	@field:SerializedName("from_date")
	val fromDate: String? = null,
	@field:JsonProperty("symptom_text")
	@field:SerializedName("symptom_text")
	val symptomText: String? = null,
	@field:JsonProperty("hospital_id")
	@field:SerializedName("hospital_id")
	val hospitalId: String? = null,
	@field:JsonProperty("physiotherapy_id")
	@field:SerializedName("physiotherapy_id")
	val physiotherapyId: String? = null,
	@field:JsonProperty("from_time")
	@field:SerializedName("from_time")
	val fromTime: String? = null,
	@field:JsonProperty("symptom_recording")
	@field:SerializedName("symptom_recording")
	val symptomRecording: String? = null,
	@field:JsonProperty("doctor_id")
	@field:SerializedName("doctor_id")
	val doctorId: String? = null,
	@field:JsonProperty("service_type")
	@field:SerializedName("service_type")
	val serviceType: String? = null,
	@field:JsonProperty("to_date")
	@field:SerializedName("to_date")
	val toDate: String? = null,
	@field:JsonProperty("appointment_type")
	@field:SerializedName("appointment_type")
	val appointmentType: String? = null,
	@field:JsonProperty("patient_id")
	@field:SerializedName("patient_id")
	val patientId: String? = null,
	@field:JsonProperty("price")
	@field:SerializedName("price")
	val price: String? = null,
	@field:JsonProperty("upload_prescription")
	@field:SerializedName("upload_prescription")
	val uploadPrescription: String? = null,
	@field:JsonProperty("private_clinic_id")
	@field:SerializedName("private_clinic_id")
	val privateClinicId: String? = null,
	@field:JsonProperty("id")
	@field:SerializedName("id")
	val id: String? = null,
	@field:JsonProperty("nurse_id")
	@field:SerializedName("nurse_id")
	val nurseId: String? = null,
	@field:JsonProperty("caregiver_id")
	@field:SerializedName("caregiver_id")
	val caregiverId: String? = null,
	@field:JsonProperty("babysitter_id")
	@field:SerializedName("babysitter_id")
	val babysitterId: String? = null,
	@field:JsonProperty("to_time")
	@field:SerializedName("to_time")
	val toTime: String? = null,
	@field:JsonProperty("family_member_id")
	@field:SerializedName("family_member_id")
	val familyMemberId: String? = null,
	@field:JsonProperty("select_item")
	@field:SerializedName("select_item")
	var isselectitem: Boolean? = true,
	@field:JsonProperty("pathology_details")
	@field:SerializedName("pathology_details")
	val pathologyDetails: PathologyDetails? = null
)