package com.easym.vegie.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.easym.vegie.R
import com.easym.vegie.databinding.SelectAddressFragmentLayoutBinding
import com.skbfinance.BaseFragment

class SelectAddressFragment : BaseFragment() {

    lateinit var binding : SelectAddressFragmentLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.select_address_fragment_layout,
                container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getAddressList(){

    }
}