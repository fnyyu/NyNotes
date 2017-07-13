package com.cvter.nynote.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Xml;

import com.cvter.nynote.R;
import com.cvter.nynote.model.PaintInfo;
import com.cvter.nynote.model.PathDrawingInfo;
import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.model.PointInfo;
import com.cvter.nynote.utils.CommonMethod;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.ImportListener;
import com.cvter.nynote.utils.SaveListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cvter on 2017/6/9.
 * 文件处理逻辑实现类
 */

public class FilePresenterImpl implements IFilePresenter {

    private static final String PATH_LIST = "PathList";
    private static final String DRAW_PATH = "DrawPath";
    private static final String PAINT = "Paint";
    private static final String COLOR = "color";
    private static final String PEN_TYPE = "PenType";
    private static final String WIDTH = "width";
    private static final String TYPE = "type";
    private static final String GRAPH_TYPE = "GraphType";
    private static final String PATH = "Path";
    private static final String POINT = "point";
    private static final String CODE_TYPE = "UTF-8";

    private static final String FILE_TYPE = "FilePresenterImpl";

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Handler mFileHandler;
    private Context mContext;

    public FilePresenterImpl(Context context) {
        this.mContext = context;
        mHandlerThread = new HandlerThread("FileOperation");
        mHandlerThread.start();
        mHandler = new Handler(Looper.getMainLooper());
        mFileHandler = new Handler(mHandlerThread.getLooper());

    }

    @Override
    public void saveAsXML(final List<PathInfo> pathList, final String path, final SaveListener listener) {

        mFileHandler.post(new Runnable() {
            @Override
            public void run() {

                OutputStream outputStream = null;
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (file.createNewFile()) {
                        outputStream = new FileOutputStream(file);

                        XmlSerializer serializer = Xml.newSerializer();
                        serializer.setOutput(outputStream, CODE_TYPE);
                        serializer.startDocument(CODE_TYPE, true);
                        serializer.startTag(null, PATH_LIST);
                        for (PathInfo pathInfo : pathList) {
                            serializer.startTag(null, DRAW_PATH);
                            Paint paint = pathInfo.getPaint();
                            serializer.startTag(null, PAINT);

                            serializer.startTag(null, COLOR);
                            serializer.text(String.valueOf(paint.getColor()));
                            serializer.endTag(null, COLOR);

                            serializer.startTag(null, PEN_TYPE);
                            serializer.text(String.valueOf(pathInfo.getPenType()));
                            serializer.endTag(null, PEN_TYPE);

                            serializer.startTag(null, WIDTH);
                            serializer.text(String.valueOf(paint.getStrokeWidth()));
                            serializer.endTag(null, WIDTH);

                            serializer.startTag(null, TYPE);
                            serializer.text(String.valueOf(pathInfo.getPaintType()));
                            serializer.endTag(null, TYPE);

                            serializer.startTag(null, GRAPH_TYPE);
                            serializer.text(String.valueOf(pathInfo.getGraphType()));
                            serializer.endTag(null, GRAPH_TYPE);

                            serializer.endTag(null, PAINT);

                            serializer.startTag(null, PATH);
                            List<PointInfo> mPoints = pathInfo.getPointList();
                            for (PointInfo points : mPoints) {
                                serializer.startTag(null, POINT);
                                serializer.text(points.mPointX + "," + points.mPointY);
                                serializer.endTag(null, POINT);
                            }
                            serializer.endTag(null, PATH);

                            serializer.endTag(null, DRAW_PATH);
                        }
                        serializer.endTag(null, PATH_LIST);
                        serializer.endDocument();

                        outputStream.flush();
                        outputStream.close();
                    }

                } catch (Exception e) {
                    saveFail(listener, e.getMessage());

                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            saveFail(listener, e.getMessage());
                        }
                    }
                }
            }
        });

    }

    @Override
    public void saveAsImg(final Bitmap bitmap, final String path, final SaveListener listener) {


        mFileHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (file.createNewFile()) {

                        Bitmap compressBitmap = CommonMethod.getCompressBitmap(bitmap);
                        OutputStream outputStream = new FileOutputStream(file);
                        compressBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        outputStream.close();

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess();
                            }
                        });
                    }

                } catch (final Exception e) {
                    saveFail(listener, "saveAsImg" + e.getMessage());
                }
            }
        });

    }

    @Override
    public void saveAsBg(final Bitmap bitmap, final String fileName, final SaveListener listener) {

        mFileHandler.post(new Runnable() {
            @Override
            public void run() {
                OutputStream outputStream = null;
                try {
                    File file = new File(fileName);
                    if (file.exists()) {
                        file.delete();
                    }

                    if (file.createNewFile()) {
                        outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        outputStream.close();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess();
                            }
                        });
                    }

                } catch (final Exception e) {
                    saveFail(listener, "saveAsBg" + e.getMessage());
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        saveFail(listener, "saveAsBg" + e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void importXML(final String filePath, final ImportListener listener) {

        mFileHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final LinkedList<PathInfo> drawPathList = new LinkedList<>();
                    PathInfo drawPath = null;
                    Path path = null;
                    PaintInfo paint = null;
                    LinkedList<PointInfo> pointList = null;
                    boolean isFirstPoint = true;
                    float startX = 0f;
                    float startY = 0f;

                    InputStream is = new FileInputStream(new File(filePath));
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(is, CODE_TYPE);
                    int eventType = parser.getEventType();
                    while (XmlPullParser.END_DOCUMENT != eventType) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:

                                break;

                            case XmlPullParser.START_TAG:
                                String startTag = parser.getName();
                                switch (startTag) {
                                    case DRAW_PATH:
                                        drawPath = new PathDrawingInfo();
                                        isFirstPoint = true;
                                        break;
                                    case PAINT:
                                        paint = new PaintInfo();
                                        paint.setStyle(Paint.Style.STROKE);
                                        paint.setStrokeCap(Paint.Cap.ROUND);
                                        paint.setAntiAlias(true);

                                        break;

                                    case COLOR:
                                        if (paint != null) {
                                            paint.setColor(Integer.parseInt(parser.nextText().trim()));
                                        }
                                        break;

                                    case PEN_TYPE:
                                        int penType = Integer.parseInt(parser.nextText().trim());
                                        switch (penType) {
                                            case Constants.ORDINARY:
                                                if (paint != null) {
                                                    paint.setOrdinaryPen();
                                                }
                                                break;
                                            case Constants.TRANS_PEN:
                                                if (paint != null) {
                                                    paint.setTransPen();
                                                }
                                                break;
                                            case Constants.INK_PEN:
                                                if (paint != null) {
                                                    paint.setInkPen();
                                                }
                                                break;
                                            case Constants.DISCRETE_PEN:
                                                if (paint != null) {
                                                    paint.setDiscretePen();
                                                }
                                                break;
                                            case Constants.DASH_PEN:
                                                if (paint != null) {
                                                    paint.setDashPen();
                                                }
                                                break;
                                            default:
                                                break;

                                        }

                                        break;

                                    case WIDTH:
                                        if (paint != null) {
                                            paint.setStrokeWidth(Float.parseFloat(parser.nextText().trim()));
                                        }
                                        break;

                                    case TYPE:
                                        int drawType = Integer.parseInt(parser.nextText().trim());
                                        PorterDuff.Mode mode = (drawType == 0) ? null : PorterDuff.Mode.CLEAR;
                                        if (mode != null && paint != null) {
                                            paint.setXfermode(new PorterDuffXfermode(mode));
                                        } else if (mode == null && paint != null) {
                                            paint.setXfermode(null);
                                        }
                                        if (drawPath != null) {
                                            drawPath.setPaintType(drawType);
                                        }
                                        break;

                                    case GRAPH_TYPE:
                                        int graphType = Integer.parseInt(parser.nextText().trim());
                                        if (drawPath != null) {
                                            drawPath.setGraphType(graphType);
                                        }
                                        break;

                                    case PATH:
                                        path = new Path();
                                        pointList = new LinkedList<>();
                                        break;

                                    case POINT:
                                        PointInfo point = new PointInfo();
                                        String[] pointArr = parser.nextText().trim().split(",");
                                        point.mPointX = Float.parseFloat(pointArr[0]);
                                        point.mPointY = Float.parseFloat(pointArr[1]);
                                        if (isFirstPoint && path != null) {
                                            startX = point.mPointX;
                                            startY = point.mPointY;
                                            path.moveTo(startX, startY);
                                            isFirstPoint = false;
                                        }
                                        if (pointList != null) {
                                            pointList.add(point);
                                        }

                                        if (drawPath != null) {
                                            CommonMethod.handleGraphType(path, startX, startY, point.mPointX, point.mPointY, drawPath.getGraphType(), Constants.DRAW);
                                        }
                                        startX = point.mPointX;
                                        startY = point.mPointY;
                                        break;

                                    default:
                                        break;
                                }
                                break;

                            case XmlPullParser.END_TAG:
                                String endTag = parser.getName();
                                switch (endTag) {

                                    case PATH:
                                        if (drawPath != null) {
                                            drawPath.setPath(path);
                                            drawPath.setPointList(pointList);
                                        }
                                        break;

                                    case PAINT:
                                        if (drawPath != null) {
                                            drawPath.setPaint(paint);
                                        }
                                        break;

                                    case DRAW_PATH:
                                        if (drawPath != null) {
                                            drawPathList.add(drawPath);
                                        }
                                        break;

                                    default:
                                        break;

                                }
                                break;

                            default:
                                break;
                        }
                        eventType = parser.next();
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(drawPathList);
                        }
                    });

                } catch (final Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFail(mContext.getString(R.string.import_fail) + e.getMessage());
                        }
                    });
                }
            }
        });

    }

    @Override
    public void createTempFile() {

        mFileHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    createFile(Constants.TEMP_PATH);
                    createFile(Constants.TEMP_XML_PATH);
                    createFile(Constants.TEMP_IMG_PATH);
                    createFile(Constants.TEMP_BG_PATH);
                } catch (Exception e) {
                    Log.e(FILE_TYPE, e.getMessage());
                }
            }
        });

    }

    @Override
    public void modifyTempFile(final String fileName, final SaveListener listener) {

        mFileHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(Constants.TEMP_PATH);
                    if (file.renameTo(new File(Constants.NOTE_PATH + fileName))) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess();
                            }
                        });
                    }
                } catch (Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFail(mContext.getString(R.string.save_fail));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void deleteTempFile() {

        mFileHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    deleteSubFile(new File(Constants.TEMP_XML_PATH));
                    deleteSubFile(new File(Constants.TEMP_IMG_PATH));
                    deleteSubFile(new File(Constants.TEMP_BG_PATH));
                }catch (Exception e){
                    Log.e(FILE_TYPE, e.getMessage());
                }

            }
        });

    }

    @Override
    public int getFileSize(String filePath) {
        int result = 0;
        try{
            File file = new File(filePath);
            if (file.exists() && file.isDirectory()) {
               result =  file.listFiles().length;
            }
        }catch (Exception e){
            Log.e(FILE_TYPE, e.getMessage());
        }

        return result;
    }

    @Override
    public boolean deleteFile(File file) {
        try {
            if (null == file || !file.exists()) {
                return false;
            } else {
                if (file.isFile()) {
                    return file.delete();
                }
                if (file.isDirectory()) {
                    File[] childFile = file.listFiles();
                    if (childFile == null || childFile.length == 0) {
                        return file.delete();
                    }
                    for (File f : childFile) {
                        deleteFile(f);
                    }
                    return file.delete();
                }
            }
        } catch (Exception e) {
            Log.e(FILE_TYPE, e.getMessage());
        }

        return false;
    }

    private void deleteSubFile(File file) {
        if (null == file || !file.exists() || !file.isDirectory())
            return;
        for (File subFile : file.listFiles()) {
            if (subFile.isFile())
                subFile.delete(); // 删除所有文件
        }
    }

    private void createFile(String path) {
        File imageFile = new File(path);
        if (!imageFile.exists()) {
            imageFile.mkdir();
        }
    }

    private void saveFail(final SaveListener listener, final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onFail(message);
            }
        });
    }

    @Override
    public void quitThread(){
        mHandlerThread.quitSafely();
    }

}
