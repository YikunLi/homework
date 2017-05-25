package com.dongnao.homework10;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.animation.LinearInterpolator;

/**
 * Created by KK on 2017/5/25.
 */

public class SearchDrawable extends Drawable implements Drawable.Callback {

    private static final int RADIUS = 100;
    private static final int STROKE_WIDTH = 10;
    private static final int COLOR = Color.RED;

    private GlassDrawable mGlassDrawable;

    private HoldingDrawable mHoldingDrawable;

    private State mState = State.IDLE;

    private enum State {
        /**
         * 默认态，显示一个放大镜
         */
        IDLE,

        /**
         * 搜索态，显示加载动画
         */
        LOADING
    }

    public SearchDrawable() {
        this.mGlassDrawable = new GlassDrawable();
        this.mHoldingDrawable = new HoldingDrawable();

        this.mGlassDrawable.setCallback(this);
        this.mHoldingDrawable.setCallback(this);
    }

    /**
     * 是否在加载中
     * @return
     */
    public boolean isLoading() {
        return this.mState == State.LOADING;
    }

    /**
     * 重置为默认态
     */
    public void reset() {
        if (this.mState == State.IDLE) {
            return;
        }
        this.mGlassDrawable.reset();
        this.mHoldingDrawable.reset();
        this.mState = State.IDLE;
        this.invalidateSelf();
    }

    /**
     * 显示加载中状态
     */
    public void loading() {
        if (this.mState == State.LOADING) {
            return;
        }
        this.mState = State.LOADING;
        // step 1 Glass 逐渐消逝
        this.mGlassDrawable.startFadeAnimation(new AnimationListener() {
            @Override
            public void onAnimationEnd() {
                // step 2 Holding 逐渐消逝
                SearchDrawable.this.mHoldingDrawable.startFadeAnimation(new AnimationListener() {
                    @Override
                    public void onAnimationEnd() {
                        // step 3 Glass 呈钟摆运动
                        SearchDrawable.this.mGlassDrawable.startPlumment(null);
                    }
                });
            }
        });
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.rotate(45);
        canvas.translate((float) ((RADIUS + STROKE_WIDTH / 2) * Math.sqrt(2)), 0);
        this.mGlassDrawable.draw(canvas);
        canvas.restore();
        canvas.save();
        float dx = (float) ((1 + Math.sqrt(2) / 2) * RADIUS + Math.sqrt(2) * STROKE_WIDTH / 4);
        float dy = dx;
        canvas.translate(dx, dy);
        this.mHoldingDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        this.mGlassDrawable.setAlpha(alpha);
        this.mHoldingDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.mGlassDrawable.setColorFilter(colorFilter);
        this.mHoldingDrawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) ((RADIUS + STROKE_WIDTH / 2.0) * (Math.sqrt(2) + 1));
    }

    @Override
    public int getIntrinsicHeight() {
        return this.getIntrinsicWidth();
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        this.invalidateSelf();
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

    }

    private abstract static class BaseDrawable extends Drawable {

        protected Paint mPaint;
        protected int mAlpha = 255;

        public BaseDrawable() {
            this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.mPaint.setColor(COLOR);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth(STROKE_WIDTH);
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
            this.mAlpha = alpha;
            this.mPaint.setAlpha(this.mAlpha);
            this.invalidateSelf();
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }

        protected void notifyAnimationEnd(AnimationListener listener) {
            if (listener != null) {
                listener.onAnimationEnd();
            }
        }

        public abstract void reset();
    }

    private static class GlassDrawable extends BaseDrawable {

        private static final int ANIMATION_DURATION_FADE = 1000;
        private static final int ANIMATION_DURATION_PLUMMET = 3000;

        private State mState = State.IDLE;
        private Path mPath;
        private PathMeasure mPathMeasure;

        private enum State {
            /**
             * 默认态
             */
            IDLE,
            /**
             * 逐渐消逝
             */
            FADE,
            /**
             * 钟摆
             */
            PLUMMET
        }

        private GlassDrawable() {
            this.mPath = new Path();
            this.mPath.addCircle(0, 0, RADIUS, Path.Direction.CW);
            this.mPathMeasure = new PathMeasure(this.mPath, false);
        }

        @Override
        public void reset() {
            if (this.mValueAnimator != null) {
                this.mValueAnimator.cancel();
            }
            this.mState = State.IDLE;
            this.mCurrentLength = 0;
        }

        private ValueAnimator mValueAnimator;
        private float mCurrentLength;

        private void startFadeAnimation(final AnimationListener listener) {
            this.mState = State.FADE;
            final float start = 0;
            final float end = this.mPathMeasure.getLength();
            this.mValueAnimator = ValueAnimator.ofFloat(start, end);
            this.mValueAnimator.setDuration(ANIMATION_DURATION_FADE);
            this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    GlassDrawable.this.mCurrentLength = (float) animation.getAnimatedValue();
                    GlassDrawable.this.invalidateSelf();
                    if (GlassDrawable.this.mCurrentLength == end) {
                        GlassDrawable.this.notifyAnimationEnd(listener);
                    }
                }
            });
            this.mValueAnimator.start();
        }

        private static final int SLIDER_WIDTH = 50;

        private void startPlumment(AnimationListener listener) {
            this.mState = State.PLUMMET;
            float start = -(this.mPathMeasure.getLength() - SLIDER_WIDTH);
            float end = -start;
            this.mValueAnimator = ValueAnimator.ofFloat(start, end);
            this.mValueAnimator.setInterpolator(new LinearInterpolator());
            this.mValueAnimator.setDuration(ANIMATION_DURATION_PLUMMET);
            this.mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    GlassDrawable.this.mCurrentLength = (float) animation.getAnimatedValue();
                    GlassDrawable.this.invalidateSelf();
                }
            });
            this.mValueAnimator.start();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            switch (this.mState) {
            case IDLE: {
                canvas.drawPath(this.mPath, this.mPaint);
                break;
            }
            case FADE: {
                Path path = new Path();
                this.mPathMeasure.getSegment(this.mCurrentLength, this.mPathMeasure.getLength(),
                        path, true);
                canvas.drawPath(path, this.mPaint);
                break;
            }
            case PLUMMET: {
                Path path = new Path();
                if (this.mCurrentLength > 0) {
                    this.mPathMeasure.getSegment(
                            this.mPathMeasure.getLength() - this.mCurrentLength - SLIDER_WIDTH,
                            this.mPathMeasure.getLength() - this.mCurrentLength, path, true);
                } else {
                    float length = this.mCurrentLength + this.mPathMeasure.getLength()
                            - SLIDER_WIDTH;
                    this.mPathMeasure.getSegment(length, length + SLIDER_WIDTH, path, true);
                }
                canvas.drawPath(path, this.mPaint);
                break;
            }
            }
        }

        @Override
        public int getIntrinsicWidth() {
            return 2 * RADIUS + STROKE_WIDTH;
        }

        @Override
        public int getIntrinsicHeight() {
            return this.getIntrinsicWidth();
        }

    }

    private static class HoldingDrawable extends BaseDrawable {

        private static final int ANIMATION_DURATION_FADE = 500;

        private State mState = State.IDLE;
        private Path mPath;
        private PathMeasure mPathMeasure;

        private enum State {
            /**
             * 默认态
             */
            IDLE,
            /**
             * 逐渐消逝
             */
            FADE,
            /**
             * 已消逝
             */
            GONE
        }

        private HoldingDrawable() {
            this.mPath = new Path();
            this.mPath.lineTo((float) (RADIUS - STROKE_WIDTH / 2.0),
                    (float) (RADIUS - STROKE_WIDTH / 2.0));
            this.mPathMeasure = new PathMeasure(this.mPath, false);
        }

        @Override
        public void reset() {
            if (this.mValueAnimator != null) {
                this.mValueAnimator.cancel();
            }
            this.mState = State.IDLE;
            this.mCurrentLength = 0;
        }

        private ValueAnimator mValueAnimator;
        private float mCurrentLength;

        private void startFadeAnimation(final AnimationListener listener) {
            this.mState = State.FADE;
            final float start = 0;
            final float end = this.mPathMeasure.getLength();
            this.mValueAnimator = ValueAnimator.ofFloat(start, end);
            this.mValueAnimator.setDuration(ANIMATION_DURATION_FADE);
            this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    HoldingDrawable.this.mCurrentLength = (float) animation.getAnimatedValue();
                    HoldingDrawable.this.invalidateSelf();
                    if (HoldingDrawable.this.mCurrentLength == end) {
                        HoldingDrawable.this.mState = HoldingDrawable.State.GONE;
                        HoldingDrawable.this.notifyAnimationEnd(listener);
                    }
                }
            });
            this.mValueAnimator.start();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            switch (this.mState) {
            case IDLE:
                canvas.drawPath(this.mPath, this.mPaint);
                break;
            case FADE:
                Path path = new Path();
                this.mPathMeasure.getSegment(0, this.mPathMeasure.getLength() - this.mCurrentLength,
                        path, true);
                canvas.drawPath(path, this.mPaint);
                break;
            case GONE:
                // do nothing
                break;
            }
        }

        @Override
        public int getIntrinsicWidth() {
            return (int) (RADIUS / Math.sqrt(2));
        }

        @Override
        public int getIntrinsicHeight() {
            return this.getIntrinsicWidth();
        }
    }

    private interface AnimationListener {

        void onAnimationEnd();
    }
}