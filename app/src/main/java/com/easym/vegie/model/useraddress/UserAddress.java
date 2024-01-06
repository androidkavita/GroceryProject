package com.easym.vegie.model.useraddress;

import java.util.List;

public class UserAddress {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private List<UserAddressData> data = null;

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

    public List<UserAddressData> getData() {
        return data;
    }

    public void setData(List<UserAddressData> data) {
        this.data = data;
    }

}
