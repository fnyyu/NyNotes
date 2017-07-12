package com.cvter.nynote.polygon;

import android.graphics.Path;

/**
 * Created by cvter on 2017/7/8.
 * 椎体绘制类
 */

public class Cone implements IPolygon {

    @Override
    public void drawPolygonPath(Path path, float startX, float startY, float endX, float endY) {

        path.rewind();
        float radius = endX - startX;
        float radiusY = endY - startY;

        path.moveTo(startX + radius / 6, startY);

        float spaceX = radius / 2;
        float spaceY = radius * 2;
        float spaceX2 = radius * 3 / 2;
        float spaceY2 = radius * 3 / 2;

        if(radiusY * radius < 0){
            spaceY = -spaceY;
            spaceY2 = -spaceY2;
        }

        path.lineTo(startX - spaceX, startY + spaceY);
        path.lineTo(startX + radius, startY + spaceY);
        path.lineTo(startX + radius / 6, startY);
        path.lineTo(startX + spaceX2, startY + spaceY2);
        path.lineTo(startX + radius, startY + spaceY);
    }

    @Override
    public void drawDash(Path path, float dashRadiusX, float dashRadiusY, float dashX, float dashY) {

        float spaceX = dashRadiusX / 2;
        float spaceY = dashRadiusX * 2;
        float spaceX2 = dashRadiusX * 3 / 2;
        float spaceY2 = dashRadiusX * 3 / 2;

        if (dashRadiusX * dashRadiusY < 0) {
            spaceY = -spaceY;
            spaceY2 = -spaceY2;
        }

        path.moveTo(dashX + spaceX2, dashY + spaceY2);
        path.lineTo(dashX, dashY + spaceY2);
        path.lineTo(dashX - spaceX, dashY + spaceY);
        path.moveTo(dashX + dashRadiusX / 6, dashY);
        path.lineTo(dashX, dashY + spaceY2);
    }
}
