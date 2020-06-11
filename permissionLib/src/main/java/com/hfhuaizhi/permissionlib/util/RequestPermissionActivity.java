package com.hfhuaizhi.permissionlib.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class RequestPermissionActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static RequestPermissionUtil.OnPermissionListener transferOnPermissionListener;
    private RequestPermissionUtil.OnPermissionListener mOnPermissionListener;
    private boolean mResult = false;
    public static final String EXTRA_PERMISSIONS = "permissions";
    private String[] mPermissionList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        getIntentData();

        ActivityCompat.requestPermissions(RequestPermissionActivity.this, mPermissionList, REQUEST_CODE);

    }

    private void getIntentData() {
        mOnPermissionListener = transferOnPermissionListener;
        transferOnPermissionListener = null;
        mPermissionList = getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults != null && grantResults.length > 0) {
            mResult = true;
            for (int tmp : grantResults) {
                if (tmp != PackageManager.PERMISSION_GRANTED) {
                    mResult = false;
                    break;
                }
            }
        }
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOnPermissionListener != null) {
            mOnPermissionListener.onPermissionResult(mResult);
        }
    }

    public static void start(Context context, String[] permissionList, RequestPermissionUtil.OnPermissionListener listener) {
        Intent intent = new Intent(context, RequestPermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RequestPermissionActivity.EXTRA_PERMISSIONS, permissionList);
        context.startActivity(intent);
        transferOnPermissionListener = listener;
    }
}