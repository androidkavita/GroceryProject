package com.easym.vegie.model.coupon;

import java.util.List;

public class GetCouponList {

    private String responseCode;
    private String responseStatus;
    private String responseMessage;
    private List<GetCouponListData> data = null;

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

    public List<GetCouponListData> getData() {
        return data;
    }

    public void setData(List<GetCouponListData> data) {
        this.data = data;
    }

}
