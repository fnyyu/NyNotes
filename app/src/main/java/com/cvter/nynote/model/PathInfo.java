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
    private ArrayList<PointInfo> mPointList;
    private int mPaintType;
    private int mGraphType;

    public PathInfo(){}

    public abstract void draw(Canvas canvas, int type);

    public void setPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public void setPath(Path mPath) {
        this.mPath = mPath;
    }

    public void setPaintType(int mPaintType) {
        this.mPaintType = mPaintType;
    }

    public void setPointList(ArrayList<PointInfo> mPointList) {
        this.mPointList = mPointList;
    }

    public void setGraphType(int mGraphType) {
        this.mGraphType = mGraphType;
    }

    public int getGraphType() {
        return mGraphType;
    }

    public ArrayList<PointInfo> getPointList() {
        return mPointList;
    }

    public int getPaintType() {
        return mPaintType;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public Path getPath() {
        return mPath;
    }


}
