package com.rootscare.ui.bookingappointment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rootscare.R
import com.rootscare.ui.bookingappointment.managers.SettingsManager
import company.tap.gosellapi.internal.api.callbacks.GoSellError
import company.tap.gosellapi.internal.api.models.*
import company.tap.gosellapi.open.buttons.PayButtonView
import company.tap.gosellapi.open.controllers.SDKSession
import company.tap.gosellapi.open.delegate.SessionDelegate
import company.tap.gosellapi.open.models.CardsList
import company.tap.gosellapi.open.models.Customer
import company.tap.gosellapi.open.models.Customer.CustomerBuilder
import company.tap.gosellapi.open.viewmodel.CustomerViewModel
import java.util.*

class PaymentActivity : AppCompatActivity(), SessionDelegate {
    private val SDK_REQUEST_CODE = 1001
    private var sdkSession: SDKSession? = null
    private var payButtonView: PayButtonView? = null
    private var settingsManager: SettingsManager? = null
    private var progress: ProgressDialog? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_activity)
        settingsManager = SettingsManager.instance
        settingsManager?.setPref(this)

    }

    companion object {
        private var adapter: RecyclerView.Adapter<*>? = null
        private var recyclerView: RecyclerView? = null
        private var data: ArrayList<SavedCard>? = null
        var myOnClickListener: View.OnClickListener? = null
        private var removedItems: ArrayList<Int>? = null

        val TAG = PaymentActivity::class.java.simpleName

        fun newIntent(activity: Activity): Intent {
            return Intent(activity, PaymentActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        if (settingsManager == null) {
            settingsManager = SettingsManager.instance
            settingsManager!!.setPref(this)
        }
    }

    //    //////////////////////////////////////////////////////  List Saved Cards  ////////////////////////
    /**
     * retrieve list of saved cards from the backend.
     */
    private fun listSavedCards() {
        if (sdkSession != null) sdkSession!!.listAllCards("cus_s4H13120191115x0R12606480", this)
    }

    /////////////////////////////////////////////////////////  needed only for demo ////////////////////
    fun showSavedCardsDialog(cardsList: CardsList?) {
        if (progress != null) progress!!.dismiss()
        if (cardsList != null && cardsList.cards != null && cardsList.cards.size == 0) {
            Toast.makeText(this, "There is no card saved for this customer", Toast.LENGTH_LONG)
                .show()
            return
        }
        recyclerView = findViewById(R.id.my_recycler_view)
        //        recyclerView.setHasFixedSize(true);
        layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        data = ArrayList()
        removedItems = ArrayList()

        adapter = CustomAdapter(cardsList?.cards!!)
        recyclerView?.adapter = adapter
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
            return CustomerBuilder(null).email("abc@abc.com").firstName("firstname")
                .lastName("lastname").metadata("")
                .phone(PhoneNumber(phoneNumber!!.countryCode, phoneNumber.number))
                .middleName("middlename").build()
        }


    private fun saveCustomerRefInSession(charge: Charge) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val gson = Gson()
        val response = preferences.getString("customer", "")
        val customersList = gson.fromJson<ArrayList<CustomerViewModel>>(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.type
        )
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

    fun getCustomerSavedCardsList(view: View?) {
        progress = ProgressDialog(this)
        progress!!.setTitle("Loading")
        progress!!.setMessage("Wait while loading...")
        progress!!.show()
        listSavedCards()
    }

    fun cancelSession(view: View?) {
        sdkSession!!.cancelSession(this)
    }

    class CustomAdapter(private val dataSet: ArrayList<SavedCard>) :
        RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textViewName: TextView = itemView.findViewById(R.id.textViewName)
            var textViewVersion: TextView = itemView.findViewById(R.id.textViewVersion)
            var textViewExp: TextView = itemView.findViewById(R.id.textViewexp)
            var imageViewIcon: ImageView = itemView.findViewById(R.id.imageView)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.cards_layout, parent, false)
//            view.setOnClickListener(myOnClickListener)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, listPosition: Int) {
            val textViewName = holder.textViewName
            val textViewVersion = holder.textViewVersion
            val textViewExp = holder.textViewExp
            val imageView = holder.imageViewIcon
            textViewName.text =
                dataSet[listPosition].firstSix + " ***** " + dataSet[listPosition].lastFour
            textViewVersion.text =
                dataSet[listPosition].exp_month + " / " + dataSet[listPosition].exp_year
            imageView.setImageResource(R.drawable.cards1)
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }
    }

    override fun paymentSucceed(charge: Charge) {
        TODO("Not yet implemented")
    }

    override fun paymentFailed(charge: Charge?) {
        TODO("Not yet implemented")
    }

    override fun authorizationSucceed(authorize: Authorize) {
        TODO("Not yet implemented")
    }

    override fun authorizationFailed(authorize: Authorize?) {
        TODO("Not yet implemented")
    }

    override fun cardSaved(charge: Charge) {
        TODO("Not yet implemented")
    }

    override fun cardSavingFailed(charge: Charge) {
        TODO("Not yet implemented")
    }

    override fun cardTokenizedSuccessfully(token: Token) {
        TODO("Not yet implemented")
    }

    override fun cardTokenizedSuccessfully(token: Token, saveCardEnabled: Boolean) {
        TODO("Not yet implemented")
    }
    override fun savedCardsList(cardsList: CardsList) {
        TODO("Not yet implemented")
    }

    override fun sdkError(goSellError: GoSellError?) {
        TODO("Not yet implemented")
    }

    override fun sessionIsStarting() {
        TODO("Not yet implemented")
    }

    override fun sessionHasStarted() {
        TODO("Not yet implemented")
    }

    override fun sessionCancelled() {
        TODO("Not yet implemented")
    }

    override fun sessionFailedToStart() {
        TODO("Not yet implemented")
    }

    override fun invalidCardDetails() {
        TODO("Not yet implemented")
    }

    override fun backendUnknownError(message: String?) {
        TODO("Not yet implemented")
    }

    override fun invalidTransactionMode() {
        TODO("Not yet implemented")
    }

    override fun invalidCustomerID() {
        TODO("Not yet implemented")
    }

    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {
        TODO("Not yet implemented")
    }
}