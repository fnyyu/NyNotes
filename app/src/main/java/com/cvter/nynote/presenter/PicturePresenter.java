package com.cvter.nynote.presenter;

import java.io.File;

/**
 * Created by cvter on 2017/6/6.
 */

public interface PicturePresenter {

    File createImgFile(String curPage);
    void getSmallBitmap(String photoPath);

}
