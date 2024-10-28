package com.synec.plynt.functions;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.synec.plynt.adapters.OrderOfTopicsAdapter;

import java.util.List;

public class ManageOrderOfTopicsTouchHelperClass extends ItemTouchHelper.Callback  {

    private final OrderOfTopicsAdapter adapter;
    private final List<String> topicList;
    private final Context context;
    private final OnOrderChangedListener onOrderChangedListener;

    public ManageOrderOfTopicsTouchHelperClass(OrderOfTopicsAdapter adapter, List<String> topicList, Context context, OnOrderChangedListener listener) {
        this.adapter = adapter;
        this.topicList = topicList;
        this.context = context;
        this.onOrderChangedListener = listener;
    }

    public interface OnOrderChangedListener {
        void onOrderChanged(List<String> newOrder);
    }

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

        // Update the order in the list
        String movedItem = topicList.remove(fromPosition);
        topicList.add(toPosition, movedItem);
        adapter.notifyItemMoved(fromPosition, toPosition);

        // Notify the fragment about the new order
        onOrderChangedListener.onOrderChanged(topicList);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        topicList.remove(position);
        adapter.notifyItemRemoved(position);

        // Notify the fragment about the new order after deletion
        onOrderChangedListener.onOrderChanged(topicList);
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
}
