package com.dongnao.homework5;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyikun on 2017/5/15.
 * 待完善
 */

public class BasicGraphStrategy implements GraphStrategy {

    private List<Path> mScratchedArea;

    private Path mCurrentPath;

    public BasicGraphStrategy() {
        this.mScratchedArea = new ArrayList<>();
    }

    @Override
    public void moveTo(float x, float y) {
        this.mCurrentPath = new Path();
        this.mScratchedArea.add(this.mCurrentPath);
        this.mCurrentPath.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y) {
        this.mCurrentPath.lineTo(x, y);
    }

    @Override
    public List<Path> getPaths() {
        return this.mScratchedArea;
    }

    @Override
    public float getPathArea() {
        return this.mScratchedArea.size();
    }

    @Override
    public void reset() {
        this.mScratchedArea.clear();
    }
}
