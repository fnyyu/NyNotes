package com.cvter.nynote.Presenter;

import android.graphics.Bitmap;

import com.cvter.nynote.View.SaveListener;

/**
 * Created by cvter on 2017/6/9.
 */

public interface IFilePresenter {

    void saveAsXML();
    void saveAsImg(Bitmap bitmap, String path, SaveListener listener);
    void importXML();

}
