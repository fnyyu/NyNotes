package com.cvter.nynote.View;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.bigmercu.cBox.CheckBox;

import com.cvter.nynote.Activity.DrawActivity;
import com.cvter.nynote.Presenter.FilePresenterImpl;
import com.cvter.nynote.Presenter.IFilePresenter;
import com.cvter.nynote.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by cvter on 2017/6/8.
 */

public class FilePopupWindow extends BasePopupWindow {

    private DrawActivity mContext;
    private Button fileNameButton;
    private EditText fileNameEditText;
    private CheckBox saveAsXML, saveAsImg;
    private String fileName = "";
    IFilePresenter filePresenter;


    public FilePopupWindow(DrawActivity context, int width, int height) {
        super(context, width, height);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        View fileView = LayoutInflater.from(mContext).inflate(R.layout.window_file_name, null);
        this.setContentView(fileView);
        fileNameButton = (Button) fileView.findViewById(R.id.fileName_button);
        fileNameEditText = (EditText) fileView.findViewById(R.id.fileName_editText);
        saveAsXML = (CheckBox) fileView.findViewById(R.id.save_xml_checkBox);
        saveAsImg = (CheckBox) fileView.findViewById(R.id.save_img_checkBox);
        filePresenter = new FilePresenterImpl();
    }

    public void setListener() {

        fileNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
                fileName = fileNameEditText.getText().toString();
                if(!saveAsImg.isChecked() && !saveAsXML.isChecked()){
                    mContext.hideProgress();
                    mContext.showToast("请选择保存方式");
                    return;
                }else if(!saveAsImg.isChecked() && saveAsXML.isChecked()){


                }else if(saveAsImg.isChecked() && !saveAsXML.isChecked()){

                    Bitmap temBitmap = Bitmap.createBitmap(mContext.drawPaintView.getWidth(),
                            mContext.drawPaintView.getHeight(), Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(temBitmap);
                    if(mContext.drawPaintView.getIsHasBG()){ //若存在背景图片
                        mContext.drawPaintView.draw(canvas);
                    }else{
                        canvas.drawColor(Color.WHITE);
                        canvas.drawBitmap(mContext.drawPaintView.getBitmap(), 0, 0, null);
                    }

                    filePresenter.saveAsImg(temBitmap, fileName, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            mContext.showProgress();
                            saveSuccess();
                        }

                        @Override
                        public void onFail(String toastMessage) {
                            mContext.showToast(toastMessage);
                            mContext.finish();
                        }
                    });

                }else if(saveAsImg.isChecked() && saveAsXML.isChecked()){

                }
            }
        });

    }

    private void saveSuccess(){
        Observable.timer(3, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                mContext.hideProgress();
                mContext.showToast("保存成功");
                mContext.finish();
            }
        });
    }


}
