package com.synec.plynt.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.synec.plynt.R;
import com.synec.plynt._Master;
import com.synec.plynt.functions.PostFeedActionsClass;
import com.synec.plynt.models.NewsModel;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    public List<NewsModel> newsList;
    public Context context;
    private static String TAG = "NewsAdapter";

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
        PostFeedActionsClass actions = new PostFeedActionsClass(newsList, context, holder);

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

        onClicks(actions, newsItem, holder, position);

//        String[] reactionsList = ["upvote", "downvote", "bookmark"];
        _Master.isDocumentExistsAccordingToTwoParameters(
                "Reactions",
                "user_document_id",
                "article_id",
                _Master.sharedPreferences.getString("session_user_id", ""),
                newsItem.getArticle_id(),   // field2Value
                holder.upvoteIcon,         // imageIcon
                R.drawable.icon_upvote_yellow,
                documentId -> {              // callback
                    if (documentId == null) {
                        holder.upvoteIcon.setImageResource(R.drawable.icon_upvote);
                    } else {
                        holder.upvoteIcon.setImageResource(R.drawable.icon_upvote_yellow);
                        Log.d(TAG, "storeReactionsInFirestore: Upvote already exists for this user and article. Document ID: " + documentId);
                    }
                }
        );
        _Master.isDocumentExistsAccordingToTwoParameters(
                "Reactions",
                "user_document_id",
                "article_id",
                _Master.sharedPreferences.getString("session_user_id", ""),
                newsItem.getArticle_id(),   // field2Value
                holder.downvoteIcon,         // imageIcon
                R.drawable.icon_downvote_yellow,
                documentId -> {              // callback
                    if (documentId == null) {
                        holder.downvoteIcon.setImageResource(R.drawable.icon_downvote);
                    } else {
                        holder.downvoteIcon.setImageResource(R.drawable.icon_downvote_yellow);
                        Log.d(TAG, "storeReactionsInFirestore: Upvote already exists for this user and article. Document ID: " + documentId);
                    }
                }
        );
        _Master.isDocumentExistsAccordingToTwoParameters(
                "Reactions",
                "user_document_id",
                "article_id",
                _Master.sharedPreferences.getString("session_user_id", ""),
                newsItem.getArticle_id(),   // field2Value
                holder.bookmarkIcon,         // imageIcon
                R.drawable.icon_bookmark_marked,
                documentId -> {              // callback
                    if (documentId == null) {
                        // No document exists, update the icon to unmarked
                        holder.bookmarkIcon.setImageResource(R.drawable.icon_bookmark_unmarked);
                    } else {
                        // Document already exists, update the icon to marked
                        holder.bookmarkIcon.setImageResource(R.drawable.icon_bookmark_marked);
                        Log.d(TAG, "storeReactionsInFirestore: Bookmark already exists for this user and article. Document ID: " + documentId);
                    }
                }
        );


    }

    private void onClicks(PostFeedActionsClass actions, NewsModel newsItem, NewsViewHolder holder, int position) {
        holder.newsHeadline.setOnClickListener(v -> {
            String url = newsItem.getUrl();
            actions.goToWebView(url);
        });
        holder.linkCopyIcon.setOnClickListener(v -> {
            String url = newsItem.getUrl();
            actions.putURLToClipboard(url);
        });
        holder.bookmarkIcon.setOnClickListener(v -> {
            // a func that accepts the newsItem all data. Then, it will process it to turn i
            // nto a map with added key "user_id" and to get it is to _Master.sharedPreferences.getString("session_user_id"
            //then it will call this _Master.addDataToCollection
            Log.d(TAG, "onClicks: bookmark icon clicked " + newsItem.getDocument_id());
            actions.storeUserReactionThingInFirestore("bookmark", newsItem, "Reactions", holder.bookmarkIcon, R.drawable.icon_bookmark_marked,R.drawable.icon_bookmark_unmarked);
        });
        holder.upvoteIcon.setOnClickListener(v -> {
            Log.d(TAG, "onClicks: upvote icon clicked " + newsItem.getDocument_id());
            actions.storeUserReactionThingInFirestore("upvote", newsItem, "Reactions", holder.upvoteIcon, R.drawable.icon_upvote_yellow, R.drawable.icon_upvote);
        });
        holder.downvoteIcon.setOnClickListener(v -> {
            Log.d(TAG, "onClicks: downcote icon clicked " + newsItem.getDocument_id());
            actions.storeUserReactionThingInFirestore("downvote", newsItem, "Reactions", holder.downvoteIcon, R.drawable.icon_downvote_yellow, R.drawable.icon_downvote);
        });


    }


    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsSource, newsDate, newsHeadline, newsBody;
        TextView upvoteCount, downvoteCount, commentsCount;
        ImageView newsImage, iconSearch, bookmarkIcon, linkCopyIcon, upvoteIcon, downvoteIcon;
        ConstraintLayout commentsContainerButton;

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
            bookmarkIcon = itemView.findViewById(R.id.icon_bookmark);
            linkCopyIcon = itemView.findViewById(R.id.icon_link);
            upvoteIcon = itemView.findViewById(R.id.icon_upvote);
            downvoteIcon = itemView.findViewById(R.id.icon_downvote);
            commentsContainerButton = itemView.findViewById(R.id.commentsContainer);
        }
    }
}
