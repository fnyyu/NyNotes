package com.cvter.nynote.utils;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import com.cvter.nynote.model.PointInfo;

import java.util.List;

/**
 * Created by cvter on 2017/7/3.
 */

public class CrossHandle {

    private int mMinDistance;
    private Region mRegion ;

    public CrossHandle(int minDistance) {
        this.mMinDistance = minDistance;
        mRegion = new Region();
    }

    private boolean isCrossOrdinary(float p1x, float p1y, float p2x, float p2y) {

        return (p1x - p2x) < mMinDistance && (p1y - p2y) < mMinDistance;
    }

    public boolean isCross(List<PointInfo> drawList, List<PointInfo> pointList, int type, Path path) {

        if (null == drawList || null == pointList) {
            return false;
        }

        int drawListSize = drawList.size();
        int pointListSize = pointList.size();
        for (int j = 0; j < drawListSize; j++) {
            for (int k = 0; k < pointListSize; k++) {

                if (type == Constants.ORDINARY && isCrossOrdinary(pointList.get(k).mPointX, pointList.get(k).mPointY,
                        drawList.get(j).mPointX, drawList.get(j).mPointY)) {
                    return true;
                }

                if (type == Constants.CIRCLE || type == Constants.SQUARE || type == Constants.CONE
                        || type == Constants.DELTA || type == Constants.PENTAGON || type == Constants.STAR || type == Constants.CUBE){
                    RectF r=new RectF();
                    path.computeBounds(r, true);
                    mRegion.setPath(path, new Region((int)r.left,(int)r.top,(int)r.right,(int)r.bottom));
                    if(mRegion.contains((int)pointList.get(k).mPointX, (int)pointList.get(k).mPointY)){
                        return true;
                    }

                }

                if (Constants.LINE == type && isCrossLine(drawList.get(0).mPointX, drawList.get(0).mPointY,
                        drawList.get(1).mPointX, drawList.get(1).mPointY,
                        pointList.get(0).mPointX, pointList.get(0).mPointY,
                        pointList.get(pointListSize - 1).mPointX, pointList.get(pointListSize - 1).mPointY)) {
                    return true;
                }

            }
        }
        return false;
    }

    private static boolean isCrossLine(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y, float p4x, float p4y) {
        return (ifCrossProduct(p1x, p1y, p2x, p2y, p3x, p3y) * ifCrossProduct(p1x, p1y, p2x, p2y, p4x, p4y) < 0); // 查看直线（p1, p2）与线段（p3， p4）是否相交
    }

    private static float ifCrossProduct(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
        return (p2x - p1x) * (p3y - p1y) - (p2y - p1y) * (p3x - p1x);
    }

}
