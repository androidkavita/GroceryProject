package com.easym.vegie.model.mycart;

public class MyCart {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private MyCartData data;

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

    public MyCartData getData() {
        return data;
    }

    public void setData(MyCartData data) {
        this.data = data;
    }
}
