package com.easym.vegie.model.otp;

public class ResendOtp {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private ResendOtpData data;

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

    public ResendOtpData getData() {
        return data;
    }

    public void setData(ResendOtpData data) {
        this.data = data;
    }

}
