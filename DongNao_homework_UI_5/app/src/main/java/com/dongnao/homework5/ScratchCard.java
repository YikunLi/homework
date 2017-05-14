package com.dongnao.homework5;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KK on 2017/5/14.
 */

public class ScratchCard extends View {

    /**
     * 当刮开区域比例超过这个值，直接显示结果
     */
    private static final float SCRATCH_OFF_PERCENT = 0.4f;

    private Paintcoat mPaintcoat;

    private ScratchResult mScratchResult;

    private State mCurrentState;

    private enum State {

        IDLE,

        SCRATCHING,

        SCRATCHED
    }

    public ScratchCard(Context context) {
        this(context, null);
    }

    public ScratchCard(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScratchCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ScratchCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                       int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.mPaintcoat = new Paintcoat();
        this.mScratchResult = new ScratchResult();
        this.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if (this.mCurrentState == State.SCRATCHED) {
            return result;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.mCurrentState = State.SCRATCHING;
                this.mPaintcoat.newLine(event.getX(), event.getY());
                break;
            default:
                this.mPaintcoat.lineTo(event.getX(), event.getY());
                break;
        }
        this.invalidate();

        if (this.mPaintcoat.getScratchAreaPercent() > SCRATCH_OFF_PERCENT) {
            this.mCurrentState = State.SCRATCHED;
            Toast.makeText(this.getContext(), this.mScratchResult.getResult(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw result
        if (this.mCurrentState != State.IDLE) {
            this.mScratchResult.draw(canvas);
        }
        // draw paintcoat
        if (this.mCurrentState != State.SCRATCHED) {
            this.mPaintcoat.draw(canvas);
        }
    }

    /**
     * @param drawable
     */
    public void setPaintcoat(Drawable drawable) {

    }

    /**
     * @param resId
     */
    public void setPaintcoat(@DrawableRes int resId) {

    }

    /**
     * @param color
     */
    public void setPaintcoatColor(int color) {

    }

    public void reset() {
        this.mCurrentState = State.IDLE;
        this.mPaintcoat.reset();
        this.mScratchResult.reset();
        this.invalidate();
    }

    public void scratched() {
        if (this.mCurrentState == State.SCRATCHED) {
            return;
        }
        this.mCurrentState = State.SCRATCHED;
        this.invalidate();
    }

    private interface ScratchItem {

        void draw(Canvas canvas);

        void reset();
    }

    private class Paintcoat implements ScratchItem {

        private Drawable mPaintcoat;

        private List<Path> mScratchedArea;

        private Path mCurrentPath;

        private Paint mPaint;

        Paintcoat() {
            this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
            this.mPaint.setXfermode(xfermode);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeCap(Paint.Cap.ROUND);
            this.mPaint.setStrokeWidth(50);

            this.mPaintcoat = ScratchCard.this.getResources().getDrawable(R.drawable.paintcoat);
            this.mScratchedArea = new ArrayList<>();
        }

        @Override
        public void draw(Canvas canvas) {
            int saveCount = canvas.saveLayer(0, 0, ScratchCard.this.getWidth(),
                    ScratchCard.this.getHeight(), null, Canvas.ALL_SAVE_FLAG);

            int width = ScratchCard.this.getWidth();
            int height = ScratchCard.this.getWidth() * this.mPaintcoat.getIntrinsicHeight()
                    / this.mPaintcoat.getIntrinsicWidth();
            this.mPaintcoat.setBounds(0, 0, width, height);

            this.mPaintcoat.draw(canvas);

            for (Path next : this.mScratchedArea) {
                canvas.drawPath(next, this.mPaint);
            }

            canvas.restoreToCount(saveCount);
        }

        @Override
        public void reset() {
            this.mScratchedArea.clear();
        }

        public float getScratchAreaPercent() {
            return (float) (this.mScratchedArea.size() / 10.0);
        }

        void newLine(float x, float y) {
            this.mCurrentPath = new Path();
            this.mScratchedArea.add(this.mCurrentPath);
            this.mCurrentPath.moveTo(x, y);
        }

        void lineTo(float x, float y) {
            this.mCurrentPath.lineTo(x, y);
        }
    }

    private class ScratchResult implements ScratchItem {

        private String mResult;

        private Drawable mResultDrawable;

        private Paint mPaint;

        ScratchResult() {
            this.mResult = "恭喜您中奖了！";
            this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.mPaint.setTextSize(100);
            this.mPaint.setColor(Color.BLACK);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawText(this.mResult, 100, 500, this.mPaint);
        }

        @Override
        public void reset() {

        }

        public String getResult() {
            return this.mResult;
        }
    }
}
