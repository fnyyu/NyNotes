package com.cvter.nynote.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.cvter.nynote.presenter.PathWFCallback;
import com.cvter.nynote.presenter.PicturePresenter;
import com.cvter.nynote.presenter.PicturePresenterImpl;
import com.cvter.nynote.R;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.view.FileAlertDialog;
import com.cvter.nynote.view.GraphPopupWindow;
import com.cvter.nynote.view.IPictureView;
import com.cvter.nynote.view.PaintPopupWindow;
import com.cvter.nynote.view.PaintView;
import com.cvter.nynote.view.PictureAlertDialog;


import butterknife.BindView;
import butterknife.OnClick;


public class DrawActivity extends BaseActivity implements IPictureView, PathWFCallback {

    private PicturePresenter mPicturePresenter;

    @BindView(R.id.save_progressBar)
    ProgressBar mSaveProgressBar;
    @BindView(R.id.reading_titile_layout)
    ConstraintLayout mReadingTitleLayout;
    @BindView(R.id.draw_activity_layout)
    RelativeLayout mDrawActivityLayout;
    @BindView(R.id.drawing_title_layout)
    LinearLayout mDrawingTitleLayout;
    @BindView(R.id.front_activity_layout)
    LinearLayout mFrontActivityLayout;

    @BindView(R.id.pen_imageView)
    ImageView mPenImageView;
    @BindView(R.id.eraser_imageView)
    ImageView mEraserImageView;
    @BindView(R.id.withdraw_imageView)
    ImageView mWithdrawImageView;
    @BindView(R.id.forward_imageView)
    ImageView mForwardImageView;

    @BindView(R.id.draw_paintView)
    PaintView mDrawPaintView;

    private FileAlertDialog mFileAlertDialog;
    private PaintPopupWindow mPaintPopupWindow;
    private GraphPopupWindow mGraphPopupWindow;
    private PictureAlertDialog mPictureDialog;

    DialogInterface.OnClickListener keyBackListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "保存"按钮弹出PopupWindow
                    mFileAlertDialog.show();
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
        mDrawActivityLayout.setClickable(true);

        mFileAlertDialog = new FileAlertDialog(this);
        mPaintPopupWindow = new PaintPopupWindow(this, mDrawPaintView.getPaint(), 600, 400);
        mGraphPopupWindow = new GraphPopupWindow(this, mDrawPaintView.getPaint(), 500, 400);

        mPicturePresenter = new PicturePresenterImpl(this);

        mDrawPaintView.setCallback(this);
        mWithdrawImageView.setEnabled(false);
        mForwardImageView.setEnabled(false);
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
        mFileAlertDialog.setListener();
        mPaintPopupWindow.setListener();
        mGraphPopupWindow.setListener();
    }

    @Override
    public void doBusiness(Context context) {
        //String skipType = getIntent().getExtras().getString("skipType");
        String skipType = "new_edit";
        if (skipType != null && !skipType.equals("")) {
            switch (skipType) {
                case "new_edit":
                    mFrontActivityLayout.setVisibility(View.GONE);
                    mReadingTitleLayout.setVisibility(View.GONE);
                    mDrawingTitleLayout.setVisibility(View.VISIBLE);
                    break;

                case "read_note":
                    mFrontActivityLayout.setVisibility(View.VISIBLE);
                    mFrontActivityLayout.bringToFront();
                    mFrontActivityLayout.setClickable(true);
                    break;

                default:
                    break;
            }
        }

    }

    @OnClick(R.id.front_activity_layout)
    public void onViewClicked() {
        if (mReadingTitleLayout.getVisibility() == View.GONE) {
            mReadingTitleLayout.setAlpha(0f);
            mReadingTitleLayout.setVisibility(View.VISIBLE);
            mReadingTitleLayout.animate().alpha(1f).setDuration(2000).setListener(null);

        } else {
            mReadingTitleLayout.animate().alpha(0f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mReadingTitleLayout.setVisibility(View.GONE);
                }
            });
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle(getString(R.string.tips));
            isExit.setMessage(getString(R.string.save_note));
            isExit.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.sure), keyBackListener);
            isExit.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), keyBackListener);
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
                mReadingTitleLayout.setVisibility(View.GONE);
                mDrawingTitleLayout.setVisibility(View.VISIBLE);
                mFrontActivityLayout.setVisibility(View.GONE);

                break;

            case R.id.import_imageView:

                break;

            case R.id.share_imageView:

                break;

            case R.id.export_imageView:

                break;

            case R.id.pen_imageView:
                view.setSelected(true);
                mEraserImageView.setSelected(false);
                mDrawPaintView.getPaint().setMode(Constants.Mode.DRAW);
                mDrawPaintView.getPaint().setGraphType(Constants.ORDINARY);
                mDrawPaintView.getPaint().setPenRawSize(mDrawPaintView.getPaint().getPenRawSize());
                mPaintPopupWindow.showAsDropDown(mDrawingTitleLayout, 0, 10);
                break;

            case R.id.eraser_imageView:
                view.setSelected(true);
                mPenImageView.setSelected(false);
                mDrawPaintView.getPaint().setMode(Constants.Mode.ERASER);
                mDrawPaintView.getPaint().setGraphType(Constants.ORDINARY);
                mDrawPaintView.getPaint().setOrdinaryPen();
                break;

            case R.id.withdraw_imageView:
                mDrawPaintView.withdraw();
                break;

            case R.id.picture_imageView:
                mPictureDialog = new PictureAlertDialog(this);
                break;

            case R.id.graph_imageView:
                mGraphPopupWindow.showAsDropDown(mDrawingTitleLayout, 330, 10);
                break;

            case R.id.forward_imageView:
                mDrawPaintView.forward();
                break;

            case R.id.clear_imageView:
                mDrawPaintView.clear();
                break;

            case R.id.save_imageView:
                mFileAlertDialog.show();
                break;

            default:
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
                    mPicturePresenter.getSmallBitmap(path);
                }
                break;

            case Constants.TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (data.hasExtra("data")) {
                            Bitmap bitmap = data.getParcelableExtra("data");
                            setPictureBG(bitmap);
                        }
                    } else {
                        mPicturePresenter.getSmallBitmap(mPictureDialog.getPhotoPath());
                    }
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateError() {
        showToast("Create Fail !");
    }

    @Override
    public void setPictureBG(Bitmap bitmap) {
        mDrawPaintView.setBackgroundBitmap(bitmap);
        //mDrawPaintView.setZOrderOnTop(true);
        //mDrawPaintView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        mDrawPaintView.setIsHasBG(true);
    }

    @Override
    public void showProgress() {
        mSaveProgressBar.bringToFront();
        mSaveProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mSaveProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void pathWFState() {
        mWithdrawImageView.setEnabled(mDrawPaintView.canWithdraw());
        mForwardImageView.setEnabled(mDrawPaintView.canForward());
    }

    public PicturePresenter getPicturePresenter(){
        return mPicturePresenter;
    }

    public ImageView getEraserImageView() {
        return mEraserImageView;
    }

    public PaintView getDrawPaintView() {
        return mDrawPaintView;
    }

}
