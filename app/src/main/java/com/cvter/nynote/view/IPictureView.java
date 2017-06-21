package com.cvter.nynote.view;

import android.graphics.Bitmap;

/**
 * Created by cvter on 2017/6/6.
 * 照片View处理接口
 */

public interface IPictureView {

    void onCreateError();

    void setPictureBG(Bitmap bitmap);

    void showProgress();

    void hideProgress();
}
