package com.cvter.nynote.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cvter.nynote.Model.PaintInfo;
import com.cvter.nynote.Model.PathDrawingInfo;
import com.cvter.nynote.Model.PathInfo;
import com.cvter.nynote.Presenter.PathWFCallback;
import com.cvter.nynote.Utils.Constants;
import com.cvter.nynote.Utils.DrawPolygon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by cvter on 2017/6/6.
 */

public class PaintView extends SurfaceView implements SurfaceHolder.Callback, ScaleGestureDetector.OnScaleGestureListener{

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Path mPath;
    private Path mGraphPath;
    public PaintInfo mPaint;
    private float mLastX;
    private float mLastY;

    private static final int MAX_CACHE_STEP = 20;

    private List<PathInfo> mDrawingList;
    private List<PathInfo> mRemovedList;

    private boolean mCanEraser;
    private Bitmap mBufferBitmap;

    private PathWFCallback mCallback;
    private MyThread myThread;

    DrawPolygon mDrawPolygon;

    public PaintView(Context context) {
        super(context);
        init();
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setCallback(PathWFCallback callback){
        mCallback = callback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                actionDown(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mPaint.getMode() == Constants.Mode.ERASER && !mCanEraser) {
                    break;
                }
                actionMove(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                actionUp();
                break;

            default:
                break;
        }

        return true;
    }

    //手指按下
    private void actionDown(float x, float y){
        mLastX = x;
        mLastY = y;
        if(mPaint.getGraphType() == Constants.ORDINARY){
            if (mPath == null) {
                mPath = new Path();
            }
            mPath.moveTo(x, y);
        }else{
            mGraphPath = new Path();
            mGraphPath.moveTo(x, y);
        }
    }

    //手指滑动
    private void actionMove(float x, float y){
        switch (mPaint.getGraphType()){
            case Constants.ORDINARY:
                mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                mLastX = x;
                mLastY = y;
                break;

            case Constants.CIRCLE:
                mDrawPolygon.drawCircle(mGraphPath, mLastX, mLastY, x, y);
                break;

            case Constants.LINE:
                mDrawPolygon.drawLine(mGraphPath, mLastX, mLastY, x, y);
                break;

            case Constants.SQUARE:
                mDrawPolygon.drawSquare(mGraphPath, mLastX, mLastY, x, y);
                break;

            case Constants.DELTA:
                mDrawPolygon.drawDelta(mGraphPath, y-mLastY, mLastX, mLastY);
                break;

            case Constants.PENTAGON:
                mDrawPolygon.drawPentagon(mGraphPath, y>mLastY?y-mLastY:mLastY-y, mLastX, mLastY);
                break;

            case Constants.STAR:
                mDrawPolygon.drawStar(mGraphPath, mLastX, mLastY, x, y);
                break;

            case Constants.SPHERE:
                mDrawPolygon.drawSphere(mGraphPath, mLastX, mLastY, x, y);
                break;

            case Constants.CONE:
                mDrawPolygon.drawCone(mGraphPath, mLastX, mLastY, x, y);
                break;

            case Constants.CUBE:
                mDrawPolygon.drawCube(mGraphPath, mLastX, mLastY, x, y);
                break;

        }
    }

    //手指抬起
    private void actionUp(){
        if (mPaint.getMode() == Constants.Mode.DRAW || mCanEraser) {
            saveDrawingPath();
        }
        if ( mPaint.getGraphType() == Constants.ORDINARY){
            mCanvas.drawPath(mPath,mPaint);
        }
        if (mPaint.getGraphType() != Constants.ORDINARY){
            mCanvas.drawPath(mGraphPath, mPaint);
            mGraphPath.reset();
        }
        if(mPath != null){
            mPath.reset();
        }
    }

    //画笔初始化
    private void init() {
        mHolder=getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
        mPaint = new PaintInfo();
        mPaint.setStrokeWidth(20);
        myThread = new MyThread(mHolder);
        mDrawPolygon = new DrawPolygon();
    }

    //画布初始化
    private void initBuffer(){
        mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBufferBitmap);
        mCanvas.drawColor(Color.WHITE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBufferBitmap != null && mPaint != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }
        if (mPath != null && mPaint!= null) {
            canvas.drawPath(mPath, mPaint);
        }
        if (mGraphPath != null && mPaint != null) {
            canvas.drawPath(mGraphPath, mPaint);
        }
    }

    /*
    //绘制
    public void drawBitmap(Canvas canvas){
        if (mBufferBitmap != null && mPaint != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }
        if (mPath != null && mPaint!= null) {
            canvas.drawPath(mPath, mPaint);
        }
        if (mGraphPath != null && mPaint != null) {
            canvas.drawPath(mGraphPath, mPaint);
        }
    }*/

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {

        if (myThread.getState() == Thread.State.TERMINATED) {
            myThread = new MyThread(mHolder);
        }
        myThread.isDrawing = true;
        myThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        myThread.isDrawing = false;
    }

    //保存路径
    private void saveDrawingPath(){
        Path cachePath;
        Paint cachePaint;
        if (mDrawingList == null) {
            mDrawingList = new ArrayList<>(MAX_CACHE_STEP);
        } else if (mDrawingList.size() == MAX_CACHE_STEP) {
            mDrawingList.remove(0);
        }
        if(mPaint.getGraphType() == Constants.ORDINARY){
            cachePath = new Path(mPath);
        }else{
            cachePath = new Path(mGraphPath);
        }
        cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();
        info.path = cachePath;
        info.paint = cachePaint;
        mDrawingList.add(info);
        mCanEraser = true;
        if (mCallback != null) {
            mCallback.pathWFState();
        }
    }

    //支持反撤销
    public boolean canWithdraw() {
        return mRemovedList != null && mRemovedList.size() > 0;
    }

    //支持撤销
    public boolean canForward(){
        return mDrawingList != null && mDrawingList.size() > 0;
    }

    //撤销
    public void withdraw() {
        int size = mRemovedList == null ? 0 : mRemovedList.size();
        if (size > 0) {
            PathInfo info = mRemovedList.remove(size - 1);
            mDrawingList.add(info);
            mCanEraser = true;
            if (mDrawingList != null) {
                mBufferBitmap.eraseColor(Color.TRANSPARENT);
                for (PathInfo pathInfo : mDrawingList) {
                    pathInfo.draw(mCanvas, mPaint.getGraphType());
                }
                invalidate();
            }
            if (mCallback != null) {
                mCallback.pathWFState();
            }
        }
    }

    //反撤销
    public void forward() {
        int size = mDrawingList == null ? 0 : mDrawingList.size();
        if (size > 0) {
            PathInfo info = mDrawingList.remove(size - 1);
            if (mRemovedList == null) {
                mRemovedList = new ArrayList<>(MAX_CACHE_STEP);
            }
            if (size == 1) {
                mCanEraser = false;
            }
            mRemovedList.add(info);
            if (mDrawingList != null) {
                mBufferBitmap.eraseColor(Color.TRANSPARENT);
                for (PathInfo pathInfo : mDrawingList) {
                    pathInfo.draw(mCanvas, mPaint.getGraphType());
                }
                invalidate();
            }
            if (mCallback != null) {
                mCallback.pathWFState();
            }
        }
    }

    //清除
    public void clear() {
        if (mBufferBitmap != null) {
            if (mDrawingList != null) {
                mDrawingList.clear();
            }
            if (mRemovedList != null) {
                mRemovedList.clear();
            }
            mCanEraser = false;
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            invalidate();
            if (mCallback != null) {
                mCallback.pathWFState();
            }
        }
    }

    //获取bitmap
    public Bitmap getBitmap(){
        return mBufferBitmap;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {}

    //线程
    private class MyThread extends Thread {
        private SurfaceHolder holder;
        private boolean isDrawing = false;

        MyThread(SurfaceHolder holder) {
            this.holder = holder;
        }

        @Override
        public void run() {
            while (isDrawing) {
                Canvas canvas = holder.lockCanvas();
                //mCanvas = mHolder.lockCanvas();
                if (canvas != null) {
                    // 绘制背景色
                    canvas.drawColor(Color.WHITE);
                    if(mBufferBitmap == null){
                        initBuffer();
                    }
                    canvas.drawBitmap(mBufferBitmap, 0, 0, null);
                    holder.unlockCanvasAndPost(canvas);
                }
                try {
                    // 休眠20ms
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
