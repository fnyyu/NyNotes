package com.cvter.nynote.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.cvter.nynote.Activity.DrawActivity;
import com.cvter.nynote.R;
import com.cvter.nynote.Utils.Constants;

import java.io.File;

/**
 * Created by cvter on 2017/6/9.
 */

public class PictureAlertDialog extends AlertDialog {

    DrawActivity context;
    private static String photoPath = "";

    public PictureAlertDialog(DrawActivity context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        View view = View.inflate(context, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                context.startActivityForResult(intent1, Constants.GALLEY_PICK);
                dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = context.picturePresenter.createImgFile();
                photoPath = file.getPath();
                Uri uri = Uri.fromFile(file);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                context.startActivityForResult(intent2, Constants.TAKE_PHOTO);// 采用ForResult打开
                dismiss();
            }
        });
        setView(view);
        show();
    }

    public static String getPhotoPath() {
        return photoPath;
    }
}
