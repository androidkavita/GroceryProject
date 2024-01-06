package com.easym.vegie.model.faq;

import java.util.List;

public class GetFAQ {

    private String responseCode;
    private String responseStatus;
    private List<GetFAQData> data = null;

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

    public List<GetFAQData> getData() {
        return data;
    }

    public void setData(List<GetFAQData> data) {
        this.data = data;
    }


}
