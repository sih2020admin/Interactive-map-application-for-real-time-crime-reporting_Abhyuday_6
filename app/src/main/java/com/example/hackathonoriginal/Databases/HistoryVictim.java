package com.example.hackathonoriginal.Databases;

import java.io.Serializable;

public class HistoryVictim implements Serializable {
    String cases,PoliceName;
    String dateReq,dateComplete;

    public HistoryVictim(){

    }

    public HistoryVictim(String cases, String policeName, String dateReq, String dateComplete) {
        this.cases = cases;
        PoliceName = policeName;
        this.dateReq = dateReq;
        this.dateComplete = dateComplete;
    }

    public String getCases() {
        return cases;
    }

    public void setCases(String cases) {
        this.cases = cases;
    }

    public String getPoliceName() {
        return PoliceName;
    }

    public void setPoliceName(String policeName) {
        PoliceName = policeName;
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
}
