package com.bandecoot.itemscoreanalysisprogram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class HighlightOverlayView extends View {
    private static final float DEFAULT_MARGIN_RATIO = 0.08f;
    private static final float HANDLE_TOUCH_MULTIPLIER = 1.6f;

    private final Paint dimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path dimPath = new Path();
    private final RectF imageBounds = new RectF();
    private final RectF cropRect = new RectF();
    private float handleRadius;
    private float handleTouchRadius;
    private float minCropSize;
    private float lastX;
    private float lastY;
    private Handle activeHandle = Handle.NONE;
    private boolean dragging = false;

    private enum Handle {
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public HighlightOverlayView(Context context) {
        super(context);
        init();
    }

    public HighlightOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HighlightOverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        dimPaint.setColor(Color.parseColor("#99000000"));
        dimPaint.setStyle(Paint.Style.FILL);

        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(dpToPx(2f));

        handlePaint.setColor(Color.WHITE);
        handlePaint.setStyle(Paint.Style.FILL);

        handleRadius = dpToPx(10f);
        handleTouchRadius = handleRadius * HANDLE_TOUCH_MULTIPLIER;
        minCropSize = dpToPx(120f);
    }

    public void setImageBounds(RectF bounds) {
        if (bounds == null) {
            return;
        }
        imageBounds.set(bounds);
        resetCropRect();
    }

    public RectF getNormalizedCropRect() {
        if (imageBounds.isEmpty()) {
            return null;
        }
        float width = imageBounds.width();
        float height = imageBounds.height();
        return new RectF(
                (cropRect.left - imageBounds.left) / width,
                (cropRect.top - imageBounds.top) / height,
                (cropRect.right - imageBounds.left) / width,
                (cropRect.bottom - imageBounds.top) / height
        );
    }

    private void resetCropRect() {
        if (imageBounds.isEmpty()) {
            return;
        }
        float marginX = imageBounds.width() * DEFAULT_MARGIN_RATIO;
        float marginY = imageBounds.height() * DEFAULT_MARGIN_RATIO;
        cropRect.set(
                imageBounds.left + marginX,
                imageBounds.top + marginY,
                imageBounds.right - marginX,
                imageBounds.bottom - marginY
        );
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (imageBounds.isEmpty()) {
            return;
        }
        dimPath.reset();
        dimPath.addRect(imageBounds, Path.Direction.CW);
        dimPath.addRect(cropRect, Path.Direction.CCW);
        dimPath.setFillType(Path.FillType.EVEN_ODD);
        canvas.drawPath(dimPath, dimPaint);
        canvas.drawRect(cropRect, borderPaint);

        canvas.drawCircle(cropRect.left, cropRect.top, handleRadius, handlePaint);
        canvas.drawCircle(cropRect.right, cropRect.top, handleRadius, handlePaint);
        canvas.drawCircle(cropRect.left, cropRect.bottom, handleRadius, handlePaint);
        canvas.drawCircle(cropRect.right, cropRect.bottom, handleRadius, handlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (imageBounds.isEmpty()) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                activeHandle = findHandle(x, y);
                dragging = activeHandle == Handle.NONE && cropRect.contains(x, y);
                if (activeHandle == Handle.NONE && !dragging) {
                    return false;
                }
                lastX = x;
                lastY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = x - lastX;
                float dy = y - lastY;
                if (dragging) {
                    moveCropRect(dx, dy);
                } else if (activeHandle != Handle.NONE) {
                    resizeCropRect(dx, dy);
                }
                lastX = x;
                lastY = y;
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                activeHandle = Handle.NONE;
                dragging = false;
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private Handle findHandle(float x, float y) {
        if (isNear(x, y, cropRect.left, cropRect.top)) {
            return Handle.TOP_LEFT;
        }
        if (isNear(x, y, cropRect.right, cropRect.top)) {
            return Handle.TOP_RIGHT;
        }
        if (isNear(x, y, cropRect.left, cropRect.bottom)) {
            return Handle.BOTTOM_LEFT;
        }
        if (isNear(x, y, cropRect.right, cropRect.bottom)) {
            return Handle.BOTTOM_RIGHT;
        }
        return Handle.NONE;
    }

    private boolean isNear(float x, float y, float handleX, float handleY) {
        float dx = x - handleX;
        float dy = y - handleY;
        return dx * dx + dy * dy <= handleTouchRadius * handleTouchRadius;
    }

    private void moveCropRect(float dx, float dy) {
        cropRect.offset(dx, dy);
        if (cropRect.left < imageBounds.left) {
            cropRect.offset(imageBounds.left - cropRect.left, 0);
        }
        if (cropRect.top < imageBounds.top) {
            cropRect.offset(0, imageBounds.top - cropRect.top);
        }
        if (cropRect.right > imageBounds.right) {
            cropRect.offset(imageBounds.right - cropRect.right, 0);
        }
        if (cropRect.bottom > imageBounds.bottom) {
            cropRect.offset(0, imageBounds.bottom - cropRect.bottom);
        }
        invalidate();
    }

    private void resizeCropRect(float dx, float dy) {
        switch (activeHandle) {
            case TOP_LEFT:
                resizeLeft(dx);
                resizeTop(dy);
                break;
            case TOP_RIGHT:
                resizeRight(dx);
                resizeTop(dy);
                break;
            case BOTTOM_LEFT:
                resizeLeft(dx);
                resizeBottom(dy);
                break;
            case BOTTOM_RIGHT:
                resizeRight(dx);
                resizeBottom(dy);
                break;
            default:
                break;
        }
        invalidate();
    }

    private void resizeLeft(float dx) {
        float newLeft = cropRect.left + dx;
        newLeft = Math.max(imageBounds.left, Math.min(newLeft, cropRect.right - minCropSize));
        cropRect.left = newLeft;
    }

    private void resizeRight(float dx) {
        float newRight = cropRect.right + dx;
        newRight = Math.min(imageBounds.right, Math.max(newRight, cropRect.left + minCropSize));
        cropRect.right = newRight;
    }

    private void resizeTop(float dy) {
        float newTop = cropRect.top + dy;
        newTop = Math.max(imageBounds.top, Math.min(newTop, cropRect.bottom - minCropSize));
        cropRect.top = newTop;
    }

    private void resizeBottom(float dy) {
        float newBottom = cropRect.bottom + dy;
        newBottom = Math.min(imageBounds.bottom, Math.max(newBottom, cropRect.top + minCropSize));
        cropRect.bottom = newBottom;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
