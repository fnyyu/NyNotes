package com.cvter.nynote.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.cvter.nynote.R;

/**
 * Created by cvter on 2017/6/14.
 */

public class PagesPopupWindow extends BasePopupWindow implements View.OnClickListener{

    private Context mContext;
    private ImageView mMorePageImageView;
    private RecyclerView mMorePageRecyclerView;

    public PagesPopupWindow(Activity context, int width, int height) {
        super(context, width, height);
        this.mContext = context;
    }

    private void initLayout() {
        View pagesView = LayoutInflater.from(mContext).inflate(R.layout.window_page_list, null);
        this.setContentView(pagesView);

        mMorePageImageView = (ImageView) pagesView.findViewById(R.id.more_pages_imageView);
        mMorePageRecyclerView = (RecyclerView) pagesView.findViewById(R.id.more_pages_recyclerView);

    }

    private void setListener(){
        mMorePageImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}
