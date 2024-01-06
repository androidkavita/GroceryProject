package com.easym.vegie.model.coupon;

public class GetCouponListData {

    private String id;
    private String coupon_description;
    private String coupon_code;
    private String percentage;
    private String image;
    private String start_date;
    private String end_date;
    private String status;
    private String created_date;
    private String updated_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponDescription() {
        return coupon_description;
    }

    public void setCouponDescription(String couponDescription) {
        this.coupon_description = couponDescription;
    }

    public String getCouponCode() {
        return coupon_code;
    }

    public void setCouponCode(String couponCode) {
        this.coupon_code = couponCode;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartDate() {
        return start_date;
    }

    public void setStartDate(String startDate) {
        this.start_date = startDate;
    }

    public String getEndDate() {
        return end_date;
    }

    public void setEndDate(String endDate) {
        this.end_date = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

}
