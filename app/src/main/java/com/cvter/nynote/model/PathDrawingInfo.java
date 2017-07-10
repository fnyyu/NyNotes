package com.cvter.nynote.model;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Path;

import com.cvter.nynote.utils.CommonMethod;
import com.cvter.nynote.utils.Constants;

import java.util.List;

/**
 * Created by cvter on 2017/6/6.
 * 路径绘制类
 */

public class PathDrawingInfo extends PathInfo {

    @Override
    public void draw(Canvas canvas, int type, List<PointInfo> info) {
        canvas.drawPath(getPath(), getPaint());

        if (info.size()>1) {
            float endX = info.get(1).mPointX;
            float endY = info.get(1).mPointY;
            float startX = info.get(0).mPointX;
            float startY = info.get(0).mPointY;

            getPaint().setPathEffect(new DashPathEffect(new float[]{5, 20}, 1));
            Path path = new Path();
            CommonMethod.handleGraphType(path, startX, startY, endX, endY, type, Constants.POLYGON);
            canvas.drawPath(path, getPaint());
            getPaint().setPathEffect(null);

        }

    }
}
