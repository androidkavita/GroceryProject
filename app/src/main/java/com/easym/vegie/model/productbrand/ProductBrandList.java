package com.easym.vegie.model.productbrand;

import java.util.List;

public class ProductBrandList {

    private List<ProductBrandResult> result = null;
    private String msg;
    private String status;

    public List<ProductBrandResult> getResult() {
        return result;
    }

    public void setResult(List<ProductBrandResult> result) {
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
