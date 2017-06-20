package com.cvter.nynote.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.HandlerThread;
import android.util.Log;

import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.SaveListener;
import com.cvter.nynote.view.IPictureView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by cvter on 2017/6/6.
 */

public class PicturePresenterImpl implements PicturePresenter{

    private IPictureView mIPictureView;

    private static final String TAG = "PicturePresenterImpl";

    public PicturePresenterImpl(IPictureView iPictureView){
        this.mIPictureView = iPictureView;
    }

    //图片存放文件目录
    @Override
    public File createImgFile(String curPage) {
        //确定文件名
        File path = new File(Constants.TEMP_BG_PATH);
        if(!path.exists()){
            path.mkdirs();
        }
        String fileName = curPage + ".png";
        File tempFile = new File(path, fileName);
        try {
            if (tempFile.exists() && !tempFile.delete()) {
                mIPictureView.onCreateError();
                return null;

            }
            if (!tempFile.createNewFile()) {
                mIPictureView.onCreateError();
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return tempFile;
    }

    //图片压缩
    @Override
    public void getSmallBitmap (String photoPath, int type, String  page) {

        if(new File(photoPath).exists()){
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, newOpts);
            newOpts.inJustDecodeBounds = false;
            int width = newOpts.outWidth;
            int height = newOpts.outHeight;
            int be = 1;
            if (width > height && width > 480f) {
                be = (int) (newOpts.outWidth / 480f);
            } else if (width < height && height > 800f) {
                be = (int) (newOpts.outHeight / 800f);
            }
            if (be <= 0)
                be = 1;
            newOpts.inSampleSize = be;// 设置缩放比例
            // 重新读入图片
            bitmap = BitmapFactory.decodeFile(photoPath, newOpts);
            bitmap = compressImage(bitmap);// 压缩好比例大小后再进行质量压缩

            if (type == Constants.GALLEY_PICK){
                saveAsImg(bitmap, createImgFile(page).getPath());
            }

            mIPictureView.setPictureBG(bitmap);
        }

    }

    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        int options = 90;

        while (stream.toByteArray().length / 1024 > 100) {
            stream.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, stream);
            options -= 10;
        }
        Bitmap bitmap = null;
        try{
           bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(stream.toByteArray()), null, null);
        }
        catch (Exception e){
            Log.e(TAG, "compressImage" + e.getMessage());
        }

        return bitmap;
    }

    private void saveAsImg(final Bitmap bitmap, final String path) {

        new HandlerThread("saveAsPicture"){
            @Override
            public void run() {
                try{
                    File file = new File(path);
                    if(file.exists()){
                        file.delete();
                    }
                    if (file.createNewFile()){
                        Bitmap compressBitmap = Constants.getCompressBitmap(bitmap);
                        OutputStream outputStream = new FileOutputStream(file);
                        compressBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        outputStream.close();
                    }

                }catch (final Exception e){
                    Log.e(TAG, "saveAsImg" + e.getMessage());
                }
            }
        }.start();

    }

}
