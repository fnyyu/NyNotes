package com.cvter.nynote.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cvter.nynote.Presenter.PathWFCallback;
import com.cvter.nynote.Presenter.PicturePresenter;
import com.cvter.nynote.Presenter.PicturePresenterImpl;
import com.cvter.nynote.R;
import com.cvter.nynote.Utils.CommonUtils;
import com.cvter.nynote.View.BasePopupWindow;
import com.cvter.nynote.View.IPictureView;
import com.cvter.nynote.View.PaintView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DrawActivity extends BaseActivity implements IPictureView, PathWFCallback {

    PicturePresenter picturePresenter;

    private final static int TAKE_PHOTO = 1;
    private final static int GALLEY_PICK = 2;
    private final static int CROP_PHOTO = 3;

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
    ImageView penImageView;
    @BindView(R.id.eraser_imageView)
    ImageView eraserImageView;
    @BindView(R.id.withdraw_imageView)
    ImageView withdrawImageView;
    @BindView(R.id.forward_imageView)
    ImageView forwardImageView;
    @BindView(R.id.draw_paintView)
    PaintView drawPaintView;
    @BindView(R.id.front_imageView)
    ImageView frontImageView;

    Button fileNameButton;
    EditText fileNameEditText;
    ImageView pencilImageView, fountainImageView, dropperImageView,
            brushImageView, brushWideImageView;
    ImageView blackImageView, blueImageView, greenImageView,
            yellowImageView, redImageView;
    SeekBar widthSeekBar;
    TextView progressTextView;
    private static int paintWidth = 20;

    BasePopupWindow mFilePopupWindow;
    BasePopupWindow mPaintPopupWindow;

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

        mFilePopupWindow = new BasePopupWindow(this, 800, 0);
        View fileView = LayoutInflater.from(DrawActivity.this).inflate(R.layout.window_file_name, null);
        mFilePopupWindow.setContentView(fileView);
        fileNameButton = (Button) fileView.findViewById(R.id.fileName_button);
        fileNameEditText = (EditText) fileView.findViewById(R.id.fileName_editText);

        mPaintPopupWindow = new BasePopupWindow(this,700, 500);
        View viewPaint = LayoutInflater.from(DrawActivity.this).inflate(R.layout.window_pen_list, null);
        mPaintPopupWindow.setContentView(viewPaint);

        pencilImageView = (ImageView) viewPaint.findViewById(R.id.pencil_imageView);
        fountainImageView = (ImageView) viewPaint.findViewById(R.id.fountain_imageView);
        dropperImageView = (ImageView) viewPaint.findViewById(R.id.dropper_imageView);
        brushImageView = (ImageView) viewPaint.findViewById(R.id.brush_imageView);
        brushWideImageView = (ImageView) viewPaint.findViewById(R.id.brush_wide_imageView);

        widthSeekBar = (SeekBar) viewPaint.findViewById(R.id.paint_width_seekBar);
        progressTextView = (TextView) viewPaint.findViewById(R.id.progress_textView);

        blackImageView = (ImageView) viewPaint.findViewById(R.id.black_imageView);
        greenImageView = (ImageView) viewPaint.findViewById(R.id.green_imageView);
        blueImageView = (ImageView) viewPaint.findViewById(R.id.blue_imageView);
        redImageView = (ImageView) viewPaint.findViewById(R.id.red_imageView);
        yellowImageView = (ImageView) viewPaint.findViewById(R.id.yellow_imageView);

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
        fileNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilePopupWindow.dismiss();
                finish();
            }
        });

        pencilImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                paintWidth = i;
                progressTextView.setText(i + "");
                eraserImageView.setSelected(false);
                drawPaintView.setMode(CommonUtils.Mode.DRAW);
                drawPaintView.setPenRawSize(paintWidth);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        blackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawPaintView.setPenColor(getResources().getColor(R.color.black));
            }
        });

        greenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawPaintView.setPenColor(getResources().getColor(R.color.green));
            }
        });

        blueImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawPaintView.setPenColor(getResources().getColor(R.color.blue));
            }
        });

        yellowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawPaintView.setPenColor(getResources().getColor(R.color.yellow));
            }
        });

        redImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawPaintView.setPenColor(getResources().getColor(R.color.red));
            }
        });

    }

    @Override
    public void doBusiness(Context context) {
        skipType = getIntent().getExtras().getString("skipType");
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
                mPaintPopupWindow.showAsDropDown(drawingTitleLayout);
                view.setSelected(true);
                eraserImageView.setSelected(false);
                drawPaintView.setMode(CommonUtils.Mode.DRAW);
                drawPaintView.setPenRawSize(paintWidth);
                break;

            case R.id.eraser_imageView:
                view.setSelected(true);
                penImageView.setSelected(false);
                drawPaintView.setMode(CommonUtils.Mode.ERASER);
                break;

            case R.id.withdraw_imageView:
                drawPaintView.withdraw();
                break;

            case R.id.picture_imageView:
                showTypeDialog();
                break;

            case R.id.graph_imageView:

                break;

            case R.id.forward_imageView:
                drawPaintView.forward();
                break;

            case R.id.clear_imageView:
                drawPaintView.clear();
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
                startActivityForResult(intent1, GALLEY_PICK);
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = picturePresenter.createImgFile();
                Uri uri = Uri.fromFile(file);
                try {
                    photoPath = file.getAbsolutePath();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent2, TAKE_PHOTO);// 采用ForResult打开
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GALLEY_PICK:
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

            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    picturePresenter.getSmallBitmap(photoPath);

                }
                break;

            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        Bitmap bitmap = extras.getParcelable("data");
                        if (bitmap != null) {
                            //setPicToView(bitmap);
                            picturePresenter.getSmallBitmap(photoPath);
                        }
                    }
                }
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
    public void pathWFState() {
        withdrawImageView.setEnabled(drawPaintView.canWithdraw());
        forwardImageView.setEnabled(drawPaintView.canForward());
    }

}
