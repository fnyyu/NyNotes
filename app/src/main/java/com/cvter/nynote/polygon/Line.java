package com.cvter.nynote.polygon;

import android.graphics.Path;
import android.util.Log;

import com.cvter.nynote.utils.Constants;

/**
 * Created by cvter on 2017/7/8.
 */

public class Line implements IPolygon {
    @Override
    public void drawPolygonPath(Path path, float startX, float startY, float endX, float endY) {
        path.rewind();
        path.moveTo(startX, startY);
        path.lineTo(endX, endY);
    }

    @Override
    public void drawDash(Path path, float dashRadiusX, float dashRadiusY, float dashX, float dashY) {
        Log.e(getClass().getName(), Constants.NO_OPERATION);
    }
}
