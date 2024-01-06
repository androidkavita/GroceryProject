package com.easym.vegie.model.forgetpassword;

public class ForgotPassword {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private ForgotPasswordData data;

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

    public ForgotPasswordData getData() {
        return data;
    }

    public void setData(ForgotPasswordData data) {
        this.data = data;
    }

}
