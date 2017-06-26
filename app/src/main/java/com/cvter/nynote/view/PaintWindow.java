package com.cvter.nynote.view;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.cvter.nynote.activity.DrawActivity;
import com.cvter.nynote.model.PaintInfo;
import com.cvter.nynote.R;
import com.cvter.nynote.utils.Constants;


/**
 * Created by cvter on 2017/6/8.
 * 画笔类型PopupWindow
 */

public class PaintWindow extends BasePopupWindow implements View.OnClickListener {

    private DrawActivity mContext;
    private PaintInfo mPaint;

    private ImageView mPencilImageView;
    private ImageView mFountainImageView;
    private ImageView mDropperImageView;
    private ImageView mBrushImageView;
    private ImageView mBrushWideImageView;
    private ImageView mBlackImageView;
    private ImageView mBlueImageView;
    private ImageView mGreenImageView;
    private ImageView mYellowImageView;
    private ImageView mRedImageView;
    private SeekBar mWidthSeekBar;

    private static final String TAG = "PaintWindow";

    public PaintWindow(DrawActivity context , Paint paint, int width, int height) {
        super(width, height);
        this.mPaint = (PaintInfo) paint;
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {

        View viewPaint = LayoutInflater.from(mContext).inflate(R.layout.dialog_pen_species, null);
        setContentView(viewPaint);

        mPencilImageView = (ImageView) viewPaint.findViewById(R.id.pencil_imageView);
        mFountainImageView = (ImageView) viewPaint.findViewById(R.id.fountain_imageView);
        mDropperImageView = (ImageView) viewPaint.findViewById(R.id.dropper_imageView);
        mBrushImageView = (ImageView) viewPaint.findViewById(R.id.brush_imageView);
        mBrushWideImageView = (ImageView) viewPaint.findViewById(R.id.brush_wide_imageView);

        mWidthSeekBar = (SeekBar) viewPaint.findViewById(R.id.paint_width_seekBar);

        mBlackImageView = (ImageView) viewPaint.findViewById(R.id.black_imageView);
        mGreenImageView = (ImageView) viewPaint.findViewById(R.id.green_imageView);
        mBlueImageView = (ImageView) viewPaint.findViewById(R.id.blue_imageView);
        mRedImageView = (ImageView) viewPaint.findViewById(R.id.red_imageView);
        mYellowImageView = (ImageView) viewPaint.findViewById(R.id.yellow_imageView);

    }

    public void setListener() {
        mPencilImageView.setOnClickListener(this);
        mFountainImageView.setOnClickListener(this);
        mDropperImageView.setOnClickListener(this);
        mBrushImageView.setOnClickListener(this);
        mBrushWideImageView.setOnClickListener(this);

        mBlackImageView.setOnClickListener(this);
        mGreenImageView.setOnClickListener(this);
        mBlueImageView.setOnClickListener(this);
        mYellowImageView.setOnClickListener(this);
        mRedImageView.setOnClickListener(this);

        mWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mContext.getEraserImageView().setSelected(false);
                mContext.getDrawPaintView().getPaint().setMode(Constants.DRAW);
                mContext.getDrawPaintView().getPaint().setPenRawSize(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, mContext.getString(R.string.no_operation));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, mContext.getString(R.string.no_operation));
            }

        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.pencil_imageView:
                mPaint.setOrdinaryPen();
                mPaint.setGraphType(Constants.ORDINARY);
                break;

            case R.id.fountain_imageView:
                mPaint.setDashPen();
                mPaint.setGraphType(Constants.ORDINARY);
                break;

            case R.id.dropper_imageView:
                mPaint.setTransPen();
                mPaint.setGraphType(Constants.ORDINARY);
                break;

            case R.id.brush_imageView:
                mPaint.setDiscretePen();
                mPaint.setGraphType(Constants.ORDINARY);
                break;

            case R.id.brush_wide_imageView:
                mPaint.setInkPen();
                mPaint.setGraphType(Constants.ORDINARY);
                break;

            case R.id.black_imageView:
                mPaint.setPenColor(Color.BLACK);
                break;

            case R.id.green_imageView:
                mPaint.setPenColor(Color.GREEN);
                break;

            case R.id.blue_imageView:
                mPaint.setPenColor(Color.BLUE);
                break;

            case R.id.red_imageView:
                mPaint.setPenColor(Color.RED);
                break;

            case R.id.yellow_imageView:
                mPaint.setPenColor(Color.YELLOW);
                break;

            default:
                break;
        }
        dismiss();
    }
}
