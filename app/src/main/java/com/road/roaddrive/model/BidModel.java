package com.road.roaddrive.model;

public class BidModel {
    private int amount;
    private String comment;

    public BidModel() {
    }

    public BidModel(int amount, String comment) {
        this.amount = amount;
        this.comment = comment;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
