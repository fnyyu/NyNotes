package com.cvter.nynote.View;

import com.cvter.nynote.Model.NoteInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cvter on 2017/6/2.
 */

public interface IMainView {
    void onLoadImagesCompleted(List<NoteInfo> notes);
}
