package com.cvter.nynote.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.cvter.nynote.activity.DrawActivity;
import com.cvter.nynote.model.PaintInfo;
import com.cvter.nynote.model.PathDrawingInfo;
import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.model.PointInfo;
import com.cvter.nynote.presenter.PathWFCallback;
import com.cvter.nynote.utils.CommonMethod;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.CrossHandle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cvter on 2017/6/6.
 * 绘制SurfaceView
 */

public class PaintView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private DrawActivity mContext;
    private Canvas mCanvas;
    private Path mPath;
    private Path mGraphPath;
    private Path mDotPath;
    private PaintInfo mPaint;

    private float mLastX;
    private float mLastY;
    private float mEndX;
    private float mEndY;
    private int mMinDistance;
    private boolean isCanDraw;
    private boolean isCrossDraw;

    private List<PathInfo> mDrawingList;
    private List<PathInfo> mRemovedList;
    private List<PathInfo> mCrossList;
    private LinkedList<PointInfo> mPointList;
    private List<PointInfo> mCrossPoint;
    private List<RectF> mEraserRectFList;
    private CrossHandle mCrossHandle;

    private boolean mCanEraser;
    private boolean mIsHasBG;
    private Bitmap mBufferBitmap;
    private Bitmap mBackgroundBitmap;

    private Rect mFromRect;
    private RectF mToRectF;

    private PathWFCallback mCallback;

    private int mBeforeColor = Color.BLACK;

    public PaintView(Context context) {
        super(context);
        mContext = (DrawActivity) context;
        init();
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (DrawActivity) context;
        init();
    }

    public PaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = (DrawActivity) context;
        init();
    }

    public void setCallback(PathWFCallback callback) {
        mCallback = callback;
    }

    private int eraserX = 0;
    private int eraserY = 0;
    private int eraserRadius;

    //画板初始化
    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
        mPaint = new PaintInfo();
        mPaint.setStrokeWidth(7);
        mBufferBitmap = Bitmap.createBitmap(getScreenSize()[0], getScreenSize()[1], Bitmap.Config.ARGB_4444);
        mBackgroundBitmap = Bitmap.createBitmap(getScreenSize()[0], getScreenSize()[1], Bitmap.Config.ARGB_4444);
        mBackgroundBitmap.eraseColor(Color.WHITE);
        mCanvas = new Canvas(mBufferBitmap);
        mMinDistance = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mCrossHandle = new CrossHandle(mMinDistance);
        eraserRadius = mMinDistance * 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isCanDraw) {
            return false;
        }

        mEndX = event.getX();
        mEndY = event.getY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mPaint.getMode() == Constants.CUT) {
                    eraserActionDown();
                    break;
                }
                actionDown();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                if (mPaint.getMode() == Constants.CUT) {
                    eraserActionMove();
                    break;
                }
                if (Math.abs(mEndX - mLastX) < mMinDistance / 2.0f && Math.abs(mEndY - mLastY) < mMinDistance / 2.0f) {
                    return true;
                }
                if (mPaint.getMode() == Constants.ERASER && !mCanEraser) {
                    break;
                }
                actionMove();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP:
                if (mPaint.getMode() == Constants.CUT) {
                    eraserActionUp();
                    break;
                }
                actionUp();
                break;

            default:
                break;
        }
        draw(Constants.TOUCH);
        return true;
    }

    private void eraserActionDown(){
        mEraserRectFList = new ArrayList<>();
        mFromRect = new Rect();
        mToRectF = new RectF();
    }

    private void eraserActionMove(){
        eraserX = (int) mEndX;
        eraserY = (int) mEndY;
        mFromRect.set(eraserX - eraserRadius, eraserY- eraserRadius, eraserX + eraserRadius, eraserY + eraserRadius);
        mToRectF.set(eraserX - eraserRadius, eraserY- eraserRadius, eraserX + eraserRadius, eraserY + eraserRadius);
        mCanvas.drawBitmap(mBackgroundBitmap, mFromRect, mToRectF, null);
        if (mEraserRectFList != null){
            mEraserRectFList.add(new RectF(mToRectF));
        }
    }

    private void eraserActionUp(){
        mBufferBitmap.eraseColor(Color.TRANSPARENT);
        mDrawingList = mCrossHandle.isEraserCross(mEraserRectFList, mDrawingList);

        if (null != mDrawingList && !mDrawingList.isEmpty()) {
            for (PathInfo drawPath : mDrawingList) {
                mCanvas.drawPath(drawPath.getPath(), drawPath.getPaint());
            }
        }
        mEraserRectFList.clear();
    }

    private void actionDown() {

        mPointList = new LinkedList<>();
        mLastX = mEndX;
        mLastY = mEndY;

        if (isCrossDraw) {
            mPaint.setGraphType(Constants.ORDINARY);
            mBeforeColor = mPaint.getColor();
            mPaint.setColor(Color.TRANSPARENT);
            mCrossPoint = new LinkedList<>();
            mCrossPoint.add(new PointInfo(mEndX, mEndY));
        } else {

            mPointList.add(new PointInfo(mEndX, mEndY));
        }

        if (mPaint.getGraphType() == Constants.ORDINARY) {
            if (mPath == null) {
                mPath = new Path();
            }
            mPath.moveTo(mEndX, mEndY);
        } else {
            mGraphPath = new Path();
            mDotPath = new Path();
            mGraphPath.moveTo(mEndX, mEndY);
        }
    }

    private void actionMove() {

        if (mPaint.getGraphType() == Constants.ORDINARY){
            if (isCrossDraw) {
                mCrossPoint.add(new PointInfo(mEndX, mEndY));
            } else {
                mPointList.add(new PointInfo(mEndX, mEndY));
            }
            CommonMethod.handleGraphType(mPath, mLastX, mLastY, mEndX, mEndY, mPaint.getGraphType(), Constants.DRAW);
            mLastX = mEndX;
            mLastY = mEndY;
        } else {
            CommonMethod.handleGraphType(mGraphPath, mLastX, mLastY, mEndX, mEndY, mPaint.getGraphType(), Constants.DRAW);
        }

    }

    private void actionUp() {

        if (isCrossDraw) {
            mCrossPoint.add(new PointInfo(mEndX, mEndY));
            setCrossList();
            mContext.setChooseStyle();
            mPaint.setColor(mBeforeColor);
            mPath.reset();
            return;
        } else {
            mPointList.add(new PointInfo(mEndX, mEndY));
        }

        if (mPaint.getMode() == Constants.DRAW || mCanEraser) {
            saveDrawingPath();
        }

        if (mPaint.getGraphType() == Constants.ORDINARY && mPath != null) {
            mCanvas.drawPath(mPath, mPaint);
            mPath.reset();
        }

        if (mPaint.getGraphType() != Constants.ORDINARY && mGraphPath != null) {
            mCanvas.drawPath(mGraphPath, mPaint);
            mPaint.setIsDottedPen();
            CommonMethod.handleGraphType(mDotPath, mLastX, mLastY, mEndX, mEndY, mPaint.getGraphType(), Constants.POLYGON);
            mCanvas.drawPath(mDotPath, mPaint);
            mPaint.setNoDottedPen();
            mDotPath.reset();
            mGraphPath.reset();
        }


        mPointList = null;
    }

    //获取画布进行绘制
    private void draw(int type) {
        Canvas canvas = mHolder.lockCanvas();
        if (canvas != null) {
            // 绘制背景色
            canvas.drawColor(Color.WHITE);
            if (mIsHasBG && mBackgroundBitmap != null) {
                canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
            }
            drawBitmap(canvas, type);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    //绘制于bitmap上
    public void drawBitmap(Canvas canvas, int type) {
        if (mBufferBitmap != null && mPaint != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }

        if (mPaint != null && mPaint.getMode() == Constants.CUT) {
            mFromRect.set(eraserX - eraserRadius, eraserY- eraserRadius, eraserX + eraserRadius, eraserY + eraserRadius);
            mToRectF.set(eraserX - eraserRadius, eraserY- eraserRadius, eraserX + eraserRadius, eraserY + eraserRadius);
            canvas.drawBitmap(mBackgroundBitmap, mFromRect, mToRectF, null);
        }

        if (mPath != null && mPaint != null && !isCrossDraw) {
            canvas.drawPath(mPath, mPaint);
        }

        if (mGraphPath != null && mPaint != null && !isCrossDraw) {
            canvas.drawPath(mGraphPath, mPaint);
            if (type == Constants.TOUCH && mPaint.getMode() != Constants.CUT) {
                mPaint.setIsDottedPen();
                Path path = new Path();
                CommonMethod.handleGraphType(path, mLastX, mLastY, mEndX, mEndY, mPaint.getGraphType(), Constants.POLYGON);
                canvas.drawPath(path, mPaint);
                path.reset();
                mPaint.setNoDottedPen();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        //do nothing
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        draw(Constants.TOUCH);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        //do nothing
    }

    //保存路径
    private void saveDrawingPath() {
        Path cachePath;
        Paint cachePaint;
        if (mDrawingList == null) {
            mDrawingList = new LinkedList<>();
        }
        if (mPaint.getGraphType() == Constants.ORDINARY) {
            cachePath = new Path(mPath);
        } else {
            cachePath = new Path(mGraphPath);
        }
        cachePaint = new Paint(mPaint);
        PathDrawingInfo info = new PathDrawingInfo();
        info.setPath(cachePath);
        info.setPaint(cachePaint);
        info.setPaintType(mPaint.getMode());
        info.setPointList(mPointList);
        info.setGraphType(mPaint.getGraphType());
        info.setPenType(mPaint.getPenType());
        mDrawingList.add(info);
        mCanEraser = true;
        if (mCallback != null) {
            mCallback.pathWFState();
        }
    }

    //支持撤销
    public boolean canWithdraw() {
        return mRemovedList != null && !mRemovedList.isEmpty();
    }

    //支持恢复
    public boolean canForward() {
        return mDrawingList != null && !mDrawingList.isEmpty();
    }

    //恢复
    public void withdraw() {
        int size = mRemovedList == null ? 0 : mRemovedList.size();
        if (size > 0 && mRemovedList != null) {
            PathInfo info = mRemovedList.remove(size - 1);
            mDrawingList.add(info);
            mCanEraser = true;
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (PathInfo pathInfo : mDrawingList) {
                pathInfo.draw(mCanvas, pathInfo.getGraphType(), pathInfo.getPointList());
            }
            if (mCallback != null) {
                mCallback.pathWFState();
            }
        }

        draw(Constants.WFC);
    }

    //撤销
    public void forward() {
        int size = mDrawingList == null ? 0 : mDrawingList.size();
        if (size > 0 && mDrawingList != null) {
            PathInfo info = mDrawingList.remove(size - 1);
            if (mRemovedList == null) {
                mRemovedList = new LinkedList<>();
            }
            if (size == 1) {
                mCanEraser = false;
            }
            mRemovedList.add(info);
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (PathInfo pathInfo : mDrawingList) {
                pathInfo.draw(mCanvas, pathInfo.getGraphType(), pathInfo.getPointList());
            }
            if (mCallback != null) {
                mCallback.pathWFState();
            }
        }

        draw(Constants.WFC);
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

        draw(Constants.WFC);
    }

    //获取屏幕大小
    private int[] getScreenSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
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
    public Bitmap getBitmap() {
        return mBufferBitmap;
    }

    //设置背景图片
    public void setBackgroundBitmap(Bitmap backgroundBitmap) {
        this.mBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, getScreenSize()[0], getScreenSize()[1], true);
        draw(Constants.TOUCH);
    }

    public Bitmap getBackgroundBitmap() {
        return mBackgroundBitmap;
    }

    //获取当前全部路径
    public List<PathInfo> getDrawingList() {
        return mDrawingList;
    }

    // 设置要显示的路径
    public void setDrawingList(List<PathInfo> newDrawPathList) {
        if (newDrawPathList != null) {
            this.mDrawingList = newDrawPathList;
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (PathInfo pathInfo : mDrawingList) {
                pathInfo.draw(mCanvas, pathInfo.getGraphType(), pathInfo.getPointList());
            }
        }
        draw(Constants.WFC);
    }

    // 设置缩放后显示的路径
    public void setCrossDrawingList(List<PathInfo> newDrawPathList) {
        if (newDrawPathList != null && mDrawingList != null){
            for (PathInfo info : newDrawPathList) {
                mDrawingList.add(info);
            }
            mBufferBitmap.eraseColor(Color.TRANSPARENT);
            for (PathInfo pathInfo : mDrawingList) {
                pathInfo.draw(mCanvas, pathInfo.getGraphType(), pathInfo.getPointList());
            }
        }

        draw(Constants.WFC);
    }

    public void setCanDraw(boolean canDraw) {
        this.isCanDraw = canDraw;
    }

    public boolean getCanDraw() {
        return isCanDraw;
    }

    public void setIsCrossDraw(boolean isCrossDraw) {
        this.isCrossDraw = isCrossDraw;
    }

    public List<PathInfo> getCrossList() {
        return mCrossList;
    }

    //获取选中进行缩放的路径
    public void setCrossList() {
        mCrossList = new LinkedList<>();
        List<Integer> pos = new LinkedList<>();
        for (int i = 0; i < mDrawingList.size(); i++) {
            PathInfo pathInfo = mDrawingList.get(i);
            if (mCrossHandle.isCross(mDrawingList.get(i).getPointList(), mCrossPoint, pathInfo.getGraphType(), mDrawingList.get(i).getPath())) {
                mCrossList.add(pathInfo);
                pos.add(i - pos.size());
            }

        }
        if (!pos.isEmpty()) {
            for (int index : pos)
                mDrawingList.remove(index);

        }
        pos.clear();

    }

    //将被选中进行缩放的路径进行删除
    public void clearCross() {
        mBufferBitmap.eraseColor(Color.TRANSPARENT);
        if (null != mDrawingList && !mDrawingList.isEmpty()) {
            for (PathInfo drawPath : mDrawingList) {
                mCanvas.drawPath(drawPath.getPath(), drawPath.getPaint());
            }
            draw(Constants.TOUCH);
        }
    }

    public List<RectF> getEraserRectFList() {
        return mEraserRectFList;
    }
}
