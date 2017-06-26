package com.cvter.nynote.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by cvter on 2017/6/5.
 * PopupWindow基类
 */

public class BasePopupWindow extends PopupWindow {

    private Drawable mOutsideBackgroundDrawable;
    private int mWidth;
    private int mHeight;

    public BasePopupWindow(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        initBasePopupWindow();
    }

    @Override
    public void setOutsideTouchable(boolean touchable) {
        super.setOutsideTouchable(touchable);
        if (touchable) {
            if (mOutsideBackgroundDrawable == null) {
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
        if (mHeight == 0) {
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            setHeight(mHeight);
        }

        if (mWidth == 0) {
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            setWidth(mWidth);
        }

        setOutsideTouchable(true);  //默认设置outside点击无响应
        setFocusable(true);
    }

    @Override
    public void setContentView(View contentView) {
        if (contentView != null) {
            contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            super.setContentView(contentView);
            addKeyListener(contentView);
        }
    }

    ///为窗体添加outside点击事件
    private void addKeyListener(View contentView) {
        if (contentView != null) {
            contentView.setFocusable(true);
            contentView.setFocusableInTouchMode(true);
            contentView.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View view, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dismiss();
                    }

                    return false;
                }
            });
        }
    }

}
