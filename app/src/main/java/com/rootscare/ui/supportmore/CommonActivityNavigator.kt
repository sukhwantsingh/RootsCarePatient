package com.rootscare.ui.supportmore

import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.ui.supportmore.models.ModelIssueTypes

interface CommonActivityNavigator{
    fun onSuccessSubmitSupport(response: CommonResponse)

    // implement on need support screen
    fun onFetchHelpTopics(response: ModelIssueTypes?){}

    fun errorInApi(throwable: Throwable?)
}