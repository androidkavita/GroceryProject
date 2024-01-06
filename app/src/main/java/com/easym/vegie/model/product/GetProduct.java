package com.easym.vegie.model.product;

public class GetProduct {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private GetProductData data;

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

    public GetProductData getData() {
        return data;
    }

    public void setData(GetProductData data) {
        this.data = data;
    }

}
