package com.cvter.nynote.utils;

import android.os.Environment;

/**
 * Created by cvter on 2017/6/5.
 */

public class Constants {

    public static final String PATH = Environment.getExternalStorageDirectory().toString() + "/NyNote";//sd路径

    public static final String PICTURE_FILE_PATH = PATH + "/pic";//note图片文件夹路径
    public static final String PICTURE_PATH = PATH + "/pic/";//note图片路径

    //背景照片选择
    public static final int TAKE_PHOTO = 1;
    public static final int GALLEY_PICK = 2;

    //画笔模式
    public enum Mode{
        DRAW,
        ERASER
    }

    //绘制图形
    public static final int ORDINARY = 0;
    public static final int CIRCLE = 1;
    public static final int LINE = 2;
    public static final int SQUARE = 3;
    public static final int CONE = 4;
    public static final int SPHERE = 5;
    public static final int CUBE = 6;
    public static final int DELTA = 7;
    public static final int PENTAGON = 8;
    public static final int STAR = 9;

}
