package com.example.hackathonoriginal.Databases;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    Context context;
    SharedPreferences sharedPreferences;
    private String mobile;
    private String FullName, AdharCard, Address, Gender, Age;
    private String policeID;

    private String victimReq,profileURL;

    private String Tuser1, Tuser2;

    private String TPolice;
    private  String caseSession;

    private String policeFoundIDSession;

    private String language;



    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    public String getLanguage() {
        language=sharedPreferences.getString("language", "");
        return language;
    }

    public void setLanguage(String language) {
        sharedPreferences.edit().putString("language", caseSession).commit();
        this.language = language;
    }

    public String getCaseSession() {
        caseSession=sharedPreferences.getString("caseSession", "");
        return caseSession;
    }

    public void setCaseSession(String caseSession) {
        sharedPreferences.edit().putString("caseSession", caseSession).commit();
        this.caseSession = caseSession;
    }

    public String getPoliceFoundIDSession() {
        policeFoundIDSession=sharedPreferences.getString("policeFoundIDSession", "");
        return policeFoundIDSession;
    }

    public void setPoliceFoundIDSession(String policeFoundIDSession) {
        sharedPreferences.edit().putString("policeFoundIDSession", policeFoundIDSession).commit();
        this.policeFoundIDSession = policeFoundIDSession;
    }

    public String getTPolice() {
        TPolice=sharedPreferences.getString("TPolice", "");
        return TPolice;
    }

    public void setTPolice(String TPolice) {
        sharedPreferences.edit().putString("TPolice", TPolice).commit();
        this.TPolice = TPolice;
    }

    public String getProfileURL() {
        profileURL=sharedPreferences.getString("profileURL", "");
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        sharedPreferences.edit().putString("profileURL", profileURL).commit();
        this.profileURL = profileURL;
    }

    public String getTuser1() {
        Tuser1 = sharedPreferences.getString("user1", "");
        return Tuser1;
    }

    public void setTuser1(String tuser1) {
        sharedPreferences.edit().putString("user1", tuser1).commit();
        Tuser1 = tuser1;
    }

    public String getTuser2() {
        Tuser2 = sharedPreferences.getString("user2", "");
        return Tuser2;
    }

    public void setTuser2(String tuser2) {
        sharedPreferences.edit().putString("user2", tuser2).commit();
        Tuser2 = tuser2;
    }

    public String getVictimReq() {
        victimReq = sharedPreferences.getString("userReq", "");
        return victimReq;
    }

    public void setVictimReq(String victimReq) {
        sharedPreferences.edit().putString("userReq", mobile).commit();
        this.victimReq = victimReq;
    }

    public String getMobile() {
        mobile = sharedPreferences.getString("userMobile", "");
        return mobile;
    }

    public String getFullName() {
        FullName = sharedPreferences.getString("userFullName", "");
        return FullName;
    }

    public String getAdharCard() {
        AdharCard = sharedPreferences.getString("userAdharCard", "");
        return AdharCard;
    }

    public String getAddress() {
        Address = sharedPreferences.getString("userAddress", "");
        return Address;
    }

    public String getGender() {
        Gender = sharedPreferences.getString("userGender", "");
        return Gender;
    }

    public String getAge() {
        Age = sharedPreferences.getString("userAge", "");
        return Age;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
        sharedPreferences.edit().putString("userMobile", mobile).commit();
    }
    public void setFullName(String fullName) {
        FullName = fullName;
        sharedPreferences.edit().putString("userFullName", FullName).commit();
    }

    public void setAdharCard(String adharCard) {
        AdharCard = adharCard;
        sharedPreferences.edit().putString("userAdharCard", AdharCard).commit();
    }

    public void setAddress(String address) {
        Address = address;
        sharedPreferences.edit().putString("userAddress", Address).commit();
    }

    public void setGender(String gender) {
        Gender = gender;
        sharedPreferences.edit().putString("userGender", Gender).commit();
    }

    public void setAge(String age) {
        Age = age;
        sharedPreferences.edit().putString("userAge", Age).commit();
    }

    public String getPoliceID() {
        policeID = sharedPreferences.getString("policeID", "");
        return policeID;
    }

    public void setPoliceID(String policeID) {

        this.policeID = policeID;
        sharedPreferences.edit().putString("policeID", policeID).commit();

    }

    //session delete
    public void remove() {
        sharedPreferences.edit().clear().commit();
    }
}
