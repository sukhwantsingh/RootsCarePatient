package com.rootscare.utilitycommon

import androidx.annotation.Keep
import com.rootscare.ApplicationClass
import com.rootscare.R
import com.rootscare.ui.newaddition.providerlisting.models.ModelTaskListWithPrice
import java.util.*

@Keep
enum class ProviderTypes(private val tp: String, private val dn: String, private val arNm: String) {
    NURSE("nurse", "Nurse","ممرضة"),
    CAREGIVER("caregiver", "Nurse Assistant","مساعد ممرض"),
    BABYSITTER("babysitter", "Babysitter","جليسة أطفال"),
    PHYSIOTHERAPY("physiotherapy", "Physiotherapist","اخصائي العلاج الطبيعي"),
    DOCTOR("doctor", "Doctor","طبيب"),
    HOSPITAL_DOCTOR("hospital_doctor", "Hospital Doctor","طبيب مستشفى"),
    HOSPITAL("hospital", "Hospital","مستشفى"),
    LAB_TECHNICIAN("lab-technician", "Lab-Technician","فني مختبر"),
    PATHOLOGY("pathology", "Pathology","علم الأمراض"),
    ALL("all", "All","الجميع");

    fun getType() = this.tp
    fun getDisplayName() = this.dn
    fun getDisplayHeading() = if (ApplicationClass.instance?.appSharedPref?.languagePref.equals(LanguageModes.ENG.get(), ignoreCase = true)) {
        this.dn } else this.arNm
}

fun getBookingTypeFromProviderType(mType: String? = "") = when (mType?.trim()) {
   // ProviderTypes.HOSPITAL.getType() -> ProviderTypes.HOSPITAL.getDisplayName()
   // ProviderTypes.HOSPITAL_DOCTOR.getType() -> ProviderTypes.HOSPITAL_DOCTOR.getDisplayName()
  //  ProviderTypes.DOCTOR.getType() -> ProviderTypes.DOCTOR.getDisplayName()
  //  ProviderTypes.LAB_TECHNICIAN.getType() -> ProviderTypes.LAB_TECHNICIAN.getDisplayName()
 //   ProviderTypes.PATHOLOGY.getType() -> ProviderTypes.PATHOLOGY.getDisplayName()

    ProviderTypes.DOCTOR.getType() -> BookingTypes.ONLINE_CONS.get()
    ProviderTypes.NURSE.getType() -> BookingTypes.TASK_BASED.get()
    ProviderTypes.CAREGIVER.getType(), ProviderTypes.BABYSITTER.getType() -> BookingTypes.HOURLY_BASED.get()
    ProviderTypes.PHYSIOTHERAPY.getType() -> BookingTypes.TASK_BASED.get()
    else -> BookingTypes.TASK_BASED.get()
}
@Keep
enum class BookingTypes(private val slotTp: String, private val n: String, private val apt: String, private val lanPref: String) {
    TASK_BASED("TASK_BOOKING","Task Booking","task_base","حجز مُهمة"),
    HOURLY_BASED("HOURLY_BOOKING","Hourly Booking","hour_base","حجز بالساعة"),
    ONLINE_CONS("ONLINE","Online Cons","online_task","استشارة عن بُعد"),
    HOME_VISIT("HOME_VISIT","Home Visit","home_visit","زيارة منزلية");

    fun get() = this.n
    fun getSlotType() = this.slotTp
    fun getApiType() = this.apt
    fun getDisplayHeading() = if (ApplicationClass.instance?.appSharedPref?.languagePref.equals(LanguageModes.ENG.get(), ignoreCase = true)) {
        this.n } else this.lanPref

}

@Keep
enum class SarTyps(private val srType: String, private val srArType: String) {
    SAR("SAR","ر.س");

    fun getSar() = if (ApplicationClass.instance?.appSharedPref?.languagePref.equals(LanguageModes.ENG.get(), ignoreCase = true)) {
        this.srType } else this.srArType

}

fun getProvderFromType(mType: String? = "") = when (mType?.trim()) {
    ProviderTypes.HOSPITAL.getType() -> ProviderTypes.HOSPITAL.getDisplayName()
    ProviderTypes.HOSPITAL_DOCTOR.getType() -> ProviderTypes.HOSPITAL_DOCTOR.getDisplayName()
    ProviderTypes.DOCTOR.getType() -> ProviderTypes.DOCTOR.getDisplayName()
    ProviderTypes.NURSE.getType() -> ProviderTypes.NURSE.getDisplayName()
    ProviderTypes.CAREGIVER.getType() -> ProviderTypes.CAREGIVER.getDisplayName()
    ProviderTypes.BABYSITTER.getType() -> ProviderTypes.BABYSITTER.getDisplayName()
    ProviderTypes.PHYSIOTHERAPY.getType() -> ProviderTypes.PHYSIOTHERAPY.getDisplayName()
    ProviderTypes.LAB_TECHNICIAN.getType() -> ProviderTypes.LAB_TECHNICIAN.getDisplayName()
    ProviderTypes.PATHOLOGY.getType() -> ProviderTypes.PATHOLOGY.getDisplayName()
    else -> "Unknown"
}
fun getProvderEmojiFromType(mType: String? = "") = when (mType?.trim()) {
    ProviderTypes.NURSE.getType() -> R.drawable.img_home_nurse
    ProviderTypes.CAREGIVER.getType() -> R.drawable.img_home_caregiver
    ProviderTypes.BABYSITTER.getType() -> R.drawable.img_home_babysitter
    ProviderTypes.PHYSIOTHERAPY.getType() -> R.drawable.img_home_phy
    ProviderTypes.PATHOLOGY.getType() -> R.drawable.img_home_lab
    ProviderTypes.LAB_TECHNICIAN.getType() -> R.drawable.img_home_lab
    ProviderTypes.HOSPITAL.getType() -> R.drawable.img_home_lab
    ProviderTypes.HOSPITAL_DOCTOR.getType() -> R.drawable.img_hosp_doc
    ProviderTypes.DOCTOR.getType() -> R.drawable.img_home_doc
    else -> R.color.colorTxtGrey1
}
@Keep
enum class AppointmentTypes(private val n: String) {
    ALL("All"),
    ONGOING("Ongoing"),
    PENDING("Pending"),
    UPCOMING("Upcoming"),
    PAST("Past");

    fun get() = this.n
}


@Keep
enum class SupportMoreUrls(val eng: String, val ar: String) {
    ABOUTUS("about-us/eng", "about-us/ar"),
    PRIVACY_POLICY("privacy-policy/eng", "privacy-policy/ar"),
    TERMS_CONIDTIONS("terms-and-conditions/eng", "terms-and-conditions/ar");

    fun getEngLink() = API_BASE_URL + this.eng
    fun getArabicLink() = API_BASE_URL + this.ar

}

@Keep
enum class LanguageModes(val lg: String, private val lgLocale: String) {
    ENG("ENG","en"),
    AR("AR","ar");

    fun get() = this.lg
    fun getLangLocale() = this.lgLocale
}
@Keep
enum class GenderType(val gn: String) {
    MALE("Male"),
    FEMALE("Female");

    fun get() = this.gn
}

@Keep
enum class SlotBookingId(private val gn: String) {
    TASK_BOOKING("TASK_BOOKING"),
    HOURLY_BOOKING("HOURLY_BOOKING"),
    ONLINE_BOOKING("ONLINE_BOOKING"),
    HOMEVISIT_BOOKING("HOMEVISIT_BOOKING");

    fun get() = this.gn
}

@Keep
enum class DoctorEnabledFor(private val def: String) {
    ONLINE("ONLINE_CONSULT"),
    HOME_VIST("HOME_VISIT_CONSULT");

    fun get() = this.def
}

fun homeBottomMenuMoreOptions() = arrayListOf("Profile")

@Keep
enum class HomeBottomMenu(private val n_: String) {
    PROFILE("Profile");

    fun get() = this.n_
}

@Keep
enum class TransactionStatus(private val ts: String, private val clr: String) {
    PENDING("Pending", "#F9D800"),
    PAID("Paid", "#4FB82A"),
    ACCEPTED("Accepted", "#4FB82A"),
    COMPLETED("Completed", "#4FB82A"),
    SUCCESS("Success", "#4FB82A"),
    CANCELLED("Cancelled", "#FF4E00"),
    REJECTED("Rejected", "#FF4E00"),
    ELSE("Else", "#515C6F");

    fun get() = this.ts
    fun getColor() = this.clr
}

// appointment providers type
fun appointmentsPrefLs(): ArrayList<String?> = arrayListOf(ProviderTypes.ALL.getDisplayHeading(),
        ProviderTypes.NURSE.getDisplayHeading(),
        ProviderTypes.CAREGIVER.getDisplayHeading(),
        ProviderTypes.BABYSITTER.getDisplayHeading(),
        ProviderTypes.PHYSIOTHERAPY.getDisplayHeading(),
        ProviderTypes.DOCTOR.getDisplayHeading()
        /*,"Hospital"*/)
  //  arrayListOf("All","Nurse","Nurse Assistant","Babysitter","Physiotherapy" /*, "Doctor","Hospital"*/)

// Life style listing static data
fun yesNoLs(): ArrayList<String?> = arrayListOf("Yes", "No")
fun bloodGroupsLs(): ArrayList<String?> =
    arrayListOf("A+", "O+", "B+", "AB+", "A-", "O-", "B-", "AB-")

fun activityLevelLs(): ArrayList<String?> = arrayListOf(
    "Extremely inactive", "Sedentary", "Moderately active", "Vigorously active", "Extremely active"
)

fun foodPrefLs(): ArrayList<String?> =
    arrayListOf("Standard", "Pescetarian", "Vegetarian", "Lacto-vegetarian", "Vegan")

fun occupationLs(): ArrayList<String?> = arrayListOf(
    "Technician", "Teacher", "Machinist", "Radiographer", "Technologist",
    "Electrician", "Engineering technician", "Actuary", "Tradesman",
    "Medical laboratory scientist", "Quantity surveyor", "Prosthetist",
    "Paramedic", "Bricklayer", "Special Education Teacher", "Lawyer", "Physician", "Other"
)

fun martialStatusLs(): ArrayList<String?> =
    arrayListOf("Married", "Widowed", "Separated", "Divorced", "Single")
