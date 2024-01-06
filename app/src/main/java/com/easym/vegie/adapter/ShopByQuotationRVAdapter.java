package com.easym.vegie.adapter;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.easym.vegie.model.shopbyquotation.Result;
import com.easym.vegie.sharePref.UserPref;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class ShopByQuotationRVAdapter extends RecyclerView.Adapter {

    Context context;
    List<Result>  list;
    String [] viewType;
    Cart cart;
    Dialog dialog  = null;
    ApiService1 apiService;
    UserPref userPref;
    String total;
    Utils utils;


    public ShopByQuotationRVAdapter(Context context, String[] viewTypeList,
                                    List<Result>  quotationListData, String totalAmount,
                                    Cart mCart, ApiService1 mApiService, UserPref mUserPref, Utils utils) {
        this.context = context;
        this.viewType =viewTypeList;
        this.list = quotationListData;
        this.cart = mCart;
        this.total = totalAmount;
        this.apiService = mApiService;
        this.userPref = mUserPref;
        this.utils = utils;

    }

    int CategoryTitleItem = 0;
    int HeaderItem = 1;
    int ProductItem = 2;
    int TotalAmount = 3;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == HeaderItem){

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_shop_by_quotation_header_item,
                    parent, false);
            return new HeaderItemRVViewHolder(view);

        }else if(viewType == ProductItem){

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_by_quotation_product_recyclerview_layout,
                    parent, false);
            return new ProductItemRVViewHolder(view);

        }else{

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_shop_by_quotation_total_amount,
                    parent, false);
            return new TotalAmountRVViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        if(viewType == HeaderItem){
            ((HeaderItemRVViewHolder) holder).bind();
        }else if(viewType == ProductItem){
            ((ProductItemRVViewHolder) holder).bind();
        }else {
            ((TotalAmountRVViewHolder) holder).bind();
        }

    }

    @Override
    public int getItemCount() {
        return viewType.length;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) return HeaderItem;
        else if(position == 2) return TotalAmount;
        else return ProductItem;

    }

    class HeaderItemRVViewHolder extends RecyclerView.ViewHolder {

        TextView tv_ProductTitle,/*tv_UOMTitle,*/tv_Price,tv_QTYTilte,
                tv_TotalPrice,tv_AddToCart;

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

    class ProductItemRVViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rv_Product;
        ProductCategoryRVAdapter adapter;

        public ProductItemRVViewHolder(@NonNull View itemView) {
            super(itemView);

            rv_Product = itemView.findViewById(R.id.rv_Product);

        }
        public void bind() {

            LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,
                    false);

            List<String> viewTypeList = new ArrayList<>();
            viewTypeList.add("CategoryTitle");
            viewTypeList.add("Product");

            adapter = new ProductCategoryRVAdapter(context,list,viewTypeList);
            rv_Product.setAdapter(adapter);
            rv_Product.setLayoutManager(llm);

        }
    }

    class TotalAmountRVViewHolder extends RecyclerView.ViewHolder {

        TextView tv_TotalAmount;

        public TotalAmountRVViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_TotalAmount = itemView.findViewById(R.id.tv_TotalAmount);
        }
        public void bind() {

            tv_TotalAmount.setText("Total Amount : "+total);
        }
    }

    class  CategoryTitleRVViewHolder extends RecyclerView.ViewHolder{

        TextView tv_CategoryTitle;

        public CategoryTitleRVViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_CategoryTitle = itemView.findViewById(R.id.tv_CategoryTitle);
        }

        public void bind(String title) {
            tv_CategoryTitle.setText(title);
        }
    }

   class ItemRVViewHolder extends RecyclerView.ViewHolder{

        RecyclerView rv_Product;

       public ItemRVViewHolder(@NonNull View itemView) {
           super(itemView);

           rv_Product = itemView.findViewById(R.id.rv_Product);

       }

       public void bind(List<Product> product) {

           LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,
                   false);
           ProductItemRVAdapter adapter = new ProductItemRVAdapter(context,product);
           rv_Product.setAdapter(adapter);
           rv_Product.setLayoutManager(llm);

       }
   }


    class  ProductCategoryRVAdapter extends RecyclerView.Adapter{

        Context context;
        List<Result>  list;
        List<String> viewType;

        public ProductCategoryRVAdapter(Context context, List<Result> list,
                                        List<String> mViewType) {
            this.context = context;
            this.list = list;
            this.viewType = mViewType;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if(viewType == CategoryTitleItem){

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_shop_by_quotation_category_title_item,
                        parent, false);
                return new CategoryTitleRVViewHolder(view);

            }else {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_by_quotation_product_recyclerview_layout,
                        parent, false);
                return new ItemRVViewHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            int viewType = getItemViewType(position);
            if(viewType == CategoryTitleItem){
                ((CategoryTitleRVViewHolder) holder).bind(list.get(position).getName());
            }else if(viewType == ProductItem){
                ((ItemRVViewHolder) holder).bind(list.get(position).getProduct());
            }

        }
        @Override
        public int getItemCount() {
            return viewType.size();
        }
        @Override
        public int getItemViewType(int position) {

            if (position == 0) return HeaderItem;
            else  return ProductItem;

        }

    }

    class ProductItemRVAdapter extends RecyclerView.Adapter<ProductItemRVAdapter.RecyclerViewHolder>{

        Context context;
        List<Product> productList;
        String[] brandIdArr;

        public ProductItemRVAdapter(Context context, List<Product> list) {
            this.context = context;
            this.productList = list;

            brandIdArr = new String[productList.size()];
            for(int i = 0; i<productList.size();i++){
                brandIdArr[i] = "";
            }
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_shop_by_quotation_product_item,parent,false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {


            if(!productList.get(position).getOther_name().equals("false")) {
                holder.tv_productname.setText(productList.get(position).getOther_name());
            }else{
                holder.tv_productname.setText(productList.get(position).getMenuName());
            }

           // holder.tv_UOM.setText(productList.get(position).getUnit());
            holder.tv_Price.setText(productList.get(position).getPrice());
            holder.tv_QTY.setText(productList.get(position).getQuantity());
            holder.tv_TotalPrice.setText(productList.get(position).getTotalPrice());

            if(productList.get(position).getIs_cart()){
                holder.cb_AddToCart.setChecked(true);
            }else{
                holder.cb_AddToCart.setChecked(false);
            }


            holder.cb_AddToCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(holder.cb_AddToCart.isChecked()){

                        cart.addToCart(productList.get(position).getId(),
                                productList.get(position).getQuantity(),brandIdArr[position]);

                    }else{

                        cart.removeFromCart(productList.get(position).getId());
                    }
                }
            });

            holder.iv_BrandDropDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Utility.getInstance().checkInternetConnection(context)) {

                        Log.e("UserId",userPref.getUser().getId()+"");

                        apiService.getProductBrand(
                                productList.get(position).getId(),
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

                                        if(response.getData().getProductBrandResult() == null){
                                            Toast.makeText(context, "No brand found", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        List<ProductBrandResult> brandResults = response.getData().getProductBrandResult().getResult();
                                        String brandResponse = new Gson().toJson(brandResults);
                                        Log.e("BrandResponse",brandResponse+"");
                                        showPopUp(holder.iv_BrandDropDown,brandResults,position);


                                    } else if(response.getResponseCode().equals("403")){

                                        utils.openLogoutDialog(context,userPref);

                                    }else{
                                        hideProgressDialog();
                                        Toast.makeText(context, ""+response.getResponseMessage(), Toast.LENGTH_SHORT).show();
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


                    }else{
                        Utility.simpleAlert(context, context.getString(R.string.error),
                                context.getString(R.string.check_network_connection));
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        class RecyclerViewHolder extends RecyclerView.ViewHolder{

            TextView tv_productname,/*tv_UOM,*/tv_Price,tv_QTY,tv_TotalPrice;
            ImageView iv_BrandDropDown;
            CheckBox cb_AddToCart;

            public RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_productname = itemView.findViewById(R.id.tv_productname);
               // tv_UOM = itemView.findViewById(R.id.tv_UOM);
                tv_Price = itemView.findViewById(R.id.tv_Price);
                tv_QTY = itemView.findViewById(R.id.tv_QTY);
                tv_TotalPrice = itemView.findViewById(R.id.tv_TotalPrice);
                iv_BrandDropDown = itemView.findViewById(R.id.iv_BrandDropDown);
                cb_AddToCart = itemView.findViewById(R.id.cb_AddToCart);
            }
        }

        private void showPopUp(View view, List<ProductBrandResult> brandList, int position){

            View  popupView = LayoutInflater.from(context).inflate(R.layout.brand_list_custom_alert_dialog, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            RecyclerView recyclerView = popupView.findViewById(R.id.rv_Brand);

            LinearLayoutManager lm = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
            BrandRVAdapter brandRVAdapter = new BrandRVAdapter(context,brandList);
            recyclerView.setAdapter(brandRVAdapter);
            recyclerView.setLayoutManager(lm);

            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(@NotNull View view, int i) {
                            brandIdArr[position] = brandList.get(i).getId();
                            popupWindow.dismiss();
                        }
                    }));

            popupWindow.setOutsideTouchable(true);
            popupWindow.showAsDropDown(view, 0, 0);

        }

        private void showProgressDialog(){

            if (dialog == null)
                dialog = new Dialog(context);
            dialog.setContentView(R.layout.progress_dialog);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }

        }

        private void hideProgressDialog(){

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    }





   public interface Cart {

        void addToCart(String menuId,String qty,String brandId);
        void removeFromCart(String menuId);

    }
}
