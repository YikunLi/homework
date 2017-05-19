package com.dongnao.homework7;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by KK on 2017/5/19.
 * 封装搜索按钮、动画与输入框，代理并提供部分与Search业务相关的EditText方法
 * TODO
 */

public class SearchView extends FrameLayout {

    private static final String TAG = "SearchView";

    private MagnifierView mMagnifierView;

    private EditText mInputEditor;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                      int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.mMagnifierView = new MagnifierView(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        this.addView(this.mMagnifierView, layoutParams);

        this.mInputEditor = new EditText(context);
        this.mInputEditor.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        this.mInputEditor.setVisibility(View.INVISIBLE);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addView(this.mInputEditor, layoutParams);

        this.mMagnifierView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchView.this.mMagnifierView.reset();
                SearchView.this.mMagnifierView.start();
            }
        });

        this.mMagnifierView.setAnimationListener(new MagnifierView.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                SearchView.this.mInputEditor.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * {@link android.widget.EditText#getText}
     *
     * @return
     */
    public Editable getText() {
        return this.mInputEditor.getText();
    }

    /**
     * {@link android.widget.EditText#setText}
     *
     * @param text
     * @param type
     */
    public void setText(CharSequence text, TextView.BufferType type) {
        this.mInputEditor.setText(text, type);
    }

    /**
     * {@link android.widget.EditText#addTextChangedListener}
     *
     * @param watcher
     */
    public void addTextChangedListener(TextWatcher watcher) {
        this.mInputEditor.addTextChangedListener(watcher);
    }

    private static class MagnifierView extends View {
        private static final float START_ANGLE = 45;
        private static final float TOTAL_ANGLE = 360;

        private int mColor;
        private int mRadius = 50;
        private int mHoldLength = this.mRadius;
        private int mStrokeWidth = 5;

        private Paint mPaint;
        private State mState = State.IDLE;
        private float mStartAngle = START_ANGLE;
        private float mSweepAngle = TOTAL_ANGLE;

        private boolean mIsRunning;

        /**
         * 动画的状态
         */
        private enum State {

            /**
             * 初始态，完整的放大镜，没有输入框
             */
            IDLE,

            /**
             * 有圆弧，有镜柄，同时有输入框
             */
            WITH_CIRCLE,

            /**
             * 没有圆弧，有镜柄，同时有输入框
             */
            WITHOUT_CIRCLE,

            /**
             * 只剩下输入框
             */
            ONLY_LINE,

            /**
             * 完成，隐藏掉
             */
            FINISHED

        }

        public MagnifierView(Context context) {
            this(context, null);
        }

        public MagnifierView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MagnifierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            this(context, attrs, defStyleAttr, 0);
        }

        public MagnifierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                             int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);

            this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaint.setStrokeWidth(this.mStrokeWidth);
        }

        private AnimationListener mAnimationListener;

        /**
         * Set the Animation listener
         *
         * @param listener
         */
        public void setAnimationListener(AnimationListener listener) {
            this.mAnimationListener = listener;
        }

        /**
         * Start the animation
         */
        public void start() {
            Log.e(TAG, "start");
            this.mIsRunning = true;
            this.invalidate();

            this.mStartTime = SystemClock.elapsedRealtime();
        }

        /**
         * Reset the animation
         */
        public void reset() {
            this.mState = State.IDLE;
            this.mIsRunning = false;
            this.mStartAngle = START_ANGLE;
            this.mSweepAngle = TOTAL_ANGLE;
        }

        private static final long INVALIDATE_INTERVEL = 50;

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Log.e(TAG, "onDraw : " + this.mState);

            // Check current state
            this.checkState();

            double holdStart = (Math.sqrt(2) + 2) / 2 * this.mRadius;
            double magnifierWidth = holdStart + this.mHoldLength / Math.sqrt(2);

            // Draw circular arc
            this.drawCircle(canvas, magnifierWidth);
            // Draw holding
            this.drawHolding(canvas, holdStart, magnifierWidth);
            // Draw input editor
            this.drawInputEditor(canvas, magnifierWidth);

            if (this.mState != State.FINISHED && this.mState != State.IDLE) {
                this.postInvalidateDelayed(INVALIDATE_INTERVEL);
            }
        }

        private long mStartTime;

        /**
         * Check current state and update data of animation
         */
        private void checkState() {
            if (!this.mIsRunning) {
                return;
            }
            long interval = (SystemClock.elapsedRealtime() - this.mStartTime) / INVALIDATE_INTERVEL;
            Log.e(TAG, "interval : " + interval);
            switch (this.mState) {
                case IDLE:
                    this.mState = State.WITH_CIRCLE;
                case WITH_CIRCLE:
                    if (this.mStartAngle >= TOTAL_ANGLE) {
                        this.mState = State.WITHOUT_CIRCLE;
                    } else {
                        this.mStartAngle += interval;
                        this.mSweepAngle -= interval;
                        break;
                    }
                case WITHOUT_CIRCLE:
                    // TODO
                    break;
                case ONLY_LINE:
                    // TODO
                    break;
                case FINISHED:
                    this.mAnimationListener.onAnimationEnd();
                    break;
            }


        }

        /**
         * Draw circular arc
         *
         * @param canvas
         * @param magnifierWidth
         */
        private void drawCircle(Canvas canvas, double magnifierWidth) {
            if (!(this.mState == State.IDLE || this.mState == State.WITH_CIRCLE)) {
                return;
            }

            float top = this.mStrokeWidth;
            float left = (float) ((this.getWidth() - magnifierWidth) / 2);
            float right = left + this.mRadius * 2;
            float bottom = top + this.mRadius * 2;

            RectF rectF = new RectF(left, top, right, bottom);
            canvas.drawArc(rectF, this.mStartAngle, this.mSweepAngle, false, this.mPaint);
        }

        /**
         * Draw holding
         *
         * @param canvas
         * @param holdStart
         * @param magnifierWidth
         */
        private void drawHolding(Canvas canvas, double holdStart, double magnifierWidth) {
            if (!(this.mState == State.IDLE || this.mState == State.WITH_CIRCLE
                    || this.mState == State.WITHOUT_CIRCLE)) {
                return;
            }

            int saveCount = canvas.save();
            float left = (float) ((this.getWidth() - magnifierWidth) / 2);
            canvas.translate(left + (float) holdStart, (float) holdStart);
            canvas.drawLine(0, 0, this.mHoldLength, this.mHoldLength, this.mPaint);
            canvas.restoreToCount(saveCount);
        }

        /**
         * Draw input editor
         * 从中心向两边扩散的一条线, 左边扩散速度稍快
         *
         * @param canvas
         */
        private void drawInputEditor(Canvas canvas, double magnifierWidth) {
            if (!(this.mState == State.WITH_CIRCLE || this.mState == State.WITHOUT_CIRCLE
                    || this.mState == State.ONLY_LINE)) {
                return;
            }

            int saveCount = canvas.save();
            float x = (float) (this.getWidth() / 2 + magnifierWidth / 2);
            float y = (float) magnifierWidth;
            canvas.translate(x, y);
            canvas.drawLine(0, y, 100, y, this.mPaint);
            canvas.restoreToCount(saveCount);
        }

        private interface AnimationListener {
            /**
             * <p>Notifies the start of the animation.</p>
             */
            void onAnimationStart();

            /**
             * <p>Notifies the end of the animation. This callback is not invoked
             * for animations with repeat count set to INFINITE.</p>
             */
            void onAnimationEnd();

        }

    }
}