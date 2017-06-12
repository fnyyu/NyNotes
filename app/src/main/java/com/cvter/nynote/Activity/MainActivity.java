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

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity implements IMainView {

    IMainPresenter mPresenter;
    @BindView(R.id.new_note_floatingActionButton)
    FloatingActionButton newNoteFloatingActionButton;
    @BindView(R.id.thumbnail_recyclerView)
    RecyclerView thumbnailRecyclerView;

    private ThumbnailRecyclerAdapter mAdapter;

    public static void launch(Context context){
        context.startActivity(new Intent(context,MainActivity.class));
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

    }

    @Override
    public void doBusiness(Context context) {

        File file = new File(Constants.PICTURE_PATH);
        mPresenter.getNoteImage(file);

    }

    @OnClick(R.id.new_note_floatingActionButton)
    public void onViewClicked() {
        Bundle bundle = new Bundle();
        bundle.putString("skipType", "new_edit");
        startActivity(DrawActivity.class, bundle);
    }

    @Override
    public void updateListCompleted() {

    }

    @Override
    public void onLoadImagesCompleted(ArrayList<NoteInfo> notes) {
        thumbnailRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mAdapter = new ThumbnailRecyclerAdapter(MainActivity.this, notes);
        thumbnailRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
            thumbnailRecyclerView.setAdapter(mAdapter);
        }
        super.onResume();
    }
}
