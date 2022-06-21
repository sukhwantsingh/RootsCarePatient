package com.interfaces

import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem

interface OnUpcomingLabAppointmentButtonClickListener {
    fun onCancelBtnClick(
        modelDoctorAppointmentItem: AppointmentItem, clickPosition: String
    )

    fun onRescheduleBtnClick(
        modelDoctorAppointmentItem: AppointmentItem, clickPosition: String
    )

    fun onViewDetailsClick(
        modelDoctorAppointmentItem: AppointmentItem, position: String
    )

    fun onVideoCallClick(
        modelDoctorAppointmentItem: AppointmentItem, position: String
    )
}