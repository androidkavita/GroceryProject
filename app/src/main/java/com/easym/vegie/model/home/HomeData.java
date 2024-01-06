package com.easym.vegie.model.home;

import java.util.List;

public class HomeData {

    private TimeSlot time_slot;
    private List<Banner> banner = null;
    private List<CategoryName> caegory_name = null;
    private String whatsapp_number = null;

    public TimeSlot getTimeSlot() {
        return time_slot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.time_slot = timeSlot;
    }

    public List<Banner> getBanner() {
        return banner;
    }

    public void setBanner(List<Banner> banner) {
        this.banner = banner;
    }

    public List<CategoryName> getCaegoryName() {
        return caegory_name;
    }

    public void setCaegoryName(List<CategoryName> caegoryName) {
        this.caegory_name = caegoryName;
    }

    public void setWhatsapp_number(String whatsapp_number) {
        this.whatsapp_number = whatsapp_number;
    }

    public String getWhatsapp_number() {
        return whatsapp_number;
    }





}
