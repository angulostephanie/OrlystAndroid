package com.example.stephanieangulo.orlyst;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

// A helper class to put all image related functions in one place. :)
public class ItemImage {
    private byte[] bytes;
    private Bitmap bitmap;

    public ItemImage(byte[] bytes) {
        this.bytes = bytes;
    }
    public ItemImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public Bitmap decodeToBitmap() {
        // decode byte array to bitmap
        // create a copy of bitmap (java requires this)

        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Bitmap mutableBitmap = decodedBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // return scaled version of bitmap (has to be scaled)
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mutableBitmap, mutableBitmap.getWidth(),
            mutableBitmap.getHeight(), false);
        return scaledBitmap;
    }
    public ByteArrayOutputStream getCompressedStream(int quality) {
        // creates a byte[] stream from a bitmap

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream;
    }

}
