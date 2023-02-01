package com.eighttechprojects.propertytaxshirol.Utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageFileUtils {

//    public static final String DATE_FORMAT = "dd.MM.yyyy:HH.mm.ss.SSS";
    public static final String DATE_FORMAT = "dd_MM_yyyy_HHmmssSSS";
    public static final String IMAGE_DIRECTORY = "SurveyBaba";
    private SimpleDateFormat dateFormatter;

    public File getRootDirFile(Context context)
    {
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public File getRootDirFileDoc(Context context)
    {
        File file = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public File getRootDirAudioFileDoc(Context context)
    {
        File file = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public File getRootDirFileVideo(Context context)
    {
        File file = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public SimpleDateFormat getDateFormatter()
    {
        if(dateFormatter==null)
            return dateFormatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        else
            return dateFormatter;
    }

    public File getDestinationFile(File rootDirFile)
    {
        return new File(rootDirFile, "img_" + getDateFormatter().format(new Date())+ ".jpeg");
    }

    public File getDestinationFileDoc(File rootDirFile, String ext)
    {
        if(ext!=null) {
            return new File(rootDirFile, ext + "_" + getDateFormatter().format(new Date()) + "." + ext);
        }
        else
        {
            return getDestinationFile(rootDirFile);
        }
    }

    public File getDestinationFileImageInput(File rootDirFile, String inputId)
    {
        return new File(rootDirFile, "img_"+ getDateFormatter().format(new Date()) +"_"+ inputId+ ".jpeg");
    }
    public File getDestinationFileImageInput(File rootDirFile)
    {
        return new File(rootDirFile, "img_"+ getDateFormatter().format(new Date()) + ".jpeg");
    }
    public File getDestinationFileVideo(File rootDirFile, String inputId)
    {
        return new File(rootDirFile, "vid_"+
                getDateFormatter().format(new Date()) +"_"+ inputId+ ".mp4");
    }

    public static String getMimeType(Context context, Uri uri)
    {
        final MimeTypeMap mime = MimeTypeMap.getSingleton();
        return context.getContentResolver().getType(uri);
    }

    public static String getExtFromUri(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            try {
                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            try {
                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }

        }

        return extension;
    }

    /**
     * This is useful when an image is not available in sdcard physically but
     * it displays into photos application via google drive(Google Photos) and
     * also for if image is available in sdcard physically.
     *
     * @param uriPhoto
     * @return
     */

    public String getPathUri(Context context, Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uriPhoto, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);
            String tempFilename = null, mimeType = "image/jpg";
            mimeType = getMimeType(context, uriPhoto);
            if(mimeType!=null)
            {
                if(mimeType.split("/")[0].equalsIgnoreCase("image"))
                    tempFilename = getTempFilename(context);
                else
                    tempFilename = getTempFilename(context, mimeType);
            }

            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[7168];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return tempFilename;
        } catch (IOException ignored) {
            ignored.printStackTrace();
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    public void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    public String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    public String getTempFilename(Context context, String mimeType) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile(mimeType.split("/")[0], "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    public Bitmap decodeFile(File src, File dest) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(src);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(src);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("IMAGE", "Width :" + b.getWidth() + " Height :" + b.getHeight());

        try {
            FileOutputStream out = new FileOutputStream(dest);
            b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public Bitmap getBitMapFromUri(Context context, Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getBitmapAspectRatio(File srcPath, File destPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath.getAbsolutePath());
        Bitmap b2 = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
        try {
            FileOutputStream out = new FileOutputStream(destPath);
            b2.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return b2;
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, File destFile) {
        Bitmap b2 = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
        try {
            FileOutputStream out = new FileOutputStream(destFile);
            b2.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveImageToTempFile(View view, File destFile) {
            try {
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);
                FileOutputStream outputStream = new FileOutputStream(destFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                return true;
            } catch (Throwable e) {
                e.printStackTrace();
                return false;
            }
    }

    public static Bitmap getBitmapFromFilePath(String filePath)
    {
        return BitmapFactory.decodeFile(filePath);
    }

    public static Bitmap getThumbnailImage(String filePath)
    {
        return ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND);
    }

    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_WIDTH = 1980;
        int MAX_HEIGHT = 1980;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static boolean takeScreenshot(View view, File destFile) {
        try {
//            View view = context.getWindow().getDecorView().getRootView();
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            FileOutputStream outputStream = new FileOutputStream(destFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}
