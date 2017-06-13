package com.cvter.nynote.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.cvter.nynote.Adapter.ThumbnailRecyclerAdapter;
import com.cvter.nynote.Model.NoteInfo;
import com.cvter.nynote.Presenter.IMainPresenter;
import com.cvter.nynote.Presenter.MainPresenterImpl;
import com.cvter.nynote.R;
import com.cvter.nynote.Utils.Constants;
import com.cvter.nynote.View.IMainView;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity implements IMainView {

    private IMainPresenter mPresenter;
    @BindView(R.id.new_note_floatingActionButton)
    FloatingActionButton mNewNoteFloatingActionButton;
    @BindView(R.id.thumbnail_recyclerView)
    RecyclerView mThumbnailRecyclerView;
    @BindView(R.id.refresh_pullToRefreshView)
    com.yalantis.phoenix.PullToRefreshView mRefreshPullToRefreshView;

    private ThumbnailRecyclerAdapter mAdapter;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void initWidget(Bundle bundle) {

        mPresenter = new MainPresenterImpl(this);

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

                mAdapter.clearData();
                mThumbnailRecyclerView.removeAllViews();
                doBusiness(MainActivity.this);
            }
        });

    }

    @Override
    public void doBusiness(Context context) {

        File file = new File(Constants.PICTURE_FILE_PATH);
        mPresenter.getNoteImage(file);

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
        mAdapter = new ThumbnailRecyclerAdapter(MainActivity.this, notes);
        mThumbnailRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        if (mAdapter != null) {
            mAdapter.clearData();
            mThumbnailRecyclerView.removeAllViews();
            doBusiness(MainActivity.this);
        }
        super.onResume();
    }

}
