package com.cvter.nynote.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
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

    // 根据Path的类型来处理Path
    public static void handleGraphType(Path path, float startX, float startY, float x, float y, int type) {
        switch (type) {
            case Constants.ORDINARY:
                float endX = (x + startX) / 2;
                float endY = (y + startY) / 2;
                path.quadTo(startX, startY, endX, endY);
                break;
            case Constants.CIRCLE:
                RectF rectF = new RectF(startX, startY, x, y);
                path.addOval(rectF, Path.Direction.CW);
                break;
            case Constants.LINE:
                path.moveTo(startX, startY);
                path.lineTo(x, y);
                break;
            case Constants.SQUARE:
                path.addRect(Math.min(startX, x), Math.min(startY, y), Math.max(x, startX), Math.max(y, startY), Path.Direction.CW);
                break;
            case Constants.DELTA:
                handleDelta(path, startX, startY, y);
                break;
            case Constants.PENTAGON:
                handlePentagon(path, startX, startY, y);
                break;
            case Constants.STAR:
                handleStar(path, startX, startY, y);
                break;

            case Constants.CONE:
                handleCone(path, startX, startY, x, y);
                break;

            case Constants.CUBE:
                handleCube(path, startX, startY, x);
                break;

            default:
                break;
        }
    }

    //绘制三角形
    private static void handleDelta(Path path, float startX, float startY, float y) {
        float radiusD = (y - startY) * 2 / 3;
        path.moveTo(startX, startY);
        float spaceXD = (float) (radiusD * (Math.sin(Math.PI * 60 / 180)));
        float spaceYD = (float) (radiusD + radiusD * (Math.cos(Math.PI * 60 / 180)));
        path.lineTo((startX + spaceXD), (startY + spaceYD));
        path.moveTo((startX + spaceXD), (startY + spaceYD));
        path.lineTo((startX - spaceXD), (startY + spaceYD));
        path.moveTo((startX - spaceXD), (startY + spaceYD));
        path.lineTo(startX, startY);
    }

    //绘制正五边形
    private static void handlePentagon(Path path, float startX, float startY, float y) {
        float radiusP = y - startY;
        path.moveTo(startX, startY - radiusP);
        float spaceXP = (float) (radiusP * (Math.sin(Math.PI * 72 / 180)));
        float spaceYP = (float) (radiusP * (Math.cos(Math.PI * 72 / 180)));

        float spaceX2P = (float) (radiusP * (Math.sin(Math.PI * 36 / 180)));
        float spaceY2P = (float) (radiusP * (Math.cos(Math.PI * 36 / 180)));

        path.lineTo((startX + spaceXP), (startY - spaceYP));
        path.moveTo((startX + spaceXP), (startY - spaceYP));

        path.lineTo((startX + spaceX2P), (startY + spaceY2P));
        path.moveTo((startX + spaceX2P), (startY + spaceY2P));

        path.lineTo((startX - spaceX2P), (startY + spaceY2P));
        path.moveTo((startX - spaceX2P), (startY + spaceY2P));

        path.lineTo((startX - spaceXP), (startY - spaceYP));
        path.moveTo((startX - spaceXP), (startY - spaceYP));

        path.lineTo(startX, startY - radiusP);
    }

    //绘制五角星
    private static void handleStar(Path path, float startX, float startY, float y) {
        float radiusS = y - startY;
        path.moveTo(startX, startY - radiusS);
        float spaceXS = (float) (radiusS * (Math.sin(Math.PI * 72 / 180)));
        float spaceYS = (float) (radiusS * (Math.cos(Math.PI * 72 / 180)));

        float spaceX2S = (float) (radiusS * (Math.sin(Math.PI * 36 / 180)));
        float spaceY2S = (float) (radiusS * (Math.cos(Math.PI * 36 / 180)));

        path.lineTo((startX + spaceX2S), (startY + spaceY2S));
        path.moveTo((startX + spaceX2S), (startY + spaceY2S));

        path.lineTo((startX - spaceXS), (startY - spaceYS));
        path.moveTo((startX - spaceXS), (startY - spaceYS));

        path.lineTo((startX + spaceXS), (startY - spaceYS));
        path.moveTo((startX + spaceXS), (startY - spaceYS));

        path.lineTo((startX - spaceX2S), (startY + spaceY2S));
        path.moveTo((startX - spaceX2S), (startY + spaceY2S));


        path.lineTo(startX, startY - radiusS);
    }

    //绘制正方体
    private static void handleCube(Path path, float startX, float startY, float x) {
        float radiusC = x - startX;
        path.moveTo(startX, startY);

        float spaceXC = (radiusC * 1) / 3;
        float spaceYC = (radiusC * 1) / 3;
        float spaceX2C = (radiusC * 4) / 3;
        float spaceY2C = (radiusC * 2) / 3;

        if (radiusC > 0) {
            path.lineTo(startX + radiusC, startY);
            path.lineTo(startX + radiusC, startY + radiusC);
            path.lineTo(startX, startY + radiusC);
            path.lineTo(startX, startY);
            path.lineTo(startX + spaceXC, startY - spaceYC);
            path.lineTo(startX + spaceX2C, startY - spaceYC);
            path.lineTo(startX + spaceX2C, startY + spaceY2C);
            path.moveTo(startX + radiusC, startY + radiusC);
            path.lineTo(startX + spaceX2C, startY + spaceY2C);
            path.moveTo(startX + radiusC, startY);
            path.lineTo(startX + spaceX2C, startY - spaceYC);
        } else {
            path.lineTo(startX + radiusC, startY);
            path.lineTo(startX + radiusC, startY - radiusC);
            path.lineTo(startX, startY - radiusC);
            path.lineTo(startX, startY);
            path.lineTo(startX + spaceXC, startY + spaceYC);
            path.lineTo(startX + spaceX2C, startY + spaceYC);
            path.lineTo(startX + spaceX2C, startY - spaceY2C);
            path.moveTo(startX + radiusC, startY - radiusC);
            path.lineTo(startX + spaceX2C, startY - spaceY2C);
            path.moveTo(startX + radiusC, startY);
            path.lineTo(startX + spaceX2C, startY + spaceYC);
        }
    }

    //绘制四棱锥
    private static void handleCone(Path path, float startX, float startY, float x, float y) {
        float radiusCX = x - startX;
        float radiusCY = y - startY;
        path.moveTo(startX + radiusCX / 6, startY);

        float spaceX = radiusCX / 2;
        float spaceY = radiusCX * 2;
        float spaceX2 = radiusCX * 3 / 2;
        float spaceY2 = radiusCX * 3 / 2;

        if (radiusCY * radiusCX > 0) {
            path.lineTo(startX - spaceX, startY + spaceY);
            path.lineTo(startX + radiusCX, startY + spaceY);
            path.lineTo(startX + radiusCX / 6, startY);
            path.lineTo(startX + spaceX2, startY + spaceY2);
            path.lineTo(startX + radiusCX, startY + spaceY);

            path.moveTo(startX + spaceX2, startY + spaceY2);
            path.lineTo(startX, startY + spaceY2);
            path.lineTo(startX - spaceX, startY + spaceY);
            path.moveTo(startX + radiusCX / 6, startY);
            path.lineTo(startX, startY + spaceY2);
        } else {
            path.lineTo(startX - spaceX, startY - spaceY);
            path.lineTo(startX + radiusCX, startY - spaceY);
            path.lineTo(startX + radiusCX / 6, startY);
            path.lineTo(startX + spaceX2, startY - spaceY2);
            path.lineTo(startX + radiusCX, startY - spaceY);

            path.moveTo(startX + spaceX2, startY - spaceY2);
            path.lineTo(startX, startY - spaceY2);
            path.lineTo(startX - spaceX, startY - spaceY);
            path.moveTo(startX + radiusCX / 6, startY);
            path.lineTo(startX, startY - spaceY2);
        }
    }

}
