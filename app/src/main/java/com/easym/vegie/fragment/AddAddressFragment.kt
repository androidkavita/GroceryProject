package com.easym.vegie.fragment

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.easym.vegie.R
import com.easym.vegie.Utils.Utility
import com.easym.vegie.databinding.AddAddressFragmentLayoutBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.skbfinance.BaseFragment
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.ConnectException
import java.util.concurrent.TimeoutException


class AddAddressFragment : BaseFragment() {

    lateinit var binding : AddAddressFragmentLayoutBinding

    var lat = ""
    var lng = ""
    var addressLat = ""
    var addressLong = ""
    var placeAddress = ""
    var mshortName = ""
    var locality = ""
    var source_city = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.add_address_fragment_layout, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if(bundle != null) {

            placeAddress = bundle.getString("address")!!
            addressLat = bundle.getString("dropLat")!!
            addressLong = bundle.getString("dropLong")!!
            locality = bundle.getString("locality")!!
            source_city = bundle.getString("city")!!
            mshortName = bundle.getString("shortName")!!


           // binding.etAddress.setText(placeAddress)
            binding.etLocalityTown.setText(locality)
            binding.etCityDistrict.setText(source_city)
            lat = addressLat
            lng = addressLong

        }


        binding.buttonSave.setOnClickListener {
            validate(view)
        }


    }

    private fun validate(view: View){

        if(TextUtils.isEmpty(binding.etAddress.text.toString().trim())){
            Snackbar.make(view, "Please enter address", Snackbar.LENGTH_SHORT).show()
            return
        }else if(TextUtils.isEmpty(binding.etLocalityTown.text.toString().trim())){
            Snackbar.make(view, "Please enter locality", Snackbar.LENGTH_SHORT).show()
            return
        }else if(TextUtils.isEmpty(binding.etPincode.text.toString().trim())){
            Snackbar.make(view, "Please enter pincode", Snackbar.LENGTH_SHORT).show()
            return
        }else if(TextUtils.isEmpty(binding.etCityDistrict.text.toString().trim())){
            Snackbar.make(view, "Please enter city / district", Snackbar.LENGTH_SHORT).show()
            return
        }else if(TextUtils.isEmpty(binding.etState.text.toString().trim())){
            Snackbar.make(view, "Please enter state", Snackbar.LENGTH_SHORT).show()
            return
        }else{

            saveAddress()

        }

    }

    private fun saveAddress(){

        binding.buttonSave.isEnabled = false

        val address = binding.etAddress.text.toString().trim()
        val locality = binding.etLocalityTown.text.toString().trim()
        val pincode = binding.etPincode.text.toString().trim()
        val state = binding.etState.text.toString().trim()
        val city = binding.etCityDistrict.text.toString().trim()

        /*val latLng = getLocationFromAddress(address+" "+locality+" "+city+" "+address)
        if(latLng != null){
            lat = latLng.latitude.toString()
            lng = latLng.longitude.toString()
        }
*/

        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.addAddress(
                    userPref.user.id!!,
                    address,
                    locality,
                    pincode,
                    state,
                    city,
                    userPref.user.token!!,
                    lat,
                    lng)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgressDialog() }
                    .doOnCompleted { hideProgressDialog() }
                    .subscribe({

                        if (it.responseCode.equals("200")) {

                            binding.etAddress.setText("")
                            binding.etLocalityTown.setText("")
                            binding.etPincode.setText("")
                            binding.etState.setText("")
                            binding.etCityDistrict.setText("")

                            Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()
                            //getQuotationProductList()

                            binding.buttonSave.isEnabled = true

                        } else if (it.responseCode == "403") {
                            utils.openLogoutDialog(requireContext(), userPref)
                        } else {

                            hideProgressDialog()
                            Utility.simpleAlert(context, getString(R.string.info_dialog_title), it.responseMessage)

                            binding.buttonSave.isEnabled = true

                        }

                    }, {

                        try {

                            if (it is ConnectException) {
                                hideProgressDialog()
                                Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection))

                                binding.buttonSave.isEnabled = true


                            } else if (it is TimeoutException) {
                                hideProgressDialog()
                                Utility.simpleAlert(context, getString(R.string.error), getString(R.string.timeout_occur))

                                binding.buttonSave.isEnabled = true


                            } else {
                                hideProgressDialog()
                                it.printStackTrace()
                                Utility.simpleAlert(context, "", getString(R.string.something_went_wrong))

                                binding.buttonSave.isEnabled = true

                            }

                        } catch (ex: Exception) {
                            Log.e("", "Within Throwable Exception::" + it.message)
                        }

                    })

        } else {
            hideProgressDialog()
            Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection))

            binding.buttonSave.isEnabled = true


        }

    }

    fun getLocationFromAddress(strAddress: String?) : LatLng? {

        //Create coder with Activity context - this
        val coder = Geocoder(requireContext())
        val address: List<Address>?
        try {
            //Get latLng from String
            address = coder.getFromLocationName(strAddress!!, 5)
            //check for null
            if (address == null) {
                return null
            }
            //Lets take first possibility from the all possibilities.
            val location: Address = address[0]
            val latLng = LatLng(location.getLatitude(), location.getLongitude())

            Log.e("Lat", "" + location.latitude)
            Log.e("Lng", "" + location.longitude)

            return latLng

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

}