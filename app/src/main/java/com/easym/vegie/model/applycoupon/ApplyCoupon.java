package com.easym.vegie.model.applycoupon;

public class ApplyCoupon {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private CouponData data;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public CouponData getData() {
        return data;
    }

    public void setData(CouponData data) {
        this.data = data;
    }

}
