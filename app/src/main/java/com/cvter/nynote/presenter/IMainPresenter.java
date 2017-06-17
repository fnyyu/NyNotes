package com.cvter.nynote.presenter;

import java.io.File;

/**
 * Created by cvter on 2017/6/2.
 */

public interface IMainPresenter {

    boolean deleteNote(File file);
    void getNoteImage(File file);

}
