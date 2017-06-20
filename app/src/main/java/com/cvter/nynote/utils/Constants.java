package com.cvter.nynote.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;

/**
 * Created by cvter on 2017/6/5.
 */

public class Constants {

    public static final String PATH = Environment.getExternalStorageDirectory().toString() + "/NyNote";//sd路径
    public static final String NOTE_PATH = Environment.getExternalStorageDirectory().toString() + "/NyNote/";//sd路径

    public static final String PICTURE_FILE_PATH = PATH + "/pic";//note图片文件夹路径
    public static final String PICTURE_PATH = PATH + "/pic/";//note图片路径

    public static final String TEMP_PATH = PATH + "/temp";//临时文件路径
    public static final String TEMP_XML_PATH = TEMP_PATH + "/xml";//临时文件夹路径
    public static final String TEMP_XML_PATHS = TEMP_PATH + "/xml/";//临时文件夹路径
    public static final String TEMP_IMG_PATH = TEMP_PATH + "/pic";//临时文件夹路径
    public static final String TEMP_BG_PATH = TEMP_PATH + "/bg";//临时文件夹路径

    //背景照片选择
    public static final int TAKE_PHOTO = 1;
    public static final int GALLEY_PICK = 2;

    public static final String NEW_EDIT = "new_edit";
    public static final String READ_NOTE = "read_note";

    //画笔模式
    public enum Mode{
        DRAW,
        ERASER
    }

    public enum ScaleMode{
        NONE,
        DRAG,
        ZOOM
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

    public static final int ORDINARY_PEN = 1;
    public static final int TRANS_PEN = 2;
    public static final int INK_PEN = 3;
    public static final int DISCRETE_PEN = 4;
    public static final int DASH_PEN = 5;



    //图片压缩
    public static Bitmap getCompressBitmap(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

        Matrix matrix = new Matrix();
        matrix.setScale(0.9f, 0.9f);

        Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        byteArrayOutputStream.reset();
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        while (byteArrayOutputStream.toByteArray().length > 5 * 1024){
            matrix.setScale(0.9f, 0.9f);
            resultBitmap = Bitmap.createBitmap(resultBitmap, 0, 0, resultBitmap.getWidth(), resultBitmap.getHeight(), matrix, true);
            byteArrayOutputStream.reset();
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        }
        return resultBitmap;
    }

    //获取屏幕大小
    public static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

}
