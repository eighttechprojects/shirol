package com.eighttechprojects.propertytaxshirol.Model;

public class GeoJsonModel {

    private String polygonStatus;
    private String id;
    private String polygonID;
    private String gisID;

    private String wardNo;
    private String LatLon;
    private String Form;
    private boolean isOnlineSave = false;


//------------------------------------------------------- Getter ---------------------------------------------------------------------------------------------------------------------------


    public String getWardNo() {
        return wardNo;
    }

    public String getId() {
        return id;
    }

    public String getPolygonID() {
        return polygonID;
    }

    public String getGisID() {
        return gisID;
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

    public String getPolygonStatus() {
        return polygonStatus;
    }

    //------------------------------------------------------- Setter ---------------------------------------------------------------------------------------------------------------------------


    public void setWardNo(String wardNo) {
        this.wardNo = wardNo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPolygonID(String polygonID) {
        this.polygonID = polygonID;
    }

    public void setGisID(String gisID) {
        this.gisID = gisID;
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

    public void setPolygonStatus(String polygonStatus) {
        this.polygonStatus = polygonStatus;
    }
}
