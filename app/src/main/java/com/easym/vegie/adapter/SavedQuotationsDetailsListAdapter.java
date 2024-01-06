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
import com.easym.vegie.model.userquatationdetails.Product;

import java.util.List;

public class SavedQuotationsDetailsListAdapter extends RecyclerView.Adapter {

    Context context;

   /* int CategoryTitleItem = 0;
    int MyCartListRecyclerView = 1;
    int MyCartTotalAmount = 2;*/

    int CategoryTitleItem = 1;
    int HeaderItem = 2;
    int ProductItem = 3;
    int TotalAmount = 4;
    int CategorySubToTalAmount = 5;

    List<Product> list;
    Action action;
    String totalAmount;

    public SavedQuotationsDetailsListAdapter(Context context,
                                             List<Product> listData,
                                             String totalAmount,
                                             Action mAction) {
        this.context = context;
        this.list = listData;
        this.action = mAction;
        this.totalAmount = totalAmount;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == CategoryTitleItem) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_shop_by_quotation_category_title_item,
                    parent, false);
            return new MyCartListHeaderItem(view);

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
            return new SingleMycartTotalAmount(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewtype = holder.getItemViewType();
        if (viewtype == CategoryTitleItem) {
            if (!list.get(position).getOther_category_name().equals("false")) {
                ((MyCartListHeaderItem) holder).bind(list.get(position).getCategory_name() + " / " +
                        list.get(position).getOther_category_name());
            } else {
                ((MyCartListHeaderItem) holder).bind(list.get(position).getCategory_name());
            }
        } else if (viewtype == HeaderItem) {
            ((HeaderItemRVViewHolder) holder).bind();
        } else if (viewtype == ProductItem) {
            ((ProductItemRVViewHolder) holder).bind(list.get(position), position);
        } else if (viewtype == CategorySubToTalAmount) {
            ((SubTotalAmountRVViewHolder) holder).bind("" + list.get(position).getCategorySubtotal());
        } else {
            ((SingleMycartTotalAmount) holder).bind();
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

    class MyCartListHeaderItem extends RecyclerView.ViewHolder {

        TextView tv_CategoryTitle;

        public MyCartListHeaderItem(@NonNull View itemView) {
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
        /* tv_UOM,*/ tv_Price,
                tv_TotalPrice, tvAvailableQty;
        RelativeLayout rl_Delete, rl_Save;
        EditText et_Qty;
        // ImageView iv_Delete,iv_Save;

        public ProductItemRVViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_SerialNo = itemView.findViewById(R.id.tv_SerialNo);
            tv_Product = itemView.findViewById(R.id.tv_Product);
            tvAvailableQty = itemView.findViewById(R.id.tvAvailableQty);
            tv_Price = itemView.findViewById(R.id.tv_Price);
            et_Qty = itemView.findViewById(R.id.et_Qty);
            tv_TotalPrice = itemView.findViewById(R.id.tv_TotalPrice);
            rl_Delete = itemView.findViewById(R.id.rl_Delete);
            //iv_Delete = itemView.findViewById(R.id.iv_Delete);
            //iv_Save = itemView.findViewById(R.id.iv_Save);
            rl_Save = itemView.findViewById(R.id.rl_Save);

        }

        public void bind(Product product, int position) {
            tv_SerialNo.setText("" + product.getSerialNo());

            if (product.getQBrandId().equals("0")) {

                if (!product.getOtherName().equals("false")) {
                    tv_Product.setText(product.getMenuName() + " / " + product.getUnit() + "\n" + product.getOtherName());

                } else {
                    tv_Product.setText(product.getMenuName());
                }

/*                if (product.getInStock().equals(0)) {
                    tvAvailableQty.setVisibility(View.VISIBLE);
                    tvAvailableQty.setText("Available Qty " + product.getAvailableQty());
                } else {
                    tvAvailableQty.setVisibility(View.GONE);
                }*/

                tvAvailableQty.setText(product.getAvailableQty());

                tv_Price.setText(product.getPrice());

            } else {

                if (product.getQBrandName() != null &&
                        product.getOtherQBrand_name() != null &&
                        !product.getQBrandName().equals("false") &&
                        !product.getOtherQBrand_name().equals("false")) {

                    tv_Product.setText(product.getQBrandName() + " / " + product.getQBrandUnit() + "\n" + product.getOtherQBrand_name());

                } else {
                    tv_Product.setText(product.getQBrandName());
                }

                tv_Price.setText(product.getQBrandPrice());

            }


            // tv_UOM.setText(product.getUnit());

            et_Qty.setText(product.getQTotalQty());

            tv_TotalPrice.setText(product.getQTotalPrice());

            rl_Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.removeItem(position);
                }
            });


            rl_Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String qty = et_Qty.getText().toString().trim();
                    action.updateQty(qty, position, product.getQBrandId());
                }
            });

            et_Qty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        et_Qty.clearFocus();
                        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(et_Qty.getWindowToken(), 0);

                        //saveTask(et_Search.getText().toString());
                        if (!TextUtils.isEmpty(et_Qty.getText().toString().trim())) {

                            // searchProduct(binding.etSearch.getText().toString().trim());
                            String qty = et_Qty.getText().toString().trim();
                            action.updateQty(qty, position, product.getQBrandId());

                        } else {

                            return true;
                        }
                        return true;
                    }
                    return false;
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


    class SingleMycartTotalAmount extends RecyclerView.ViewHolder {

        LinearLayout ll_AddMore;
        TextView tv_TotalAmount;


        public SingleMycartTotalAmount(@NonNull View itemView) {
            super(itemView);

            ll_AddMore = itemView.findViewById(R.id.ll_AddMore);
            tv_TotalAmount = itemView.findViewById(R.id.tv_TotalAmount);
        }

        public void bind() {

            tv_TotalAmount.setText("Total Amount : " + totalAmount);
            ll_AddMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.addMore();
                }
            });
        }
    }


    public interface Action {

        void removeItem(int position);

        void updateQty(String qty, int position, String brandId);

        void addMore();
    }

}
