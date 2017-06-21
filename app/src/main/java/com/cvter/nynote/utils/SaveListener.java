package com.cvter.nynote.utils;

/**
 * Created by cvter on 2017/6/9.
 * 保存监听类
 */

public interface SaveListener {

    void onSuccess();

    void onFail(String toastMessage);

}
