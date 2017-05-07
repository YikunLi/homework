package com.dongnao.homework2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lykly on 2017/5/8.
 */

public class WaterfallFlowView extends ViewGroup {

    public WaterfallFlowView(Context context) {
        this(context, null);
    }

    public WaterfallFlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterfallFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public WaterfallFlowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int totalHeight = 0;
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) (child.getLayoutParams());
                totalHeight += layoutParams.topMargin + layoutParams.bottomMargin;
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, totalHeight);
                totalHeight += child.getMeasuredHeight();
            }
        }
        this.setMeasuredDimension(widthSize, Math.max(totalHeight, heightSize));

        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                continue;
            }
            MarginLayoutParams layoutParams = (MarginLayoutParams) (child.getLayoutParams());
            child.measure(MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int currentY = top;
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) (child.getLayoutParams());
            int childLeft = left + layoutParams.leftMargin;
            int childTop = currentY + layoutParams.topMargin;
            int childRight = childLeft + this.getMeasuredWidth();
            int childButtom = childTop + child.getMeasuredHeight();
            child.layout(childLeft, childTop, childRight, childButtom);
            currentY = childButtom + layoutParams.bottomMargin;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
    }
}
