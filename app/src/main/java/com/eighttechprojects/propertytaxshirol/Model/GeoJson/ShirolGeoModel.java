package com.eighttechprojects.propertytaxshirol.Model.GeoJson;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ShirolGeoModel {

    private String GISID;

    private ArrayList<LatLng> latLngs;

    public ShirolGeoModel(String GISID, ArrayList<LatLng> latLngs) {
        this.GISID = GISID;
        this.latLngs = latLngs;
    }

    public String getGISID() {
        return GISID;
    }

    public void setGISID(String GISID) {
        this.GISID = GISID;
    }

    public ArrayList<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(ArrayList<LatLng> latLngs) {
        this.latLngs = latLngs;
    }
}
