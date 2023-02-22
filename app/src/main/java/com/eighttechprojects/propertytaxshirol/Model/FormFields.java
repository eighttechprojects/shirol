package com.eighttechprojects.propertytaxshirol.Model;

import java.util.ArrayList;

public class FormFields {

    // default fields-----------------
    private String polygon_status;
    private String form_number;
    private String fid;
    private String polygon_id;

    private String lastKey;
    private String unique_number;
    private String form_id;
    private String user_id;
    private String latitude;
    private String longitude;
    private String created_on;
    private String version;

    private String property_images;
    private String plan_attachment;

    // form fields---------------------
    private String owner_name;
    private String old_property_no;
    private String new_property_no;
    private String property_name;
    private String property_address;
    private String property_user_type;
    private String property_user;
    private String resurvey_no;
    private String gat_no;
    private String zone;
    private String ward;
    private String mobile;
    private String email;
    private String aadhar_no;
    private String grid_no;
    private String gis_id;
    private String property_type;
    private String no_of_floor;
    private String property_release_date;
    private String build_permission;
    private String build_completion_form;
    private String metal_road;
    private String is_toilet_available;
    private String total_toilet;
    private String toilet_type;
    private String is_streetlight_available;
    private String is_water_line_available;
    private String total_water_line1;
    private String total_water_line2;
    private String water_use_type;
    private String solar_panel_available;
    private String solar_panel_type;
    private String rain_water_harvesting;

    private String form_status;

    private String form_comment;
    private String plot_area;
    private String property_area;
    private String total_area;

    private ArrayList<FormTableModel> detais;


//------------------------------------------------------- Getter ---------------------------------------------------------------------------------------------------------------------------


    public String getPolygon_status() {
        return polygon_status;
    }

    public String getLastKey() {
        return lastKey;
    }

    public String getUnique_number() {
        return unique_number;
    }

    public String getForm_status() {
        return form_status;
    }

    public String getForm_comment() {
        return form_comment;
    }

    public ArrayList<FormTableModel> getDetais() {
        return detais;
    }

    public String getForm_number() {
        return form_number;
    }

    public String getFid() {
        return fid;
    }

    public String getProperty_images() {
        return property_images;
    }

    public String getPlan_attachment() {
        return plan_attachment;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public String getOld_property_no() {
        return old_property_no;
    }

    public String getNew_property_no() {
        return new_property_no;
    }

    public String getProperty_name() {
        return property_name;
    }

    public String getProperty_address() {
        return property_address;
    }

    public String getProperty_user_type() {
        return property_user_type;
    }

    public String getProperty_user() {
        return property_user;
    }

    public String getResurvey_no() {
        return resurvey_no;
    }

    public String getGat_no() {
        return gat_no;
    }

    public String getZone() {
        return zone;
    }

    public String getWard() {
        return ward;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getAadhar_no() {
        return aadhar_no;
    }

    public String getGrid_no() {
        return grid_no;
    }

    public String getGis_id() {
        return gis_id;
    }

    public String getProperty_type() {
        return property_type;
    }

    public String getNo_of_floor() {
        return no_of_floor;
    }

    public String getProperty_release_date() {
        return property_release_date;
    }

    public String getBuild_permission() {
        return build_permission;
    }

    public String getBuild_completion_form() {
        return build_completion_form;
    }

    public String getMetal_road() {
        return metal_road;
    }

    public String getIs_toilet_available() {
        return is_toilet_available;
    }

    public String getTotal_toilet() {
        return total_toilet;
    }

    public String getToilet_type() {
        return toilet_type;
    }

    public String getIs_streetlight_available() {
        return is_streetlight_available;
    }

    public String getIs_water_line_available() {
        return is_water_line_available;
    }

    public String getTotal_water_line1() {
        return total_water_line1;
    }

    public String getTotal_water_line2() {
        return total_water_line2;
    }

    public String getWater_use_type() {
        return water_use_type;
    }

    public String getSolar_panel_available() {
        return solar_panel_available;
    }

    public String getSolar_panel_type() {
        return solar_panel_type;
    }

    public String getRain_water_harvesting() {
        return rain_water_harvesting;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getVersion() {
        return version;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getPolygon_id() {
        return polygon_id;
    }

    public String getForm_id() {
        return form_id;
    }

    public String getPlot_area() {
        return plot_area;
    }

    public String getProperty_area() {
        return property_area;
    }

    public String getTotal_area() {
        return total_area;
    }

//------------------------------------------------------- Setter ---------------------------------------------------------------------------------------------------------------------------


    public void setPolygon_status(String polygon_status) {
        this.polygon_status = polygon_status;
    }

    public void setLastKey(String lastKey) {
        this.lastKey = lastKey;
    }

    public void setUnique_number(String unique_number) {
        this.unique_number = unique_number;
    }

    public void setForm_status(String form_status) {
        this.form_status = form_status;
    }

    public void setForm_comment(String form_comment) {
        this.form_comment = form_comment;
    }

    public void setDetais(ArrayList<FormTableModel> detais) {
        this.detais = detais;
    }

    public void setForm_number(String form_number) {
        this.form_number = form_number;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public void setProperty_images(String property_images) {
        this.property_images = property_images;
    }

    public void setPlan_attachment(String plan_attachment) {
        this.plan_attachment = plan_attachment;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public void setOld_property_no(String old_property_no) {
        this.old_property_no = old_property_no;
    }

    public void setNew_property_no(String new_property_no) {
        this.new_property_no = new_property_no;
    }

    public void setProperty_name(String property_name) {
        this.property_name = property_name;
    }

    public void setProperty_address(String property_address) {
        this.property_address = property_address;
    }

    public void setProperty_user_type(String property_user_type) {
        this.property_user_type = property_user_type;
    }

    public void setProperty_user(String property_user) {
        this.property_user = property_user;
    }

    public void setResurvey_no(String resurvey_no) {
        this.resurvey_no = resurvey_no;
    }

    public void setGat_no(String gat_no) {
        this.gat_no = gat_no;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAadhar_no(String aadhar_no) {
        this.aadhar_no = aadhar_no;
    }

    public void setGrid_no(String grid_no) {
        this.grid_no = grid_no;
    }

    public void setGis_id(String gis_id) {
        this.gis_id = gis_id;
    }

    public void setProperty_type(String property_type) {
        this.property_type = property_type;
    }

    public void setNo_of_floor(String no_of_floor) {
        this.no_of_floor = no_of_floor;
    }

    public void setProperty_release_date(String property_release_date) {
        this.property_release_date = property_release_date;
    }

    public void setBuild_permission(String build_permission) {
        this.build_permission = build_permission;
    }

    public void setBuild_completion_form(String build_completion_form) {
        this.build_completion_form = build_completion_form;
    }

    public void setMetal_road(String metal_road) {
        this.metal_road = metal_road;
    }

    public void setIs_toilet_available(String is_toilet_available) {
        this.is_toilet_available = is_toilet_available;
    }

    public void setTotal_toilet(String total_toilet) {
        this.total_toilet = total_toilet;
    }

    public void setToilet_type(String toilet_type) {
        this.toilet_type = toilet_type;
    }

    public void setIs_streetlight_available(String is_streetlight_available) {
        this.is_streetlight_available = is_streetlight_available;
    }

    public void setIs_water_line_available(String is_water_line_available) {
        this.is_water_line_available = is_water_line_available;
    }

    public void setTotal_water_line1(String total_water_line1) {
        this.total_water_line1 = total_water_line1;
    }

    public void setTotal_water_line2(String total_water_line2) {
        this.total_water_line2 = total_water_line2;
    }

    public void setWater_use_type(String water_use_type) {
        this.water_use_type = water_use_type;
    }

    public void setSolar_panel_available(String solar_panel_available) {
        this.solar_panel_available = solar_panel_available;
    }

    public void setSolar_panel_type(String solar_panel_type) {
        this.solar_panel_type = solar_panel_type;
    }

    public void setRain_water_harvesting(String rain_water_harvesting) {
        this.rain_water_harvesting = rain_water_harvesting;
    }

    public void setPlot_area(String plot_area) {
        this.plot_area = plot_area;
    }

    public void setProperty_area(String property_area) {
        this.property_area = property_area;
    }

    public void setTotal_area(String total_area) {
        this.total_area = total_area;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }


    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public void setPolygon_id(String polygon_id) {
        this.polygon_id = polygon_id;
    }


    @Override
    public String toString() {
        return "FormFields{" +
                "form_number='" + form_number + '\'' +
                ", fid='" + fid + '\'' +
                ", polygon_id='" + polygon_id + '\'' +
                ", form_id='" + form_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", created_on='" + created_on + '\'' +
                ", version='" + version + '\'' +
                ", property_images='" + property_images + '\'' +
                ", plan_attachment='" + plan_attachment + '\'' +
                ", owner_name='" + owner_name + '\'' +
                ", old_property_no='" + old_property_no + '\'' +
                ", new_property_no='" + new_property_no + '\'' +
                ", property_name='" + property_name + '\'' +
                ", property_address='" + property_address + '\'' +
                ", property_user_type='" + property_user_type + '\'' +
                ", property_user='" + property_user + '\'' +
                ", resurvey_no='" + resurvey_no + '\'' +
                ", gat_no='" + gat_no + '\'' +
                ", zone='" + zone + '\'' +
                ", ward='" + ward + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", aadhar_no='" + aadhar_no + '\'' +
                ", grid_no='" + grid_no + '\'' +
                ", gis_id='" + gis_id + '\'' +
                ", property_type='" + property_type + '\'' +
                ", no_of_floor='" + no_of_floor + '\'' +
                ", property_release_date='" + property_release_date + '\'' +
                ", build_permission='" + build_permission + '\'' +
                ", build_completion_form='" + build_completion_form + '\'' +
                ", metal_road='" + metal_road + '\'' +
                ", is_toilet_available='" + is_toilet_available + '\'' +
                ", total_toilet='" + total_toilet + '\'' +
                ", toilet_type='" + toilet_type + '\'' +
                ", is_streetlight_available='" + is_streetlight_available + '\'' +
                ", is_water_line_available='" + is_water_line_available + '\'' +
                ", total_water_line1='" + total_water_line1 + '\'' +
                ", total_water_line2='" + total_water_line2 + '\'' +
                ", water_use_type='" + water_use_type + '\'' +
                ", solar_panel_available='" + solar_panel_available + '\'' +
                ", solar_panel_type='" + solar_panel_type + '\'' +
                ", rain_water_harvesting='" + rain_water_harvesting + '\'' +
                ", plot_area='" + plot_area + '\'' +
                ", property_area='" + property_area + '\'' +
                ", total_area='" + total_area + '\'' +
                '}';
    }
}


//    owner_name
//    old_property_no
//    new_property_no
//    property_name
//    property_address
//    property_user_type
//    property_user
//    no_of_floor
//    resurvey_no
//    gat_no
//    zone
//    ward
//    mobile
//    email
//    aadhar_no
//    grid_no
//    gis_id
//    property_type
//    property_release_date
//    build_permission
//    build_completion_form
//    metal_road
//    is_toilet_available
//    total_toilet
//    toilet_type
//    is_streetlight_available
//    is_water_line_available
//    total_water_line1
//    total_water_line2
//    water_use_type
//    solar_panel_available
//    solar_panel_type
//    rain_water_harvesting
//    plot_area
//    property_area
//    total_area