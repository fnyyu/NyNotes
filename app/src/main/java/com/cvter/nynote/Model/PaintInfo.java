package com.cvter.nynote.Model;

import android.graphics.BlurMaskFilter;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;

import com.cvter.nynote.Utils.CommonUtils;


/**
 * Created by cvter on 2017/6/8.
 */

public class PaintInfo extends Paint {

    private int mDrawSize;
    private int type = CommonUtils.ODINARY;
    private boolean isAlpha;
    private CommonUtils.Mode mMode = CommonUtils.Mode.DRAW;
    private Xfermode mClearMode;

    public PaintInfo(){

        super(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        init();

    }

    private void init() {
        setStyle(Paint.Style.STROKE);
        setFilterBitmap(true);
        setStrokeCap(Paint.Cap.ROUND);
        setColor(0XFF000000);
        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    public void setMode(CommonUtils.Mode mode) {
        if (mode != mMode) {
            mMode = mode;
            if (mMode == CommonUtils.Mode.DRAW) {
                setXfermode(null);
            } else {
                setXfermode(mClearMode);
                setStrokeWidth(40);
            }
        }
    }

    public void setPenRawSize(int mDrawSize){
        this.mDrawSize = mDrawSize;
        this.setStrokeWidth(mDrawSize);
    }

    public void setPenColor(int color) {
        setColor(color);
    }

    public boolean getIsAlpha() {
        return isAlpha;
    }

    public void setOrdinaryPen(){
        isAlpha = false;
        setAlpha(255);
        setStrokeCap(Paint.Cap.ROUND);
        setMaskFilter(null);
        setPathEffect(null);
    }

    public void setTransPen(){
        isAlpha = true;
        setAlpha(100);
        setStrokeCap(Paint.Cap.SQUARE);
        setMaskFilter(null);
        setPathEffect(null);
    }

    public void setInkPen(){
        isAlpha = false;
        setAlpha(255);
        setStrokeCap(Paint.Cap.ROUND);
        setMaskFilter(new BlurMaskFilter(mDrawSize, BlurMaskFilter.Blur.NORMAL));
        setPathEffect(null);
    }

    public void setDiscretePen(){
        isAlpha = false;
        setAlpha(255);
        setStrokeCap(Paint.Cap.ROUND);
        setMaskFilter(null);
        setPathEffect(new DiscretePathEffect(2.0F, 5.0F));
    }

    public void setDashPen(){
        isAlpha = false;
        setAlpha(255);
        setStrokeCap(Paint.Cap.ROUND);
        setMaskFilter(null);
        Path path = new Path();
        path.addCircle(0, 0, 3, Path.Direction.CCW);
        PathEffect pathEffect = new PathDashPathEffect(path, mDrawSize, mDrawSize, PathDashPathEffect.Style.ROTATE);
        setPathEffect(pathEffect);
    }

    public void setStroke(Paint.Cap strokeCap){
        this.setStrokeCap(strokeCap);
    }

    public void setGraphType(int type){
        this.type = type;
    }

    public int getGraphType(){
        return type;
    }

    public CommonUtils.Mode getMode() {
        return mMode;
    }
}
