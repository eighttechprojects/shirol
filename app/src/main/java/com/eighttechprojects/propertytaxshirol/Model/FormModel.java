package com.eighttechprojects.propertytaxshirol.Model;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;

public class FormModel {

    private FormFields form;
    private ArrayList<FormTableModel> detais;

//------------------------------------------------------- Constructor ---------------------------------------------------------------------------------------------------------------------------

    public FormModel() {}

//------------------------------------------------------- Getter ---------------------------------------------------------------------------------------------------------------------------


    public FormFields getForm() {
        return form;
    }

    public ArrayList<FormTableModel> getDetais() {
        return detais;
    }


//------------------------------------------------------- Setter ---------------------------------------------------------------------------------------------------------------------------


    public void setForm(FormFields form) {
        this.form = form;
    }

    public void setDetais(ArrayList<FormTableModel> detais) {
        this.detais = detais;
    }


}



