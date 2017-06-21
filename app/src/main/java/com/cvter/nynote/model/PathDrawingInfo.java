package com.cvter.nynote.model;

import android.graphics.Canvas;

/**
 * Created by cvter on 2017/6/6.
 * 路径绘制类
 */

public class PathDrawingInfo extends PathInfo {

    @Override
    public void draw(Canvas canvas, int type) {
        canvas.drawPath(getPath(), getPaint());
    }
}
