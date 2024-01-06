package com.easym.vegie.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easym.vegie.R;
import com.easym.vegie.model.mycartlist.Product;

import java.util.List;

public class MyCartListAdapter extends RecyclerView.Adapter {

    Context context;

    int CategoryTitleItem = 1;
    int HeaderItem = 2;
    int ProductItem = 3;
    int TotalAmount = 4;
    int CategorySubToTalAmount = 5;

    List<Product> list;
    Cart cart;
    AddMore addMore;
    String totalAmount;

    public MyCartListAdapter(Context context, List<Product> listData,
                             String totalAmount, Cart myCart, AddMore mAddMore) {

        this.context = context;
        this.list = listData;
        this.cart = myCart;
        this.addMore = mAddMore;
        this.totalAmount = totalAmount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == CategoryTitleItem) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_shop_by_quotation_category_title_item,
                    parent, false);
            return new CategoryTitleRVViewHolder(view);

        } else if (viewType == HeaderItem) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_my_cart_list_header_item,
                    parent, false);
            return new HeaderItemRVViewHolder(view);

        } else if (viewType == ProductItem) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_my_cart_rv_item,
                    parent, false);
            return new ProductItemRVViewHolder(view);

        } else if (viewType == CategorySubToTalAmount) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_my_cart_list_sub_total_item,
                    parent, false);
            return new SubTotalAmountRVViewHolder(view);

        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_mycart_total_amount,
                    parent, false);
            return new TotalAmountRVViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewtype = holder.getItemViewType();
        if (viewtype == CategoryTitleItem) {
            if (!list.get(position).getOther_category_name().equals("false")) {

                ((CategoryTitleRVViewHolder) holder).bind(list.get(holder.getAdapterPosition()).getCategory_name() + " / " +
                        list.get(position).getOther_category_name());
            } else {
                ((CategoryTitleRVViewHolder) holder).bind(list.get(holder.getAdapterPosition()).getCategory_name());
            }

        } else if (viewtype == HeaderItem) {
            ((HeaderItemRVViewHolder) holder).bind();
        } else if (viewtype == ProductItem) {
            ((ProductItemRVViewHolder) holder).bind(list.get(holder.getAdapterPosition()), holder.getAdapterPosition());
        } else if (viewtype == CategorySubToTalAmount) {
            ((SubTotalAmountRVViewHolder) holder).bind("" + list.get(holder.getAdapterPosition()).getCategorySubtotal());
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

        } else if (!list.get(position).getView_type().equals("")
                && list.get(position).getView_type().equals("CategorySubTotal")) {

            return CategorySubToTalAmount;

        } else {

            return ProductItem;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
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

        public HeaderItemRVViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        public void bind() {

        }
    }

    class ProductItemRVViewHolder extends RecyclerView.ViewHolder {

        TextView tv_SerialNo, tv_Product,
        /*tv_UOM,*/tv_Price,
                tv_TotalPrice, tvAvailableQty;
        RelativeLayout rl_Delete, rl_Save;
        EditText et_Qty;

        public ProductItemRVViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_SerialNo = itemView.findViewById(R.id.tv_SerialNo);
            tv_Product = itemView.findViewById(R.id.tv_Product);
            tvAvailableQty = itemView.findViewById(R.id.tvAvailableQty);
            // tv_UOM = itemView.findViewById(R.id.tv_UOM);
            tv_Price = itemView.findViewById(R.id.tv_Price);
            et_Qty = itemView.findViewById(R.id.et_Qty);
            tv_TotalPrice = itemView.findViewById(R.id.tv_TotalPrice);
            rl_Delete = itemView.findViewById(R.id.rl_Delete);
            rl_Save = itemView.findViewById(R.id.rl_Save);
        }

        public void bind(Product product, int position) {

            //int serialNo = position+1;
            tv_SerialNo.setText(product.getSerialNo());

            if (product.getMBrandId().equals("0")) {

                if (!product.getOtherName().equals("false")) {
                    tv_Product.setText(product.getMenuName() + " / " + product.getUnit() + "\n" + product.getOtherName());
                } else {
                    tv_Product.setText(product.getMenuName());
                }

                tvAvailableQty.setText(product.getAvailableQty());
            } else {

                if (product.getBrand_name() != null &&
                        product.getOther_brand_name() != null &&
                        !product.getBrand_name().equals("false") &&
                        !product.getOther_brand_name().equals("false")) {
                    tv_Product.setText(product.getBrand_name() + " / " + product.getUnit() + "\n" + product.getOther_brand_name());
                } else {
                    tv_Product.setText(product.getBrand_name());
                }
            }

            //tv_UOM.setText(product.getUnit());
            tv_Price.setText(product.getPrice());
            et_Qty.setText(product.getQty());
            tv_TotalPrice.setText("" + product.getSubtotal());

            rl_Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String qty = et_Qty.getText().toString().trim();
                    addMore.updateQty(qty, position, product.getMBrandId());
                }
            });

            et_Qty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        et_Qty.clearFocus();
                        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(et_Qty.getWindowToken(), 0);

                        if (!TextUtils.isEmpty(et_Qty.getText().toString().trim())) {

                            // searchProduct(binding.etSearch.getText().toString().trim());
                            String qty = et_Qty.getText().toString().trim();
                            addMore.updateQty(qty, position, product.getMBrandId());

                        } else {
                            return true;
                        }
                        return true;
                    }
                    return false;
                }
            });

            rl_Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cart.removeFromCart(
                            product.getMenuId(),
                            product.getMBrandId());
                }
            });
        }
    }

    class SubTotalAmountRVViewHolder extends RecyclerView.ViewHolder {

        TextView tv_SubTotalAmount;

        public SubTotalAmountRVViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_SubTotalAmount = itemView.findViewById(R.id.tv_SubTotalAmount);
        }

        public void bind(String subTotal) {

            tv_SubTotalAmount.setText(" : " + subTotal);

        }
    }

    class TotalAmountRVViewHolder extends RecyclerView.ViewHolder {

        TextView tv_TotalAmount;
        LinearLayout ll_AddMore;


        public TotalAmountRVViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_TotalAmount = itemView.findViewById(R.id.tv_TotalAmount);
        }

        public void bind() {

            tv_TotalAmount.setText("Total Amount : " + totalAmount);
            ll_AddMore = itemView.findViewById(R.id.ll_AddMore);
            ll_AddMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMore.addMoreButton();
                }
            });
        }
    }

    /*//Old One

    class MyCartListHeaderItem extends RecyclerView.ViewHolder{

        public MyCartListHeaderItem(@NonNull View itemView) {
            super(itemView);

        }
        public void bind() {

        }
    }

    class MyCartListRecyclerview extends RecyclerView.ViewHolder{

        RecyclerView rv_MyCartItems;

        public MyCartListRecyclerview(@NonNull View itemView) {
            super(itemView);

            rv_MyCartItems = itemView.findViewById(R.id.rv_MyCartItems);

        }
        public void bind() {

            LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
            MyCartListItemRVAdapter adapter = new MyCartListItemRVAdapter();
            rv_MyCartItems.setAdapter(adapter);
            rv_MyCartItems.setLayoutManager(llm);

        }
    }

    class SingleMycartTotalAmount extends RecyclerView.ViewHolder{

        LinearLayout ll_AddMore;
        TextView tv_TotalAmount;

        public SingleMycartTotalAmount(@NonNull View itemView) {
            super(itemView);

            ll_AddMore = itemView.findViewById(R.id.ll_AddMore);
            tv_TotalAmount = itemView.findViewById(R.id.tv_TotalAmount);
        }

        public void bind() {

            tv_TotalAmount.setText("Total Amount : "+totalAmount);

            ll_AddMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMore.addMoreButton();
                }
            });
        }
    }


    class MyCartListItemRVAdapter extends
            RecyclerView.Adapter<MyCartListItemRVAdapter.RecyclerViewHolder> {


        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_my_cart_rv_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

            int serialNo = position+1;
            holder.tv_SerialNo.setText(""+serialNo);
            holder.tv_Product.setText(list.get(position).getMenuName());
            holder.tv_UOM.setText(list.get(position).getUnit());
            holder.tv_Price.setText(list.get(position).getPrice());
            holder.et_Qty.setText(list.get(position).getQty());
            holder.tv_TotalPrice.setText(list.get(position).getSubtotal());



            holder.et_Qty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        holder.et_Qty.clearFocus();
                        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(holder.et_Qty.getWindowToken(), 0);

                        if(!TextUtils.isEmpty(holder.et_Qty.getText().toString().trim())) {

                            // searchProduct(binding.etSearch.getText().toString().trim());
                            String qty = holder.et_Qty.getText().toString().trim();
                            addMore.updateQty(qty,position);

                        } else {
                            return true;
                        }
                        return true;
                    }
                    return false;
                }
            });


            holder.rl_Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cart.removeFromCart(list.get(position).getMenuId());
                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class RecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView tv_SerialNo, tv_Product,
                    tv_UOM,tv_Price,
                    tv_TotalPrice;
            RelativeLayout rl_Delete;
            EditText et_Qty;



            public RecyclerViewHolder(@NonNull View itemView) {
                super(itemView);

                tv_SerialNo = itemView.findViewById(R.id.tv_SerialNo);
                tv_Product = itemView.findViewById(R.id.tv_Product);
                tv_UOM = itemView.findViewById(R.id.tv_UOM);
                tv_Price = itemView.findViewById(R.id.tv_Price);
                et_Qty = itemView.findViewById(R.id.et_Qty);
                tv_TotalPrice = itemView.findViewById(R.id.tv_TotalPrice);
                rl_Delete = itemView.findViewById(R.id.rl_Delete);

            }
        }

    }*/

    public interface Cart {
        void removeFromCart(String menuId, String brandId);
    }

    public interface AddMore {
        void addMoreButton();

        void updateQty(String qty, int position, String brandId);
    }
}
