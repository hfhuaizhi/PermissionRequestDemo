package com.hfhuaizhi.permissionrequestdemo;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hfhuaizhi.permissionlib.util.RequestPermissionUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String[] list = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION};
        findViewById(R.id.bt_main_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestPermissionUtil.getInstance().requestPermissions(getApplicationContext(), list, new RequestPermissionUtil.OnPermissionListener() {
                    @Override
                    public void onPermissionResult(boolean success) {
                        Toast.makeText(getApplicationContext(), "result::" + success, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}