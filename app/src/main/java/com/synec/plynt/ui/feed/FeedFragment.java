package com.synec.plynt.ui.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.synec.plynt.R;
import com.synec.plynt._Master;
import com.synec.plynt.adapters.NewsAdapter;
import com.synec.plynt.models.NewsModel;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private static String TAG = "FeedFragment";
    private FeedViewModel mViewModel;
    private RecyclerView recyclerViewNewsFeed;
    private NewsAdapter adapter;
    private List<NewsModel> newsList = new ArrayList<>();

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        // Initialize RecyclerView
        recyclerViewNewsFeed = view.findViewById(R.id.recyclerViewNewsFeed);
        recyclerViewNewsFeed.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter and set it to RecyclerView
        adapter = new NewsAdapter(newsList, getActivity());
        recyclerViewNewsFeed.setAdapter(adapter);

        // Fetch data from Firestore
        getNewsData();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
    }

    // Method to fetch data from Firestore
    private void getNewsData() {
        FirebaseFirestore db = _Master.db;  // Use Master class's db instance
        Log.d(TAG, "Fetching news Data: ");

        db.collection("NewsData")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    newsList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Create a NewsModel instance and set the document ID
                        NewsModel newsItem = document.toObject(NewsModel.class);
                        newsItem.setDocument_id(document.getId()); // Set the document ID

                        newsList.add(newsItem);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("FeedFragment", "Error fetching data", e);
                    Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
                });

    }
}
