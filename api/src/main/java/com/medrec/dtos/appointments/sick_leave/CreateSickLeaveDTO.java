package com.medrec.dtos.appointments.sick_leave;

public class CreateSickLeaveDTO {
    private String date;
    private int numberOfDays;

    public CreateSickLeaveDTO() {
    }

    public CreateSickLeaveDTO(String date, int numberOfDays) {
        this.date = date;
        this.numberOfDays = numberOfDays;
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
