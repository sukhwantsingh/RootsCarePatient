package com.rootscare.utils

import com.rootscare.utilitycommon.API_BASE_URL

object AppConstants {

    val API_STATUS_CODE_LOCAL_ERROR = 0

    val DB_NAME = "database_name"

    val SHARED_PREF_NAME = "shared_pref_name"

    val NULL_INDEX = -1L

    val PREF_NAME = "shared_pref_name"

    val SEED_DATABASE_OPTIONS = "seed/options.json"

    val SEED_DATABASE_QUESTIONS = "seed/questions.json"

    val STATUS_CODE_FAILED = "failed"

    val STATUS_CODE_SUCCESS = "success"

    val TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss"

    val channelname = "Phoenix"
    val channelId = "Phoenix"
    const val REQUEST_RESULT_CODE_FOR_TRANSITION_ANIM_LABLISTING_TO_POPUPIMAGESHOW = 21
    const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_AND_CAMERA = 16
    const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 17
    var IS_DOCTOR_RESCHEDULE = false
    var IS_NURSE_RESCHEDULE = false
    var IS_PHYSIO_RESCHEDULE = false
    var IS_CAREGIVER_RESCHEDULE = false
    var IS_BABYSITTER_RESCHEDULE = false
    var DoctorRescheduleClickPosition = 0
    var PhysiotherapistRescheduleClickPosition = 0
    var CaregiverRescheduleClickPosition = 0
    var BabysitterRescheduleClickPosition = 0
    var NurseRescheduleClickPosition = 0

    var rescheduleFrom = ""
    var TWILIO_CALL_URL = "${API_BASE_URL}api-get-video-access-token"

}// This utility class is not publicly instantiable


