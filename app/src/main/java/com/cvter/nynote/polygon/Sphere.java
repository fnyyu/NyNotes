package com.cvter.nynote.polygon;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by cvter on 2017/7/8.
 * 球体绘制类
 */

public class Sphere implements IPolygon {

    @Override
    public void drawPolygonPath(Path path, float startX, float startY, float endX, float endY) {
        path.rewind();
        float radius = endY - startY > 0 ? endY - startY : startY - endY;
        
        path.addCircle(startX, startY, radius, Path.Direction.CW);
        RectF rectF = new RectF(startX - radius, startY - radius / 2.7f, startX + radius, startY + radius / 2.7f);
        path.addArc(rectF, 0, 180);
    }

    @Override
    public void drawDash(Path path, float dashRadiusX, float dashRadiusY, float dashX, float dashY) {

        float radius = dashRadiusY > 0 ? dashRadiusY : -dashRadiusY;

        RectF rectF = new RectF(dashX - radius, dashY - radius / 2.7f, dashX + radius, dashY + radius / 2.7f);
        path.addArc(rectF, 0, -180);
    }
}
