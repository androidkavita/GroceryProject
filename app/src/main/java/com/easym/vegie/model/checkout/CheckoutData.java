package com.easym.vegie.model.checkout;

public class CheckoutData {

    private CheckoutDataData data;
    private Address address;

    public CheckoutDataData getData() {
        return data;
    }

    public void setData(CheckoutDataData data) {
        this.data = data;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}
