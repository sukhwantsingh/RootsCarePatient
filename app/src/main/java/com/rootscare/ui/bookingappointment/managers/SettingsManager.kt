package com.rootscare.ui.bookingappointment.managers

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import company.tap.gosellapi.R
import company.tap.gosellapi.internal.api.enums.AmountModificatorType
import company.tap.gosellapi.internal.api.enums.AuthorizeActionType
import company.tap.gosellapi.internal.api.models.AmountModificator
import company.tap.gosellapi.internal.api.models.PhoneNumber
import company.tap.gosellapi.open.enums.AppearanceMode
import company.tap.gosellapi.open.enums.OperationMode
import company.tap.gosellapi.open.enums.TransactionMode
import company.tap.gosellapi.open.models.*
import company.tap.gosellapi.open.models.Customer.CustomerBuilder
import company.tap.gosellapi.open.viewmodel.CustomerViewModel
import java.math.BigDecimal

class SettingsManager {
    private var pref: SharedPreferences? = null
    private var context: Context? = null
    fun setPref(ctx: Context?) {
        context = ctx
        if (pref == null) pref = PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun saveCustomer(
        name: String?,
        middle: String?,
        last: String?,
        email: String?,
        sdn: String?,
        mobile: String?,
        ctx: Context?
    ) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val gson = Gson()
        val response = preferences.getString("customer", "")
        var customersList = gson.fromJson<ArrayList<CustomerViewModel?>>(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.type
        )
        if (customersList == null) customersList = ArrayList()
        customersList.add(
            CustomerViewModel(
                null,
                name!!,
                middle!!,
                last!!,
                email!!,
                sdn!!,
                mobile!!
            )
        )
        val data = gson.toJson(customersList)
        writeCustomersToPreferences(data, preferences)
    }

    fun editCustomer(
        oldCustomer: CustomerViewModel?,
        newCustomer: CustomerViewModel,
        ctx: Context?
    ) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val gson = Gson()
        val response = preferences.getString("customer", "")
        val customersList = gson.fromJson<ArrayList<CustomerViewModel>>(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.type
        )
        if (customersList != null) {
            val customerRef = customersList[0].ref
            customersList.clear()
            customersList.add(
                CustomerViewModel(
                    customerRef,
                    newCustomer.name,
                    newCustomer.middleName,
                    newCustomer.lastName,
                    newCustomer.email,
                    newCustomer.sdn,
                    newCustomer.mobile
                )
            )
            val data = gson.toJson(customersList)
            writeCustomersToPreferences(data, preferences)
        } else {
            saveCustomer(
                newCustomer.name,
                newCustomer.middleName,
                newCustomer.lastName,
                newCustomer.email,
                newCustomer.sdn,
                newCustomer.mobile, ctx
            )
        }
    }

    private fun writeCustomersToPreferences(data: String, preferences: SharedPreferences) {
        val editor = preferences.edit()
        editor.putString("customer", data)
        editor.apply()
    }

    fun getRegisteredCustomers(ctx: Context?): List<CustomerViewModel> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val gson = Gson()
        val response = preferences.getString("customer", "")
        val customersList = gson.fromJson<ArrayList<CustomerViewModel>>(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.type
        )
        return customersList ?: ArrayList()
    }// check if customer id is in pref.

    //////////////////////////////////////   Get Payment Settings ////////////////////////////////
    val customer: Customer
        get() {
            val customer: Customer
            val gson = Gson()
            val response = pref!!.getString("customer", "")
            println(" get customer: $response")
            val customersList = gson.fromJson<ArrayList<CustomerViewModel>>(
                response,
                object : TypeToken<List<CustomerViewModel?>?>() {}.type
            )

            // check if customer id is in pref.
            customer = if (customersList != null) {
                println("preparing data source with customer ref :" + customersList[0].ref)
                CustomerBuilder(customersList[0].ref).firstName(customersList[0].name).middleName(
                    customersList[0].middleName
                ).lastName(customersList[0].lastName).email(customersList[0].email).phone(
                    PhoneNumber(
                        customersList[0].sdn,
                        customersList[0].mobile
                    )
                ).metadata("meta").build()
            } else {
                println(" paymentResultDataManager.getCustomerRef(context) null")
                CustomerBuilder(null).firstName("Name").middleName("MiddleName").lastName("Surname")
                    .email("hello@tap.company").phone(PhoneNumber("965", "69045932"))
                    .metadata("meta").build()
            }
            return customer
        }

    //    val paymentItems: ArrayList<PaymentItem>
//        get() {
//            val items = ArrayList<PaymentItem>()
//            items.add(
//                PaymentItemBuilder("", Quantity(Mass.KILOGRAMS, BigDecimal.ONE), BigDecimal.ONE)
//                    .description("Description for test item #1")
//                    .discount(AmountModificator(AmountModificatorType.FIXED, BigDecimal.ZERO))
//                    .taxes(null)
//                    .build()
//            )
//            return items
//        }
    val taxes: ArrayList<Tax>
        get() {
            val taxes = ArrayList<Tax>()
            taxes.add(
                Tax(
                    "Test tax #1",
                    "Test tax #1 description",
                    AmountModificator(AmountModificatorType.FIXED, BigDecimal.TEN)
                )
            )
            return taxes
        }
    val shippingList: ArrayList<Shipping>
        get() {
            val shipping = ArrayList<Shipping>()
            shipping.add(
                Shipping(
                    "Test shipping #1",
                    "Test shipping description #1",
                    BigDecimal.ONE
                )
            )
            return shipping
        }

    //        Base URL
    val postURL: String
        get() =//        Base URL
            "https://tap.company"
    val paymentDescription: String
        get() = "Test payment description."
    val paymentMetaData: HashMap<String, String>
        get() {
            val paymentMetadata = HashMap<String, String>()
            paymentMetadata["metadata_key_1"] = "metadata value 1"
            return paymentMetadata
        }
    val paymentReference: Reference
        get() = Reference(
            "acquirer_1",
            "gateway_1",
            "payment_1",
            "track_1",
            "transaction_1",
            "order_1"
        )
    val paymentStatementDescriptor: String
        get() = "Test payment statement descriptor."
    val receipt: Receipt
        get() = Receipt(true, true)
    val authorizeAction: AuthorizeAction
        get() = AuthorizeAction(
            AuthorizeActionType.VOID,
            10
        )// total amount, transferred to the destination account

    // transfer currency code
    //number of destinations trabsfer involved
    //List of destinations object
    /// destination unique identifier
    // Amount to be transferred to the destination account
    //currency code (three digit ISO format)
    //Description about the transfer
    //Merchant reference number to the destination
    val destination: Destinations
        get() {
            val destinations = ArrayList<Destination>()
            destinations.add(
                Destination(
                    "1014",  /// destination unique identifier
                    BigDecimal(10),  // Amount to be transferred to the destination account
                    TapCurrency("kwd"),  //currency code (three digit ISO format)
                    "please deduct 10 kd for this account",  //Description about the transfer
                    "" //Merchant reference number to the destination
                )
            )
            return Destinations(
                BigDecimal(10),  // total amount, transferred to the destination account
                TapCurrency("kwd"),  // transfer currency code
                1,  //number of destinations trabsfer involved
                destinations
            ) //List of destinations object
        }
    ////////////////////////////////////////////////// Specific Settings ////////////////////////////
    /**
     * Session Data Source
     */
    fun getSDKOperationMode(key: String?): OperationMode {
        val op_mode = pref!!.getString(key, OperationMode.SAND_BOX.name)
        return if (op_mode == OperationMode.SAND_BOX.name) OperationMode.SAND_BOX else OperationMode.PRODUCTION
    }

    /**
     * get Transaction mode
     *
     * @param key
     * @return
     */
    fun getTransactionsMode(key: String?): TransactionMode {
        val trx_mode = pref!!.getString(key, TransactionMode.PURCHASE.name)
        if (trx_mode.equals(
                TransactionMode.PURCHASE.name,
                ignoreCase = true
            )
        ) return TransactionMode.PURCHASE
        if (trx_mode.equals(
                TransactionMode.AUTHORIZE_CAPTURE.name,
                ignoreCase = true
            )
        ) return TransactionMode.AUTHORIZE_CAPTURE
        return if (trx_mode.equals(
                TransactionMode.TOKENIZE_CARD.name,
                ignoreCase = true
            )
        ) TransactionMode.TOKENIZE_CARD else TransactionMode.SAVE_CARD
    }

    /**
     * get transaction currency
     *
     * @param key
     * @return
     */
    fun getTransactionCurrency(key: String?): TapCurrency {
        val trx_curr = pref!!.getString(key, "kwd")
        Log.d("Settings Manager", "trx_curr :" + trx_curr!!.trim { it <= ' ' })
        return if (trx_curr != null && !"".equals(
                trx_curr.trim { it <= ' ' },
                ignoreCase = true
            )
        ) TapCurrency(trx_curr) else TapCurrency("kwd")
    }

    /**
     * Get Appearance Mode [FULLSCREEN_MODE - WINDOWED_MODE]
     *
     * @param key
     * @return
     */
    fun getAppearanceMode(key: String?): AppearanceMode {
        val mode = pref!!.getString(key, AppearanceMode.FULLSCREEN_MODE.name)
        return if (mode == AppearanceMode.WINDOWED_MODE.name) AppearanceMode.WINDOWED_MODE else AppearanceMode.FULLSCREEN_MODE
    }
    //////////////////////////////////////////////////  General ////////////////////////////////
    /**
     * Get Font name saved in session or return default
     *
     * @param key
     * @param defaultFont
     * @return
     */
    fun getFont(key: String?, defaultFont: String?): String? {
        println("pref: " + pref!!.getString(key, defaultFont))
        return pref!!.getString(key, defaultFont)
    }

    /**
     * Get Color saved in session or return default
     *
     * @param key
     * @param defaultColor
     * @return
     */
    fun getColor(key: String?, defaultColor: Int): Int {
        val color = pref!!.getString(key, "")
        return extractColorCode(color, defaultColor)
    }

    /**
     * @param key
     * @return
     */
    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return pref!!.getBoolean(key, defaultValue)
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    fun getString(key: String?, defaultValue: String?): String? {
        return pref!!.getString(key, defaultValue)
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        return pref!!.getInt(key, defaultValue)
    }

    //////////////////////////////////////////////////  PayButton /////////////////////////////////
    fun getTapButtonEnabledBackgroundColor(key: String?): Int {
        val color = pref!!.getString(key, "")
        return extractEnabledBackgroundColorCode(color, R.color.vibrant_green_pressed)
    }

    fun getTapButtonDisabledBackgroundColor(key: String?): Int {
        val color = pref!!.getString(key, "")
        return extractDisabledBackgroundColorCode(color)
    }

    private fun extractEnabledBackgroundColorCode(color: String?, defaultColor: Int): Int {
        return if (color!!.trim { it <= ' ' }
                .equals("", ignoreCase = true)) defaultColor else Color.parseColor(
            color.split("_").toTypedArray()[1]
        )
    }

    private fun extractDisabledBackgroundColorCode(color: String?): Int {
        return if (color!!.trim { it <= ' ' }
                .equals("", ignoreCase = true)) R.color.silver_light else Color.parseColor(
            color.split("_").toTypedArray()[1]
        )
    }

    fun getTapButtonFont(key: String?): String? {
        return pref!!.getString(key, "")
    }

    fun getTapButtonDisabledTitleColor(key: String?, defaultColor: Int): Int {
        val color = pref!!.getString(key, "")
        return extractColorCode(color, defaultColor)
    }

    ////////////////////////////////////////////  UTILS  //////////////////////////////////////////////
    fun getTapButtonEnabledTitleColor(key: String?, defaultColor: Int): Int {
        val color = pref!!.getString(key, "")
        return extractColorCode(color, defaultColor)
    }

    private fun extractColorCode(color: String?, defaultColor: Int): Int {
        return if (color!!.trim { it <= ' ' }
                .equals("", ignoreCase = true)) defaultColor else Color.parseColor(
            color.split("_").toTypedArray()[1]
        )
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private object SingleInstanceAdmin {
        var instance = SettingsManager()
    }

    companion object {
        val instance: SettingsManager
            get() = SingleInstanceAdmin.instance
    }
}