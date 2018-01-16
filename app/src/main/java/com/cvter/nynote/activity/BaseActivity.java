package com.cvter.nynote.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.cvter.nynote.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by serenefang on 2017/6/2.
 * Activity基类
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mUnBinder;

    //需要进行检测的权限数组
    protected String[] mNeedPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private static final int PERMISSION_REQUEST_CODE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        initParams(bundle);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //全屏

        setContentView(LayoutInflater.from(this).inflate(bindLayout(), null));
        mUnBinder = ButterKnife.bind(this);

        checkPermissions(mNeedPermissions);

        initWidget(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //不可横屏

        setListener();
        doBusiness(this);

    }


    //初始化控件
    protected abstract void initWidget(Bundle bundle);

    //初始化参数
    public abstract void initParams(Bundle params);


    // 绑定布局
    public abstract int bindLayout();


    //设置监听
    public abstract void setListener();

    // 业务操作
    public abstract void doBusiness(Context context);


    //页面跳转
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(BaseActivity.this, clz));
    }

    // 携带数据的页面跳转
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    //含有Bundle通过Class打开编辑界面
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    // 简化Toast
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    //检查应用权限设置
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissionList = findDeniedPermissions(permissions);
        if (!needRequestPermissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissionList.toArray(
                            new String[needRequestPermissionList.size()]),
                    PERMISSION_REQUEST_CODE);
        }
    }

    //获取申请权限列表
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                needRequestPermissionList.add(perm);
            }
        }
        return needRequestPermissionList;
    }


    //检查所有权限是否授权
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //显示提示信息
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notifyTitle);
        builder.setMessage(R.string.notifyMsg);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton(R.string.allow,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    //启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && !verifyPermissions(grantResults)) {
            showMissingPermissionDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }

}
