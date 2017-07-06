package com.cvter.nynote.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.cvter.nynote.R;
import com.cvter.nynote.activity.DrawActivity;
import com.cvter.nynote.model.PaintInfo;
import com.cvter.nynote.model.PathDrawingInfo;
import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.model.PointInfo;
import com.cvter.nynote.presenter.PathWFCallback;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.CrossHandle;
import com.cvter.nynote.utils.DrawPolygon;

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
    private PaintInfo mPaint;

    private float mLastX;
    private float mLastY;
    private int mMinDistance;
    private boolean isCanDraw;
    private boolean isCrossDraw;

    private List<PathInfo> mDrawingList;
    private List<PathInfo> mRemovedList;
    private List<PathInfo> mCrossList;
    private LinkedList<PointInfo> mPointList;
    private DrawPolygon mDrawPolygon;
    private CrossHandle mCrossHandle;

    private boolean mCanEraser;
    private boolean mIsHasBG;
    private Bitmap mBufferBitmap;
    private Bitmap mBackgroundBitmap;

    private PathWFCallback mCallback;

    private List<PointInfo> mCrossPoint;

    private int mBeforeColor = Color.BLACK;

    private static final String TAG = "PaintView";

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

    private int eraserX = 0 ;
    private int eraserY = 0;
    private int screenW;
    private int screenH;
    private int eraserRadius = 20;

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
        mCanvas = new Canvas(mBufferBitmap);
        mMinDistance = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mDrawPolygon = new DrawPolygon();
        mCrossHandle = new CrossHandle(mMinDistance);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isCanDraw) {
            return false;
        }

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (mPaint.getMode() == Constants.CUT){
                    eraserX = (int) event.getX();
                    eraserY = (int) event.getY();
                    mPaint.setStyle(Paint.Style.FILL);
                    eraserRadius =  mMinDistance;
                    break;
                }
                actionDown(x, y);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                if (mPaint.getMode() == Constants.CUT){
                    eraserX = (int) event.getX();
                    eraserY = (int) event.getY();
                    mBufferBitmap.eraseColor(Color.TRANSPARENT);

                    mDrawingList = mCrossHandle.getEraserList(mDrawingList, new PointInfo(eraserX, eraserY), eraserRadius);

                    if (null != mDrawingList && !mDrawingList.isEmpty()) {
                        for (PathInfo drawPath : mDrawingList) {
                            mCanvas.drawPath(drawPath.getPath(), drawPath.getPaint());
                        }
                    }
                    break;
                }
                if (Math.abs(x - mLastX) < mMinDistance/2.0f && Math.abs(y - mLastY) < mMinDistance/2.0f) {
                    return true;
                }
                if (mPaint.getMode() == Constants.ERASER && !mCanEraser) {
                    break;
                }
                actionMove(x, y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP:
                if (mPaint.getMode() == Constants.CUT){
                    eraserX = (int) event.getX();
                    eraserY = (int) event.getY();
                    eraserRadius = 0;
                    mPaint.setStyle(Paint.Style.STROKE);
                    break;
                }
                actionUp(x, y);
                break;

            default:
                break;
        }
        draw();
        return true;
    }

    //手指按下
    private void actionDown(float x, float y) {

        mPointList = new LinkedList<>();
        mLastX = x;
        mLastY = y;

        if (isCrossDraw) {
            mPaint.setGraphType(Constants.ORDINARY);
            mBeforeColor = mPaint.getColor();
            mPaint.setColor(Color.TRANSPARENT);
            mCrossPoint = new LinkedList<>();
            mCrossPoint.add(new PointInfo(x, y));
        }else{

            mPointList.add(new PointInfo(x, y));
        }

        if (mPaint.getGraphType() == Constants.ORDINARY) {
            if (mPath == null) {
                mPath = new Path();
            }
            mPath.moveTo(x, y);
        } else {
            mGraphPath = new Path();
            mGraphPath.moveTo(x, y);
        }
    }

    //手指滑动
    private void actionMove(float x, float y) {

        switch (mPaint.getGraphType()) {
            case Constants.ORDINARY:


                if(isCrossDraw){
                    mCrossPoint.add(new PointInfo(x, y));
                } else{
                    mPointList.add(new PointInfo(x, y));
                }

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
                mDrawPolygon.drawDelta(mGraphPath, ((y - mLastY) * 2 / 3), mLastX, mLastY);
                break;

            case Constants.PENTAGON:
                mDrawPolygon.drawPentagon(mGraphPath, y - mLastY, mLastX, mLastY);
                break;

            case Constants.STAR:
                mDrawPolygon.drawStar(mGraphPath, y - mLastY, mLastX, mLastY);
                break;

            case Constants.SPHERE:
                mDrawPolygon.drawSphere(mGraphPath, y - mLastY, mLastX, mLastY);
                mDrawPolygon.setDash(x - mLastX, y - mLastY, mLastX, mLastY);
                break;

            case Constants.CONE:
                mDrawPolygon.drawCone(mGraphPath, x - mLastX, y - mLastY, mLastX, mLastY);
                mDrawPolygon.setDash(x - mLastX, y - mLastY, mLastX, mLastY);
                break;

            case Constants.CUBE:
                mDrawPolygon.drawCube(mGraphPath, x - mLastX, mLastX, mLastY);
                mDrawPolygon.setDash(x - mLastX, y - mLastY, mLastX, mLastY);
                break;

            default:
                break;

        }
    }

    //手指抬起
    private void actionUp(float x, float y) {


        if (isCrossDraw) {
            mCrossPoint.add(new PointInfo(x, y));
            setCrossList();
            mContext.setChooseStyle();
            mPaint.setColor(mBeforeColor);
            mPath.reset();
            return;
        }else {
            mPointList.add(new PointInfo(x, y));
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
            mPaint.setIfDottedPen(true);

            switch (mPaint.getGraphType()){
                case Constants.CONE:
                    mDrawPolygon.drawConeDash(mGraphPath);
                    mCanvas.drawPath(mGraphPath, mPaint);
                    break;

                case Constants.CUBE:
                    mDrawPolygon.drawCubeDash(mGraphPath);
                    mCanvas.drawPath(mGraphPath, mPaint);
                    break;

                case Constants.SPHERE:
                    mDrawPolygon.drawSphereDash(mGraphPath);
                    mCanvas.drawPath(mGraphPath, mPaint);
                    break;

                default:
                    break;
            }

            mPaint.setIfDottedPen(false);
            mGraphPath.reset();
        }


        mPointList = null;
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

    private void revise(){
        if(eraserX <= eraserRadius){
            eraserX = eraserRadius;
        }else if(eraserX >= (screenW- eraserRadius)){
            eraserX = screenW- eraserRadius;
        }
        if(eraserY <= eraserRadius){
            eraserY = eraserRadius;
        }else if(eraserY >= (screenH- eraserRadius)){
            eraserY = screenH- eraserRadius;
        }
    }

    //绘制于bitmap上
    public void drawBitmap(Canvas canvas) {
        if (mBufferBitmap != null && mPaint != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }

        if (mPaint != null && mPaint.getMode() == Constants.CUT){
            revise();
            canvas.drawCircle(eraserX, eraserY, eraserRadius, mPaint);
        }

        if (mPath != null && mPaint != null && !isCrossDraw) {
            canvas.drawPath(mPath, mPaint);
        }

        if (mGraphPath != null && mPaint != null&& !isCrossDraw) {
            canvas.drawPath(mGraphPath, mPaint);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.e(TAG, getContext().getString(R.string.no_operation));
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        screenW = getWidth();
        screenH = getHeight();
        draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.e(TAG, getContext().getString(R.string.no_operation));
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

        draw();
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
        draw();
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
        this.mDrawingList = newDrawPathList;
        if (null != mDrawingList && !mDrawingList.isEmpty()) {
            for (PathInfo drawPath : mDrawingList) {
                mCanvas.drawPath(drawPath.getPath(), drawPath.getPaint());
            }
            draw();
        }
    }

    public void setCrossDrawingList(List<PathInfo> newDrawPathList) {
        for(PathInfo info : newDrawPathList){
            mDrawingList.add(info);
        }
        mBufferBitmap.eraseColor(Color.TRANSPARENT);
        if (null != mDrawingList && !mDrawingList.isEmpty()) {
            for (PathInfo drawPath : mDrawingList) {
                drawPath.draw(mCanvas, mPaint.getGraphType(), drawPath.getPointList());
            }
            draw();
        }
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

    public void setCrossList() {
        mCrossList = new LinkedList<>();
        List<Integer> pos = new LinkedList<>();
        for(int i = 0; i<mDrawingList.size(); i++){
            PathInfo pathInfo = mDrawingList.get(i);
            if(mCrossHandle.isCross(mDrawingList.get(i).getPointList(), mCrossPoint, pathInfo.getGraphType(), mDrawingList.get(i).getPath())){
                mCrossList.add(pathInfo);
                pos.add(i - pos.size());
            }

        }
        if(!pos.isEmpty()){
            for(int index : pos)
                mDrawingList.remove(index);

        }
        pos.clear();

    }

    public void clearCross(){
        mBufferBitmap.eraseColor(Color.TRANSPARENT);
        if (null != mDrawingList && !mDrawingList.isEmpty()) {
            for (PathInfo drawPath : mDrawingList) {
                mCanvas.drawPath(drawPath.getPath(), drawPath.getPaint());
            }
            draw();
        }
    }
}
