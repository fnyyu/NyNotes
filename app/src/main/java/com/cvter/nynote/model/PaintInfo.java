package com.cvter.nynote.model;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;

import com.cvter.nynote.utils.Constants;


/**
 * Created by cvter on 2017/6/8.
 */

public class PaintInfo extends Paint {

    private int mDrawSize = 20;
    private int type = Constants.ORDINARY;
    private Constants.Mode mMode = Constants.Mode.DRAW;
    private Xfermode mClearMode;
    private int mPenColor = Color.BLACK;

    public PaintInfo(){

        super(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        init();

    }

    private void init() {
        setStyle(Paint.Style.STROKE);
        setFilterBitmap(true);
        setStrokeCap(Paint.Cap.ROUND);
        setColor(mPenColor);
        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.SRC);
    }

    public void setMode(Constants.Mode mode) {
        if (mode != mMode) {
            mMode = mode;
            if (mMode == Constants.Mode.DRAW) {
                setXfermode(null);
                setPenColor(mPenColor);
            } else {

                setXfermode(mClearMode);
                setStrokeWidth(40);
            }
        }
    }

    @Override
    public Xfermode setXfermode(Xfermode xfermode) {
        setColor(Color.WHITE);
        return super.setXfermode(xfermode);
    }

    public void setPenRawSize(int mDrawSize){
        this.mDrawSize = mDrawSize;
        this.setStrokeWidth(mDrawSize);
    }

    public int getPenRawSize(){
        return mDrawSize;
    }

    public void setPenColor(int color) {
        this.mPenColor = color;
        setColor(mPenColor);
    }

    public void setOrdinaryPen(){
        setAlpha(255);
        setStrokeCap(Paint.Cap.ROUND);
        setMaskFilter(null);
        setPathEffect(null);
    }

    public void setTransPen(){
        setAlpha(70);
        setStrokeCap(Paint.Cap.SQUARE);
        setMaskFilter(null);
        setPathEffect(null);
    }

    public void setInkPen(){
        setAlpha(255);
        setStrokeCap(Paint.Cap.ROUND);
        setMaskFilter(new BlurMaskFilter(mDrawSize, BlurMaskFilter.Blur.NORMAL));
        setPathEffect(null);
    }

    public void setDiscretePen(){
        setAlpha(255);
        setStrokeCap(Paint.Cap.ROUND);
        setMaskFilter(null);
        setPathEffect(new DiscretePathEffect(2.0F, 5.0F));
    }

    public void setDashPen(){
        setAlpha(255);
        setStrokeCap(Paint.Cap.ROUND);
        setMaskFilter(null);
        Path path = new Path();
        path.addCircle(0, 0, 3, Path.Direction.CCW);
        PathEffect pathEffect = new PathDashPathEffect(path, mDrawSize, mDrawSize, PathDashPathEffect.Style.ROTATE);
        setPathEffect(pathEffect);
    }

    public void setGraphType(int type){
        this.type = type;
    }

    public int getGraphType(){
        return type;
    }

    public Constants.Mode getMode() {
        return mMode;
    }

    public int getPenColor(){
        return mPenColor;
    }
}
