package com.road.roaddrive.model;

public class DriverProfile {
    private String uid,name,mobile,driverType,address,agentUsername,email;
    private boolean verified;
    private BalanceProfile balanceProfile;
    public DriverProfile() {
    }

    public DriverProfile(String uid, String name, String mobile, String driverType, String agentUsername, String email) {
        this.uid = uid;
        this.name = name;
        this.mobile = mobile;
        this.driverType = driverType;
        this.verified=false;
        this.balanceProfile=new BalanceProfile(0,0);
        this.address="Nor Set";
        this.agentUsername=agentUsername;
        this.email=email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAgentUsername() {
        return agentUsername;
    }

    public void setAgentUsername(String agentUsername) {
        this.agentUsername = agentUsername;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BalanceProfile getBalanceProfile() {
        return balanceProfile;
    }

    public void setBalanceProfile(BalanceProfile balanceProfile) {
        this.balanceProfile = balanceProfile;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
