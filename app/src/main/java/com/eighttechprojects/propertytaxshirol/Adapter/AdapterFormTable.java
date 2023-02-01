package com.eighttechprojects.propertytaxshirol.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

//------------------------------------------------------- Constructor -------------------------------------------------------------------------------------------------------------------------------------------------

    public AdapterFormTable(Activity mActivity, ArrayList<FormTableModel> formTableModels) {
        this.mActivity = mActivity;
        this.formTableModels = formTableModels;
    }

    public AdapterFormTable(Activity mActivity, ArrayList<FormTableModel> formTableModels, boolean isViewMode) {
        this.mActivity = mActivity;
        this.formTableModels = formTableModels;
        this.isViewMode = isViewMode;
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
        }
        else{
            holder.btRemoveItem.setVisibility(View.VISIBLE);
            // Button Remove Item
            holder.btRemoveItem.setOnClickListener(view -> {
                int pos = position;
                Utility.showYesNoDialogBox(mActivity,"Are you sure you want to remove this?", dialog -> {
                    formTableModels.remove(pos);
                    notifyItemRemoved(pos);
                    notifyDataSetChanged();
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
        // Annual Rent
        holder.annual_rent.setText(Utility.getStringValue(bin.getAnnual_rent()));
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

        }
    }

}





