package com.easym.vegie.model.language;

import java.util.List;

public class LanguageList {

    private List<LanguageListResult> result = null;
    private String msg;
    private String status;

    public List<LanguageListResult> getResult() {
        return result;
    }

    public void setResult(List<LanguageListResult> result) {
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
