package com.eighttechprojects.propertytaxshirol.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.eighttechprojects.propertytaxshirol.BuildConfig;
import com.eighttechprojects.propertytaxshirol.Model.FileUploadViewModel;
import com.eighttechprojects.propertytaxshirol.R;

import java.io.File;
import java.util.ArrayList;

public class FileUploadViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Context
    Context activity;
    // ArrayList File Upload View Model
    ArrayList<FileUploadViewModel> fileUploadViewModelArrayList;

    //------------------------------------ Constructor -------------------------------------------------------------------------------------------------------------------

    public FileUploadViewAdapter(Context activity, ArrayList<FileUploadViewModel> fileUploadViewModelArrayList){
        this.activity = activity;
        this.fileUploadViewModelArrayList = fileUploadViewModelArrayList;
    }

    //------------------------------------ On Create View HOLDER -------------------------------------------------------------------------------------------------------------------
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // return null;
        return new FileUploadViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.file_upload_view_recycle_view_layout, parent, false));
    }

    //------------------------------------ Bind View Holder --------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // File Upload VIew Holder
        FileUploadViewHolder fileUploadViewHolder = (FileUploadViewHolder) holder;
        // Get All File Data Here!
        FileUploadViewModel data = fileUploadViewModelArrayList.get(position);
        // File Name Set To RecycleView
        fileUploadViewHolder.file_upload_view_name.setText(data.getName());

        // When User Click On File!
        fileUploadViewHolder.itemView.setOnClickListener(view -> {
            // true means it is form server here we get link
            if(data.isServerFile()){
                Uri uri = Uri.parse(data.getPath());
                activity.startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
            else
            {
                File file_upload = new File(data.getPath());
                final Uri uri = FileProvider.getUriForFile(activity.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file_upload);
                Log.d("File View",uri.getPath());
                activity.grantUriPermission(activity.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                final Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, activity.getContentResolver().getType(uri)).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(intent);
            }

        });

    }

    //------------------------------------ File Upload View HOLDER --------------------------------------------------------------------------------------------------------------------------------
    public static class FileUploadViewHolder extends RecyclerView.ViewHolder{
        TextView file_upload_view_name;

        FileUploadViewHolder(@NonNull View itemView) {
            super(itemView);
            file_upload_view_name = itemView.findViewById(R.id.file_upload_view_name);
        }
    }
    //------------------------------------ Item Count --------------------------------------------------------------------------------------------------------------------------------
    @Override
    public int getItemCount() {
        return fileUploadViewModelArrayList.size();
    }

}
