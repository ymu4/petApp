package Model;
import android.net.Uri;

import java.util.List;

public class Post {

    private String userId;
    private String petSpecies;

    private String petGender;
    private String petDescription;
    private long postTimestamp;
//    private double petLatitude; // Latitude of the pet's location
//    private double petLongitude; // Longitude of the pet's location
    private String city; // Latitude of the pet's location
    private String street; // Longitude of the pet's location
    private String imageUrls;
    // private String postStatus;
    private Boolean petInjured;

    // Constructor
    public Post() {

    }


    // Constructor
    public Post( String userId, String petSpecies,
                 String petGender, String petDescription,
                 long postTimestamp, String city,String street,
                 String imageUrls, /*String postStatus,*/ Boolean petInjured) {

        this.userId = userId;
        this.petSpecies = petSpecies;

        this.petGender = petGender;
        this.petDescription = petDescription;
        this.postTimestamp = postTimestamp;
//        this.petLatitude = petLatitude;
//        this.petLongitude = petLongitude;
        this.city = city;
        this.street = street;
        this.imageUrls = imageUrls;
        //this.postStatus = postStatus;
        this.petInjured = petInjured;
    }

    // Getters and setters



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPetSpecies() {
        return petSpecies;
    }

    public void setPetSpecies(String petSpecies) {
        this.petSpecies = petSpecies;
    }


    public String getPetGender() {
        return petGender;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }
//    public double getPetLatitude() {
//        return petLatitude;
//    }
//    public void setPetLatitude(double petLatitude) {
//        this.petLatitude = petLatitude;
//    }
//    public double getPetLongitude() {
//        return petLongitude;
//    }
//    public void setPetLongitude(double petLongitude) {
//        this.petLongitude = petLongitude;
//    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getStreet() {
        return street;

    }
    public void setStreet(String street) {
        this.street = street;
    }
//


    public String getPetDescription() {
        return petDescription;
    }

    public void setPetDescription(String petDescription) {
        this.petDescription = petDescription;
    }

    public long getPostTimestamp() {
        return postTimestamp;
    }

    public void setPostTimestamp(long postTimestamp) {
        this.postTimestamp = postTimestamp;
    }



    public String getImageUrls() {
        return imageUrls;
    }
    public void setImageUrls(String  imageUrls) {
        this.imageUrls = imageUrls;
    }
    //    public String getPostStatus() {
//        return postStatus;
//    }
//    public void setPostStatus(String postStatus) {
//        this.postStatus = postStatus;
//    }
    public Boolean getPetInjured() {
        return petInjured;
    }
    public void setPetInjured(Boolean petInjured) {
        this.petInjured = petInjured;
    }


}


