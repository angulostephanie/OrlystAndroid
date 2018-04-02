package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{
    private Context context;
    private List<byte[]> imageGallery;
    public GalleryAdapter(Context context, List<byte[]> imageGallery) {
        this.context = context;
        this.imageGallery = imageGallery;
    }
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_gallery_item, parent, false);
        return new GalleryAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, final int position) {
        final byte[] bytes = imageGallery.get(position);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        holder.thumbnail.setImageBitmap(
                Bitmap.createScaledBitmap(mutableBitmap, mutableBitmap.getWidth(),
                        mutableBitmap.getHeight(), false));
    }

    @Override
    public int getItemCount() {
        return imageGallery.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public ViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.image_gallery_image_view);
        }
    }
}
