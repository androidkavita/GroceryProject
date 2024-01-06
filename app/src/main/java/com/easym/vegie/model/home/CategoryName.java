package com.easym.vegie.model.home;

public class CategoryName {

    private String id;
    private String parent;
    private String name;
    private String image;
    private String bannerImage;
    private String url;
    private String status;
    private String createdDate;
    private String updatedDate;
    private String isSelectedForBanner;
    private Object menuCategoryName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getIsSelectedForBanner() {
        return isSelectedForBanner;
    }

    public void setIsSelectedForBanner(String isSelectedForBanner) {
        this.isSelectedForBanner = isSelectedForBanner;
    }

    public Object getMenuCategoryName() {
        return menuCategoryName;
    }

    public void setMenuCategoryName(Object menuCategoryName) {
        this.menuCategoryName = menuCategoryName;
    }

}
