package com.cvter.nynote.polygon;

import android.graphics.Path;

/**
 * Created by cvter on 2017/7/8.
 * 正方体绘制类
 */

public class Cube implements IPolygon {

    @Override
    public void drawPolygonPath(Path path, float startX, float startY, float endX, float endY) {
        path.rewind();
        path.moveTo(startX, startY);

        float radius = endX - startX;

        float spaceX = (radius * 1) / 3;
        float spaceY = (radius * 1) / 3;
        float spaceX2 = (radius * 4) / 3;
        float spaceY2 = (radius * 2) / 3;

        if (radius > 0) {
            path.lineTo(startX + radius, startY);
            path.lineTo(startX + radius, startY + radius);
            path.lineTo(startX, startY + radius);//
            path.lineTo(startX, startY);
            path.lineTo(startX + spaceX, startY - spaceY);
            path.lineTo(startX + spaceX2, startY - spaceY);//
            path.lineTo(startX + spaceX2, startY + spaceY2);
            path.moveTo(startX + radius, startY + radius);
            path.lineTo(startX + spaceX2, startY + spaceY2);//
            path.moveTo(startX + radius, startY);
            path.lineTo(startX + spaceX2, startY - spaceY);
        } else {
            path.lineTo(startX + radius, startY);
            path.lineTo(startX + radius, startY - radius);
            path.lineTo(startX, startY - radius);
            path.lineTo(startX, startY);
            path.lineTo(startX + spaceX, startY + spaceY);
            path.lineTo(startX + spaceX2, startY + spaceY);
            path.lineTo(startX + spaceX2, startY - spaceY2);
            path.moveTo(startX + radius, startY - radius);
            path.lineTo(startX + spaceX2, startY - spaceY2);
            path.moveTo(startX + radius, startY);
            path.lineTo(startX + spaceX2, startY + spaceY);
        }

    }

    @Override
    public void drawDash(Path path, float dashRadiusX, float dashRadiusY, float dashX, float dashY) {
        float spaceX = (dashRadiusX * 1) / 3;
        float spaceY = (dashRadiusX * 1) / 3;
        float spaceX2 = (dashRadiusX * 4) / 3;
        float spaceY2 = (dashRadiusX * 2) / 3;

        if (dashRadiusX > 0) {
            path.moveTo(dashX, dashY + dashRadiusX);
            path.lineTo(dashX + spaceX, dashY + spaceY2);
            path.lineTo(dashX + spaceX, dashY - spaceY);
            path.moveTo(dashX + spaceX, dashY + spaceY2);
            path.lineTo(dashX + spaceX2, dashY + spaceY2);
        } else {
            path.moveTo(dashX, dashY - dashRadiusX);
            path.lineTo(dashX + spaceX, dashY - spaceY2);
            path.lineTo(dashX + spaceX, dashY + spaceY);
            path.moveTo(dashX + spaceX, dashY - spaceY2);
            path.lineTo(dashX + spaceX2, dashY - spaceY2);
        }
    }
}
