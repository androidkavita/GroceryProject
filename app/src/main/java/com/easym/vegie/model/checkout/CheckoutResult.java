package com.easym.vegie.model.checkout;

public class CheckoutResult {

    private String sub_total;
    private String delivery_charge;
    private String coupon_discount;
    private String tax;
    private double total;
    private double advance_payable;
    private double remain_payable;

    public String getSubTotal() {
        return sub_total;
    }

    public void setSubTotal(String subTotal) {
        this.sub_total = subTotal;
    }

    public String getDeliveryCharge() {
        return delivery_charge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.delivery_charge = deliveryCharge;
    }

    public String getCouponDiscount() {
        return coupon_discount;
    }

    public void setCouponDiscount(String couponDiscount) {
        this.coupon_discount = couponDiscount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getAdvance_payable() {
        return advance_payable;
    }

    public void setAdvance_payable(Double advance_payable) {
        this.advance_payable = advance_payable;
    }

    public Double getRemain_payable() {
        return remain_payable;
    }

    public void setRemain_payable(Double remain_payable) {
        this.remain_payable = remain_payable;
    }
}
