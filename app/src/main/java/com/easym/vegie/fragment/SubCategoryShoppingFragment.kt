package com.easym.vegie.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.easym.vegie.R
import com.easym.vegie.Utils.Utility
import com.easym.vegie.activities.ShopByCategoryActivity
import com.easym.vegie.adapter.ProductListItemRVAdapter
import com.easym.vegie.databinding.ShoppingFragmentLayoutBinding
import com.easym.vegie.model.category.SubCategoory
import com.google.gson.Gson
import com.skbfinance.BaseFragment
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.ConnectException

class SubCategoryShoppingFragment : BaseFragment() {

    lateinit var binding: ShoppingFragmentLayoutBinding
    lateinit var productListItem: ProductListItemRVAdapter

    lateinit var subCategoryName: SubCategoory

    var qtyOfProduct = 1
    var quotationId = ""
    var Category_id = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.shopping_fragment_layout, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            Log.e("@@quotation", "quotationId")
            val categoryResponse = bundle.getString("CategoryResponse")

            quotationId = bundle.getString("quotationId").toString()
            Category_id = bundle.getString("Category_id").toString()
            Log.e("Debug Response", "" + categoryResponse)
            subCategoryName = Gson().fromJson(categoryResponse, SubCategoory::class.java)


            getProduct(Category_id)

        }


    }

    private fun getProduct(categoryId: String) {

        if (Utility.getInstance().checkInternetConnection(context)) {

            Log.d("UserId", userPref.user.id + " bbb "+categoryId)

            apiService.getProduct(
                userPref.user.id!!,
                userPref.user.token!!,
                categoryId,
                userPref.getUserPreferLanguageCode()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({ this.showProgressDialog() })
                .doOnCompleted({ this.hideProgressDialog() })
                .subscribe({
                    if (it.responseCode.equals("200")) {

                        val menuList = it.data.menuList

                        val linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        productListItem = ProductListItemRVAdapter(
                            requireContext(), menuList,
                            object : ProductListItemRVAdapter.WishList {

                                override fun addToWishList(
                                    iv_RemoveFromWishlist: ImageView,
                                    iv_AddToWishlist: ImageView,
                                    menuId: String
                                ) {

                                    addWishListApi(iv_RemoveFromWishlist, iv_AddToWishlist, menuId)

                                }

                                override fun removeFromWishList(
                                    iv_AddToWishlist: ImageView,
                                    iv_RemoveFromWishlist: ImageView,
                                    menuId: String
                                ) {

                                    removeWishListApi(
                                        iv_AddToWishlist,
                                        iv_RemoveFromWishlist,
                                        menuId
                                    )
                                }

                                override fun refreshCartCountList() {

                                    Log.e("CartCount", "Refresh")
                                    getCartCount()

                                }

                            }, apiService, userPref, utils, quotationId
                        )
                        binding.rvItemList.adapter = productListItem
                        binding.rvItemList.layoutManager = linearLayoutManager

                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(requireContext(), userPref)
                    } else {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            context,
                            getString(R.string.info_dialog_title),
                            it.responseMessage
                        )
                    }

                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(
                                context,
                                "",
                                getString(R.string.something_went_wrong)
                            )
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })
        } else {
            Utility.simpleAlert(
                context,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }
    }


    private fun addWishListApi(
        iv_RemoveFromWishlist: ImageView,
        iv_AddToWishlist: ImageView, menuId: String
    ) {

        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.addWishList(
                userPref.user.id!!,
                menuId,
                userPref.user.token!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({

                    if (it.responseCode.equals("200")) {
                        iv_AddToWishlist.visibility = View.GONE
                        iv_RemoveFromWishlist.visibility = View.VISIBLE
                        Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()


                        iv_RemoveFromWishlist.setOnClickListener({
                            removeWishListApi(iv_AddToWishlist, iv_RemoveFromWishlist, menuId)
                        })

                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(requireContext(), userPref)
                    } else {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            context,
                            getString(R.string.info_dialog_title),
                            it.responseMessage
                        )
                    }
                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(
                                context,
                                "",
                                getString(R.string.something_went_wrong)
                            )
                        }
                    } catch (ex: Exception) {
                        Log.e("MyProfilePage Activity", "Within Throwable Exception::" + it.message)

                    }
                })

        } else {
            hideProgressDialog()
            Utility.simpleAlert(
                context,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }
    }

    private fun removeWishListApi(
        iv_AddToWishlist: ImageView,
        iv_RemoveFromWishlist: ImageView,
        menuId: String
    ) {

        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.removeWishList(
                userPref.user.id!!,
                menuId,
                userPref.user.token!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({

                    if (it.responseCode.equals("200")) {

                        iv_RemoveFromWishlist.visibility = View.GONE
                        iv_AddToWishlist.visibility = View.VISIBLE
                        Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()

                        iv_AddToWishlist.setOnClickListener({
                            addWishListApi(iv_RemoveFromWishlist, iv_AddToWishlist, menuId)
                        })

                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(requireContext(), userPref)
                    } else {

                        hideProgressDialog()
                        Utility.simpleAlert(context, getString(R.string.error), it.responseMessage)
                    }

                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(
                                context,
                                "",
                                getString(R.string.something_went_wrong)
                            )
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })

        } else {
            hideProgressDialog()
            Utility.simpleAlert(
                context,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }
    }


    private fun getCartCount() {

        if (Utility.getInstance().checkInternetConnection(context)) {

            apiService.userCartCount(
                userPref.user.id!!,
                userPref.user.token!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                /* .doOnSubscribe { showProgressDialog() }
                 .doOnCompleted { hideProgressDialog() }*/
                .subscribe({

                    if (it.responseCode.equals("200")) {

                        //Toast.makeText(this,it.responseMessage,Toast.LENGTH_SHORT).show()

                        val activity = requireActivity() as ShopByCategoryActivity
                        activity.cartCount = it.data.count_cart
                        activity.totalAmount = if (it.data.total_amount != null) {
                            it.data.total_amount
                        } else {
                            0
                        }

                        activity.minimumOrderLimit = it.data.minimum_order_limit

                        val total = it.data.count_cart
                        if (total != 0) {

                            requireActivity().findViewById<TextView>(R.id.tv_CartCount).text =
                                "" + total
                            // tv_CartCount.setText(""+total)
                            Log.d("UserCartCountApi", "" + total)

                        } else {
                            requireActivity().findViewById<TextView>(R.id.tv_CartCount).text = ""
                        }

                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(requireContext(), userPref)
                    } else {

                        requireActivity().findViewById<TextView>(R.id.tv_CartCount).text = ""
                        // tv_CartCount.setText("")
                        Log.d("UserCartCountApi", "" + it.responseMessage)

                        /* hideProgressDialog()
                         Utility.simpleAlert(this, getString(R.string.error), it.responseMessage)*/
                    }

                }, {

                    try {

                        if (it is ConnectException) {

                            Log.d(
                                "UserCartCountApi",
                                "" + getString(R.string.check_network_connection)
                            )

                        } else {

                            Log.d("UserCartCountApi", "" + it.message)
                        }
                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }
                })

        } else {
            Log.d("UserCartCountApi", "" + getString(R.string.check_network_connection))
            /* hideProgressDialog()
             Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection))*/
        }

    }

    override fun onPause() {
        super.onPause()
        if (::productListItem.isInitialized && productListItem != null) {

            if (productListItem.popupWindow != null) {
                productListItem.popupWindow!!.dismiss()
            }
        }
    }

    /*  private fun addIntoCart(ivAddtocart: ImageView, ivAddedintocart: ImageView, menu: MenuList,
                              qty :String) {

          Log.e("QtyOfItemAdded","InCart"+qty);

          if (Utility.getInstance().checkInternetConnection(context)) {

              apiService.addCart(
                      userPref.user.id!!,
                      menu.id,
                      qty,"","",
                      userPref.user.token!!)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .doOnSubscribe { showProgressDialog() }
                      .doOnCompleted { hideProgressDialog() }
                      .subscribe({

                          if (it.responseCode.equals("200")) {

                              ivAddtocart.visibility = View.GONE
                              ivAddedintocart.visibility = View.VISIBLE
                              Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()

                              ivAddedintocart.setOnClickListener({
                                  removeItemFromCart(ivAddtocart,ivAddedintocart,menu)
                              })

                          }else{

                              hideProgressDialog()
                              Utility.simpleAlert(context, getString(R.string.error), it.responseMessage)
                          }

                      },{

                          if (it is ConnectException) {
                              hideProgressDialog()
                              Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection))
                          } else {
                              hideProgressDialog()
                              Utility.simpleAlert(context, getString(R.string.error), it.message)
                          }

                      })

          } else {
              hideProgressDialog()
              Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection))
          }

      }

      private fun removeItemFromCart(ivAddtocart: ImageView, ivAddedintocart: ImageView, menu: MenuList) {

          if (Utility.getInstance().checkInternetConnection(context)) {
              apiService.removeFromCart(
                      userPref.user.id!!,
                      menu.id,
                      userPref.user.token!!)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .doOnSubscribe { showProgressDialog() }
                      .doOnCompleted { hideProgressDialog() }
                      .subscribe({

                          if (it.responseCode.equals("200")) {

                              ivAddedintocart.visibility = View.GONE
                              ivAddtocart.visibility =View.VISIBLE

                              ivAddtocart.setOnClickListener({

                                  addIntoCart(ivAddtocart, ivAddedintocart, menu,qtyOfProduct.toString())

                              })


                          }else{

                              hideProgressDialog()
                              Utility.simpleAlert(context, getString(R.string.error), it.responseMessage)
                          }

                      },{

                          if (it is ConnectException) {
                              hideProgressDialog()
                              Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection))
                          } else {
                              hideProgressDialog()
                              Utility.simpleAlert(context, getString(R.string.error), it.message)
                          }

                      })

          } else {
              hideProgressDialog()
              Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection))
          }

      }*/
}