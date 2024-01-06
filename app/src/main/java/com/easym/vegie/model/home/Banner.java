package com.easym.vegie.model.home;

public class Banner {

    private String id;
    private String banner_name;
    private String mobile_banner_image;
    private String from_to;
    private String to_date;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBannerName() {
        return banner_name;
    }

    public void setBannerName(String bannerName) {
        this.banner_name = bannerName;
    }

    public String getMobileBannerImage() {
        return mobile_banner_image;
    }

    public void setMobileBannerImage(String mobileBannerImage) {
        this.mobile_banner_image = mobileBannerImage;
    }

    public String getFromTo() {
        return from_to;
    }

    public void setFromTo(String fromTo) {
        this.from_to = fromTo;
    }

    public String getToDate() {
        return to_date;
    }

    public void setToDate(String toDate) {
        this.to_date = toDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
