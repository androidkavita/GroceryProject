package com.easym.vegie.model.shopbyquotation;

import java.util.List;

public class Result {

    private String name;
    private String other_category_name;
    private List<Product> product = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }

    public String getOther_category_name() {
        return other_category_name;
    }

    public void setOther_category_name(String other_category_name) {
        this.other_category_name = other_category_name;
    }
}
