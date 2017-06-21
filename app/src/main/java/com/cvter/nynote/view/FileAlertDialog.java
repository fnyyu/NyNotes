package com.cvter.nynote.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bigmercu.cBox.CheckBox;

import com.cvter.nynote.activity.DrawActivity;
import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.presenter.FilePresenterImpl;
import com.cvter.nynote.presenter.IFilePresenter;
import com.cvter.nynote.R;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.SaveListener;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by cvter on 2017/6/8.
 * 文件保存AlertDialog
 */

public class FileAlertDialog extends AlertDialog {

    private DrawActivity mContext;
    private Button mFileSaveButton;
    private EditText mFileNameEditText;
    private CheckBox saveAsXML;
    private CheckBox mSaveAsImgCheckBox;
    private String mFileName = "";
    private IFilePresenter mFilePresenter;
    private static final String TAG = "FileAlertDialog";

    public FileAlertDialog(Activity context) {
        super(context);
        this.mContext = (DrawActivity) context;
        initLayout();
    }

    private void initLayout() {
        View fileView = View.inflate(mContext, R.layout.dialog_file_name, null);
        mFileSaveButton = (Button) fileView.findViewById(R.id.fileSave_button);
        mFileNameEditText = (EditText) fileView.findViewById(R.id.fileName_editText);
        saveAsXML = (CheckBox) fileView.findViewById(R.id.save_xml_checkBox);
        mSaveAsImgCheckBox = (CheckBox) fileView.findViewById(R.id.save_img_checkBox);
        mFilePresenter = new FilePresenterImpl(mContext);
        setView(fileView);
    }

    public void setListener() {

        mFileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
                mFileName = mFileNameEditText.getText().toString();
                if (mFileName.isEmpty() || mFileName.equals("")) {
                    mContext.showToast(mContext.getString(R.string.enter_null));
                    return;
                }

                if (mFileName.equals(stringFilter(mFileName))) {
                    mContext.showToast(mContext.getString(R.string.enter_fail));
                    mFileNameEditText.setText("");
                    return;
                }

                if (!mSaveAsImgCheckBox.isChecked() && !saveAsXML.isChecked()) {
                    mContext.hideProgress();
                    mContext.showToast(mContext.getString(R.string.choose_saveWay));

                } else if (mSaveAsImgCheckBox.isChecked() && !saveAsXML.isChecked()) {
                    saveImage(Constants.PICTURE_PATH + mFileName + ".png");

                } else {
                    saveImage(Constants.PICTURE_PATH + mFileName + ".png");
                    saveAsXML();
                }
            }
        });

    }

    private void saveAsXML() {

        String filePath = Constants.TEMP_XML_PATHS + Integer.parseInt(mContext.getCurPagesTextView().getText().toString()) + ".xml";

        mFilePresenter.saveAsXML(getSavePath(), filePath, new SaveListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFail(String toastMessage) {
                saveFail(toastMessage);
            }
        });

    }

    private void changeTempFile() {
        mFilePresenter.modifyTempFile(mFileName, new SaveListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFail(String toastMessage) {
                saveFail(toastMessage);
            }
        });
    }

    private List<PathInfo> getSavePath() {
        return mContext.getDrawPaintView().getDrawingList();
    }

    //保存成功
    private void saveSuccess() {
        mContext.showProgress();
        Observable.timer(2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                mContext.hideProgress();
                mContext.showToast(mContext.getString(R.string.save_success));
                mContext.finish();
            }
        });
    }

    //保存失败
    private void saveFail(String toastMessage) {
        Log.e(TAG, toastMessage);
        mContext.finish();
    }

    //得到要保存的bitmap
    public Bitmap getSaveBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(mContext.getDrawPaintView().getWidth(),
                mContext.getDrawPaintView().getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        if (mContext.getDrawPaintView().getIsHasBG()) { //若存在背景图片
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mContext.getDrawPaintView().getBackgroundBitmap(), 0, 0, null);
            canvas.drawBitmap(mContext.getDrawPaintView().getBitmap(), 0, 0, null);
        } else {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mContext.getDrawPaintView().getBitmap(), 0, 0, null);
        }
        return bitmap;
    }

    //保存为图片类型
    private void saveImage(String filePath) {

        mFilePresenter.saveAsImg(getSaveBitmap(), filePath, new SaveListener() {
            @Override
            public void onSuccess() {
                changeTempFile();
                saveSuccess();
            }

            @Override
            public void onFail(String toastMessage) {
                saveFail(toastMessage);
            }
        });

    }

    private static String stringFilter(String str) {
        // 只允许字母、数字和汉字
        String regEx = "[\\u4e00-\\u9fa5_a-zA-Z0-9_]{2,10}";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

}
