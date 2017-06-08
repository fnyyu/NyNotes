package com.cvter.nynote.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.MainThread;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cvter.nynote.Model.PathDrawingInfo;
import com.cvter.nynote.Model.PathInfo;
import com.cvter.nynote.Presenter.PathWFCallback;
import com.cvter.nynote.Utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by cvter on 2017/6/6.
 */

public class PaintView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private float mLastX;
    private float mLastY;

    private static final int MAX_CACHE_STEP = 20;

    private List<PathInfo> mDrawingList;
    private List<PathInfo> mRemovedList;

    private Xfermode mClearMode;
    private float mDrawSize;
    private float mEraserSize;

    private boolean mCanEraser;
    private Bitmap mBufferBitmap;

    private PathWFCallback mCallback;
    private MyThread myThread;

    private CommonUtils.Mode mMode = CommonUtils.Mode.DRAW;

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
                mLastX = x;
                mLastY = y;
                if (mPath == null) {
                    mPath = new Path();
                }
                mPath.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                //使绘制的曲线更平滑
                mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                if (mBufferBitmap == null) {
                    initBuffer();
                }

                if (mMode == CommonUtils.Mode.ERASER && !mCanEraser) {
                    break;
                }
                mCanvas.drawPath(mPath,mPaint);
                invalidate();
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
                if (mMode == CommonUtils.Mode.DRAW || mCanEraser) {
                    saveDrawingPath();
                }
                mPath.reset();
                break;

            default:
                break;
        }

        return true;
    }

    private void init() {

        mHolder=getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawSize = 20;
        mEraserSize = 50;
        mPaint.setStrokeWidth(mDrawSize);
        mPaint.setColor(0XFF000000);

        mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

        myThread = new MyThread(getHolder());
    }

    private void initBuffer(){
        mBufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBufferBitmap);
    }


    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {

        if (myThread.getState() == Thread.State.TERMINATED) {
            myThread = new MyThread(getHolder());
        }
        myThread.isDrawing = true;
        myThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        myThread.isDrawing = false;
    }

    private void saveDrawingPath(){
        if (mDrawingList == null) {
            mDrawingList = new ArrayList<>(MAX_CACHE_STEP);
        } else if (mDrawingList.size() == MAX_CACHE_STEP) {
            mDrawingList.remove(0);
        }
        Path cachePath = new Path(mPath);
        Paint cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();
        info.path = cachePath;
        info.paint = cachePaint;
        mDrawingList.add(info);
        mCanEraser = true;
        if (mCallback != null) {
            mCallback.pathWFState();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }
    }

    public void setMode(CommonUtils.Mode mode) {
        if (mode != mMode) {
            mMode = mode;
            if (mMode == CommonUtils.Mode.DRAW) {
                mPaint.setXfermode(null);
            } else {
                mPaint.setXfermode(mClearMode);
                mPaint.setStrokeWidth(mEraserSize);
            }
        }
    }

    public void setEraserSize(float size) {
        mEraserSize = size;
    }

    public void setPenRawSize(float size) {
        mDrawSize = size;
        mPaint.setStrokeWidth(mDrawSize);
    }

    public void setPenColor(int color) {
        mPaint.setColor(color);
    }

    public void setPenAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    //支持反撤销
    public boolean canWithdraw() {
        return mRemovedList != null && mRemovedList.size() > 0;
    }

    //支持撤销
    public boolean canForward(){
        return mDrawingList != null && mDrawingList.size() > 0;
    }

    //反撤销
    public void withdraw() {
        int size = mRemovedList == null ? 0 : mRemovedList.size();
        if (size > 0) {
            PathInfo info = mRemovedList.remove(size - 1);
            mDrawingList.add(info);
            mCanEraser = true;
            if (mDrawingList != null) {
                mBufferBitmap.eraseColor(Color.TRANSPARENT);
                for (PathInfo PathInfo : mDrawingList) {
                    PathInfo.draw(mCanvas);
                }
                invalidate();
            }
            if (mCallback != null) {
                mCallback.pathWFState();
            }
        }
    }


    //撤销
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
                for (PathInfo PathInfo : mDrawingList) {
                    PathInfo.draw(mCanvas);
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

    private class MyThread extends Thread {
        private SurfaceHolder holder;
        private boolean isDrawing = false;
        //private int red = 0, green = 0, blue = 0;
        //private int colorValue = 0;
        //private float hsbValue[];

        MyThread(SurfaceHolder holder) {
            this.holder = holder;
            //hsbValue = new float[]{0, 1, 1};
        }

        @Override
        public void run() {
            while (isDrawing) {
                Canvas canvas = holder.lockCanvas();
                // 背景色渐变
                //hsbValue[0] = hsbValue[0] + 1 <= 360 ? hsbValue[0] + 1 : 0;
                if (canvas != null) {
                    // 绘制背景色
                    //canvas.drawColor(Color.HSVToColor(hsbValue));
                    canvas.drawColor(Color.WHITE);
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
