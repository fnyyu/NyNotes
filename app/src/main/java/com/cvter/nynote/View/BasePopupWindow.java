package com.cvter.nynote.View;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by cvter on 2017/6/5.
 * PopupWindow基类
 */

public class BasePopupWindow extends PopupWindow {

    private Activity mContext;
    private float mShowAlpha = 0.88f;
    private Drawable mOutsideBackgroundDrawable;
    private int mWidth;
    private int mHeight;

    public BasePopupWindow(Activity context, int width, int height) {
        this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
        initBasePopupWindow();
    }

    @Override
    public void setOutsideTouchable(boolean touchable) {
        super.setOutsideTouchable(touchable);
        if(touchable) {
            if(mOutsideBackgroundDrawable == null) {
                mOutsideBackgroundDrawable = new ColorDrawable(Color.WHITE);
            }
            super.setBackgroundDrawable(mOutsideBackgroundDrawable);
        } else {
            super.setBackgroundDrawable(null);
        }
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        mOutsideBackgroundDrawable = background;
        setOutsideTouchable(isOutsideTouchable());
    }

    //初始化BasePopupWindow的一些信息
    private void initBasePopupWindow() {
        setAnimationStyle(android.R.style.Animation_Dialog);
        if(mHeight == 0){
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            setHeight(mHeight);
        }
        setWidth(mWidth);
        setOutsideTouchable(true);  //默认设置outside点击无响应
        setFocusable(true);
    }

    @Override
    public void setContentView(View contentView) {
        if(contentView != null) {
            contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            super.setContentView(contentView);
            addKeyListener(contentView);
        }
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        getShowAnimator().start();
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        getShowAnimator().start();
    }

    @Override
    public void showAsDropDown(View anchor, int x, int y) {
        super.showAsDropDown(anchor, x, y);
        getShowAnimator().start();
    }

    @Override
    public void showAsDropDown(View anchor, int x, int y, int gravity) {
        super.showAsDropDown(anchor, x, y, gravity);
        getShowAnimator().start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        getDismissAnimator().start();
    }

    //窗口显示，窗口背景透明度渐变动画
    private ValueAnimator getShowAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, mShowAlpha);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(360);
        return animator;
    }

    //窗口隐藏，窗口背景透明度渐变动画
    private ValueAnimator getDismissAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(mShowAlpha, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(320);
        return animator;
    }

    ///为窗体添加outside点击事件
    private void addKeyListener(View contentView) {
        if(contentView != null) {
            contentView.setFocusable(true);
            contentView.setFocusableInTouchMode(true);
            contentView.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View view, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK){
                        dismiss();
                    }

                    return false;
                }
            });
        }
    }

    //控制窗口背景的不透明度
    private void setWindowBackgroundAlpha(float alpha) {
        Window window = ((Activity)getContext()).getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = alpha;
        window.setAttributes(layoutParams);
    }

}
