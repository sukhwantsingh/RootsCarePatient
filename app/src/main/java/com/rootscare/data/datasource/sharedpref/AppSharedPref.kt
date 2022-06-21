package com.rootscare.data.datasource.sharedpref

import android.content.Context
import android.content.SharedPreferences
import com.rootscare.utilitycommon.LanguageModes

class AppSharedPref(context: Context, prefFileName: String) {

    private val mPrefs: SharedPreferences


    companion object {
        private val PREF_KEY_IS_LOGGED_IN_ISER = "PREF_KEY_IS_LOGGED_IN_ISER"
        private val PREF_KEY_LOG_USERNAME = "PREF_KEY_LOG_USERNAME"
        private val PREF_KEY_LOG_PWD = "PREF_KEY_LOG_PWD"
        private val PREF_KEY_PLACE_KEY = "PREF_KEY_PLACE_KEY"

        private val PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"

        private val PREF_KEY_IS_LOGIN_REMEMBER = "PREF_KEY_IS_LOGIN_REMEMBER"

        private val PREF_KEY_CURRENT_USER_EMAIL = "PREF_KEY_CURRENT_USER_EMAIL"

        private val PREF_KEY_USER_ID = "PREF_KEY_USER_ID"

        private val PREF_KEY_APPOINTMENT_TYPE = "PREF_KEY_APPOINTMENT_TYPE"

        private val PREF_KEY_USER_TYPE = "PREF_KEY_USER_TYPE"

        private val PREF_KEY_TRINNER_ID = "PREF_KEY_TRINNER_ID"


        private val PREF_KEY_CURRENCY_SYMBOL = "PREF_KEY_currencySymbol"
        private val PREF_KEY_WORK_AREA = "PREF_KEY_WORK_AREA"
        private val PREF_KEY_LOGIN_STUDENT_NAME = "PREF_KEY_LOGIN_STUDENT_NAME"
        private val PREF_KEY_STUDENT_REFERAL_CODE = "PREF_KEY_STUDENT_REFERAL_CODE"
        private val PREF_KEY_LOGIN_STUDENT_IMAGE = "PREF_KEY_LOGIN_STUDENT_IMAGE"
        private val PREF_KEY_LOGIN_STUDENT_EMAIL = "PREF_KEY_LOGIN_STUDENT_EMAIL"
        private val PREF_KEY_LOGIN_STUDENT_LOGIN_PASSWORD = "PREF_KEY_LOGIN_STUDENT_LOGIN_PASSWORD"
        private val PREF_KEY_LANG = "PREF_KEY_LANG"
        private val PREF_KEY_USER_CURR_LOCATION = "PREF_KEY_USER_CURR_LOCATION"
    }
    init {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)
    }

    fun deleteCurrentUserEmail() {
        mPrefs.edit().remove(PREF_KEY_CURRENT_USER_EMAIL).apply()
    }
    var isLoggedIn: Boolean?
        get() = mPrefs.getBoolean(PREF_KEY_IS_LOGGED_IN_ISER, false)
        set(islogin) = mPrefs.edit().putBoolean(PREF_KEY_IS_LOGGED_IN_ISER, islogin ?: false).apply()

    var remUsername: String?
        get() = mPrefs.getString(PREF_KEY_LOG_USERNAME, null)
        set(remUn) = mPrefs.edit().putString(PREF_KEY_LOG_USERNAME, remUn).apply()

    var remPwd: String?
        get() = mPrefs.getString(PREF_KEY_LOG_PWD, null)
        set(rpd) = mPrefs.edit().putString(PREF_KEY_LOG_PWD, rpd).apply()

    var plcKey: String?
        get() = mPrefs.getString(PREF_KEY_PLACE_KEY, null)
        set(pKey) = mPrefs.edit().putString(PREF_KEY_PLACE_KEY, pKey).apply()

    var isloginremember: Boolean?
        get() = mPrefs.getBoolean(PREF_KEY_IS_LOGIN_REMEMBER, false)
        set(isloginremember) = mPrefs.edit().putBoolean(PREF_KEY_IS_LOGIN_REMEMBER, isloginremember ?: false).apply()

    var languagePref: String?
        get() = mPrefs.getString(PREF_KEY_LANG, LanguageModes.AR.get())
        set(langPref) = mPrefs.edit().putString(PREF_KEY_LANG, langPref?.trim() ?: LanguageModes.AR.get()).apply()

    var accessToken: String?
        get() = mPrefs.getString(PREF_KEY_ACCESS_TOKEN, null)
        set(accessToken) = mPrefs.edit().putString(PREF_KEY_ACCESS_TOKEN, accessToken).apply()

    var userId: String?
        get() = mPrefs.getString(PREF_KEY_USER_ID, null)
        set(email) = mPrefs.edit().putString(PREF_KEY_USER_ID, email).apply()

    var appointmentType: String?
        get() = mPrefs.getString(PREF_KEY_APPOINTMENT_TYPE, null)
        set(appointmentType) = mPrefs.edit().putString(PREF_KEY_APPOINTMENT_TYPE, appointmentType).apply()

    var userType: String?
        get() = mPrefs.getString(PREF_KEY_USER_TYPE, null)
        set(usertype) = mPrefs.edit().putString(PREF_KEY_USER_TYPE, usertype?.trim()?:"").apply()
    var trainnerid: String?
        get() = mPrefs.getString(PREF_KEY_TRINNER_ID, null)
        set(trainnerid) = mPrefs.edit().putString(PREF_KEY_TRINNER_ID, trainnerid).apply()


    var workArea: String?
        get() = mPrefs.getString(PREF_KEY_WORK_AREA, null)
        set(workarea) = mPrefs.edit().putString(PREF_KEY_WORK_AREA, workarea?.trim()?:"").apply()

    var currencySymbol: String?
        get() = mPrefs.getString(PREF_KEY_CURRENCY_SYMBOL, null)
        set(crSymbol) = mPrefs.edit().putString(PREF_KEY_CURRENCY_SYMBOL, crSymbol?.trim().orEmpty()).apply()

    var userName: String?
        get() = mPrefs.getString(PREF_KEY_LOGIN_STUDENT_NAME, null)
        set(userName) = mPrefs.edit().putString(PREF_KEY_LOGIN_STUDENT_NAME, userName?.trim()?:"").apply()

    var userCurrentLocation: String?
        get() = mPrefs.getString(PREF_KEY_USER_CURR_LOCATION, null)
        set(uCurLoc) = mPrefs.edit().putString(PREF_KEY_USER_CURR_LOCATION, uCurLoc?.trim()?:"").apply()

    var studentreferalcode: String?
        get() = mPrefs.getString(PREF_KEY_STUDENT_REFERAL_CODE, null)
        set(studentreferalcode) = mPrefs.edit().putString(PREF_KEY_STUDENT_REFERAL_CODE, studentreferalcode).apply()
    var userEmail: String?
        get() = mPrefs.getString(PREF_KEY_LOGIN_STUDENT_EMAIL, null)
        set(userEmail) = mPrefs.edit().putString(PREF_KEY_LOGIN_STUDENT_EMAIL, userEmail?.trim()?:"").apply()

    var studentLogInPassword: String?
        get() = mPrefs.getString(PREF_KEY_LOGIN_STUDENT_LOGIN_PASSWORD, null)
        set(studentLogInPassword) = mPrefs.edit().putString(PREF_KEY_LOGIN_STUDENT_LOGIN_PASSWORD, studentLogInPassword).apply()

    var userImage: String?
        get() = mPrefs.getString(PREF_KEY_LOGIN_STUDENT_IMAGE, null)
        set(userImage) = mPrefs.edit().putString(PREF_KEY_LOGIN_STUDENT_IMAGE, userImage?.trim()?:"").apply()

    fun deleteUserId() {
        mPrefs.edit().remove(PREF_KEY_IS_LOGGED_IN_ISER).apply()
    }

    fun deleteIsLogINRemember() {
        mPrefs.edit().remove(PREF_KEY_IS_LOGGED_IN_ISER).apply()
    }

    fun deleteIsAppointmentType() {
        mPrefs.edit().remove(PREF_KEY_APPOINTMENT_TYPE).apply()
    }
}
