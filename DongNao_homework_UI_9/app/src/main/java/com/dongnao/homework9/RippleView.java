package com.dongnao.homework9;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KK on 2017/5/23.
 * 一个水波纹由波峰和波谷组成
 * 一个水波纹的动画有两步：1 向右平移，2 向上平移
 */

public class RippleView extends View {

    private Paint mPaint;

    private List<Ripple> mRipples;

    private Point mEndPoint;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaint.setColor(Color.BLUE);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mRipples = new ArrayList<>();
    }

    public void restart() {
        if (this.mMoveUpAnimator != null) {
            this.mMoveUpAnimator.cancel();
        }
        this.mWaveHeight = 0;
        this.invalidate();
    }

    /**
     * 一屏随机产生波纹数量
     */
    private static final int[] RANDOM_RIPPLE_COUNT = { 3, 4 };
    /**
     * 偏移单位
     */
    private static final int OFFSET_UNIT = 15;

    /**
     * 波的类型
     */
    private enum Wave {
        /**
         * 波峰
         */
        WAVE_CREST,
        /**
         * 波谷
         */
        WAVE_THROUGH
    }

    private void reset() {
        this.mRipples.clear();
        this.mEndPoint = new Point(0, 0);
        Wave wave = Wave.WAVE_CREST;
        this.updateRipples(wave);
    }

    /**
     * 随机生成一条新的Ripple
     * @return
     */
    private Ripple generateRipple(Wave wave) {
        int coefficient = wave == Wave.WAVE_CREST ? 1 : -1;
        Ripple ripple = new Ripple();
        ripple.startPoint = new Point(this.mEndPoint.x, this.mEndPoint.y);
        int index = this.random(0, RANDOM_RIPPLE_COUNT.length - 1);
        int count = RANDOM_RIPPLE_COUNT[index];
        double averageWidth = this.getWidth() * 1.0 / count;
        double offsetWidth = this.getWidth() * 1.0 / OFFSET_UNIT;
        double width = this.random(averageWidth - offsetWidth, averageWidth + offsetWidth);
        double height = this.random(averageWidth - 3 * offsetWidth, averageWidth - 2 * offsetWidth);
        int control1X = (int) this.random(this.mEndPoint.x, this.mEndPoint.x + width / 2);
        int control1Y = (int) (coefficient
                * this.random(coefficient * this.mEndPoint.y + height / 2,
                        coefficient * this.mEndPoint.y + height));
        int control2X = (int) this.random(this.mEndPoint.x + width / 2, this.mEndPoint.x + width);
        int control2Y = (int) (coefficient
                * this.random(coefficient * this.mEndPoint.y + height / 2,
                        coefficient * this.mEndPoint.y + height));
        this.mEndPoint.x += width;
        this.mEndPoint.y = (int) this.random(this.mEndPoint.y - offsetWidth,
                this.mEndPoint.y + offsetWidth);

        ripple.controlPoint1 = new Point(control1X, control1Y);
        ripple.controlPoint2 = new Point(control2X, control2Y);
        ripple.endPoint = new Point(this.mEndPoint.x, this.mEndPoint.y);
        ripple.wave = wave;
        return ripple;
    }

    private int random(int start, int end) {
        return (int) this.random((double) start, (double) end);
    }

    private double random(double start, double end) {
        return Math.random() * (end + 1 - start) + start;
    }

    private static final int START_HEIGHT = 10;
    private int mWaveHeight;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.mWaveHeight == 0) {
            this.reset();
            this.mWaveHeight = START_HEIGHT;
            this.startAnimation();
        }

        canvas.save();
        canvas.translate(this.getWidth(), this.getHeight());
        canvas.rotate(180);

        canvas.translate(0, this.mWaveHeight);
        Path path = new Path();
        path.moveTo(this.mRipples.get(0).startPoint.x, this.mRipples.get(0).startPoint.y);
        for (Ripple next : this.mRipples) {
            path.cubicTo(next.controlPoint1.x, next.controlPoint1.y, next.controlPoint2.x,
                    next.controlPoint2.y, next.endPoint.x, next.endPoint.y);
        }
        path.lineTo(this.mEndPoint.x, -this.mWaveHeight);
        path.lineTo(0, -this.mWaveHeight);
        path.close();
        canvas.drawPath(path, this.mPaint);
        canvas.restore();
    }

    private ValueAnimator mMoveUpAnimator;

    private void startAnimation() {
        this.mMoveUpAnimator = ValueAnimator.ofObject(new TranslationEvaluator(), START_HEIGHT,
                this.getHeight() * 2);
        this.mMoveUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RippleView.this.mWaveHeight = (int) animation.getAnimatedValue();
                RippleView.this.updateRippleX();
                RippleView.this.invalidate();
            }
        });
        this.mMoveUpAnimator.setDuration(8000);
        this.mMoveUpAnimator.start();
    }

    /**
     * 更新水波纹X坐标
     */
    private void updateRippleX() {
        int offset = 10;
        while (this.mRipples.get(0).endPoint.x - 10 <= 0) {
            this.mRipples.remove(0);
        }
        for (Ripple next : this.mRipples) {
            next.startPoint.x -= offset;
            next.controlPoint1.x -= offset;
            next.controlPoint2.x -= offset;
            next.endPoint.x -= offset;
        }
        this.mEndPoint.x -= offset;
        Wave wave = this.mRipples.get(this.mRipples.size() - 1).wave;
        wave = (wave == Wave.WAVE_CREST ? Wave.WAVE_THROUGH : Wave.WAVE_CREST);
        this.updateRipples(wave);
    }

    private void updateRipples(Wave wave) {
        while (this.mEndPoint.x < this.getWidth()) {
            Ripple ripple = this.generateRipple(wave);
            this.mRipples.add(ripple);
            wave = (wave == Wave.WAVE_CREST ? Wave.WAVE_THROUGH : Wave.WAVE_CREST);
        }
    }

    private class TranslationEvaluator implements TypeEvaluator<Integer> {

        @Override
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            return (int) (startValue + (endValue - startValue) * fraction);
        }
    }

    private class Ripple {
        Wave wave;
        Point startPoint;
        Point endPoint;
        Point controlPoint1;
        Point controlPoint2;
    }
}
