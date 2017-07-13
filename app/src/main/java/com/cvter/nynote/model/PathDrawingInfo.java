package com.cvter.nynote.model;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PathEffect;

import com.cvter.nynote.utils.CommonMethod;
import com.cvter.nynote.utils.Constants;

import java.util.List;

/**
 * Created by cvter on 2017/6/6.
 * 路径绘制类
 */

public class PathDrawingInfo extends PathInfo {

    private PathEffect mBeforeEffect = null;

    @Override
    public void draw(Canvas canvas, int type, List<PointInfo> info) {
        canvas.drawPath(getPath(), getPaint());

        if (info.size()>1) {
            float endX = info.get(1).mPointX;
            float endY = info.get(1).mPointY;
            float startX = info.get(0).mPointX;
            float startY = info.get(0).mPointY;

            if (getPaint().getPathEffect() != null){
                mBeforeEffect = getPaint().getPathEffect();
            }
            CommonMethod.handleGraphType(mDashPath, startX, startY, endX, endY, type, Constants.POLYGON);
            if (mDeltaX > 0f || mDeltaY > 0f || mDeltaX < 0f || mDeltaY < 0f){
                mDashPath.offset(mDeltaX, mDeltaY);
            }
            getPaint().setPathEffect(mPathEffect);
            canvas.drawPath(mDashPath, getPaint());
            mDashPath.reset();
            getPaint().setPathEffect(mBeforeEffect);
        }
    }

}
