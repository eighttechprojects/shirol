package com.eighttechprojects.propertytaxshirol.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eighttechprojects.propertytaxshirol.Model.FormFields;
import com.eighttechprojects.propertytaxshirol.Model.FormListModel;
import com.eighttechprojects.propertytaxshirol.Model.FormModel;
import com.eighttechprojects.propertytaxshirol.Model.FormTableModel;
import com.eighttechprojects.propertytaxshirol.R;
import com.eighttechprojects.propertytaxshirol.Utilities.SystemPermission;
import com.eighttechprojects.propertytaxshirol.Utilities.Utility;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AdapterFormListView extends RecyclerView.Adapter<AdapterFormListView.ViewHolder> {

    // TAG
    private static final String TAG = AdapterFormListView.class.getSimpleName();
    // Context
    Activity mActivity;
    // ArrayList
    ArrayList<FormListModel> formList;
    // Interface
    ItemClickListener itemClickListener;

//------------------------------------ Constructor -------------------------------------------------------------------------------------------------------------------

    public AdapterFormListView(Activity mActivity, ArrayList<FormListModel> formList, ItemClickListener itemClickListener){
        this.mActivity = mActivity;
        this.formList = formList;
        this.itemClickListener = itemClickListener;
    }

//------------------------------------ onCreateViewHolder -------------------------------------------------------------------------------------------------------------------
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return null;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_formview_layout, parent, false));
    }

//------------------------------------ onBindViewHolder --------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Form Name
        holder.formName.setText(Utility.getStringValue(formList.get(position).getFormModel().getForm().getNew_property_no()));

        // Name Click
        holder.formName.setOnClickListener(view -> {
            if(position != RecyclerView.NO_POSITION){
                itemClickListener.onItemClick(formList.get(position));
            }
        });

        // Download Image Click
        holder.imgDownload.setOnClickListener(view -> {
            if(formList.get(position).getFormModel() != null){
                try{
                    generateFormPDF(formList.get(position).getFormModel());
                }
                catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }
        });

    }

//------------------------------------ View HOLDER --------------------------------------------------------------------------------------------------------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView formName;
        ImageView imgDownload;

        ViewHolder(@NonNull View v) {
            super(v);
            formName    = v.findViewById(R.id.txtName);
            imgDownload = v.findViewById(R.id.imgDownload);

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

//------------------------------------ PDF Generate --------------------------------------------------------------------------------------------------------------------------------

    private File generatePDFFolder(String PDFName){
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/PropertyTaxShirol" + "/PDF/" + PDFName.split("-")[0], PDFName + ".pdf");
        if (!pdfFile.exists()) {
            File bl = new File(Environment.getExternalStorageDirectory() + "/PropertyTaxShirol/" + "/PDF/" + "/"+PDFName.split("-")[0]+"/");
            boolean dirs = bl.mkdirs();
        }
        else{
            boolean file_delete = pdfFile.delete();
        }
        return pdfFile;
    }

    private void generateFormPDF(FormModel formModel){
        int pageWidth = 700;
        int pageHeight = 1169;
        int pageNumber1 = 1;
        int pageNumber2 = 2;
        int midWidth = (pageWidth/2);

        int rectLeft = (midWidth/4);
        int rectRight =  (pageWidth - midWidth/4);
        int rectBottom = pageHeight - 40;

        try{

            if(SystemPermission.isInternalStorage(mActivity)){
                // do work
                Utility.showYesNoDialogBox(mActivity, "PDF Download", "You want to Download PDF?", okDialogBox -> {

                    if(!Utility.isEmptyString(formModel.getForm().getNew_property_no())){
                        FormFields bin = formModel.getForm();
                        ArrayList<FormTableModel> details = formModel.getDetais();
                        String[] str = formModel.getForm().getNew_property_no().split("/");
                        String PDFName = str[0] + "-"+str[1];

                        //  Created PDF Document -----------
                        PdfDocument pdfDocument = new PdfDocument();
                        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(pageWidth,pageHeight,pageNumber1).create();
                        PdfDocument.Page page1 = pdfDocument.startPage(pageInfo1);

                        // Canvas -------------
                        Canvas c = page1.getCanvas();
                        // Paint 1
                        Paint p = new Paint();
                        p.setTextAlign(Paint.Align.CENTER);
                        p.setTextSize(15);
                        p.setColor(Color.BLACK);
                        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                        // Paint 2
                        Paint p2 = new Paint();
                        p2.setTextAlign(Paint.Align.LEFT);
                        p2.setTextSize(11);
                        p2.setColor(Color.BLACK);
                        p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

                        // Image Add To PDF!
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inScaled = false;
                        Bitmap pdfLogoBitmap = BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.pdf_logo4);
//                        Bitmap bitmap = pdfLogoBitmap.copy(Bitmap.Config.ARGB_8888, true);
//                        bitmap.setPixel(10,10, Color.TRANSPARENT);
                        Bitmap scalePdfLogoBitmap = Bitmap.createScaledBitmap(pdfLogoBitmap,90,90,true);
                        c.drawBitmap(scalePdfLogoBitmap,rectLeft - 5,20,null);

                        c.drawBitmap(scalePdfLogoBitmap,rectRight - 80,20,null);
                        // main Header
                        int topHeight = 60;
                        String mainHeader = "शिरोळ नागरपरिषेद , शिरोळ";
                        c.drawText(mainHeader,midWidth,topHeight,p);

                        // sub Header
                        int subHeaderHeight = topHeight + 15;
                        p.setTextSize(10);
                        String subHeader = "मालमत्ता कर आकारणी मूल्यांकन तपासणी तत्का";
                        c.drawText(subHeader,midWidth,subHeaderHeight,p);

                        // subHeader 1
                        int subHeader1Height = subHeaderHeight + 15;
                        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        String subHeader1  = "(महाराष्ट्रा नगरपरिषद , नगरपंचायती आणि औद्योगिक नगरी अधिनियम १९६५ चे कलम ११५ (२) अन्व्ये)";
                        c.drawText(subHeader1,midWidth,subHeader1Height,p);

                        // subHeader 2
                        int subHeader2Height = subHeader1Height + 15;
                        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        String subHeader2 = "जुनी / निवन मिळतक";
                        c.drawText(subHeader2,midWidth,subHeader2Height,p);


                        // Rectangle
                        p.setStyle(Paint.Style.STROKE);
                        c.drawRect(rectLeft,subHeader2Height + 30, rectRight,rectBottom - 40,p);

                        int rectTop = subHeader2Height + 30;

                        // SNo. 1
                        int sno1Height = rectTop + 20;
                        c.drawText("मिळकत धारकाचे नाव :-",rectLeft + 10, sno1Height,p2);

                        // Draw Horizontal Line
                        int lineHeight1 = sno1Height + 20;
                        c.drawLine(rectLeft,lineHeight1,rectRight,lineHeight1,p);

                        // Value 1
                        c.drawText(Utility.getStringValue(bin.getOwner_name()),rectLeft + 160,sno1Height,p2);

                        // SNo. 2
                        int sno2Height = lineHeight1 + 20;
                        c.drawText("जुना मिळकत क्र. :-",rectLeft + 10, sno2Height,p2);

                        // Draw Horizontal Line
                        int lineHeight2 = sno2Height + 20;
                        c.drawLine(rectLeft,lineHeight2,rectRight,lineHeight2,p);

                        // Draw Veritcal Line
                        int lineVertical1 = rectLeft + 150  + 150;
                        c.drawLine(lineVertical1 ,lineHeight1,lineVertical1,lineHeight2,p);

                        // Value 2
                        c.drawText(Utility.getStringValue(bin.getOld_property_no()),rectLeft + 160,sno2Height,p2);


                        // SNo. 3
                        c.drawText("नविन मिळकत क्र. :-",lineVertical1 + 10, sno2Height,p2);
                        c.drawLine(lineVertical1 + 100 ,lineHeight1,lineVertical1 + 100,lineHeight2,p);

                        // Value 3
                        c.drawText(Utility.getStringValue(bin.getNew_property_no()),lineVertical1 + 110,sno2Height,p2);


                        // Sno. 4
                        int sno4Height = lineHeight2 + 20;
                        c.drawText("मिळकतीचे नाव :-",rectLeft + 10, sno4Height,p2);

                        // Draw Horizontal Line
                        int lineHeight4 = sno4Height + 20;
                        c.drawLine(rectLeft,lineHeight4,rectRight,lineHeight4,p);

                        // Value 4
                        c.drawText(Utility.getStringValue(bin.getProperty_name()),rectLeft + 160,sno4Height,p2);


                        // Sno. 5
                        int sno5Height = lineHeight4 + 20;
                        c.drawText("पत्ता / भागाचे नाव :-",rectLeft + 10, sno5Height,p2);

                        // Draw Horizontal Line
                        int lineHeight5 = sno5Height + 20;
                        c.drawLine(rectLeft,lineHeight5,rectRight,lineHeight5,p);

                        // Value 5
                        c.drawText(Utility.getStringValue(bin.getProperty_address()),rectLeft + 160,sno5Height,p2);


                        // Sno. 6
                        int sno6Height = lineHeight5 + 20;
                        c.drawText("वापरकर्ता :-",rectLeft + 10, sno6Height,p2);

                        // Draw Horizontal Line
                        int lineHeight6 = sno6Height + 20;
                        c.drawLine(rectLeft,lineHeight6,rectRight,lineHeight6,p);

                        // Value 6
                        c.drawText(Utility.getStringValue(bin.getProperty_user_type()),rectLeft + 160,sno6Height,p2);

                        // Sno. 7
                        int sno7Height = lineHeight6 + 20;
                        c.drawText("भोगवटदाराचे नाव :-",rectLeft + 10, sno7Height,p2);

                        // Draw Horizontal Line
                        int lineHeight7 = sno7Height + 20;
                        c.drawLine(rectLeft,lineHeight7,rectRight,lineHeight7,p2);

                        // Value 7
                        c.drawText(Utility.getStringValue(bin.getProperty_user()),rectLeft + 160,sno7Height,p2);


                        // Sno. 8
                        int sno8Height = lineHeight7 + 20;
                        c.drawText("सि.स.नं. :-",rectLeft + 10, sno8Height,p2);

                        // Draw Horizontal Line
                        int lineHeight8 = sno8Height + 20;
                        c.drawLine(rectLeft,lineHeight8,rectRight,lineHeight8,p2);

                        // Value 8
                        c.drawText(Utility.getStringValue(bin.getResurvey_no()),rectLeft + 160,sno8Height,p2);


                        // Sno. 9
                        c.drawText("गट नं. :-",lineVertical1 + 50 + 10, sno8Height,p2);
                        // Draw to Vertical Line
                        // Start Line
                        c.drawLine(lineVertical1 + 50 ,lineHeight7,lineVertical1 + 50,lineHeight8,p);
                        // End Line
                        c.drawLine(lineVertical1 + 100 ,lineHeight7,lineVertical1 + 100,lineHeight8,p);

                        // Value 9
                        c.drawText(Utility.getStringValue(bin.getGat_no()),lineVertical1 + 110,sno8Height,p2);


                        // Sno. 10
                        int sno10Height = lineHeight8 + 20;
                        c.drawText("झोन क्र. :-",rectLeft + 10, sno10Height,p2);

                        // Draw Horizontal Line
                        int lineHeight10 = sno10Height + 20;
                        c.drawLine(rectLeft,lineHeight10,rectRight,lineHeight10,p2);

                        // Value 10
                        c.drawText(Utility.getStringValue(bin.getZone()),rectLeft + 160,sno10Height,p2);


                        // Sno. 11
                        c.drawText("वार्ड क्र. :-",lineVertical1 + 50 + 10, sno10Height,p2);
                        // Start Line
                        c.drawLine(lineVertical1 + 50 ,lineHeight8,lineVertical1 + 50,lineHeight10,p);
                        // End Line
                        c.drawLine(lineVertical1 + 100 ,lineHeight8,lineVertical1 + 100,lineHeight10,p);

                        // Value 11
                        c.drawText(Utility.getStringValue(bin.getWard()),lineVertical1 + 110,sno10Height,p2);


                        // Sno. 12
                        int sno12Height = lineHeight10 + 20;
                        c.drawText("मोबाईल क्र. :-",rectLeft + 10, sno12Height,p2);

                        // Draw Horizontal Line
                        int lineHeight12 = sno12Height + 20;
                        c.drawLine(rectLeft,lineHeight12,rectRight,lineHeight12,p2);

                        // Value 12
                        c.drawText(Utility.getStringValue(bin.getMobile()),rectLeft + 160,sno12Height,p2);


                        // Sno. 13
                        c.drawText("मेल आय डी :-",lineVertical1 + 30 + 10, sno12Height,p2);
                        // Start Line
                        c.drawLine(lineVertical1 + 30 ,lineHeight10,lineVertical1 + 30,lineHeight12,p);
                        // End Line
                        c.drawLine(lineVertical1 + 100 ,lineHeight10,lineVertical1 + 100,lineHeight12,p);

                        // Value 13
                        c.drawText(Utility.getStringValue(bin.getEmail()),lineVertical1 + 110,sno12Height,p2);


                        // Sno. 14
                        int sno14Height = lineHeight12 + 20;
                        c.drawText("मिळकतधारकाचे आधार क्र. :-",rectLeft + 10, sno14Height,p2);

                        // Draw Horizontal Line
                        int lineHeight14 = sno14Height + 20;
                        c.drawLine(rectLeft,lineHeight14,rectRight,lineHeight14,p2);

                        // Value 14
                        c.drawText(Utility.getStringValue(bin.getAadhar_no()),rectLeft + 160,sno14Height,p2);

//
//                    // Sno. 15
//                    int sno15Height = lineHeight14 + 20;
//                    c.drawText("जी.आय.एस.आय.डी. :-",rectLeft + 10, sno15Height,p2);
//
//                    // Draw Horizontal Line
//                    int lineHeight15 = sno15Height + 20;
//                    c.drawLine(rectLeft,lineHeight15,rectRight,lineHeight15,p2);
//
//
//                    // Value 15
//                    c.drawText(Utility.getStringValue(""),rectLeft + 160,sno15Height,p2);
//

                        // Sno. 16
                        int sno16Height = sno14Height + 40;
                        c.drawText("जी.आय.एस.आय.डी. :-",rectLeft + 10, sno16Height,p2);

                        // Draw Horizontal Line
                        int lineHeight16 = sno16Height + 20;
                        c.drawLine(rectLeft,lineHeight16,rectRight,lineHeight16,p2);

                        // Value 16
                        c.drawText(Utility.getStringValue(bin.getGis_id()),rectLeft + 160,sno16Height,p2);


                        // Sno. 17
                        int sno17Height = sno16Height + 40;
                        c.drawText("मिळकतीचे वर्णन :-",rectLeft + 10, sno17Height,p2);

                        // Draw Horizontal Line
                        int lineHeight17 = sno17Height + 20;
                        c.drawLine(rectLeft,lineHeight17,rectRight,lineHeight17,p2);

                        // Value 17
                        c.drawText(Utility.getStringValue(bin.getProperty_type()),rectLeft + 160,sno17Height,p2);

                        // Sno. 17.1
                        // Draw Veritcal Line
                        c.drawLine(lineVertical1 ,lineHeight16,lineVertical1,lineHeight17,p);

                        c.drawText("No of Floors :-",lineVertical1 + 10, sno17Height,p2);
                        c.drawLine(lineVertical1 + 100 ,lineHeight16,lineVertical1 + 100,lineHeight17,p);

                        // Value 17.1
                        c.drawText(Utility.getStringValue(bin.getNo_of_floor()),lineVertical1 + 110,sno17Height,p2);

                        // Sno. 18
                        int sno18Height = sno17Height + 40;
                        String sno18 =  "मिळकतीचे बांधकाम पूर्ण झाल्यची \n दिनांक / वर्ष :-";
                        for(String no18 : sno18.split("\n")){
                            c.drawText(no18,rectLeft + 10, sno18Height,p2);
                            sno18Height += p2.descent() - p2.ascent();
                        }

                        // Draw Horizontal Line
                        int lineHeight18 = sno18Height + 20;
                        c.drawLine(rectLeft,lineHeight18,rectRight,lineHeight18,p2);


                        // Value 18
                        c.drawText(Utility.getStringValue(bin.getProperty_release_date()),rectLeft + 160,sno18Height - 20,p2);


                        // Sno. 19
                        int sno19Height = sno18Height + 40;
                        c.drawText("बांधकाम परवानगी घेतली आहे का?",rectLeft + 10, sno19Height,p2);

                        // Draw Horizontal Line
                        int lineHeight19 = sno19Height + 20;
                        c.drawLine(rectLeft,lineHeight19,rectRight,lineHeight19,p2);

                        // Value 19
                        c.drawText(Utility.getStringValue(bin.getBuild_permission()),rectLeft + 160,sno19Height,p2);


                        // Sno. 20
                        c.drawText("बांधकाम पूर्णत्वाचा दाखला घतेला आहे का?",lineVertical1 - 40, sno19Height,p2);
                        // Start Line
                        c.drawLine(lineVertical1 - 50 ,lineHeight18,lineVertical1 - 50,lineHeight19,p);
                        // End Line
                        c.drawLine(lineVertical1 + 140 ,lineHeight18,lineVertical1 + 140,lineHeight19,p);

                        // Draw Vertical Line
                        c.drawLine(rectLeft + 150,rectTop,rectLeft + 150,lineHeight19,p);


                        // Value 20
                        c.drawText(Utility.getStringValue(bin.getBuild_completion_form()),lineVertical1 + 150,sno19Height,p2);


                        // Sno. 21  -------------------------------
                        int sno21Height = sno19Height + 40;
                        p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        c.drawText("सोयी / सुविधा ",midWidth - (p2.descent() - p2.ascent()), sno21Height,p2);


                        p2.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        p2.setTextAlign(Paint.Align.LEFT);
                        // Draw Horizontal Line
                        int lineHeight21 = sno21Height + 20;
                        c.drawLine(rectLeft,lineHeight21,rectRight,lineHeight21,p2);

                        // Draw Vertical Line
                        c.drawLine(rectLeft + 150,lineHeight21,rectLeft + 150,rectBottom - 40,p);


                        // Sno. 22
                        int sno22Height = sno21Height + 40;
                        c.drawText("पोच मार्ग :-",rectLeft + 10, sno22Height,p2);

                        // Draw Horizontal Line
                        int lineHeight22 = sno22Height + 20;
                        c.drawLine(rectLeft,lineHeight22,rectRight,lineHeight22,p2);

                        // Value 22
                        c.drawText(Utility.getStringValue(bin.getMetal_road()),rectLeft + 160,sno22Height,p2);

                        // Sno. 23
                        int sno23Height = sno22Height + 40;
                        c.drawText("वैयक्तिक शौचालय आहे का ? :-",rectLeft + 10, sno23Height,p2);

                        // Draw Horizontal Line
                        int lineHeight23 = sno23Height + 20;
                        c.drawLine(rectLeft,lineHeight23,rectRight,lineHeight23,p2);

                        // Value 23
                        c.drawText(Utility.getStringValue(bin.getIs_toilet_available()),rectLeft + 160,sno23Height,p2);


                        // Sno. 24
                        int sno24Height = sno23Height + 40;
                        c.drawText("एकूण संख्या :-",rectLeft + 10, sno24Height,p2);

                        // Draw Horizontal Line
                        int lineHeight24 = sno24Height + 20;
                        c.drawLine(rectLeft,lineHeight24,rectRight,lineHeight24,p2);

                        // Value 24
                        c.drawText(Utility.getStringValue(bin.getTotal_toilet()),rectLeft + 160,sno24Height,p2);


                        // Sno. 25
                        c.drawText("शौचालय प्रकार :-",lineVertical1 + 20 + 10, sno24Height,p2);
                        // Start Line
                        c.drawLine(lineVertical1 + 20 ,lineHeight23,lineVertical1 + 20,lineHeight24,p);
                        // End Line
                        c.drawLine(lineVertical1 + 100 ,lineHeight23,lineVertical1 + 100,lineHeight24,p);

                        // Value 25
                        c.drawText(Utility.getStringValue(bin.getToilet_type()),lineVertical1 + 110,sno24Height,p2);


                        // Sno. 26
                        int sno26Height = sno24Height + 40;
                        c.drawText("दिवाबत्ती सोय आहे का ?",rectLeft + 10, sno26Height,p2);

                        // Draw Horizontal Line
                        int lineHeight26 = sno26Height + 20;
                        c.drawLine(rectLeft,lineHeight26,rectRight,lineHeight26,p2);

                        // Value 26
                        c.drawText(Utility.getStringValue(bin.getIs_streetlight_available()),rectLeft + 160,sno26Height,p2);


                        // Sno. 27
                        int sno27Height = sno26Height + 40;
                        c.drawText("नळ कनेक्शन आहे का ?",rectLeft + 10, sno27Height,p2);

                        // Draw Horizontal Line
                        int lineHeight27 = sno27Height + 20 + 20;
                        c.drawLine(rectLeft,lineHeight27 ,rectRight,lineHeight27 ,p2);

                        // Value 27
                        c.drawText(Utility.getStringValue(bin.getIs_water_line_available()),rectLeft + 160,sno27Height,p2);


                        // Sno. 28
                        c.drawText("एकूण संख्या :-",lineVertical1 - 60, sno27Height,p2);
                        // Start Line
                        c.drawLine(lineVertical1 - 70 ,lineHeight26,lineVertical1  - 70,lineHeight27,p);
                        // Vertical Line
                        c.drawLine(lineVertical1 - 70 ,lineHeight26 + 30,lineVertical1  + 70,lineHeight26 + 30,p);

                        // Value 28
                        c.drawText(Utility.getStringValue(bin.getTotal_water_line1()),lineVertical1,sno27Height,p2);

                        // Value 28.1
                        c.drawText(Utility.getStringValue(bin.getTotal_water_line2()),lineVertical1 - 20,sno27Height + 30,p2);


                        // Sno. 29
                        c.drawText("वापर :-",lineVertical1 + 80, sno27Height,p2);
                        // Start Line
                        c.drawLine(lineVertical1 + 70 ,lineHeight26,lineVertical1  + 70,lineHeight27,p);
                        // End Line
                        c.drawLine(lineVertical1 + 120,lineHeight26,lineVertical1 + 120,lineHeight27,p);

                        // Value 29
                        c.drawText(Utility.getStringValue(bin.getWater_use_type()),lineVertical1 + 130 ,sno27Height,p2);


                        // Sno. 30
                        int sno30Height = sno27Height + 40 + 20;
                        c.drawText("छतावरील सौर ऊर्जा प्रकल्प ?",rectLeft + 10, sno30Height,p2);

                        // Draw Horizontal Line
                        int lineHeight30 = sno30Height + 20;
                        c.drawLine(rectLeft,lineHeight30,rectRight,lineHeight30,p2);

                        // Start Line
                        c.drawLine(lineVertical1  ,lineHeight27,lineVertical1  ,lineHeight30,p);

                        // Value 30
                        c.drawText(Utility.getStringValue(bin.getSolar_panel_available()),rectLeft + 160 ,sno30Height,p2);

                        // Value 30.1
                        c.drawText(Utility.getStringValue(bin.getSolar_panel_type()),lineVertical1 + 10 ,sno30Height,p2);

                        // Sno. 31
                        int sno31Height = sno30Height + 40 ;
                        String sno31 = "पावसाच्या पाण्याची साठवण प्रकल्प" + "\n" + "(रेन हार्वेस्टिंग)";
                        for(String no31 : sno31.split("\n")){
                            c.drawText(no31,rectLeft + 10, sno31Height,p2);
                            sno31Height += p2.descent() - p2.ascent();
                        }

                        // Value 31
                        c.drawText(Utility.getStringValue(bin.getRain_water_harvesting()),rectLeft + 160 ,sno31Height - 20,p2);


                        // Close Page 1
                        pdfDocument.finishPage(page1);

                        // Created Page 2 ############################################################################################################################

                        // Created New Page --------------------------------------------------
                        // Page Number 2
                        PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth,pageHeight,pageNumber2).create();
                        PdfDocument.Page page2 = pdfDocument.startPage(pageInfo2);

                        // Canvas -------------
                        Canvas c2 = page2.getCanvas();
                        // Paint 3
                        Paint p3 = new Paint();
                        p3.setTextAlign(Paint.Align.LEFT);
                        p3.setTextSize(15);
                        p3.setColor(Color.BLACK);
                        p3.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));


                        // Paint 4
                        Paint p4 = new Paint();
                        p4.setTextAlign(Paint.Align.LEFT);
                        p4.setTextSize(11);
                        p4.setColor(Color.BLACK);
                        p4.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

                        // Header
                        // Sno. 30
                        int subHeaderPage2 = 30;
                        p3.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        c2.drawText("इमारतीचे वर्णन :-",rectLeft + 10, subHeaderPage2,p3);


                        // Table ---------------------------------------------------------
                        int rectBottomHeight = 720;
                        // Rectangle
//                        p3.setStyle(Paint.Style.STROKE);
//                        c2.drawRect(rectLeft,subHeaderPage2 + 30, rectRight,rectBottomHeight,p3);
                        int fixHeight = subHeaderPage2 + 30 + 20;
                        int lineVertical2 = rectLeft + 50;

                        // Sno. 1
                        int snoT1Start = rectLeft - 30 + 10;
                        c2.drawText("अ.क्र.",snoT1Start, fixHeight,p4);

                        // Draw Horizontal Line
                        int lineHeightT1 = fixHeight + 20 + 20;
                        c2.drawLine(rectLeft - 30,lineHeightT1,rectRight + 30,lineHeightT1,p4);

                        // Start Line
                        int snoT1StartLine = rectLeft - 30 + 40;
                        c2.drawLine(snoT1StartLine  ,subHeaderPage2 + 30,snoT1StartLine ,rectBottomHeight,p4);

                        // Sno. 2
                        int snoT2Start = snoT1Start + 40;
                        String snoT2Str = "इमारत \n मजला";
                        int heightT2 = fixHeight;
                        for(String no2 : snoT2Str.split("\n")){
                            c2.drawText(no2,snoT2Start, heightT2,p4);
                            heightT2 += p4.descent() - p4.ascent();
                        }
                        // Start Line
                        int snoT2StartLine = snoT1StartLine + 50;
                        c2.drawLine(snoT2StartLine ,subHeaderPage2 + 30,snoT2StartLine,rectBottomHeight,p4);


                        // Sno. 3
                        int snoT3Start = snoT2Start + 50;
                        String snoT3Str = "इमारत \n प्रकार";
                        int heightT3 = fixHeight;
                        for(String no3 : snoT3Str.split("\n")){
                            c2.drawText(no3,snoT3Start + 10, heightT3,p4);
                            heightT3 += p4.descent() - p4.ascent();
                        }
                        int snoT3StartLine = snoT2StartLine + 50 + 30;
                        // Start Line
                        c2.drawLine(snoT3StartLine ,subHeaderPage2 + 30,snoT3StartLine,rectBottomHeight,p4);


                        // Sno. 4
                        int snoT4Start = snoT3Start + 50 + 30;
                        String snoT4Str = "इमारत \n वापर";
                        int heightT4 = fixHeight;
                        for(String no4 : snoT4Str.split("\n")){
                            c2.drawText(no4,snoT4Start, heightT4,p4);
                            heightT4 += p4.descent() - p4.ascent();
                        }
                        int snoT4StartLine = snoT3StartLine + 50;
                        // Start Line
                        c2.drawLine(snoT4StartLine ,subHeaderPage2 + 30,snoT4StartLine,rectBottomHeight,p4);

                        // Sno. 5
                        int snoT5Start = snoT4Start + 70;
                        c2.drawText("मोजमाप (फुटात)",snoT5Start, fixHeight,p4);

                        int snoT5StartLine = snoT4StartLine + 160;
                        // Start Line
                        c2.drawLine(snoT5StartLine ,subHeaderPage2 + 30,snoT5StartLine,rectBottomHeight,p4);
                        // Horizontal Line
                        c2.drawLine(snoT4StartLine ,subHeaderPage2 + 60,snoT5StartLine,subHeaderPage2 + 60,p4);


                        int fixHeight1 = subHeaderPage2 + 80;
                        // Sno. 5.1
                        int snoT51Start = snoT4Start + 50;
                        c2.drawText("लांबी",snoT51Start, fixHeight1,p4);
                        int snoT51StartLine = snoT4StartLine + 50;
                        // Start Line
                        c2.drawLine(snoT51StartLine ,subHeaderPage2 + 60,snoT51StartLine,rectBottomHeight,p4);

                        // Sno. 5.2
                        int snoT52Start = snoT51Start + 50;
                        c2.drawText("रुंदी",snoT52Start, fixHeight1,p4);
                        int snoT52StartLine = snoT51StartLine + 50;
                        // Start Line
                        c2.drawLine(snoT52StartLine ,subHeaderPage2 + 60,snoT52StartLine,rectBottomHeight,p4);

                        // Sno. 5.3
                        int snoT53Start = snoT52Start + 50;
                        c2.drawText("क्षेत्रफळ",snoT53Start, fixHeight1,p4);

                        // Sno. 6
                        int snoT6Start = snoT5StartLine + 10;
                        String snoT6Str = "इमारतीचे \n वय";
                        int heightT6 = fixHeight;
                        for(String no6 : snoT6Str.split("\n")){
                            c2.drawText(no6,snoT6Start, heightT6,p4);
                            heightT6 += p4.descent() - p4.ascent();
                        }
                        int snoT6StartLine = snoT5StartLine + 60;
                        // Start Line
                        c2.drawLine(snoT6StartLine ,subHeaderPage2 + 30,snoT6StartLine,rectBottomHeight,p4);

                        // Sno. 7
                        int snoT7Start = snoT6Start + 60;
                        String snoT7Str = "वार्षिक भाडे \n रक्कम";
                        int heightT7 = fixHeight;
                        for(String no7 : snoT7Str.split("\n")){
                            c2.drawText(no7,snoT7Start, heightT7,p4);
                            heightT7 += p4.descent() - p4.ascent();
                        }
                        int snoT7StartLine = snoT6StartLine + 70;
                        // Start Line
                        c2.drawLine(snoT7StartLine ,subHeaderPage2 + 30,snoT7StartLine,rectBottomHeight,p4);

                        // Sno. 8
                        int snoT8Start = snoT7StartLine + 10;
                        c2.drawText("शेरा",snoT8Start, fixHeight,p4);


                        int rectTopHeight =  lineHeightT1 + 60;
                        // Created 9 * 9 matrix
                        for(int i=0; i<9; i++){
                            c2.drawLine(rectLeft - 30,rectTopHeight,rectRight + 30,rectTopHeight,p4);
                            rectTopHeight += 60;
                        }
                        Log.e(TAG, "Table Length -> "+ rectTopHeight);

                        p4.setTextSize(10);

                        p3.setStyle(Paint.Style.STROKE);
                        c2.drawRect(rectLeft - 30,subHeaderPage2 + 30, rectRight + 30,rectTopHeight,p3);

                        // Fill Matrix Form!
                        if(details.size() > 0){
                            int rectTopHeight1 =  lineHeightT1 + 60;
                            for(int i=0; i<details.size(); i++){
                                if(i == 9){
                                    break;
                                }
                                String srNo =  String.valueOf(i + 1);//Utility.getStringValue(details.get(i).getSr_no());
                                String floor = Utility.getStringValue(details.get(i).getFloor());
                                String buildingType = Utility.getStringValue(details.get(i).getBuilding_type());
                                String buildingUseType = Utility.getStringValue(details.get(i).getBuilding_use_type());
                                String length = Utility.getStringValue(details.get(i).getLength());
                                String height = Utility.getStringValue(details.get(i).getHeight());
                                String area = Utility.getStringValue(details.get(i).getArea());
                                String buildingAge = Utility.getStringValue(details.get(i).getBuilding_age());
                                String annualRent = Utility.getStringValue(details.get(i).getAnnual_rent());
                                String tagNo = Utility.getStringValue(details.get(i).getTag_no());

                                // 1
                                int heightRow1 = rectTopHeight1 - 40;
                                for(String no : srNo.split(" ")){
                                    c2.drawText(no,snoT1Start, heightRow1,p4);
                                    heightRow1 += p4.descent() - p4.ascent();
                                }
                                // 2
                                int heightRow2 = rectTopHeight1 - 40;
                                for(String no : floor.split(" ")){
                                    c2.drawText(no,snoT2Start, heightRow2,p4);
                                    heightRow2 += p4.descent() - p4.ascent();
                                }
//                                 <item>आर.सी.सी. पद्धतीची इमारत</item>
//                                <item>लोडबेरिंग</item>
//                                <item>दगड विटांचे चनुा किंवा सिमेंट वापरून उभारलेली इमारत</item>
//                                <item>दगड विटांचे मातीची इमारत</item>
//                                <item>झोपडी किंवा मातीची इमारत</item>
//                                <item>खुली जागा</item>
//                                <item>मनोरा तळ</item>
//
                                // 3
                                if(buildingType.equalsIgnoreCase("आर.सी.सी. पद्धतीची इमारत")){
                                    int heightRow3 = rectTopHeight1 - 40;
                                    buildingType = "आर.सी.सी. \n पद्धतीची इमारत";
                                    for(String no : buildingType.split("\n")){
                                        c2.drawText(no,snoT3Start - 10, heightRow3,p4);
                                        heightRow3 += p4.descent() - p4.ascent();
                                    }
                                }
                                else if(buildingType.equalsIgnoreCase("दगड विटांचे चनुा किंवा सिमेंट वापरून उभारलेली इमारत")){
                                    int heightRow3 = rectTopHeight1 - 40;
                                    buildingType  = "दगड विटांचे \n चनुा किंवा \n सिमेंट वापरून \n उभारलेली इमारत";
                                    for(String no : buildingType.split("\n")){
                                        c2.drawText(no,snoT3Start - 5 , heightRow3,p4);
                                        heightRow3 += p4.descent() - p4.ascent();
                                    }
                                }
                                else if(buildingType.equalsIgnoreCase("दगड विटांचे मातीची इमारत")){
                                    int heightRow3 = rectTopHeight1 - 40;
                                    buildingType = "दगड विटांचे \n मातीची इमारत";
                                    for(String no : buildingType.split("\n")){
                                        c2.drawText(no,snoT3Start - 10, heightRow3,p4);
                                        heightRow3 += p4.descent() - p4.ascent();
                                    }
                                }
                                else if(buildingType.equalsIgnoreCase("झोपडी किंवा मातीची इमारत")){
                                    int heightRow3 = rectTopHeight1 - 40;
                                    buildingType = "झोपडी किंवा \n मातीची इमारत";
                                    for(String no : buildingType.split("\n")){
                                        c2.drawText(no,snoT3Start - 10, heightRow3,p4);
                                        heightRow3 += p4.descent() - p4.ascent();
                                    }
                                }
                                else{
                                    int heightRow3 = rectTopHeight1 - 40;
                                    for(String no : buildingType.split(" ")){
                                        c2.drawText(no,snoT3Start - 10, heightRow3,p4);
                                        heightRow3 += p4.descent() - p4.ascent();
                                    }
                                }

                                // 4
                                int heightRow4 = rectTopHeight1 - 40;
                                for(String no : buildingUseType.split(" ")){
                                    c2.drawText(no,snoT4Start, heightRow4,p4);
                                    heightRow4 += p4.descent() - p4.ascent();
                                }
                                // 5.1
                                int heightRow51 = rectTopHeight1 - 40;
                                for(String no : length.split(" ")){
                                    c2.drawText(no,snoT51Start, heightRow51,p4);
                                    heightRow51 += p4.descent() - p4.ascent();
                                }

                                // 5.2
                                int heightRow52 = rectTopHeight1 - 40;
                                for(String no : height.split(" ")){
                                    c2.drawText(no,snoT52Start, heightRow52,p4);
                                    heightRow52 += p4.descent() - p4.ascent();
                                }

                                // 5.3
                                int heightRow53 = rectTopHeight1 - 40;
                                for(String no : area.split(" ")){
                                    c2.drawText(no,snoT53Start, heightRow53,p4);
                                    heightRow53 += p4.descent() - p4.ascent();
                                }

                                // 6
                                int heightRow6 = rectTopHeight1 - 40;
                                for(String no : buildingAge.split(" ")){
                                    c2.drawText(no,snoT6Start, heightRow6,p4);
                                    heightRow6 += p4.descent() - p4.ascent();
                                }

                                // 7
                                int heightRow7 = rectTopHeight1 - 40;
                                for(String no : annualRent.split(" ")){
                                    c2.drawText(no,snoT7Start, heightRow7,p4);
                                    heightRow7 += p4.descent() - p4.ascent();
                                }

                                // 8
                                int heightRow8 = rectTopHeight1 - 40;
                                for(String no : tagNo.split(" ")){
                                    c2.drawText(no,snoT8Start, heightRow8,p4);
                                    heightRow8 += p4.descent() - p4.ascent();
                                }

//
//                            c2.drawText(srNo,snoT1Start,rectTopHeight1 - 40,p4);
//                            c2.drawText(floor,snoT2Start,rectTopHeight1 - 40,p4);
//                            c2.drawText(buildingType,snoT3Start,rectTopHeight1 - 40,p4);
//                            c2.drawText(buildingUseType,snoT4Start,rectTopHeight1 - 40,p4);
//                            c2.drawText(length,snoT51Start,rectTopHeight1 - 40,p4);
//                            c2.drawText(height,snoT52Start,rectTopHeight1 - 40,p4);
//                            c2.drawText(area,snoT53Start,rectTopHeight1 - 40,p4);
//                            c2.drawText(buildingAge,snoT6Start,rectTopHeight1 - 40,p4);
//                            c2.drawText(annualRent,snoT7Start,rectTopHeight1 - 40,p4);
//                            c2.drawText(tagNo,snoT8Start,rectTopHeight1 - 40,p4);
//
                                rectTopHeight1 += 60;
                            }

                        }


                        // Text -----------------------
                        // Sno. 1
                        int snoT11Start = rectLeft + 10;
                        p4.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        p4.setTextSize(12);
                        c2.drawText("प्लॉटचे एकूण शेटफळ",snoT11Start, rectTopHeight + 30,p4);

                        // Value 1
                        c2.drawText(Utility.getStringValue(bin.getPlot_area()),snoT11Start + 10 ,rectTopHeight + 80,p4);


                        // Sno. 2 min
                        int snoT12Start = snoT11Start + 110;
                        c2.drawText("-",snoT12Start, rectTopHeight + 30 ,p4);

                        // Sno. 3
                        int snoT13Start = snoT12Start + 20;
                        c2.drawText("तळ मजल्याचे बांधकाम शेटफळ",snoT13Start, rectTopHeight + 30 ,p4);

                        // Value 3
                        c2.drawText(Utility.getStringValue(bin.getProperty_area()),snoT13Start + 10 ,rectTopHeight + 80,p4);

                        // Sno. 4 =
                        int snoT14Start = snoT13Start + 160;
                        c2.drawText("=",snoT14Start, rectTopHeight + 30 ,p4);

                        // Sno. 5
                        int snoT15Start = snoT14Start + 20;
                        c2.drawText("तळ मजला उर्वरित मोकळी जागा",snoT15Start, rectTopHeight + 30 ,p4);

                        // Value 3
                        c2.drawText(Utility.getStringValue(bin.getTotal_area()),snoT15Start + 10 ,rectTopHeight + 80,p4);

                        // Sno.  min
                        int snoT16Start = snoT11Start + 110;
                        c2.drawText("-",snoT16Start, rectTopHeight + 80 ,p4);

                        // Sno.  equals
                        int snoT17Start = snoT13Start + 160;
                        c2.drawText("=",snoT17Start, rectTopHeight + 80 ,p4);

                        p4.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        p4.setTextSize(10);
                        p4.setStyle(Paint.Style.STROKE);
                        c2.drawRect(snoT11Start,rectTopHeight + 60,snoT11Start + 100,rectTopHeight + 100,p4);
                        c2.drawRect(snoT13Start,rectTopHeight + 60,snoT13Start + 140,rectTopHeight + 100,p4);
                        c2.drawRect(snoT15Start,rectTopHeight + 60,snoT15Start + 140,rectTopHeight + 100,p4);



                        // टिप :- [ इमारत प्रकार  : - अ  - आर . सी . सी . विटांचे   इमारत , ब - लोडबेरिंग , क - दगड  विनयचे चुना किंवा सिमेंट वापरून उभारलेली इमारत , ड - दगड विटांचे मातीची इमारत , इ - झोपडी किंवा मातीची इमारत , ई - खुली जागा , उ  - मनोरा तळ ]
                        String a = "टिप :- [ इमारत प्रकार  : - अ- आर.सी.सी. विटांचे इमारत, ब - लोडबेरिंग, क - दगड विनयचे चुना किंवा सिमेंट वापरून \n उभारलेली इमारत, ड - दगड विटांचे मातीची इमारत, इ - झोपडी किंवा मातीची इमारत, ई - खुली जागा, उ - मनोरा तळ ]";

                        p4.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        int h1 = rectTopHeight + 130;
                        p4.setTextSize(12);
                        p4.setStyle(Paint.Style.FILL);
                        for(String x : a.split("\n")){
                            c2.drawText(x,rectLeft + 10, h1,p4);
                            h1 += p4.descent() - p4.ascent();
                        }


                        String b = "[ इमारत वापर प्रकार:- R - निवासी, C - वाणिज्य, I - औद्योगिक, E - शैक्षणिक, G - शासकीय  इमारत, NP- नगरपरिषद \n इमारत, M - मिश्र इमारत, D - धामिर्क, P- पार्किग ]";
                        // [ इमारत वापर प्रकार:- R - निवासी , C - वाणिज्य  , I - औद्योगिक, E - शैक्षणिक , G - शासकीय  इमारत , NP- नगरपरिषद  इमारत , M -  मिश्र  इमारत , D - धामिर्क  , P- पार्किग  ]
                        int h2 = rectTopHeight + 170;
                        p4.setTextSize(12);
                        p4.setStyle(Paint.Style.FILL);
                        for(String x : b.split("\n")){
                            c2.drawText(x,rectLeft + 10, h2,p4);
                            h2 += p4.descent() - p4.ascent();
                        }

                        p4.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        String c1 = "वरील सर्व माहिती माझ्या हजेरी समक्ष दिलेली /घेतली आहे. तसेच व्यक्तिगत व समजुतीनुसार व कागदपत्रानुसार खरी आहे. \n सदर माहिती खोडी आढळल्यास भारतीय दंड संहिता अन्व्ये कायधानुसार माज्यावर खटला व शिक्षेत्र पात्र राहील याची मला जाणीव आहे.";
                        // वरील सर्व माहिती माझ्या हजेरी समक्ष दिलेली /घेतली आहे . तसेच व्यक्तिगत व समजुतीनुसार व कागदपत्रानुसार खरी आहे . सदर माहिती खोडी आढळल्यास भारतीय दंड संहिता अन्व्ये कायधानुसार माज्यावर खटला व  शिक्षेत्र  पात्र राहील याची मला जाणीव आहे .
                        int h3 = rectTopHeight + 210;
                        p4.setTextSize(12);
                        p4.setStyle(Paint.Style.FILL);
                        for(String x : c1.split("\n")){
                            c2.drawText(x,rectLeft + 10, h3,p4);
                            h3 += p4.descent() - p4.ascent();
                        }


                        p4.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        c2.drawText("मेळकत धारक / प्रेतिनिधी नाव व सही",rectLeft + 10, h3 + 40,p4);
                        c2.drawText("-----------------------------",rectLeft + 10, h3 + 100,p4);

                        c2.drawText("सर्वेक्षक अधिकाऱ्याचे नाव व सही",rectLeft + 10 + 180, h3 + 40,p4);
                        c2.drawText("-----------------------------",rectLeft + 10 + 180, h3 + 100,p4);

                        c2.drawText("वार्ड लिपिकाची नाव व सही",rectLeft + 10 + 180 + 160, h3 + 40,p4);
                        c2.drawText("-----------------------------",rectLeft + 10 + 180 + 160, h3 + 100,p4);

                        // सर्वेक्षण दिनांक :
                        c2.drawText("सर्वेक्षण दिनांक :     /     /   ",rectLeft + 10 , rectBottom - 20,p4);

                        // Close Page 2
                        pdfDocument.finishPage(page2);
                        // File Save To Storage!
                        try{
                            FileOutputStream fos = new FileOutputStream(generatePDFFolder(PDFName));
                            pdfDocument.writeTo(fos);
                            Log.e(TAG,"PDF Download!");
                            Utility.showToast(mActivity,"PDF Download");
                        }
                        catch (IOException e) {
                            Log.e(TAG,e.getMessage());
                        }
                    }
                    else{
                        Log.e(TAG,"Form Number -> Empty");
                    }

                });
            }
            else{
                Log.e("TAG","Internal Storage Permission Not Granted");
            }
        }
        catch (Exception e){
            Log.e(TAG, e.getMessage());
        }



    }


}

