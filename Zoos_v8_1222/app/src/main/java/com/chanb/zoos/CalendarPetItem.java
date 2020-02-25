package com.chanb.zoos;

public class CalendarPetItem {

    private String petNo, petProfile, petName, petRace;

    public CalendarPetItem(String petNo, String petProfile, String petName, String petRace) {
        this.petNo = petNo;
        this.petProfile = petProfile;
        this.petName = petName;
        this.petRace = petRace;
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

    public String getPetRace() {
        return petRace;
    }


}
