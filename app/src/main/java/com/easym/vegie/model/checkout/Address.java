package com.easym.vegie.model.checkout;

import java.util.List;

public class Address {

    private List<ResultAddress> result_address = null;
    private String msg;
    private String status;

    public List<ResultAddress> getResultAddress() {
        return result_address;
    }

    public void setResultAddress(List<ResultAddress> resultAddress) {
        this.result_address = resultAddress;
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
