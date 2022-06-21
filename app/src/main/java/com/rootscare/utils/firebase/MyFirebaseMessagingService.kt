package com.rootscare.utils.firebase

import android.R.id
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rootscare.ApplicationClass
import com.rootscare.R
import com.rootscare.data.datasource.sharedpref.AppSharedPref
import com.rootscare.twilio.SoundPoolManager
import com.rootscare.twilio.VideoCallActivity
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.splash.SplashActivity
import com.rootscare.utils.PreferenceUtility
import org.json.JSONException
import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = MyFirebaseMessagingService::class.java.simpleName

    private var notificationUtils: NotificationUtils? = null

    override fun onNewToken(refreshedToken: String) {
        Log.wtf(TAG, "refreshedToken : $refreshedToken")
        super.onNewToken(refreshedToken)
        storeRegIdInPref(refreshedToken)

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        val registrationComplete = Intent(Config.REGISTRATION_COMPLETE)
        registrationComplete.putExtra("token", refreshedToken)
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete)
    }

    private fun storeRegIdInPref(token: String) {
        ApplicationClass.instance?.appSharedPref?.accessToken = token
     }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(TAG, "From: $remoteMessage")
        Log.e(TAG, "From Priority: " + remoteMessage.priority)
        Log.e(TAG, "From Sender: " + remoteMessage.senderId)
        Log.e(TAG, "From Original Priority: " + remoteMessage.originalPriority)
        Log.e(TAG, "From TTl: " + remoteMessage.ttl)

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.notification?.body)
//            handleNotification(remoteMessage.notification!!.body)
        }

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "Data Payload: " + remoteMessage.data.toString())
            try {
                val params = remoteMessage.data
                val jsonObject = JSONObject(params as Map<*, *>)
                handleDataMessage(jsonObject);
            } catch (e: Exception) {
                Log.e(TAG, "Exception: " + e.message)
            }
        }
    }

    private fun handleNotification(title: String?, message: String?) {
        if (!notificationUtils!!.isAppIsInBackground(applicationContext)) {
            println("app in foreground")
            // app is in foreground, broadcast the push message
            val pushNotification = Intent(Config.PUSH_NOTIFICATION)
            pushNotification.putExtra("message", id.message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)

            val resultIntent = Intent(applicationContext, SplashActivity::class.java)
            showNotificationMessage(applicationContext, title ?: "", message?:"", resultIntent)

        } else {
            println("app in background")
            // app is in background, show the notification in notification tray
            val resultIntent = Intent(applicationContext, SplashActivity::class.java)
            showNotificationMessage(applicationContext, title?: "", message?:"", resultIntent)
        }
    }

    private fun handleDataMessage(json: JSONObject) {
        Log.e(TAG, "push json: $json")
        notificationUtils = NotificationUtils(applicationContext)
        try {
            val title = json.optString("title")
            val notificationType = json.optString("type")

            Log.e(TAG, "notificationType: $notificationType")
            Log.e(TAG, "title: $title")

            when {
                notificationType.equals("doctor_to_patient_video_call", ignoreCase = true) -> {
                    val messageBody = json.optString("message")
                    val roomName = json.getString("room_name")
                    val fromUserName = json.optString("fromUserName")
                    val fromUserId = json.getString("fromUserId")
                    val toUserName = json.getString("toUserName")
                    val toUserId = json.getString("toUserId")
                    val orderId = json.getString("order_id")
                    val resultIntent = Intent(applicationContext, VideoCallActivity::class.java)
                    resultIntent.putExtra("roomName", roomName)
                    resultIntent.putExtra("messageBody", messageBody)
                    resultIntent.putExtra("toUserName", toUserName)
                    resultIntent.putExtra("fromUserName", fromUserName)
                    resultIntent.putExtra("notificationType", notificationType)
                    resultIntent.putExtra("messageBody", messageBody)
                    resultIntent.putExtra("toUserId", toUserId)
                    resultIntent.putExtra("fromUserId", fromUserId)
                    resultIntent.putExtra("orderId", orderId)
                    resultIntent.putExtra("isDoctorCalling", true)
                    showCallNotificationMessage(applicationContext, resultIntent, fromUserName)
                }
                notificationType.equals("doctor_to_patient_video_call_reject", ignoreCase = true) -> {
                    val fromUserName = json.optString("fromUserName")
                    val fromUserId = json.getString("fromUserId")
                    SoundPoolManager.getInstance(this)?.stopRinging()
                    VideoCallActivity.room?.disconnect()

                    //                VideoCallActivity.isCallTaken = true;
                //    if (notificationUtils?.isAppIsInBackground(applicationContext)==true) {
                        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancelAll()
                        VideoCallActivity.isReject = true
                //    }
                    if (VideoCallActivity.alertDialog != null) VideoCallActivity.alertDialog!!.dismiss()
                    val resultIntent = Intent(applicationContext, HomeActivity::class.java)
                    PreferenceUtility.saveStringInPreference(applicationContext,"isVideoCall","isVideoCallReject")
                    PreferenceUtility.saveStringInPreference(applicationContext,"fromUserId",fromUserId)
                    PreferenceUtility.saveStringInPreference(applicationContext,"fromUserName", fromUserName)
                    resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                //    if (notificationUtils!!.isAppIsInBackground(applicationContext)) {
                        notificationUtils?.showNotificationMessage(resources.getString(R.string.app_name),"Missed call from $fromUserName", resultIntent)
                 //   } else {
                 //       startActivity(resultIntent)
                //    }
                    // if (VideoCallActivity.appCompatActivity != null)
                        VideoCallActivity.appCompatActivity?.finish()
                        VideoCallActivity.isCallTaken = false
                }
                else -> {
                    val messageBody = json.optString("message")
                    val obj = JSONObject(messageBody)
                    println("objobj, $obj")
                    val message = obj.optString("body")
                    handleNotification(title, message)

                }
            }


        } catch (e: JSONException) {
            Log.e(TAG, "Json Exception: " + e.message)
        } catch (e: Exception) {
            Log.e(TAG, "Exception: " + e.message)
        }
    }

    private fun showCallNotificationMessage(
        context: Context, intent: Intent, fromUserName: String
    ) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
 //       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
 //           if (notificationUtils!!.isAppIsInBackground(applicationContext)) {
                notificationUtils?.showCallNotification(resources.getString(R.string.app_name),"$fromUserName is calling.", intent)
//            } else {
//                startActivity(intent)
 //           }
 //       } else startActivity(intent)
    }

    /**
     * Showing notification with text only
     */
    fun showNotificationMessage(context: Context, title: String, message: String, intent: Intent) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils?.showNotificationMessage(title, message, intent)
    }


    /**
     * Showing notification with text and image
     */
    private fun showNotificationMessageWithBigImage(
        context: Context, title: String, message: String, intent: Intent, imageUrl: String
    ) {
        notificationUtils = NotificationUtils(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationUtils!!.showNotificationMessage(title, message, intent, imageUrl)
    }

}