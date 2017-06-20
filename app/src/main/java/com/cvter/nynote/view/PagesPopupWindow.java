package com.cvter.nynote.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.cvter.nynote.R;
import com.cvter.nynote.activity.DrawActivity;
import com.cvter.nynote.adapter.MorePagesRecyclerAdapter;
import com.cvter.nynote.presenter.RecyclerTouchCallback;
import com.cvter.nynote.utils.Constants;


/**
 * Created by cvter on 2017/6/14.
 */

public class PagesPopupWindow extends BasePopupWindow {

    private DrawActivity mContext;
    private ImageView mMorePageImageView;

    private MorePagesRecyclerAdapter mAdapter;
    private Canvas mCanvas;

    public PagesPopupWindow(Activity context, int width, int height) {
        super(context, width, height);
        this.mContext = (DrawActivity) context;
        initLayout();
    }

    private void initLayout() {
        View pagesView = LayoutInflater.from(mContext).inflate(R.layout.window_page_list, null);
        this.setContentView(pagesView);

        mMorePageImageView = (ImageView) pagesView.findViewById(R.id.more_pages_imageView);
        RecyclerView morePageRecyclerView = (RecyclerView) pagesView.findViewById(R.id.more_pages_recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        morePageRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new MorePagesRecyclerAdapter(mContext);
        morePageRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new RecyclerTouchCallback(mAdapter));
        touchHelper.attachToRecyclerView(morePageRecyclerView);

    }

    public void setListener(){
        Bitmap mAddBitmap = Bitmap.createBitmap(69, 110, Bitmap.Config.RGB_565);
        mAddBitmap.eraseColor(Color.WHITE);
        final Bitmap finalBitmap = Constants.getCompressBitmap(mAddBitmap);
        mMorePageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mContext.getDrawPaintView().getIfCanDraw()){
                    mAdapter.getPages().add(mAdapter.getItemCount(), finalBitmap);
                    mContext.getAllPagesTextView().setText(String.valueOf(mAdapter.getPages().size()));
                    mAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    //更新数据
    public void updateData(int num){
        if (mAdapter.getPages().isEmpty()){
            mAdapter.getPages().add(0, getCurrentBitmap());
            mAdapter.notifyItemChanged(0);
        } else {
            mAdapter.getPages().remove(num -1 );
            mAdapter.getPages().add(num - 1, getCurrentBitmap());
            mAdapter.notifyItemChanged(num - 1);
        }

    }

    //得到当前画布图片
    private Bitmap getCurrentBitmap(){
        Bitmap mCurrentBitmap = Bitmap.createBitmap(Constants.getScreenSize(mContext)[0], Constants.getScreenSize(mContext)[1], Bitmap.Config.RGB_565);
        mCanvas = new Canvas(mCurrentBitmap);
        mCanvas.drawColor(Color.WHITE);
        mCanvas = new Canvas(mCurrentBitmap);
        mCanvas.drawColor(Color.WHITE);

        if ( ! mContext.getDrawPaintView().getIsHasBG()){ //若不存在背景图片

            mCanvas.drawBitmap(mContext.getDrawPaintView().getBitmap(), 0, 0, null);

        } else {
            mCanvas.drawBitmap(mContext.getDrawPaintView().getBackgroundBitmap(), 0, 0, null);
            mCanvas.drawBitmap(mContext.getDrawPaintView().getBitmap(), 0, 0, null);
        }
        return mCurrentBitmap;
    }

    public void setSaveBitmapSize(int size){
        mAdapter.setSaveBitmapSize(size);
    }

    @Override
    public void dismiss() {
        if (mContext.getDrawPaintView().getIfCanDraw()){
            mContext.getAllPagesTextView().setText(Integer.toString(mAdapter.getPages().size()));
        }

        super.dismiss();
    }

}
