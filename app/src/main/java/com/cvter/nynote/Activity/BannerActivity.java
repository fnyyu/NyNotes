package com.cvter.nynote.Activity;

import android.content.Context;
import android.os.Bundle;

import com.cvter.nynote.R;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by cvter on 2017/6/7.
 */

public class BannerActivity extends BaseActivity {
    @Override
    protected void initWidget(Bundle bundle) {

    }

    @Override
    public void initParams(Bundle params) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_banner;
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context context) {

        Observable.timer(4, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                MainActivity.launch(BannerActivity.this);
                finish();
            }
        });

    }
}
