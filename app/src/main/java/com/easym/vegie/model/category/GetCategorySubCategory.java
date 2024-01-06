package com.easym.vegie.model.category;

public class GetCategorySubCategory {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private GetCategorySubCategoryData data;

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

    public GetCategorySubCategoryData getData() {
        return data;
    }

    public void setData(GetCategorySubCategoryData data) {
        this.data = data;
    }
}
