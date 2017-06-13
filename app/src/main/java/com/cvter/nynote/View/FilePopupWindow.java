package com.cvter.nynote.View;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.bigmercu.cBox.CheckBox;

import com.cvter.nynote.Activity.DrawActivity;
import com.cvter.nynote.Presenter.FilePresenterImpl;
import com.cvter.nynote.Presenter.IFilePresenter;
import com.cvter.nynote.R;
import com.cvter.nynote.Utils.Constants;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by cvter on 2017/6/8.
 */

public class FilePopupWindow extends BasePopupWindow {

    private DrawActivity mContext;
    private Button mFileSaveButton;
    private EditText mFileNameEditText;
    private CheckBox saveAsXML;
    private CheckBox mSaveAsImgCheckBox;
    private String mFileName = "";
    IFilePresenter mFilePresenter;


    public FilePopupWindow(Activity context, int width, int height) {
        super(context, width, height);
        this.mContext = (DrawActivity) context;
        initLayout();
    }

    private void initLayout() {
        View fileView = LayoutInflater.from(mContext).inflate(R.layout.window_file_name, null);
        this.setContentView(fileView);
        mFileSaveButton = (Button) fileView.findViewById(R.id.fileSave_button);
        mFileNameEditText = (EditText) fileView.findViewById(R.id.fileName_editText);
        saveAsXML = (CheckBox) fileView.findViewById(R.id.save_xml_checkBox);
        mSaveAsImgCheckBox = (CheckBox) fileView.findViewById(R.id.save_img_checkBox);
        mFilePresenter = new FilePresenterImpl();
    }

    public void setListener() {

        mFileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
                mFileName = mFileNameEditText.getText().toString();
                if (!mSaveAsImgCheckBox.isChecked() && !saveAsXML.isChecked()){
                    mContext.hideProgress();
                    mContext.showToast(mContext.getString(R.string.choose_saveWay));
                    return;
                } else if (mSaveAsImgCheckBox.isChecked() && !saveAsXML.isChecked()){
                    saveAsImage();
                } else {
                    saveAsImage();
                }
            }
        });

    }

    //保存成功
    private void saveSuccess(){
        mContext.showProgress();
        Observable.timer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                mContext.hideProgress();
                mContext.showToast(mContext.getString(R.string.save_success));
                mContext.finish();
            }
        });
    }

    //保存失败
    private void saveFail(String toastMessage){
        mContext.showToast(toastMessage);
        mContext.finish();
    }

    //得到要保存的bitmap
    private Bitmap getSaveBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(mContext.getDrawPaintView().getWidth(),
                mContext.getDrawPaintView().getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        if (mContext.getDrawPaintView().getIsHasBG()){ //若存在背景图片
            mContext.getDrawPaintView().draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mContext.getDrawPaintView().getBitmap(), 0, 0, null);
        }
        return bitmap;
    }

    //保存为图片类型
    private void saveAsImage(){

        String filePath = Constants.PICTURE_PATH +  mFileName + ".jpg";

        mFilePresenter.saveAsImg(getSaveBitmap(), filePath, new SaveListener() {
            @Override
            public void onSuccess() {
                saveSuccess();
            }

            @Override
            public void onFail(String toastMessage) {
                saveFail(toastMessage);
            }
        });
    }


}
