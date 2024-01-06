package com.easym.vegie.model.getorderdetail;

public class Status {

    private String status_id;
    private String is_active;
    private String created_at;
    private String status;

    public String getStatusId() {
        return status_id;
    }

    public void setStatusId(String statusId) {
        this.status_id = statusId;
    }

    public String getIsActive() {
        return is_active;
    }

    public void setIsActive(String isActive) {
        this.is_active = isActive;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String createdAt) {
        this.created_at = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
