package com.eighttechprojects.propertytaxshirol.Model;

public class FormDBModel {


    private String id;
    private String form_id;
    private String polygon_id;
    private String user_id;
    private String latitude;
    private String longitude;
    private String formData;
    private String isOnlineSave = "f";
    private String token;
    private String filePath;
    private String cameraPath;

//------------------------------------------------------- Getter ---------------------------------------------------------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getForm_id() {
        return form_id;
    }

    public String getPolygon_id() {
        return polygon_id;
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

    public String getFilePath() {
        return filePath;
    }

    public String getCameraPath() {
        return cameraPath;
    }

//------------------------------------------------------- Setter ---------------------------------------------------------------------------------------------------------------------------


    public void setId(String id) {
        this.id = id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public void setPolygon_id(String polygon_id) {
        this.polygon_id = polygon_id;
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

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setCameraPath(String cameraPath) {
        this.cameraPath = cameraPath;
    }
}
