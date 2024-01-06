package com.easym.vegie.model.mycartlist;

public class GetCartList {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private GetCartListData data = null;

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

    public GetCartListData getData() {
        return data;
    }

    public void setData(GetCartListData data) {
        this.data = data;
    }
}
