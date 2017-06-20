package com.cvter.nynote.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cvter.nynote.R;
import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.presenter.FilePresenterImpl;
import com.cvter.nynote.presenter.IFilePresenter;
import com.cvter.nynote.presenter.PathWFCallback;
import com.cvter.nynote.presenter.PicturePresenter;
import com.cvter.nynote.presenter.PicturePresenterImpl;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.ImportListener;
import com.cvter.nynote.utils.SaveListener;
import com.cvter.nynote.view.FileAlertDialog;
import com.cvter.nynote.view.GestureImageView;
import com.cvter.nynote.view.GraphPopupWindow;
import com.cvter.nynote.view.IPictureView;
import com.cvter.nynote.view.PagesPopupWindow;
import com.cvter.nynote.view.PaintPopupWindow;
import com.cvter.nynote.view.PaintView;
import com.cvter.nynote.view.PictureAlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class DrawActivity extends BaseActivity implements IPictureView, PathWFCallback {

    @BindView(R.id.scale_imageView)
    GestureImageView scaleImageView;
    @BindView(R.id.more_pages_linearLayout)
    LinearLayout morePagesLinearLayout;
    private PicturePresenter mPicturePresenter;
    private IFilePresenter mFilePresenter;

    @BindView(R.id.save_progressBar)
    ProgressBar mSaveProgressBar;
    @BindView(R.id.reading_titile_layout)
    ConstraintLayout mReadingTitleLayout;
    @BindView(R.id.draw_activity_layout)
    RelativeLayout mDrawActivityLayout;
    @BindView(R.id.drawing_title_layout)
    LinearLayout mDrawingTitleLayout;

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
    @BindView(R.id.cur_pages_textView)
    TextView mCurPagesTextView;
    @BindView(R.id.all_pages_textView)
    TextView mAllPagesTextView;

    private FileAlertDialog mFileAlertDialog;
    private PaintPopupWindow mPaintPopupWindow;
    private GraphPopupWindow mGraphPopupWindow;
    private PictureAlertDialog mPictureDialog;
    private PagesPopupWindow mPagesPopupWindow;

    private String skipType = "";
    private boolean ifCanDraw;
    private static final String TAG = "DrawActivity";
    private int pageSize = 0;
    private String noteName = "";
    private List<PathInfo> mRevertList;

    DialogInterface.OnClickListener keyBackListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "保存"按钮弹出PopupWindow
                    if (skipType.equals(Constants.NEW_EDIT)) {
                        mFileAlertDialog.show();
                    } else {
                        EditFileSave();
                    }

                    break;

                case AlertDialog.BUTTON_NEGATIVE:// "取消"按钮退出该界面
                    mFilePresenter.deleteTempFile();
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
        mPagesPopupWindow = new PagesPopupWindow(this, Constants.getScreenSize(this)[0], Constants.getScreenSize(this)[1] / 3);

        mPicturePresenter = new PicturePresenterImpl(this);
        mFilePresenter = new FilePresenterImpl(this);

        mDrawPaintView.setCallback(this);
        mWithdrawImageView.setEnabled(false);
        mForwardImageView.setEnabled(false);

        skipType = getIntent().getExtras().getString("skipType");
        noteName = getIntent().getStringExtra("noteName") == null ? "" : getIntent().getStringExtra("noteName").replace(".png", "");
    }

    @Override
    public void initParams(Bundle params) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        mPagesPopupWindow.setListener();
    }

    @Override
    public void doBusiness(Context context) {

        if (skipType != null && !skipType.equals("")) {
            switch (skipType) {
                case Constants.NEW_EDIT:
                    mDrawPaintView.setIfCanDraw(true);
                    mReadingTitleLayout.setVisibility(View.GONE);
                    mDrawingTitleLayout.setVisibility(View.VISIBLE);
                    ifCanDraw = true;
                    break;

                case Constants.READ_NOTE:

                    mDrawPaintView.setIfCanDraw(false);
                    ifCanDraw = false;
                    mAllPagesTextView.bringToFront();
                    mAllPagesTextView.setClickable(true);
                    final String notePath = Constants.NOTE_PATH + noteName + "/xml/1.xml";
                    final String imagePath = Constants.NOTE_PATH + noteName + "/bg/1.png";
                    pageSize = mFilePresenter.getFileSize(Constants.NOTE_PATH + noteName + "/xml");
                    mAllPagesTextView.setText(String.valueOf(pageSize));
                    mPagesPopupWindow.setSaveBitmapSize(pageSize);
                    mFilePresenter.importXML(notePath, new ImportListener() {
                        @Override
                        public void onSuccess(List<PathInfo> info) {
                            mDrawPaintView.setDrawingList(info);
                        }

                        @Override
                        public void onFail(String toastMessage) {
                            Log.e(TAG, toastMessage);
                        }

                    });
                    mPicturePresenter.getSmallBitmap(imagePath, 3, "");
                    break;

                default:
                    break;
            }
        }

    }

    @OnClick(R.id.draw_activity_layout)
    public void onViewClicked() {
        if (mReadingTitleLayout.getVisibility() == View.GONE) {
            mReadingTitleLayout.setAlpha(0f);
            mReadingTitleLayout.setVisibility(View.VISIBLE);
            mReadingTitleLayout.animate().alpha(1f).setDuration(2000).setListener(null);

        } else {
            mReadingTitleLayout.animate().alpha(0f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mReadingTitleLayout != null) {
                        mReadingTitleLayout.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && ifCanDraw) {
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle(getString(R.string.tips));
            isExit.setMessage(getString(R.string.save_note));
            isExit.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.sure), keyBackListener);
            isExit.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), keyBackListener);
            isExit.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.draw_imageView, R.id.gesture_imageView, R.id.share_imageView,
            R.id.export_imageView, R.id.pen_imageView, R.id.eraser_imageView,
            R.id.withdraw_imageView, R.id.picture_imageView, R.id.graph_imageView,
            R.id.forward_imageView, R.id.clear_imageView, R.id.save_imageView})

    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.draw_imageView:
                setNoScale();
                mReadingTitleLayout.setVisibility(View.GONE);
                mDrawingTitleLayout.setVisibility(View.VISIBLE);
                mDrawPaintView.setIfCanDraw(true);
                ifCanDraw = true;
                mFilePresenter.createTempFile();

                break;

            case R.id.gesture_imageView:
                if (scaleImageView.getVisibility() == View.VISIBLE) {
                    setNoScale();

                } else {
                    setCanScale();
                }


                break;

            case R.id.share_imageView:

                break;

            case R.id.export_imageView:


                break;

            case R.id.pen_imageView:
                view.setSelected(true);
                mEraserImageView.setSelected(false);
                mDrawPaintView.getPaint().setMode(Constants.Mode.DRAW);
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
                mDrawPaintView.getPaint().setMode(Constants.Mode.DRAW);
                mGraphPopupWindow.showAsDropDown(mDrawingTitleLayout, 330, 10);
                break;

            case R.id.forward_imageView:
                mDrawPaintView.forward();
                break;

            case R.id.clear_imageView:
                mDrawPaintView.clear();
                break;

            case R.id.save_imageView:
                if (getIntent().getExtras().getString("skipType").equals("new_edit")) {
                    mFileAlertDialog.show();
                } else {
                    EditFileSave();
                }
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
                    mPicturePresenter.getSmallBitmap(path, Constants.GALLEY_PICK, String.valueOf(getCurPagesTextView().getText().toString()));
                }
                break;

            case Constants.TAKE_PHOTO:
                if (resultCode == RESULT_OK && mPictureDialog.getPhotoPath() != null) {
                    mPicturePresenter.getSmallBitmap(mPictureDialog.getPhotoPath(), Constants.TAKE_PHOTO, "");
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
        Bitmap backGroundBitmap = Constants.getCompressBitmap(bitmap);
        mDrawPaintView.setBackgroundBitmap(backGroundBitmap);
        mDrawPaintView.setIsHasBG(true);
    }

    @Override
    public void showProgress() {
        if (mSaveProgressBar != null) {
            mSaveProgressBar.bringToFront();
            mSaveProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgress() {
        if (mSaveProgressBar != null) {
            mSaveProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void pathWFState() {
        mWithdrawImageView.setEnabled(mDrawPaintView.canWithdraw());
        mForwardImageView.setEnabled(mDrawPaintView.canForward());
    }

    public PicturePresenter getPicturePresenter() {
        return mPicturePresenter;
    }

    public ImageView getEraserImageView() {
        return mEraserImageView;
    }

    public PaintView getDrawPaintView() {
        return mDrawPaintView;
    }

    public TextView getCurPagesTextView() {
        return mCurPagesTextView;
    }

    public TextView getAllPagesTextView() {
        return mAllPagesTextView;
    }

    private void EditFileSave() {

        if (mDrawPaintView.getIsHasBG() && mDrawPaintView.getBackgroundBitmap() != null) {
            mFilePresenter.saveAsBg(mDrawPaintView.getBackgroundBitmap(),
                    Constants.TEMP_BG_PATH + "/" + Integer.parseInt(getCurPagesTextView().getText().toString()) + ".png",
                    new SaveListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFail(String toastMessage) {
                            Log.e(TAG, toastMessage);
                        }
                    });
        }

        mFilePresenter.saveAsImg(mFileAlertDialog.getSaveBitmap(), Constants.PICTURE_PATH + noteName + ".png", new SaveListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFail(String toastMessage) {
                Log.e(TAG, toastMessage);
            }
        });

        String filePath = Constants.TEMP_XML_PATHS + Integer.parseInt(getCurPagesTextView().getText().toString()) + ".xml";

        mFilePresenter.deleteFile(new File(Constants.NOTE_PATH + noteName));
        mFilePresenter.saveAsXML(mDrawPaintView.getDrawingList(), filePath, new SaveListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail(String toastMessage) {
                Log.e(TAG, toastMessage);
            }
        });

        mFilePresenter.modifyTempFile(noteName, new SaveListener() {
            @Override
            public void onSuccess() {
                showProgress();
                Observable.timer(2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        hideProgress();
                        showToast(getString(R.string.save_success));
                        finish();
                    }
                });
            }

            @Override
            public void onFail(String toastMessage) {
                Log.e(TAG, toastMessage);
            }
        });

    }

    public int getPageSize() {
        return pageSize;
    }

    public void setCanScale(){
        mRevertList = new ArrayList<>(mDrawPaintView.getDrawingList());
        scaleImageView.setImageBitmap(mFileAlertDialog.getSaveBitmap());
        mDrawPaintView.clear();
        scaleImageView.bringToFront();
        scaleImageView.setVisibility(View.VISIBLE);
        morePagesLinearLayout.bringToFront();
    }

    public void setNoScale(){
        mDrawPaintView.setDrawingList(mRevertList);
        scaleImageView.setVisibility(View.GONE);
        mDrawPaintView.setVisibility(View.VISIBLE);
        morePagesLinearLayout.setVisibility(View.VISIBLE);
        scaleImageView.setNormalScale();
    }

    @OnClick(R.id.more_pages_linearLayout)
    public void onPageViewClicked() {
        if (mDrawPaintView.getIfCanDraw()) {
            mPagesPopupWindow.updateData(Integer.parseInt(mCurPagesTextView.getText().toString()));
        }
        mPagesPopupWindow.showAsDropDown(mCurPagesTextView, 0, 50);

    }

}
