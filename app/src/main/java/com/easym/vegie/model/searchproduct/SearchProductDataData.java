package com.easym.vegie.model.searchproduct;

import java.util.List;

public class SearchProductDataData {

    private List<SearchProductResult> result = null;
    private String msg;
    private String status;

    public List<SearchProductResult> getResult() {
        return result;
    }

    public void setResult(List<SearchProductResult> result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
