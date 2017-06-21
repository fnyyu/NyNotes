package com.cvter.nynote.presenter;

import java.io.File;

/**
 * Created by cvter on 2017/6/6.
 * 图片处理逻辑接口
 */

public interface PicturePresenter {

    File createImgFile(String curPage);

    void getSmallBitmap(String photoPath, int type, String page);

}
