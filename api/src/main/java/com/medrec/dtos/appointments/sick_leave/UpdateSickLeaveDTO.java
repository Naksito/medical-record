package com.medrec.dtos.appointments.sick_leave;

import com.medrec.exception_handling.exceptions.IdNotSetException;
import com.medrec.grpc.appointments.Appointments;

public class UpdateSickLeaveDTO {
    private Integer id;
    private String date;
    private Integer numberOfDays;

    public UpdateSickLeaveDTO() {
    }

    public UpdateSickLeaveDTO(int id, String date, int numberOfDays) {
        this.id = id;
        this.date = date;
        this.numberOfDays = numberOfDays;
    }

    public Appointments.UpdateSickLeaveRequest safeConvertToGrpcRequest() throws RuntimeException {
        Appointments.UpdateSickLeaveRequest.Builder requestBuilder = Appointments.UpdateSickLeaveRequest.newBuilder();

        if (this.id == null) {
            throw new IdNotSetException("Sick Leave id is not set. Cannot update Sick Leave");
        }

        requestBuilder.setId(this.id);

        if (this.date != null) {
            requestBuilder.setDate(this.date);
        }

        if (this.numberOfDays != null) {
            requestBuilder.setDaysOfLeave(this.numberOfDays);
        }

        return requestBuilder.build();
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
