package com.cvter.nynote.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cvter.nynote.adapter.ThumbnailRecyclerAdapter;
import com.cvter.nynote.model.NoteInfo;
import com.cvter.nynote.presenter.IMainPresenter;
import com.cvter.nynote.presenter.MainPresenterImpl;
import com.cvter.nynote.R;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.view.IMainView;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity implements IMainView {

    private IMainPresenter mIMainPresenter;
    @BindView(R.id.new_note_floatingActionButton)
    FloatingActionButton mNewNoteFloatingActionButton;
    @BindView(R.id.thumbnail_recyclerView)
    RecyclerView mThumbnailRecyclerView;
    @BindView(R.id.refresh_pullToRefreshView)
    com.yalantis.phoenix.PullToRefreshView mRefreshPullToRefreshView;

    private ThumbnailRecyclerAdapter mThumbnailRecyclerAdapter;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void initWidget(Bundle bundle) {
        mIMainPresenter = new MainPresenterImpl(this);
    }

    @Override
    public void initParams(Bundle params) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void setListener() {

        mRefreshPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshPullToRefreshView.setRefreshing(false);
                    }
                }, 1000);

                mThumbnailRecyclerAdapter.clearData();
                mThumbnailRecyclerView.removeAllViews();
                doBusiness(MainActivity.this);
            }
        });


    }

    @Override
    public void doBusiness(Context context) {

        File file = new File(Constants.PICTURE_FILE_PATH);
        mIMainPresenter.getNoteImage(file);

    }

    @OnClick(R.id.new_note_floatingActionButton)
    public void onViewClicked() {
        Bundle bundle = new Bundle();
        bundle.putString("skipType", "new_edit");
        startActivity(DrawActivity.class, bundle);
    }

    @Override
    public void onLoadImagesCompleted(List<NoteInfo> notes) {
        mThumbnailRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mThumbnailRecyclerAdapter = new ThumbnailRecyclerAdapter(MainActivity.this, notes);
        mThumbnailRecyclerView.setAdapter(mThumbnailRecyclerAdapter);
    }

    @Override
    protected void onResume() {
        if (mThumbnailRecyclerAdapter != null) {
            mThumbnailRecyclerAdapter.clearData();
            mThumbnailRecyclerView.removeAllViews();
            doBusiness(MainActivity.this);
        }
        super.onResume();
    }

    public IMainPresenter getIMainPresenter() {
        return mIMainPresenter;
    }

}