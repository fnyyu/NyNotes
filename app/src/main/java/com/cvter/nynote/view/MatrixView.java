package com.cvter.nynote.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.utils.CommonMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cvter on 2017/6/22.\
 * 缩放拖拽操作View
 */

public class MatrixView extends View {

    private Matrix mMatrix;
    private Matrix mSaveMatrix;
    private Matrix mNowMatrix;
    private Matrix mTempMatrix;
    private RectF mRectF;

    private PointF mPoint;
    private float mDistance;

    private List<PathInfo> mDrawingList;
    private Canvas mCanvas;
    private Bitmap mBitmap;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mMode = NONE;

    private float mWidth;
    private float mHeight;
    private float mDeltaX;
    private float mDeltaY;
    private boolean isFirst;

    public MatrixView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                init();
            }
        });
    }

    private void init() {
        mPoint = new PointF();
        mMatrix = new Matrix();
        mSaveMatrix = new Matrix();
        mTempMatrix = new Matrix();
        mNowMatrix = new Matrix();
        mRectF = new RectF();
        mBitmap = Bitmap.createBitmap(CommonMethod.getScreenSize(getContext())[0], CommonMethod.getScreenSize(getContext())[1], Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mWidth = getMeasuredWidth();
                mHeight = getMeasuredHeight();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mSaveMatrix.set(mMatrix);
                mTempMatrix.set(mNowMatrix);
                mPoint.set(event.getX(), event.getY());
                mMode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mDistance = distance(event);
                if (mDistance > 10f) {
                    mSaveMatrix.set(mMatrix);
                    mTempMatrix.set(mNowMatrix);
                    mMode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mMode == DRAG) {
                    // 一个手指拖动
                    mMatrix.set(mSaveMatrix);
                    mNowMatrix.set(mTempMatrix);
                    mNowMatrix.postTranslate(event.getX() - mPoint.x, event.getY() - mPoint.y);
                    mMatrix.postTranslate(event.getX() - mPoint.x, event.getY() - mPoint.y);
                } else if (mMode == ZOOM) {
                    // 两个手指滑动
                    float newDist = distance(event);
                    if (newDist > 10f) {
                        mMatrix.set(mSaveMatrix);
                        mNowMatrix.set(mTempMatrix);
                        float scale = newDist / mDistance;
                        float[] values = new float[9];
                        mMatrix.getValues(values);
                        mMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
                        mNowMatrix.getValues(values);
                        mNowMatrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                mMode = NONE;
                break;

            default:
                break;
        }
        invalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mDrawingList.isEmpty()) {
            for (PathInfo drawPath : mDrawingList) {
                drawPath.draw(mCanvas, drawPath.getGraphType(), drawPath.getPointList());
            }
        }

        if (mBitmap != null) {
            if (isFirst) {
                mMatrix.postTranslate(-mDeltaX, -mDeltaY);
                isFirst = false;
            }
            canvas.drawBitmap(mBitmap, mMatrix, null);
        }
    }

    public void setOnDraw(List<PathInfo> newDrawPathList) {

        if (newDrawPathList != null) {
            mDrawingList = new ArrayList<>(newDrawPathList);
            for (int i = 0; i < mDrawingList.size(); i++) {
                RectF rectF = new RectF();
                mDrawingList.get(i).getPath().computeBounds(rectF, true);
                mRectF.union(rectF);
            }
            if (mRectF.height() <= mHeight && mRectF.width() <= mWidth &&
                    (mRectF.left < 0 || mRectF.right > mWidth ||mRectF.top < 0 ||mRectF.bottom >mHeight )) {
                translate();

            }
        }
        invalidate();

    }

    private void translate() {

        mDeltaX = mWidth * 0.5f - mRectF.right + mRectF.width() * 0.5f;
        mDeltaY = mHeight * 0.5f - mRectF.bottom + mRectF.height() * 0.5f;
        for (int i = 0; i < mDrawingList.size(); i++) {
            mDrawingList.get(i).getPath().offset(mDeltaX, mDeltaY);
        }
        isFirst = true;

    }

    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    //计算更改后的坐标和路径
    public List<PathInfo> getMatrixList() {

        List<PathInfo> newList = null;
        float startX = 0;
        float startY = 0;
        if (mDrawingList != null) {
            newList = new ArrayList<>(mDrawingList);
            for (int i = 0; i < newList.size(); i++) {

                Path path = new Path();
                for (int j = 0; j < newList.get(i).getPointList().size(); j++) {

                    float[] mCoefficient = {newList.get(i).getPointList().get(j).mPointX,
                            newList.get(i).getPointList().get(j).mPointY};
                    mNowMatrix.mapPoints(mCoefficient);
                    newList.get(i).getPointList().get(j).mPointX = mCoefficient[0];
                    newList.get(i).getPointList().get(j).mPointY = mCoefficient[1];
                    if (j == 0) {
                        startX = mCoefficient[0];
                        startY = mCoefficient[1];
                        path.moveTo(startX, startY);
                    }

                    CommonMethod.handleGraphType(path, startX, startY, mCoefficient[0], mCoefficient[1], newList.get(i).getGraphType());
                    startX = mCoefficient[0];
                    startY = mCoefficient[1];
                }
                newList.get(i).setPath(path);

            }
        }
        return newList;

    }

    public void recycle() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

}
