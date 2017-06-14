package com.cvter.nynote.view;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.cvter.nynote.model.PaintInfo;
import com.cvter.nynote.R;
import com.cvter.nynote.utils.Constants;


/**
 * Created by cvter on 2017/6/8.
 */

public class GraphPopupWindow extends BasePopupWindow implements View.OnClickListener{

    private Activity mContext;
    private PaintInfo mPaint;

    private ImageView mCircleImageView;
    private ImageView mLineImageView;
    private ImageView mSquareImageView;

    private ImageView mDeltaImageView;
    private ImageView mPentagonImageView;
    private ImageView mStarImageView;

    private ImageView mConeImageView;
    private ImageView mSphereImageView;
    private ImageView mCubeImageView;

    public GraphPopupWindow(Activity context, Paint paint, int width, int height) {
        super(context, width, height);
        this.mContext = context;
        this.mPaint = (PaintInfo) paint;
        initLayout();
    }

    private void initLayout() {
        View graphView = LayoutInflater.from(mContext).inflate(R.layout.window_graph_species, null);
        this.setContentView(graphView);

        mCircleImageView = (ImageView) graphView.findViewById(R.id.circle_imageView);
        mLineImageView = (ImageView) graphView.findViewById(R.id.line_imageView);
        mSquareImageView = (ImageView) graphView.findViewById(R.id.square_imageView);
        mConeImageView = (ImageView) graphView.findViewById(R.id.cone_imageView);
        mSphereImageView = (ImageView) graphView.findViewById(R.id.sphere_imageView);
        mCubeImageView = (ImageView) graphView.findViewById(R.id.cube_imageView);
        mDeltaImageView = (ImageView) graphView.findViewById(R.id.delta_imageView);
        mPentagonImageView = (ImageView) graphView.findViewById(R.id.pentagon_imageView);
        mStarImageView = (ImageView) graphView.findViewById(R.id.star_imageView);

    }

    public void setListener() {

        mCircleImageView.setOnClickListener(this);
        mLineImageView.setOnClickListener(this);
        mSquareImageView.setOnClickListener(this);
        mConeImageView.setOnClickListener(this);
        mSphereImageView.setOnClickListener(this);
        mCubeImageView.setOnClickListener(this);
        mDeltaImageView.setOnClickListener(this);
        mPentagonImageView.setOnClickListener(this);
        mStarImageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.circle_imageView:
                mPaint.setGraphType(Constants.CIRCLE);
                break;

            case R.id.line_imageView:
                mPaint.setGraphType(Constants.LINE);
                break;

            case R.id.square_imageView:
                mPaint.setGraphType(Constants.SQUARE);
                break;

            case R.id.cone_imageView:
               mPaint.setGraphType(Constants.CONE);
                break;

            case R.id.sphere_imageView:
                mPaint.setGraphType(Constants.SPHERE);
                break;

            case R.id.cube_imageView:
                mPaint.setGraphType(Constants.CUBE);
                break;

            case R.id.delta_imageView:
                mPaint.setGraphType(Constants.DELTA);
                break;

            case R.id.pentagon_imageView:
                mPaint.setGraphType(Constants.PENTAGON);
                break;

            case R.id.star_imageView:
                mPaint.setGraphType(Constants.STAR);
                break;

            default:
                break;
        }
    }
}
