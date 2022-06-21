package com.rootscare.utilitycommon

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.customview.MyEditTextView
import com.rootscare.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


@BindingAdapter("visibilityWithNullEmptyCheck")
fun View.visibilityWithNullEmptyCheck(vl: Any?) {
    val mVisi = when (vl) {
        is String -> {
            if (vl.isNotBlank()) View.VISIBLE else View.GONE
        }
        is Boolean -> View.VISIBLE
        else -> View.GONE
    }
    this.visibility = mVisi
}

@BindingAdapter("goneWith10")
fun View.goneWith10(vl: String?) {
    val mVisi = when (vl) {
        "0" -> View.VISIBLE
        else -> View.GONE
    }
    this.visibility = mVisi
}

@BindingAdapter("invisibleWith10")
fun View.invisibleWith10(vl: String?) {
    val mVisi = when (vl) {
        "0" -> View.VISIBLE
        else -> View.INVISIBLE
    }
    this.visibility = mVisi
}

@BindingAdapter("enableDisableGreenWith01")
fun TextView.enableDisableGreenWith01(vl: String?) {
  when (vl) {
      "0" -> {
      this.setBackgroundResource(R.drawable.square_bg_green)
      this.isEnabled = true
      }
      else -> {
      this.setBackgroundResource(R.drawable.square_bg_grey)
      this.isEnabled = false
      }
  }
}
@BindingAdapter("enableDisableBlueWith01")
fun TextView.enableDisableBlueWith01(vl: String?) {
  when (vl) {
      "0" -> {
      this.setBackgroundResource(R.drawable.square_bg)
      this.isEnabled = true
      }
      else -> {
      this.setBackgroundResource(R.drawable.square_bg_grey)
      this.isEnabled = false
      }
  }
}

@BindingAdapter("changeColorWithAvailability")
fun TextView.changeColorWithAvailability(checkVl: String?) {
    // 0 is for available 1 is for not available
    if(checkVl.equals("0",ignoreCase = true)){
        this.setTextColor(Color.parseColor("#4FB82A"))
    } else {
        this.setTextColor(Color.parseColor("#8F98A7"))
    }


}

@BindingAdapter("visibleWithTF")
fun View.visibleWithTF(vl: Boolean?) {
    val mVisi = when (vl) {
        true -> View.VISIBLE
        false -> View.GONE
        else -> View.GONE
    }
    this.visibility = mVisi
}

@BindingAdapter(value = ["avText","status"], requireAll = true)
fun TextView.setAvailabilityHeadText(text: String?, status:String?) {
  val visiText =  when (status) {
        "1" -> {}
        "0" ->{}
        else -> {}
    }

}

@BindingAdapter("changeColorWithCheck")
fun TextView.changeColorWithCheck(checkStatus: Boolean? = false) {
    checkStatus?.let {
        if (it) this.setTextColor(Color.parseColor("#0888D1"))
        else this.setTextColor(Color.parseColor("#8F98A7"))
    }

}

@BindingAdapter("setAmount")
fun TextView.setAmount(amt: String?) {
  //  this@setAmount.text = amt?.let { "$amt ${SarTyps.SAR.getSar()}" } ?: ""
    this@setAmount.text = amt?.let { "$amt" } ?: ""
}

@BindingAdapter("setAmount")
fun TextView.setAmount(amt: Int?) {
 //   this@setAmount.text = amt?.let { "$amt ${SarTyps.SAR.getSar()}" } ?: ""
    this@setAmount.text = amt?.let { "$amt" } ?: ""
}
@BindingAdapter("setAmount")
fun TextView.setAmount(amt: Double?) {
  //  this@setAmount.text = amt?.let { "$amt ${SarTyps.SAR.getSar()}" } ?: ""
    this@setAmount.text = amt?.let { "$amt" } ?: ""
}

@SuppressLint("SetTextI18n")
@BindingAdapter(value = ["amount", "currencyIf"] , requireAll = true)
fun TextView.setAmountWithCurrency(amt: Any?, currency: String?) {
  amt?.let {
      when(it){
          is String ->  this@setAmountWithCurrency.text ="$it ${currency?: "SAR"}"
          is Int ->  this@setAmountWithCurrency.text = "$it ${currency?: "SAR"}"
          is Float ->  this@setAmountWithCurrency.text = "$it ${currency?: "SAR"}"
          is Double ->  this@setAmountWithCurrency.text = "$it ${currency?: "SAR"}"
          else -> this@setAmountWithCurrency.text = "$it"
      }
  }


}

@BindingAdapter("setAvgRating")
fun TextView.setAvgRating(rating: String?) {
    this@setAvgRating.text = (rating?.toFloat() ?: 0.0).toString()

}

@BindingAdapter("setCircularRemoteImageWithNoImage")
fun CircleImageView.setCircularRemoteImageWithNoImage(url_: String?) {
    try {
        if(url_.isNullOrBlank()){ this.borderWidth =0} else this.borderWidth =1

        val options: RequestOptions = RequestOptions().placeholder(R.drawable.no_img_logo).priority(Priority.HIGH)
        this.let {
            Glide.with(this.context).load("${BaseMediaUrls.USERIMAGE.url}${url_?.trim()}")
                .apply(options).into(it)
        }
    } catch (e: Exception) {
        println(e)
    }
}

@BindingAdapter("setCircularRemoteImage")
fun CircleImageView.setCircularRemoteImage(url_: String?) {
    try {
        val options: RequestOptions =
            RequestOptions().placeholder(R.drawable.profile_no_image).priority(Priority.HIGH)
        this.let {
            Glide.with(this.context).load("${BaseMediaUrls.USERIMAGE.url}${url_?.trim()}")
                .apply(options).into(it)
        }
    } catch (e: Exception) {
        println(e)
    }

}

@BindingAdapter("setSquareRemoteImage")
fun ImageView.setSquareRemoteImage(url_: String?) {
    try {
        val options: RequestOptions =
            RequestOptions().placeholder(R.color.colorTxtGrey1).priority(Priority.HIGH)
        this.let {
            Glide.with(this.context).load("${BaseMediaUrls.USERIMAGE.url}${url_?.trim()}")
                .apply(options).into(it)
        }
    } catch (e: Exception) {
        println(e)
    }

}

@BindingAdapter("setRemotePrescImage")
fun ImageView.setRemotePrescImage(url_: String?) {
    val options: RequestOptions =
        RequestOptions().placeholder(R.drawable.ic_presc).priority(Priority.HIGH)
    this.let {
        Glide
            .with(this.context)
            .load("${BaseMediaUrls.USERIMAGE.url}${url_?.trim()}")
            .apply(options)
            .into(it)
    }
}

@BindingAdapter("changeColorWithCheck")
fun MyEditTextView.changeColorWithCheck(checkStatus: Boolean? = false) {
    checkStatus?.let {
        if (it) this.setTextColor(Color.parseColor("#041A27"))
        else this.setTextColor(Color.parseColor("#8F98A7"))
    }

}

@BindingAdapter("backgroundStatusColor")
fun TextView.backgroundStatusColor(mStatus: String? = "") {
    when {
        mStatus.equals(TransactionStatus.PAID.get(), ignoreCase = true) ||
                mStatus.equals(TransactionStatus.COMPLETED.get(), ignoreCase = true) ||
                mStatus.equals(TransactionStatus.ACCEPTED.get(), ignoreCase = true) ||
                mStatus.equals(TransactionStatus.SUCCESS.get(), ignoreCase = true) -> {
            this.setBackgroundColor(Color.parseColor(TransactionStatus.PAID.getColor()))
        }
        mStatus.equals(TransactionStatus.PENDING.get(), ignoreCase = true) -> {
            this.setBackgroundColor(Color.parseColor(TransactionStatus.PENDING.getColor()))
        }
        mStatus.equals(TransactionStatus.CANCELLED.get(), ignoreCase = true) ||
                mStatus.equals(TransactionStatus.REJECTED.get(), ignoreCase = true) -> {
            this.setBackgroundColor(Color.parseColor(TransactionStatus.CANCELLED.getColor()))
        }
        else -> this.setBackgroundColor(Color.parseColor(TransactionStatus.ELSE.getColor()))
    }

}

@BindingAdapter("setDisplayUserType")
fun TextView.setDisplayUserType(mType: String? = "") {
    this.text = when (mType?.trim()) {
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

}

@BindingAdapter("needToShowSpeciality")
fun TextView.needToShowSpeciality(mType: String? = "") {
    this.visibility = when (mType?.trim()) {
        ProviderTypes.CAREGIVER.getType(), ProviderTypes.BABYSITTER.getType() -> View.GONE
        else -> View.VISIBLE
    }

}







