package com.example.hackathonoriginal.Databases;

import java.io.Serializable;

public class Upload implements Serializable {
    String NFullName, NAddress, NCase, NDescription, NPhone, dateRequest;

    public Upload(){

    }

    public Upload(String NFullName, String NAddress, String NCase, String NDescription, String NPhone, String dateRequest) {
        this.NFullName = NFullName;
        this.NAddress = NAddress;
        this.NCase = NCase;
        this.NDescription = NDescription;
        this.NPhone = NPhone;
        this.dateRequest = dateRequest;
    }

    public String getDateRequest() {
        return dateRequest;
    }

    public void setDateRequest(String dateRequest) {
        this.dateRequest = dateRequest;
    }

    public String getNFullName() {
        return NFullName;
    }

    public void setNFullName(String NFullName) {
        this.NFullName = NFullName;
    }

    public String getNAddress() {
        return NAddress;
    }

    public void setNAddress(String NAddress) {
        this.NAddress = NAddress;
    }

    public String getNCase() {
        return NCase;
    }

    public void setNCase(String NCase) {
        this.NCase = NCase;
    }

    public String getNDescription() {
        return NDescription;
    }

    public void setNDescription(String NDescription) {
        this.NDescription = NDescription;
    }

    public String getNPhone() {
        return NPhone;
    }

    public void setNPhone(String NPhone) {
        this.NPhone = NPhone;
    }

}
