package com.interfaces

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.SlotItem

interface OnDoctorSlotClickListner {
    fun onSloctClick(position: SlotItem)
}