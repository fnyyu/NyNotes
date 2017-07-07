package com.cvter.nynote.utils;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import com.cvter.nynote.model.PathDrawingInfo;
import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.model.PointInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cvter on 2017/7/3.
 */

public class CrossHandle {

    private int mMinDistance;
    private Region mRegion;
    private HashMap<Integer, List<Integer>> mPointPosition;

    public CrossHandle(int minDistance) {
        this.mMinDistance = minDistance;
        mRegion = new Region();
        mPointPosition = new LinkedHashMap<>();
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

        if (drawList == null || drawList.isEmpty() || null == pointList || pointList.isEmpty()) {
            return false;
        }

        int drawListSize = drawList.size();
        int pointListSize = pointList.size();

        float drawX = drawList.get(0).mPointX;
        float drawY = drawList.get(0).mPointY;

        List<PointInfo> list = new LinkedList<>();

        if (type == Constants.DELTA) {
            list.clear();
            list.add(new PointInfo(drawX, drawY));
            float radius = (drawList.get(1).mPointY - drawY) * 2 / 3;
            float spaceX = (float) (radius * (Math.sin(Math.PI * 60 / 180)));
            float spaceY = (float) (radius + radius * (Math.cos(Math.PI * 60 / 180)));
            list.add(new PointInfo((drawX + spaceX), (drawY + spaceY)));
            list.add(new PointInfo((drawX - spaceX), (drawY + spaceY)));
        }

        if (type == Constants.PENTAGON || type == Constants.STAR) {
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

                if ((type == Constants.CIRCLE || type == Constants.SQUARE || type == Constants.CONE
                        || type == Constants.CUBE) && isCrossPolygon(path, pointX, pointY)) {
                    return true;

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

                if ((type == Constants.DELTA || type == Constants.PENTAGON || type == Constants.STAR)
                        && isCrossPolygon(new PointInfo(pointX, pointY), list)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isCrossLine(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y, float p4x, float p4y) {
        return (isCrossProduct(p1x, p1y, p2x, p2y, p3x, p3y) * isCrossProduct(p1x, p1y, p2x, p2y, p4x, p4y) < 0); // 查看直线（p1, p2）与线段（p3， p4）是否相交
    }

    private boolean isCrossPolygon(Path path, float pointX, float pointY) {
        RectF r = new RectF();
        path.computeBounds(r, true);
        mRegion.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        return mRegion.contains((int) pointX, (int) pointY);
    }

    private static float isCrossProduct(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
        return (p2x - p1x) * (p3y - p1y) - (p2y - p1y) * (p3x - p1x);
    }

    private boolean isCrossPolygon(PointInfo point, List<PointInfo> aPoints) {

        if (aPoints == null || aPoints.isEmpty()) {
            return false;
        }

        int nCross = 0;
        for (int i = 0; i < aPoints.size(); i++) {
            PointInfo p1 = aPoints.get(i);
            PointInfo p2 = aPoints.get((i + 1) % aPoints.size());
            // 求解 y=p.y 与 p1p2 的交点
            double exp = 10E-10;
            if (Math.abs(p1.mPointY - p2.mPointY) > -1 * exp && Math.abs(p1.mPointY - p2.mPointY) < exp)
                continue;
            if (point.mPointY < Math.min(p1.mPointY, p2.mPointY))
                continue;
            if (point.mPointY >= Math.max(p1.mPointY, p2.mPointY))
                continue;
            double x = (double) (point.mPointY - p1.mPointY)
                    * (double) (p2.mPointX - p1.mPointX)
                    / (double) (p2.mPointY - p1.mPointY) + p1.mPointX;
            if (x > point.mPointX)
                nCross++;
        }
        // 单边交点为偶数，点在多边形之外
        return (nCross % 2 == 1);
    }

    private boolean eraserCross(int listIndex, List<PointInfo> drawList, PointInfo info, float radius) {

        List<Integer> pointList = new ArrayList<>();

        if (null == drawList) {
            return false;
        }
        boolean isCross = false;

        int drawListSize = drawList.size();
        List<Integer> flag = new LinkedList<>();
        for (int i = 0; i < drawListSize; i++) {
            if (isCrossCircle(drawList.get(i).mPointX, drawList.get(i).mPointY, info.mPointX, info.mPointY, radius)) {
                flag.add(i - flag.size());
            }
        }

        if (!flag.isEmpty()) {
            for (int index : flag) {
                drawList.remove(index);
                pointList.add(index - pointList.size());
            }
            isCross = true;
            pointList.add(drawList.size() - pointList.size());
        }

        mPointPosition.put(listIndex, pointList);
        return isCross;

    }

    public List<PathInfo> getEraserList(List<PathInfo> infoList, PointInfo pointInfo, float radius) {

        List<Integer> index = new LinkedList<>();

        if (infoList == null) {
            return new LinkedList<>();
        }

        for (int j = 0; j < infoList.size(); j++) {
            if (eraserCross(j, infoList.get(j).getPointList(), pointInfo, radius)) {
                index.add(j);
            }
        }

        for (int i : index) {
            List<Integer> pointIndex = mPointPosition.get(i);

            if (!pointIndex.isEmpty() && infoList.size() > i) {

                for (int j = 0; j < pointIndex.size(); j++) {
                    PathDrawingInfo info = new PathDrawingInfo();
                    List<PointInfo> pointList = new LinkedList<>();
                    float startX = 0f;
                    float startY = 0f;
                    Path path = new Path();
                    int k;
                    if (j == 0) {
                        k = 0;
                    } else {
                        k = pointIndex.get(j - 1) + 1;
                    }
                    boolean isFirst = true;
                    for (; k < pointIndex.get(j); k++) {
                        PointInfo point = new PointInfo();
                        if (infoList.get(i).getPointList().size() <= k) {
                            break;
                        }
                        point.mPointX = infoList.get(i).getPointList().get(k).mPointX;
                        point.mPointY = infoList.get(i).getPointList().get(k).mPointY;
                        if (isFirst) {
                            startX = point.mPointX;
                            startY = point.mPointY;
                            path.moveTo(startX, startY);
                            isFirst = false;
                        }
                        pointList.add(point);
                        CommonMethod.handleGraphType(path, startX, startY, point.mPointX, point.mPointY, Constants.ORDINARY);
                        startX = point.mPointX;
                        startY = point.mPointY;
                    }
                    info.setPaint(infoList.get(i).getPaint());
                    info.setGraphType(Constants.ORDINARY);
                    info.setPenType(infoList.get(i).getPenType());
                    info.setPath(path);
                    info.setPaintType(Constants.DRAW);
                    info.setPointList(pointList);
                    infoList.add(info);
                }

                for (int in : index) {
                    infoList.remove(in - index.indexOf(in));
                }
            }

        }


        return infoList;
    }

}
