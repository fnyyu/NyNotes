package com.cvter.nynote.Model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by cvter on 2017/6/6.
 */

public abstract class PathInfo {

    public Path path;
    public Paint paint;
    public ArrayList<ArrayList<PointInfo>> lines;


    public abstract void draw(Canvas canvas, int type);

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setLines(ArrayList<ArrayList<PointInfo>> lines) {
        this.lines = lines;
    }

    public ArrayList<ArrayList<PointInfo>> getLines() {
        return lines;
    }

    public Paint getPaint() {
        return paint;
    }

    public Path getPath() {
        return path;
    }

}
