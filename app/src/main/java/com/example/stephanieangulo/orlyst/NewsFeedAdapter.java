package com.example.stephanieangulo.orlyst;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder>{

    private Context context;
    private List<Item> itemsList;

    public NewsFeedAdapter(Context context, List<Item> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsfeed_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Item item = itemsList.get(position);
        holder.name.setText(item.getItemName());
        holder.description.setText(item.getDescription());
        holder.user.setText(item.getUser());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description, user;
        public ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.itemTitle);
            description = view.findViewById(R.id.itemDescription);
            user = view.findViewById(R.id.userSellingItem);
            thumbnail = view.findViewById(R.id.itemImageView);
        }
    }
}
