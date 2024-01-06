package com.easym.vegie.model.getorderdetail;

public class ProductList {

    private String menu_name;
    private String quantity;
    private String price;
    private String unit;
    private String brand_name;
    private String subtotal;

    public String getMenuName() {
        return menu_name;
    }

    public void setMenuName(String menuName) {
        this.menu_name = menuName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }
}
