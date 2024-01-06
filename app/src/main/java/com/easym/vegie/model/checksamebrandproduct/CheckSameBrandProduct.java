package com.easym.vegie.model.checksamebrandproduct;

public class CheckSameBrandProduct {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private CheckSameBrandProductData data;

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

    public CheckSameBrandProductData getData() {
        return data;
    }

    public void setData(CheckSameBrandProductData data) {
        this.data = data;
    }
}
