package com.road.roaddrive.model;

public class TimeStamp {
    private int day,month,year,hours,minute;

    public TimeStamp() {
    }

    public TimeStamp(int day, int month, int year, int hours, int minute) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hours = hours;
        this.minute = minute;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
