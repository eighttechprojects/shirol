package com.eighttechprojects.propertytaxshirol.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eighttechprojects.propertytaxshirol.Model.FormListModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import java.util.ArrayList;

public class AdapterFormListView extends RecyclerView.Adapter<AdapterFormListView.ViewHolder> {

    // Context
    Context activity;
    // ArrayList
    ArrayList<FormListModel> formList;
    // Interface
    ItemClickListener itemClickListener;

//------------------------------------ Constructor -------------------------------------------------------------------------------------------------------------------

    public AdapterFormListView(Context activity, ArrayList<FormListModel> formList, ItemClickListener itemClickListener){
        this.activity = activity;
        this.formList = formList;
        this.itemClickListener = itemClickListener;
    }

//------------------------------------ onCreateViewHolder -------------------------------------------------------------------------------------------------------------------
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return null;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.file_upload_view_recycle_view_layout, parent, false));
    }

//------------------------------------ onBindViewHolder --------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Form Name
        holder.formName.setText(Utility.getStringValue(formList.get(position).getFid()));
    }

//------------------------------------ View HOLDER --------------------------------------------------------------------------------------------------------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView formName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            formName = itemView.findViewById(R.id.file_upload_view_name);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    itemClickListener.onItemClick(formList.get(position));
                }
            });
        }
    }

//------------------------------------ Item Count --------------------------------------------------------------------------------------------------------------------------------
    @Override
    public int getItemCount() {
        return formList.size();
    }

//------------------------------------ Interface --------------------------------------------------------------------------------------------------------------------------------

    public interface ItemClickListener{
        void onItemClick(FormListModel formListModel);
    }


}

