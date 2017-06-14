package com.cvter.nynote.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cvter on 2017/6/6.
 */

public abstract class PathInfo {

    private Path mPath;
    private Paint mPaint;
    private ArrayList<ArrayList<PointInfo>> mLines;


    public abstract void draw(Canvas canvas, int type);

    public void setPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public void setPath(Path mPath) {
        this.mPath = mPath;
    }

    public void List(ArrayList<ArrayList<PointInfo>> lines) {
        this.mLines = lines;
    }

    public List<ArrayList<PointInfo>> getmLines() {
        return mLines;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public Path getPath() {
        return mPath;
    }

}
