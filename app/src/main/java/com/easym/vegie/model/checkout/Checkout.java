package com.easym.vegie.model.checkout;

public class Checkout {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private CheckoutData data;

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

    public CheckoutData getCheckoutData() {
        return data;
    }

    public void setCheckoutData(CheckoutData data) {
        this.data = data;
    }

}
