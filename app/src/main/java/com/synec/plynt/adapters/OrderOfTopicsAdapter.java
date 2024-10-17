package com.synec.plynt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.synec.plynt.R;

import java.util.List;

public class OrderOfTopicsAdapter extends RecyclerView.Adapter<OrderOfTopicsAdapter.TopicViewHolder> {

    private List<String> topics;

    public OrderOfTopicsAdapter(List<String> topics) {
        this.topics = topics;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic_recyclerview, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        String topic = topics.get(position);
        holder.itemTitle.setText(topic);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        ImageView hamburgerIcon;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            hamburgerIcon = itemView.findViewById(R.id.hamburgerIcon);
        }
    }
}
