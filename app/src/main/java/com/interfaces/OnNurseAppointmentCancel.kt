package com.interfaces

import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem

interface OnNurseAppointmentCancel {
    fun onCancelBtnClick(nurseAppointmentItem: AppointmentItem, clickPosition: String)
    fun onRescheduleBtnClick(nurseAppointmentItem: AppointmentItem, clickPosition: String)
    fun onViewDetailsClick(nurseAppointmentItem: AppointmentItem, clickPosition: String)
}