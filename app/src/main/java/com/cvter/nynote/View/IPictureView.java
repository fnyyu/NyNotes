package com.cvter.nynote.View;

import android.graphics.Bitmap;

/**
 * Created by cvter on 2017/6/6.
 */

public interface IPictureView {

    void onCreateError();
    void setPictureBG(Bitmap bitmap);
    void showProgress();
    void hideProgress();
}
