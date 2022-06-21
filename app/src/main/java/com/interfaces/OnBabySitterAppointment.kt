package com.interfaces

import com.rootscare.data.model.api.response.appointmenthistoryresponse.BabysitterAppointmentItem

interface OnBabySitterAppointment {
    fun onCancelBtnClick(id: String)
    fun onRescheduleBtnClick(
        babysitterAppointmentItem: BabysitterAppointmentItem,
        clickPosition: String
    )

    fun onViewDetailsClick(
        babysitterAppointmentItem: BabysitterAppointmentItem,
        clickPosition: String
    )
}