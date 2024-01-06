package com.easym.vegie.model.searchproduct;

public class SearchProduct {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private SearchProductData data;

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

    public SearchProductData getData() {
        return data;
    }

    public void setData(SearchProductData data) {
        this.data = data;
    }

}
