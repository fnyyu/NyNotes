package com.cvter.nynote.View;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.cvter.nynote.Activity.DrawActivity;
import com.cvter.nynote.R;
import com.cvter.nynote.Utils.CommonUtils;


/**
 * Created by cvter on 2017/6/8.
 */

public class GraphPopupWindow extends BasePopupWindow implements View.OnClickListener{

    private DrawActivity mContext;

    private ImageView circleImageView, lineImageView, squareImageView,
            coneImageView, sphereImageView, cubeImageView;

    public GraphPopupWindow(DrawActivity context, int width, int height) {
        super(context, width, height);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        View graphView = LayoutInflater.from(mContext).inflate(R.layout.window_graph_species, null);
        this.setContentView(graphView);

        circleImageView = (ImageView) graphView.findViewById(R.id.circle_imageView);
        lineImageView = (ImageView) graphView.findViewById(R.id.line_imageView);
        squareImageView = (ImageView) graphView.findViewById(R.id.square_imageView);
        coneImageView = (ImageView) graphView.findViewById(R.id.cone_imageView);
        sphereImageView = (ImageView) graphView.findViewById(R.id.sphere_imageView);
        cubeImageView = (ImageView) graphView.findViewById(R.id.cube_imageView);

    }

    public void setListener() {

        circleImageView.setOnClickListener(this);
        lineImageView.setOnClickListener(this);
        squareImageView.setOnClickListener(this);
        coneImageView.setOnClickListener(this);
        sphereImageView.setOnClickListener(this);
        cubeImageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.circle_imageView:
                mContext.drawPaintView.mPaint.setGraphType(CommonUtils.CIRCLE);
                break;

            case R.id.line_imageView:
                mContext.drawPaintView.mPaint.setGraphType(CommonUtils.LINE);
                break;

            case R.id.square_imageView:
                mContext.drawPaintView.mPaint.setGraphType(CommonUtils.SQUARE);
                break;

            case R.id.cone_imageView:
                mContext.drawPaintView.mPaint.setGraphType(CommonUtils.CONE);
                break;

            case R.id.sphere_imageView:
                mContext.drawPaintView.mPaint.setGraphType(CommonUtils.SPHERE);
                break;

            case R.id.cube_imageView:
                mContext.drawPaintView.mPaint.setGraphType(CommonUtils.CUBE);
                break;
        }
    }
}
