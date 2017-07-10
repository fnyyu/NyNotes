package com.cvter.nynote.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.cvter.nynote.polygon.Circle;
import com.cvter.nynote.polygon.Cone;
import com.cvter.nynote.polygon.Cube;
import com.cvter.nynote.polygon.Delta;
import com.cvter.nynote.polygon.IPolygon;
import com.cvter.nynote.polygon.Line;
import com.cvter.nynote.polygon.Odinary;
import com.cvter.nynote.polygon.Pentagon;
import com.cvter.nynote.polygon.Sphere;
import com.cvter.nynote.polygon.Square;
import com.cvter.nynote.polygon.Star;

import java.io.ByteArrayOutputStream;

/**
 * Created by cvter on 2017/6/26.
 */

public class CommonMethod {

    private static IPolygon mPolygon;

    CommonMethod(){

    }

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

    // 根据Path的类型来处理Path
    public static void handleGraphType(Path path, float startX, float startY, float x, float y, int graphType, int invokeType) {
        switch (graphType) {
            case Constants.ORDINARY:
                mPolygon = new Odinary();
                break;
            case Constants.CIRCLE:
                mPolygon = new Circle();
                break;
            case Constants.LINE:
                mPolygon = new Line();
                break;
            case Constants.SQUARE:
                mPolygon = new Square();
                break;
            case Constants.DELTA:
                mPolygon = new Delta();
                break;
            case Constants.PENTAGON:
                mPolygon = new Pentagon();
                break;
            case Constants.STAR:
                mPolygon = new Star();
                break;
            case Constants.CONE:
                mPolygon = new Cone();
                break;
            case Constants.CUBE:
                mPolygon = new Cube();
                break;
            case Constants.SPHERE:
                mPolygon = new Sphere();
                break;
            default:
                break;
        }

        if (invokeType == Constants.DRAW){
            mPolygon.drawPolygonPath(path, startX, startY, x, y);
        }
        if (invokeType == Constants.POLYGON){
            mPolygon.drawDash(path, x - startX, y - startY, startX, startY);
        }
    }

}
