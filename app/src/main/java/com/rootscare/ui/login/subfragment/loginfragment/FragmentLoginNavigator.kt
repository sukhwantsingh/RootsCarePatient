package com.rootscare.ui.login.subfragment.loginfragment

import com.rootscare.ui.login.ModelLoginResponse

interface FragmentLoginNavigator {
    fun successLoginResponse(loginResponse: ModelLoginResponse?)
    fun errorLoginResponse(throwable: Throwable?)
}