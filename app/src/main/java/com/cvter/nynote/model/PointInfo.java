package com.cvter.nynote.model;

import java.io.Serializable;

/**
 * Created by serenefang on 2017/6/10.
 * 路径点坐标bean类
 */

public class PointInfo implements Serializable {

    public float mPointX;
    public float mPointY;

    public PointInfo() {
    }

    public PointInfo(float x, float y) {
        this.mPointX = x;
        this.mPointY = y;
    }
}