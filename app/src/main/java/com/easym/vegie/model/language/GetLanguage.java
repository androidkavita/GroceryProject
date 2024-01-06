package com.easym.vegie.model.language;

public class GetLanguage {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private GetLanguageData data;

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

    public GetLanguageData getData() {
        return data;
    }

    public void setData(GetLanguageData data) {
        this.data = data;
    }
}
