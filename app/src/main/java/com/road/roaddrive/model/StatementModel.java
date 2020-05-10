package com.road.roaddrive.model;

public class StatementModel {
    private double totalFare,driverEarn, companyEarn, agentEarn;
    private boolean paid;
    private String driverId,agentId,paymentType,id;

    public StatementModel() {
    }

    public StatementModel(double totalFare, double driverEarn, double companyEarn, double agentEarn, String driverId, String agentId) {
        this.driverEarn = driverEarn;
        this.companyEarn = companyEarn;
        this.agentEarn = agentEarn;
        this.driverId = driverId;
        this.agentId = agentId;
        this.totalFare=totalFare;
        this.paid = false;
        this.paymentType="Cash";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }

    public double getDriverEarn() {
        return driverEarn;
    }

    public void setDriverEarn(double driverEarn) {
        this.driverEarn = driverEarn;
    }

    public double getCompanyEarn() {
        return companyEarn;
    }

    public void setCompanyEarn(double companyEarn) {
        this.companyEarn = companyEarn;
    }

    public double getAgentEarn() {
        return agentEarn;
    }

    public void setAgentEarn(double agentEarn) {
        this.agentEarn = agentEarn;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
