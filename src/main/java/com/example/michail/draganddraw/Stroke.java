package com.example.michail.draganddraw;

import android.graphics.Path;
import android.graphics.PointF;

public class Stroke {
    public int color;
    public int strokeWidth;
    public int drawIndex;
    public Path path;
    private PointF mOrigin;


    public Stroke(int color, int strokeWidth, Path path, int drawIndex) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
        this.drawIndex = drawIndex;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public void setOrigin(PointF mOrigin) {
        this.mOrigin = mOrigin;
    }
}
