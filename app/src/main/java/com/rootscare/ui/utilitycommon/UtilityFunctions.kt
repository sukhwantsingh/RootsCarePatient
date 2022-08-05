package com.rootscare.utilitycommon

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.Patterns
import android.util.TypedValue
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.dialog.CommonDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.interfaces.DialogClickCallback
import com.interfaces.DropDownDialogCallBack
import com.model.SlotModel
import com.rootscare.ApplicationClass
import com.rootscare.BuildConfig
import com.rootscare.R
import com.rootscare.ui.newaddition.providerlisting.models.ModelDateSlots
import com.rootscare.ui.showimagelarger.TransaprentPopUpActivityForImageShow
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import android.util.Log
import android.view.View
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File




const val IS_PRODUCTION = false

val PAYMENT_KEY_: String by lazy { if(IS_PRODUCTION) "sk_live_6GPzSurWAK9ng1C7yUq8wOeh" else "sk_test_KOfdbVzDXW7JreslyPL2g1nN" }
val API_BASE_URL: String by lazy { if(IS_PRODUCTION) "https://rootscare.net/application/" else "https://teq-dev-var19.co.in/rootscare/" }
const val SUCCESS_CODE = "200"

enum class BaseMediaUrls(val url: String) {
    USERIMAGE("${API_BASE_URL}uploads/images/"),
    AUDIO("${API_BASE_URL}uploads/images/")
}

fun getAppVersionText() = "RC Version ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"

fun getAppVersionNumber() = BuildConfig.VERSION_CODE

fun getAppLocale() = if (ApplicationClass.instance?.appSharedPref?.languagePref.equals(LanguageModes.ENG.get(), ignoreCase = true)) {
    LanguageModes.ENG.getLangLocale() } else LanguageModes.AR.getLangLocale()

class IntentParams(key: String = "", value: String = "") {
    var key: String? = key
    var value: String? = value

}
fun Activity.doAnimation(anim:Int) = AnimationUtils.loadAnimation(this, anim)

fun dpToPx(context: Context, valueInDp: Float): Float {
    val metrics: DisplayMetrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
}

 fun isValidMobile(phone: String): Boolean {
    return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches()
}

val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
fun isValidPassword(s: String): Boolean {
    val PASSWORD_PATTERN = Pattern.compile(
        "[a-zA-Z0-9!@#$]{8,24}"
    )

//        return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches()
    return !TextUtils.isEmpty(s)
}

infix fun Activity.viewFileEnlarge(imgUrl: String) {
    startActivity(TransaprentPopUpActivityForImageShow.newIntent(this, imgUrl))
}

infix fun Fragment.viewFileEnlarge(imgUrl: String) {
    startActivity(TransaprentPopUpActivityForImageShow.newIntent(requireActivity(), imgUrl))
}

fun Fragment.pickupImage(reqCode: Int) {
    var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
    chooseFile.type = "image/*"
    chooseFile = Intent.createChooser(chooseFile, "Choose a image")
    startActivityForResult(chooseFile, reqCode)
}

fun Activity.uplaodCertificate(reqCode: Int) {
    var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
    chooseFile.type = "*/*"
    chooseFile = Intent.createChooser(chooseFile, "Choose a file")
    startActivityForResult(chooseFile, reqCode)
}

fun Fragment.uplaodCertificate(reqCode: Int) {
    var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
    chooseFile.type = "*/*"
    chooseFile = Intent.createChooser(chooseFile, "Choose a file")
    startActivityForResult(chooseFile, reqCode)
}

fun Activity.openDialer(num: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$num")
    startActivity(intent)
}

fun Fragment.openDialer(num: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$num")
    startActivity(intent)
}

fun Activity.openGoogleMap(lat: String, lng: String) {
    val gmmIntentUri = Uri.parse("geo:$lat,$lng")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    startActivity(Intent.createChooser(mapIntent, "Open via"))
//    mapIntent.resolveActivity(packageManager)?.let {
//        startActivity(mapIntent)
//    }
}

fun Fragment.openGoogleMap(lat: String, lng: String) {
    val gmmIntentUri = Uri.parse("geo:$lat,$lng")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    startActivity(Intent.createChooser(mapIntent, "Open via"))
//    mapIntent.resolveActivity(requireActivity().packageManager)?.let {
//        startActivity(mapIntent)
//    }
}

// "yyyy-MM-dd"
fun Date.changeToYYYYMMdd(): String? {
    val serverFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH) // server time
    return serverFormatter.format(this)
}
// "MMM-dd"
fun Date.changeToMMMdd(): String? {
    val serverFormatter = SimpleDateFormat("MMM dd", Locale.ENGLISH) // server time
    return serverFormatter.format(this)
}
// "EEE-dd"
fun Date.changeToEEEdd(): String? {
    val serverFormatter = SimpleDateFormat("EEE dd", Locale.ENGLISH) // server time
    return serverFormatter.format(this)
}

inline fun <reified T : Activity> Activity.navigate(params: List<IntentParams> = emptyList()) {
    val intent = Intent(this, T::class.java)
    for (parameter in params) {
        intent.putExtra(parameter.key, parameter.value)
    }
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
}

inline fun <reified T : Activity> Fragment.navigate(params: List<IntentParams> = emptyList()) {
    val intent = Intent(activity, T::class.java)
    for (parameter in params) {
        intent.putExtra(parameter.key, parameter.value)
    }
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)

}

inline fun <reified T : Activity> Fragment.navigateWithResultSet(resultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(requireActivity(), T::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    resultLauncher.launch(intent)
}

inline fun <reified T : Activity> Activity.navigateWithResultSet(resultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(this, T::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
    resultLauncher.launch(intent)
}

inline fun <reified T : Any> String.getModelFromPref(): T? {
    return Gson().fromJson(this, T::class.java)
}


fun ImageView.setRemoteProfileImage(url_: String?) {
    val options: RequestOptions =
        RequestOptions().centerCrop().placeholder(R.color.colorTxtGrey1).priority(Priority.HIGH)
    this.let {
        Glide.with(this.context)
            .load("${BaseMediaUrls.USERIMAGE.url}${url_?.trim()}")
            .apply(options).into(it)
    }
}

fun CircleImageView.setCircularLocalImage(url_: String) {
    val options: RequestOptions =
        RequestOptions().centerCrop().placeholder(R.drawable.profile_no_image)
            .priority(Priority.HIGH)

    this.let {
        Glide
            .with(this.context)
            .load(url_.trim())
            .apply(options)
            .into(it)
    }
}

fun CircleImageView.setHamburgerImage(url_: String) {
    val options: RequestOptions = RequestOptions().error(R.drawable.ic_circle_img_hamburger)
        .placeholder(R.drawable.ic_circle_img_hamburger).priority(
        Priority.HIGH
    )

    this.let {
        Glide
            .with(this.context)
            .load("${BaseMediaUrls.USERIMAGE.url}${url_.trim()}")
            .apply(options)
            .into(it)
    }


}

fun Activity.materialAlert(
    title: String? = "",
    msg: String? = "",
    positiveText: String = "Ok",
    cancelable: Boolean = false
) {
//    try {
//        val builder: MaterialDialog.Builder? = MaterialDialog.Builder(this)
//            .cancelable(cancelable)
//            .content(msg.orEmpty())
//            .positiveText(positiveText)
//            .onPositive { d, _ -> d.dismiss() }
//        if (title?.isNotBlank() == true) builder?.title(title)
//        builder?.build()?.show()
//    } catch (e: java.lang.Exception) {
//        Crashlytics.logException(e)
//    }
}

fun Fragment.materialAlert(
    title: String? = "",
    msg: String = "",
    positiveText: String = "Ok",
    cancelable: Boolean = false
) {
//    try {
//        val builder: MaterialDialog.Builder? = MaterialDialog.Builder(requireContext())
//            .cancelable(cancelable)
//            .content(msg)
//            .positiveText(positiveText)
//            .onPositive { d, _ -> d.dismiss() }
//        if (title?.isNotBlank() == true) builder?.title(title)
//        builder?.build()?.show()
//    } catch (e: java.lang.Exception) {
//        Crashlytics.logException(e)
//    }
}

fun String.asReqBody() = this.toRequestBody("multipart/form-data".toMediaTypeOrNull())


fun setClickableString(
    wholeValue: String,
    textView: TextView?,
    clickableValue: Array<String>,
    clickableSpans: Array<ClickableSpan?>
) {
    val spannableString = SpannableString(wholeValue)
    for (i in clickableValue.indices) {
        val clickableSpan = clickableSpans[i]
        val link = clickableValue[i]
        val startIndexOfLink = wholeValue.indexOf(link)
        spannableString.setSpan(
            clickableSpan,
            startIndexOfLink,
            startIndexOfLink + link.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    textView?.highlightColor = Color.TRANSPARENT
    textView?.movementMethod = LinkMovementMethod.getInstance()
    textView?.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun getCurrentAppDate() :String {
    val df = SimpleDateFormat("yyyy-MM-dd",  Locale.ENGLISH)
    return df.format(Calendar.getInstance().time) ?: ""
}
fun getCurrentAppTime():String {
    val df = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    return df.format(Calendar.getInstance().time) ?: ""
}

fun isTimeAfterCurrent(dateTime: String): Boolean {
    val timeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a",Locale.ENGLISH)
    val currTime = timeFormat.parse("${getCurrentAppDate()} ${getCurrentAppTime()}")

    return try {
        val timeSlot = timeFormat.parse(dateTime)
        timeSlot?.after(currTime) ?: false
    } catch (e:ParseException) {
        false
    }


}

fun Activity.startSmoothScroll(pos: Int?, recycle: RecyclerView?) {
    if (pos != null) {
        Handler(Looper.getMainLooper()).postDelayed({
            runOnUiThread {
                val smoothScroller: RecyclerView.SmoothScroller =
                    object : LinearSmoothScroller(this) {
                        override fun getVerticalSnapPreference(): Int {
                            return SNAP_TO_ANY
                        }
                    }
                smoothScroller.targetPosition = pos
                recycle?.layoutManager?.startSmoothScroll(smoothScroller)
            }
        }, 400)
    }
}

fun Fragment.startSmoothScroll(pos: Int?, recycle: RecyclerView?) {
    if (pos != null) {
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                requireActivity().runOnUiThread {
                    val smoothScroller: RecyclerView.SmoothScroller =
                        object : LinearSmoothScroller(requireContext()) {
                            override fun getVerticalSnapPreference(): Int {
                                return SNAP_TO_ANY
                            }
                    }
                    smoothScroller.targetPosition = pos
                    recycle?.layoutManager?.startSmoothScroll(smoothScroller)
                }
            }, 400)
        } catch (e: Exception){
            throw e
        }

    }
}

fun Activity.dialogDropdown(title:String, mList: ArrayList<String?>, textView:TextView?) {
    CommonDialog.showDialogForDropDownList(this,title, mList, object : DropDownDialogCallBack {
            override fun onConfirm(text: String) {
                textView?.text =text
            }
        })
}
fun Fragment.dialogDropdown(title:String , mList: ArrayList<String?>, textView:TextView?) {
    CommonDialog.showDialogForDropDownList(requireContext(),title, mList,
        object : DropDownDialogCallBack {
            override fun onConfirm(text: String) {
                textView?.text =text
            }
        })
}

interface AppTabListener: TabLayout.OnTabSelectedListener {
    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabUnselected(tab: TabLayout.Tab?) {}
}


infix fun Activity.showAlertWithSystemSetting(mMsg: String) {
    runOnUiThread {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.location_permission))
            setMessage(mMsg)
            setCancelable(false)
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton(getString(R.string.proceed)) { dialog, _ ->
                openAppSystemSettings()
                dialog.dismiss()
            }
            show()
        }
    }

}


fun convertDateFormat(datee: String): String? {
    val inputPattern = "yyyy-MM-dd HH:mm:ss"
    val outputPattern = "dd MMM, yyyy"
    val inputFormat = SimpleDateFormat(inputPattern,  Locale.ENGLISH)
    val outputFormat = SimpleDateFormat(outputPattern,  Locale.ENGLISH)
    var str: String? = null
    try {
       val date = inputFormat.parse(datee) ?: Date()
           str = outputFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return str
}

fun String?.asReqBodyForm() = this?.toRequestBody("multipart/form-data".toMediaTypeOrNull())
fun File?.asReqBodyForm() = this?.asRequestBody("multipart/form-data".toMediaTypeOrNull())

fun Context.openAppSystemSettings() {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", packageName, null)
    })
}
fun Fragment.showDialogWithGo(dTitle: String?, msg: String?, btnText:String = getString(R.string.ok), mCallback: DialogClickCallback): androidx.appcompat.app.AlertDialog? {
    val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
    if(dTitle.isNullOrBlank().not()) alertDialogBuilder.setTitle(dTitle)
    alertDialogBuilder.setMessage(msg)
    alertDialogBuilder.setCancelable(false)
    alertDialogBuilder.setPositiveButton(btnText) { d, _ ->
        mCallback.onConfirm()
        d.dismiss()
    }
    return alertDialogBuilder.show()
}
fun getDateSlots(): ArrayList<ModelDateSlots?>  {
    val slotsList: ArrayList<ModelDateSlots?> = ArrayList()
    val currCal = Calendar.getInstance()
    val fromDate_ = currCal.time
    currCal.add(Calendar.DATE,28) // add 28 days more
    val endDate = currCal.time   // sdf.format(currCal.time)

    val tempCal = Calendar.getInstance()
    tempCal.time = fromDate_
    while (tempCal.time.before(endDate)) {
        val strDate = tempCal.time.changeToYYYYMMdd()
        val strDateMMdd = tempCal.time.changeToEEEdd()?.replace(" ","\n")

        slotsList.add(ModelDateSlots(strDateMMdd, strDate,false))
        tempCal.add(Calendar.DATE, 1)
    }
    return slotsList
}


fun needToShowVideoCall(appDate: String?, appTime: String?, btnVideoCall: TextView) {

    val sdf = SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.ENGLISH)
    val sdf1 = SimpleDateFormat("EEE dd MMM yyyy hh:mm a", Locale.ENGLISH)
    val appDateTimeMilli = if(appDate?.contains(",")==true){
        sdf.parse("$appDate $appTime")?.time ?: Date().time
    } else {
        sdf1.parse("$appDate $appTime")?.time ?: Date().time
    }

    val appDateTime = Calendar.getInstance()
    appDateTime.timeInMillis = appDateTimeMilli
    val now = Calendar.getInstance()
    val diff = appDateTime.timeInMillis - now.timeInMillis

    var diffInMin = (diff / (1000 * 60)) % 60
    val diffInHour = ((diff / (1000 * 60 * 60)) % 24)*60
    diffInMin += diffInHour
    val diffInDays = ((diff / (1000 * 60 * 60 * 24)) % 365) * 24 * 60
    diffInMin += diffInDays

    try {
        val yesNo = when {
            now.get(Calendar.YEAR) == appDateTime.get(Calendar.YEAR) &&
                    now.get(Calendar.MONTH) == appDateTime.get(Calendar.MONTH) &&
                    now.get(Calendar.DATE) == appDateTime.get(Calendar.DATE) &&
                //    now.get(Calendar.HOUR) >= appDateTime.get(Calendar.HOUR) &&
                    diffInMin <= 10  -> {

                btnVideoCall.visibility = View.VISIBLE
                true
            }
            else ->{
                btnVideoCall.visibility = View.GONE
                false
            }
        }
        Log.wtf("OkHttp", "$diffInMin needToShowVideoCall: - $yesNo")

    } catch (e: ParseException) {
        btnVideoCall.visibility = View.GONE
        e.printStackTrace()
    }
}

fun getSlots(dateSelected: String, diff: Long, timetoadd: Int) {
    val slotsList: ArrayList<SlotModel> = ArrayList()
    val mySlotListTemp: ArrayList<SlotModel> = ArrayList()

    val fromTime_ = "09:00 AM"        // yyyy-MM-dd
    val toTime_ = "09:00 PM"        // yyyy-MM-dd
    val date1 = dateSelected        // yyyy-MM-dd
    val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH)

    val dateObjStart = sdf.parse("$date1 $fromTime_") ?: Date()
    val dateObjEnd = sdf.parse("$date1 $toTime_") ?: Date()

    var dif = dateObjStart.time
    while (dif <= dateObjEnd.time) {
        val slot = Date(dif)

        val formateSlot = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(slot)
        Log.wtf("output slot ", formateSlot)
        dif += diff    // 1800000

        // check for to days days time
        val sdff = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val strDate = sdf.parse("$dateSelected $formateSlot")
        Log.wtf("bbbbbb:--", "--" + sdff.parse(dateSelected))

        if (sdff.parse(getCurrentAppDate()) == sdff.parse(dateSelected)) {

            if (System.currentTimeMillis() > strDate.time) {
                slotsList.add(SlotModel(false, formateSlot, false, timetoadd))
                mySlotListTemp.add(SlotModel(false, formateSlot, false, timetoadd))
            } else {
                slotsList.add(SlotModel(false, formateSlot, true, timetoadd))
                mySlotListTemp.add(SlotModel(false, formateSlot, true, timetoadd))
            }
        } else {
            slotsList.add(SlotModel(false, formateSlot, true, timetoadd))
            mySlotListTemp.add(SlotModel(false, formateSlot, true, timetoadd))
        }


    }

    Log.wtf("slotsList:--", "--" + slotsList)
}

fun Activity.openWebLink(webLink: String?) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(webLink)
    val title = "Choose one"
    val chooser = Intent.createChooser(intent, title)
    startActivity(chooser)
}

// import android.content.pm.PackageManager
//import android.util.Base64
//import android.util.Log
//import java.security.MessageDigest
//import java.security.NoSuchAlgorithmException
//
// fun Activity.getAppKeyHash(){
//    try {
//        val info = packageManager.getPackageInfo(
//            "com.rootscare",
//            PackageManager.GET_SIGNATURES)
//        for (signature in info.signatures) {
//            val md = MessageDigest.getInstance("SHA")
//            md.update(signature.toByteArray())
//            Log.wtf("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
//        }
//    } catch (e: PackageManager.NameNotFoundException) {
//
//    } catch (e: NoSuchAlgorithmException) {
//
//    }
//}