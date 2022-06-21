package com.interfaces

import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem

interface OnCaregiverAppointment {
    fun onCancelBtnClick(
        caregiverAppointmentItem: AppointmentItem, clickPosition: String
    )

    fun onRescheduleBtnClick(
        caregiverAppointmentItem: AppointmentItem, clickPosition: String
    )

    fun onViewDetailsClick(
        caregiverAppointmentItem: AppointmentItem, clickPosition: String
    )
}