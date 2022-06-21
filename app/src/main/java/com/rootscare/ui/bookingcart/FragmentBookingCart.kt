package com.rootscare.ui.bookingcart

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.dialog.CommonDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.interfaces.DialogClickCallback
import com.interfaces.OnClickOfCartItem
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.cartitemdeleterequest.CartItemDeleteRequest
import com.rootscare.data.model.api.request.doctorrequest.bookingcartrequests.BookingCartRequest
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse.cartitemdeleteresponse.DoctorCartItemDeleteResponse
import com.rootscare.databinding.LayoutNewCartScreenBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.bookingappointment.managers.SettingsManager
import com.rootscare.ui.bookingcart.adapter.AdapterCartListingNew
import com.rootscare.ui.bookingcart.models.ModelPatientCartNew
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.supportmore.bottomsheet.DialogThankyou
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback
import com.rootscare.utilitycommon.PAYMENT_KEY_
import com.rootscare.utilitycommon.SUCCESS_CODE
import company.tap.gosellapi.GoSellSDK
import company.tap.gosellapi.internal.api.callbacks.GoSellError
import company.tap.gosellapi.internal.api.models.*
import company.tap.gosellapi.open.controllers.SDKSession
import company.tap.gosellapi.open.controllers.ThemeObject
import company.tap.gosellapi.open.delegate.SessionDelegate
import company.tap.gosellapi.open.enums.AppearanceMode
import company.tap.gosellapi.open.enums.TransactionMode
import company.tap.gosellapi.open.models.CardsList
import company.tap.gosellapi.open.models.Customer
import company.tap.gosellapi.open.models.Receipt
import company.tap.gosellapi.open.models.TapCurrency
import company.tap.gosellapi.open.viewmodel.CustomerViewModel
import kotlinx.android.synthetic.main.common_toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class FragmentBookingCart: BaseFragment<LayoutNewCartScreenBinding, FragmentBookingCartViewModel>(),
    FragmentBookingCartNavigator, SessionDelegate {
    private var fragmentBookingCartBinding: LayoutNewCartScreenBinding? = null
    private var fragmentBookingCartViewModel: FragmentBookingCartViewModel? = null

  //  var adapterBookingCartRecyclerview: AdapterBookingCartRecyclerview? = null
    var adapterBookingCartRecyclerview: AdapterCartListingNew? = null

    var subTotalPrice: Double = 0.0
    var vatPrice: Double = 0.0
    var totalPaidPrice: Double = 0.0
    var resultCartItemIds :String? = ""
    var resultServiceType :String? = ""

    private var adapter: RecyclerView.Adapter<*>? = null

    private val SDK_REQUEST_CODE = 1001
    private var sdkSession: SDKSession? = null
    private var settingsManager: SettingsManager? = null
    private var progress: ProgressDialog? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.layout_new_cart_screen
    override val viewModel: FragmentBookingCartViewModel
        get() {
            fragmentBookingCartViewModel =
                ViewModelProviders.of(this).get(FragmentBookingCartViewModel::class.java)
            return fragmentBookingCartViewModel as FragmentBookingCartViewModel
        }

    private var isTimerRunning = true
    private var alertPopup: DialogThankyou? = null

    companion object {
        fun newInstance(): FragmentBookingCart {
            val args = Bundle()
            val fragment = FragmentBookingCart()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentBookingCartViewModel?.navigator = this
        settingsManager = SettingsManager.instance
        settingsManager?.setPref(activity)
        // start tap goSellSDK

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBookingCartBinding = viewDataBinding
        fragmentBookingCartBinding?.lifecycleOwner = this
        adapterBookingCartRecyclerview = AdapterCartListingNew(requireContext())
        adapterBookingCartRecyclerview?.mCurrency = fragmentBookingCartViewModel?.appSharedPref?.currencySymbol
//        getCustomerSavedCardsList(view)
//        setUpNotificationRecyclerview()

        getCartItem()

        fragmentBookingCartBinding?.btnGotohome?.setOnClickListener {
          (activity as? HomeActivity)?.cartToHome()
        }
        fragmentBookingCartBinding?.btnCartCheckout?.setOnClickListener {
            // start tap goSellSDK
          startSDKWithUI()
        }

    }

    private fun makePayment(chargeId:String) {
     Log.wtf("CART ITEM ID STRING", resultCartItemIds)
     if (resultCartItemIds.isNullOrBlank().not()) {
          apiAfterSuccessPayment(resultServiceType,chargeId,resultCartItemIds)
           // bookingCheckoutApiCall(subTotalPrice.toString(),
        // vatPrice.toString(), totalPaidPrice.toString(),resultCartItemIds?:"")
        } else {
           showToast(getString(R.string.select_item_to_checkout))
        }

    }

    // Set up recycler view for service listing if available
    private fun setUpNotificationRecyclerview(cartItemList: ArrayList<ModelPatientCartNew.Result?>?) {
    //    adapterBookingCartRecyclerview = AdapterBookingCartRecyclerview(cartItemList, requireContext())
        fragmentBookingCartBinding?.recyclerViewRootscareBookingcart?.adapter = adapterBookingCartRecyclerview
        adapterBookingCartRecyclerview?.updatedArrayList?.clear()
        adapterBookingCartRecyclerview?.loadDataIntoList(cartItemList)
        adapterBookingCartRecyclerview?.mCallback = object : OnClickOfCartItem {
            override fun onFirstItemClick(id: Int) {
                CommonDialog.showDialog(requireActivity(), object : DialogClickCallback {
                  override fun onConfirm() {
                        if (isNetworkConnected) {
                            baseActivity?.showLoading()
                            val req = CartItemDeleteRequest()
                            req.id = id.toString()
                            fragmentBookingCartViewModel?.apiDeletePatientCart(req)

                        } else {
                           showToast(getString(R.string.check_network_connection))
                        }
                    }

                }, getString(R.string.confirmation), getString(R.string.are_you_sure_to_delete), getString(R.string.delete))
            }

            override fun onSecondItemClick(cartItemList: ArrayList<ModelPatientCartNew.Result?>?) {
                subTotalPrice = 0.0
                vatPrice = 0.0
                totalPaidPrice = 0.0
                val idArray = ArrayList<String?>()
                cartItemList?.forEach {
                    if (it?.isselectitem == true) {
                        subTotalPrice += it.sub_total_price?.toDoubleOrNull() ?: 0.0
                        vatPrice += it.vat_price?.toDoubleOrNull() ?: 0.0
                        totalPaidPrice += it.total_price?.toDoubleOrNull() ?: 0.0
                        idArray.add(it.id)
                    }

                }
                resultCartItemIds = idArray.joinToString(separator = ",")
                Log.wtf("CART ITEM ID STRING", resultCartItemIds)
                // check to enable checkout button
                val mFindItemSelected = cartItemList?.find{it?.isselectitem==true}

                if(mFindItemSelected != null){
                    fragmentBookingCartBinding?.btnCartCheckout?.visibility = View.VISIBLE
                }else {
                    fragmentBookingCartBinding?.btnCartCheckout?.visibility = View.GONE
                }

            }

        }

    }

    override fun successBookingCartResponse(response: ModelPatientCartNew?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE,ignoreCase = true)) {
            fragmentBookingCartBinding?.run {
                btnCartCheckout.visibility = View.VISIBLE
            }
            if (response?.result.isNullOrEmpty().not()) {
                resultCartItemIds = response?.result?.getOrNull(0)?.id ?:""
                resultServiceType =  response?.result?.getOrNull(0)?.service_type ?:""
                fragmentBookingCartBinding?.run {
                    recyclerViewRootscareBookingcart.visibility = View.VISIBLE
                    llNodata.visibility = View.GONE
                }
                setUpNotificationRecyclerview(response?.result)
            } else {
                fragmentBookingCartBinding?.run {
                    recyclerViewRootscareBookingcart.visibility = View.GONE
                    llNodata.visibility = View.VISIBLE
                    tvNoDate.text = response?.message
                }
            }
             response?.result?.forEach { totalPaidPrice += it?.total_price?.toDoubleOrNull() ?: 0.0 }
            try {
             startSDK()
            } catch (e:Exception){
               Log.wtf("payment", e)
            }
        } else {
            fragmentBookingCartBinding?.run{
                recyclerViewRootscareBookingcart.visibility = View.GONE
                btnCartCheckout.visibility = View.GONE
                tvNoDate.text = response?.message
                llNodata.visibility = View.VISIBLE
            }
        }
    }

    override fun successDoctorCartItemDeleteResponse(response: DoctorCartItemDeleteResponse?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE,ignoreCase = true)) {
            getCartItem()
            subTotalPrice = 0.0
            vatPrice = 0.0
            totalPaidPrice = 0.0
        }
        showToast(response?.message?: getString(R.string.something_went_wrong))
    }

    override fun errorBookingCartResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        showToast(throwable?.message?: getString(R.string.something_went_wrong))
    }

   private fun getCartItem() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val bookingCartRequest = BookingCartRequest()
            bookingCartRequest.userId = fragmentBookingCartViewModel?.appSharedPref?.userId
            fragmentBookingCartViewModel?.apiPatientCart(bookingCartRequest)

        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }

   private fun apiAfterSuccessPayment(servType: String?, transId: String, cartId: String?) {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val jsonObject = JsonObject().apply {
                addProperty("service_type", servType)
                addProperty("cart_id", cartId)
                addProperty("trid", transId)
                addProperty("login_user_id", fragmentBookingCartViewModel?.appSharedPref?.userId)
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
             fragmentBookingCartViewModel?.apiAfterPaymentHit(body)
        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }

    override fun onAfterPaymentSuccess(response: CommonResponse?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE,ignoreCase = true)) {
            fragmentBookingCartBinding?.btnCartCheckout?.visibility = View.GONE
            gotoAppointment()
            alertPopup = DialogThankyou.newInstance( btsCallback, response?.message, getString(R.string.goto_appointment))
            alertPopup?.show(childFragmentManager, "ThankyouDialog")

        } else {
            getCartItem()
            showToast(response?.message?: getString(R.string.something_went_wrong))
        }
    }

    private val btsCallback = object: OnBottomSheetCallback {
        override fun onCloseBottomSheet() {
            isTimerRunning = false
            (activity as? HomeActivity)?.openAppointmentfromBottomMenu()
        }
    }

    private fun gotoAppointment() {
        try {
            isTimerRunning = true
            Handler(Looper.getMainLooper()).postDelayed({
                if(isTimerRunning) {
                    isTimerRunning = false
                    alertPopup?.dismiss()
                    (activity as? HomeActivity)?.openAppointmentfromBottomMenu()
                    // checkInBackstack(FragNewAppointmentListing.newInstance(AppointmentTypes.ALL.get()))
                }
            },4000)
        }catch (e:Exception){
            Log.e("Error", "$e")
        }

    }

    /**
     * Integrating SDK.
     */
    private fun startSDK() {
        /**
         * Required step.
         * Configure SDK with your Secret API key and App Bundle name registered with tap company.
         */
        configureApp()
        /**
         * Optional step
         * Here you can configure your app theme (Look and Feel).
         */
        configureSDKThemeObject()
        /**
         * Required step.
         * Configure SDK Session with all required data.
         */
        configureSDKSession()
        /**
         * Required step.
         * Choose between different SDK modes
         */
//        configureSDKMode()
        /**
         * If you included Tap Pay Button then configure it first, if not then ignore this step.
         */
        initPayButton()
    }

    /**
     * Required step.
     * Configure SDK with your Secret API key and App Bundle name registered with tap company.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun configureApp() {
//        GoSellSDK.init(
//            activity,
//            "sk_test_kovrMB0mupFJXfNZWx6Etg5y",
//            "company.tap.goSellSDKExample"
//        ) // to be replaced by merchant
//        GoSellSDK.init(activity, "sk_test_KOfdbVzDXW7JreslyPL2g1nN","com.rootscare" ) // to be replaced by merchant
       GoSellSDK.init(activity, PAYMENT_KEY_,"com.rootscare" ) // to be replaced by merchant
  //     GoSellSDK.setLocale(getAppLocale()) //  language to be set by merchant
    }

    /**
     * Configure SDK Theme
     */
    private fun configureSDKThemeObject() {
        ThemeObject.getInstance()
            .setAppearanceMode(AppearanceMode.WINDOWED_MODE)
//            .setSdkLanguage(getAppLocale())
            .setHeaderFont(Typeface.createFromAsset(requireActivity().assets,"font/roboto_light.ttf"))
            .setHeaderTextColor(resources.getColor(R.color.black1))
            .setHeaderTextSize(17)
            .setHeaderBackgroundColor(resources.getColor(R.color.french_gray_new))
            .setCardInputFont(Typeface.createFromAsset(requireActivity().assets,"font/roboto_light.ttf"))
            .setCardInputTextColor(resources.getColor(R.color.black))
            .setCardInputInvalidTextColor(resources.getColor(R.color.red))
            .setCardInputPlaceholderTextColor(resources.getColor(R.color.gray))
            .setSaveCardSwitchOffThumbTint(resources.getColor(R.color.french_gray_new))
            .setSaveCardSwitchOnThumbTint(resources.getColor(R.color.vibrant_green))
            .setSaveCardSwitchOffTrackTint(resources.getColor(R.color.french_gray))
            .setSaveCardSwitchOnTrackTint(resources.getColor(R.color.vibrant_green_pressed))
            .setScanIconDrawable(resources.getDrawable(R.drawable.btn_card_scanner_normal))
            .setPayButtonResourceId(R.drawable.btn_pay_selector) //btn_pay_merchant_selector
            .setPayButtonFont(Typeface.createFromAsset(requireActivity().assets,"font/roboto_light.ttf"))
            .setPayButtonDisabledTitleColor(resources.getColor(R.color.white))
            .setPayButtonEnabledTitleColor(resources.getColor(R.color.white))
            .setPayButtonTextSize(14)
            .setPayButtonLoaderVisible(true)
            .setPayButtonSecurityIconVisible(true) //.setPayButtonText("PAY BTN CAN BE VERY VERY VERY  LONG LONG") // **Optional**
            // setup dialog text color and text size
            .setDialogTextColor(resources.getColor(R.color.black1)).dialogTextSize = 17 // **Optional**
    }

    /**
     * Configure SDK Session
     */
    private fun configureSDKSession() {

        // Instantiate SDK Session
        if (sdkSession == null) sdkSession = SDKSession() //** Required **

        // pass your activity as a session delegate to listen to SDK internal payment process follow
        sdkSession!!.addSessionDelegate(this) //** Required **

        // initiate PaymentDataSource
        sdkSession!!.instantiatePaymentDataSource() //** Required **

        // set transaction currency associated to your account
        sdkSession!!.setTransactionCurrency(TapCurrency(fragmentBookingCartViewModel?.appSharedPref?.currencySymbol ?: "SAR")) //** Required **

        // Using static CustomerBuilder method available inside TAP Customer Class you can populate TAP Customer object and pass it to SDK
        sdkSession!!.setCustomer(customer) //** Required **

        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping
        sdkSession!!.setAmount(BigDecimal(totalPaidPrice)) //** Required **

        // Set Payment Items array list
        sdkSession!!.setPaymentItems(ArrayList()) // ** Optional ** you can pass empty array list


//       sdkSession.setPaymentType("CARD");   //** Merchant can pass paymentType

        // Set Taxes array list
        sdkSession!!.setTaxes(ArrayList()) // ** Optional ** you can pass empty array list

        // Set Shipping array list
        sdkSession!!.setShipping(ArrayList()) // ** Optional ** you can pass empty array list

        // Post URL
        sdkSession!!.setPostURL("") // ** Optional **

        // Payment Description
        sdkSession!!.setPaymentDescription("") //** Optional **

        // Payment Extra Info
        sdkSession!!.setPaymentMetadata(HashMap()) // ** Optional ** you can pass empty array hash map

        // Payment Reference
        sdkSession!!.setPaymentReference(null) // ** Optional ** you can pass null

        // Payment Statement Descriptor
        sdkSession!!.setPaymentStatementDescriptor("") // ** Optional **

        // Enable or Disable Saving Card
        sdkSession!!.isUserAllowedToSaveCard(true) //  ** Required ** you can pass boolean

        // Enable or Disable 3DSecure
        sdkSession!!.isRequires3DSecure(true)

        //Set Receipt Settings [SMS - Email ]
        sdkSession!!.setReceiptSettings(
            Receipt(false,false)) // ** Optional ** you can pass Receipt object or null

        // Set Authorize Action
        sdkSession!!.setAuthorizeAction(null) // ** Optional ** you can pass AuthorizeAction object or null
        sdkSession!!.setDestination(null) // ** Optional ** you can pass Destinations object or null
        sdkSession!!.setMerchantID(null) // ** Optional ** you can pass merchant id or null
     //   sdkSession!!.setCardType(CardType.ALL) // ** Optional ** you can pass which cardType[CREDIT/DEBIT] you want.By default it loads all available cards for Merchant.

        // sdkSession.setDefaultCardHolderName("TEST TAP"); // ** Optional ** you can pass default CardHolderName of the user .So you don't need to type it.
        // sdkSession.isUserAllowedToEnableCardHolderName(false); // ** Optional ** you can enable/ disable  default CardHolderName .
    }

    /**
     * Configure SDK Theme
     */
    private fun configureSDKMode() {
        /**
         * You have to choose only one Mode of the following modes:
         * Note:-
         * - In case of using PayButton, then don't call sdkSession.start(this); because the SDK will start when user clicks the tap pay button.
         */
        //////////////////////////////////////////////////////    SDK with UI //////////////////////
        /**
         * 1- Start using  SDK features through SDK main activity (With Tap CARD FORM)
         */
        startSDKWithUI()
    }

    /**
     * Start using  SDK features through SDK main activity
     */
    private fun startSDKWithUI() {
        if (sdkSession != null) {
            try {
                baseActivity?.showLoading(getString(R.string.payment_initiation))
                val trx_mode =
                    if (settingsManager != null) settingsManager?.getTransactionsMode("key_sdk_transaction_mode") else TransactionMode.PURCHASE
                // set transaction mode [TransactionMode.PURCHASE - TransactionMode.AUTHORIZE_CAPTURE - TransactionMode.SAVE_CARD - TransactionMode.TOKENIZE_CARD ]
                sdkSession?.transactionMode = trx_mode //** Required **
                // if you are not using tap button then start SDK using the following call
                sdkSession?.start(activity)
            } catch (e:Exception){
                baseActivity?.hideLoading()
                println(e)
            }

        }
    }

    /**
     * Include pay button in merchant page
     */
    private fun initPayButton() {
//        if (ThemeObject.getInstance().payButtonFont != null) fragmentBookingCartBinding?.btnCartCheckout?.setupFontTypeFace(
//            ThemeObject.getInstance().payButtonFont
//        )
//        if (ThemeObject.getInstance().payButtonDisabledTitleColor != 0 && ThemeObject.getInstance().payButtonEnabledTitleColor != 0)
//            fragmentBookingCartBinding?.btnCartCheckout?.setupTextColor(
//                ThemeObject.getInstance().payButtonEnabledTitleColor,
//                ThemeObject.getInstance().payButtonDisabledTitleColor
//            )
//        if (ThemeObject.getInstance().payButtonTextSize != 0) fragmentBookingCartBinding?.btnCartCheckout?.payButton?.textSize =
//            ThemeObject.getInstance().payButtonTextSize.toFloat()
//        //
//        if (ThemeObject.getInstance().isPayButtSecurityIconVisible) fragmentBookingCartBinding?.btnCartCheckout?.securityIconView?.visibility =
//            if (ThemeObject.getInstance().isPayButtSecurityIconVisible) View.VISIBLE else View.INVISIBLE
//        if (ThemeObject.getInstance().payButtonResourceId != 0) fragmentBookingCartBinding?.btnCartCheckout?.setBackgroundSelector(
//            ThemeObject.getInstance().payButtonResourceId
//        )
        if (sdkSession != null) {
//            val trx_mode = sdkSession!!.transactionMode
//            if (trx_mode != null) {
//                if (TransactionMode.SAVE_CARD == trx_mode || TransactionMode.SAVE_CARD_NO_UI == trx_mode) {
//                    fragmentBookingCartBinding?.btnCartCheckout?.payButton?.text =
//                        getString(company.tap.gosellapi.R.string.save_card)
//                } else if (TransactionMode.TOKENIZE_CARD == trx_mode || TransactionMode.TOKENIZE_CARD_NO_UI == trx_mode) {
//                    fragmentBookingCartBinding?.btnCartCheckout?.payButton?.text =
//                        getString(company.tap.gosellapi.R.string.tokenize)
//                } else {
//                    fragmentBookingCartBinding?.btnCartCheckout?.payButton?.text =
//                        getString(company.tap.gosellapi.R.string.pay)
//                }
//            } else {
//                configureSDKMode()
//            }
            sdkSession!!.setButtonView(
                fragmentBookingCartBinding?.btnCartCheckout,
                activity,
                SDK_REQUEST_CODE
            )
        }
    }
    //    //////////////////////////////////////////////////////  List Saved Cards  ////////////////////////
    /**
     * retrieve list of saved cards from the backend.
     */
    private fun listSavedCards() {
        if (sdkSession != null) sdkSession!!.listAllCards("cus_s4H13120191115x0R12606480", this)
    }

    //    //////////////////////////////////////////////////////  Overridden section : Session Delegation ////////////////////////
    override fun paymentSucceed(charge: Charge) {
       Log.wtf("payment","Payment Succeeded : charge status : " + charge.status)
       Log.wtf("payment","Payment Succeeded : description : " + charge.description)
       Log.wtf("payment","Payment Succeeded : message : " + charge.response.message)
       Log.wtf("payment","##########################################")
        if (charge.card != null) {
           Log.wtf("payment","Payment Succeeded : first six : " + charge.card!!.firstSix)
           Log.wtf("payment","Payment Succeeded : last four: " + charge.card!!.last4)
           Log.wtf("payment","Payment Succeeded : card object : " + charge.card!!.getObject())
           Log.wtf("payment","Payment Succeeded : brand : " + charge.card!!.brand)
//           Log.wtf("payment","Payment Succeeded : expiry : " + charge.card!!.expiry!!.month + "\n" + charge.card!!.expiry!!.year)
        }
       Log.wtf("payment","##########################################")
        if (charge.acquirer != null) {
           Log.wtf("payment","Payment Succeeded : acquirer id : " + charge.acquirer!!.id)
           Log.wtf("payment","Payment Succeeded : acquirer response code : " + charge.acquirer!!.response.code)
           Log.wtf("payment","Payment Succeeded : acquirer response message: " + charge.acquirer!!.response.message)
        }
       Log.wtf("payment","############################################")
        if (charge.source != null) {
           Log.wtf("payment","Payment Succeeded : source id: " + charge.source.id)
           Log.wtf("payment","Payment Succeeded : source channel: " + charge.source.channel)
           Log.wtf("payment","Payment Succeeded : source object: " + charge.source.getObject())
           Log.wtf("payment","Payment Succeeded : source payment method: " + charge.source.paymentMethodStringValue)
           Log.wtf("payment","Payment Succeeded : source payment type: " + charge.source.paymentType)
           Log.wtf("payment","Payment Succeeded : source type: " + charge.source.type)
        }
       Log.wtf("payment","#############################################")
        if (charge.expiry != null) {
           Log.wtf("payment","Payment Succeeded : expiry type :" + charge.expiry!!.type)
           Log.wtf("payment","Payment Succeeded : expiry period :" + charge.expiry!!.period)
        }

        saveCustomerRefInSession(charge)
        configureSDKSession()
    //    showDialog(charge.id, charge.response.message, company.tap.gosellapi.R.drawable.ic_checkmark_normal)
        makePayment(charge.id)

    }


    override fun paymentFailed(charge: Charge?) {
        baseActivity?.hideLoading()
       Log.wtf("payment","Payment Failed : " + charge!!.status)
       Log.wtf("payment","Payment Failed : " + charge.description)
       Log.wtf("payment","Payment Failed : " + charge.response.message)
       showDialog(charge.id, charge.response.message, company.tap.gosellapi.R.drawable.icon_failed)
    }

    override fun authorizationSucceed(authorize: Authorize) {
       Log.wtf("payment","Authorize Succeeded : " + authorize.status)
       Log.wtf("payment","Authorize Succeeded : " + authorize.response.message)
        if (authorize.card != null) {
           Log.wtf("payment","Payment Authorized Succeeded : first six : " + authorize.card!!.firstSix)
           Log.wtf("payment","Payment Authorized Succeeded : last four: " + authorize.card!!.last4)
           Log.wtf("payment","Payment Authorized Succeeded : card object : " + authorize.card!!.getObject())
        }
       Log.wtf("payment","##############################################################################")
        if (authorize.acquirer != null) {
           Log.wtf("payment","Payment Authorized Succeeded : acquirer id : " + authorize.acquirer!!.id)
           Log.wtf("payment",
                "Payment Authorized Succeeded : acquirer response code : " + authorize.acquirer!!
                    .response.code
            )
           Log.wtf("payment",
                "Payment Authorized Succeeded : acquirer response message: " + authorize.acquirer!!
                    .response.message
            )
        }
       Log.wtf("payment","##############################################################################")
        if (authorize.source != null) {
           Log.wtf("payment","Payment Authorized Succeeded : source id: " + authorize.source.id)
           Log.wtf("payment","Payment Authorized Succeeded : source channel: " + authorize.source.channel)
           Log.wtf("payment","Payment Authorized Succeeded : source object: " + authorize.source.getObject())
           Log.wtf("payment","Payment Authorized Succeeded : source payment method: " + authorize.source.paymentMethodStringValue)
           Log.wtf("payment","Payment Authorized Succeeded : source payment type: " + authorize.source.paymentType)
           Log.wtf("payment","Payment Authorized Succeeded : source type: " + authorize.source.type)
        }
       Log.wtf("payment","##############################################################################")
        if (authorize.expiry != null) {
           Log.wtf("payment","Payment Authorized Succeeded : expiry type :" + authorize.expiry!!.type)
           Log.wtf("payment","Payment Authorized Succeeded : expiry period :" + authorize.expiry!!.period)
        }
        saveCustomerRefInSession(authorize)
        configureSDKSession()
        showDialog(
            authorize.id,
            authorize.response.message,
            company.tap.gosellapi.R.drawable.ic_checkmark_normal
        )
    }

    override fun authorizationFailed(authorize: Authorize) {
        baseActivity?.hideLoading()
       Log.wtf("payment","Authorize Failed : " + authorize.status)
       Log.wtf("payment","Authorize Failed : " + authorize.description)
       Log.wtf("payment","Authorize Failed : " + authorize.response.message)
        showDialog(
            authorize.id,
            authorize.response.message,
            company.tap.gosellapi.R.drawable.icon_failed
        )
    }

    override fun cardSaved(charge: Charge) {
        // Cast charge object to SaveCard first to get all the Card info.
        if (charge is SaveCard) {
           Log.wtf("payment",
                "Card Saved Succeeded : first six digits : " + charge.getCard()!!
                    .firstSix + "  last four :" + charge.getCard()!!.last4
            )
        }
       Log.wtf("payment","Card Saved Succeeded : " + charge.status)
       Log.wtf("payment","Card Saved Succeeded : " + charge.card!!.brand)
       Log.wtf("payment","Card Saved Succeeded : " + charge.description)
       Log.wtf("payment","Card Saved Succeeded : " + charge.response.message)
       Log.wtf("payment","Card Saved Succeeded : " + (charge as SaveCard).cardIssuer.name)
       Log.wtf("payment","Card Saved Succeeded : " + charge.cardIssuer.id)
        saveCustomerRefInSession(charge)
        showDialog(charge.getId(), charge.getStatus().toString(), company.tap.gosellapi.R.drawable.ic_checkmark_normal
        )
    }

    override fun cardSavingFailed(charge: Charge) {
       Log.wtf("payment","Card Saved Failed : " + charge.status)
       Log.wtf("payment","Card Saved Failed : " + charge.description)
       Log.wtf("payment","Card Saved Failed : " + charge.response.message)
        showDialog(
            charge.id,
            charge.status.toString(),
            company.tap.gosellapi.R.drawable.icon_failed
        )
    }

    override fun cardTokenizedSuccessfully(token: Token) {
       Log.wtf("payment","Card Tokenized Succeeded : ")
       Log.wtf("payment","Token card : " + token.card.firstSix + " **** " + token.card.lastFour)
       Log.wtf("payment","Token card : " + token.card.fingerprint + " **** " + token.card.funding)
       Log.wtf("payment","Token card : " + token.card.id + " ****** " + token.card.name)
       Log.wtf("payment","Token card : " + token.card.address + " ****** " + token.card.getObject())
       Log.wtf("payment","Token card : " + token.card.expirationMonth + " ****** " + token.card.expirationYear)
        showDialog(token.id, "Token", company.tap.gosellapi.R.drawable.ic_checkmark_normal)
    }

    override fun cardTokenizedSuccessfully(token: Token, saveCardEnabled: Boolean) {
        Log.wtf("payment","Card Tokenized Succeeded2 : ")
        Log.wtf("payment","Token card2 : " + token.card.firstSix + " **** " + token.card.lastFour)
        Log.wtf("payment","Token card2 : " + token.card.fingerprint + " **** " + token.card.funding)
        Log.wtf("payment","Token card2 : " + token.card.id + " ****** " + token.card.name)
        Log.wtf("payment","Token card2 : " + token.card.address + " ****** " + token.card.getObject())
        Log.wtf("payment","Token card2 : " + token.card.expirationMonth + " ****** " + token.card.expirationYear)
        showDialog(token.id, "Token", company.tap.gosellapi.R.drawable.ic_checkmark_normal)
    }

    override fun savedCardsList(cardsList: CardsList) {
        if (cardsList.cards != null) {
           Log.wtf("payment"," Card List Response Code : " + cardsList.responseCode)
           Log.wtf("payment"," Card List Top 10 : " + cardsList.cards.size)
           Log.wtf("payment"," Card List Has More : " + cardsList.isHas_more)
           Log.wtf("payment","----------------------------------------------")
            for (sc in cardsList.cards) {
               Log.wtf("payment",sc.brandName)
            }
           Log.wtf("payment","----------------------------------------------")
            showSavedCardsDialog(cardsList)
        }
    }

    override fun sdkError(goSellError: GoSellError?) {
        baseActivity?.hideLoading()
        if (progress != null) progress!!.dismiss()
        if (goSellError != null) {
           Log.wtf("payment","SDK Process Error : " + goSellError.errorBody)
           Log.wtf("payment","SDK Process Error : " + goSellError.errorMessage)
           Log.wtf("payment","SDK Process Error : " + goSellError.errorCode)
            showDialog(goSellError.errorCode.toString() + "", goSellError.errorMessage,
                company.tap.gosellapi.R.drawable.icon_failed
            )
        }
    }

    override fun sessionIsStarting() {
       // baseActivity?.hideLoading()
       Log.wtf("payment"," Session Is Starting.....")
    }

    override fun sessionHasStarted() {
       Log.wtf("payment"," Session Has Started .......")
        baseActivity?.hideLoading()
    }

    override fun sessionCancelled() {
        baseActivity?.hideLoading()
        Log.wtf("MainActivity", "Session Cancelled.........")
    }

    override fun sessionFailedToStart() {
        baseActivity?.hideLoading()

        Log.wtf("MainActivity", "Session Failed to start.........")
    }

    override fun invalidCardDetails() {
        baseActivity?.hideLoading()
       Log.wtf("payment"," Card details are invalid....")
    }

    override fun backendUnknownError(message: String) {
        baseActivity?.hideLoading()
       Log.wtf("payment","Backend Un-Known error.... : $message")
    }

    override fun invalidTransactionMode() {
        baseActivity?.hideLoading()
       Log.wtf("payment"," invalidTransactionMode  ......")
    }

    override fun invalidCustomerID() {
        baseActivity?.hideLoading()
        if (progress != null) progress!!.dismiss()
       Log.wtf("payment","Invalid Customer ID .......")
    }

    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {
       Log.wtf("payment","userEnabledSaveCardOption :  $saveCardEnabled")
    }

    /////////////////////////////////////////////////////////  needed only for demo ////////////////////
    fun showSavedCardsDialog(cardsList: CardsList?) {
        if (progress != null) progress!!.dismiss()
        if (cardsList != null && cardsList.cards != null && cardsList.cards.size == 0) {
            Toast.makeText(activity, "There is no card saved for this customer", Toast.LENGTH_LONG)
                .show()
            return
        }
//        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view)
//        //        recyclerView.setHasFixedSize(true);
//        layoutManager = LinearLayoutManager(this)
//        recyclerView?.layoutManager = layoutManager
//        recyclerView?.itemAnimator = DefaultItemAnimator()
////        data = ArrayList<SavedCard>()
//        removedItems = ArrayList<Int>()
//        adapter = CustomAdapter(cardsList!!.cards)
//        recyclerView?.adapter = adapter
    }
    // test customer id cus_Kh1b4220191939i1KP2506448

    //    public void openSettings(View view) {
    //        startActivity(new Intent(this, SettingsActivity.class));
    //    }
    private val customer: Customer
        get() { // test customer id cus_Kh1b4220191939i1KP2506448
            val customer = if (settingsManager != null) settingsManager!!.customer else null
            val phoneNumber =
                if (customer != null) customer.phone else PhoneNumber("965", "69045932")
            return Customer.CustomerBuilder(null).
                  email("abc@abc.com").firstName("firstname").lastName("lastname").
                  metadata("").phone(PhoneNumber(phoneNumber!!.countryCode, phoneNumber.number))
                  .middleName("middlename").build()
        }

    private fun showDialog(chargeID: String, msg: String?, icon: Int) {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val popupWindow: PopupWindow
        try {
            val inflater =
                requireActivity().getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(
                company.tap.gosellapi.R.layout.charge_status_layout, requireActivity().findViewById(
                    company.tap.gosellapi.R.id.popup_element
                )
            )
            popupWindow = PopupWindow(layout, width, 250, true)
            val status_icon =
                layout.findViewById<ImageView>(company.tap.gosellapi.R.id.status_icon)
            val statusText =
                layout.findViewById<TextView>(company.tap.gosellapi.R.id.status_text)
            val chargeText =
                layout.findViewById<TextView>(company.tap.gosellapi.R.id.charge_id_txt)
            status_icon.setImageResource(icon)
            //                status_icon.setVisibility(View.INVISIBLE);
            chargeText.text = chargeID
            statusText.text = if (msg != null && msg.length > 30) msg.substring(0, 29) else msg
            popupWindow.showAtLocation(layout, Gravity.TOP, 0, 50)
            popupWindow.contentView.startAnimation(
                AnimationUtils.loadAnimation(activity, R.anim.popup_show)
            )
            setupTimer(popupWindow)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupTimer(popupWindow: PopupWindow) {
        // Hide after some seconds
        val handler = Handler()
        val runnable = Runnable {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }
        popupWindow.setOnDismissListener { handler.removeCallbacks(runnable) }
        handler.postDelayed(runnable, 4000)
    }

    private fun saveCustomerRefInSession(charge: Charge) {
       Log.wtf("payment","charge ${Gson().toJson(charge)}")
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val gson = Gson()
        val response = preferences.getString("customer", "")
        val customersList = gson.fromJson<ArrayList<CustomerViewModel>>(
            response, object : TypeToken<List<CustomerViewModel?>?>() {}.type)
        if (customersList != null) {
            customersList.clear()
            customersList.add(
                CustomerViewModel(
                    charge.customer.identifier,
                    charge.customer.firstName!!,
                    charge.customer.middleName!!,
                    charge.customer.lastName!!,
                    charge.customer.email!!,
                    charge.customer.phone!!.countryCode,
                    charge.customer.phone!!.number
                )
            )
            val data = gson.toJson(customersList)
            writeCustomersToPreferences(data, preferences)
        }
    }

    private fun writeCustomersToPreferences(data: String, preferences: SharedPreferences) {
        val editor = preferences.edit()
        editor.putString("customer", data)
        editor.apply()
    }

    private fun getCustomerSavedCardsList(view: View?) {
        progress = ProgressDialog(activity)
        progress!!.setTitle("Loading")
        progress!!.setMessage("Wait while loading...")
        progress!!.show()
        listSavedCards()
    }

    fun cancelSession() {
        sdkSession!!.cancelSession(activity)
    }


}