package com.cvter.nynote.Model;

import android.graphics.Canvas;

/**
 * Created by cvter on 2017/6/6.
 */

public class PathDrawingInfo extends PathInfo{

    @Override
    public void draw(Canvas canvas, int type) {
        canvas.drawPath(path, paint);
    }
}
