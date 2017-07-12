package com.cvter.nynote.polygon;

import android.graphics.Path;

/**
 * Created by cvter on 2017/7/8.
 * 矩形绘制类
 */

public class Square implements IPolygon {
    @Override
    public void drawPolygonPath(Path path, float startX, float startY, float endX, float endY) {
        path.rewind();
        path.addRect(Math.min(startX, endX), Math.min(startY, endY), Math.max(endX, startX), Math.max(endY, startY), Path.Direction.CW);
    }

    @Override
    public void drawDash(Path path, float dashRadiusX, float dashRadiusY, float dashX, float dashY) {
        //do nothing
    }
}
