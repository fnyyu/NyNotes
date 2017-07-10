package com.cvter.nynote.polygon;

import android.graphics.Path;
import android.util.Log;

import com.cvter.nynote.utils.Constants;

/**
 * Created by cvter on 2017/7/8.
 */

public class Delta implements IPolygon {

    @Override
    public void drawPolygonPath(Path path, float startX, float startY, float endX, float endY) {
        path.rewind();
        path.moveTo(startX, startY);
        float radius = (endY - startY) * 2 / 3;
        float spaceX = (float) (radius * (Math.sin(Math.PI * 60 / 180)));
        float spaceY = (float) (radius + radius * (Math.cos(Math.PI * 60 / 180)));
        path.lineTo((startX + spaceX), (startY + spaceY));
        path.moveTo((startX + spaceX), (startY + spaceY));
        path.lineTo((startX - spaceX), (startY + spaceY));
        path.moveTo((startX - spaceX), (startY + spaceY));
        path.lineTo(startX, startY);
    }

    @Override
    public void drawDash(Path path, float dashRadiusX, float dashRadiusY, float dashX, float dashY) {
        Log.e(getClass().getName(), Constants.NO_OPERATION);
    }
}
