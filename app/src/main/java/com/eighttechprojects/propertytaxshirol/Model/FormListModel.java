package com.eighttechprojects.propertytaxshirol.Model;

public class FormListModel {

    private String id;
    private String form_id;

    private String fid;
    private String polygon_id;


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

    public String getFid() {
        return fid;
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

    public void setFid(String fid) {
        this.fid = fid;
    }
}
