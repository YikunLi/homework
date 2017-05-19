package com.dongnao.homework7;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by KK on 2017/5/19.
 */

public class RevealView extends View {

    private static final String TAG = "RevealView";

    private Drawable mActiveDrawable;

    private Drawable mInActiveDrawable;

    private float mRevealPercent;

    public RevealView(Context context) {
        this(context, null);
    }

    public RevealView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RevealView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RevealView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.setRevealPercent(0.6f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mActiveDrawable == null || this.mInActiveDrawable == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int maxWidth = this.mActiveDrawable.getIntrinsicWidth();
        int maxHeight = this.mActiveDrawable.getIntrinsicHeight();
        this.mActiveDrawable.setBounds(0, 0, this.mActiveDrawable.getIntrinsicWidth(),
                this.mActiveDrawable.getIntrinsicHeight());
        this.mInActiveDrawable.setBounds(0, 0, this.mInActiveDrawable.getIntrinsicWidth(),
                this.mInActiveDrawable.getIntrinsicHeight());
        maxWidth = maxWidth > this.mInActiveDrawable.getIntrinsicWidth() ? maxWidth
                : this.mInActiveDrawable.getIntrinsicWidth();
        maxHeight = maxHeight > this.mInActiveDrawable.getIntrinsicHeight() ? maxHeight
                : this.mInActiveDrawable.getIntrinsicHeight();
        this.setMeasuredDimension(maxWidth, maxHeight);
    }

    public void setDrawable(@DrawableRes int activeId, @DrawableRes int inActiveId) {
        this.mActiveDrawable = this.getContext().getDrawable(activeId);
        this.mInActiveDrawable = this.getContext().getDrawable(inActiveId);
        this.requestLayout();
    }

    /**
     * @param percent
     */
    public void setRevealPercent(float percent) {
        this.mRevealPercent = percent;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mActiveDrawable == null || this.mInActiveDrawable == null) {
            return;
        }

        int saveCount = canvas.save();
        RectF activeRectF = new RectF(0, 0,
                this.mActiveDrawable.getIntrinsicWidth() * (1 - this.mRevealPercent),
                this.mActiveDrawable.getIntrinsicHeight());
        canvas.clipRect(activeRectF);
        this.mActiveDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);

        saveCount = canvas.save();
        RectF inActiveRegion = new RectF(
                this.mInActiveDrawable.getIntrinsicWidth() * (1 - this.mRevealPercent), 0,
                this.mInActiveDrawable.getIntrinsicWidth(),
                this.mInActiveDrawable.getIntrinsicHeight());
        canvas.clipRect(inActiveRegion);
        this.mInActiveDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }
}
