package com.example.hackathonoriginal;

public class Victim {
    private String cases;
    private String dateComplete;
    private String dateReq;
    private String policeName;
    private String victimPhone;

    public Victim(){

    }
    public Victim(String cases, String dateComplete, String dateReq, String victimName){//, String victimPhone) {
        this.cases = cases;
        this.dateComplete = dateComplete;
        this.dateReq = dateReq;
        this.policeName = victimName;
       // this.victimPhone = victimPhone;
    }


    public String getCases() {
        return cases;
    }

    public void setCases(String cases) {
        this.cases = cases;
    }

    public String getDateComplete() {
        return dateComplete;
    }

    public void setDateComplete(String dateComplete) {
        this.dateComplete = dateComplete;
    }

    public String getDateReq() {
        return dateReq;
    }

    public void setDateReq(String dateReq) {
        this.dateReq = dateReq;
    }

    public String getPoliceName() {
        return policeName;
    }

    public void setPoliceName(String policeName) {
        this.policeName = policeName;
    }

    /*  public String getVictimPhone() {
        return victimPhone;
    }

    public void setVictimPhone(String victimPhone) {
        this.victimPhone = victimPhone;
    }*/
}

