package com.easym.vegie.model.category;

import java.util.List;

public class CaegoryName {

    private String id;
    private String parent;
    private String name;
    private String image;
    private String banner_image;
    private String url;
    private String status;
    private String created_date;
    private String updated_date;
    private String is_selected_for_banner;
    private String other_name;
    private List<SubCategoory> sub_categoory = null;

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
        return banner_image;
    }

    public void setBannerImage(String bannerImage) {
        this.banner_image = bannerImage;
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

    public String getIsSelectedForBanner() {
        return is_selected_for_banner;
    }

    public void setIsSelectedForBanner(String isSelectedForBanner) {
        this.is_selected_for_banner = isSelectedForBanner;
    }

    public List<SubCategoory> getSubCategoory() {
        return sub_categoory;
    }

    public void setSubCategoory(List<SubCategoory> subCategoory) {
        this.sub_categoory = subCategoory;
    }

    public String getOther_name() {
        return other_name;
    }

    public void setOther_name(String other_name) {
        this.other_name = other_name;
    }
}
