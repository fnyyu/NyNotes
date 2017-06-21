package com.cvter.nynote.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by cvter on 2017/6/20.
 * 手势ImageView类
 */

public class GestureImageView extends ImageView {

    private Matrix mMatrix;
    private Matrix mSaveMatrix;

    private PointF mPoint;
    private float mDistance;

    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    private Mode mMode = Mode.NONE;

    private static final float MAX_SCALE = 3;
    private static final float MIN_SCALE = (float) 0.3;

    public GestureImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPoint = new PointF();
        mMatrix = new Matrix();
        mSaveMatrix = new Matrix();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                setScaleType(ScaleType.MATRIX);
                mMatrix.set(getImageMatrix());
                mSaveMatrix.set(mMatrix);
                mPoint.set(event.getX(), event.getY());
                mMode = Mode.DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mDistance = distance(event);
                if (mDistance > 10f) {
                    mSaveMatrix.set(mMatrix);
                    mMode = Mode.ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mMode == Mode.DRAG) {
                    // 一个手指拖动
                    mMatrix.set(mSaveMatrix);
                    mMatrix.postTranslate(event.getX() - mPoint.x, event.getY() - mPoint.y);
                } else if (mMode == Mode.ZOOM) {
                    // 两个手指滑动
                    float newDist = distance(event);
                    if (newDist > 10f) {
                        mMatrix.set(mSaveMatrix);
                        float scale = newDist / mDistance;
                        float[] values = new float[9];
                        mMatrix.getValues(values);
                        checkMaxScale(scale, values);
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                mMode = Mode.NONE;
                break;

            default:
                break;
        }

        this.setImageMatrix(mMatrix);
        return true;
    }

    //使图像缩放不越界
    private void checkMaxScale(float scale, float[] values) {
        float newScale = scale;
        if (scale * values[Matrix.MSCALE_X] > MAX_SCALE) {
            newScale = MAX_SCALE / values[Matrix.MSCALE_X];
        }

        if (scale * values[Matrix.MSCALE_X] < MIN_SCALE) {
            newScale = MIN_SCALE / values[Matrix.MSCALE_X];
        }

        mMatrix.postScale(newScale, newScale, getWidth() / 2, getHeight() / 2);
    }

    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public void setNormalScale() {
        this.setImageMatrix(null);
    }

}
