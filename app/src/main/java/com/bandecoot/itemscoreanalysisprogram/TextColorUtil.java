package com.bandecoot.itemscoreanalysisprogram;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * TextColorUtil - Utility class to apply consistent text colors throughout the app.
 * 
 * Global Color Rules:
 * - ALL text (TextViews, EditTexts, labels) should be BLACK for maximum readability
 * - Button text should be WHITE to contrast with button backgrounds
 * - This replaces the previous outlined/white text approach for better teacher-friendly UX
 */
public final class TextColorUtil {
    
    private TextColorUtil() {}
    
    /**
     * Apply black text color to a TextView (unless it's a Button).
     * 
     * @param view TextView to apply color to
     */
    public static void applyBlackText(TextView view) {
        if (view == null) return;
        
        // Don't change button text colors - they should remain white
        if (view instanceof Button) {
            return;
        }
        
        // Set text color to black
        view.setTextColor(Color.BLACK);
        
        // Remove any shadow effects
        view.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
    }
    
    /**
     * Apply white text color to a Button.
     * 
     * @param button Button to apply color to
     */
    public static void applyWhiteTextToButton(Button button) {
        if (button == null) return;
        
        // Set text color to white for buttons
        button.setTextColor(Color.WHITE);
        
        // Remove any shadow effects
        button.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
    }
    
    /**
     * Apply black text color to an EditText.
     * 
     * @param view EditText to apply color to
     */
    public static void applyBlackText(EditText view) {
        if (view == null) return;
        
        // Set text color to black
        view.setTextColor(Color.BLACK);
        
        // Remove any shadow effects
        view.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
    }
    
    /**
     * Recursively apply black text to all TextViews and EditTexts in a view hierarchy,
     * and white text to all Buttons.
     * 
     * This ensures consistent typography throughout the app:
     * - Regular text: BLACK
     * - Button text: WHITE
     * 
     * @param root Root view to start traversal from
     */
    public static void applyGlobalTextColors(View root) {
        if (root == null) return;
        
        // Apply appropriate color based on view type
        // Order matters: Button extends TextView, so check Button first
        if (root instanceof Button) {
            // Buttons get white text
            applyWhiteTextToButton((Button) root);
        } else if (root instanceof EditText) {
            // EditTexts get black text (EditText extends TextView, so check before TextView)
            applyBlackText((EditText) root);
        } else if (root instanceof TextView) {
            // Other TextViews get black text
            applyBlackText((TextView) root);
        }
        
        // Recursively apply to children if this is a ViewGroup
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyGlobalTextColors(group.getChildAt(i));
            }
        }
    }
    
    /**
     * Check if a view has black text applied.
     * 
     * @param view TextView to check
     * @return true if text is black, false otherwise
     */
    public static boolean hasBlackText(TextView view) {
        if (view == null) return false;
        return view.getCurrentTextColor() == Color.BLACK;
    }
    
    /**
     * Check if a button has white text applied.
     * 
     * @param button Button to check
     * @return true if text is white, false otherwise
     */
    public static boolean hasWhiteText(Button button) {
        if (button == null) return false;
        return button.getCurrentTextColor() == Color.WHITE;
    }
}
