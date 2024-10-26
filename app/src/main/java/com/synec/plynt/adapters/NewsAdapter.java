package com.synec.plynt.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.synec.plynt.R;
import com.synec.plynt.WebViewActivity;
import com.synec.plynt.models.NewsModel;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsModel> newsList;
    private Context context;

    // Constructor
    public NewsAdapter(List<NewsModel> newsList, Context context) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_post, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsModel newsItem = newsList.get(position);

        holder.newsSource.setText(newsItem.getPublisher() != null ? newsItem.getPublisher() : "Unknown Source");
        holder.newsDate.setText(newsItem.getPublishing_date() != null ? newsItem.getPublishing_date() : "Unknown Date");
        holder.newsHeadline.setText(newsItem.getTitle() != null ? newsItem.getTitle() : "No Title");

        String description = newsItem.getDescription();
        if (description != null && description.length() > 146) {
            description = description.substring(0, 146) + "...";
        }
        holder.newsBody.setText(description != null ? description : "No Description");

        holder.upvoteCount.setText(String.valueOf(newsItem.getUpvotes_count()));
        holder.downvoteCount.setText(String.valueOf(newsItem.getDownvotes_count()));
        holder.commentsCount.setText(String.valueOf(newsItem.getComments_count()));

        Glide.with(holder.itemView.getContext())
                .load(newsItem.getImage_url())
                .error(R.drawable.image_not_available)
                .into(holder.newsImage);

        Glide.with(holder.itemView.getContext())
                .load(newsItem.getSource_icon())
                .error(R.drawable.image_not_available)
                .into(holder.iconSearch);

        // Click Listener to open WebViewActivity with URL
        holder.itemView.setOnClickListener(v -> {
            String url = newsItem.getUrl();
            Log.d("TAGas", "onBindViewHolder: "+ url);
            if (url != null && !url.isEmpty()) {
                WebViewActivity.start(context, url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsSource, newsDate, newsHeadline, newsBody;
        TextView upvoteCount, downvoteCount, commentsCount;
        ImageView newsImage, iconSearch;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsSource = itemView.findViewById(R.id.newsSource2);
            newsDate = itemView.findViewById(R.id.newsDate);
            newsHeadline = itemView.findViewById(R.id.newsHeadline);
            newsBody = itemView.findViewById(R.id.newsBody);
            newsImage = itemView.findViewById(R.id.newsImage);
            iconSearch = itemView.findViewById(R.id.icon_search);
            upvoteCount = itemView.findViewById(R.id.upvoteCount);
            downvoteCount = itemView.findViewById(R.id.downvoteCount);
            commentsCount = itemView.findViewById(R.id.commentsCount);
        }
    }
}
