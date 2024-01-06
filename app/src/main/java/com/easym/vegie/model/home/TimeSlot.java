package com.easym.vegie.model.home;

public class TimeSlot {

    private String id;
    private String slot_number_of_day;
    private String start_time;
    private String end_time;
    private String status;
    private String created_date;
    private String updated_date;
    private String slot_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlotNumberOfDay() {
        return slot_number_of_day;
    }

    public void setSlotNumberOfDay(String slotNumberOfDay) {
        this.slot_number_of_day = slotNumberOfDay;
    }

    public String getStartTime() {
        return start_time;
    }

    public void setStartTime(String startTime) {
        this.start_time = startTime;
    }

    public String getEndTime() {
        return end_time;
    }

    public void setEndTime(String endTime) {
        this.end_time = endTime;
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

    public String getSlotDate() {
        return slot_date;
    }

    public void setSlotDate(String slotDate) {
        this.slot_date = slotDate;
    }
}
