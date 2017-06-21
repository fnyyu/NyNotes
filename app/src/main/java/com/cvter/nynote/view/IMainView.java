package com.cvter.nynote.view;

import com.cvter.nynote.model.NoteInfo;

import java.util.List;

/**
 * Created by cvter on 2017/6/2.
 * 主页面view处理接口
 */

public interface IMainView {
    void onLoadImagesCompleted(List<NoteInfo> notes);
}
