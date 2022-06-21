package com.interfaces

import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem

interface OnPhysiotherapyAppointment {
    fun onCancelBtnClick(
        physiotherapyAppointmentItem: AppointmentItem, clickPosition: String
    )

    fun onRescheduleBtnClick(
        physiotherapyAppointmentItem: AppointmentItem, clickPosition: String
    )

    fun onViewDetailsClick(
        physiotherapyAppointmentItem: AppointmentItem, clickPosition: String
    )
}