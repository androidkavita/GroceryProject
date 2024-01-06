package com.easym.vegie.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.easym.vegie.R
import com.easym.vegie.fragment.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.custom_actionbar_layout.*


class FragmentContainerActivity : BaseActivity(), View.OnClickListener {

    var isForResult : String = ""
    var userPreferLanguageCode = ""

    val AUTOCOMPLETE_DELIVERY_REQUEST_CODE = 4

    var addressLat = ""
    var addressLong = ""
    var placeAddress = ""
    var mshortName = ""
    var locality = ""
    var source_city = ""

    var cartCount = 0
    var totalAmount = 0
    var minimumOrderLimit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)



        iv_Back.setOnClickListener(this)
        iv_Search.visibility = View.GONE
        iv_Cart.visibility = View.GONE

        val bundle = intent.extras

        if(bundle != null){

            val fragmentName = bundle.get("FragmentName")

            when(fragmentName){

                "Search Fragment" -> {

                    tv_Title.setText(getString(R.string.search_product))
                    tv_Title.visibility = View.VISIBLE

                    setFragment(SearchFragment())

                }

                "Offers Fragment" -> {
                    tv_Title.setText(getString(R.string.offers))
                    tv_Title.visibility = View.VISIBLE

                    setFragment(OffersFragment())

                }

                "Refund Policy Fragment" -> {
                    tv_Title.setText(getString(R.string.refund))

                    tv_Title.visibility = View.VISIBLE

                    setFragment(RefundPolicyFragment())

                }

                "My Cart List Fragment" -> {
                    tv_Title.visibility = View.GONE
                    var source = ""
                    var menu_Id = ""
                    var qty = ""
                    var brand_Id = ""
                    var quotationId = ""

                    if(bundle.containsKey("source")){
                        source = bundle.get("source").toString()
                    }
                    if(bundle.containsKey("menu_Id")){
                        menu_Id = bundle.getString("menu_Id").toString()
                    }
                    if(bundle.containsKey("qty")){
                        qty = bundle.getString("qty").toString()
                    }
                    if(bundle.containsKey("brand_Id")){
                        brand_Id = bundle.getString("brand_Id").toString()
                    }
                    if(bundle.containsKey("quotationId")){
                        quotationId = bundle.getString("quotationId").toString()
                    }

                    Log.e("source",""+source)
                    Log.e("menu_Id",""+menu_Id)
                    Log.e("qty",""+qty)
                    Log.e("brand_Id",""+brand_Id)
                    Log.e("quotationId", ""+quotationId )

                    if(source != "" && menu_Id != "" && qty != "") {
                        val myCartListFragment = MyCartListFragment()
                        val bundle = Bundle()
                        bundle.putString("source",source)
                        bundle.putString("menu_Id",menu_Id)
                        bundle.putString("qty",qty)
                        bundle.putString("brand_Id",brand_Id)
                        bundle.putString("quotationId",quotationId)

                        myCartListFragment.arguments = bundle

                        setFragment(myCartListFragment)


                    }else{
                        setFragment(MyCartListFragment())

                    }

                }

                "Change Language Fragment" -> {

                    if(bundle.containsKey("IsForResult")) {
                        isForResult = bundle.getString("IsForResult", null)

                    }else{
                        isForResult = ""
                    }


                    tv_Title.setText(getString(R.string.changeLanguage))
                    tv_Title.visibility = View.VISIBLE
                    setFragment(ChangeLanguageFragment())

                }

                "Add Address Fragment" -> {

                    val fields = listOf(
                            Place.Field.ID,
                            Place.Field.NAME,
                            Place.Field.LAT_LNG,
                            Place.Field.ADDRESS,
                            Place.Field.ADDRESS_COMPONENTS
                    )

                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
                    startActivityForResult(intent, AUTOCOMPLETE_DELIVERY_REQUEST_CODE)

                }

                "Saved Quotations Fragment" -> {
                    setFragment(SavedQuotationsFragment())
                }
                "Saved Quotations Details Fragment" -> {

                    iv_Cart.visibility = View.VISIBLE
                    iv_Cart.setOnClickListener {

                      /*  if (cartCount == 0) {

                            val intent = Intent(
                                this,
                                FragmentContainerActivity::class.java
                            )
                            intent.putExtra("FragmentName", "My Cart List Fragment")
                            startActivity(intent)

                        }
                        else {

                            if (totalAmount >= minimumOrderLimit) {

                                val intent = Intent(
                                    this,
                                    FragmentContainerActivity::class.java
                                )
                                intent.putExtra("FragmentName", "My Cart List Fragment")
                                startActivity(intent)

                            } else {

                                utils.simpleAlert(
                                    this,
                                    getString(R.string.minimum_order_amount),
                                    getString(R.string.minimum_order_amount_limit_is) + " " + minimumOrderLimit
                                )
                            }
                        }*/

                        intent.putExtra("FragmentName", "My Cart List Fragment")
                        startActivity(intent)

                    }

                    val mBundle = Bundle()
                    mBundle.putString("QuotationId",bundle.getString("QuotationId"))
                    mBundle.putString("QuotationSavedDate",bundle.getString("QuotationSavedDate"))

                    val fragment = SavedQuotationsDetailsFragment()
                    fragment.arguments = mBundle

                    setFragment(fragment)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_DELIVERY_REQUEST_CODE && data != null) {

            try {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data.let {
                            val place = Autocomplete.getPlaceFromIntent(data)
                            //binding.etDeliveryLocation.setText(place.address)
                            addressLat = place.latLng!!.latitude.toString()
                            addressLong = place.latLng!!.longitude.toString()


                            Log.i(
                                    "TAG", "Place: ${place.name}, ${place.id}," +
                                    " ${place.latLng!!.latitude}, " +
                                    "${place.latLng!!.longitude}, ${place.address}"
                            )

                            for (i in 0 until place.addressComponents!!.asList().size) {
                                if(place.name != null) {
                                    placeAddress = place.name!!
                                }

                                val jsonObject = place.addressComponents!!.asList()[i]
                                val type = jsonObject.types[0]
                                val name = jsonObject.name
                                val shortName = jsonObject.shortName

                                when (type) {
                                    "locality" -> {
                                        Log.e("locality", name)
                                        locality = name
                                        //Log.e("jsonObject",jsonObject.toString())
                                    }
                                    "administrative_area_level_2" -> {
                                        Log.e("city", name)
                                        source_city = name
                                    }
                                    "administrative_area_level_1" -> {
                                        Log.e("state", name)
                                        Log.e("shortName", shortName!!)
                                        mshortName = shortName
                                    }
                                    "country" -> {
                                        Log.e("country", name)
                                    }
                                    "postal_code" -> {
                                        Log.e("postal_code", name)
                                        //postalCode = name
                                    }
                                }
                            }



                            val addAddressFragment = AddAddressFragment()
                            val bundle = Bundle()
                            bundle.putString("address",placeAddress)
                            bundle.putString("dropLat",addressLat)
                            bundle.putString("dropLong",addressLong)
                            bundle.putString("locality",locality)
                            bundle.putString("city",source_city)
                            bundle.putString("shortName",mshortName)

                            addAddressFragment.arguments = bundle
                            tv_Title.text = getString(R.string.changeAddress)
                            tv_Title.visibility = View.VISIBLE
                            setFragment(addAddressFragment)

                        }
                    }

                    AutocompleteActivity.RESULT_ERROR -> {

                        data.let {
                            val status = Autocomplete.getStatusFromIntent(data)
                            Log.i("TAG", status.statusMessage!!)
                        }
                    }

                    Activity.RESULT_CANCELED -> {
                    }
                }

            } catch (e: Exception) {

            }
        }
    }

    fun setFragment(fragment: Fragment?) {
        val fragmentManager = supportFragmentManager
        val ftTransaction = fragmentManager.beginTransaction()
        ftTransaction.add(R.id.container, fragment!!)
        ftTransaction.commit()
    }

    override fun onClick(p0: View?) {

        val id = p0!!.id

        when(id) {

            R.id.iv_Back -> {
                if(isForResult != null && isForResult.equals("True")){
                    val returnIntent = Intent()
                    returnIntent.putExtra("result", userPreferLanguageCode)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()

                }else{
                    finish()
                }
            }
        }

    }

    override fun onBackPressed() {

        if(isForResult != null && isForResult.equals("True")){
            val returnIntent = Intent()
            returnIntent.putExtra("result", userPreferLanguageCode)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()

        }else{
            finish()
        }

    }
}