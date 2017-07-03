package com.cvter.nynote.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.presenter.FilePresenterImpl;
import com.cvter.nynote.presenter.IFilePresenter;
import com.cvter.nynote.utils.CommonMethod;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.DrawPolygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cvter on 2017/6/22.\
 * 缩放拖拽操作View
 */

public class MatrixView extends View {

    private Matrix mMatrix;
    private Matrix mSaveMatrix;

    private PointF mPoint;
    private float mDistance;

    private List<PathInfo> mDrawingList;

    private IFilePresenter mFilePresenter;

    private Canvas mCanvas;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private int mMode = NONE;

    private static final float MAX_SCALE = 3;
    private static final float MIN_SCALE = (float) 0.3;

    private Bitmap mBitmap;

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
        mFilePresenter = new FilePresenterImpl(getContext());
        mPoint = new PointF();
        mMatrix = new Matrix();
        mSaveMatrix = new Matrix();
        mBitmap = Bitmap.createBitmap(CommonMethod.getScreenSize(getContext())[0], CommonMethod.getScreenSize(getContext())[1], Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mSaveMatrix.set(mMatrix);
                mPoint.set(event.getX(), event.getY());
                mMode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mDistance = distance(event);
                if (mDistance > 10f) {
                    mSaveMatrix.set(mMatrix);
                    mMode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mMode ==DRAG) {
                    // 一个手指拖动
                    mMatrix.set(mSaveMatrix);
                    mMatrix.postTranslate(event.getX() - mPoint.x, event.getY() - mPoint.y);
                } else if (mMode == ZOOM) {
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
        super.onDraw(canvas);

        if (null != mDrawingList && !mDrawingList.isEmpty()) {
            for (PathInfo drawPath : mDrawingList) {
                Paint paint = drawPath.getPaint();
                Path path = drawPath.getPath();
                mCanvas.drawPath(path, paint);
            }
            invalidate();
        }

        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, mMatrix, null);
        }
    }

    public void setOnDraw(List<PathInfo> newDrawPathList) {
        this.mDrawingList = new ArrayList<>(newDrawPathList);
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

                    float[] mCoefficient = {newList.get(i).getPointList().get(j).mPointX ,
                    newList.get(i).getPointList().get(j).mPointY};
                    mMatrix.mapPoints(mCoefficient);
                    newList.get(i).getPointList().get(j).mPointX = mCoefficient[0];
                    newList.get(i).getPointList().get(j).mPointY = mCoefficient[1];
                    if (j == 0){
                        startX = mCoefficient[0];
                        startY = mCoefficient[1];
                        path.moveTo(startX, startY);
                    }

                    mFilePresenter.handleGraphType(path, startX, startY, mCoefficient[0], mCoefficient[1], newList.get(i).getGraphType());
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
