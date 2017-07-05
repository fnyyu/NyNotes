package com.cvter.nynote.utils;

import android.os.Environment;

/**
 * Created by cvter on 2017/6/5.
 * 公共常量类
 */

public class Constants {

    Constants(){}

    public static final String PATH = Environment.getExternalStorageDirectory().toString() + "/NyNote";//sd路径
    public static final String NOTE_PATH = Environment.getExternalStorageDirectory().toString() + "/NyNote/";//sd路径

    public static final String PICTURE_FILE_PATH = PATH + "/pic";//note图片文件夹路径
    public static final String PICTURE_PATH = PATH + "/pic/";//note图片路径

    public static final String TEMP_PATH = PATH + "/temp";//临时文件路径
    public static final String TEMP_XML_PATH = TEMP_PATH + "/xml";//临时文件夹路径
    public static final String TEMP_XML_PATHS = TEMP_PATH + "/xml/";//临时文件夹路径
    public static final String TEMP_IMG_PATH = TEMP_PATH + "/pic";//临时文件夹路径
    public static final String TEMP_IMG_PATHS = TEMP_PATH + "/pic/";//临时文件夹路径
    public static final String TEMP_BG_PATH = TEMP_PATH + "/bg";//临时文件夹路径
    public static final String TEMP_BG_PATHS = TEMP_PATH + "/bg/";//临时文件夹路径

    //背景照片选择
    public static final int TAKE_PHOTO = 1;
    public static final int GALLEY_PICK = 2;

    public static final String NEW_EDIT = "new_edit";
    public static final String READ_NOTE = "read_note";

    //画笔模式
    public static final int DRAW = 0;
    public static final int ERASER = 1;
    public static final int CUT = 2;

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

    public static final int ORDINARY_PEN = 1;
    public static final int TRANS_PEN = 2;
    public static final int INK_PEN = 3;
    public static final int DISCRETE_PEN = 4;
    public static final int DASH_PEN = 5;

}
