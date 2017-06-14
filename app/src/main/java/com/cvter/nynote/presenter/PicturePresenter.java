package com.cvter.nynote.presenter;

import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Created by cvter on 2017/6/6.
 */

public interface PicturePresenter {

    File createImgFile();
    void getSmallBitmap(String photoPath);
    int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight);

}
