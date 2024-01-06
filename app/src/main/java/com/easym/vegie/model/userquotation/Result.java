package com.easym.vegie.model.userquotation;

public class Result {

    private String id;
    private String qu_user_id;
    private String name;
    private String qu_no;
    private String qu_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuUserId() {
        return qu_user_id;
    }

    public void setQuUserId(String quUserId) {
        this.qu_user_id = quUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQu_no() {
        return qu_no;
    }

    public void setQu_no(String qu_no) {
        this.qu_no = qu_no;
    }

    public String getQu_date() {
        return qu_date;
    }

    public void setQu_date(String qu_date) {
        this.qu_date = qu_date;
    }
}
