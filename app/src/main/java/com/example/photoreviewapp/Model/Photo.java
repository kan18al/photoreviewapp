package com.example.photoreviewapp.Model;

import java.io.Serializable;
import java.util.Date;

public class Photo implements Serializable {

    public String id;
    public String photo;
    public Double latitude;
    public Double longitude;
    public String comment;
    public String EmailUser;
    public String avaUserUri;
    public Date currentDate;

    public Photo() {
    }

    public Photo(String id, String photo, Double latitude, Double longitude, String comment, String EmailUser, String avaUserUri, Date currentDate) {
        this.id = id;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;
        this.EmailUser = EmailUser;
        this.avaUserUri = avaUserUri;
        this.currentDate = currentDate;
    }
}
