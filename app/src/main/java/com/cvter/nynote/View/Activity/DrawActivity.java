package com.cvter.nynote.View.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cvter.nynote.R;
import com.cvter.nynote.Utils.CommonUtils;
import com.cvter.nynote.View.View.BasePopupWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

import static android.R.attr.path;


public class DrawActivity extends BaseActivity {

    String skipType = "";

    @BindView(R.id.reading_titile_layout)
    ConstraintLayout readingTitleLayout;
    @BindView(R.id.draw_activity_layout)
    RelativeLayout drawActivityLayout;
    @BindView(R.id.drawing_title_layout)
    LinearLayout drawingTitleLayout;
    @BindView(R.id.front_activity_layout)
    LinearLayout frontActivityLayout;

    BasePopupWindow mFilePopupWindow;

    DialogInterface.OnClickListener keyBackListener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case AlertDialog.BUTTON_POSITIVE:// "保存"按钮弹出PopupWindow
                    mFilePopupWindow.showAtLocation(drawActivityLayout, Gravity.CENTER, 0, 0);
                    break;

                case AlertDialog.BUTTON_NEGATIVE:// "取消"按钮退出该界面
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void initWidget(Bundle bundle) {
        drawActivityLayout.setClickable(true);
        mFilePopupWindow = new BasePopupWindow(this);
        mFilePopupWindow.setContentView(LayoutInflater.from(DrawActivity.this).inflate(R.layout.window_file_name, null));
    }

    @Override
    public void initParams(Bundle params) {

        requestWindowFeature(Window.FEATURE_NO_TITLE); //无title

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_draw;
    }

    @Override
    public void setListener() {



    }

    @Override
    public void doBusiness(Context context) {
        skipType = getIntent().getExtras().getString("skipType");
        if(skipType != null && !skipType.equals("")){
            switch (skipType) {
                case "new_edit":
                    frontActivityLayout.setVisibility(View.GONE);
                    break;

                case "read_note":
                    frontActivityLayout.setVisibility(View.VISIBLE);
                    frontActivityLayout.bringToFront();
                    frontActivityLayout.setClickable(true);
                    break;

                default:
                    break;
            }
        }

    }

    @OnClick(R.id.front_activity_layout)
    public void onViewClicked() {
        if (readingTitleLayout.getVisibility() == View.GONE) {
            readingTitleLayout.setAlpha(0f);
            readingTitleLayout.setVisibility(View.VISIBLE);
            readingTitleLayout.animate().alpha(1f).setDuration(2000).setListener(null);

        } else {
            readingTitleLayout.animate().alpha(0f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    readingTitleLayout.setVisibility(View.GONE);
                }
            });
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle("系统提示");
            isExit.setMessage("是否保存note");
            isExit.setButton(AlertDialog.BUTTON_POSITIVE, "保存", keyBackListener);
            isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", keyBackListener);
            isExit.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.draw_imageView, R.id.import_imageView, R.id.share_imageView,
            R.id.export_imageView, R.id.pen_imageView, R.id.eraser_imageView,
            R.id.withdraw_imageView, R.id.picture_imageView, R.id.graph_imageView,
            R.id.forward_imageView, R.id.clear_imageView, R.id.save_imageView})

    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.draw_imageView:
                readingTitleLayout.setVisibility(View.GONE);
                drawingTitleLayout.setVisibility(View.VISIBLE);
                frontActivityLayout.setVisibility(View.GONE);

                break;

            case R.id.import_imageView:

                break;

            case R.id.share_imageView:

                break;

            case R.id.export_imageView:

                break;

            case R.id.pen_imageView:

                break;

            case R.id.eraser_imageView:

                break;

            case R.id.withdraw_imageView:

                break;

            case R.id.picture_imageView:
                showTypeDialog();
                break;

            case R.id.graph_imageView:

                break;

            case R.id.forward_imageView:

                break;

            case R.id.clear_imageView:

                break;

            case R.id.save_imageView:
                mFilePopupWindow.showAtLocation(drawActivityLayout, Gravity.CENTER, 0, 0);
                break;

        }
    }

    //显示图片选择dialog
    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawActivity.this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(DrawActivity.this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 0);
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "myHead.jpg")));
                startActivityForResult(intent2, 1);// 采用ForResult打开
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    //裁剪图片
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        //找到指定URI对应的资源图片
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        //进入系统裁剪图片的界面
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if (resultCode == RESULT_OK){
                    cropPhoto(data.getData());
                }
                break;

            case 1:
                if (resultCode == RESULT_OK){
                    File temp = new File(Environment.getExternalStorageDirectory() + "/myPic.jpg");
                    cropPhoto(Uri.fromFile(temp));
                }
                break;

            case 2:
                if (resultCode == RESULT_OK){
                    if (data != null) {
                        Bundle extras = data.getExtras();
                         Bitmap bitmap = extras.getParcelable("data");
                        if (bitmap != null) {
                            setPicToView(bitmap);
                            drawActivityLayout.setBackground(new BitmapDrawable(bitmap));
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //设置图片到ImageView中
    private void setPicToView(Bitmap mBitmap){
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd卡是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(CommonUtils.PATH);
        file.mkdirs();// 创建以此File对象为名（path）的文件夹
        String fileName = path + "myPic.jpg";//图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件（compress：压缩）

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
