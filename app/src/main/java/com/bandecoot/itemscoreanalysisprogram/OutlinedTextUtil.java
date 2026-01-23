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
     * Apply white text with black outline to a TextView.
     * Uses shadow layers at multiple angles to simulate outline effect.
     * 
     * @param view TextView to apply outline to
     */
    public static void applyOutline(TextView view) {
        if (view == null) return;
        
        // Set text color to white
        view.setTextColor(Color.WHITE);
        
        // Apply thick black shadow for a more visible outline effect
        // Shadow: radius, dx, dy, color
        // Increased radius from 3f to 6f for thicker, more visible outline
        view.setShadowLayer(6f, 0, 0, Color.BLACK);
        
        // Additional paint flags for better rendering
        view.getPaint().setStrokeWidth(2f);
        view.getPaint().setStyle(android.graphics.Paint.Style.FILL);
    }
    
    /**
     * Apply white text with black outline to an EditText.
     * 
     * @param view EditText to apply outline to
     */
    public static void applyOutline(EditText view) {
        if (view == null) return;
        
        // Set text color to white
        view.setTextColor(Color.WHITE);
        
        // Apply thick black shadow for a more visible outline effect
        // Increased radius from 3f to 6f for thicker, more visible outline
        view.setShadowLayer(6f, 0, 0, Color.BLACK);
        
        // Additional paint flags for better rendering
        view.getPaint().setStrokeWidth(2f);
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
        
        // Check if text is white and has shadow
        return view.getCurrentTextColor() == Color.WHITE;
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
