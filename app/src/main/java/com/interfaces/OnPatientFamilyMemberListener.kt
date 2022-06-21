package com.interfaces

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.ResultItem

interface OnPatientFamilyMemberListener {
    fun onItemClick(resultItem: ResultItem)
    fun onEditButtonClick(modelOfGetAddPatientList: ResultItem)
    fun onDeleteButtonClick(id: String)
}