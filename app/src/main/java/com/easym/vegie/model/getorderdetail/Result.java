package com.easym.vegie.model.getorderdetail;

import java.util.List;

public class Result {

    private List<ProductList> product_list = null;
    private String order_id;
    private String expected_delivery_date;
    private String total;
    private String Delivery_charge;
    private String paid_amount;
    private String remaining_amount;
    private String status;
    List<Status> status_details;
    private String payment_status;
    private String payment_type;
    private String transaction_key = null;

    public List<ProductList> getProductList() {
        return product_list;
    }

    public void setProductList(List<ProductList> productList) {
        this.product_list = productList;
    }

    public String getOrderId() {
        return order_id;
    }

    public void setOrderId(String orderId) {
        this.order_id = orderId;
    }

    public String getExpectedDeliveryDate() {
        return expected_delivery_date;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expected_delivery_date = expectedDeliveryDate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDeliveryCharge() {
        return Delivery_charge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.Delivery_charge = deliveryCharge;
    }

    public String getPaidAmount() {
        return paid_amount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paid_amount = paidAmount;
    }

    public String getRemainingAmount() {
        return remaining_amount;
    }

    public void setRemainingAmount(String remainingAmount) {
        this.remaining_amount = remainingAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public List<Status> getStatusDetails() {
        return status_details;
    }

    public void setStatusDetails(List<Status> statusDetails) {
        this.status_details = statusDetails;
    }

    public String getTransaction_key() {
        return transaction_key;
    }

    public void setTransaction_key(String transaction_key) {
        this.transaction_key = transaction_key;
    }

}
