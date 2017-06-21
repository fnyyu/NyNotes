package com.cvter.nynote.presenter;

import java.io.File;

/**
 * Created by cvter on 2017/6/2.
 * 主页面逻辑接口
 */

public interface IMainPresenter {

    boolean deleteNote(File file);

    void getNoteImage(File file);

}
