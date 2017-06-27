package com.cvter.nynote.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.FragmentManager;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.cvter.nynote.utils.CommonMethod;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.ImportListener;
import com.cvter.nynote.utils.RestoreFragment;
import com.cvter.nynote.utils.SaveListener;
import com.cvter.nynote.view.FileAlertDialog;
import com.cvter.nynote.view.GraphWindow;
import com.cvter.nynote.view.IPictureView;
import com.cvter.nynote.view.MatrixView;
import com.cvter.nynote.view.PagesPopupWindow;
import com.cvter.nynote.view.PaintView;
import com.cvter.nynote.view.PaintWindow;
import com.cvter.nynote.view.PictureAlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by cvter on 2017/6/2.
 * 绘制页面Activity
 */

public class DrawActivity extends BaseActivity implements IPictureView, PathWFCallback {

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
    @BindView(R.id.picture_imageView)
    ImageView pictureImageView;
    @BindView(R.id.graph_imageView)
    ImageView graphImageView;
    @BindView(R.id.save_imageView)
    ImageView saveImageView;

    @BindView(R.id.draw_matrixView)
    MatrixView drawMatrixView;
    @BindView(R.id.more_pages_linearLayout)
    LinearLayout morePagesLinearLayout;
    @BindView(R.id.draw_paintView)
    PaintView mDrawPaintView;
    @BindView(R.id.cur_pages_textView)
    TextView mCurPagesTextView;
    @BindView(R.id.all_pages_textView)
    TextView mAllPagesTextView;

    private FileAlertDialog mFileAlertDialog;
    private PaintWindow mPaintWindow;
    private GraphWindow mGraphWindow;
    private PictureAlertDialog mPictureDialog;
    private PagesPopupWindow mPagesWindow;

    private String skipType = "";
    private boolean ifCanDraw;
    private boolean ifCanScale = true;
    private static final String TAG = "DrawActivity";
    private int pageSize = 0;
    private String noteName = "";
    private StringBuilder path = new StringBuilder();

    private RestoreFragment mRestoreFragment;

    DialogInterface.OnClickListener keyBackListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "保存"按钮弹出PopupWindow
                    if (skipType.equals(Constants.NEW_EDIT)) {
                        mFileAlertDialog.show();
                    } else {
                        editFileSave();
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
        mPaintWindow = new PaintWindow(this, mDrawPaintView.getPaint(), 600, 420);
        mGraphWindow = new GraphWindow(this, mDrawPaintView.getPaint(), 450, 400);
        mPagesWindow = new PagesPopupWindow(this, 0, 0);

        mPicturePresenter = new PicturePresenterImpl(this);
        mFilePresenter = new FilePresenterImpl(this);

        mDrawPaintView.setCallback(this);
        mWithdrawImageView.setEnabled(false);
        mForwardImageView.setEnabled(false);

        skipType = getIntent().getExtras().getString("skipType");
        noteName = getIntent().getStringExtra("noteName") == null ? "" : getIntent().getStringExtra("noteName").replace(getString(R.string.png), "");

        FragmentManager fm = getFragmentManager();
        mRestoreFragment = (RestoreFragment) fm.findFragmentByTag("Restore");
        if (mRestoreFragment == null) {
            mRestoreFragment = new RestoreFragment();
            fm.beginTransaction().add(mRestoreFragment, "Restore").commit();
        }
        restoreData(mRestoreFragment);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mRestoreFragment.setData(mDrawPaintView.getDrawingList(), mDrawPaintView.getBackgroundBitmap(),
                mPagesWindow.getAdapter().getPages(), mPagesWindow.getAdapter().getPages().size());
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
        mPaintWindow.setListener();
        mGraphWindow.setListener();
        mPagesWindow.setListener();
    }

    @Override
    public void doBusiness(Context context) {

        if (!TextUtils.isEmpty(skipType)) {
            switch (skipType) {
                case Constants.NEW_EDIT:
                    mDrawPaintView.setCanDraw(true);
                    mReadingTitleLayout.setVisibility(View.GONE);
                    mDrawingTitleLayout.setVisibility(View.VISIBLE);
                    ifCanDraw = true;
                    break;

                case Constants.READ_NOTE:

                    mDrawPaintView.setCanDraw(false);
                    ifCanDraw = false;
                    mAllPagesTextView.bringToFront();
                    mAllPagesTextView.setClickable(true);
                    path.setLength(0);
                    path.append(Constants.NOTE_PATH).append(noteName).append("/xml");
                    pageSize = mFilePresenter.getFileSize(path.toString());
                    mAllPagesTextView.setText(String.valueOf(pageSize));
                    mPagesWindow.setSaveBitmapSize(pageSize);
                    path.setLength(0);
                    path.append(Constants.NOTE_PATH).append(noteName).append("/xml/1.xml");
                    mFilePresenter.importXML(path.toString(), new ImportListener() {
                        @Override
                        public void onSuccess(List<PathInfo> info) {
                            mDrawPaintView.setDrawingList(info);
                        }

                        @Override
                        public void onFail(String toastMessage) {
                            Log.e(TAG, toastMessage);
                        }

                    });
                    path.setLength(0);
                    path.append(Constants.NOTE_PATH).append(noteName).append("/bg/1.png");
                    mPicturePresenter.getSmallBitmap(path.toString(), 3, "");
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
            R.id.forward_imageView, R.id.clear_imageView, R.id.save_imageView,
            R.id.choose_imageView})

    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.draw_imageView:
                setDrawStyle();
                break;

            case R.id.gesture_imageView:

                break;

            case R.id.share_imageView:
                break;

            case R.id.export_imageView:

                break;

            case R.id.pen_imageView:
                setPenStyle(view);
                mDrawPaintView.setIsCrossDraw(false);
                break;

            case R.id.eraser_imageView:
                setEraserStyle(view);
                mDrawPaintView.setIsCrossDraw(false);
                break;

            case R.id.withdraw_imageView:
                mDrawPaintView.withdraw();
                mDrawPaintView.setIsCrossDraw(false);
                break;

            case R.id.picture_imageView:
                mPictureDialog = new PictureAlertDialog(this);
                mDrawPaintView.setIsCrossDraw(false);
                break;

            case R.id.graph_imageView:
                setGraphStyle();
                mDrawPaintView.setIsCrossDraw(false);
                break;

            case R.id.forward_imageView:
                mDrawPaintView.forward();
                mDrawPaintView.setIsCrossDraw(false);
                break;

            case R.id.clear_imageView:
                mDrawPaintView.setIsCrossDraw(false);
                mDrawPaintView.clear();
                break;

            case R.id.choose_imageView:
                mDrawPaintView.setIsCrossDraw(true);
                break;

            case R.id.save_imageView:
                if (getIntent().getExtras().getString("skipType").equals("new_edit")) {
                    mFileAlertDialog.show();
                } else {
                    editFileSave();
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
                    String bitmapPath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                    cursor.close();
                    mPicturePresenter.getSmallBitmap(bitmapPath, Constants.GALLEY_PICK, String.valueOf(getCurPagesTextView().getText().toString()));
                }
                break;

            case Constants.TAKE_PHOTO:
                if (resultCode == RESULT_OK && !TextUtils.isEmpty(mPictureDialog.getPhotoPath())) {
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
        Bitmap backGroundBitmap = CommonMethod.getCompressBitmap(bitmap);
        mDrawPaintView.setBackgroundBitmap(backGroundBitmap);
        mDrawPaintView.setIsHasBG(true);
    }

    @Override
    public void showProgress() {
        if (mSaveProgressBar != null) {
            mDrawPaintView.setCanDraw(false);
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

    private void editFileSave() {

        if (mDrawPaintView.getIsHasBG() && mDrawPaintView.getBackgroundBitmap() != null) {

            path.setLength(0);
            path.append(Constants.TEMP_BG_PATHS);
            path.append(Integer.parseInt(getCurPagesTextView().getText().toString()));
            path.append(getString(R.string.png));
            mFilePresenter.saveAsBg(mDrawPaintView.getBackgroundBitmap(),
                    path.toString(), new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Log.e(TAG, getString(R.string.bg_success));
                        }

                        @Override
                        public void onFail(String toastMessage) {
                            Log.e(TAG, toastMessage);
                        }
                    });
        }

        path.setLength(0);
        path.append(Constants.PICTURE_PATH).append(noteName).append(getString(R.string.png));
        mFilePresenter.saveAsImg(mFileAlertDialog.getSaveBitmap(), path.toString(), new SaveListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, getString(R.string.pic_success));
            }

            @Override
            public void onFail(String toastMessage) {
                Log.e(TAG, toastMessage);
            }
        });

        path.setLength(0);
        path.append(Constants.TEMP_XML_PATHS).append(Integer.parseInt(getCurPagesTextView().getText().toString())).append(getString(R.string.xml));
        mFilePresenter.deleteFile(new File(Constants.NOTE_PATH + noteName));
        mFilePresenter.saveAsXML(mDrawPaintView.getDrawingList(), path.toString(), new SaveListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, getString(R.string.xml_success));
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

    private void setViewEnable(boolean ifEnable) {
        morePagesLinearLayout.setEnabled(ifEnable);
        saveImageView.setEnabled(ifEnable);
        pictureImageView.setEnabled(ifEnable);
        mEraserImageView.setEnabled(ifEnable);
    }

    private void restoreData(RestoreFragment fragment) {


        if (fragment.getPath() != null) {
            mDrawPaintView.setDrawingList(fragment.getPath());
        }


        if (fragment.getPage() != null) {
            mPagesWindow.getAdapter().setPages(fragment.getPage());
        }

        if (fragment.getBitmap() != null) {
            mDrawPaintView.setBackgroundBitmap(fragment.getBitmap());
        }

        if (fragment.getPageNum() != 0) {
            mAllPagesTextView.setText(String.valueOf(fragment.getPageNum()));
        }
    }

    private void setDrawStyle() {
        mReadingTitleLayout.setVisibility(View.GONE);
        mDrawingTitleLayout.setVisibility(View.VISIBLE);
        mDrawPaintView.setCanDraw(true);
        ifCanDraw = true;
        mFilePresenter.createTempFile();
    }

    private void setPenStyle(View view) {
        if (!ifCanScale) {
            List<PathInfo> mRevertList = new LinkedList<>(drawMatrixView.getMatrixList());

            if (!mRevertList.isEmpty()) {
                mDrawPaintView.setDrawingList(mRevertList);
                drawMatrixView.recycle();
            }
            drawMatrixView.setVisibility(View.INVISIBLE);
            ifCanScale = true;
        }

        setViewEnable(true);
        view.setSelected(true);
        mEraserImageView.setSelected(false);
        mDrawPaintView.getPaint().setMode(Constants.DRAW);
        mDrawPaintView.getPaint().setPenRawSize(mDrawPaintView.getPaint().getPenRawSize());
        mPaintWindow.showAsDropDown(mDrawingTitleLayout, 10, 5);
    }

    private void setEraserStyle(View view) {
        view.setSelected(true);
        mPenImageView.setSelected(false);
        mDrawPaintView.getPaint().setMode(Constants.ERASER);
        mDrawPaintView.getPaint().setGraphType(Constants.ORDINARY);
        mDrawPaintView.getPaint().setOrdinaryPen();
    }

    private void setGraphStyle() {
        if (!ifCanScale) {
            List<PathInfo> mRevertList = new LinkedList<>(drawMatrixView.getMatrixList());

            if (!mRevertList.isEmpty()) {
                mDrawPaintView.setDrawingList(mRevertList);
                drawMatrixView.recycle();
            }
            drawMatrixView.setVisibility(View.INVISIBLE);
            ifCanScale = true;
        }

        mDrawPaintView.getPaint().setMode(Constants.DRAW);
        mGraphWindow.showAsDropDown(mDrawingTitleLayout, 300, 5);
    }

    public void setChooseStyle() {
        if (ifCanScale && mDrawPaintView.getCrossList() != null && !mDrawPaintView.getDrawingList().isEmpty()) {
            drawMatrixView.setOnDraw(new ArrayList<>(mDrawPaintView.getCrossList()));
            mDrawPaintView.clearCross();
            drawMatrixView.invalidate();
            drawMatrixView.setVisibility(View.VISIBLE);
            drawMatrixView.bringToFront();
            morePagesLinearLayout.bringToFront();
            setViewEnable(false);
            ifCanScale = false;
        }
    }

    public PagesPopupWindow getPagesDialog() {
        return mPagesWindow;
    }

    @OnClick(R.id.more_pages_linearLayout)
    public void onPageViewClicked() {
        if (mDrawPaintView.getCanDraw()) {
            mPagesWindow.updateData(Integer.parseInt(mCurPagesTextView.getText().toString()));
        }

        mPagesWindow.showAtLocation(mDrawActivityLayout, Gravity.BOTTOM, 0, 150);

    }

}
