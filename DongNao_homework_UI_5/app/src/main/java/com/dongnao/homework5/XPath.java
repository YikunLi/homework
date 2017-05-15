package com.dongnao.homework5;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyikun on 2017/5/15.
 */

public class XPath extends Path {

    private List<XPoint> mPoints = new ArrayList<>();

    public XPath() {
        super();
    }

    public XPath(Path src) {
        super(src);
    }

    @Override
    public void moveTo(float x, float y) {
        super.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y) {
        super.lineTo(x, y);
    }

    public List<XPoint> getPoints() {
        return this.mPoints;
    }
}
