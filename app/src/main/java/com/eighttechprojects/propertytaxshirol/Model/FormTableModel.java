package com.eighttechprojects.propertytaxshirol.Model;

public class FormTableModel {

    private String sr_no;
    private String floor;
    private String building_type;
    private String building_use_type;
    private String length;
    private String height;
    private String area;
    private String building_age;
    private String annual_rent;
    private String tag_no;

//------------------------------------------------------- Constructor ---------------------------------------------------------------------------------------------------------------------------

    public FormTableModel(String sr_no, String floor, String building_type, String building_use_type, String length, String height, String area, String building_age, String annual_rent, String tag_no) {
        this.sr_no = sr_no;
        this.floor = floor;
        this.building_type = building_type;
        this.building_use_type = building_use_type;
        this.length = length;
        this.height = height;
        this.area = area;
        this.building_age = building_age;
        this.annual_rent = annual_rent;
        this.tag_no = tag_no;
    }


//------------------------------------------------------- Getter ---------------------------------------------------------------------------------------------------------------------------

    public String getSr_no() {
        return sr_no;
    }

    public String getFloor() {
        return floor;
    }

    public String getBuilding_type() {
        return building_type;
    }

    public String getBuilding_use_type() {
        return building_use_type;
    }

    public String getLength() {
        return length;
    }

    public String getHeight() {
        return height;
    }

    public String getArea() {
        return area;
    }

    public String getBuilding_age() {
        return building_age;
    }

    public String getAnnual_rent() {
        return annual_rent;
    }

    public String getTag_no() {
        return tag_no;
    }


//------------------------------------------------------- Setter ---------------------------------------------------------------------------------------------------------------------------


    public void setSr_no(String sr_no) {
        this.sr_no = sr_no;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setBuilding_type(String building_type) {
        this.building_type = building_type;
    }

    public void setBuilding_use_type(String building_use_type) {
        this.building_use_type = building_use_type;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setBuilding_age(String building_age) {
        this.building_age = building_age;
    }

    public void setAnnual_rent(String annual_rent) {
        this.annual_rent = annual_rent;
    }

    public void setTag_no(String tag_no) {
        this.tag_no = tag_no;
    }
}
