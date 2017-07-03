package com.cvter.nynote.model;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;

import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.DrawPolygon;

import java.util.List;

/**
 * Created by cvter on 2017/6/6.
 * 路径绘制类
 */

public class PathDrawingInfo extends PathInfo {

    private DrawPolygon mDrawPolygon = new DrawPolygon();

    @Override
    public void draw(Canvas canvas, int type, List<PointInfo> info) {
        canvas.drawPath(getPath(), getPaint());
        if (info.size() > 1) {
            float endX = info.get(1).mPointX;
            float endY = info.get(1).mPointY;
            float startX = info.get(0).mPointX;
            float startY = info.get(0).mPointY;

            getPaint().setPathEffect(new DashPathEffect(new float[]{5, 20}, 1));
            mDrawPolygon.setDash(endX - startX,
                    endY - startY, startX, startY);

            switch (type){
                case Constants.CONE:
                    mDrawPolygon.drawConeDash(getPath());
                    canvas.drawPath(getPath(), getPaint());
                    break;

                case Constants.CUBE:
                    mDrawPolygon.drawCubeDash(getPath());
                    canvas.drawPath(getPath(), getPaint());
                    break;

                case Constants.SPHERE:
                    mDrawPolygon.drawSphereDash(getPath());
                    canvas.drawPath(getPath(), getPaint());
                    break;

                default:
                    break;
            }

            getPaint().setPathEffect(null);

        }

    }
}
