package com.easym.vegie.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.easym.vegie.R
import com.easym.vegie.adapter.ViewPagerAdapter
import com.easym.vegie.databinding.MainCategoryShoppingFragmentBinding
import com.easym.vegie.model.category.CaegoryName
import com.google.gson.Gson
import com.skbfinance.BaseFragment

class MainCategoryShoppingFragment : BaseFragment() {

    lateinit var binding : MainCategoryShoppingFragmentBinding

    lateinit var adapter : ViewPagerAdapter
    lateinit var categoryName: CaegoryName
    var quotationId= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_category_shopping_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("@@sub","subcategory")
        val bundle = arguments
        if(bundle !=null){
            val categoryResponse = bundle.getString("CategoryResponse")
            categoryName = Gson().fromJson(categoryResponse,CaegoryName::class.java)
            quotationId = bundle.getString("quotationId").toString()
            setupViewPager(binding.viewPager)
            binding.tabLayout.setupWithViewPager(binding.viewPager)
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {

        adapter = ViewPagerAdapter(childFragmentManager)
        //mainCategoryShoppingFragment = SubCategoryShoppingFragment()

        if(categoryName != null) {

            if(categoryName.subCategoory != null ) {

                val subCategories = categoryName.subCategoory
                for (item in subCategories) {

                    val subCategoryShoppingFragment = SubCategoryShoppingFragment()
                    val bundle = Bundle()
                    val categoryResponse = Gson().toJson(item)
                    bundle.putString("CategoryResponse", categoryResponse)
                    bundle.putString("Category_id", item.id)
                    bundle.putString("quotationId", quotationId)
                    subCategoryShoppingFragment.arguments = bundle
                    if (!item.other_name.equals("false")) {
                        adapter.addFragment(subCategoryShoppingFragment, item.name +
                                "\n" + item.other_name)
                    } else {
                        adapter.addFragment(subCategoryShoppingFragment, item.name)
                    }
                }
                viewPager.adapter = adapter
            }
        }
    }
}