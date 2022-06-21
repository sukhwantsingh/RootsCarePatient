package com.rootscare.ui.login.subfragment.forgotpassword

import com.rootscare.data.model.api.response.forgotpasswordresponse.forgotpasswordchangepassword.ForgotPasswordChangePasswordResponse
import com.rootscare.data.model.api.response.forgotpasswordresponse.forgotpasswordsendmailresponse.ForgotPasswordSendMailResponse

interface FragmentForGotPasswordNavigator {

    fun successForgotPasswordSendMailResponse(forgotPasswordSendMailResponse: ForgotPasswordSendMailResponse?)
    fun successForgotPasswordChangePasswordResponse(forgotPasswordChangePasswordResponse: ForgotPasswordChangePasswordResponse?)
    fun errorForgotPasswordSendMailResponse(throwable: Throwable?)
}