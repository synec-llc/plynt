package com.synec.plynt.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.synec.plynt.R;

import java.util.Collections;
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

    // Method to swap items in the list
    public void swapItems(int fromPosition, int toPosition) {
        Collections.swap(topics, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        ImageView hamburgerIcon;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            hamburgerIcon = itemView.findViewById(R.id.hamburgerIcon);
        }

        // Method to set the selected state with animations
        public void setSelectedState(boolean selected) {
            itemView.animate().cancel();
            if (selected) {
                itemView.animate()
                        .setDuration(200) // Animation duration of 0.2 seconds
                        .alpha(1f)
                        .translationZ(10) // Adds a shadow effect
                        .withEndAction(() -> itemView.setBackgroundColor(Color.LTGRAY));
            } else {
                itemView.animate()
                        .setDuration(200)
                        .translationZ(0) // Removes the shadow effect
                        .withEndAction(() -> itemView.setBackgroundColor(Color.WHITE));
            }
        }
    }
}
