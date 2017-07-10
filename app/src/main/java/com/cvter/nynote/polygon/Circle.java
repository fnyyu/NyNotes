package com.cvter.nynote.polygon;

import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import com.cvter.nynote.polygon.IPolygon;
import com.cvter.nynote.utils.Constants;

public class Circle implements IPolygon{

    @Override
    public void drawPolygonPath(Path path, float startX, float startY, float endX, float endY) {
        path.rewind();
        RectF rectF = new RectF(startX, startY, endX, endY);
        path.addOval(rectF, Path.Direction.CW);
    }

    @Override
    public void drawDash(Path path, float dashRadiusX, float dashRadiusY, float dashX, float dashY) {
        Log.e(getClass().getName(), Constants.NO_OPERATION);
    }
}
