package com.easym.vegie.model.order;

public class GetOrder {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private GetOrderData data;

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

    public GetOrderData getData() {
        return data;
    }

    public void setData(GetOrderData data) {
        this.data = data;
    }
}
