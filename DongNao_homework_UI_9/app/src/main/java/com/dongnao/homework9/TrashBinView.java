package com.dongnao.homework9;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by KK on 2017/5/23.
 * 点击开垃圾箱，再点关垃圾箱
 */

public class TrashBinView extends AppCompatImageView implements TrashBin {

    private TrashBinDrawable mTrashBinDrawable;

    public TrashBinView(Context context) {
        this(context, null);
    }

    public TrashBinView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrashBinView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mTrashBinDrawable = new TrashBinDrawable();
        super.setImageDrawable(this.mTrashBinDrawable);
    }

    @Override
    public void open() {
        this.mTrashBinDrawable.open();
    }

    @Override
    public void close() {
        this.mTrashBinDrawable.close();
    }

    @Override
    public boolean isOpened() {
        return this.mTrashBinDrawable.isOpened();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        throw new UnsupportedOperationException(
                "setImageResource(int) is not supported in TrashBinView");
    }
}