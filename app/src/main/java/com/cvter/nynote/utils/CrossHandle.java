package com.cvter.nynote.utils;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import com.cvter.nynote.model.PointInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by cvter on 2017/7/3.
 */

public class CrossHandle {

    private int mMinDistance;
    private Region mRegion;

    public CrossHandle(int minDistance) {
        this.mMinDistance = minDistance;
        mRegion = new Region();
    }

    private boolean isCrossOrdinary(float p1x, float p1y, float p2x, float p2y) {

        return (p1x - p2x) < mMinDistance && (p1y - p2y) < mMinDistance;
    }

    private boolean isCrossCircle(float p1x, float p1y, float p2x, float p2y, float r) {
        float distanceX = Math.abs(p1x - p2x);
        float distanceY = Math.abs(p1y - p2y);
        float distanceZ = (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        return distanceZ <= r;
    }

    public boolean isCross(List<PointInfo> drawList, List<PointInfo> pointList, int type, Path path) {

        if (null == drawList || null == pointList) {
            return false;
        }

        int drawListSize = drawList.size();
        int pointListSize = pointList.size();

        float drawX = drawList.get(0).mPointX;
        float drawY = drawList.get(0).mPointY;

        List<PointInfo> list = new LinkedList<>();

        if(type == Constants.DELTA){
            list.clear();
            list.add(new PointInfo(drawX, drawY));
            float radius = (drawList.get(1).mPointY - drawY) * 2 / 3;
            float spaceX = (float) (radius * (Math.sin(Math.PI * 60 / 180)));
            float spaceY = (float) (radius + radius * (Math.cos(Math.PI * 60 / 180)));
            list.add(new PointInfo((drawX + spaceX), (drawY + spaceY)));
            list.add(new PointInfo((drawX - spaceX), (drawY + spaceY)));
        }

        if(type == Constants.PENTAGON || type == Constants.STAR){
            list.clear();
            float radius = drawList.get(1).mPointY - drawY;
            list.add(new PointInfo(drawX, drawY - radius));
            float spaceX = (float) (radius * (Math.sin(Math.PI * 72 / 180)));
            float spaceY = (float) (radius * (Math.cos(Math.PI * 72 / 180)));
            float spaceX2 = (float) (radius * (Math.sin(Math.PI * 36 / 180)));
            float spaceY2 = (float) (radius * (Math.cos(Math.PI * 36 / 180)));
            list.add(new PointInfo((drawX + spaceX), (drawY - spaceY)));
            list.add(new PointInfo((drawX + spaceX2), (drawY + spaceY2)));
            list.add(new PointInfo((drawX - spaceX2), (drawY + spaceY2)));
            list.add(new PointInfo((drawX - spaceX), (drawY - spaceY)));
        }


        for (int j = 0; j < drawListSize; j++) {
            for (int k = 0; k < pointListSize; k++) {

                float pointX = pointList.get(k).mPointX;
                float pointY = pointList.get(k).mPointY;

                if (type == Constants.ORDINARY && isCrossOrdinary(pointX, pointY,
                        drawList.get(j).mPointX, drawList.get(j).mPointY)) {
                    return true;
                }

                if (type == Constants.CIRCLE || type == Constants.SQUARE || type == Constants.CONE || type == Constants.CUBE) {
                    RectF r = new RectF();
                    path.computeBounds(r, true);
                    mRegion.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
                    if (mRegion.contains((int) pointX, (int) pointY)) {
                        return true;
                    }

                }

                if (Constants.SPHERE == type && isCrossCircle(pointX, pointY,
                        drawX, drawY, drawList.get(1).mPointY - drawY)) {
                    return true;
                }

                if (Constants.LINE == type && isCrossLine(drawX, drawY,
                        drawList.get(1).mPointX, drawList.get(1).mPointY,
                        pointList.get(0).mPointX, pointList.get(0).mPointY,
                        pointList.get(pointListSize - 1).mPointX, pointList.get(pointListSize - 1).mPointY)) {
                    return true;
                }

                if ((type == Constants.DELTA || type == Constants.PENTAGON || type == Constants.STAR )
                        && ptInPolygon(new PointInfo(pointX, pointY), list)) {
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

    private boolean ptInPolygon(PointInfo point, List<PointInfo> aPoints) {

        if (aPoints == null || aPoints.isEmpty()) {
            return false;
        }

        int nCross = 0;
        for (int i = 0; i < aPoints.size(); i++) {
            PointInfo p1 = aPoints.get(i);
            PointInfo p2 = aPoints.get((i + 1) % aPoints.size());
            // 求解 y=p.y 与 p1p2 的交点
            if (p1.mPointY == p2.mPointY)
                continue;
            if (point.mPointY < Math.min(p1.mPointY, p2.mPointY))
                continue;
            if (point.mPointY >= Math.max(p1.mPointY, p2.mPointY))
                continue;
            double x = (double) (point.mPointY - p1.mPointY)
                    * (double) (p2.mPointX - p1.mPointX)
                    / (double) (p2.mPointY - p1.mPointY) + p1.mPointX;
            if (x > point.mPointX)
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外 ---
        return (nCross % 2 == 1);
    }

}
