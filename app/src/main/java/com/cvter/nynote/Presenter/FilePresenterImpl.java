package com.cvter.nynote.Presenter;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.cvter.nynote.Utils.CommonUtils;
import com.cvter.nynote.View.SaveListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by cvter on 2017/6/9.
 */

public class FilePresenterImpl implements IFilePresenter {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void saveAsXML() {

    }

    @Override
    public void saveAsImg(final Bitmap bitmap, final String path, final SaveListener listener) {

        new HandlerThread("saveAsPicture"){
            @Override
            public void run() {
                try{
                    File file = new File(CommonUtils.PICTURE_PATH + "/" + path + ".jpg");
                    file.createNewFile();
                    OutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    outputStream.close();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess();
                        }
                    });
                }catch (final Exception e){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFail("保存图片失败 - " + e.getMessage());
                        }
                    });
                }
            }
        }.start();

    }

    @Override
    public void importXML() {

    }
}
