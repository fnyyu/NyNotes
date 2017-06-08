package com.cvter.nynote.Model;

import android.graphics.Canvas;
import android.graphics.Path;

/**
 * Created by cvter on 2017/6/6.
 */

public class PathDrawingInfo extends PathInfo{

    public Path path;

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }
}
