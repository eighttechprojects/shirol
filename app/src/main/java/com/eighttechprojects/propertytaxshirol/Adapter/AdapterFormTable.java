package com.eighttechprojects.propertytaxshirol.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eighttechprojects.propertytaxshirol.Activity.Form.FormActivity;
import com.eighttechprojects.propertytaxshirol.Model.FormTableModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import java.util.ArrayList;

public class AdapterFormTable extends RecyclerView.Adapter<AdapterFormTable.ViewHolder> {
    // Activity
    Activity mActivity;
    ArrayList<FormTableModel> formTableModels;
    // boolean
    boolean isViewMode = false;
    boolean isSno6Selected = false;

    String db_form_sp_building_type = "";
    String db_form_sp_building_use_type = "";

//------------------------------------------------------- Constructor -------------------------------------------------------------------------------------------------------------------------------------------------

    public AdapterFormTable(Activity mActivity, ArrayList<FormTableModel> formTableModels) {
        this.mActivity = mActivity;
        this.formTableModels = formTableModels;
    }

    public AdapterFormTable(Activity mActivity, ArrayList<FormTableModel> formTableModels, boolean isViewMode, boolean isSno6Selected) {
        this.mActivity = mActivity;
        this.formTableModels = formTableModels;
        this.isViewMode = isViewMode;
        this.isSno6Selected = isSno6Selected;
    }

//------------------------------------------------------- onCreate ViewHolder -------------------------------------------------------------------------------------------------------------------------------------------------

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_form_table_item_view, parent, false));
    }

//------------------------------------------------------- onBind ViewHolder -------------------------------------------------------------------------------------------------------------------------------------------------

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FormTableModel bin = formTableModels.get(position);
        // init
        init(holder,bin);
        holder.tvHeader.setText("" + (holder.getAdapterPosition() + 1));

        if(isViewMode){
            holder.btRemoveItem.setVisibility(View.GONE);
            holder.btEditItem.setVisibility(View.GONE);
        }
        else{
            holder.btRemoveItem.setVisibility(View.VISIBLE);
            holder.btEditItem.setVisibility(View.VISIBLE);
            // Button Remove Item
            holder.btRemoveItem.setOnClickListener(view -> {
                int pos = position;
                Utility.showYesNoDialogBox(mActivity,"Are you sure you want to remove this?", dialog -> {
                    formTableModels.remove(pos);
                    notifyItemRemoved(pos);
                    notifyDataSetChanged();
                });
            });
            // Button Edit Item
            holder.btEditItem.setOnClickListener(view -> {
                int pos1 = position;
                Utility.showYesNoDialogBox(mActivity,"Are you sure you want to edit this?", dialog -> {
                    EditItem(bin,pos1);
                });
            });

        }

    }

//------------------------------------------------------- init -------------------------------------------------------------------------------------------------------------------------------------------------

    @SuppressLint("SetTextI18n")
    private void init(ViewHolder holder, FormTableModel bin){
        // Sr No
        holder.sr_no.setText("" + (holder.getAdapterPosition() + 1));
        // Floor
        holder.floor.setText(Utility.getStringValue(bin.getFloor()));
        // Building Type
        holder.building_type.setText(Utility.getStringValue(bin.getBuilding_type()));
        // Building Use Type
        holder.building_use_type.setText(Utility.getStringValue(bin.getBuilding_use_type()));
        // Length
        holder.length.setText(Utility.getStringValue(bin.getLength()));
        // Height
        holder.height.setText(Utility.getStringValue(bin.getHeight()));
        // Area
        holder.area.setText(Utility.getStringValue(bin.getArea()));
        // Building Age
        holder.building_age.setText(Utility.getStringValue(bin.getBuilding_age()));

        if(isSno6Selected){
            holder.ll_table7.setVisibility(View.VISIBLE);
            // Annual Rent
            holder.annual_rent.setText(Utility.getStringValue(bin.getAnnual_rent()));
        }
        else{
            holder.ll_table7.setVisibility(View.GONE);
            holder.annual_rent.setText("");
        }

        // Tag No
        holder.tag_no.setText(Utility.getStringValue(bin.getTag_no()));
    }

//------------------------------------------------------- Get Item Count -------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public int getItemCount() {
        return formTableModels.size();
    }

    public ArrayList<FormTableModel> getFormTableModels(){
        return formTableModels;
    }

//------------------------------------------------------- View Holder -------------------------------------------------------------------------------------------------------------------------------------------------

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        TextView sr_no;
        TextView floor;
        TextView building_type;
        TextView building_use_type;
        TextView length;
        TextView height;
        TextView area;
        TextView building_age;
        TextView annual_rent;
        TextView tag_no;
        Button btRemoveItem;
        Button btEditItem;

        LinearLayout ll_table7;

        ViewHolder(@NonNull View v) {
            super(v);
            // Text View
            tvHeader          = v.findViewById(R.id.tvHeader);
            sr_no             = v.findViewById(R.id.form_table_sr_no);
            floor             = v.findViewById(R.id.form_table_floor);
            building_type     = v.findViewById(R.id.form_table_building_type);
            building_use_type = v.findViewById(R.id.form_table_building_use_type);
            length            = v.findViewById(R.id.form_table_length);
            height            = v.findViewById(R.id.form_table_height);
            area              = v.findViewById(R.id.form_table_area);
            building_age      = v.findViewById(R.id.form_table_building_age);
            annual_rent       = v.findViewById(R.id.form_table_annual_rent);
            tag_no            = v.findViewById(R.id.form_table_tag_no);
            // Button
            btRemoveItem      = v.findViewById(R.id.btRemoveItem);
            btEditItem        = v.findViewById(R.id.btEditItem);
            // Linear Layout
            ll_table7         = v.findViewById(R.id.ll_table7);

        }
    }

    private void EditItem(FormTableModel bin,int pos){

        Dialog fDB = new Dialog(mActivity);
        fDB.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fDB.setCancelable(false);
        fDB.setContentView(R.layout.dialogbox_form_table_item);
        fDB.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        // Exit Button
        Button btExit       = fDB.findViewById(R.id.dbExit);
        btExit.setOnClickListener(view -> fDB.dismiss());
        // Init Edit Text
        EditText sr_no             = fDB.findViewById(R.id.form_table_sr_no);
        EditText floor             = fDB.findViewById(R.id.form_table_floor);
        EditText length            = fDB.findViewById(R.id.form_table_length);
        EditText height            = fDB.findViewById(R.id.form_table_height);
        EditText area              = fDB.findViewById(R.id.form_table_area);
        EditText building_age      = fDB.findViewById(R.id.form_table_building_age);
        EditText annual_rent       = fDB.findViewById(R.id.form_table_annual_rent);
        EditText tag_no            = fDB.findViewById(R.id.form_table_tag_no);
        // Spinner
        Spinner building_type     = fDB.findViewById(R.id.form_sp_building_type);
        Spinner building_use_type = fDB.findViewById(R.id.form_sp_building_use_type);



        // fetch tv-Item
        sr_no.setText(Utility.getStringValue(bin.getSr_no()));
        floor.setText(Utility.getStringValue(bin.getFloor()));
        length.setText(Utility.getStringValue(bin.getLength()));
        height.setText(Utility.getStringValue(bin.getHeight()));
        area.setText(Utility.getStringValue(bin.getArea()));
        building_age.setText(Utility.getStringValue(bin.getBuilding_age()));
        annual_rent.setText(Utility.getStringValue(bin.getAnnual_rent()));
        tag_no.setText(Utility.getStringValue(bin.getTag_no()));


        // Building Type Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterBuildingType = ArrayAdapter.createFromResource(mActivity, R.array.sp_building_type,android.R.layout.simple_spinner_item);
        adapterBuildingType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building_type.setAdapter(adapterBuildingType);

        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getBuilding_type())) {
                switch (bin.getBuilding_type()) {
                    case "आर.सी.सी. पद्धतीची इमारत":
                        building_type.setSelection(0);
                        break;

                    case "लोडबेरिंग":
                        building_type.setSelection(1);
                        break;

                    case "दगड विटांचे चनुा किंवा सिमेंट वापरून उभारलेली इमारत":
                        building_type.setSelection(2);
                        break;

                    case "दगड विटांचे मातीची इमारत":
                        building_type.setSelection(3);
                        break;

                    case "झोपडी किंवा मातीची इमारत":
                        building_type.setSelection(4);
                        break;

                    case "खुली जागा":
                        building_type.setSelection(5);
                        break;

                    case "मनोरा तळ":
                        building_type.setSelection(6);
                        break;
                }
            }
        }

        building_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                db_form_sp_building_type = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // Building Use Type Spinner -----------------------------------------------------------------------------
        ArrayAdapter<CharSequence> adapterBuildingUseType = ArrayAdapter.createFromResource(mActivity, R.array.sp_building_use_type,android.R.layout.simple_spinner_item);
        adapterBuildingUseType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        building_use_type.setAdapter(adapterBuildingUseType);

        // set Data
        if(bin != null) {
            if (!Utility.isEmptyString(bin.getBuilding_type())) {
                switch (bin.getBuilding_type()) {
                    case "निवासी":
                        building_use_type.setSelection(0);
                        break;

                    case "वाणिज्य":
                        building_use_type.setSelection(1);
                        break;

                    case "औद्योगिक":
                        building_use_type.setSelection(2);
                        break;

                    case "शैक्षणिक":
                        building_use_type.setSelection(3);
                        break;

                    case "शासकीय इमारत":
                        building_use_type.setSelection(4);
                        break;

                    case "नगरपरिषद इमारत":
                        building_use_type.setSelection(5);
                        break;

                    case "मिश्र इमारत":
                        building_use_type.setSelection(6);
                        break;

                    case "धार्मिक":
                        building_use_type.setSelection(7);
                        break;

                    case "पार्किंग":
                        building_use_type.setSelection(8);
                        break;
                }
            }
        }
        building_use_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                db_form_sp_building_use_type = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});


        // Update Button
        Button updateItem = fDB.findViewById(R.id.dbAdd);
        updateItem.setText("Update");
        updateItem.setOnClickListener(view -> {
            FormTableModel f = new FormTableModel(
                    sr_no.getText().toString(),
                    floor.getText().toString(),
                    db_form_sp_building_type,
                    db_form_sp_building_use_type,
                    length.getText().toString(),
                    height.getText().toString(),
                    area.getText().toString(),
                    building_age.getText().toString(),
                    annual_rent.getText().toString(),
                    tag_no.getText().toString()
            );
            formTableModels.set(pos,f);
            notifyDataSetChanged();
            db_form_sp_building_type = "";
            db_form_sp_building_use_type = "";
            fDB.dismiss();
        });


        fDB.show();
    }

}





