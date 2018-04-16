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
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private final String orderBy = MediaStore.Images.Media._ID;
    private final String[] projection = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID
    };
    private final Uri imageGalleryLink = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private Context context;
    private static List<byte[]> imageGallery;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    public GalleryAdapter(Context context) {
        this.context = context;
        this.imageGallery = new ArrayList<>();
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.gallery_image_item, parent, false);
        viewHolder = new GalleryAdapter.ViewHolder(itemView);

        return viewHolder;
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
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return  imageGallery == null ? 0 : imageGallery.size();
    }
    @Override
    public int getItemViewType(int position) {
        return (position == imageGallery.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public static List<byte[]> getImageGallery() {
        return imageGallery;
    }

    public void setMovies(List<byte[]> imageGallery) {
        this.imageGallery = imageGallery;
    }
     class ViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        ImageView thumbnail;

        ViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.gallery_item_image_view);
        }

    }
}
