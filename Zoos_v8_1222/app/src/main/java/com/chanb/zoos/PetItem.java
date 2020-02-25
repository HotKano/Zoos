package com.chanb.zoos;

public class PetItem {

    private String petNo, petProfile, petName, petGender, petKind, petAge;

    public PetItem(String petNo, String petProfile, String petName, String petGender, String petKind, String petAge) {
        this.petNo = petNo;
        this.petProfile = petProfile;
        this.petName = petName;
        this.petGender = petGender;
        this.petKind = petKind;
        this.petAge = petAge;

    }

    public String getPetNo() {
        return petNo;
    }

    public void setPetNo(String petNo) {
        this.petNo = petNo;
    }

    public String getPetProfile() {
        return petProfile;
    }

    public void setPetProfile(String petProfile) {
        this.petProfile = petProfile;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetGender() {
        return petGender;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }

    public String getPetKind() {
        return petKind;
    }

    public void setPetKind(String petKind) {
        this.petKind = petKind;
    }

    public String getPetAge() {
        return petAge;
    }

    public void setPetAge(String petAge) {
        this.petAge = petAge;
    }


}
