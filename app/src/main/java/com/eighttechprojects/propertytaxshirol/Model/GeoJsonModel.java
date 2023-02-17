package com.eighttechprojects.propertytaxshirol.Model;

public class GeoJsonModel {

    private String id;
    private String polygonID;
    private String LatLon;
    private String Form;

    private boolean isOnlineSave = false;


//------------------------------------------------------- Getter ---------------------------------------------------------------------------------------------------------------------------


    public String getId() {
        return id;
    }

    public String getPolygonID() {
        return polygonID;
    }

    public String getLatLon() {
        return LatLon;
    }

    public String getForm() {
        return Form;
    }

    public boolean isOnlineSave() {
        return isOnlineSave;
    }

    //------------------------------------------------------- Setter ---------------------------------------------------------------------------------------------------------------------------


    public void setId(String id) {
        this.id = id;
    }

    public void setPolygonID(String polygonID) {
        this.polygonID = polygonID;
    }

    public void setLatLon(String latLon) {
        LatLon = latLon;
    }

    public void setForm(String form) {
        Form = form;
    }

    public void setOnlineSave(boolean onlineSave) {
        isOnlineSave = onlineSave;
    }
}
