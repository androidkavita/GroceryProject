package com.easym.vegie.model.otp;

public class ResendOtpData {

    private String id;
    private String username;
    private String country_code;
    private String mobile_number;
    private Integer otp;
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCountryCode() {
        return country_code;
    }

    public void setCountryCode(String countryCode) {
        this.country_code = countryCode;
    }

    public String getMobileNumber() {
        return mobile_number;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobile_number = mobileNumber;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
