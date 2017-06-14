package com.cvter.nynote.presenter;

import android.graphics.Bitmap;

import com.cvter.nynote.view.SaveListener;

/**
 * Created by cvter on 2017/6/9.
 */

public interface IFilePresenter {

    void saveAsXML();
    void saveAsImg(Bitmap bitmap, String path, SaveListener listener);
    void importXML();

}
