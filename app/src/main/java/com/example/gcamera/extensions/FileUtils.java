package com.example.gcamera.extensions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class FileUtils {

    private FileUtils() {

    }

    private static FileUtils INSTANCE;

    public static FileUtils doInstance() {
        if (null == INSTANCE) {
            INSTANCE = new FileUtils();
        }
        return INSTANCE;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File resizeFile(File originFile) {
        try {
            File photoResize = createImageFile();

            ExifInterface exif = new ExifInterface(originFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Bitmap bitmap = BitmapFactory.decodeFile(originFile.getAbsolutePath());

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bitmap.getWidth(), (float) bitmap.getHeight());
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());

            FileOutputStream fOut = new FileOutputStream(photoResize);
            getResizedBitmap(rotatedBitmap, 800).compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

            return photoResize;
        } catch (Exception e) {

        }
        return null;
    }

    public static File saveImage(Bitmap original) {
        try {
            File photoResize = createImageFile();

            FileOutputStream fOut = new FileOutputStream(photoResize);
            original.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream

            return photoResize;
        } catch (Exception e) {

        }
        return null;
    }

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "temp/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public static JSONObject getJSONFromFile(Context context, String path) {
        JSONObject data = null;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);

            return jsonObject;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

    public static JSONArray getJSONArrayFromFile(Context context, String path) {
        JSONArray data = null;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONArray jsonObject = new JSONArray(json);

            return jsonObject;
        } catch (Exception e) {

        }
        return data;
    }

    public static File resizeImageFile(File originFile) {
        try {
            File photoResize = createImageFile();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap originBitmap = BitmapFactory.decodeFile(originFile.getAbsolutePath(), options);

            Bitmap bitmapResized = getResizedBitmap(originBitmap, 700);

            originBitmap.recycle();

            //Rotate image
            ExifInterface exif = new ExifInterface(originFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            int rotationAngle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            if (rotationAngle != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotationAngle, (float) bitmapResized.getWidth(), (float) bitmapResized.getHeight());
                Bitmap bitmapRotated = Bitmap.createBitmap(bitmapResized, 0, 0, bitmapResized.getWidth(), bitmapResized.getHeight(), matrix, true);

                bitmapResized.recycle();

                FileOutputStream fOut = new FileOutputStream(photoResize);
                bitmapRotated.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream

                bitmapRotated.recycle();
            } else {
                FileOutputStream fOut = new FileOutputStream(photoResize);
                bitmapResized.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream

                bitmapResized.recycle();
            }

            return photoResize;
        } catch (Exception e) {
        }
        return null;
    }

    public File resizeImageFileV2(File originFile) {
        try {
            File photoResize = createImageFile();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap originBitmap = BitmapFactory.decodeFile(originFile.getAbsolutePath(), options);

            Bitmap bitmapResized = getResizedBitmap(originBitmap, 700);

            originBitmap.recycle();

            //Rotate image
            ExifInterface exif = new ExifInterface(originFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            int rotationAngle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            if (rotationAngle != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(rotationAngle, (float) bitmapResized.getWidth(), (float) bitmapResized.getHeight());
                Bitmap bitmapRotated = Bitmap.createBitmap(bitmapResized, 0, 0, bitmapResized.getWidth(), bitmapResized.getHeight(), matrix, true);

                bitmapResized.recycle();

                FileOutputStream fOut = new FileOutputStream(photoResize);
                bitmapRotated.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream

                bitmapRotated.recycle();
            } else {
                FileOutputStream fOut = new FileOutputStream(photoResize);
                bitmapResized.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream

                bitmapResized.recycle();
            }

            return photoResize;
        } catch (Exception e) {
        }
        return null;
    }

    private static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static File createTemporaryFile(String part, String ext) throws Exception {
        if (Build.VERSION.SDK_INT >= 24) {

            File file = null;
            try {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "temp/");
                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }
                file = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return file;

        } else {
            File tempDir = Environment.getExternalStorageDirectory();
            tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
            return File.createTempFile(part, ext, tempDir);
        }
    }
}
