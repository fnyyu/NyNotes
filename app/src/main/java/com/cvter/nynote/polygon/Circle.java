package com.cvter.nynote.polygon;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by serenefang on 2017/7/8.
 * 圆形绘制类
 */

public class Circle implements IPolygon{

    @Override
    public void drawPolygonPath(Path path, float startX, float startY, float endX, float endY) {
        path.rewind();
        RectF rectF = new RectF(startX, startY, endX, endY);
        path.addOval(rectF, Path.Direction.CW);
    }

    @Override
    public void drawDash(Path path, float dashRadiusX, float dashRadiusY, float dashX, float dashY) {
        //do nothing
    }
}
