package com.dongnao.homework5;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.PrintWriter;

/**
 * Created by liyikun on 2017/5/15.
 */

public class XPoint implements Parcelable {

    public float x;
    public float y;

    public XPoint() {
    }

    public XPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public XPoint(XPoint src) {
        this.x = src.x;
        this.y = src.y;
    }

    /**
     * Set the point's x and y coordinates
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Negate the point's coordinates
     */
    public final void negate() {
        x = -x;
        y = -y;
    }

    /**
     * Offset the point's coordinates by dx, dy
     */
    public final void offset(float dx, float dy) {
        x += dx;
        y += dy;
    }

    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    public final boolean equals(float x, float y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        XPoint point = (XPoint) o;

        if (x != point.x)
            return false;
        if (y != point.y)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        float result = x;
        result = 31 * result + y;
        return (int) result;
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }

    /** @hide */
    public void printShortString(PrintWriter pw) {
        pw.print("[");
        pw.print(x);
        pw.print(",");
        pw.print(y);
        pw.print("]");
    }

    /**
     * Parcelable interface methods
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write this point to the specified parcel. To restore a point from
     * a parcel, use readFromParcel()
     * @param out The parcel to write the point's coordinates into
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(x);
        out.writeFloat(y);
    }

    public static final Parcelable.Creator<XPoint> CREATOR = new Parcelable.Creator<XPoint>() {
        /**
         * Return a new point from the data in the specified parcel.
         */
        public XPoint createFromParcel(Parcel in) {
            XPoint r = new XPoint();
            r.readFromParcel(in);
            return r;
        }

        /**
         * Return an array of rectangles of the specified size.
         */
        public XPoint[] newArray(int size) {
            return new XPoint[size];
        }
    };

    /**
     * Set the point's coordinates from the data stored in the specified
     * parcel. To write a point to a parcel, call writeToParcel().
     * @param in The parcel to read the point's coordinates from
     */
    public void readFromParcel(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
    }
}