package com.easym.vegie.model.updateprofile;

public class UpdateProfile {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private UpdateProfileData data;

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

    public UpdateProfileData getData() {
        return data;
    }

    public void setData(UpdateProfileData data) {
        this.data = data;
    }


}
