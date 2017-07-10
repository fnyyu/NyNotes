package com.cvter.nynote.polygon;

import android.graphics.Path;
import android.util.Log;

import com.cvter.nynote.utils.Constants;

/**
 * Created by cvter on 2017/7/10.
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
        Log.e(getClass().getName(), Constants.NO_OPERATION);
    }
}
