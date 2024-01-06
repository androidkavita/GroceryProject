package com.easym.vegie.model.cartcount;

public class UserCartCountData {

    private Integer total_amount;
    private Integer count_cart;
    private Integer minimum_order_limit;

    public Integer getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Integer total_amount) {
        this.total_amount = total_amount;
    }

    public Integer getCount_cart() {
        return count_cart;
    }

    public void setCount_cart(Integer count_cart) {
        this.count_cart = count_cart;
    }

    public Integer getMinimum_order_limit() {
        return minimum_order_limit;
    }

    public void setMinimum_order_limit(Integer minimum_order_limit) {
        this.minimum_order_limit = minimum_order_limit;
    }
}
