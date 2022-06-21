package com.rootscare.ui.nurses.editpatient

import com.rootscare.data.model.api.response.editpatientfamilymemberresponse.EditFamilyMemberResponse

interface FragmentNurseEditPatientNavigator {
    fun successEditFamilyMemberResponse(editFamilyMemberResponse: EditFamilyMemberResponse?)
    fun errorEditFamilyMemberResponse(throwable: Throwable?)
}