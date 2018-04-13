package com.example.stephanieangulo.orlyst;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

// A helper class to put all image related functions in one place. :)
public class ItemImage {
    private byte[] bytes;
    private Bitmap bitmap;
    private String path;

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

        Bitmap mutableBitmap = decodedBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // return scaled version of bitmap (has to be scaled)
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mutableBitmap, mutableBitmap.getWidth(),
            mutableBitmap.getHeight(), false);
        return scaledBitmap;
    }
    public byte[] convertToBytes(int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }


}
