package com.eighttechprojects.propertytaxshirol.Model;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;

public class FormModel {

    private FormFields form;
    private ArrayList<FormTableModel> detais;
//    private FormFields formFields;
//    private ArrayList<FormTableModel> form_detail;

    private CameraFileModel cameraFileModel;

    private FileModel fileModel;

//------------------------------------------------------- Constructor ---------------------------------------------------------------------------------------------------------------------------

    public FormModel() {}




//------------------------------------------------------- Getter ---------------------------------------------------------------------------------------------------------------------------


    public FormFields getForm() {
        return form;
    }

    public ArrayList<FormTableModel> getDetais() {
        return detais;
    }


    public FileModel getFileModel() {
        return fileModel;
    }

    public CameraFileModel getCameraFileModel() {
        return cameraFileModel;
    }


//------------------------------------------------------- Setter ---------------------------------------------------------------------------------------------------------------------------


    public void setForm(FormFields form) {
        this.form = form;
    }

    public void setDetais(ArrayList<FormTableModel> detais) {
        this.detais = detais;
    }


    public void setCameraFileModel(CameraFileModel cameraFileModel) {
        this.cameraFileModel = cameraFileModel;
    }

    public void setFileModel(FileModel fileModel) {
        this.fileModel = fileModel;
    }


}



