package com.bandecoot.itemscoreanalysisprogram;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Placeholder overlay view (draws nothing).
 * Keep it in the layout to allow future OMR guides/crop boxes.
 */
public class OmrOverlayView extends View {
    public OmrOverlayView(Context context) { super(context); }
    public OmrOverlayView(Context context, AttributeSet attrs) { super(context, attrs); }
    public OmrOverlayView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Intentionally empty for now.
    }
}