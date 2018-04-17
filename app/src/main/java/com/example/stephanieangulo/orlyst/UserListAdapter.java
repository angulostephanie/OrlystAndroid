package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by emily_000 on 4/16/2018.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private Context context;
    private List<Item> itemsList;

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
        holder.description.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description;
        public ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.itemTitle);
            description = view.findViewById(R.id.itemDescription);
            thumbnail = view.findViewById(R.id.itemImageView);
        }
    }
}
