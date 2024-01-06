package com.easym.vegie.model.searchproduct;

import java.util.List;

public class SearchProductData {

    private List<SearchProductResult> result = null;
    private String status = null;

    public List<SearchProductResult> getData() {
        return result;
    }

    public void setData(List<SearchProductResult> data) {
        this.result = data;
    }

    public List<SearchProductResult> getResult() {
        return result;
    }

    public void setResult(List<SearchProductResult> result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
