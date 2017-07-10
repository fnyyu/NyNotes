package com.cvter.nynote.presenter;

import android.graphics.Bitmap;
import android.graphics.Path;

import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.utils.ImportListener;
import com.cvter.nynote.utils.SaveListener;

import java.io.File;
import java.util.List;

/**
 * Created by cvter on 2017/6/9.
 * 文件处理逻辑接口
 */

public interface IFilePresenter {

    void saveAsXML(List<PathInfo> drawPaths, String path, SaveListener listener);

    void saveAsImg(Bitmap bitmap, String path, SaveListener listener);

    void saveAsBg(Bitmap bitmap, String fileName, SaveListener listener);

    void importXML(String path, ImportListener listener);

    void createTempFile();

    void modifyTempFile(String fileName, SaveListener listener);

    void deleteTempFile();

    int getFileSize(String filePath);

    boolean deleteFile(File file);

    void quitThread();

}
