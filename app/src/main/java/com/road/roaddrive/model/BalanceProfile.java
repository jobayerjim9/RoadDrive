package com.road.roaddrive.model;

public class BalanceProfile {
    private double currentBalance,totalEarn;

    public BalanceProfile() {
    }

    public BalanceProfile(double currentBalance, double totalEarn) {
        this.currentBalance = currentBalance;
        this.totalEarn = totalEarn;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getTotalEarn() {
        return totalEarn;
    }

    public void setTotalEarn(double totalEarn) {
        this.totalEarn = totalEarn;
    }
}
