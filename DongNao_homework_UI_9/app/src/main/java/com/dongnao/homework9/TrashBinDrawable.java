package com.dongnao.homework9;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by KK on 2017/5/23.
 */

public class TrashBinDrawable extends Drawable implements TrashBin {

    private BinDrawable mBinDrawable;
    private CoverDrawable mCoverDrawable;
    private int mCurrentAngle;

    public TrashBinDrawable() {
        this.mBinDrawable = new BinDrawable();
        this.mCoverDrawable = new CoverDrawable();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.translate(100, 200);
        canvas.translate(50, 300);
        canvas.rotate(-this.mCurrentAngle);
        canvas.translate(-50, -300);
        this.mCoverDrawable.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.translate(110, 300);
        this.mBinDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public int getIntrinsicWidth() {
        return 800;
    }

    @Override
    public int getIntrinsicHeight() {
        return 800;
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    private boolean mIsOpened;

    @Override
    public void open() {
        if (this.mIsOpened) {
            return;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new RotationEvaluator(), 0, 30);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentAngle = (int) animation.getAnimatedValue();
                invalidateSelf();
                if (mCurrentAngle == 30) {
                    mIsOpened = true;
                }
            }
        });
        valueAnimator.start();
    }

    @Override
    public void close() {
        if (!this.mIsOpened) {
            return;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new RotationEvaluator(), 30, 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentAngle = (int) animation.getAnimatedValue();
                invalidateSelf();
                if (mCurrentAngle == 0) {
                    mIsOpened = false;
                }
            }
        });
        valueAnimator.start();
    }

    @Override
    public boolean isOpened() {
        return this.mIsOpened;
    }

    /**
     * 垃圾盖
     */
    private static class CoverDrawable extends Drawable {

        public CoverDrawable() {

        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            Path path = new Path();
            path.addRoundRect(150, 0, 250, 30, 10, 10, Path.Direction.CW);
            path.addRoundRect(0, 30, 450, 80, 10, 10, Path.Direction.CW);
            canvas.drawPath(path, paint);
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    }

    /**
     * 垃圾桶
     */
    private static class BinDrawable extends Drawable {

        private static final int BIN_WIDTH = 400;
        private static final int BIN_HEIGHT = 500;
        private static final int BAR_WIDTH = 40;

        public BinDrawable() {

        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            Path path = new Path();
            path.moveTo(0, 0);
            path.lineTo(50, BIN_HEIGHT);
            path.lineTo(350, BIN_HEIGHT);
            path.lineTo(BIN_WIDTH, 0);

            // Rect rect = new Rect();
            // Matrix matrix = new Matrix();
            path.addRoundRect((400 - BAR_WIDTH) / 2, BAR_WIDTH, (400 + BAR_WIDTH) / 2,
                    500 - BAR_WIDTH, 10, 10, Path.Direction.CW);
            canvas.drawPath(path, paint);
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    }

    private static class RotationEvaluator implements TypeEvaluator {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            int startAngle = (int) startValue;
            int endAngle = (int) endValue;
            int currentAngle = (int) (startAngle + fraction * (endAngle - startAngle));
            return currentAngle;
        }

    }
}