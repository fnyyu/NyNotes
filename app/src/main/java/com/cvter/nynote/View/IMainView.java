package com.cvter.nynote.View;

import com.cvter.nynote.Model.NoteInfo;

import java.util.ArrayList;

/**
 * Created by cvter on 2017/6/2.
 */

public interface IMainView {
    void updateListCompleted();
    void onLoadImagesCompleted(ArrayList<NoteInfo> notes);
}
