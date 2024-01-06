package com.easym.vegie.model.cartcount;

public class UserCartCount {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private UserCartCountData data;

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

    public UserCartCountData getData() {
        return data;
    }

    public void setData(UserCartCountData data) {
        this.data = data;
    }

}
