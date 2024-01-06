package com.easym.vegie.adapter

import android.app.Dialog
import android.content.Context
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.AppCompatSpinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.easym.vegie.R
import com.easym.vegie.Utils.RecyclerItemClickListener
import com.easym.vegie.Utils.Utility
import com.easym.vegie.Utils.Utils
import com.easym.vegie.api.ApiService1
import com.easym.vegie.model.product.MenuList
import com.easym.vegie.model.product.MenuVerient
import com.easym.vegie.model.productbrand.ProductBrandResult
import com.easym.vegie.sharePref.UserPref
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.ConnectException


class ProductListItemRVAdapter(
    val context: Context,
    val menuList: MutableList<MenuList>,
    val wishList: WishList,
    val apiService: ApiService1,
    val userPref: UserPref,
    val utils: Utils,
    var quotationId: String,
) : RecyclerView.Adapter<ProductListItemRVAdapter.RecyclerViewItem>() {

    var dialog: Dialog? = null
    var brandIdArr = arrayOfNulls<String>(menuList.size)
    var varientIdArr = arrayOfNulls<String>(menuList.size)
    var popupWindow: PopupWindow? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewItem {

        val view = LayoutInflater.from(context).inflate(
            R.layout.shopping_product_list_item,
            parent, false
        )

        return RecyclerViewItem(view)
    }

    override fun getItemCount(): Int {

        for (i in 0..menuList.size - 1) {
            brandIdArr[i] = ""
            varientIdArr[i] = ""
        }

        return menuList.size

    }

    override fun onBindViewHolder(holder: RecyclerViewItem, position: Int) {


        if (menuList.get(position).unit!="Bunch"&&menuList[position].unit!="pcs"){
            holder.count.inputType=InputType.TYPE_CLASS_NUMBER
        }

        else{
            holder.count.inputType=InputType.TYPE_CLASS_NUMBER
        }


        Picasso.get()
            .load(menuList[position].image)
            .placeholder(R.drawable.image_loading)
            .into(holder.img_product)

        if (!menuList[position].other_name.equals("false")) {
            holder.txt_product_name.text = (menuList[position].menuName + "\n"
                    + menuList.get(position).other_name)
        } else {
            holder.txt_product_name.text = menuList.get(position).menuName
        }

        if (menuList[position].itype == "1") {
            holder.ll_withoutdecimal.visibility = View.VISIBLE


        } else {

            holder.ll_withoutdecimal.visibility = View.VISIBLE

        }

        holder.txt_price.text = menuList.get(position).price + " ( " + menuList.get(position).unit + " ) "

        if (menuList.get(position).haveBrand) {
            holder.moreBrandContainer.visibility = View.VISIBLE
        } else {
            holder.moreBrandContainer.visibility = View.GONE
        }

        if (menuList.get(position).is_cart) {

            val cart_qty = menuList.get(position).cart_qty
            holder.count.setText(cart_qty)

            holder.count.isFocusable = false
            holder.count.isEnabled = true

            holder.count.setOnClickListener {
                Toast.makeText(
                    context,
                    "You can update the quantity in My Cart Page",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            holder.count.isFocusableInTouchMode = true
            holder.count.isFocusable = true
            holder.count.isEnabled = true
        }

        val discount = menuList.get(position).discount
        if (!discount.equals("") && !discount.equals("0.00")) {
            holder.tv_Discount.text = menuList.get(position).discount + " Off"
            holder.tv_Discount.visibility = View.VISIBLE
        }

        if (menuList.get(position).quantity.equals("0") || menuList.get(position).quantity.equals("null")) {
            holder.relative_layout1.visibility = View.VISIBLE
        }

        holder.moreBrandContainer.setOnClickListener {


            if (Utility.getInstance().checkInternetConnection(context)) {
                apiService.getProductBrand(
                    menuList.get(position).id,
                    userPref.user.token!!,
                    userPref.getUserPreferLanguageCode()
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgressDialog() }
                    .doOnCompleted { hideProgressDialog() }
                    .subscribe({

                        if (it.responseCode.equals("200")) {
                            if (it.data.productBrandResult == null) {
                                Toast.makeText(context, "No brand found", Toast.LENGTH_SHORT).show()
                                return@subscribe
                            }

                            val brandResults = it.data.productBrandResult.result

                            showPopUp(
                                holder.moreBrandContainer,
                                brandResults,
                                position,
                                menuList.get(position).id,
                                holder
                            )

                        } else if (it.responseCode == "403") {
//                            utils.openLogoutDialog(context, userPref)
                        } else {

                            hideProgressDialog()
                            Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()
                            //Utility.simpleAlert(context, context.getString(R.string.error), it.responseMessage)
                        }

                    }, {

                        try {

                            if (it is ConnectException) {
                                hideProgressDialog()
                                Utility.simpleAlert(
                                    context, context.getString(R.string.error),
                                    context.getString(R.string.check_network_connection)
                                )
                            } else {
                                hideProgressDialog()
                                it.printStackTrace()
                                Utility.simpleAlert(
                                    context,
                                    "",
                                    context.getString(R.string.something_went_wrong)
                                )
                            }

                        } catch (ex: Exception) {
                            Log.e("", "Within Throwable Exception::" + it.message)
                        }

                    })

            } else {
                hideProgressDialog()
                Utility.simpleAlert(
                    context, context.getString(R.string.error),
                    context.getString(R.string.check_network_connection)
                )
            }
        }

        if (menuList.get(position).isWishlist) {

            holder.iv_AddToWishlist.visibility = View.GONE
            holder.iv_RemoveFromWishlist.visibility = View.VISIBLE

            holder.iv_RemoveFromWishlist.setOnClickListener {

                wishList.removeFromWishList(
                    holder.iv_AddToWishlist,
                    holder.iv_RemoveFromWishlist,
                    menuList.get(position).id
                )
            }

        } else {

            holder.iv_RemoveFromWishlist.visibility = View.GONE
            holder.iv_AddToWishlist.visibility = View.VISIBLE

            holder.iv_AddToWishlist.setOnClickListener {
                wishList.addToWishList(
                    holder.iv_RemoveFromWishlist,
                    holder.iv_AddToWishlist,
                    menuList.get(position).id
                )
            }
        }

        if (menuList.get(position).is_cart) {

            holder.iv_AddToCart.visibility = View.GONE
            holder.iv_AddedIntoCart.visibility = View.VISIBLE

            holder.iv_AddedIntoCart.setOnClickListener {


                // cart.removeFromCart(holder.iv_AddToCart,holder.iv_AddedIntoCart,position)

                if (Utility.getInstance().checkInternetConnection(context)) {

                    apiService.removeFromCart(
                        userPref.user.id!!,
                        menuList[position].id,
                        userPref.user.token!!,
                        brandIdArr[position].toString()
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { showProgressDialog() }
                        .doOnCompleted { hideProgressDialog() }
                        .subscribe({

                            if (it.responseCode.equals("200")) {

                                holder.iv_AddedIntoCart.visibility = View.GONE
                                holder.iv_AddToCart.visibility = View.VISIBLE

                                wishList.refreshCartCountList()

                                holder.iv_AddToCart.setOnClickListener {
                                    //   Toast.makeText(context,"Button 1 clicked",Toast.LENGTH_LONG).show()
                                    Log.e("@@quotationId", "quotationId")

                                    val qty = holder.count.text.toString().trim()

                                    Log.e("QtyOfItemAdded", "InCart" + qty)

                                    if (TextUtils.isEmpty(qty) || qty.equals("0.0") || qty == "0") {

                                        Snackbar.make(
                                            holder.itemView,
                                            "Please enter or select the quantity of product",
                                            Snackbar.LENGTH_SHORT
                                        ).show()

                                        return@setOnClickListener
                                    }

                                    if (quotationId == "") {
                                        addIntoCart(
                                            holder.iv_AddToCart,
                                            holder.iv_AddedIntoCart,
                                            menuList[position],
                                            qty,
                                            varientIdArr[position]!!,
                                            brandIdArr[position]!!,
                                            holder
                                        )
                                    } else {
                                        updateQuotation(
                                            menuList[position].id,
                                            qty,
                                            brandIdArr[position]!!
                                        )
                                    }

                                }


                                holder.add1.setOnClickListener(null)
                                holder.minus.setOnClickListener(null)

                                holder.add1.setOnClickListener {
                                    val qtyStr: Int = String.format(
                                        holder.count.text.toString()
                                    ).toInt()
                                    var qtyNumber = qtyStr

                                    qtyNumber = qtyNumber + 1
                                    qtyNumber = String.format(qtyNumber.toString()).toInt()
                                    holder.count.setText("" + qtyNumber)
                                    val pos: Int = holder.count.text.length
                                    holder.count.setSelection(pos)

                                }
                                holder.add1.setOnClickListener({


                                    val qtyStr: Int = String.format(
                                        holder.count.text.toString()
                                    ).toInt()
                                    var qtyNumber = qtyStr

                                    qtyNumber = qtyNumber + 1
                                    qtyNumber = String.format(qtyNumber.toString()).toInt()
                                    holder.count.setText("" + qtyNumber)
                                    val pos: Int = holder.count.text.length
                                    holder.count.setSelection(pos)
                                    //  qty.qtyOfItem(qtyNumber)


                                })

                                holder.minus.setOnClickListener({

                                    val qtyStr: Int = String.format(
                                        holder.count.text.toString()
                                    ).toInt()
                                    var qtyNumber = qtyStr
                                    if (qtyNumber > 0) {
                                        qtyNumber = qtyNumber - 1
                                        qtyNumber = String.format(qtyNumber.toString()).toInt()
                                        holder.count.setText("" + qtyNumber)
                                        val pos: Int = holder.count.text.length
                                        holder.count.setSelection(pos)
                                        //  qty.qtyOfItem(qtyNumber)
                                    }
                                })
                                holder.minus.setOnClickListener({

                                    val qtyStr: Int = String.format(
                                        holder.count.text.toString()
                                    ).toInt()

                                    //val qtyStr = holder.tv_productcount.text.toString().trim
                                    var qtyNumber = qtyStr.toInt()
                                    if (qtyNumber > 0) {
                                        qtyNumber = qtyNumber - 0
                                        qtyNumber =
                                            String.format(qtyNumber.toString()).toInt()
                                        holder.count.setText("" + qtyNumber)
                                        val pos: Int = holder.count.text.length
                                        holder.count.setSelection(pos)
                                        //  qty.qtyOfItem(qtyNumber)
                                    }
                                })


                            } else if (it.responseCode == "403") {
                                utils.openLogoutDialog(context, userPref)
                            } else {

                                hideProgressDialog()
                                Utility.simpleAlert(
                                    context,
                                    context.getString(R.string.info_dialog_title),
                                    it.responseMessage
                                )
                            }

                        }, {

                            try {

                                if (it is ConnectException) {
                                    hideProgressDialog()
                                    Utility.simpleAlert(
                                        context, context.getString(R.string.error),
                                        context.getString(R.string.check_network_connection)
                                    )
                                } else {
                                    hideProgressDialog()
                                    it.printStackTrace()
                                    Utility.simpleAlert(
                                        context,
                                        "",
                                        context.getString(R.string.something_went_wrong)
                                    )
                                }

                            } catch (ex: Exception) {
                                Log.e("", "Within Throwable Exception::" + it.message)
                            }

                        })

                } else {
                    hideProgressDialog()
                    Utility.simpleAlert(
                        context, context.getString(R.string.error),
                        context.getString(R.string.check_network_connection)
                    )
                }

            }

        } else {

            holder.iv_AddedIntoCart.visibility = View.GONE
            holder.iv_AddToCart.visibility = View.VISIBLE








            holder.iv_AddToCart.setOnClickListener {
                var qty = ""
                if (menuList.get(position).itype == "1") {
                    qty = holder.count.text.toString().trim()

                } else {
                    qty = holder.count.text.toString().trim()

                }
                //  Toast.makeText(context,"Button 2 clicked",Toast.LENGTH_LONG).show()

                if (TextUtils.isEmpty(qty) || qty.equals("0.0") || qty == "0") {

                    Snackbar.make(
                        holder.itemView, "Please enter or select the quantity of product",
                        Snackbar.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }



                Log.e("QtyOfItemAdded", "InCart" + qty)


                // cart.addToCart(holder.iv_AddToCart,holder.iv_AddedIntoCart,position,qtyNumber)

                if (Utility.getInstance().checkInternetConnection(context)) {

                    Log.e("MenuVarient", "" + varientIdArr[position].toString())
                    Log.e("BrandVarient", "" + brandIdArr[position].toString())


                    if (quotationId == "") {

                        val rbUserId =
                            userPref.user.id!!.toRequestBody("text/plain".toMediaTypeOrNull())
                        val rbMenuId =
                            menuList.get(position).id.toRequestBody("text/plain".toMediaTypeOrNull())
                        val rbQty = qty.toRequestBody("text/plain".toMediaTypeOrNull())
                        val rbVarient = "".toRequestBody("text/plain".toMediaTypeOrNull())
                        val rbBrandId =
                            brandIdArr[position]!!.toRequestBody("text/plain".toMediaTypeOrNull())
                        val rbToken =
                            userPref.user.token!!.toRequestBody("text/plain".toMediaTypeOrNull())

                        apiService.addCart(
                            rbUserId,
                            rbMenuId,
                            rbQty, rbVarient, rbBrandId,
                            rbToken
                        )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe { showProgressDialog() }
                            .doOnCompleted { hideProgressDialog() }
                            .subscribe({

                                if (it.responseCode.equals("200")) {

                                    wishList.refreshCartCountList()

                                    holder.iv_AddToCart.visibility = View.GONE
                                    holder.iv_AddedIntoCart.visibility = View.VISIBLE

                                    holder.count.isFocusable = false
                                    holder.count.isEnabled = true
                                    holder.count.setOnClickListener {
                                        Toast.makeText(
                                            context,
                                            "You can update the quantity in My Cart Page",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    //Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()

                                    // menuList.get(position).is_cart = true
                                    // notifyDataSetChanged()


                                    holder.add1.setOnClickListener(null)
                                    holder.minus.setOnClickListener(null)

                                    holder.add1.setOnClickListener {
                                        Toast.makeText(
                                            context,
                                            "You can update the quantity in My Cart Page",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    holder.add1.setOnClickListener {
                                        Toast.makeText(
                                            context,
                                            "You can update the quantity in My Cart Page",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    holder.minus.setOnClickListener {
                                        Toast.makeText(
                                            context,
                                            "You can update the quantity in My Cart Page",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    holder.minus.setOnClickListener {
                                        Toast.makeText(
                                            context,
                                            "You can update the quantity in My Cart Page",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    holder.iv_AddedIntoCart.setOnClickListener {

                                        removeItemFromCart(
                                            holder.iv_AddToCart,
                                            holder.iv_AddedIntoCart,
                                            menuList.get(position), "", holder
                                        )

                                    }

                                } else if (it.responseCode == "403") {
                                    utils.openLogoutDialog(context, userPref)
                                } else {

                                    hideProgressDialog()
                                    Utility.simpleAlert(
                                        context,
                                        context.getString(R.string.info_dialog_title),
                                        it.responseMessage
                                    )
                                }

                            }, {

                                try {

                                    if (it is ConnectException) {
                                        hideProgressDialog()
                                        Utility.simpleAlert(
                                            context,
                                            context.getString(R.string.error),
                                            context.getString(R.string.check_network_connection)
                                        )
                                    } else {
                                        hideProgressDialog()
                                        it.printStackTrace()
                                        Utility.simpleAlert(
                                            context,
                                            "",
                                            context.getString(R.string.something_went_wrong)
                                        )
                                    }

                                } catch (ex: Exception) {
                                    Log.e("", "Within Throwable Exception::" + it.message)
                                }

                            })


                    } else {
                        updateQuotation(menuList.get(position).id, qty, brandIdArr[position]!!)
                    }


                } else {
                    hideProgressDialog()
                    Utility.simpleAlert(
                        context, context.getString(R.string.error),
                        context.getString(R.string.check_network_connection)
                    )
                }


            }


        }

        holder.add1.setOnClickListener {
            if (!menuList.get(position).is_cart) {
//                val qtyStr: Double =
//                    String.format("%.1f", holder.tv_productcount.text.toString().toDouble())
//                        .toDouble()
//                var qtyNumber = qtyStr.toFloat()

//                 val qtyStr = holder.tv_productcount.text.toString().trim()
//                var qtyNumber = qtyStr
//                qtyNumber = qtyNumber + 1
                if (menuList.get(position).unit!="Bunch"&&menuList[position].unit != "pcs") {
                    holder.count.inputType = InputType.TYPE_CLASS_NUMBER
//                    if (holder.tv_productcount.text.toString().contains(".")) {
//                        Toast.makeText(context, "Please don't use decimal", Toast.LENGTH_SHORT).show()
////                        if (menuList.get(position).menuCategoryId=="68"){
////                            Toast.makeText(context, "Please don't use decimal", Toast.LENGTH_SHORT).show()
////                        }
//                    } else {
                    val qtyStr: Int = String.format(
                        holder.count.text.toString()
                    ).toInt()
                    var qtyNumber = qtyStr

                    qtyNumber = qtyNumber + 1


                    holder.count.setText("" + qtyNumber)
                    val pos: Int = holder.count.text.length
                    holder.count.setSelection(pos)

                } else {
                    holder.count.inputType = InputType.TYPE_CLASS_NUMBER
//                    if (holder.tv_productcount.text.toString().contains(".")) {
//                        Toast.makeText(context, "Please don't use decimal", Toast.LENGTH_SHORT).show()
////                        if (menuList.get(position).menuCategoryId=="68"){
////                            Toast.makeText(context, "Please don't use decimal", Toast.LENGTH_SHORT).show()
////                        }
//                    } else {
                    val qtyStr: Int = String.format(
                        holder.count.text.toString()
                    ).toInt()
                    var qtyNumber = qtyStr

                    qtyNumber = qtyNumber + 1


                    holder.count.setText("" + qtyNumber)
                    val pos: Int = holder.count.text.length
                    holder.count.setSelection(pos)

//                    }

                }


            } else {

                Toast.makeText(
                    context,
                    "You can update the quantity in My Cart Page",
                    Toast.LENGTH_SHORT
                ).show()

            }

            //  qty.qtyOfItem(qtyNumber)

        }
        holder.add1.setOnClickListener {

            if (!menuList.get(position).is_cart) {

                val qtyStr: Int = String.format(
                    holder.count.text.toString()
                ).toInt()
                var qtyNumber = qtyStr

                qtyNumber = qtyNumber + 1
                qtyNumber = String.format(qtyNumber.toString()).toInt()
                holder.count.setText("" + qtyNumber)
                val pos: Int = holder.count.text.length
                holder.count.setSelection(pos)
                //  qty.qtyOfItem(qtyNumber)


            } else {

                Toast.makeText(
                    context,
                    "You can update the quantity in My Cart Page",
                    Toast.LENGTH_SHORT
                ).show()

                /*  val qtyStr: Double = String.format("%.1f", holder.tv_productcount.text.toString().toDouble()).toDouble()
                // val qtyStr = holder.tv_productcount.text.toString().trim()
                var qtyNumber = qtyStr.toFloat()
                qtyNumber = qtyNumber + 0.1f
                qtyNumber = String.format("%.1f", qtyNumber.toFloat()).toFloat()
                holder.tv_productcount.setText("" + qtyNumber)
                val pos: Int = holder.tv_productcount.getText().length
                holder.tv_productcount.setSelection(pos)*/


            }

            //  qty.qtyOfItem(qtyNumber)

        }

        holder.minus.setOnClickListener {

            if (!menuList.get(position).is_cart) {


                if (menuList.get(position).unit!="Bunch"&&menuList.get(position).unit!="pcs") {
                    val qtyStr: Int = String.format(
                        holder.count.text.toString()
                    ).toInt()
                    var qtyNumber = qtyStr
                    if (qtyNumber > 0) {
                        qtyNumber = qtyNumber - 1
                        qtyNumber = String.format(qtyNumber.toString()).toInt()
                        holder.count.setText("" + qtyNumber)
                        val pos: Int = holder.count.text.length
                        holder.count.setSelection(pos)
                        //  qty.qtyOfItem(qtyNumber)
                    }

                } else{
                    val qtyStr: Int = String.format(
                        holder.count.text.toString()
                    ).toInt()
                    var qtyNumber = qtyStr
                    if (qtyNumber > 0) {
                        qtyNumber = qtyNumber - 1


                        qtyNumber = String.format(qtyNumber.toString()).toInt()
                        holder.count.setText("" + qtyNumber)
                        val pos: Int = holder.count.text.length
                        holder.count.setSelection(pos)


                    }
                }

            } else {

                Toast.makeText(
                    context,
                    "You can update the quantity in My Cart Page",
                    Toast.LENGTH_SHORT
                ).show()

                /*val qtyStr: Double = String.format("%.1f",
                        holder.tv_productcount.text.toString().toDouble()).toDouble()
                //val qtyStr = holder.tv_productcount.text.toString().trim
                var qtyNumber = qtyStr.toFloat()
                if (qtyNumber > 0.0) {
                    qtyNumber = qtyNumber - 0.1f
                    qtyNumber = String.format("%.1f", qtyNumber.toFloat()).toFloat()
                    holder.tv_productcount.setText("" + qtyNumber)
                    val pos: Int = holder.tv_productcount.getText().length
                    holder.tv_productcount.setSelection(pos)
                    //  qty.qtyOfItem(qtyNumber)
                }*/

            }

        }
        holder.minus.setOnClickListener {

            if (!menuList.get(position).is_cart) {


                val qtyStr: Int = String.format(
                    holder.count.text.toString()
                ).toInt()
                var qtyNumber = qtyStr
                if (qtyNumber > 0) {
                    qtyNumber = qtyNumber - 1
                    qtyNumber = String.format(qtyNumber.toString()).toInt()
                    holder.count.setText("" + qtyNumber)
                    val pos: Int = holder.count.text.length
                    holder.count.setSelection(pos)
                    //  qty.qtyOfItem(qtyNumber)-1
                }

            } else {

                Toast.makeText(
                    context,
                    "You can update the quantity in My Cart Page",
                    Toast.LENGTH_SHORT
                ).show()

                /*val qtyStr: Double = String.format("%.1f",
                        holder.tv_productcount.text.toString().toDouble()).toDouble()
                //val qtyStr = holder.tv_productcount.text.toString().trim
                var qtyNumber = qtyStr.toFloat()
                if (qtyNumber > 0.0) {
                    qtyNumber = qtyNumber - 0.1f
                    qtyNumber = String.format("%.1f", qtyNumber.toFloat()).toFloat()
                    holder.tv_productcount.setText("" + qtyNumber)
                    val pos: Int = holder.tv_productcount.getText().length
                    holder.tv_productcount.setSelection(pos)
                    //  qty.qtyOfItem(qtyNumber)
                }*/

            }

        }

        //set The Menu Varient in the spinner
        val menuVarient = menuList.get(position).menuVerient

        if (menuVarient.size != 0) {

            var productVarientList: List<String> = ArrayList()

            var list = productVarientList.toMutableList()

            list.add("Select " + menuVarient.get(0).unit)


            for (item in menuVarient) {

                list.add(item.quantity)
            }

            addProductVarientInSpinner(list, holder.productVarient, menuVarient, position)

        } else {
            holder.rl_Varient.visibility = View.INVISIBLE
        }
    }

    inner class RecyclerViewItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val spinner_Brand: AppCompatSpinner
        val iv_BrandDropDown: ImageView
        val moreBrandContainer: RelativeLayout
        val txt_product_name: TextView
        val txt_price: TextView
        val img_product: ImageView
        val tv_Discount: TextView
        val frame_WishList: FrameLayout
        val iv_AddToWishlist: ImageView
        val iv_RemoveFromWishlist: ImageView
        val frame_Cart: FrameLayout
        val iv_AddToCart: ImageView
        val iv_AddedIntoCart: ImageView

        //val et_productcount : EditText


        val productVarient: Spinner
        val rl_Varient: RelativeLayout
        val relative_layout1: RelativeLayout
        val ll_withoutdecimal: RelativeLayout
        val add1: TextView
        val count: EditText
        val minus: TextView

        init {

            spinner_Brand = itemView.findViewById(R.id.spinner_Brand)
            ll_withoutdecimal = itemView.findViewById(R.id.ll_withoutdecimal)
            add1 = itemView.findViewById(R.id.add)
            count = itemView.findViewById(R.id.count)
            minus = itemView.findViewById(R.id.minus)
            iv_BrandDropDown = itemView.findViewById(R.id.iv_BrandDropDown)
            moreBrandContainer = itemView.findViewById(R.id.moreBrandContainer)
            txt_product_name = itemView.findViewById(R.id.txt_product_name)
            txt_price = itemView.findViewById(R.id.txt_price)
            img_product = itemView.findViewById(R.id.img_product)
            tv_Discount = itemView.findViewById(R.id.tv_Discount)
            frame_WishList = itemView.findViewById(R.id.frame_WishList)
            iv_AddToWishlist = itemView.findViewById(R.id.iv_AddToWishlist)
            iv_RemoveFromWishlist = itemView.findViewById(R.id.iv_RemoveFromWishlist)
            frame_Cart = itemView.findViewById(R.id.frame_Cart)
            iv_AddToCart = itemView.findViewById(R.id.iv_AddToCart)
            iv_AddedIntoCart = itemView.findViewById(R.id.iv_AddedIntoCart)
            relative_layout1 = itemView.findViewById(R.id.relative_layout1)

            //et_productcount = itemView.findViewById(R.id.et_productcount)
            productVarient = itemView.findViewById(R.id.productVarient)
            rl_Varient = itemView.findViewById(R.id.rl_Varient)

        }
    }

    private fun showPopUp(
        view: View,
        result: List<ProductBrandResult>,
        position: Int,
        menuId: String,
        holder: RecyclerViewItem,
    ) {
        val popupView: View =
            LayoutInflater.from(context).inflate(R.layout.brand_list_custom_alert_dialog, null)
        popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

        val recyclerView = popupView.findViewById<RecyclerView>(R.id.rv_Brand)

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val brandRVAdapter = BrandRVAdapter(context, result)
        recyclerView.adapter = brandRVAdapter
        recyclerView.layoutManager = lm

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(context,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, i: Int) {

                        Picasso.get()
                            .load(result.get(i).brand_image)
                            .placeholder(R.drawable.image_loading)
                            .into(holder.img_product)

                        if (!result.get(i).other_name.equals("false")) {
                            holder.txt_product_name.text = (result.get(i).brandName + "/"
                                    + result.get(i).other_name)
                        } else {
                            holder.txt_product_name.text = result.get(i).brandName
                        }

                        holder.txt_price.text =
                            result.get(i).price + " ( " + result.get(i).unit + " ) "

                        checkSelectedBrandProductIsAddedInCart(
                            menuId,
                            result.get(i).id,
                            holder, i, position, result,
                            popupWindow!!
                        )

                    }
                })
        )

        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.showAsDropDown(view, 0, 0)


    }

    private fun checkSelectedBrandProductIsAddedInCart(
        menuId: String,
        brandId: String,
        holder: RecyclerViewItem,
        i: Int,
        position: Int,
        result: List<ProductBrandResult>,
        popupWindow: PopupWindow,
    ) {

        apiService.checkSameBrandProduct(
            userPref.user.token!!,
            userPref.user.id!!,
            menuId,
            brandId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgressDialog() }
            .doOnCompleted { hideProgressDialog() }
            .subscribe({

                if (it.responseCode.equals("200")) {

                    holder.iv_AddedIntoCart.setOnClickListener(null)

                    hideProgressDialog()
                    Toast.makeText(context, "" + it.responseMessage, Toast.LENGTH_SHORT).show()
                    val roundedNumber = Math.round(it.data.qty.toDouble()).toInt()

                    holder.count.setText(roundedNumber)

                    val cart_id = it.data.id

                    popupWindow.dismiss()

                    holder.iv_AddToCart.visibility = View.GONE
                    holder.iv_AddedIntoCart.visibility = View.VISIBLE


                    holder.iv_AddedIntoCart.setOnClickListener {
                        Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show()
                        // cart.removeFromCart(holder.iv_AddToCart,holder.iv_AddedIntoCart,position)
                        if (Utility.getInstance().checkInternetConnection(context)) {
                            apiService.removeFromCart(
                                userPref.user.id!!,
                                menuList.get(position).id,
                                userPref.user.token!!,
                                brandIdArr[position].toString()
                            )
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe { showProgressDialog() }
                                .doOnCompleted { hideProgressDialog() }
                                .subscribe({

                                    if (it.responseCode.equals("200")) {

                                        holder.iv_AddedIntoCart.visibility = View.GONE
                                        holder.iv_AddToCart.visibility = View.VISIBLE

                                        wishList.refreshCartCountList()


                                        holder.iv_AddToCart.setOnClickListener {

                                            //   Toast.makeText(context,"Button 3 clicked",Toast.LENGTH_LONG).show()

                                            Log.e("@@quotationId", "quotationId")
                                            val qty =
                                                holder.count.text.toString().trim()

                                            Log.e("QtyOfItemAdded", "InCart" + qty)

                                            if (TextUtils.isEmpty(qty) || qty.equals("0.0") || qty == "0") {

                                                Snackbar.make(
                                                    holder.itemView,
                                                    "Please enter or select the quantity of product",
                                                    Snackbar.LENGTH_SHORT
                                                ).show()

                                                return@setOnClickListener
                                            }

                                            if (quotationId == "") {
                                                addIntoCart(
                                                    holder.iv_AddToCart,
                                                    holder.iv_AddedIntoCart,
                                                    menuList.get(position),
                                                    qty,
                                                    varientIdArr[position]!!,
                                                    brandIdArr[position]!!,
                                                    holder
                                                )
                                            } else {
                                                updateQuotation(
                                                    menuList.get(position).id,
                                                    qty,
                                                    brandIdArr[position]!!
                                                )
                                            }

                                        }

                                        holder.add1.setOnClickListener {


                                            val qtyStr: Int = String.format(
                                                holder.count.text.toString()
                                            ).toInt()
                                            var qtyNumber = qtyStr

                                            qtyNumber = qtyNumber + 1
                                            qtyNumber = String.format(qtyNumber.toString()).toInt()
                                            holder.count.setText("" + qtyNumber)
                                            val pos: Int = holder.count.text.length
                                            holder.count.setSelection(pos)

                                            //    Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show()


                                        }
                                        holder.add1.setOnClickListener {

                                            val qtyStr: Int = String.format(
                                                holder.count.text.toString()
                                            ).toInt()
                                            var qtyNumber = qtyStr
                                            if (qtyNumber > 0) {
                                                qtyNumber = qtyNumber + 1
                                                qtyNumber =
                                                    String.format(qtyNumber.toString()).toInt()
                                                holder.count.setText("" + qtyNumber)
                                                val pos: Int = holder.count.text.length
                                                holder.count.setSelection(pos)
                                                //  qty.qtyOfItem(qtyNumber)
                                            }
                                            //    Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show()


                                        }

                                        holder.minus.setOnClickListener {

                                            val qtyStr: Int = String.format(
                                                holder.count.text.toString()
                                            ).toInt()
                                            var qtyNumber = qtyStr
                                            if (qtyNumber > 0) {
                                                qtyNumber = qtyNumber - 1
                                                qtyNumber = String.format(qtyNumber.toString()).toInt()
                                                holder.count.setText("" + qtyNumber)
                                                val pos: Int = holder.count.text.length
                                                holder.count.setSelection(pos)
                                                //  qty.qtyOfItem(qtyNumber)
                                            }
                                            //  Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show()


                                        }
                                        holder.minus.setOnClickListener {


                                            val qtyStr: Int = String.format(
                                                holder.count.text.toString()
                                            ).toInt()
                                            var qtyNumber = qtyStr
                                            if (qtyNumber > 0) {
                                                qtyNumber = qtyNumber - 1
                                                qtyNumber =
                                                    String.format(qtyNumber.toString()).toInt()
                                                holder.count.setText("" + qtyNumber)
                                                val pos: Int = holder.count.text.length
                                                holder.count.setSelection(pos)
                                                //  qty.qtyOfItem(qtyNumber)
                                            }

                                            //  Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show()


                                        }


                                    } else if (it.responseCode == "403") {
                                        utils.openLogoutDialog(context, userPref)
                                    }
                                    else {

                                        hideProgressDialog()
                                        Utility.simpleAlert(
                                            context,
                                            context.getString(R.string.info_dialog_title),
                                            it.responseMessage
                                        )
                                    }

                                }, {

                                    try {

                                        if (it is ConnectException) {
                                            hideProgressDialog()
                                            Utility.simpleAlert(
                                                context,
                                                context.getString(R.string.error),
                                                context.getString(R.string.check_network_connection)
                                            )
                                        } else {
                                            hideProgressDialog()
                                            it.printStackTrace()
                                            Utility.simpleAlert(
                                                context,
                                                "",
                                                context.getString(R.string.something_went_wrong)
                                            )
                                        }

                                    } catch (ex: Exception) {
                                        Log.e("", "Within Throwable Exception::" + it.message)
                                    }

                                })

                        } else {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context, context.getString(R.string.error),
                                context.getString(R.string.check_network_connection)
                            )
                        }

                    }


                    holder.add1.setOnClickListener {

                        Toast.makeText(
                            context,
                            "You can update the quantity in My Cart Page",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                    holder.add1.setOnClickListener {

                        Toast.makeText(
                            context,
                            "You can update the quantity in My Cart Page",
                            Toast.LENGTH_SHORT
                        ).show()


                    }

                    holder.minus.setOnClickListener {

                        Toast.makeText(
                            context,
                            "You can update the quantity in My Cart Page",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    holder.minus.setOnClickListener {

                        Toast.makeText(
                            context,
                            "You can update the quantity in My Cart Page",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                } else if (it.responseCode.equals("400")) {

                    holder.iv_AddedIntoCart.setOnClickListener(null)

                    holder.count.setText("1")


                    popupWindow.dismiss()

                    Log.e("brandId", "" + brandIdArr[position])

                    holder.iv_AddedIntoCart.visibility = View.GONE
                    holder.iv_AddToCart.visibility = View.VISIBLE


                    holder.iv_AddToCart.setOnClickListener {
                        Toast.makeText(context, "Button 4 clicked", Toast.LENGTH_LONG).show()
                        val qty = holder.count.text.toString().trim()

                        brandIdArr[position] = result.get(i).id


                        if (TextUtils.isEmpty(qty) || qty.equals("0.0") || qty == "0") {

                            Snackbar.make(
                                holder.itemView,
                                "Please enter or select the quantity of product",
                                Snackbar.LENGTH_SHORT
                            ).show()

                            return@setOnClickListener
                        }



                        Log.e("QtyOfItemAdded", "InCart" + qty)


                        // cart.addToCart(holder.iv_AddToCart,holder.iv_AddedIntoCart,position,qtyNumber)

                        if (Utility.getInstance().checkInternetConnection(context)) {


                            if (quotationId == "") {

                                val rbUserId =
                                    userPref.user.id!!.toRequestBody("text/plain".toMediaTypeOrNull())
                                val rbMenuId =
                                    menuList.get(position).id.toRequestBody("text/plain".toMediaTypeOrNull())
                                val rbQty =
                                    qty.toRequestBody("text/plain".toMediaTypeOrNull())
                                val rbVarient =
                                    "".toRequestBody("text/plain".toMediaTypeOrNull())
                                val rbBrandId =
                                    brandIdArr[position]!!.toRequestBody("text/plain".toMediaTypeOrNull())
                                val rbToken =
                                    userPref.user.token!!.toRequestBody("text/plain".toMediaTypeOrNull())


                                apiService.addCart(
                                    rbUserId,
                                    rbMenuId,
                                    rbQty, rbVarient,
                                    rbBrandId,
                                    rbToken
                                )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnSubscribe { showProgressDialog() }
                                    .doOnCompleted { hideProgressDialog() }
                                    .subscribe({

                                        if (it.responseCode.equals("200")) {

                                            wishList.refreshCartCountList()

                                            holder.iv_AddToCart.visibility = View.GONE
                                            holder.iv_AddedIntoCart.visibility = View.VISIBLE
                                            //Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()

                                            // menuList.get(position).is_cart = true
                                            // notifyDataSetChanged()

                                            brandIdArr[position] = result.get(i).id


                                            holder.iv_AddedIntoCart.setOnClickListener {
                                                //    Toast.makeText(context, "hello2", Toast.LENGTH_SHORT) .show()
                                                brandIdArr[position] = result.get(i).id


                                                removeItemFromCart(
                                                    holder.iv_AddToCart,
                                                    holder.iv_AddedIntoCart,
                                                    menuList.get(position),
                                                    brandIdArr[position].toString(),
                                                    holder
                                                )

                                            }


                                            holder.add1.setOnClickListener {

                                                Toast.makeText(
                                                    context,
                                                    "You can update the quantity in My Cart Page",
                                                    Toast.LENGTH_SHORT
                                                ).show()


                                            }
                                            holder.add1.setOnClickListener {

                                                Toast.makeText(
                                                    context,
                                                    "You can update the quantity in My Cart Page",
                                                    Toast.LENGTH_SHORT
                                                ).show()


                                            }

                                            holder.minus.setOnClickListener {

                                                Toast.makeText(
                                                    context,
                                                    "You can update the quantity in My Cart Page",
                                                    Toast.LENGTH_SHORT
                                                ).show()


                                            }
                                            holder.minus.setOnClickListener {

                                                Toast.makeText(
                                                    context,
                                                    "You can update the quantity in My Cart Page",
                                                    Toast.LENGTH_SHORT
                                                ).show()


                                            }

                                        } else if (it.responseCode == "403") {
                                            utils.openLogoutDialog(context, userPref)
                                        } else {

                                            hideProgressDialog()
                                            Utility.simpleAlert(
                                                context,
                                                context.getString(R.string.info_dialog_title),
                                                it.responseMessage
                                            )
                                        }

                                    }, {

                                        try {

                                            if (it is ConnectException) {
                                                hideProgressDialog()
                                                Utility.simpleAlert(
                                                    context,
                                                    context.getString(R.string.error),
                                                    context.getString(R.string.check_network_connection)
                                                )
                                            } else {
                                                hideProgressDialog()
                                                it.printStackTrace()
                                                Utility.simpleAlert(
                                                    context,
                                                    "",
                                                    context.getString(R.string.something_went_wrong)
                                                )
                                            }

                                        } catch (ex: Exception) {
                                            Log.e(
                                                "",
                                                "Within Throwable Exception::" + it.message
                                            )
                                        }

                                    })
                            } else {
                                updateQuotation(
                                    menuList.get(position).id,
                                    qty,
                                    brandIdArr[position]!!
                                )
                            }

                        } else {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context, context.getString(R.string.error),
                                context.getString(R.string.check_network_connection)
                            )
                        }


                    }


                    holder.add1.setOnClickListener {

                        val qtyStr: Int = String.format(
                            holder.count.text.toString()
                        ).toInt()
                        var qtyNumber = qtyStr

                        qtyNumber = qtyNumber + 1
                        qtyNumber = String.format(qtyNumber.toString()).toInt()
                        holder.count.setText("" + qtyNumber)
                        val pos: Int = holder.count.text.length
                        holder.count.setSelection(pos)
                        //    Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show()


                    }
                    holder.add1.setOnClickListener {


                        val qtyStr: Int = String.format(
                            holder.count.text.toString()
                        ).toInt()
                        var qtyNumber = qtyStr

                        qtyNumber = qtyNumber + 1
                        qtyNumber = String.format(qtyNumber.toString()).toInt()
                        holder.count.setText("" + qtyNumber)
                        val pos: Int = holder.count.text.length
                        holder.count.setSelection(pos)
                        //  qty.qtyOfItem(qtyNumber)


                        //    Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show()


                    }

                    holder.minus.setOnClickListener {
                        val qtyStr: Int = String.format(
                            holder.count.text.toString()
                        ).toInt()
                        var qtyNumber = qtyStr
                        if (qtyNumber > 0) {
                            qtyNumber = qtyNumber - 1
                            qtyNumber = String.format(qtyNumber.toString()).toInt()
                            holder.count.setText("" + qtyNumber)
                            val pos: Int = holder.count.text.length
                            holder.count.setSelection(pos)
                            //  qty.qtyOfItem(qtyNumber)
                        }

                        //  Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show()


                    }
                    holder.minus.setOnClickListener {


                        val qtyStr: Int = String.format(
                            holder.count.text.toString()
                        ).toInt()
                        var qtyNumber = qtyStr
                        if (qtyNumber > 0) {
                            qtyNumber = qtyNumber - 1
                            qtyNumber = String.format(qtyNumber.toString()).toInt()
                            holder.count.setText("" + qtyNumber)
                            val pos: Int = holder.count.text.length
                            holder.count.setSelection(pos)
                            //  qty.qtyOfItem(qtyNumber)
                        }

                        //  Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show()


                    }

                } else if (it.responseCode == "403") {
                    utils.openLogoutDialog(context, userPref)
                }
                else {

                    hideProgressDialog()
                    Utility.simpleAlert(
                        context,
                        context.getString(R.string.info_dialog_title),
                        it.responseMessage
                    )
                }

            }, {

                try {

                    if (it is ConnectException) {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            context, context.getString(R.string.error),
                            context.getString(R.string.check_network_connection)
                        )
                    } else {
                        hideProgressDialog()
                        it.printStackTrace()
                        Utility.simpleAlert(
                            context,
                            "",
                            context.getString(R.string.something_went_wrong)
                        )
                    }

                } catch (ex: Exception) {
                    Log.e("", "Within Throwable Exception::" + it.message)
                }

            })
    }


    private fun addProductVarientInSpinner(
        list: List<String>,
        spinner: Spinner,
        menuVarient: MutableList<MenuVerient>,
        position: Int,
    ) {

        val stateAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            context,
            R.layout.simple_spinner_item_custom, list
        )
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = stateAdapter


        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                i: Int,
                l: Long
            ) {

                if (i != 0) {
                    varientIdArr[position] = menuVarient.get(i - 1).id
                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

    }


    interface WishList {

        fun addToWishList(
            iv_RemoveFromWishlist: ImageView,
            iv_AddToWishlist: ImageView,
            menuId: String,
        )

        fun removeFromWishList(
            iv_AddToWishlist: ImageView,
            iv_RemoveFromWishlist: ImageView,
            menuId: String,
        )

        fun refreshCartCountList()

    }

    interface AddToCart {

        fun addCartList(
            iv_RemoveFromWishlist: ImageView,
            iv_AddToWishlist: ImageView,
            menuId: String,
        )


    }


    private fun addIntoCart(
        ivAddtocart: ImageView, ivAddedintocart: ImageView, menu: MenuList,
        qty: String, variendId: String, brandId: String, holder: RecyclerViewItem,
    ) {

        if (Utility.getInstance().checkInternetConnection(context)) {

            val rbUserId = userPref.user.id!!.toRequestBody("text/plain".toMediaTypeOrNull())
            val rbMenuId = menu.id.toRequestBody("text/plain".toMediaTypeOrNull())
            val rbQty = qty.toRequestBody("text/plain".toMediaTypeOrNull())
            val rbVarient = "".toRequestBody("text/plain".toMediaTypeOrNull())
            val rbBrandId = brandId.toRequestBody("text/plain".toMediaTypeOrNull())
            val rbToken =
                userPref.user.token!!.toRequestBody("text/plain".toMediaTypeOrNull())

            apiService.addCart(
                rbUserId,
                rbMenuId,
                rbQty, rbVarient, rbBrandId,
                rbToken
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({

                    if (it.responseCode.equals("200")) {

                        wishList.refreshCartCountList()

                        ivAddtocart.visibility = View.GONE
                        ivAddedintocart.visibility = View.VISIBLE
                        Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()

                        holder.count.isFocusable = false
                        holder.count.isEnabled = true
                        holder.count.setOnClickListener {
                            Toast.makeText(
                                context,
                                "You can update the quantity in My Cart Page",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        holder.add1.setOnClickListener(null)
                        holder.minus.setOnClickListener(null)
                        holder.minus.setOnClickListener(null)

                        holder.add1.setOnClickListener {
                            Toast.makeText(
                                context,
                                "You can update the quantity in My Cart Page",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        holder.add1.setOnClickListener {
                            Toast.makeText(
                                context,
                                "You can update the quantity in My Cart Page",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        holder.minus.setOnClickListener {
                            Toast.makeText(
                                context,
                                "You can update the quantity in My Cart Page",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        holder.minus.setOnClickListener {
                            Toast.makeText(
                                context,
                                "You can update the quantity in My Cart Page",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(context, userPref)
                    } else {

                        hideProgressDialog()
                        Utility.simpleAlert(
                            context,
                            context.getString(R.string.info_dialog_title),
                            it.responseMessage
                        )
                    }

                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context, context.getString(R.string.error),
                                context.getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(
                                context,
                                "",
                                context.getString(R.string.something_went_wrong)
                            )
                        }
                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })

        } else {
            hideProgressDialog()
            Utility.simpleAlert(
                context, context.getString(R.string.error),
                context.getString(R.string.check_network_connection)
            )
        }
    }

    private fun removeItemFromCart(
        ivAddtocart: ImageView,
        ivAddedintocart: ImageView,
        menu: MenuList,
        brandId: String,
        holder: RecyclerViewItem,
    ) {

        if (Utility.getInstance().checkInternetConnection(context)) {

            apiService.removeFromCart(
                userPref.user.id!!,
                menu.id,
                userPref.user.token!!, brandId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({

                    if (it.responseCode.equals("200")) {

                        wishList.refreshCartCountList()

                        ivAddedintocart.visibility = View.GONE
                        ivAddtocart.visibility = View.VISIBLE

                        holder.count.isFocusableInTouchMode = true
                        holder.count.isFocusable = true
                        holder.count.isEnabled = true

                        holder.count.setOnClickListener(null)
                        holder.add1.setOnClickListener(null)
                        holder.minus.setOnClickListener(null)

                        holder.add1.setOnClickListener {

                            val qtyStr: Int = String.format(
                                holder.count.text.toString()
                            ).toInt()
                            var qtyNumber = qtyStr

                            qtyNumber = qtyNumber + 1
                            qtyNumber = String.format(qtyNumber.toString()).toInt()
                            holder.count.setText("" + qtyNumber)
                            val pos: Int = holder.count.text.length
                            holder.count.setSelection(pos)
                        }
                        holder.add1.setOnClickListener {
                            val qtyStr: Int = String.format(
                                holder.count.text.toString()
                            ).toInt()
                            var qtyNumber = qtyStr

                            qtyNumber = qtyNumber + 1
                            qtyNumber = String.format(qtyNumber.toString()).toInt()
                            holder.count.setText("" + qtyNumber)
                            val pos: Int = holder.count.text.length
                            holder.count.setSelection(pos)
                            //  qty.qtyOfItem(qtyNumber)

                        }
                        holder.minus.setOnClickListener {

                            val qtyStr: Int = String.format(
                                holder.count.text.toString()
                            ).toInt()
                            var qtyNumber = qtyStr
                            if (qtyNumber > 0) {
                                qtyNumber = qtyNumber - 1
                                qtyNumber = String.format(qtyNumber.toString()).toInt()
                                holder.count.setText("" + qtyNumber)
                                val pos: Int = holder.count.text.length
                                holder.count.setSelection(pos)
                                //  qty.qtyOfItem(qtyNumber)
                            }
                        }
                        holder.minus.setOnClickListener {


                            val qtyStr: Int = String.format(
                                holder.count.text.toString()
                            ).toInt()
                            var qtyNumber = qtyStr
                            if (qtyNumber > 0) {
                                qtyNumber = qtyNumber - 1
                                qtyNumber = String.format(qtyNumber.toString()).toInt()
                                holder.count.setText("" + qtyNumber)
                                val pos: Int = holder.count.text.length
                                holder.count.setSelection(pos)
                                //  qty.qtyOfItem(qtyNumber)
                            }

                        }

                    } else
                        if (it.responseCode == "403") {
                            utils.openLogoutDialog(context, userPref)
                        } else {

                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                context.getString(R.string.info_dialog_title),
                                it.responseMessage
                            )
                        }

                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context, context.getString(R.string.error),
                                context.getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(
                                context,
                                "",
                                context.getString(R.string.something_went_wrong)
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
                context.getString(R.string.error),
                context.getString(R.string.check_network_connection)
            )
        }


    }

    protected fun showProgressDialog() {


        if (dialog == null)
            dialog = Dialog(context)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCancelable(false)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        if (dialog != null && !dialog!!.isShowing)
            dialog!!.show()

    }

    protected fun hideProgressDialog() {


        if (dialog != null && dialog!!.isShowing)
            dialog!!.dismiss()

    }

    private fun updateProductQty(qty: String, itemId: String, menuId: String, brandId: String) {

        if (Utility.getInstance().checkInternetConnection(context)) {

            apiService.updateCartItem(
                qty,
                itemId,
                menuId,
                userPref.user.token!!, brandId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({

                    if (it.responseCode.equals("200")) {

                        //Toast.makeText(context,it.responseMessage, Toast.LENGTH_SHORT).show()
                        //getQuotationProductList()

                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(context, userPref)
                    } else {

                        hideProgressDialog()
                        Utility.simpleAlert(
                            context,
                            context.getString(R.string.info_dialog_title),
                            it.responseMessage
                        )
                    }

                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context, context.getString(R.string.error),
                                context.getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(
                                context,
                                "",
                                context.getString(R.string.something_went_wrong)
                            )
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })

        } else {
            hideProgressDialog()
            Utility.simpleAlert(
                context, context.getString(R.string.error),
                context.getString(R.string.check_network_connection)
            )
        }
    }

    private fun updateQuotation(menuId: String, qty: String, brandId: String) {
        if (Utility.getInstance().checkInternetConnection(context)) {
            if (menuId != null) {
                apiService.updateUserQuotation(
                    userPref.user.id!!,
                    menuId,
                    qty,
                    userPref.user.token!!,
                    brandId,
                    quotationId
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgressDialog() }
                    .doOnCompleted { hideProgressDialog() }
                    .subscribe({

                        if (it.responseCode.equals("200")) {
                            Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT)
                                .show()
                        } else if (it.responseCode == "403") {
                            utils.openLogoutDialog(context, userPref)
                        } else {

                            hideProgressDialog()

                        }

                    }, {

                        try {
                            if (it is ConnectException) {
                                hideProgressDialog()
                                Utility.simpleAlert(
                                    context,
                                    context.getString(R.string.error),
                                    context.getString(R.string.check_network_connection)
                                )
                            } else {
                                hideProgressDialog()
                                it.printStackTrace()
                                Utility.simpleAlert(
                                    context,
                                    "",
                                    context.getString(R.string.something_went_wrong)
                                )
                            }
                        } catch (ex: Exception) {
                            Log.e("", "Within Throwable Exception::" + it.message)
                        }
                    })
            }

        } else {
            hideProgressDialog()
            Utility.simpleAlert(
                context,
                context.getString(R.string.error),
                context.getString(R.string.check_network_connection)
            )
        }
    }


}