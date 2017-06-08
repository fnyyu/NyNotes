package com.cvter.nynote.Model;

import android.graphics.Canvas;
import android.graphics.Path;

import com.cvter.nynote.Utils.CommonUtils;

/**
 * Created by cvter on 2017/6/6.
 */

public class PathDrawingInfo extends PathInfo{

    public Path path;
    public float startX;
    public float startY;
    public float stopX;
    public float stopY;

    @Override
    public void draw(Canvas canvas, int type) {
        switch (type){
            case CommonUtils.ODINARY:
                canvas.drawPath(path, paint);
                break;

            case CommonUtils.CIRCLE:
                canvas.drawCircle(startX, startY, 150, paint);
                break;

            case CommonUtils.LINE:
                canvas.drawLine(startX, startY, stopX, stopY, paint);
                break;

            case CommonUtils.SQUARE:
                break;

            case CommonUtils.SPHERE:
                break;

            case CommonUtils.CONE:
                break;

            case CommonUtils.CUBE:
                break;
        }

    }

    @Override
    public void setStartX(float startX) {

    }

    @Override
    public void setStartY(float startY) {

    }

    @Override
    public void setStopX(float stopX) {

    }

    @Override
    public void setStopY(float stopY) {

    }

    @Override
    public float getStartX() {
        return startX;
    }

    @Override
    public float getStartY() {
        return startY;
    }

    @Override
    public float getStopX() {
        return stopX;
    }

    @Override
    public float getStopY() {
        return stopY;
    }


}
