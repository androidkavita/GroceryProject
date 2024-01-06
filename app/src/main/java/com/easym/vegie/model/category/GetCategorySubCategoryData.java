package com.easym.vegie.model.category;

import java.util.List;

public class GetCategorySubCategoryData {

    private CartCount cart_count;
    private List<CaegoryName> caegory_name = null;

    public CartCount getCartCount() {
        return cart_count;
    }

    public void setCartCount(CartCount cartCount) {
        this.cart_count = cartCount;
    }

    public List<CaegoryName> getCaegoryName() {
        return caegory_name;
    }

    public void setCaegoryName(List<CaegoryName> caegoryName) {
        this.caegory_name = caegoryName;
    }

}
