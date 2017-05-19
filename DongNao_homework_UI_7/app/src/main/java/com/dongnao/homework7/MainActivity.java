package com.dongnao.homework7;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mLinearLayout;

    private RevealHolder[] mDrawables = { new RevealHolder(R.drawable.avft_active, R.drawable.avft),
            new RevealHolder(R.drawable.box_stack_active, R.drawable.box_stack),
            new RevealHolder(R.drawable.bubble_frame_active, R.drawable.bubble_frame),
            new RevealHolder(R.drawable.bubbles_active, R.drawable.bubbles),
            new RevealHolder(R.drawable.bullseye_active, R.drawable.bullseye),
            new RevealHolder(R.drawable.circle_filled_active, R.drawable.circle_filled),
            new RevealHolder(R.drawable.circle_outline_active, R.drawable.circle_outline) };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.mLinearLayout = (LinearLayout) this.findViewById(R.id.linearLayout);
        for (int i = 0; i < this.mDrawables.length; i++) {
            RevealView view = new RevealView(this);
            view.setDrawable(this.mDrawables[i].activeId, this.mDrawables[i].inActiveId);
            view.setRevealPercent((float) (1.0 / (i + 1)));
            this.mLinearLayout.addView(view);
        }
        // TODO
        // this.findViewById(R.id.scrollView).setOnScrollChangeListener(new
        // View.OnScrollChangeListener() {
        // @Override
        // public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX,
        // int oldScrollY) {
        // for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
        // View child = mLinearLayout.getChildAt(i);
        // float start = mLinearLayout.getLeft()
        // + (mLinearLayout.getWidth() - child.getWidth()) / 2;
        // float end = start + child.getWidth();
        // if (child.getX() >= start && child.getX() <= end) {
        // ((RevealView) child).setRevealPercent(0);
        // } else {
        // ((RevealView) child).setRevealPercent(1);
        // }
        // }
        // }
        // });
    }

    static class RevealHolder {
        int activeId;
        int inActiveId;

        RevealHolder(int activeId, int inActiveId) {
            this.activeId = activeId;
            this.inActiveId = inActiveId;
        }
    }
}
