package com.bandecoot.itemscoreanalysisprogram;

import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;

/**
 * OutlinedTextUtil - Helper class to apply white text with black outline effect.
 * Uses shadow layer technique to simulate text outlines for better readability.
 */
public final class OutlinedTextUtil {
    
    private OutlinedTextUtil() {}
    
    /**
     * Apply plain black text styling to a TextView.
     * 
     * @param view TextView to apply styling to
     */
    public static void applyOutline(TextView view) {
        if (view == null) return;
        view.setTextColor(Color.BLACK);
        view.setShadowLayer(0f, 0, 0, Color.TRANSPARENT);
        view.getPaint().setStrokeWidth(0f);
        view.getPaint().setStyle(android.graphics.Paint.Style.FILL);
    }
    
    /**
     * Apply plain black text styling to an EditText.
     * 
     * @param view EditText to apply styling to
     */
    public static void applyOutline(EditText view) {
        if (view == null) return;
        view.setTextColor(Color.BLACK);
        view.setShadowLayer(0f, 0, 0, Color.TRANSPARENT);
        view.getPaint().setStrokeWidth(0f);
        view.getPaint().setStyle(android.graphics.Paint.Style.FILL);
    }
    
    /**
     * Remove outline effect from TextView, reverting to default dark text.
     * 
     * @param view TextView to remove outline from
     */
    public static void removeOutline(TextView view) {
        if (view == null) return;
        
        // Revert to default text color (black/dark)
        view.setTextColor(Color.BLACK);
        
        // Remove shadow layer
        view.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        
        // Reset paint
        view.getPaint().setStrokeWidth(0f);
        view.getPaint().setStyle(android.graphics.Paint.Style.FILL);
    }
    
    /**
     * Remove outline effect from EditText, reverting to default dark text.
     * 
     * @param view EditText to remove outline from
     */
    public static void removeOutline(EditText view) {
        if (view == null) return;
        
        // Revert to default text color (black/dark)
        view.setTextColor(Color.BLACK);
        
        // Remove shadow layer
        view.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        
        // Reset paint
        view.getPaint().setStrokeWidth(0f);
        view.getPaint().setStyle(android.graphics.Paint.Style.FILL);
    }
    
    /**
     * Check if a TextView has outline applied.
     * 
     * @param view TextView to check
     * @return true if outline is applied, false otherwise
     */
    public static boolean hasOutline(TextView view) {
        if (view == null) return false;
        return view.getCurrentTextColor() != Color.BLACK;
    }
    
    /**
     * Recursively apply white text with thick black outline to all text-bearing views
     * in a view hierarchy. This includes TextView, EditText, Button, and Material components.
     * 
     * @param root Root view to start traversal from
     */
    public static void applyOutlineToTree(android.view.View root) {
        if (root == null) return;
        
        // Apply outline to the current view if it's a text-bearing view
        if (root instanceof EditText) {
            applyOutline((EditText) root);
        } else if (root instanceof TextView) {
            applyOutline((TextView) root);
        }
        
        // Recursively apply to children if this is a ViewGroup
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyOutlineToTree(group.getChildAt(i));
            }
        }
    }
}
