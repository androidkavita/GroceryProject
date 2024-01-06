package com.easym.vegie.model.getorderdetail;

public class GetOrderDetail {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private GetOrderDetailData data;

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

    public GetOrderDetailData getData() {
        return data;
    }

    public void setData(GetOrderDetailData data) {
        this.data = data;
    }

}
