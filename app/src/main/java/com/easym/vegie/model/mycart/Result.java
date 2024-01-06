package com.easym.vegie.model.mycart;

public class Result {

    private String sub_total;
    private String delivery_charge;
    private String tax;
    private Integer total;

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

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
