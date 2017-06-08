package com.cvter.nynote.Presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.cvter.nynote.Utils.CommonUtils;
import com.cvter.nynote.View.IPictureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static android.R.attr.path;

/**
 * Created by cvter on 2017/6/6.
 */

public class PicturePresenterImpl implements PicturePresenter{

    private IPictureView iPictureView;
    private Context mContext;

    public PicturePresenterImpl(IPictureView iPictureView, Context mContext){
        this.iPictureView = iPictureView;
        this.mContext = mContext;
    }

    //设置图片到ImageView中
    @Override
    public void setPicToView(Bitmap mBitmap) {

        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd卡是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(CommonUtils.PATH);
        if (file.mkdirs()) {
            // 创建以此File对象为名（path）的文件夹
            String fileName = path + "myPic.jpg";//图片名字
            try {
                b = new FileOutputStream(fileName);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 30, b);// 把数据写入文件（compress：压缩）

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (b != null) {
                        b.flush();
                        b.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {
            iPictureView.onError();
        }
    }

    //图片存放文件目录
    @Override
    public File createImgFile() {
        //确定文件名
        String fileName = SimpleDateFormat.getDateTimeInstance() + ".jpg";
        File dir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStorageDirectory();
        } else {
            dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        File tempFile = new File(dir, fileName);
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
