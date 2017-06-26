package com.cvter.nynote.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;

/**
 * Created by cvter on 2017/6/26.
 */

public class CommonMethod {

    CommonMethod(){}

    //图片压缩
    public static Bitmap getCompressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

        Matrix matrix = new Matrix();
        matrix.setScale(0.9f, 0.9f);

        Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        byteArrayOutputStream.reset();
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        while (byteArrayOutputStream.toByteArray().length > 5 * 1024) {
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
