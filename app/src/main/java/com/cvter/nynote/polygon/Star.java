package com.cvter.nynote.polygon;

import android.graphics.Path;

/**
 * Created by cvter on 2017/7/8.
 * 五角星绘制类
 */

public class Star implements IPolygon {

    @Override
    public void drawPolygonPath(Path path, float startX, float startY, float endX, float endY) {
        path.rewind();
        float radius = endY - startY;
        path.moveTo(startX, startY - radius);
        float spaceX = (float) (radius * (Math.sin(Math.PI * 72 / 180)));
        float spaceY = (float) (radius * (Math.cos(Math.PI * 72 / 180)));

        float spaceX2 = (float) (radius * (Math.sin(Math.PI * 36 / 180)));
        float spaceY2 = (float) (radius * (Math.cos(Math.PI * 36 / 180)));

        path.lineTo((startX + spaceX2), (startY + spaceY2));
        path.moveTo((startX + spaceX2), (startY + spaceY2));

        path.lineTo((startX - spaceX), (startY - spaceY));
        path.moveTo((startX - spaceX), (startY - spaceY));

        path.lineTo((startX + spaceX), (startY - spaceY));
        path.moveTo((startX + spaceX), (startY - spaceY));

        path.lineTo((startX - spaceX2), (startY + spaceY2));
        path.moveTo((startX - spaceX2), (startY + spaceY2));


        path.lineTo(startX, startY - radius);
    }

    @Override
    public void drawDash(Path path, float dashRadiusX, float dashRadiusY, float dashX, float dashY) {
        //do nothing
    }
}
