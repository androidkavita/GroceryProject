package com.easym.vegie.model.shopbyquotation;

import java.util.List;

public class QuotationList {

    private Integer total;
    private List<Result> result = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

}
