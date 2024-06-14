package br.com.jjconsulting.mobile.jjlib.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageCameraGallery {

    public final static int OPEN_CAMERA = 111;
    public final static int OPEN_FILE = 222;
    public final static String INDEX =  "index";
    public final static String KEY_FIELD_NAME =  "field_name";


    private String mCurrentPhotoPath;
    private Uri outPutfileUri;


    /**
     * Open make photo intent
     *
     * @param title
     * @param activity
     * @return
     */
    public Intent getPhotoIntent(String title, String nameImage, boolean onlyCamera, Activity activity) {
        Intent chooser = null;
        mCurrentPhotoPath = null;
        outPutfileUri = null;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "JPEG_" + nameImage + ".jpg");

                mCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();
            } catch (Exception ex) {
                Log.v(Config.TAG, "Error image = " + ex.toString());
            }

            if (photoFile != null) {
                outPutfileUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
            }

            //Exibe camera e galeria
            String typeIntent = Intent.ACTION_PICK;

            //Exibe apenas a camera
            if(onlyCamera)
                typeIntent = Intent.ACTION_CAMERA_BUTTON;

            Intent galleryIntent = new Intent(typeIntent, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");

            if (photoFile != null) {
                galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
            }

            chooser = Intent.createChooser(galleryIntent, title);



            Intent[] extraIntents = {takePictureIntent};
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

        }
        return chooser;
    }

    /**
     * Open make photo intent
     *
     * @param title
     * @param activity
     * @return
     */
    public Intent getPhotoIntent(String title, String nameImage, Activity activity) {
        Intent chooser = null;
        mCurrentPhotoPath = null;
        outPutfileUri = null;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(KEY_FIELD_NAME, nameImage);

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "JPEG_" + nameImage + ".jpg");

                mCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();
            } catch (Exception ex) {
                Log.v(Config.TAG, "Error image = " + ex.toString());
            }

            if (photoFile != null) {
                outPutfileUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
            }

            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");

            if (photoFile != null) {
                galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
            }

            chooser = Intent.createChooser(galleryIntent, title);
            Intent[] extraIntents = {takePictureIntent};
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

        }
        return chooser;
    }


    /**
     * Get Real Path From URI
     *
     * @param contentURI
     * @param context
     * @return
     */
    public String getRealPathFromURI(Uri contentURI, Context context) {
        Cursor cursor = null;
        String result;
        try {
            if ("content".equals(contentURI.getScheme())) {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentURI, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                result = cursor.getString(column_index);
            } else {
                result = contentURI.getPath();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;

    }

    /**
     * Get URI to image received from capture by camera.
     */
    public Uri getCaptureImageOutputUri(Context context, String name) {
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            return Uri.fromFile(new File(getImage.getPath(), name));
        } else {
            return null;
        }
    }

    public Uri getImageResultUri(Context context, Intent data, String nameImage) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        return isCamera ? getCaptureImageOutputUri(context, nameImage) : data.getData();
    }

    /**
     * Image rotate
     *
     * @param bitmap
     * @param path
     * @return
     */
    public Bitmap imageOrientationValidator(Bitmap bitmap, String path) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Image rotate
     *
     * @param bitmap
     * @param path
     * @return
     */
    public Bitmap imageOrientationValidator(Bitmap bitmap, String path, int optimize) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90, optimize);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180, optimize);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270, optimize);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle, int optimize) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth() / optimize, source.getHeight() / optimize,
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

    public static String convertBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64Bitmap(String encodedImage) {

        if(encodedImage != null && encodedImage.length() > 0){
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedBitmap;
        } else {
            return null;
        }
    }

    public Uri getOutPutfileUri() {
        return outPutfileUri;
    }
}

