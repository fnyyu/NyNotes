package com.cvter.nynote.Utils;

import android.os.Environment;

/**
 * Created by cvter on 2017/6/5.
 */

public class CommonUtils {

    public static final String PATH = Environment.getExternalStorageDirectory().toString() + "/NyNote";//sd路径

    public static final String PICTURE_PATH = PATH + "/pic";//note图片路径

    public final static int TAKE_PHOTO = 1;
    public final static int GALLEY_PICK = 2;

    public enum Mode{
        DRAW,
        ERASER
    }

    public static final int ODINARY = 0;
    public static final int CIRCLE = 1;
    public static final int LINE = 2;
    public static final int SQUARE = 3;
    public static final int CONE = 4;
    public static final int SPHERE = 5;
    public static final int CUBE = 6;

}
