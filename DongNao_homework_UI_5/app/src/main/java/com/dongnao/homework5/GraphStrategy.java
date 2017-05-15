package com.dongnao.homework5;

import android.graphics.Path;

import java.util.List;

/**
 * Created by liyikun on 2017/5/15.
 */

public interface GraphStrategy {

    void reset();

    void moveTo(float x, float y);

    void lineTo(float x, float y);

    List<Path> getPaths();

    float getPathArea();
}
