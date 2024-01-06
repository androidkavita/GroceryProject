package com.easym.vegie.model.userquotation;

public class GetUserQuotation {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private GetUserQuotationData data;

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

    public GetUserQuotationData getData() {
        return data;
    }

    public void setData(GetUserQuotationData data) {
        this.data = data;
    }

}
