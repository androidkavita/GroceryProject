package com.easym.vegie.model.limitday;

public class LimitDay {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private LimitDayData data;

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

    public LimitDayData getData() {
        return data;
    }

    public void setData(LimitDayData data) {
        this.data = data;
    }
}
