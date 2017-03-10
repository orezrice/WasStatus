package com.whatstatus.Models;

/**
 * Created by Gal Halali on 10/03/2017.
 */
public class People {
    // Data members
    private String cardId;
    private String cardNumber;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String photo;
    private String rank;
    private int isPresentAndSafe;
    private int isPresentGlobaly;

    // C'tor
    public People(String cardId,
                  String cardNumber,
                  String firstName,
                  String lastName,
                  String phoneNumber,
                  String photo,
                  String rank,
                  int isPresentAndSafe,
                  int isPresentGlobaly) {
        this.cardId = cardId;
        this.cardNumber = cardNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
        this.rank = rank;
        this.isPresentAndSafe = isPresentAndSafe;
        this.isPresentGlobaly = isPresentGlobaly;
    }

    // Access Methods

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getIsPresentAndSafe() {
        return isPresentAndSafe;
    }

    public void setIsPresentAndSafe(int isPresentAndSafe) {
        this.isPresentAndSafe = isPresentAndSafe;
    }

    public int getIsPresentGlobaly() {
        return isPresentGlobaly;
    }

    public void setIsPresentGlobaly(int isPresentGlobaly) {
        this.isPresentGlobaly = isPresentGlobaly;
    }
}
