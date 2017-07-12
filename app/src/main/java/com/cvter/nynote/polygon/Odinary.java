package com.cvter.nynote.polygon;

import android.graphics.Path;

/**
 * Created by cvter on 2017/7/10.
 * 普通画笔绘制类
 */

public class Odinary implements IPolygon {
    @Override
    public void drawPolygonPath(Path path, float startX, float startY, float endX, float endY) {
        float x = (endX + startX) / 2;
        float y = (endY + startY) / 2;
        path.quadTo(startX, startY, x, y);
    }

    @Override
    public void drawDash(Path path, float dashRadiusX, float dashRadiusY, float dashX, float dashY) {
        //do nothing
    }
}
