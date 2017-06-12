package com.cvter.nynote.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cvter.nynote.Presenter.PathWFCallback;
import com.cvter.nynote.Presenter.PicturePresenter;
import com.cvter.nynote.Presenter.PicturePresenterImpl;
import com.cvter.nynote.R;
import com.cvter.nynote.Utils.Constants;
import com.cvter.nynote.View.FilePopupWindow;
import com.cvter.nynote.View.GraphPopupWindow;
import com.cvter.nynote.View.IPictureView;
import com.cvter.nynote.View.PaintView;
import com.cvter.nynote.View.PaintPopupWindow;
import com.cvter.nynote.View.PictureAlertDialog;

import butterknife.BindView;
import butterknife.OnClick;


public class DrawActivity extends BaseActivity implements IPictureView, PathWFCallback {

    public PicturePresenter picturePresenter;

    private String skipType = "";
    private static String photoPath = "";

    @BindView(R.id.reading_titile_layout)
    ConstraintLayout readingTitleLayout;
    @BindView(R.id.draw_activity_layout)
    RelativeLayout drawActivityLayout;
    @BindView(R.id.drawing_title_layout)
    LinearLayout drawingTitleLayout;
    @BindView(R.id.front_activity_layout)
    LinearLayout frontActivityLayout;
    @BindView(R.id.pen_imageView)
    public ImageView penImageView;
    @BindView(R.id.eraser_imageView)
    public ImageView eraserImageView;
    @BindView(R.id.withdraw_imageView)
    ImageView withdrawImageView;
    @BindView(R.id.forward_imageView)
    ImageView forwardImageView;
    @BindView(R.id.draw_paintView)
    public PaintView drawPaintView;
    @BindView(R.id.front_imageView)
    ImageView frontImageView;

    public static int paintWidth = 20;

    private FilePopupWindow mFilePopupWindow;
    private PaintPopupWindow mPaintPopupWindow;
    private GraphPopupWindow mGraphPopupWindow;
    private PictureAlertDialog pictureDialog;

    DialogInterface.OnClickListener keyBackListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
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

        mFilePopupWindow = new FilePopupWindow(this, 800, 0);
        mPaintPopupWindow = new PaintPopupWindow(this,700, 500);
        mGraphPopupWindow = new GraphPopupWindow(this, 500, 400);

        picturePresenter = new PicturePresenterImpl(this, this);

        drawPaintView.setCallback(this);
        withdrawImageView.setEnabled(false);
        forwardImageView.setEnabled(false);
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
        mFilePopupWindow.setListener();
        mPaintPopupWindow.setListener();
        mGraphPopupWindow.setListener();
    }

    @Override
    public void doBusiness(Context context) {
        //skipType = getIntent().getExtras().getString("skipType");
        skipType = "new_edit";
        if (skipType != null && !skipType.equals("")) {
            switch (skipType) {
                case "new_edit":
                    frontActivityLayout.setVisibility(View.GONE);
                    readingTitleLayout.setVisibility(View.GONE);
                    drawingTitleLayout.setVisibility(View.VISIBLE);
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
                drawPaintView.mPaint.setGraphType(Constants.ORDINARY);
                drawPaintView.mPaint.setPenRawSize(paintWidth);
                mPaintPopupWindow.showAsDropDown(drawingTitleLayout);
                view.setSelected(true);
                eraserImageView.setSelected(false);
                drawPaintView.mPaint.setMode(Constants.Mode.DRAW);
                break;

            case R.id.eraser_imageView:
                view.setSelected(true);
                penImageView.setSelected(false);
                drawPaintView.mPaint.setMode(Constants.Mode.ERASER);
                drawPaintView.mPaint.setGraphType(Constants.ORDINARY);
                drawPaintView.mPaint.setOrdinaryPen();
                break;

            case R.id.withdraw_imageView:
                drawPaintView.withdraw();
                break;

            case R.id.picture_imageView:
                pictureDialog = new PictureAlertDialog(this);
                break;

            case R.id.graph_imageView:
                mGraphPopupWindow.showAsDropDown(drawingTitleLayout, 330, 10);
                break;

            case R.id.forward_imageView:
                drawPaintView.forward();
                break;

            case R.id.clear_imageView:
                drawPaintView.clear();
                break;

            case R.id.save_imageView:
                mFilePopupWindow.showAtLocation(drawActivityLayout, Gravity.CENTER, 0, -60);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.GALLEY_PICK:
                if (resultCode == RESULT_OK) {
                    Uri photoUri = data.getData();
                    //获取照片路径
                    String[] filePathColumn = {MediaStore.Audio.Media.DATA};
                    Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    String path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                    cursor.close();
                    picturePresenter.getSmallBitmap(path);
                }
                break;

            case Constants.TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if(data != null){
                        if(data.hasExtra("data")){
                            Bitmap bitmap = data.getParcelableExtra("data");
                            setPictureBG(bitmap);
                        }
                        }else{
                            picturePresenter.getSmallBitmap(pictureDialog.getPhotoPath());
                        }
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onError() {
        showToast("Create Fail !");
    }

    @Override
    public void setPictureBG(Bitmap bitmap) {
        frontImageView.setImageBitmap(bitmap);
        frontImageView.setVisibility(View.VISIBLE);
        drawPaintView.bringToFront();
        drawPaintView.setZOrderOnTop(true);
        drawPaintView.getHolder().setFormat(PixelFormat.TRANSPARENT);

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void pathWFState() {
        withdrawImageView.setEnabled(drawPaintView.canWithdraw());
        forwardImageView.setEnabled(drawPaintView.canForward());
    }

}
