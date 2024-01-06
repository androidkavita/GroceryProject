package com.easym.vegie.model.shopbyquotation;

public class GetProductQuotation {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private GetProductQuotationData data;

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

    public GetProductQuotationData getData() {
        return data;
    }

    public void setData(GetProductQuotationData data) {
        this.data = data;
    }

}
