package com.cvter.nynote.utils;

import com.cvter.nynote.model.PathInfo;

import java.util.List;

/**
 * Created by cvter on 2017/6/9.
 */

public interface ImportListener {

    void onSuccess(List<PathInfo> info);
    void onFail(String toastMessage);

}
