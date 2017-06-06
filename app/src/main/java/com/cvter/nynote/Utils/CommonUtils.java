package com.cvter.nynote.Utils;

import android.os.Environment;

/**
 * Created by cvter on 2017/6/5.
 */

public class CommonUtils {

    public static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/NyNote";//sd路径

    public static final String PICPATH = PATH + "/pic";//note图片路径

}
