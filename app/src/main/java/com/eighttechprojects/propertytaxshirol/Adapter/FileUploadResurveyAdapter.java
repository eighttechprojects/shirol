package com.eighttechprojects.propertytaxshirol.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.eighttechprojects.propertytaxshirol.BuildConfig;
import com.eighttechprojects.propertytaxshirol.Model.FileUploadViewModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import java.io.File;
import java.util.ArrayList;

public class FileUploadResurveyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // GoogleMapActivity
    Activity activity;
    // ArrayList File Upload View Model
    ArrayList<FileUploadViewModel> fileUploadViewModelArrayList;
    // Delete File List
    ArrayList<FileUploadViewModel> fileUploadViewModelDeleteArrayList = new ArrayList<>();
    // New Add File List
    ArrayList<FileUploadViewModel> fileUploadViewModelAddArrayList = new ArrayList<>();


    //------------------------------------ Constructor -------------------------------------------------------------------------------------------------------------------
    public FileUploadResurveyAdapter(Activity activity, ArrayList<FileUploadViewModel> fileUploadViewModelArrayList){
        this.activity = activity;
        this.fileUploadViewModelArrayList = fileUploadViewModelArrayList;
    }

    //------------------------------------ On Create View HOLDER -------------------------------------------------------------------------------------------------------------------
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return null;
        return new FileUploadResurveyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.file_upload_view_recycle_view_layout1, parent, false));
    }

    //------------------------------------ Bind View Holder --------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // File Upload VIew Holder
        FileUploadResurveyViewHolder  fileUploadViewHolder = (FileUploadResurveyViewHolder) holder;
        // Get All File Data Here!
        FileUploadViewModel data = fileUploadViewModelArrayList.get(position);
        // File Name Set To RecycleView
        fileUploadViewHolder.file_upload_view_name.setText(data.getName());
        // When User Click On File!
        // File View Options -----------------------------------------------
        fileUploadViewHolder.resurvey_file_view_image_view.setOnClickListener(view -> {
            // true means it is form server here we get link

            if(data.isServerFile()){
                Uri uri = Uri.parse(data.getPath());
                activity.startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
            else{
                File file_upload = new File(data.getPath());
                final Uri uri = FileProvider.getUriForFile(activity.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file_upload);
                //final Uri uri = FileProvider.getUriForFile(activity.getApplicationContext(), "com.eighttechproject.eighttech.fileprovider", file_upload);
                Log.d("File View",uri.getPath());
                activity.grantUriPermission(activity.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                final Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, activity.getContentResolver().getType(uri)).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(intent);
            }

        });



        // Delete File Option ------------------------------------------------
        int pos = position;
        fileUploadViewHolder.resurvey_delete_image_view.setOnClickListener(view -> {
            // Show dialog box u want to delete file or not
            Utility.showYesNoDialogBox(activity, "Delete File", "Are you sure u want to delete " + data.getName() + " file?", yesDialogBox -> {
                yesDialogBox.dismiss();
                removeItem(pos);
            });
//            Utility.getOKCancelDialogBox(activity, "Delete", "Are you Sure u want to delete " + data.getName() + " file?", dialog -> removeItem(pos));
        });

    }


    //------------------------------------ File Upload View HOLDER --------------------------------------------------------------------------------------------------------------------------------
    public static class FileUploadResurveyViewHolder extends RecyclerView.ViewHolder{
        TextView file_upload_view_name;
        ImageView resurvey_file_view_image_view;
        ImageView resurvey_delete_image_view;

        FileUploadResurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_upload_view_name = itemView.findViewById(R.id.file_upload_view_name);
            resurvey_delete_image_view = itemView.findViewById(R.id.resurvey_delete_image_view);
            resurvey_file_view_image_view = itemView.findViewById(R.id.resurvey_file_view_image_view);
        }
    }
    //------------------------------------ Item Count --------------------------------------------------------------------------------------------------------------------------------
    @Override
    public int getItemCount() {
        return fileUploadViewModelArrayList.size();
    }

    //------------------------------------ Selected  File --------------------------------------------------------------------------------------------------------------------------------
    public ArrayList<FileUploadViewModel> getNewFile(){
        return fileUploadViewModelAddArrayList;
    }

    public ArrayList<FileUploadViewModel> getSelectedFile(){
        return fileUploadViewModelArrayList;
    }

    //------------------------------------ Delete File --------------------------------------------------------------------------------------------------------------------------------
    public ArrayList<FileUploadViewModel> getDeleteFile(){
        return fileUploadViewModelDeleteArrayList;
    }

    //------------------------------------ Remove Item --------------------------------------------------------------------------------------------------------------------------------
    private void removeItem(int position){
        // Store delete file into delete array!
        fileUploadViewModelDeleteArrayList.add(fileUploadViewModelArrayList.get(position));
        // delete file form current array!
        fileUploadViewModelArrayList.remove(fileUploadViewModelArrayList.get(position));
        notifyItemRemoved(position);
        notifyItemChanged(position);
    }

}

