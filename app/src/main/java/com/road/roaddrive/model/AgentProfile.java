package com.road.roaddrive.model;

public class AgentProfile {
    private String name,email,username,mobile,address;
    private BalanceProfile balanceProfile;
    public AgentProfile() {
    }

    public AgentProfile(String name, String email, String username, String mobile) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.mobile = mobile;
        this.balanceProfile=new BalanceProfile(0,0);
        this.address="Not Set!";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
