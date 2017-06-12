package com.cvter.nynote.Presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cvter.nynote.Utils.Constants;
import com.cvter.nynote.View.IPictureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cvter on 2017/6/6.
 */

public class PicturePresenterImpl implements PicturePresenter{

    private IPictureView iPictureView;

    public PicturePresenterImpl(IPictureView iPictureView, Context mContext){
        this.iPictureView = iPictureView;
    }

    //图片存放文件目录
    @Override
    public File createImgFile() {
        //确定文件名
        File path = new File(Constants.PATH);
        if(!path.exists()){
            path.mkdirs();
        }
        String fileName = new SimpleDateFormat("HH:mm:ss").format(new Date()) + ".jpg";
        File tempFile = new File(path, fileName);
        try {
            if (tempFile.exists() && !tempFile.delete()) {
                iPictureView.onError();
                return null;

            }
            if (!tempFile.createNewFile()) {
                iPictureView.onError();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    //图片压缩
    @Override
    public void getSmallBitmap(String photoPath) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(photoPath, options);
        if (bm == null) {
            iPictureView.onError();
        }
        ByteArrayOutputStream b = null;
        try {
            b = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 30, b);

        } finally {
            try {
                if (b != null)
                    b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        iPictureView.setPictureBG(bm);

    }

    //计算图片压缩大小
    @Override
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }

        return inSampleSize;
    }
}
