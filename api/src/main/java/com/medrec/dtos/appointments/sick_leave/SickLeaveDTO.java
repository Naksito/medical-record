package com.medrec.dtos.appointments.sick_leave;

public class SickLeaveDTO {
    private int id;
    private String date;
    private int numberOfDays;

    public SickLeaveDTO() {
    }

    public SickLeaveDTO(int id, String date, int numberOfDays) {
        this.id = id;
        this.date = date;
        this.numberOfDays = numberOfDays;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }
}
