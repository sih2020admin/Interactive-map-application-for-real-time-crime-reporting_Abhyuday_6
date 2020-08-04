package com.example.hackathonoriginal.Databases;

public class DatabaseHelper {
    String PhoneNo, FullName, AdharCard, Address, Gender, Age, profileURL;

    public DatabaseHelper(String phone, String dFullName, String dAdharCard, String dAddress, String dGender, String dAge,String pro) {
        this.PhoneNo = phone;
        this.FullName = dFullName;
        this.AdharCard = dAdharCard;
        this.Address = dAddress;
        this.Gender = dGender;
        this.Age = dAge;
        this.profileURL=pro;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public String getFullName() {
        return FullName;
    }

    public String getAdharCard() {
        return AdharCard;
    }

    public String getAddress() {
        return Address;
    }

    public String getGender() {
        return Gender;
    }

    public String getAge() {
        return Age;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public void setAdharCard(String adharCard) {
        AdharCard = adharCard;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public void setAge(String age) {
        Age = age;
    }
}
