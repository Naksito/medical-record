package com.medrec.dtos.appointments.icd;

public class IcdOccurrenceDTO {
    private Integer icdId;
    private String icdCode;
    private Integer occurrence;

    public IcdOccurrenceDTO() {
    }

    public IcdOccurrenceDTO(Integer icdId, String icdCode, Integer occurrence) {
        this.icdId = icdId;
        this.icdCode = icdCode;
        this.occurrence = occurrence;
    }

    public Integer getIcdId() {
        return icdId;
    }

    public void setIcdId(Integer icdId) {
        this.icdId = icdId;
    }

    public String getIcdCode() {
        return icdCode;
    }

    public void setIcdCode(String icdCode) {
        this.icdCode = icdCode;
    }

    public Integer getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(Integer occurrence) {
        this.occurrence = occurrence;
    }
}