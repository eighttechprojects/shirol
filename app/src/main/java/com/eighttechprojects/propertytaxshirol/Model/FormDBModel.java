package com.eighttechprojects.propertytaxshirol.Model;

public class FormDBModel {

    private String id;
    private String user_id;
    private String latitude;
    private String longitude;
    private String formData;

    private String isOnlineSave = "f";

    private String token;

//------------------------------------------------------- Getter ---------------------------------------------------------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getFormData() {
        return formData;
    }

    public String getIsOnlineSave() {
        return isOnlineSave;
    }

    public String getToken() {
        return token;
    }

//------------------------------------------------------- Setter ---------------------------------------------------------------------------------------------------------------------------


    public void setId(String id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public void setIsOnlineSave(String isOnlineSave) {
        this.isOnlineSave = isOnlineSave;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
