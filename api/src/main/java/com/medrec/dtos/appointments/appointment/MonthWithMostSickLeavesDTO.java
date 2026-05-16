package com.medrec.dtos.appointments.appointment;

public class MonthWithMostSickLeavesDTO {
    private String date;

    private Integer count;

    public MonthWithMostSickLeavesDTO() {
    }

    public MonthWithMostSickLeavesDTO(String date, Integer count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
