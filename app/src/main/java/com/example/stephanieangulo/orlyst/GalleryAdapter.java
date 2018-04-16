package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{
    private final String orderBy = MediaStore.Images.Media._ID;
    private final String[] projection = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID
    };
    private final Uri imageGalleryLink = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private Context context;
    private static List<byte[]> imageGallery;

    public GalleryAdapter(Context context) {
        this.context = context;
        this.imageGallery = ItemImage.getAllShownImagePaths(context,
                imageGalleryLink, projection, orderBy);
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_gallery_item, parent, false);
        return new GalleryAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, final int position) {
        final byte[] bytes = imageGallery.get(position);
        Glide.with(context)
                .asBitmap()
                .load(bytes)
                .apply(new RequestOptions()
                .placeholder(R.drawable.loading_spinner)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return imageGallery.size();
    }

    public static List<byte[]> getImageGallery() {
        return imageGallery;
    }

     class ViewHolder extends RecyclerView.ViewHolder {
         ImageView thumbnail;
         ViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.image_gallery_image_view);
        }
    }
}
