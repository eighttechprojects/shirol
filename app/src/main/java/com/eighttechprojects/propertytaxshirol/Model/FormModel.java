package com.eighttechprojects.propertytaxshirol.Model;

import java.util.ArrayList;

public class FormModel {

    private FormFields formFields;
    private ArrayList<FormTableModel> form_detail;

//------------------------------------------------------- Constructor ---------------------------------------------------------------------------------------------------------------------------

    public FormModel() {}

    public FormModel(FormFields formFields, ArrayList<FormTableModel> form_detail) {
        this.formFields = formFields;
        this.form_detail = form_detail;
    }


//------------------------------------------------------- Getter ---------------------------------------------------------------------------------------------------------------------------

    public FormFields getFormFields() {
        return formFields;
    }

    public ArrayList<FormTableModel> getForm_detail() {
        return form_detail;
    }

//------------------------------------------------------- Setter ---------------------------------------------------------------------------------------------------------------------------


    public void setFormFields(FormFields formFields) {
        this.formFields = formFields;
    }

    public void setForm_detail(ArrayList<FormTableModel> form_detail) {
        this.form_detail = form_detail;
    }

}



