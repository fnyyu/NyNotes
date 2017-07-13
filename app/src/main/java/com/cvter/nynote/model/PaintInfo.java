package com.cvter.nynote.model;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;
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
 * 画笔封装类
 */

public class PaintInfo extends Paint {

    private int mDrawSize = 7;
    private int type = Constants.ORDINARY;
    private int mMode = Constants.DRAW;
    private Xfermode mClearMode;
    private int mPenColor = Color.BLACK;
    private int mPenType = Constants.ORDINARY_PEN;
    private int mBeforeColor;

    public PaintInfo() {
        super(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        init();
    }

    private void init() {
        setStyle(Paint.Style.STROKE);
        setFilterBitmap(true);
        setStrokeCap(Paint.Cap.ROUND);
        setStrokeJoin(Join.ROUND);
        setColor(mPenColor);
        mBeforeColor = mPenColor;
        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    public void setMode(int mode) {
        if (mode != mMode) {
            mMode = mode;
            if (mMode == Constants.DRAW ) {
                setXfermode(null);
                if(getColor() == Color.WHITE){
                    setPenColor(mBeforeColor);
                } else {
                    setPenColor(mPenColor);
                }

            } else if(mMode == Constants.CUT){
                setXfermode(null);
                mBeforeColor = getColor();
                setPenColor(Color.WHITE);
            } else {
                setAlpha(0);
                setXfermode(mClearMode);
                setStrokeWidth(40);

            }
        }
    }

    public int getMode() {
        return mMode;
    }

    public void setPenRawSize(int mDrawSize) {
        this.mDrawSize = mDrawSize;
        this.setStrokeWidth(mDrawSize);
    }

    public int getPenRawSize() {
        return mDrawSize;
    }

    public void setPenColor(int color) {
        this.mPenColor = color;
        setColor(mPenColor);
    }

    public void setOrdinaryPen() {
        mPenType = Constants.ORDINARY_PEN;
        setAlpha(255);
        setMaskFilter(null);
        setPathEffect(null);
    }

    public void setTransPen() {
        mPenType = Constants.TRANS_PEN;
        setAlpha(70);
        setMaskFilter(null);
        setPathEffect(null);
    }

    public void setInkPen() {
        mPenType = Constants.INK_PEN;
        setAlpha(255);
        setMaskFilter(new BlurMaskFilter(mDrawSize, BlurMaskFilter.Blur.NORMAL));
        setPathEffect(null);
    }

    public void setDiscretePen() {
        mPenType = Constants.DISCRETE_PEN;
        setAlpha(255);
        setMaskFilter(null);
        setPathEffect(new DiscretePathEffect(2.0F, 5.0F));
    }

    public void setDashPen() {
        mPenType = Constants.DASH_PEN;
        setAlpha(255);
        setMaskFilter(null);
        Path path = new Path();
        path.addCircle(0, 0, 3, Path.Direction.CCW);
        PathEffect pathEffect = new PathDashPathEffect(path, mDrawSize, mDrawSize, PathDashPathEffect.Style.ROTATE);
        setPathEffect(pathEffect);
    }

    public void setIsDottedPen() {
        setPathEffect(new DashPathEffect(new float[]{5, 20}, 1));

    }

    public void setNoDottedPen() {
        setPathEffect(null);
    }

    public void setGraphType(int type) {
        this.type = type;
    }

    public int getGraphType() {
        return type;
    }

    public int getPenType() {
        return mPenType;
    }


}
