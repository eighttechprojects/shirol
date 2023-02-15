package com.eighttechprojects.propertytaxshirol.Model.GeoJson;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ShirolModel{

    private String GISID;

    private ArrayList<ArrayList<LatLng>> Coordinates;

    public String getGISID() {
        return GISID;
    }

    public void setGISID(String GISID) {
        this.GISID = GISID;
    }

    public ArrayList<ArrayList<LatLng>> getCoordinates() {
        return Coordinates;
    }

    public void setCoordinates(ArrayList<ArrayList<LatLng>> coordinates) {
        Coordinates = coordinates;
    }
}
