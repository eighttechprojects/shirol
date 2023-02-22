package com.eighttechprojects.propertytaxshirol.Model;

public class FormListModel {

    private String id;
    private String form_id;
    private String fid;
    private String polygon_id;
    private String form_number;
    private FormModel formModel;

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

    public String getForm_number() {
        return form_number;
    }

    public FormModel getFormModel() {
        return formModel;
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

    public void setForm_number(String form_number) {
        this.form_number = form_number;
    }

    public void setFormModel(FormModel formModel) {
        this.formModel = formModel;
    }
}

