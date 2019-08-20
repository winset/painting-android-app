package com.example.michail.draganddraw;

import android.graphics.Paint;
import android.graphics.PointF;

public class Shape {
    private PointF mOrigin;
    private PointF mCurrent;
    private PointF mCenterOfRotation;
    private Paint mColor;

    private int mDrawIndex;

    private float mRotationAngel;

    public Shape(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF mCurrent) {
        this.mCurrent = mCurrent;
    }

    public PointF getCenterOfRotation() {
        return mCenterOfRotation;
    }

    public void setCenterOfRotation(PointF mCentetOfRotation) {
        this.mCenterOfRotation = mCentetOfRotation;
    }

    public float getRotationAngel() {
        return mRotationAngel;
    }

    public void setRotationAngel(float mRotationAngel) {
        this.mRotationAngel = mRotationAngel;
    }

    public Paint getColor() {
        return mColor;
    }

    public void setColor(Paint mColor) {
        this.mColor = mColor;
    }


    public int getDrawIndex() {
        return mDrawIndex;
    }

    public void setDrawIndex(int mDrawIndex) {
        this.mDrawIndex = mDrawIndex;
    }
}
