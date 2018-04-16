package com.example.stephanieangulo.orlyst;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

// A helper class to put all image related functions in one place. :)
public class ItemImage {
    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 400;
    private byte[] bytes;
    private Bitmap bitmap;
    private String path;

    public ItemImage() {}
    public ItemImage(byte[] bytes) {
        this.bytes = bytes;
    }
    public ItemImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public ItemImage(String path) {
        this.path = path;
    }
    public Bitmap decodeToBitmap() {
        // decode byte array or file path to bitmap
        // create a copy of bitmap (java requires this)
        Bitmap decodedBitmap;
        if(path == null) {
            decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            decodedBitmap = BitmapFactory.decodeFile(path);
        }

        return scaleDownBitmap(decodedBitmap);
    }
    public static Bitmap scaleDownBitmap(Bitmap originalBitmap) {
        Bitmap mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // return scaled version of bitmap (has to be scaled)
        int width = mutableBitmap.getWidth();
        int height = mutableBitmap.getHeight();
        while(width > MAX_WIDTH || height > MAX_HEIGHT) {
            width /=2;
            height /=2;
        }
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mutableBitmap, width,
                height, false);
        return scaledBitmap;
    }
    public byte[] convertToBytes(int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
//    public byte[] getBytes() {
//        return bytes;
//    }

    public static List<byte[]> getAllShownImagePaths(Context context,
                                                      Uri imageGalleryLink,
                                                      String[] projection,
                                                      String orderBy) {
        List<byte[]> list = new ArrayList<>();

        ContentResolver imageGallery = context.getContentResolver();
        Cursor cursor = imageGallery.query(imageGalleryLink,
                projection, null, null, orderBy);

        int index;
        String path = null;
        index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);


        int j = 0;
        while (cursor.moveToNext() && j < 10) {
            path = cursor.getString(index);
            ItemImage image = new ItemImage(path);
            Bitmap bitmap = image.decodeToBitmap();
            byte[] b = new ItemImage(bitmap).convertToBytes(100);
            list.add(b);
            j++;
        }
        cursor.close();

        return list;
    }

    public static List<byte[]> getAllShownImagePaths(Context context,
                                                      Uri imageGalleryLink,
                                                      String[] projection,
                                                      String orderBy) {
        List<byte[]> list = new ArrayList<>();

        ContentResolver imageGallery = context.getContentResolver();
        Cursor cursor = imageGallery.query(imageGalleryLink,
                projection, null, null, orderBy);

        int index;
        String path = null;
        index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);


        while (cursor.moveToNext()) {
            path = cursor.getString(index);
            ItemImage image = new ItemImage(path);
            Bitmap bitmap = image.decodeToBitmap();
            byte[] b = new ItemImage(bitmap).convertToBytes(100);
            list.add(b);
        }
        cursor.close();

        return list;
    }



}
