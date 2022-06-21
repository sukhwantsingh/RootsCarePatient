package com.interfaces

import com.rootscare.data.model.api.response.appointmenthistoryresponse.PathologyAppointmentItem

interface OnHospitalItemClickListener {
    fun onCancelBtnClick(pathologyAppointmentItem: PathologyAppointmentItem, id: Int)

    fun onRescheduleBtnClick(pathologyAppointmentItem: PathologyAppointmentItem, id: Int)

    fun onViewDetailsClick(pathologyAppointmentItem: PathologyAppointmentItem, id: Int)

    fun onVideoCallClick(pathologyAppointmentItem: PathologyAppointmentItem, id: Int)
}