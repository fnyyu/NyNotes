package com.cvter.nynote.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.cvter.nynote.model.PaintInfo;
import com.cvter.nynote.model.PathDrawingInfo;
import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.presenter.PathWFCallback;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.DrawPolygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cvter on 2017/6/6.
 */

public class PaintView extends SurfaceView implements SurfaceHolder.Callback, ScaleGestureDetector.OnScaleGestureListener{

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Path mPath;
    private Path mGraphPath;
    private PaintInfo mPaint;
    private float mLastX;
    private float mLastY;

    private static final int MAX_CACHE_STEP = 20;

    private List<PathInfo> mDrawingList;
    private List<PathInfo> mRemovedList;

    private boolean mCanEraser;
    private boolean mIsHasBG;
    private Bitmap mBufferBitmap;
    private Bitmap mBackgroundBitmap;

    private PathWFCallback mCallback;

    DrawPolygon mDrawPolygon;
    private static final String TAG = "PaintView";

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
                break;

            case MotionEvent.ACTION_UP:
                actionUp();
                break;

            default:
                break;
        }
        draw();
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
                mDrawPolygon.drawPentagon(mGraphPath, y-mLastY, mLastX, mLastY);
                break;

            case Constants.STAR:
                mDrawPolygon.drawStar(mGraphPath, y-mLastY, mLastX, mLastY);
                break;

            case Constants.SPHERE:
                mDrawPolygon.drawSphere(mGraphPath);
                break;

            case Constants.CONE:
                mDrawPolygon.drawCone(mGraphPath);
                break;

            case Constants.CUBE:
                mDrawPolygon.drawCube(mGraphPath);
                break;

            default:
                break;

        }
    }

    //手指抬起
    private void actionUp(){
        if (mPaint.getMode() == Constants.Mode.DRAW || mCanEraser) {
            saveDrawingPath();
        }

        if ( mPaint.getGraphType() == Constants.ORDINARY && mPath != null){
            mCanvas.drawPath(mPath,mPaint);
            mPath.reset();
        }

        if (mPaint.getGraphType() != Constants.ORDINARY && mGraphPath != null){
            mCanvas.drawPath(mGraphPath, mPaint);
            mGraphPath.reset();
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
        mBufferBitmap = Bitmap.createBitmap(getScreenSize()[0], getScreenSize()[1], Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBufferBitmap);
        mDrawPolygon = new DrawPolygon();
    }

    //获取画布进行绘制
    private void draw() {
        Canvas canvas = mHolder.lockCanvas();
        if (canvas != null) {
            // 绘制背景色
            canvas.drawColor(Color.WHITE);
            if (mIsHasBG && mBackgroundBitmap != null) {
                canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
            }
            drawBitmap(canvas);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    //绘制于bitmap上
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
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }

    //获取屏幕大小
    private int[] getScreenSize( ) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
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
        info.setPath(cachePath);
        info.setPaint(cachePaint);
        mDrawingList.add(info);
        mCanEraser = true;
        if (mCallback != null) {
            mCallback.pathWFState();
        }
    }

    public PaintInfo getPaint() {
        return mPaint;
    }

    //是否有背景图片
    public void setIsHasBG(boolean mIsHasBG) {
        this.mIsHasBG = mIsHasBG;
    }

    //获取是否存在背景图片
    public boolean getIsHasBG() {
        return mIsHasBG;
    }

    //返回bitmap
    public Bitmap getBitmap(){
        return mBufferBitmap;
    }

    //支持反撤销
    public boolean canWithdraw() {
        return mRemovedList != null && !mRemovedList.isEmpty();
    }

    //支持撤销
    public boolean canForward(){
        return mDrawingList != null && !mDrawingList.isEmpty();
    }

    //反撤销
    public void withdraw() {
        int size = mRemovedList == null ? 0 : mRemovedList.size();
        if (size > 0 && mRemovedList != null) {
            PathInfo info = mRemovedList.remove(size - 1);
            mDrawingList.add(info);
            mCanEraser = true;
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (PathInfo pathInfo : mDrawingList) {
                pathInfo.draw(mCanvas, mPaint.getGraphType());
            }
            if (mCallback != null) {
                mCallback.pathWFState();
            }
        }

        draw();
    }

    //撤销
    public void forward() {
        int size = mDrawingList == null ? 0 : mDrawingList.size();
        if (size > 0 && mDrawingList != null) {
            PathInfo info = mDrawingList.remove(size - 1);
            if (mRemovedList == null) {
                mRemovedList = new ArrayList<>(MAX_CACHE_STEP);
            }
            if (size == 1) {
                mCanEraser = false;
            }
            mRemovedList.add(info);
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (PathInfo pathInfo : mDrawingList) {
                pathInfo.draw(mCanvas, mPaint.getGraphType());
            }
            if (mCallback != null) {
                mCallback.pathWFState();
            }
        }

        draw();
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
            if (mCallback != null) {
                mCallback.pathWFState();
            }
        }

        draw();
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

    //设置背景图片
    public void setBackgroundBitmap(Bitmap backgroundBitmap) {
        this.mBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, getScreenSize()[0], getScreenSize()[1], true);
    }

    public Bitmap getBackgroundBitmap() {
        return mBackgroundBitmap;
    }
}
