package com.example.hackathonoriginal.Databases;

import java.io.Serializable;

public class History implements Serializable {
    String victimName,victimPhone,dateReq, dateComplete;
    String cases;

    public History(){

    }

    public History(String victimName, String victimPhone, String dateReq, String dateComplete, String cases) {
        this.victimName = victimName;
        this.victimPhone = victimPhone;
        this.dateReq = dateReq;
        this.dateComplete = dateComplete;
        this.cases = cases;
    }

    public String getVictimName() {
        return victimName;
    }

    public void setVictimName(String victimName) {
        this.victimName = victimName;
    }

    public String getVictimPhone() {
        return victimPhone;
    }

    public void setVictimPhone(String victimPhone) {
        this.victimPhone = victimPhone;
    }

    public String getDateReq() {
        return dateReq;
    }

    public void setDateReq(String dateReq) {
        this.dateReq = dateReq;
    }

    public String getDateComplete() {
        return dateComplete;
    }

    public void setDateComplete(String dateComplete) {
        this.dateComplete = dateComplete;
    }

    public String getCases() {
        return cases;
    }

    public void setCases(String cases) {
        this.cases = cases;
    }
}
