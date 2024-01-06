package com.easym.vegie.model.searchproduct;

import java.util.List;

public class SearchProductResult {

    private String id;
    private String menu_category_id;
    private String p_type;
    private String menu_name;
    private String content;
    private String description;
    private String image;
    private String status;
    private String is_tranding;
    private String item_category;
    private String is_approve;
    private String price;
    private String quantity;
    private String unit;
    private String discount;
    private String created_date;
    private String updated_date;
    private Boolean is_cart;
    private Boolean is_wishlist;
    private List<MenuVerient> menu_verient = null;
    private String other_name;

    private String cart_qty;
    private String cart_id;

    private boolean haveBrand;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuCategoryId() {
        return menu_category_id;
    }

    public void setMenuCategoryId(String menuCategoryId) {
        this.menu_category_id = menuCategoryId;
    }

    public String getPType() {
        return p_type;
    }

    public void setPType(String pType) {
        this.p_type = pType;
    }

    public String getMenuName() {
        return menu_name;
    }

    public void setMenuName(String menuName) {
        this.menu_name = menuName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsTranding() {
        return is_tranding;
    }

    public void setIsTranding(String isTranding) {
        this.is_tranding = isTranding;
    }

    public String getItemCategory() {
        return item_category;
    }

    public void setItemCategory(String itemCategory) {
        this.item_category = itemCategory;
    }

    public String getIsApprove() {
        return is_approve;
    }

    public void setIsApprove(String isApprove) {
        this.is_approve = isApprove;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCreatedDate() {
        return created_date;
    }

    public void setCreatedDate(String createdDate) {
        this.created_date = createdDate;
    }

    public String getUpdatedDate() {
        return updated_date;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updated_date = updatedDate;
    }

    public Boolean getIsCart() {
        return is_cart;
    }

    public void setIsCart(Boolean isCart) {
        this.is_cart = isCart;
    }

    public Boolean getIsWishlist() {
        return is_wishlist;
    }

    public void setIsWishlist(Boolean isWishlist) {
        this.is_wishlist = isWishlist;
    }

    public List<MenuVerient> getMenuVerient() {
        return menu_verient;
    }

    public void setMenuVerient(List<MenuVerient> menuVerient) {
        this.menu_verient = menuVerient;
    }

    public String getOther_name() {
        return other_name;
    }

    public void setOther_name(String other_name) {
        this.other_name = other_name;
    }

    public String getCart_qty() {
        return cart_qty;
    }

    public void setCart_qty(String cart_qty) {
        this.cart_qty = cart_qty;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public boolean isHaveBrand() {
        return haveBrand;
    }

    public void setHaveBrand(boolean haveBrand) {
        this.haveBrand = haveBrand;
    }
}
