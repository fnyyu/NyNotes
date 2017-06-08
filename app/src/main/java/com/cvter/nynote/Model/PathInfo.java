package com.cvter.nynote.Model;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by cvter on 2017/6/6.
 */

public abstract class PathInfo {

    public Paint paint;
    public float startX;
    public float startY;
    public float stopX;
    public float stopY;

    public abstract void draw(Canvas canvas, int type);

    public abstract void setStartX(float startX);

    public abstract void setStartY(float startY);

    public abstract void setStopX(float stopX);

    public abstract void setStopY(float stopY);

    public abstract float getStartX();

    public abstract float getStartY();

    public abstract float getStopX();

    public abstract float getStopY();
}
