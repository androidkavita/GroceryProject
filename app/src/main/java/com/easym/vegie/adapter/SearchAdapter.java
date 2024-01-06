package com.easym.vegie.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easym.vegie.R;
import com.easym.vegie.Utils.RecyclerItemClickListener;
import com.easym.vegie.Utils.Utility;
import com.easym.vegie.Utils.Utils;
import com.easym.vegie.api.ApiService1;
import com.easym.vegie.model.productbrand.ProductBrandResult;
import com.easym.vegie.model.searchproduct.MenuVerient;
import com.easym.vegie.model.searchproduct.SearchProductResult;
import com.easym.vegie.sharePref.UserPref;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {


    Context context;
    List<SearchProductResult> list;
    ApiService1 apiService;
    UserPref userPref;
    Dialog dialog;
    WishList wishList;
    String[] brandIdArr;
    String[] varientIdArr;
    Utils utils;


    public SearchAdapter(Context mContext, List<SearchProductResult> mResultList,
                         ApiService1 mApiService, UserPref mUserPref, WishList mWishList,Utils utils) {

        this.context = mContext;
        this.list = mResultList;
        this.apiService = mApiService;
        this.userPref = mUserPref;
        this.wishList = mWishList;

        brandIdArr = new String[list.size()];
        varientIdArr = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            brandIdArr[i] = "";
            varientIdArr[i] = "";
        }

        this.utils = utils;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {


        Picasso.get()
                .load(list.get(position).getImage())
                .placeholder(R.drawable.image_loading)
                .into(holder.img_product);

        if(list.get(position).isHaveBrand()){
            holder.moreBrandContainer.setVisibility(View.VISIBLE);
        }else{
            holder.moreBrandContainer.setVisibility(View.GONE);
        }

        if (!list.get(position).getOther_name().equals("false")) {
            holder.txt_product_name.setText(list.get(position).getMenuName() + "\n" + list.get(position).getOther_name());
        } else {
            holder.txt_product_name.setText(list.get(position).getMenuName());
        }

        holder.txt_price.setText(list.get(position).getPrice() + " ( " + list.get(position).getUnit() + " ) ");

        String discount = list.get(position).getDiscount();
        if (!discount.equals("") && !discount.equals("0.00")) {
            holder.tv_Discount.setText(list.get(position).getDiscount() + " Off");
            holder.tv_Discount.setVisibility(View.VISIBLE);
        }

        holder.moreBrandContainer.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {


                if (Utility.getInstance().checkInternetConnection(context)) {

                    apiService.getProductBrand(
                            list.get(position).getId(),
                            userPref.getUser().getToken(),
                            userPref.getUserPreferLanguageCode())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(new Action0() {
                                @Override
                                public void call() {
                                    showProgressDialog();
                                }
                            })
                            .doOnCompleted(new Action0() {
                                @Override
                                public void call() {
                                    hideProgressDialog();
                                }
                            })
                            .subscribe(response -> {

                                if (response.getResponseCode().equals("200")) {

                                    List<ProductBrandResult> brandResults = response.getData().getProductBrandResult().getResult();

                                    showPopUp(holder.moreBrandContainer, brandResults, position, list.get(position).getId(), holder);

                                } else if(response.getResponseCode().equals("403")){

                                    utils.openLogoutDialog(context,userPref);

                                } else {

                                    hideProgressDialog();
                                    Toast.makeText(context, "" + response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                                /*Utility.simpleAlert(context, context.getString(R.string.error),
                                        response.getResponseMessage());*/
                                }

                            }, e -> {

                                try {

                                    if (e instanceof ConnectException) {
                                        hideProgressDialog();
                                        Utility.simpleAlert(context, context.getString(R.string.error),
                                                context.getString(R.string.check_network_connection));
                                    } else {
                                        hideProgressDialog();
                                        e.printStackTrace();
                                        Utility.simpleAlert(context, "", context.getString(R.string.something_went_wrong));
                                    }

                                }catch (Exception ex){
                                    Log.e( "","Within Throwable Exception::" + e.getMessage());
                                }

                            });

                } else {
                    hideProgressDialog();
                    Utility.simpleAlert(context, context.getString(R.string.error),
                            context.getString(R.string.check_network_connection));
                }
            }
        });

        if (list.get(position).getIsWishlist()) {

            holder.iv_AddToWishlist.setVisibility(View.GONE);
            holder.iv_RemoveFromWishlist.setVisibility(View.VISIBLE);

            holder.iv_RemoveFromWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    wishList.removeFromWishList(
                            holder.iv_AddToWishlist,
                            holder.iv_RemoveFromWishlist,
                            list.get(position).getId());

                }
            });

        } else {

            holder.iv_RemoveFromWishlist.setVisibility(View.GONE);
            holder.iv_AddToWishlist.setVisibility(View.VISIBLE);

            holder.iv_AddToWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    wishList.addToWishList(
                            holder.iv_RemoveFromWishlist,
                            holder.iv_AddToWishlist,
                            list.get(position).getId());
                }
            });
        }

        if (list.get(position).getIsCart()) {

            holder.tv_productcount.setText(list.get(position).getCart_qty());

            holder.tv_productcount.setFocusable(false);
            holder.tv_productcount.setEnabled(true);
            holder.tv_productcount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show();
                }
            });


            holder.iv_AddToCart.setVisibility(View.GONE);
            holder.iv_AddedIntoCart.setVisibility(View.VISIBLE);
            holder.iv_AddedIntoCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Utility.getInstance().checkInternetConnection(context)) {

                        apiService.removeFromCart(
                                userPref.getUser().getId(),
                                list.get(position).getId(),
                                userPref.getUser().getToken(),
                                "")
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe(new Action0() {
                                    @Override
                                    public void call() {
                                        showProgressDialog();
                                    }
                                })
                                .doOnCompleted(new Action0() {
                                    @Override
                                    public void call() {
                                        hideProgressDialog();
                                    }
                                })
                                .subscribe(response -> {

                                    if (response.getResponseCode().equals("200")) {

                                        //list.get(position).setIsCart(false);
                                        // notifyDataSetChanged();

                                        holder.tv_productcount.setFocusableInTouchMode(true);
                                        holder.tv_productcount.setFocusable(true);
                                        holder.tv_productcount.setEnabled(true);

                                        holder.iv_AddedIntoCart.setVisibility(View.GONE);
                                        holder.iv_AddToCart.setVisibility(View.VISIBLE);

                                        holder.iv_AddToCart.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                String qty = holder.tv_productcount.getText().toString().trim();
                                                Log.e("QtyOfItemAdded", "InCart" + qty);

                                                if (qty.equals("0.0")) {

                                                    Snackbar.make(holder.itemView, "Please select quantity of product",
                                                            Snackbar.LENGTH_SHORT).show();

                                                    return;
                                                }

                                                addIntoCart(holder.iv_AddToCart, holder.iv_AddedIntoCart,
                                                        list.get(position), qty,
                                                        varientIdArr[position], brandIdArr[position],holder);
                                            }
                                        });


                                    }
                                    else if(response.getResponseCode().equals("403")){

                                        utils.openLogoutDialog(context,userPref);

                                    }else {

                                        hideProgressDialog();
                                        Utility.simpleAlert(context, context.getString(R.string.info_dialog_title),
                                                response.getResponseMessage());
                                    }

                                }, e -> {

                                    try {

                                        if (e instanceof ConnectException) {
                                            hideProgressDialog();
                                            Utility.simpleAlert(context, context.getString(R.string.error),
                                                    context.getString(R.string.check_network_connection));
                                        } else {
                                            hideProgressDialog();
                                            e.printStackTrace();
                                            Utility.simpleAlert(context, "", context.getString(R.string.something_went_wrong));
                                        }
                                    }catch (Exception ex){
                                        Log.e( "","Within Throwable Exception::" + e.getMessage());
                                    }

                                });

                    } else {

                        hideProgressDialog();
                        Utility.simpleAlert(context, context.getString(R.string.error),
                                context.getString(R.string.check_network_connection));
                    }
                }
            });



        } else {

            holder.tv_productcount.setFocusableInTouchMode(true);
            holder.tv_productcount.setFocusable(true);
            holder.tv_productcount.setEnabled(true);

            holder.iv_AddedIntoCart.setVisibility(View.GONE);
            holder.iv_AddToCart.setVisibility(View.VISIBLE);

            holder.iv_AddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String qty = holder.tv_productcount.getText().toString().trim();

                    if (qty.equals("0.0")) {

                        Snackbar.make(holder.itemView, "Please select quantity of product",
                                Snackbar.LENGTH_SHORT).show();

                        return;
                    }

                    if (Utility.getInstance().checkInternetConnection(context)) {

                        Log.e("Selected Brand Id", "" + brandIdArr[position]);
                        Log.e("Selected Varient Id", "" + varientIdArr[position]);

                        RequestBody rbUserId = RequestBody.create(MediaType.parse("text/plain"), userPref.getUser().getId());
                        RequestBody rbMenuId = RequestBody.create(MediaType.parse("text/plain"), list.get(position).getId());
                        RequestBody rbQty = RequestBody.create(MediaType.parse("text/plain"), qty);
                        RequestBody rbVarient = RequestBody.create(MediaType.parse("text/plain"), varientIdArr[position]);
                        RequestBody rbBrandId = RequestBody.create(MediaType.parse("text/plain"), brandIdArr[position]);
                        RequestBody rbToken = RequestBody.create(MediaType.parse("text/plain"), userPref.getUser().getToken());


                        apiService.addCart(
                                rbUserId,
                                rbMenuId,
                                rbQty,
                                rbVarient,
                                rbBrandId,
                                rbToken)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe(new Action0() {
                                    @Override
                                    public void call() {
                                        showProgressDialog();
                                    }
                                })
                                .doOnCompleted(new Action0() {
                                    @Override
                                    public void call() {
                                        hideProgressDialog();
                                    }
                                })
                                .subscribe(response -> {

                                    if (response.getResponseCode().equals("200")) {

                                        // list.get(position).setIsCart(true);
                                        // notifyDataSetChanged();

                                        holder.tv_productcount.setFocusable(false);
                                        holder.tv_productcount.setEnabled(true);
                                        holder.tv_productcount.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        holder.iv_AddToCart.setVisibility(View.GONE);
                                        holder.iv_AddedIntoCart.setVisibility(View.VISIBLE);
                                        //Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()

                                        // menuList.get(position).is_cart = true
                                        // notifyDataSetChanged()

                                        holder.iv_AddedIntoCart.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                removeItemFromCart(holder.iv_AddToCart,
                                                        holder.iv_AddedIntoCart,
                                                        list.get(position),holder,"");
                                            }
                                        });


                                    }  else if(response.getResponseCode().equals("403")){

                                        utils.openLogoutDialog(context,userPref);

                                    }else {

                                        hideProgressDialog();
                                        Utility.simpleAlert(context, context.getString(R.string.info_dialog_title),
                                                response.getResponseMessage());
                                    }

                                }, e -> {

                                    try {

                                        if (e instanceof ConnectException) {
                                            hideProgressDialog();
                                            Utility.simpleAlert(context, context.getString(R.string.error),
                                                    context.getString(R.string.check_network_connection));
                                        } else {
                                            hideProgressDialog();
                                            e.printStackTrace();
                                            Utility.simpleAlert(context, "", context.getString(R.string.something_went_wrong));
                                        }

                                    }catch (Exception ex){
                                        Log.e( "","Within Throwable Exception::" + e.getMessage());
                                    }
                                });

                    } else {

                        hideProgressDialog();
                        Utility.simpleAlert(context, context.getString(R.string.error),
                                context.getString(R.string.check_network_connection));
                    }

                }
            });
        }

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (list.get(position).getIsCart()) {

                    Double qtyStr = Double.valueOf(String.format("%.1f", Double.valueOf(holder.tv_productcount.getText().toString())));
                    Float qtyNumber = Float.valueOf(qtyStr.toString());
                    qtyNumber = qtyNumber + 0.1f;
                    qtyNumber = Float.valueOf(String.format("%.1f", Float.valueOf(qtyNumber)));
                    holder.tv_productcount.setText("" + qtyNumber);
                    int pos = holder.tv_productcount.getText().length();
                    holder.tv_productcount.setSelection(pos);

                    String qty = holder.tv_productcount.getText().toString().trim();

                    updateProductQty(qty, list.get(position).getCart_id(), list.get(position).getId(), "");


                } else {

                    Double qtyStr = Double.parseDouble(String.format("%.1f", Double.parseDouble(holder.tv_productcount.getText().toString())));

                    // val qtyStr = holder.tv_productcount.text.toString().trim()
                    String qStr = qtyStr.toString();
                    Float qtyNumber = Float.parseFloat(qStr);
                    qtyNumber = qtyNumber + 0.1f;

                    qtyNumber = Float.parseFloat(String.format("%.1f", qtyNumber));


                    holder.tv_productcount.setText("" + qtyNumber);
                }
            }
        });

        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (list.get(position).getIsCart()) {

                    Double qtyStr = Double.valueOf(String.format("%.1f", Double.valueOf(holder.tv_productcount.getText().toString())));
                    Float qtyNumber = Float.valueOf(qtyStr.toString());
                    if (qtyNumber > 0.2) {
                        qtyNumber = qtyNumber - 0.1f;
                        qtyNumber = Float.valueOf(String.format("%.1f", Float.valueOf(qtyNumber)));
                        holder.tv_productcount.setText("" + qtyNumber);
                        int pos = holder.tv_productcount.getText().length();
                        holder.tv_productcount.setSelection(pos);
                        String qty = holder.tv_productcount.getText().toString().trim();
                        updateProductQty(qty, list.get(position).getCart_id(), list.get(position).getId(), "");


                    } else {

                        Toast.makeText(context, context.getString(R.string.minimum_quantity_is_required), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Double qtyStr = Double.parseDouble(String.format("%.1f", Double.parseDouble(holder.tv_productcount.getText().toString())));

                    //val qtyStr = holder.tv_productcount.text.toString().trim

                    String qStr = qtyStr.toString();
                    Float qtyNumber = Float.parseFloat(qStr);

                    if (qtyNumber > 0.0) {

                        qtyNumber = qtyNumber - 0.1f;

                        qtyNumber = Float.parseFloat(String.format("%.1f", qtyNumber));

                        holder.tv_productcount.setText("" + qtyNumber);
                        //  qty.qtyOfItem(qtyNumber)
                    }
                }

            }
        });

        //set The Menu Varient in the spinner
        List<MenuVerient> menuVarient = list.get(position).getMenuVerient();

        if (menuVarient.size() != 0) {

            List<String> list = new ArrayList();

            // var list = productVarientList.toMutableList()

            list.add("Select " + menuVarient.get(0).getUnit());

            for (int i = 0; i < menuVarient.size(); i++) {
                list.add(menuVarient.get(i).getQuantity());
            }

            addProductVarientInSpinner(list, holder.productVarient, menuVarient, position);

        } else {
            holder.rl_Varient.setVisibility(View.INVISIBLE);
        }


    }

    private void addProductVarientInSpinner(List<String> list,
                                            Spinner spinner,
                                            List<MenuVerient> menuVarient,
                                            int position) {

        ArrayAdapter stateAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item_custom, list);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(stateAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {

                if (i != 0) {

                    varientIdArr[position] = menuVarient.get(i - 1).getId();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void showPopUp(View view, List<ProductBrandResult> result, int position, String menuId, MyViewHolder holder) {

        View popupView = LayoutInflater.from(context).inflate(R.layout.brand_list_custom_alert_dialog, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        RecyclerView recyclerView = popupView.findViewById(R.id.rv_Brand);

        LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false);
        BrandRVAdapter brandRVAdapter = new BrandRVAdapter(context, result);

        recyclerView.setAdapter(brandRVAdapter);
        recyclerView.setLayoutManager(lm);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, int i) {

                Picasso.get()
                        .load(result.get(i).getBrand_image())
                        .placeholder(R.drawable.image_loading)
                        .into(holder.img_product);

                holder.txt_price.setText(result.get(i).getPrice() +" ( "+ result.get(i).getUnit() +" )");

                if (!result.get(i).getOther_name().equals("false")) {
                    holder.txt_product_name.setText(result.get(i).getBrandName() + "\n"
                            + result.get(i).getOther_name());
                } else {
                    holder.txt_product_name.setText(result.get(i).getBrandName());
                }

                checkSelectedBrandProductIsAddedInCart(menuId,
                        result.get(i).getId(),
                        holder, i, position, result,
                        popupWindow);
            }
        }));

        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(view, 0, 0);

    }

    private void checkSelectedBrandProductIsAddedInCart(String menuId,
                                                        String brandId,
                                                        MyViewHolder holder,
                                                        int i,
                                                        int position,
                                                        List<ProductBrandResult> result,
                                                        PopupWindow popupWindow) {

        apiService.checkSameBrandProduct(
                userPref.getUser().getToken(),
                userPref.getUser().getId(),
                menuId,
                brandId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showProgressDialog();
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        hideProgressDialog();
                    }
                })
                .subscribe(response -> {

                    if (response.getResponseCode().equals("200")) {

                        holder.iv_AddedIntoCart.setOnClickListener(null);

                        hideProgressDialog();
                        Toast.makeText(context, "" + response.getResponseMessage(), Toast.LENGTH_SHORT).show();

                        holder.tv_productcount.setText(response.getData().getQty());

                        String cart_id = response.getData().getId();

                        popupWindow.dismiss();

                        holder.iv_AddToCart.setVisibility(View.GONE);
                        holder.iv_AddedIntoCart.setVisibility(View.VISIBLE);

                        holder.tv_productcount.setFocusable(false);
                        holder.tv_productcount.setEnabled(true);
                        holder.tv_productcount.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show();
                            }
                        });


                        holder.iv_AddedIntoCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (Utility.getInstance().checkInternetConnection(context)) {

                                    apiService.removeFromCart(
                                            userPref.getUser().getId(),
                                            list.get(position).getId(),
                                            userPref.getUser().getToken(),
                                            brandId)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnSubscribe(new Action0() {
                                                @Override
                                                public void call() {
                                                    showProgressDialog();
                                                }
                                            })
                                            .doOnCompleted(new Action0() {
                                                @Override
                                                public void call() {
                                                    hideProgressDialog();
                                                }
                                            })
                                            .subscribe(response -> {

                                                if (response.getResponseCode().equals("200")) {

                                                    //list.get(position).setIsCart(false);
                                                    // notifyDataSetChanged();

                                                    holder.iv_AddedIntoCart.setVisibility(View.GONE);
                                                    holder.iv_AddToCart.setVisibility(View.VISIBLE);

                                                    holder.tv_productcount.setFocusableInTouchMode(true);
                                                    holder.tv_productcount.setFocusable(true);
                                                    holder.tv_productcount.setEnabled(true);

                                                    holder.iv_AddToCart.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            String qty = holder.tv_productcount.getText().toString().trim();
                                                            Log.e("QtyOfItemAdded", "InCart" + qty);

                                                            if (qty.equals("0.0")) {

                                                                Snackbar.make(holder.itemView, "Please select quantity of product",
                                                                        Snackbar.LENGTH_SHORT).show();

                                                                return;
                                                            }

                                                            addIntoCart(holder.iv_AddToCart, holder.iv_AddedIntoCart,
                                                                    list.get(position), qty,
                                                                    varientIdArr[position], brandId,holder);
                                                        }
                                                    });


                                                }
                                                else if(response.getResponseCode().equals("403")){

                                                    utils.openLogoutDialog(context,userPref);

                                                }else {

                                                    hideProgressDialog();
                                                    Utility.simpleAlert(context, context.getString(R.string.info_dialog_title),
                                                            response.getResponseMessage());
                                                }

                                            }, e -> {

                                                try {

                                                    if (e instanceof ConnectException) {
                                                        hideProgressDialog();
                                                        Utility.simpleAlert(context, context.getString(R.string.error),
                                                                context.getString(R.string.check_network_connection));
                                                    } else {
                                                        hideProgressDialog();
                                                        e.printStackTrace();
                                                        Utility.simpleAlert(context, "", context.getString(R.string.something_went_wrong));
                                                    }
                                                } catch (Exception ex){
                                                    Log.e( "","Within Throwable Exception::" + e.getMessage());
                                                }

                                            });

                                } else {

                                    hideProgressDialog();
                                    Utility.simpleAlert(context, context.getString(R.string.error),
                                            context.getString(R.string.check_network_connection));
                                }
                            }
                        });


                        holder.btn_add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Double qtyStr = Double.valueOf(String.format("%.1f", Double.valueOf(holder.tv_productcount.getText().toString())));
                                Float qtyNumber = Float.valueOf(qtyStr.toString());
                                qtyNumber = qtyNumber + 0.1f;
                                qtyNumber = Float.valueOf(String.format("%.1f", Float.valueOf(qtyNumber)));
                                holder.tv_productcount.setText("" + qtyNumber);
                                int pos = holder.tv_productcount.getText().length();
                                holder.tv_productcount.setSelection(pos);

                                String qty = holder.tv_productcount.getText().toString().trim();

                                updateProductQty(qty, cart_id, list.get(position).getId(), brandId);

                            }
                        });


                        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Double qtyStr = Double.valueOf(String.format("%.1f", Double.valueOf(holder.tv_productcount.getText().toString())));
                                Float qtyNumber = Float.valueOf(qtyStr.toString());
                                if (qtyNumber > 0.2) {
                                    qtyNumber = qtyNumber - 0.1f;
                                    qtyNumber = Float.valueOf(String.format("%.1f", Float.valueOf(qtyNumber)));
                                    holder.tv_productcount.setText("" + qtyNumber);
                                    int pos = holder.tv_productcount.getText().length();
                                    holder.tv_productcount.setSelection(pos);
                                    String qty = holder.tv_productcount.getText().toString().trim();
                                    updateProductQty(qty, cart_id, list.get(position).getId(), brandId);


                                } else {

                                    Toast.makeText(context, context.getString(R.string.minimum_quantity_is_required), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                    else if (response.getResponseCode().equals("400")) {

                        holder.iv_AddedIntoCart.setOnClickListener(null);
                        holder.tv_productcount.setText("1.0");
                        popupWindow.dismiss();

                        Log.e("brandId", "" + brandIdArr[position]);

                        holder.iv_AddedIntoCart.setVisibility(View.GONE);
                        holder.iv_AddToCart.setVisibility(View.VISIBLE);

                        holder.tv_productcount.setFocusableInTouchMode(true);
                        holder.tv_productcount.setFocusable(true);
                        holder.tv_productcount.setEnabled(true);


                        holder.iv_AddToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String qty = holder.tv_productcount.getText().toString().trim();

                                if (qty.equals("0.0")) {

                                    Snackbar.make(holder.itemView, "Please select quantity of product",
                                            Snackbar.LENGTH_SHORT).show();

                                    return;
                                }

                                if (Utility.getInstance().checkInternetConnection(context)) {

                                    Log.e("Selected Brand Id", "" + brandIdArr[position]);
                                    Log.e("Selected Varient Id", "" + varientIdArr[position]);

                                    RequestBody rbUserId = RequestBody.create(MediaType.parse("text/plain"), userPref.getUser().getId());
                                    RequestBody rbMenuId = RequestBody.create(MediaType.parse("text/plain"), list.get(position).getId());
                                    RequestBody rbQty = RequestBody.create(MediaType.parse("text/plain"), qty);
                                    RequestBody rbVarient = RequestBody.create(MediaType.parse("text/plain"), varientIdArr[position]);
                                    RequestBody rbBrandId = RequestBody.create(MediaType.parse("text/plain"), brandId);
                                    RequestBody rbToken = RequestBody.create(MediaType.parse("text/plain"), userPref.getUser().getToken());


                                    apiService.addCart(
                                            rbUserId,
                                            rbMenuId,
                                            rbQty,
                                            rbVarient,
                                            rbBrandId,
                                            rbToken)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnSubscribe(new Action0() {
                                                @Override
                                                public void call() {
                                                    showProgressDialog();
                                                }
                                            })
                                            .doOnCompleted(new Action0() {
                                                @Override
                                                public void call() {
                                                    hideProgressDialog();
                                                }
                                            })
                                            .subscribe(response -> {

                                                if (response.getResponseCode().equals("200")) {

                                                    // list.get(position).setIsCart(true);
                                                    // notifyDataSetChanged();

                                                    holder.iv_AddToCart.setVisibility(View.GONE);
                                                    holder.iv_AddedIntoCart.setVisibility(View.VISIBLE);
                                                    //Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()
                                                    // menuList.get(position).is_cart = true
                                                    // notifyDataSetChanged()
                                                    holder.tv_productcount.setFocusable(false);
                                                    holder.tv_productcount.setEnabled(true);
                                                    holder.tv_productcount.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                    holder.iv_AddedIntoCart.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            removeItemFromCart(holder.iv_AddToCart,
                                                                    holder.iv_AddedIntoCart,
                                                                    list.get(position),holder,brandId);
                                                        }
                                                    });


                                                }
                                                else if(response.getResponseCode().equals("403")){

                                                    utils.openLogoutDialog(context,userPref);

                                                }else {

                                                    hideProgressDialog();
                                                    Utility.simpleAlert(context, context.getString(R.string.info_dialog_title),
                                                            response.getResponseMessage());
                                                }

                                            }, e -> {

                                                try {

                                                    if (e instanceof ConnectException) {
                                                        hideProgressDialog();
                                                        Utility.simpleAlert(context, context.getString(R.string.error),
                                                                context.getString(R.string.check_network_connection));
                                                    } else {
                                                        hideProgressDialog();
                                                        e.printStackTrace();
                                                        Utility.simpleAlert(context, "", context.getString(R.string.something_went_wrong));
                                                    }
                                                }catch (Exception ex){
                                                    Log.e( "","Within Throwable Exception::" + e.getMessage());
                                                }

                                            });

                                } else {

                                    hideProgressDialog();
                                    Utility.simpleAlert(context, context.getString(R.string.error),
                                            context.getString(R.string.check_network_connection));
                                }

                            }
                        });


                        holder.btn_add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Double qtyStr = Double.parseDouble(String.format("%.1f", Double.parseDouble(holder.tv_productcount.getText().toString())));

                                // val qtyStr = holder.tv_productcount.text.toString().trim()
                                String qStr = qtyStr.toString();
                                Float qtyNumber = Float.parseFloat(qStr);
                                qtyNumber = qtyNumber + 0.1f;

                                qtyNumber = Float.parseFloat(String.format("%.1f", qtyNumber));


                                holder.tv_productcount.setText("" + qtyNumber);


                            }
                        });

                        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Double qtyStr = Double.parseDouble(String.format("%.1f", Double.parseDouble(holder.tv_productcount.getText().toString())));

                                //val qtyStr = holder.tv_productcount.text.toString().trim

                                String qStr = qtyStr.toString();
                                Float qtyNumber = Float.parseFloat(qStr);

                                if (qtyNumber > 0.0) {

                                    qtyNumber = qtyNumber - 0.1f;

                                    qtyNumber = Float.parseFloat(String.format("%.1f", qtyNumber));

                                    holder.tv_productcount.setText("" + qtyNumber);
                                    //  qty.qtyOfItem(qtyNumber)
                                }
                            }
                        });

                    }
                    else if(response.getResponseCode().equals("403")){

                        utils.openLogoutDialog(context,userPref);

                    }else {

                        hideProgressDialog();
                        Utility.simpleAlert(context, context.getString(R.string.info_dialog_title), response.getResponseMessage());
                    }

                }, e -> {

                    try {

                        if (e instanceof ConnectException) {
                            hideProgressDialog();
                            Utility.simpleAlert(context, context.getString(R.string.error),
                                    context.getString(R.string.check_network_connection));
                        } else {
                            hideProgressDialog();
                            e.printStackTrace();
                            Utility.simpleAlert(context, "", context.getString(R.string.something_went_wrong));
                        }

                    }catch (Exception ex){
                        Log.e( "","Within Throwable Exception::" + e.getMessage());
                    }

                });

    }

    private void updateProductQty(String qty, String itemId, String menuId, String brandId) {

        if (Utility.getInstance().checkInternetConnection(context)) {

            apiService.updateCartItem(
                    qty,
                    itemId,
                    menuId,
                    userPref.getUser().getToken(), brandId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            showProgressDialog();
                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            hideProgressDialog();
                        }
                    })
                    .subscribe(response -> {

                                if (response.getResponseCode().equals("200")) {

                                    //Toast.makeText(context,it.responseMessage, Toast.LENGTH_SHORT).show()
                                    //getQuotationProductList()

                                } else if(response.getResponseCode().equals("403")){

                                    utils.openLogoutDialog(context,userPref);

                                } else {

                                    hideProgressDialog();
                                    Utility.simpleAlert(context, context.getString(R.string.info_dialog_title), response.getResponseMessage());
                                }

                            },
                            e -> {

                                try {

                                    if (e instanceof ConnectException) {
                                        hideProgressDialog();
                                        Utility.simpleAlert(context, context.getString(R.string.error),
                                                context.getString(R.string.check_network_connection));
                                    } else {
                                        hideProgressDialog();
                                        e.printStackTrace();
                                        Utility.simpleAlert(context, "", context.getString(R.string.something_went_wrong));
                                    }

                                }catch (Exception ex){
                                    Log.e( "","Within Throwable Exception::" + e.getMessage());
                                }

                            });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(context, context.getString(R.string.error),
                    context.getString(R.string.check_network_connection));
        }
    }

    private void showProgressDialog() {


        if (dialog == null)
            dialog = new Dialog(context);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (dialog != null && !dialog.isShowing())
            dialog.show();

    }

    private void hideProgressDialog() {

        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public interface WishList {

        void addToWishList(ImageView iv_RemoveFromWishlist, ImageView iv_AddToWishlist, String menuId);

        void removeFromWishList(ImageView iv_AddToWishlist, ImageView iv_RemoveFromWishlist, String menuId);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private void addIntoCart(ImageView ivAddtocart, ImageView ivAddedintocart,
                             SearchProductResult menu,
                             String qty, String varientId, String brandId,MyViewHolder holder) {

        if (Utility.getInstance().checkInternetConnection(context)) {

            RequestBody rbUserId = RequestBody.create(MediaType.parse("text/plain"), userPref.getUser().getId());
            RequestBody rbMenuId = RequestBody.create(MediaType.parse("text/plain"), menu.getId());
            RequestBody rbQty = RequestBody.create(MediaType.parse("text/plain"), qty);
            RequestBody rbVarient = RequestBody.create(MediaType.parse("text/plain"), varientId);
            RequestBody rbBrandId = RequestBody.create(MediaType.parse("text/plain"), brandId);
            RequestBody rbToken = RequestBody.create(MediaType.parse("text/plain"), userPref.getUser().getToken());

            apiService.addCart(
                    rbUserId,
                    rbMenuId,
                    rbQty,
                    rbVarient,
                    rbBrandId,
                    rbToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            showProgressDialog();
                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            hideProgressDialog();
                        }
                    })
                    .subscribe(response -> {


                        if (response.getResponseCode().equals("200")) {

                            ivAddtocart.setVisibility(View.GONE);
                            ivAddedintocart.setVisibility(View.VISIBLE);

                            holder.tv_productcount.setFocusable(false);
                            holder.tv_productcount.setEnabled(true);
                            holder.tv_productcount.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(context,"You can update the quantity in My Cart Page",Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(context,userPref);

                        } else {

                            hideProgressDialog();
                            Utility.simpleAlert(context, context.getString(R.string.info_dialog_title), response.getResponseMessage());
                        }

                    }, e -> {

                        try {

                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(context, context.getString(R.string.error),
                                        context.getString(R.string.check_network_connection));
                            } else {
                                hideProgressDialog();
                                e.printStackTrace();
                                Utility.simpleAlert(context, "", context.getString(R.string.something_went_wrong));
                            }

                        }catch (Exception ex){
                            Log.e( "","Within Throwable Exception::" + e.getMessage());
                        }

                    });


        } else {
            hideProgressDialog();
            Utility.simpleAlert(context, context.getString(R.string.error),
                    context.getString(R.string.check_network_connection));
        }
    }

    private void removeItemFromCart(ImageView ivAddtocart,
                                    ImageView ivAddedintocart,
                                    SearchProductResult menu,
                                    MyViewHolder holder,String brandId) {

        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.removeFromCart(
                    userPref.getUser().getId(),
                    menu.getId(),
                    userPref.getUser().getToken(),
                    brandId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            showProgressDialog();
                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            hideProgressDialog();
                        }
                    })
                    .subscribe(response -> {

                        if (response.getResponseCode().equals("200")) {

                            ivAddedintocart.setVisibility(View.GONE);
                            ivAddtocart.setVisibility(View.VISIBLE);

                            holder.tv_productcount.setFocusableInTouchMode(true);
                            holder.tv_productcount.setFocusable(true);
                            holder.tv_productcount.setEnabled(true);

                        } else if(response.getResponseCode().equals("403")){

                            utils.openLogoutDialog(context,userPref);

                        }else {

                            hideProgressDialog();
                            Utility.simpleAlert(context,
                                    context.getString(R.string.info_dialog_title),
                                    response.getResponseMessage());
                        }
                    }, e -> {

                        try {

                            if (e instanceof ConnectException) {
                                hideProgressDialog();
                                Utility.simpleAlert(context, context.getString(R.string.error),
                                        context.getString(R.string.check_network_connection));
                            } else {
                                hideProgressDialog();
                                e.printStackTrace();
                                Utility.simpleAlert(context, "", context.getString(R.string.something_went_wrong));
                            }

                        }catch (Exception ex){
                            Log.e( "","Within Throwable Exception::" + e.getMessage());
                        }

                    });

        } else {
            hideProgressDialog();
            Utility.simpleAlert(context,
                    context.getString(R.string.error),
                    context.getString(R.string.check_network_connection));
        }


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img_product, iv_BrandDropDown,
                iv_AddToWishlist, iv_RemoveFromWishlist, iv_AddToCart, iv_AddedIntoCart;
        TextView tv_Discount, txt_product_name, txt_price,
                btn_add, btn_minus;
        EditText tv_productcount;
        RelativeLayout moreBrandContainer, rl_Varient;
        FrameLayout frame_WishList, frame_Cart;
        Spinner productVarient;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_product = itemView.findViewById(R.id.img_product);
            iv_BrandDropDown = itemView.findViewById(R.id.iv_BrandDropDown);
            iv_AddToWishlist = itemView.findViewById(R.id.iv_AddToWishlist);
            iv_RemoveFromWishlist = itemView.findViewById(R.id.iv_RemoveFromWishlist);
            iv_AddToCart = itemView.findViewById(R.id.iv_AddToCart);
            iv_AddedIntoCart = itemView.findViewById(R.id.iv_AddedIntoCart);
            tv_productcount = itemView.findViewById(R.id.tv_productcount);
            productVarient = itemView.findViewById(R.id.productVarient);

            rl_Varient = itemView.findViewById(R.id.rl_Varient);


            tv_Discount = itemView.findViewById(R.id.tv_Discount);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_price = itemView.findViewById(R.id.txt_price);

            btn_add = itemView.findViewById(R.id.btn_add);
            btn_minus = itemView.findViewById(R.id.btn_minus);

            moreBrandContainer = itemView.findViewById(R.id.moreBrandContainer);

            frame_WishList = itemView.findViewById(R.id.frame_WishList);
            frame_Cart = itemView.findViewById(R.id.frame_Cart);

        }
    }


}
