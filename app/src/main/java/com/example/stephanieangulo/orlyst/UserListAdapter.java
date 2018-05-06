package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by emily_000 on 4/16/2018.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private static final String TAG = "UserListAdapter";
    private Context context;
    private List<Item> itemsList;
    private Boolean itemSold;

    public UserListAdapter(Context context, List<Item> list){
        this.context = context;
        this.itemsList = list;
    }

    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_user_item, parent, false);

        return new UserListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserListAdapter.ViewHolder holder, final int position) {
        final Item item = itemsList.get(position);
        holder.name.setText(item.getItemName());
        holder.price.setText("$" + item.getPrice());
        holder.category.setText(item.getCategory());
        holder.user.setText(item.getSeller());
        Log.d(TAG, "Image found? " + item.isImageFound());
        if(item.getBytes() != null) {
            Glide.with(context)
                    .load(item.getBytes())
                    .asBitmap()
                    .placeholder(R.drawable.loading_spinner)
                    .into(holder.thumbnail);
        }

        if(!item.isImageFound()) {
            holder.thumbnail.setImageResource(R.drawable.image_not_found);
        }
        //if(itemSold)
           // holder.layout.getBackground().setAlpha(128);

    }


    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description, price, category, user, onWatchlist;
        public ImageView thumbnail;
        public RelativeLayout layout;

        public ViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.item_layout);
            name = view.findViewById(R.id.itemTitle);
            price = view.findViewById(R.id.itemPrice);
            category = view.findViewById(R.id.itemCategory);
            user = view.findViewById(R.id.userSellingItem);
            thumbnail = view.findViewById(R.id.itemImageView);
            onWatchlist = view.findViewById(R.id.onWatchlistText);

        }
    }
}
