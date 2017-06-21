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

    private int mDrawSize = 20;
    private int type = Constants.ORDINARY;
    private Constants.Mode mMode = Constants.Mode.DRAW;
    private Xfermode mClearMode;
    private int mPenColor = Color.BLACK;
    private int mPenType = Constants.ORDINARY_PEN;

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
        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    //设置画笔模式
    public void setMode(Constants.Mode mode) {
        if (mode != mMode) {
            mMode = mode;
            if (mMode == Constants.Mode.DRAW) {
                setXfermode(null);
                setPenColor(mPenColor);
            } else {
                setAlpha(0);
                //setColor(Color.WHITE);
                setXfermode(mClearMode);
                setStrokeWidth(40);

            }
        }
    }

    //得到int类型的画笔模式
    public int getIntMode() {
        if (mMode == Constants.Mode.DRAW) {
            return 0;
        } else {
            return 1;
        }

    }

    //设置画笔粗细
    public void setPenRawSize(int mDrawSize) {
        this.mDrawSize = mDrawSize;
        this.setStrokeWidth(mDrawSize);
    }

    //得到画笔粗细
    public int getPenRawSize() {
        return mDrawSize;
    }

    //设置画笔颜色
    public void setPenColor(int color) {
        this.mPenColor = color;
        setColor(mPenColor);
    }

    //设置为普通笔
    public void setOrdinaryPen() {
        mPenType = Constants.ORDINARY_PEN;
        setAlpha(255);
        setMaskFilter(null);
        setPathEffect(null);
    }

    //设置为透明笔
    public void setTransPen() {
        mPenType = Constants.TRANS_PEN;
        setAlpha(70);
        setMaskFilter(null);
        setPathEffect(null);
    }

    //设置为水彩笔
    public void setInkPen() {
        mPenType = Constants.INK_PEN;
        setAlpha(255);
        setMaskFilter(new BlurMaskFilter(mDrawSize, BlurMaskFilter.Blur.NORMAL));
        setPathEffect(null);
    }

    //设置为虚线笔
    public void setDiscretePen() {
        mPenType = Constants.DISCRETE_PEN;
        setAlpha(255);
        setMaskFilter(null);
        setPathEffect(new DiscretePathEffect(2.0F, 5.0F));
    }

    //设置为离散路径笔
    public void setDashPen() {
        mPenType = Constants.DASH_PEN;
        setAlpha(255);
        setMaskFilter(null);
        Path path = new Path();
        path.addCircle(0, 0, 3, Path.Direction.CCW);
        PathEffect pathEffect = new PathDashPathEffect(path, mDrawSize, mDrawSize, PathDashPathEffect.Style.ROTATE);
        setPathEffect(pathEffect);
    }

    //设置立体图形虚线
    public void setIfDottedPen() {
        setPathEffect(new DashPathEffect(new float[]{5, 20}, 1));
    }

    //设置画笔图形类别
    public void setGraphType(int type) {
        this.type = type;
    }

    //得到画笔图形类别
    public int getGraphType() {
        return type;
    }

    //得到画笔类型
    public int getPenType() {
        return mPenType;
    }

    //得到画笔模式
    public Constants.Mode getMode() {
        return mMode;
    }

}
