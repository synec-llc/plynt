package com.synec.plynt.ui.plyntify;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
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
    private Context context;

    public static PlyntifyFragment newInstance() {
        return new PlyntifyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_plyntify, container, false);

        recyclerView = root.findViewById(R.id.item_topic_recyclerview);
        context = getContext();
        topicList = new ArrayList<>();
        topicList.add("Typhoons");
        topicList.add("Science and Technology");
        topicList.add("Politics");
        topicList.add("Carlos Yulo");
        topicList.add("Business and Finance");

        adapter = new OrderOfTopicsAdapter(topicList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PlyntifyViewModel.class);
    }

    ItemTouchHelper.Callback itemTouchCallback = new ItemTouchHelper.Callback() {
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START;  // Left swipe only
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            adapter.swapItems(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            topicList.remove(position);
            adapter.notifyItemRemoved(position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;

                // Draw red background
                ColorDrawable background = new ColorDrawable(Color.RED);
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                        itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // Draw "Delete" text
                Paint textPaint = new Paint();
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(40);
                textPaint.setTextAlign(Paint.Align.CENTER);
                float textY = itemView.getTop() + (itemView.getHeight() / 2) + (textPaint.descent() - textPaint.ascent()) / 4;
                c.drawText("Delete", itemView.getRight() - 150, textY, textPaint);

                // Fade out item as it's swiped
                itemView.setAlpha(1.0f - Math.abs(dX) / (float) itemView.getWidth());
                itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(1.0f);  // Restore full opacity
            viewHolder.itemView.setTranslationX(0);  // Reset translation
        }
    };
}
