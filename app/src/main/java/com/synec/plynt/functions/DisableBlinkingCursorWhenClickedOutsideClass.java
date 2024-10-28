package com.synec.plynt.functions;

import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.widget.AppCompatEditText;

public class DisableBlinkingCursorWhenClickedOutsideClass {

    private AppCompatEditText textInputEditText;
    private View parentLayout;

    public DisableBlinkingCursorWhenClickedOutsideClass(AppCompatEditText textInputEditText, View parentLayout) {
        this.textInputEditText = textInputEditText;
        this.parentLayout = parentLayout;

        setupTouchListener();
    }

    private void setupTouchListener() {
        parentLayout.setOnTouchListener(this::onTouch);

        // Optionally, add a touch listener to the TextInputEditText to ensure it doesn't lose focus
        textInputEditText.setOnTouchListener((view, motionEvent) -> {
            // Allow the EditText to be focused when tapped
            return false; // Let the event be handled by the EditText itself
        });
    }

    private boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            // Get the coordinates of the TextInputEditText
            int[] location = new int[2];
            textInputEditText.getLocationOnScreen(location);
            float x = motionEvent.getRawX();
            float y = motionEvent.getRawY();

            // Check if the touch was outside of the TextInputEditText
            if (x < location[0] || x > location[0] + textInputEditText.getWidth() ||
                    y < location[1] || y > location[1] + textInputEditText.getHeight()) {
                textInputEditText.clearFocus(); // Clear focus to stop blinking cursor
                return true; // Consume the event
            }
        }
        return false; // Allow other touch events to be handled
    }
}
