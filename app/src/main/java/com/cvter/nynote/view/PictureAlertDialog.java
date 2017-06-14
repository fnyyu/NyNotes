package com.cvter.nynote.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.cvter.nynote.activity.DrawActivity;
import com.cvter.nynote.R;
import com.cvter.nynote.utils.Constants;

import java.io.File;

/**
 * Created by cvter on 2017/6/9.
 */

public class PictureAlertDialog extends AlertDialog {

    private DrawActivity mContext;
    private String mPhotoPath = "";
    private TextView mGalleryTextView;
    private TextView mCameraTextView;

    public PictureAlertDialog(DrawActivity context) {
        super(context);
        this.mContext = context;
        init();
    }

    private void init() {
        View pictureView = View.inflate(mContext, R.layout.dialog_select_photo, null);
        mGalleryTextView = (TextView) pictureView.findViewById(R.id.gallery_textView);
        mCameraTextView = (TextView) pictureView.findViewById(R.id.camera_textView);
        setListener();
        setView(pictureView);
        show();
    }

    private void setListener() {
        mGalleryTextView.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                skipToGallery();
                dismiss();
            }
        });
        mCameraTextView.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                skipToCamera();
                dismiss();
            }
        });
    }

    //跳转到相册
    private void skipToGallery(){
        Intent intentGallery = new Intent(Intent.ACTION_PICK, null);
        intentGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        mContext.startActivityForResult(intentGallery, Constants.GALLEY_PICK);
    }

    //跳转到相机
    private void skipToCamera(){
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = mContext.getPicturePresenter().createImgFile();
        mPhotoPath = file.getPath();
        Uri uri = Uri.fromFile(file);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        mContext.startActivityForResult(intentCamera, Constants.TAKE_PHOTO);// 采用ForResult打开
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }
}
