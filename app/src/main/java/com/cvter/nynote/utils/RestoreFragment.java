package com.cvter.nynote.utils;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cvter.nynote.model.PathInfo;

import java.util.List;

/**
 * Created by cvter on 2017/6/24.
 */

public class RestoreFragment extends Fragment {

    private List<PathInfo> mPath;
    private Bitmap mBitmap;
    private List<Bitmap> mPage;
    private int mPageNum = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setData(List<PathInfo> data, Bitmap bitmap, List<Bitmap> page, int pageNum) {
        this.mPath = data;
        this.mBitmap = bitmap;
        this.mPage = page;
        this.mPageNum = pageNum;
    }

    public List<PathInfo> getPath() {
        return mPath;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public List<Bitmap> getPage() {

        return mPage;
    }

    public int getPageNum() {
        return mPageNum;
    }
}
