package com.cvter.nynote.polygon;

import android.graphics.Path;

/**
 * Created by cvter on 2017/7/8.
 * 图形绘制封装类
 */

public interface IPolygon {

    void drawPolygonPath(Path path, float startX, float startY, float endX, float endY);

    void drawDash(Path path, float dashRadiusX, float dashRadiusY, float dashX, float dashY);

}
