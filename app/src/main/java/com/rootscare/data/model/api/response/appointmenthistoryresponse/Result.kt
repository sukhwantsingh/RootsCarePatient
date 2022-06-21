package com.rootscare.data.model.api.response.appointmenthistoryresponse

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable
@Keep
data class Result(

	@field:SerializedName("result")
	val result: Result? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("appointment")
	val appointment: List<AppointmentItem?>? = null
)
@Keep
data class DepartmentItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: String? = null
)
@Keep
data class AppointmentItem(

	@field:SerializedName("appointment_status")
	val appointmentStatus: String? = null,

	@field:SerializedName("booking_date")
	val bookingDate: String? = null,

	@field:SerializedName("appointment_date")
	val appointmentDate: String? = null,

	@field:SerializedName("clinic_name")
	val clinicName: String? = null,

	@field:SerializedName("task_id")
	val taskId: String? = null,

	@field:SerializedName("from_time")
	val fromTime: String? = null,

	@field:SerializedName("paymentType")
	val paymentType: String? = null,

	@field:SerializedName("booked_by")
	val bookedBy: String? = null,

	@field:SerializedName("user_type")
	val userType: String? = null,

	@field:SerializedName("pathology_name")
	val pathologyName: String? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("patient_name")
	val patientName: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("department")
	val department: List<DepartmentItem?>? = null,

	@field:SerializedName("paymentStatus")
	val paymentStatus: String? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null,

	@field:SerializedName("hospital_name")
	val hospitalName: String? = null,

	@field:SerializedName("slot_type")
	val slotType: String? = null,

	@field:SerializedName("hospital_id")
	val hospitalId: String? = null,

	@field:SerializedName("acceptance_status")
	val acceptanceStatus: String? = null,

	@field:SerializedName("task_details")
	val taskDetails: TaskDetails? = null,

	@field:SerializedName("patient_contact")
	val patientContact: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("clinic_address")
	val clinicAddress: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("pathology_test_id")
	val pathologyTestId: String? = null,

	@field:SerializedName("to_time")
	val toTime: String? = null,

	@field:SerializedName("order_id")
	val orderId: String? = null,

	@field:SerializedName("appointment_type")
	val appointmentType: String? = null,

	@field:SerializedName("symptom_text")
	val symptomText: String? = null,

	@field:SerializedName("symptom_recording")
	val symptomRecording: String? = null,

	@field:SerializedName("upload_prescription")
	val uploadPrescription: String? = null
)
@Keep
data class TaskDetails(

	@field:SerializedName("test_ids")
	val testIds: String? = null,

	@field:SerializedName("test_price")
	val testPrice: String? = null,

	@field:SerializedName("test_name")
	val testName: String? = null
) : Serializable
