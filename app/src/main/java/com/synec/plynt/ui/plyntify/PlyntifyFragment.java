package com.synec.plynt.ui.plyntify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synec.plynt.R;
import com.synec.plynt.adapters.OrderOfTopicsAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlyntifyFragment extends Fragment {

    private PlyntifyViewModel mViewModel;
    private RecyclerView recyclerView;
    private OrderOfTopicsAdapter adapter;
    private List<String> topicList;

    public static PlyntifyFragment newInstance() {
        return new PlyntifyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_plyntify, container, false);

        // Initialize the RecyclerView
        recyclerView = root.findViewById(R.id.item_topic_recyclerview);
        topicList = new ArrayList<>();

        // Add sample data to the list
        topicList.add("Entertainment and Culture");
        topicList.add("Science and Technology");
        topicList.add("Health and Wellness");
        topicList.add("Business and Finance");
        topicList.add("Travel and Adventure");
        topicList.add("Science and Technology");
        topicList.add("Health and Wellness");
        topicList.add("Business and Finance");
        topicList.add("Travel and Adventure");
        topicList.add("Science and Technology");
        topicList.add("Health and Wellness");
        topicList.add("Business and Finance");
        topicList.add("Travel and Adventure");
        topicList.add("Science and Technology");
        topicList.add("Health and Wellness");
        topicList.add("Business and Finance");
        topicList.add("Travel and Adventure");

        // Set up the adapter with the sample data
        adapter = new OrderOfTopicsAdapter(topicList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return root; // Return the root view
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PlyntifyViewModel.class);
        // Use the ViewModel as needed
    }
}
