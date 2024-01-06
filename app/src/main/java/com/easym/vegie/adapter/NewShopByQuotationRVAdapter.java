package com.easym.vegie.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.easym.vegie.model.shopbyquotation.Product;
import com.easym.vegie.sharePref.UserPref;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class NewShopByQuotationRVAdapter extends RecyclerView.Adapter {

    int CategoryTitleItem = 1;
    int HeaderItem = 2;
    int ProductItem = 3;
    int TotalAmount = 4;

    Dialog dialog = null;
    Context context;
    List<Product> list;
    Cart cart;
    ApiService1 apiService;
    UserPref userPref;
    int total;
    String[] brandIdArr;

    boolean isSaveButtonClicked = false;


    String updatedQty = "";
    String updatedPrice = "";


    String selectedBrandId = "";
    TextView tv_TotalAmount;

    public PopupWindow popupWindow = null;

    String totalPrice = "";
    Utils utils;

    public NewShopByQuotationRVAdapter(Context context, List<Product> list,
                                       Cart cart, ApiService1 apiService,
                                       UserPref userPref, int total,
                                       String selectedBrandId, Utils utils) {
        this.context = context;
        this.list = list;
        this.cart = cart;
        this.apiService = apiService;
        this.userPref = userPref;
        this.total = total;

        this.totalPrice = "" + total;
        brandIdArr = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            brandIdArr[i] = "";
        }
        this.selectedBrandId = selectedBrandId;
        this.utils = utils;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == CategoryTitleItem) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_shop_by_quotation_category_title_item,
                    parent, false);
            return new CategoryTitleRVViewHolder(view);

        } else if (viewType == HeaderItem) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_shop_by_quotation_header_item,
                    parent, false);
            return new HeaderItemRVViewHolder(view);

        } else if (viewType == ProductItem) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_shop_by_quotation_product_item,
                    parent, false);
            return new ProductItemRVViewHolder(view);

        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_shop_by_quotation_total_amount,
                    parent, false);
            return new TotalAmountRVViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        int viewType = getItemViewType(position);
        if (viewType == CategoryTitleItem) {
            if (!list.get(position).getOther_category_name().equals("false")) {
                ((CategoryTitleRVViewHolder) holder).bind(list.get(position).getCategory_name() + " / " + list.get(position).getOther_category_name());
            } else {
                ((CategoryTitleRVViewHolder) holder).bind(list.get(position).getCategory_name());
            }
        } else if (viewType == HeaderItem) {
            ((HeaderItemRVViewHolder) holder).bind();
        } else if (viewType == ProductItem) {
            ((ProductItemRVViewHolder) holder).bind(list.get(position), position);
        } else {
            ((TotalAmountRVViewHolder) holder).bind();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (!list.get(position).getView_type().equals("")
                && list.get(position).getView_type().equals("CategoryTitle")) {

            return CategoryTitleItem;

        } else if (!list.get(position).getView_type().equals("")
                && list.get(position).getView_type().equals("HeaderTitle")) {

            return HeaderItem;

        } else if (!list.get(position).getView_type().equals("")
                && list.get(position).getView_type().equals("Total")) {

            return TotalAmount;

        } else {
            return ProductItem;
        }

    }


    class CategoryTitleRVViewHolder extends RecyclerView.ViewHolder {

        TextView tv_CategoryTitle;

        public CategoryTitleRVViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_CategoryTitle = itemView.findViewById(R.id.tv_CategoryTitle);
        }

        public void bind(String title) {
            tv_CategoryTitle.setText(title);
        }
    }

    class HeaderItemRVViewHolder extends RecyclerView.ViewHolder {

        TextView tv_ProductTitle,/*tv_UOMTitle,*/
                tv_Price, tv_QTYTilte,
                tv_TotalPrice, tv_AddToCart;

        public HeaderItemRVViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_ProductTitle = itemView.findViewById(R.id.tv_ProductTitle);
            // tv_UOMTitle = itemView.findViewById(R.id.tv_UOMTitle);
            tv_Price = itemView.findViewById(R.id.tv_Price);
            tv_QTYTilte = itemView.findViewById(R.id.tv_QTYTilte);
            tv_TotalPrice = itemView.findViewById(R.id.tv_TotalPrice);
            tv_AddToCart = itemView.findViewById(R.id.tv_AddToCart);

        }

        public void bind() {

        }
    }

    public class ProductItemRVViewHolder extends RecyclerView.ViewHolder {

        TextView tv_productname,/*tv_UOM,*/
                tv_Price, tv_TotalPrice;
        ImageView iv_BrandDropDown;
        CheckBox cb_AddToCart;
        RelativeLayout rlSave;
        EditText tv_QTY;

        boolean isAddedInCart = false;
        boolean isUpdateAllowed = false;
        boolean canRemoveItemFromCart = false;

        boolean isPresentInCart = false;

        View itemView;

        Product product;
        int position;

        String price;


        public ProductItemRVViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;

            tv_productname = itemView.findViewById(R.id.tv_productname);
            //tv_UOM = itemView.findViewById(R.id.tv_UOM);
            tv_Price = itemView.findViewById(R.id.tv_Price);
            tv_QTY = itemView.findViewById(R.id.tv_QTY);
            tv_TotalPrice = itemView.findViewById(R.id.tv_TotalPrice);
            iv_BrandDropDown = itemView.findViewById(R.id.iv_BrandDropDown);
            cb_AddToCart = itemView.findViewById(R.id.cb_AddToCart);
            rlSave = itemView.findViewById(R.id.rlSave);
        }


        public void bind(Product product, int position) {

            this.product = product;
            this.position = position;
            this.price = product.getPrice();

            if (!product.getOther_name().equals("false")) {
                tv_productname.setText(product.getMenuName() + " / " + product.getUnit()
                        + "\n" + product.getOther_name());
            } else {
                tv_productname.setText(product.getMenuName());
            }

            if(product.getHaveBrand()){
                iv_BrandDropDown.setVisibility(View.VISIBLE);
            }else{
                iv_BrandDropDown.setVisibility(View.GONE);
            }

            // tv_UOM.setText(product.getUnit());
            tv_Price.setText(product.getPrice());

            Log.e("IsSavedButtonClicked", "" + isSaveButtonClicked);
            Log.e("IsAddedToCart", "" + isAddedInCart);

            if (!product.getQty_in_cart().equals("0")) {
                tv_QTY.setText(product.getQty_in_cart());

                Double unitPriceDouble = Double.valueOf(String.format("%.2f", Double.valueOf(product.getPrice())));
                Double qtyDouble = Double.valueOf(String.format("%.2f", Double.valueOf(product.getQty_in_cart())));

                Double mUpdatedPrice = unitPriceDouble * qtyDouble;

                Double roundedPrice = Double.valueOf(String.format("%.2f", mUpdatedPrice));

                tv_TotalPrice.setText("" + roundedPrice);

            } else {
                tv_QTY.setText("1");
                tv_TotalPrice.setText(product.getPrice());
            }


            if (product.getIs_cart()) {

                isUpdateAllowed = false;
                canRemoveItemFromCart = true;
                cb_AddToCart.setChecked(true);

                tv_QTY.setFocusable(false);
                tv_QTY.setEnabled(true);

                tv_QTY.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "You can update the quantity in My Cart Page", Toast.LENGTH_SHORT).show();
                    }
                });

                cb_AddToCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (cb_AddToCart.isChecked() && !isUpdateAllowed) {

                            cart.addToCart(product.getId(),
                                    tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                            isAddedInCart = true;


                        } else if (cb_AddToCart.isChecked() && isUpdateAllowed && !isPresentInCart) {

                            cart.addToCart(product.getId(),
                                    tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                            isAddedInCart = true;

                        } else if (!cb_AddToCart.isChecked() && canRemoveItemFromCart) {

                            Log.e("BrandId", "" + selectedBrandId);
                            cart.removeFromCart(product.getId(), selectedBrandId);
                            isAddedInCart = false;

                        }

                    }
                });


            } else {

                isUpdateAllowed = false;
                canRemoveItemFromCart = false;
                cb_AddToCart.setChecked(false);

                tv_QTY.setFocusableInTouchMode(true);
                tv_QTY.setFocusable(true);
                tv_QTY.setEnabled(true);

                cb_AddToCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (cb_AddToCart.isChecked() && !isUpdateAllowed) {

                            cart.addToCart(product.getId(),
                                    tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                            isAddedInCart = true;

                        } else if (cb_AddToCart.isChecked() && isUpdateAllowed && !isPresentInCart) {

                            cart.addToCart(product.getId(),
                                    tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                            isAddedInCart = true;

                        } else if (!cb_AddToCart.isChecked() && canRemoveItemFromCart) {

                            Log.e("BrandId", "" + selectedBrandId);
                            cart.removeFromCart(product.getId(), selectedBrandId);
                            isAddedInCart = false;

                        }

                    }
                });


            }

            rlSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   // String unitPrice = product.getPrice();
                    String unitPrice = price;

                    Double unitPriceDouble = Double.valueOf(String.format("%.2f", Double.valueOf(unitPrice)));
                    Double qtyDouble = Double.valueOf(String.format("%.2f", Double.valueOf(tv_QTY.getText().toString())));

                    Double mUpdatedPrice = unitPriceDouble * qtyDouble;

                    Double roundedPrice = Double.valueOf(String.format("%.2f", mUpdatedPrice));

                    tv_TotalPrice.setText("" + roundedPrice);

                }
            });


            iv_BrandDropDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Utility.getInstance().checkInternetConnection(context)) {

                        Log.e("UserId", userPref.getUser().getId() + "");

                        apiService.getProductBrand(
                                product.getId(),
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

                                        if (response.getData().getProductBrandResult() == null) {
                                            Toast.makeText(context, "No brand found", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        List<ProductBrandResult> brandResults = response.getData().getProductBrandResult().getResult();
                                        String brandResponse = new Gson().toJson(brandResults);
                                        Log.e("BrandResponse", brandResponse + "");

                                        showPopUp(iv_BrandDropDown,
                                                brandResults,
                                                position,
                                                product.getId(),
                                                cb_AddToCart,
                                                tv_QTY,
                                                tv_productname,
                                                tv_Price);


                                    }
                                    else if(response.getResponseCode().equals("403")){

                                        utils.openLogoutDialog(context,userPref);

                                    } else {
                                        hideProgressDialog();
                                        Toast.makeText(context, "" + response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                                        // Utility.simpleAlert(getContext(), getString(R.string.error), response.getResponseMessage());
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
                        Utility.simpleAlert(context, context.getString(R.string.error),
                                context.getString(R.string.check_network_connection));
                    }

                }
            });

        }

        private void showPopUp(View view, List<ProductBrandResult> brandList, int position,
                               String menuId, CheckBox addToCartCheckBox,
                               TextView tv_QTY,
                               TextView tv_productname,
                               TextView tv_Price) {

            View popupView = LayoutInflater.from(context).inflate(R.layout.brand_list_custom_alert_dialog, null);
            popupWindow = new PopupWindow(popupView,
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            RecyclerView recyclerView = popupView.findViewById(R.id.rv_Brand);

            LinearLayoutManager lm = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            BrandRVAdapter brandRVAdapter = new BrandRVAdapter(context, brandList);
            recyclerView.setAdapter(brandRVAdapter);
            recyclerView.setLayoutManager(lm);


            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(@NotNull View view, int i) {

                            ProductBrandResult brandResult = brandList.get(i);

                            if (!brandResult.getOther_name().equals("false")) {
                                tv_productname.setText(brandResult.getBrandName() + " / " + brandResult.getUnit()
                                        + "\n" + brandResult.getOther_name());
                            } else {
                                tv_productname.setText(brandResult.getBrandName());
                            }
                            // tv_UOM.setText(product.getUnit());
                            price = brandResult.getPrice();
                            tv_Price.setText(brandResult.getPrice());

                            checkSelectedBrandProductIsAddedInCart(menuId,
                                    brandResult.getId(),
                                    addToCartCheckBox, i, position, brandResult,
                                    popupWindow, tv_QTY);
                            // ....
                            // brandIdArr[position] = brandList.get(i).getId();
                            // popupWindow.dismiss();

                        }
                    }));

            popupWindow.setOutsideTouchable(true);
            popupWindow.showAsDropDown(view, 0, 0);

        }

        private void checkSelectedBrandProductIsAddedInCart(String menuId,
                                                            String brandId,
                                                            CheckBox addToCartCheckBox,
                                                            int index,
                                                            int position,
                                                            ProductBrandResult brandResult,
                                                            PopupWindow popupWindow,
                                                            TextView tv_QTY) {


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
                    }).subscribe(response -> {

                if (response.getResponseCode().equals("200")) {

                    //Already added in cart
                    isUpdateAllowed = true;
                    isPresentInCart = true;
                    canRemoveItemFromCart = true;
                    brandIdArr[position] = brandId;
                    selectedBrandId = brandId;

                    tv_QTY.setText(response.getData().getQty());

                    addToCartCheckBox.setChecked(true);
                    popupWindow.dismiss();

                    tv_QTY.setFocusable(false);
                    tv_QTY.setEnabled(true);

                    tv_QTY.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "You can update the quantity in My Cart Page", Toast.LENGTH_SHORT).show();
                        }
                    });

                    cb_AddToCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (cb_AddToCart.isChecked() && !isUpdateAllowed) {

                                String brandId = brandIdArr[position];

                                if (!TextUtils.isEmpty(brandId) && !brandId.equals("0")) {

                                    performAddToCart(product.getId().toString(), tv_QTY.getText().toString(),brandIdArr[position],updatedPrice);

                                } else {

                                    cart.addToCart(product.getId(),
                                            tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                                    isAddedInCart = true;

                                }


                                tv_QTY.setFocusable(false);
                                tv_QTY.setEnabled(true);

                                tv_QTY.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(context, "You can update the quantity in My Cart Page", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } else if (cb_AddToCart.isChecked() && isUpdateAllowed && !isPresentInCart) {

                                if (!TextUtils.isEmpty(brandId) && !brandId.equals("0")) {

                                    performAddToCart(product.getId().toString(), tv_QTY.getText().toString(),brandIdArr[position],updatedPrice);


                                } else {

                                    cart.addToCart(product.getId(),
                                            tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                                    isAddedInCart = true;

                                }

                                tv_QTY.setFocusable(false);
                                tv_QTY.setEnabled(true);

                                tv_QTY.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(context, "You can update the quantity in My Cart Page", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } else if (!cb_AddToCart.isChecked() && canRemoveItemFromCart) {

                                Log.e("BrandId", "" + selectedBrandId);

                                if (!TextUtils.isEmpty(brandId) && !brandId.equals("0")) {

                                    performRemoveItemFromCart(product.getId(),selectedBrandId);

                                } else {
                                    cart.removeFromCart(product.getId(), selectedBrandId);
                                    isAddedInCart = false;
                                }

                                tv_QTY.setFocusableInTouchMode(true);
                                tv_QTY.setFocusable(true);
                                tv_QTY.setEnabled(true);

                            }

                        }
                    });


                }
                else if (response.getResponseCode().equals("400")) {

                    // Not in cart
                    isUpdateAllowed = true;
                    isPresentInCart = false;

                    brandIdArr[position] = brandId;

                    tv_QTY.setText("1.0");


                    Log.e("BrandId", "" + brandIdArr[position]);

                    canRemoveItemFromCart = false;

                    addToCartCheckBox.setChecked(false);
                    popupWindow.dismiss();

                    tv_QTY.setFocusableInTouchMode(true);
                    tv_QTY.setFocusable(true);
                    tv_QTY.setEnabled(true);


                    cb_AddToCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (cb_AddToCart.isChecked() && !isUpdateAllowed) {

                                cart.addToCart(product.getId(),
                                        tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                                isAddedInCart = true;

                                tv_QTY.setFocusable(false);
                                tv_QTY.setEnabled(true);

                                tv_QTY.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(context, "You can update the quantity in My Cart Page", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } else if (cb_AddToCart.isChecked() && isUpdateAllowed && !isPresentInCart) {

                                cart.addToCart(product.getId(),
                                        tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                                isAddedInCart = true;

                                tv_QTY.setFocusable(false);
                                tv_QTY.setEnabled(true);

                                tv_QTY.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(context, "You can update the quantity in My Cart Page", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } else if (!cb_AddToCart.isChecked() && canRemoveItemFromCart) {

                                Log.e("BrandId", "" + selectedBrandId);
                                cart.removeFromCart(product.getId(), selectedBrandId);
                                isAddedInCart = false;

                                tv_QTY.setFocusableInTouchMode(true);
                                tv_QTY.setFocusable(true);
                                tv_QTY.setEnabled(true);

                            }

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
                                Utility.simpleAlert(context, "",
                                        context.getString(R.string.something_went_wrong));
                            }

                        }catch (Exception ex ){
                            Log.e( "","Within Throwable Exception::" + e.getMessage());
                        }
            });


        }


        private void performAddToCart(String menuId, String qty, String brandId,String updatedPrice) {

            if (Utility.getInstance().checkInternetConnection(context)) {

                RequestBody rbUserId = RequestBody.create(MediaType.parse("text/plain"),userPref.getUser().getId());
                RequestBody rbMenuId = RequestBody.create(MediaType.parse("text/plain"),menuId);
                RequestBody rbQty = RequestBody.create(MediaType.parse("text/plain"),qty);
                RequestBody rbVarient = RequestBody.create(MediaType.parse("text/plain"),"");
                RequestBody rbBrandId = RequestBody.create(MediaType.parse("text/plain"),brandId);
                RequestBody rbToken = RequestBody.create(MediaType.parse("text/plain"),userPref.getUser().getToken());


                apiService.addCart(
                        rbUserId,
                        rbMenuId,
                        rbQty,
                        rbVarient,
                        rbBrandId,
                        rbToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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
                        }).subscribe(response -> {

                    if (response.getResponseCode().equals("200")) {

                        isUpdateAllowed = false;
                        canRemoveItemFromCart = true;
                        cb_AddToCart.setChecked(true);

                        cart.updateQty();

                        Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();

                        cb_AddToCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                if (cb_AddToCart.isChecked() && !isUpdateAllowed) {

                                    String brandId = brandIdArr[position];

                                    if (!TextUtils.isEmpty(brandId) && !brandId.equals("0")) {

                                        performAddToCart(product.getId().toString(), tv_QTY.getText().toString(),brandIdArr[position],updatedPrice);

                                    } else {

                                        cart.addToCart(product.getId(),
                                                tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                                        isAddedInCart = true;

                                    }


                                    tv_QTY.setFocusable(false);
                                    tv_QTY.setEnabled(true);

                                    tv_QTY.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(context, "You can update the quantity in My Cart Page", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                } else if (cb_AddToCart.isChecked() && isUpdateAllowed && !isPresentInCart) {

                                    if (!TextUtils.isEmpty(brandId) && !brandId.equals("0")) {

                                        performAddToCart(product.getId().toString(), tv_QTY.getText().toString(),brandIdArr[position],updatedPrice);


                                    } else {

                                        cart.addToCart(product.getId(),
                                                tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                                        isAddedInCart = true;

                                    }

                                    tv_QTY.setFocusable(false);
                                    tv_QTY.setEnabled(true);

                                    tv_QTY.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(context, "You can update the quantity in My Cart Page", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                } else if (!cb_AddToCart.isChecked() && canRemoveItemFromCart) {

                                    Log.e("BrandId", "" + selectedBrandId);

                                    if (!TextUtils.isEmpty(brandId) && !brandId.equals("0")) {

                                        performRemoveItemFromCart(product.getId(),selectedBrandId);

                                    } else {
                                        cart.removeFromCart(product.getId(), selectedBrandId);
                                        isAddedInCart = false;
                                    }

                                    tv_QTY.setFocusableInTouchMode(true);
                                    tv_QTY.setFocusable(true);
                                    tv_QTY.setEnabled(true);

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
                                    Utility.simpleAlert(context, "",
                                            context.getString(R.string.something_went_wrong));
                                }

                            }catch (Exception ex){
                                Log.e( "","Within Throwable Exception::" + e.getMessage());
                            }
                });

            }else {
                hideProgressDialog();
                Utility.simpleAlert(context, context.getString(R.string.error),
                        context.getString(R.string.check_network_connection));
            }
        }


        private void performRemoveItemFromCart(String menuId, String brandId) {

            if (Utility.getInstance().checkInternetConnection(context)) {
                apiService.removeFromCart(
                        userPref.getUser().getId(),
                        menuId,
                        userPref.getUser().getToken(),
                        brandId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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
                        }).subscribe(response -> {

                    if (response.getResponseCode().equals("200")) {

                        isUpdateAllowed = false;
                        canRemoveItemFromCart = false;
                        cb_AddToCart.setChecked(false);

                        cart.updateQty();

                        Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();

                        cb_AddToCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                if (cb_AddToCart.isChecked() && !isUpdateAllowed) {

                                    String brandId = brandIdArr[position];

                                    if (!TextUtils.isEmpty(brandId) && !brandId.equals("0")) {

                                        performAddToCart(product.getId().toString(), tv_QTY.getText().toString(),brandIdArr[position],updatedPrice);

                                    } else {

                                        cart.addToCart(product.getId(),
                                                tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                                        isAddedInCart = true;

                                    }


                                    tv_QTY.setFocusable(false);
                                    tv_QTY.setEnabled(true);

                                    tv_QTY.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(context, "You can update the quantity in My Cart Page", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                } else if (cb_AddToCart.isChecked() && isUpdateAllowed && !isPresentInCart) {

                                    if (!TextUtils.isEmpty(brandId) && !brandId.equals("0")) {

                                        performAddToCart(product.getId().toString(), tv_QTY.getText().toString(),brandIdArr[position],updatedPrice);


                                    } else {

                                        cart.addToCart(product.getId(),
                                                tv_QTY.getText().toString(), brandIdArr[position], updatedPrice);

                                        isAddedInCart = true;

                                    }

                                    tv_QTY.setFocusable(false);
                                    tv_QTY.setEnabled(true);

                                    tv_QTY.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(context, "You can update the quantity in My Cart Page", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                } else if (!cb_AddToCart.isChecked() && canRemoveItemFromCart) {

                                    Log.e("BrandId", "" + selectedBrandId);

                                    if (!TextUtils.isEmpty(brandId) && !brandId.equals("0")) {

                                        performRemoveItemFromCart(product.getId(),selectedBrandId);

                                    } else {
                                        cart.removeFromCart(product.getId(), selectedBrandId);
                                        isAddedInCart = false;
                                    }

                                    tv_QTY.setFocusableInTouchMode(true);
                                    tv_QTY.setFocusable(true);
                                    tv_QTY.setEnabled(true);

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
                                    Utility.simpleAlert(context, "",
                                            context.getString(R.string.something_went_wrong));
                                }

                            }catch (Exception ex){
                                Log.e( "","Within Throwable Exception::" + e.getMessage());
                            }
                });

            }else {
                hideProgressDialog();
                Utility.simpleAlert(context, context.getString(R.string.error),
                        context.getString(R.string.check_network_connection));
            }
        }


    }

    public class TotalAmountRVViewHolder extends RecyclerView.ViewHolder {


        public TotalAmountRVViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_TotalAmount = itemView.findViewById(R.id.tv_TotalAmount);
        }

        public void bind() {

            if (total != 0) {
                // tv_TotalAmount.setText("Total Amount : " + total);
            }
        }
    }


    private void showProgressDialog() {

        if (dialog == null)
            dialog = new Dialog(context);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

    }


    private void hideProgressDialog() {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public interface Cart {

        void addToCart(String menuId, String qty, String brandId, String totalPrice);

        void removeFromCart(String id, String brandId);

        void updateQty();

    }
}
