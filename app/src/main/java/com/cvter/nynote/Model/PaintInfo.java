package com.cvter.nynote.Model;

import android.graphics.BlurMaskFilter;
import android.graphics.Paint;

import com.cvter.nynote.Utils.CommonUtils;


/**
 * Created by cvter on 2017/6/8.
 */

public class PaintInfo extends Paint {

    private int mDrawSize;
    public int type = CommonUtils.ODINARY;
    public boolean isAlpha;

    public PaintInfo(){

        super(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        init();

    }

    private void init() {

        this.setStyle(Paint.Style.STROKE);
        this.setFilterBitmap(true);
        this.setStrokeCap(Paint.Cap.ROUND);
        this.setColor(0XFF000000);
    }

    public void setPenRawSize(int mDrawSize){
        this.mDrawSize = mDrawSize;
        this.setStrokeWidth(mDrawSize);
    }

    public void setPenColor(int color) {
        this.setColor(color);
    }

    public boolean getIsAlpha() {
        return isAlpha;
    }

    public void setPenAlpha(boolean isAlpha) {
        this.isAlpha = isAlpha;
        if(isAlpha){
            this.setAlpha(100);
        }else{
            this.setAlpha(255);
        }

    }

    public void setStrokeCap(Paint.Cap strokeCap){
        //this.setStrokeCap(strokeCap);
    }

    public void setMaskFilter(boolean isSetMask){
        if(isSetMask){
            this.setMaskFilter(new BlurMaskFilter(mDrawSize, BlurMaskFilter.Blur.NORMAL));
        }else{
            this.setMaskFilter(null);
        }

    }

    public void setGraphType(int type){
        this.type = type;
    }

    public int getGrapgType(){
        return type;
    }


}
